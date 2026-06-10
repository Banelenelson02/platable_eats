/**
 * Plateable Eats — Main Script
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

    // If the local server loaded the page instantly already, run hide immediately!
    if (document.readyState === 'complete') {
      performHide();
    } else {
      // Otherwise, wait for the standard browser load event
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

  menuData: [], // Will be populated by the Spring Boot backend

  async init() {
    if (!this.overlay) return;

    // Fetch the live menu in the background when the page loads
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

    // Filter against live API data
    const matches = this.menuData.filter(item => item.name.toLowerCase().includes(q));

    if (matches.length === 0) {
      this.suggestions.innerHTML = `<p>No results for "${q}"</p>`;
      return;
    }

    // Render matches formatting price in Rands
    this.suggestions.innerHTML = matches
        .map(m => `
        <a href="#order" style="display:flex; justify-content: space-between; padding:.8rem 0; color:#fff; font-size:1.6rem; border-bottom:1px solid rgba(255,255,255,.08); text-decoration: none;">
            <span>${m.name}</span>
            <span style="color: #c8a97e; font-weight: 600;">R${m.price.toFixed(2)}</span>
        </a>`)
        .join('');

    this.suggestions.querySelectorAll('a').forEach(a => {
      a.addEventListener('click', () => this.close());
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
   7. ADD TO ORDER BUTTONS
===================================================== */
const AddButtons = {
  init() {
    document.querySelectorAll('.dish-card__add, .btn--sm').forEach(btn => {
      btn.addEventListener('click', () => {
        const card = btn.closest('.dish-card, .dessert-card');
        const name = card?.querySelector('h3')?.textContent || 'Item';
        CartToast.show(`${name} added to your order`);
        btn.style.transform = 'scale(0.85)';
        setTimeout(() => (btn.style.transform = ''), 200);
      });
    });
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
   9. ORDER FORM VALIDATION & FETCH API
===================================================== */
const OrderForm = {
  form: document.getElementById('order-form'),
  submitBtn: document.getElementById('submit-btn'),

  validators: {
    name:    v => v.trim().length >= 2 ? null : 'Please enter your full name',
    contact: v => /^[\d\s\+\-()]{7,15}$/.test(v.trim()) ? null : 'Enter a valid contact number',
    food:    v => v ? null : 'Please select a dish',
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
    // Intercept default form behavior
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

    // Capture frontend data
    const customerName = this.form.elements['name'].value;
    const mainDish = this.form.elements['food'].value;

    if (this.submitBtn) {
      const btnText = this.submitBtn.querySelector('.btn__text');
      if (btnText) btnText.textContent = 'Sending to Kitchen…';
      this.submitBtn.disabled = true;
    }

    try {
      // POST the order to the Spring Boot Backend
      const orderResponse = await fetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        // Assigning to T2 as a default web-order proxy
        body: JSON.stringify({ tableId: "T2", waiterId: "WEB_ORDER" })
      });

      if (!orderResponse.ok) throw new Error('Server rejected order');
      const orderData = await orderResponse.json();

      CartToast.show('Order successfully transmitted!');

      // Redirect to the success screen with the new database ID
      setTimeout(() => {
        window.location.href = `/results.html?name=${encodeURIComponent(customerName)}&orderId=${orderData.orderId}&dish=${encodeURIComponent(mainDish)}`;
      }, 1500);

    } catch (error) {
      console.error('Network Error:', error);
      CartToast.show('Could not connect to the restaurant. Please try again.');

      // Reset button state on failure
      if (this.submitBtn) {
        const btnText = this.submitBtn.querySelector('.btn__text');
        if (btnText) btnText.textContent = 'Place Order';
        this.submitBtn.disabled = false;
      }
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
   12. INIT
===================================================== */
document.addEventListener('DOMContentLoaded', () => {
  Loader.hide();
  Header.init();
  MobileNav.init();
  SearchOverlay.init();
  DishFilter.init();
  AddButtons.init();
  HeroSlider.init();
  OrderForm.init();
  ScrollReveal.init();
});