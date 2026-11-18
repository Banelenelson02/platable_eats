/**
 * Plateable Eats — Main Script with Dynamic Basket Integration
 * Architecture: Module pattern with explicit init
 * No jQuery, no unnecessary deps. Vanilla ES6+.
 */

'use strict';

/* =====================================================
   1. LOADER
===================================================== */
const Loader = {
  el: document.getElementById('loader'),

  hide() {
    if (!this.el) return;

    const performHide = () => {
      setTimeout(() => {
        this.el.classList.add('hidden');
        this.el.addEventListener('transitionend', () => {
          this.el.remove();
        }, { once: true });
      }, 800);
    };

    if (document.readyState === 'complete') {
      performHide();
    } else {
      window.addEventListener('load', performHide);
    }
  }
};

/* =====================================================
   2. HEADER — scroll shadow + active link tracking
===================================================== */
const Header = {
  el: document.getElementById('header'),
  navLinks: document.querySelectorAll('.header__nav-link:not(.header__nav-link--cta)'),
  sections: document.querySelectorAll('section[id]'),

  init() {
    if (!this.el) return;
    window.addEventListener('scroll', () => this._onScroll(), { passive: true });
    this._setActiveLinkFromHash();
  },

  _onScroll() {
    this.el.classList.toggle('scrolled', window.scrollY > 20);
    let current = '';
    this.sections.forEach(sec => {
      const top = sec.offsetTop - 120;
      if (window.scrollY >= top) current = sec.getAttribute('id');
    });

    this.navLinks.forEach(link => {
      const href = link.getAttribute('href').replace('#', '');
      link.classList.toggle('active', href === current);
    });
  },

  _setActiveLinkFromHash() {
    const hash = window.location.hash;
    if (!hash) return;
    const link = document.querySelector(`.header__nav-link[href="${hash}"]`);
    if (link) {
      this.navLinks.forEach(l => l.classList.remove('active'));
      link.classList.add('active');
    }
  }
};

/* =====================================================
   3. MOBILE NAV
===================================================== */
const MobileNav = {
  toggle: document.getElementById('menu-toggle'),
  nav: document.getElementById('navbar'),

  init() {
    if (!this.toggle || !this.nav) return;

    this.toggle.addEventListener('click', () => {
      const isOpen = this.nav.classList.toggle('open');
      this.toggle.setAttribute('aria-expanded', isOpen);
    });

    this.nav.querySelectorAll('a').forEach(link => {
      link.addEventListener('click', () => {
        this.nav.classList.remove('open');
        this.toggle.setAttribute('aria-expanded', 'false');
      });
    });

    document.addEventListener('click', e => {
      if (!this.nav.contains(e.target) && !this.toggle.contains(e.target)) {
        this.nav.classList.remove('open');
        this.toggle.setAttribute('aria-expanded', 'false');
      }
    });
  }
};

/* =====================================================
   4. SEARCH OVERLAY & LIVE API MENU FETCHING
===================================================== */
const SearchOverlay = {
  overlay: document.getElementById('search-overlay'),
  openBtn: document.getElementById('search-toggle'),
  closeBtn: document.getElementById('search-close'),
  input:   document.getElementById('search-input'),
  suggestions: document.getElementById('search-suggestions'),

  menuData: [],

  async init() {
    if (!this.overlay) return;

    await this.fetchLiveMenu();

    this.openBtn?.addEventListener('click', () => this.open());
    this.closeBtn?.addEventListener('click', () => this.close());

    document.addEventListener('keydown', e => {
      if (e.key === 'Escape') this.close();
    });

    this.overlay.addEventListener('click', e => {
      if (e.target === this.overlay) this.close();
    });

    this.input?.addEventListener('input', () => this._filter());
  },

  async fetchLiveMenu() {
    try {
      const response = await fetch('/api/menu');
      if (response.ok) {
        this.menuData = await response.json();
      }
    } catch (error) {
      console.error("Could not fetch live menu from backend.", error);
    }
  },

  open() {
    this.overlay.hidden = false;
    this.openBtn?.setAttribute('aria-expanded', 'true');
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        this.overlay.style.opacity = '';
        this.overlay.style.visibility = '';
      });
    });
    setTimeout(() => this.input?.focus(), 50);
  },

  close() {
    this.overlay.hidden = true;
    this.openBtn?.setAttribute('aria-expanded', 'false');
    if (this.input) this.input.value = '';
    if (this.suggestions) this.suggestions.innerHTML = '';
  },

  _filter() {
    const q = this.input.value.trim().toLowerCase();
    if (!q) { this.suggestions.innerHTML = ''; return; }

    const matches = this.menuData.filter(item => item.name.toLowerCase().includes(q));

    this.suggestions.innerHTML = '';
    if (matches.length === 0) {
      const empty = document.createElement('p');
      empty.textContent = `No results for "${q}"`;
      this.suggestions.appendChild(empty);
      return;
    }

    matches.forEach(m => {
      const a = document.createElement('a');
      a.href = '#order';
      a.style.cssText = 'display:flex; justify-content:space-between; padding:.8rem 0; color:#fff; font-size:1.6rem; border-bottom:1px solid rgba(255,255,255,.08); text-decoration:none;';

      const name = document.createElement('span');
      name.textContent = m.name; // textContent, not innerHTML — no injection possible

      const price = document.createElement('span');
      price.style.cssText = 'color:#c8a97e; font-weight:600;';
      price.textContent = `R${m.price.toFixed(2)}`;

      a.append(name, price);
      a.addEventListener('click', () => this.close());
      this.suggestions.appendChild(a);
    });
  }
};

/* =====================================================
   5. DISH FILTER
===================================================== */
const DishFilter = {
  buttons: document.querySelectorAll('.filter-btn'),
  cards: document.querySelectorAll('.dish-card'),

  init() {
    this.buttons.forEach(btn => {
      btn.addEventListener('click', () => {
        const filter = btn.dataset.filter;

        this.buttons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');

        this.cards.forEach(card => {
          const matches = filter === 'all' || card.dataset.category === filter;
          card.classList.toggle('hidden', !matches);
          if (matches) {
            card.style.animation = 'none';
            void card.offsetHeight;
            card.style.animation = '';
          }
        });
      });
    });
  }
};

/* =====================================================
   6. CART TOAST
===================================================== */
const CartToast = {
  el: document.getElementById('cart-toast'),
  msg: document.getElementById('cart-toast-msg'),
  _timer: null,

  show(message = 'Added to order') {
    if (!this.el) return;
    if (this._timer) clearTimeout(this._timer);
    if (this.msg) this.msg.textContent = message;
    this.el.hidden = false;
    void this.el.offsetHeight;
    this._timer = setTimeout(() => {
      this.el.hidden = true;
    }, 2800);
  }
};

/* =====================================================
   7. BACKEND INTEGRATED SHOPPING BASKET SYSTEM
===================================================== */
const ShoppingBasket = {
  items: [], // Global reactive runtime state
  badge: document.getElementById('cart-badge'),
  drawer: document.getElementById('cart-drawer'),
  toggleBtn: document.getElementById('cart-toggle-btn'),
  closeBtn: document.getElementById('cart-drawer-close-btn'),
  overlay: document.getElementById('cart-drawer-overlay'),
  container: document.getElementById('cart-drawer-items-list'),
  subtotalEl: document.getElementById('cart-drawer-subtotal-value'),
  checkoutBtn: document.getElementById('cart-drawer-checkout-action-btn'),
  formSubmitBtn: document.getElementById('submit-btn'),

  init() {
    // Connect click events to add buttons across cards
    document.querySelectorAll('.dish-card__add, .btn--sm').forEach(btn => {
      btn.addEventListener('click', () => {
        const card = btn.closest('.dish-card, .dessert-card');
        if (!card) return;

        const name = card.querySelector('h3').textContent.trim();
        const priceText = card.querySelector('.dish-card__price, .price').textContent;
        const price = parseFloat(priceText.replace('R', '')) || 0;

        const id = card.dataset.menuId;
        if (!id) {
          CartToast.show('Menu is loading. Please try again.');
          return;
        }
        this.addItem(id, name, price);

        btn.style.transform = 'scale(0.85)';
        setTimeout(() => (btn.style.transform = ''), 200);
      });
    });

    // Drawer visibility bindings
    this.toggleBtn?.addEventListener('click', () => (this.drawer.hidden = false));
    this.closeBtn?.addEventListener('click', () => (this.drawer.hidden = true));
    this.overlay?.addEventListener('click', () => (this.drawer.hidden = true));
    this.checkoutBtn?.addEventListener('click', () => (this.drawer.hidden = true));
  },

  addItem(id, name, price) {
    const existing = this.items.find(item => item.id === id);
    if (existing) {
      existing.quantity += 1;
    } else {
      this.items.push({ id, name, price, quantity: 1 });
    }
    CartToast.show(`${name} added to basket`);
    this.render();
  },

  changeQuantity(id, amount) {
    const item = this.items.find(item => item.id === id);
    if (!item) return;

    item.quantity += amount;
    if (item.quantity <= 0) {
      this.items = this.items.filter(i => i.id !== id);
    }
    this.render();
  },

  render() {
    // Update total counter counts
    const totalCount = this.items.reduce((sum, item) => sum + item.quantity, 0);
    if (this.badge) this.badge.textContent = totalCount;

    // Scale animation pulse trigger on layout changes
    if (this.badge) {
      this.badge.style.transform = 'scale(1.3)';
      setTimeout(() => (this.badge.style.transform = ''), 200);
    }

    if (this.items.length === 0) {
      this.container.innerHTML = `<p class="cart-drawer-empty-placeholder">Your basket is completely empty.</p>`;
      if (this.subtotalEl) this.subtotalEl.textContent = 'R0.00';
      if (this.formSubmitBtn) {
        this.formSubmitBtn.disabled = true;
        this.formSubmitBtn.querySelector('.btn__text').textContent = 'Add Items From Basket First';
      }
      return;
    }

    // Populate lines item elements
    this.container.innerHTML = this.items
        .map(item => `
        <div class="cart-drawer-item">
          <div class="cart-drawer-item-details">
            <span class="cart-drawer-item-name">${escapeHtml(item.name)}</span>
            <span class="cart-drawer-item-price">R${(item.price * item.quantity).toFixed(2)}</span>
          </div>
          <div class="cart-drawer-item-controls">
            <button class="cart-qty-adjust-btn minus" data-id="${escapeHtml(item.id)}">&minus;</button>
            <span class="cart-qty-value">${item.quantity}</span>
            <button class="cart-qty-adjust-btn plus" data-id="${escapeHtml(item.id)}">&plus;</button>
          </div>
        </div>
      `).join('');

    // Attach listeners to active controls inside container panel
    this.container.querySelectorAll('.cart-qty-adjust-btn.plus').forEach(btn => {
      btn.addEventListener('click', () => this.changeQuantity(btn.dataset.id, 1));
    });
    this.container.querySelectorAll('.cart-qty-adjust-btn.minus').forEach(btn => {
      btn.addEventListener('click', () => this.changeQuantity(btn.dataset.id, -1));
    });

    // Compute live prices subtotal tracking metrics
    const subtotal = this.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    if (this.subtotalEl) this.subtotalEl.textContent = `R${subtotal.toFixed(2)}`;

    // Enable validation form submissions layers
    if (this.formSubmitBtn) {
      this.formSubmitBtn.disabled = false;
      this.formSubmitBtn.querySelector('.btn__text').textContent = 'Confirm Order Fulfillment';
    }
  }
};

/* =====================================================
   8. HERO SWIPER
===================================================== */
const HeroSlider = {
  init() {
    if (typeof Swiper === 'undefined') return;
    new Swiper('.hero__slider', {
      loop: true,
      speed: 800,
      autoplay: {
        delay: 6000,
        disableOnInteraction: false,
        pauseOnMouseEnter: true,
      },
      pagination: {
        el: '.hero__pagination',
        clickable: true,
      },
      a11y: {
        prevSlideMessage: 'Previous slide',
        nextSlideMessage: 'Next slide',
      },
      effect: 'fade',
      fadeEffect: { crossFade: true },
    });
  }
};

/* =====================================================
   9. ORDER FORM VALIDATION & FETCH API DISPATCHER
===================================================== */
const OrderForm = {
  form: document.getElementById('order-form'),
  submitBtn: document.getElementById('submit-btn'),

  validators: {
    name:    v => v.trim().length >= 2 ? null : 'Please enter your full name',
    contact: v => /^[\d\s\+\-()]{7,15}$/.test(v.trim()) ? null : 'Enter a valid contact number',
    address: v => v.trim().length >= 8 ? null : 'Please enter a delivery address',
  },

  init() {
    if (!this.form) return;

    Object.keys(this.validators).forEach(fieldName => {
      const field = this.form.elements[fieldName];
      if (!field) return;
      field.addEventListener('blur', () => this._validateField(field));
      field.addEventListener('input', () => {
        if (field.classList.contains('invalid')) this._validateField(field);
      });
    });

    this.form.addEventListener('submit', e => this._onSubmit(e));
  },

  _validateField(field) {
    const name = field.name;
    const validator = this.validators[name];
    if (!validator) return true;

    const error = validator(field.value);
    const errorEl = field.closest('.form-group')?.querySelector('.form-error');

    field.classList.toggle('invalid', !!error);
    field.classList.toggle('valid', !error);
    if (errorEl) errorEl.textContent = error || '';
    return !error;
  },

  async _onSubmit(e) {
    e.preventDefault();

    const allValid = Object.keys(this.validators).every(name => {
      const field = this.form.elements[name];
      return field ? this._validateField(field) : true;
    });

    if (!allValid) {
      const firstInvalid = this.form.querySelector('.form-input.invalid');
      firstInvalid?.focus();
      return;
    }

    if (ShoppingBasket.items.length === 0) {
      CartToast.show('Your basket is empty. Add menu items first!');
      return;
    }

    const btnText = this.submitBtn?.querySelector('.btn__text');
    if (btnText) btnText.textContent = 'Placing your order…';
    if (this.submitBtn) this.submitBtn.disabled = true;

    try {
      const payload = {
        customerName: this.form.elements['name'].value.trim(),
        contactNumber: this.form.elements['contact'].value.trim(),
        deliveryAddress: this.form.elements['address'].value.trim(),
        specialInstructions: this.form.elements['special'].value.trim(),
        items: ShoppingBasket.items.map(item => ({
          menuItemId: item.id,
          quantity: item.quantity
        }))
      };

      const response = await fetch('/api/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        throw new Error(data.message || 'We could not place your order.');
      }
      const order = await response.json();
      ShoppingBasket.items = [];
      ShoppingBasket.render();

      const confirmation = document.createElement('div');
      confirmation.className = 'order-confirmation';
      confirmation.setAttribute('role', 'status');
      const heading = document.createElement('h3');
      heading.textContent = 'Order received!';
      const description = document.createElement('p');
      description.textContent = 'Your order has been saved. This is a demonstration restaurant. No payment has been taken.';
      const reference = document.createElement('p');
      reference.textContent = 'Reference: ' + order.orderId;
      const total = document.createElement('p');
      total.textContent = 'Total: R' + Number(order.total).toFixed(2);
      confirmation.append(heading, description, reference, total);
      this.form.replaceWith(confirmation);
      CartToast.show('Order placed successfully');
    } catch (error) {
      console.error('Checkout failed:', error);
      CartToast.show(error.message || 'Could not place your order. Please try again.');
      if (btnText) btnText.textContent = 'Confirm Order Fulfillment';
      if (this.submitBtn) this.submitBtn.disabled = false;
    }
  }
};

/* =====================================================
   10. SCROLL REVEAL
===================================================== */
const ScrollReveal = {
  init() {
    if (!('IntersectionObserver' in window)) return;

    const targets = [
      '.section-header',
      '.dish-card',
      '.dessert-card',
      '.about__pillar',
      '.about__stat',
      '.order__info > *',
    ];

    targets.forEach((selector, groupIdx) => {
      document.querySelectorAll(selector).forEach((el, i) => {
        el.setAttribute('data-reveal', '');
        el.style.transitionDelay = `${i * 80}ms`;
      });
    });

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed');
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });

    document.querySelectorAll('[data-reveal]').forEach(el => observer.observe(el));
  }
};

/* =====================================================
   11. COPYRIGHT YEAR
===================================================== */
const yearEl = document.getElementById('copyright-year');
if (yearEl) yearEl.textContent = new Date().getFullYear();

/* =====================================================
   12. INIT APPLICATION CONTEXT EXECUTION
===================================================== */

// Use PostgreSQL as the authoritative source of menu IDs, prices and availability.
const MenuSync = {
  async init() {
    const cards = document.querySelectorAll('.dish-card, .dessert-card');
    cards.forEach(card => {
      const button = card.querySelector('.dish-card__add, .btn--sm');
      if (button) button.disabled = true;
    });

    try {
      const response = await fetch('/api/menu');
      if (!response.ok) throw new Error('Menu API unavailable');
      const menu = await response.json();
      const byName = new Map(menu.map(item => [item.name.trim().toLowerCase(), item]));
      cards.forEach(card => {
        const name = card.querySelector('h3')?.textContent.trim().toLowerCase();
        const item = byName.get(name);
        const button = card.querySelector('.dish-card__add, .btn--sm');
        const price = card.querySelector('.dish-card__price, .price');
        if (item && item.available) {
          card.dataset.menuId = item.id;
          if (price) price.textContent = 'R' + Number(item.price).toFixed(2);
          if (button) button.disabled = false;
        } else {
          if (price) price.textContent = 'Unavailable';
        }
      });
    } catch (err) {
      console.error('Unable to load menu:', err);
      CartToast.show('Menu temporarily unavailable. Please try again later.');
    }
  }
};

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, char => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[char]));
}

document.addEventListener('DOMContentLoaded', () => {
  MenuSync.init();
  Loader.hide();
  Header.init();
  MobileNav.init();
  SearchOverlay.init();
  DishFilter.init();
  ShoppingBasket.init();
  HeroSlider.init();
  OrderForm.init();
  ScrollReveal.init();
});