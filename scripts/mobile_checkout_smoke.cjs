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

  if (process.env.MOCK_API === '1') {
    const fixture = [
      {id:'WEB001',name:'Alfredo Pasta',price:100,category:'Mains',available:true},
      {id:'WEB002',name:'Chicken & Waffles',price:95,category:'Mains',available:true},
      {id:'WEB007',name:'French Toast Stuffing',price:100,category:'Desserts',available:true}
    ];
    await page.route('**/api/menu', route => route.fulfill({
      status:200,contentType:'application/json',body:JSON.stringify(fixture)
    }));
    await page.route('**/api/checkout', async route => {
      const body=route.request().postDataJSON();
      const count=body.items.reduce((sum,item)=>sum+item.quantity,0);
      const total=body.items.reduce((sum,item)=>sum+item.quantity*(fixture.find(x=>x.id===item.menuItemId)?.price||0),0);
      await route.fulfill({
        status:201, contentType:'application/json',
        body:JSON.stringify({orderId:'ORDMOBILEMOCK'+run,status:'PENDING',total,itemCount:count})
      });
    });
  }

  try {
    console.log('MOBILE_TEST_START', base, 'run', run);
    await page.goto(base, {waitUntil:'domcontentloaded', timeout:180000});
    await page.locator('#loader').waitFor({state:'hidden',timeout:20000});
    await page.locator('.hero__slide.is-active h1').waitFor({state:'visible',timeout:20000});
    console.log('MOBILE_HERO_VISIBLE');
    // Verify desserts use exactly the same circular + button and basket logic.
    const dessert=page.locator('.dessert-card[data-menu-id]').first();
    await dessert.waitFor({state:'visible',timeout:20000});
    const dessertButton=dessert.locator('.dish-card__add');
    if(await dessertButton.textContent() !== '') throw new Error('Dessert control still contains visible Add text');
    await dessertButton.click();
    await page.locator('#cart-toggle-btn').click();
    if((await page.locator('#cart-badge').textContent()).trim() !== '1')
      throw new Error('Dessert plus button did not add an item');
    await page.locator('.cart-qty-adjust-btn.minus').first().click();
    if((await page.locator('#cart-badge').textContent()).trim() !== '0')
      throw new Error('Dessert could not be removed from basket');
    await page.locator('#cart-drawer-close-btn').click();
    console.log('DESSERT_PLUS_PASS');
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
