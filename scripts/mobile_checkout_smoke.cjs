// Browser-level mobile checkout smoke test. One clearly labelled dummy order; no payment.
const { chromium } = require('playwright');
(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({
    viewport: { width: 390, height: 844 },
    deviceScaleFactor: 3,
    isMobile: true,
    hasTouch: true,
    userAgent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.0 Mobile/15E148 Safari/604.1'
  });
  const base = process.env.TEST_BASE_URL || 'https://platable-eats.vercel.app';
  const run = process.env.GITHUB_RUN_ID || 'manual';
  const errors = [];
  page.on('pageerror', error => errors.push(String(error)));
  const created = [];
  page.on('response', async response => {
    if (!response.url().endsWith('/api/checkout') || response.request().method() !== 'POST') return;
    if (response.status() === 201) {
      try {
        const body = await response.json();
        created.push(body.orderId);
      } catch {}
    }
  });
  try {
    console.log('MOBILE_TEST_START', base, 'run', run);
    await page.goto(base, {waitUntil:'domcontentloaded', timeout:180000});
    await page.locator('#loader').waitFor({state:'hidden',timeout:20000});
    await page.locator('.hero__slide.is-active h1').waitFor({state:'visible',timeout:20000});
    console.log('MOBILE_HERO_VISIBLE');
    const cards=page.locator('.dish-card[data-menu-id]');
    await cards.first().waitFor({state:'visible',timeout:180000});
    const first=cards.first();
    const name=(await first.locator('h3').textContent()).trim();
    await first.locator('.dish-card__add').click();
    await page.locator('#cart-toggle-btn').click();
    await page.locator('#cart-badge').waitFor({state:'visible'});
    let badge=(await page.locator('#cart-badge').textContent()).trim();
    if(badge !== '1') throw new Error('Expected basket count 1, got '+badge);
    await page.locator('.cart-qty-adjust-btn.plus').first().click();
    badge=(await page.locator('#cart-badge').textContent()).trim();
    if(badge !== '2') throw new Error('Expected basket count 2, got '+badge);
    await page.locator('.cart-qty-adjust-btn.minus').first().click();
    badge=(await page.locator('#cart-badge').textContent()).trim();
    if(badge !== '1') throw new Error('Expected basket count 1 after minus, got '+badge);
    await page.locator('#cart-drawer-checkout-action-btn').click();
    await page.locator('#name').fill('Mobile Browser Test');
    await page.locator('#contact').fill('0000000000');
    await page.locator('#address').fill('TEST ONLY DO NOT DELIVER '+run);
    await page.locator('#special').fill('Automated mobile test. No payment or delivery.');
    if(await page.locator('#submit-btn').isDisabled()) throw new Error('Checkout is disabled with one basket item');
    console.log('MOBILE_BASKET_AND_FORM_VALIDATED',name);
    const responsePromise=page.waitForResponse(response=>response.url().endsWith('/api/checkout') && response.request().method()==='POST', {timeout:180000});
    await page.locator('#submit-btn').click();
    const response=await responsePromise;
    if(response.status()!==201) throw new Error('Checkout returned HTTP '+response.status()+': '+(await response.text()).slice(0,220));
    await page.getByText('Order received!', {exact:true}).waitFor({state:'visible',timeout:20000});
    const receipt=await response.json();
    if(!(receipt.total>0 && receipt.itemCount===1 && receipt.orderId)) throw new Error('Bad checkout receipt '+JSON.stringify(receipt));
    const viewText=await page.locator('.order-confirmation').innerText();
    if(!viewText.includes(receipt.orderId)) throw new Error('Order reference missing from confirmation');
    if(errors.length) throw new Error('Browser errors: '+errors.slice(0,3).join(' | '));
    console.log('MOBILE_CHECKOUT_PASS '+JSON.stringify({orderId:receipt.orderId,total:receipt.total,itemCount:receipt.itemCount,menuItem:name,run}));
  } catch(error) {
    console.error('MOBILE_CHECKOUT_FAILURE',error.message);
    console.error('CREATED_ORDERS '+JSON.stringify(created));
    process.exitCode=1;
  } finally {await browser.close();}
})();
