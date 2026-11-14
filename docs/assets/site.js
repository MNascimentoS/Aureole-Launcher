(() => {
  const reduce = matchMedia('(prefers-reduced-motion: reduce)').matches;
  let demoVisible = false;

  // Topo: a tela inicial troca de tema com uma onda que nasce no relógio
  const rot = document.querySelector('.rotator');
  if (rot) {
    const imgs = [...rot.querySelectorAll('img')];
    let i = 0, timer = null;
    const next = () => {
      const prev = imgs[i];
      i = (i + 1) % imgs.length;
      imgs.forEach((im) => im.classList.remove('was-on'));
      prev.classList.replace('is-on', 'was-on');
      imgs[i].classList.add('is-on');
    };
    const start = () => { if (!reduce && !timer) timer = setInterval(next, 1600); };
    const stop = () => { clearInterval(timer); timer = null; };
    document.addEventListener('visibilitychange', () => (document.hidden ? stop() : start()));
    start();
  }

  // "Como funciona": a demo do app com capítulos sincronizados
  const demo = document.getElementById('demo');
  const chapters = [...document.querySelectorAll('.chapter')];
  if (demo && chapters.length) {
    let current = -1;
    const setActive = (i) => {
      if (i === current) return;
      chapters.forEach((c, j) => c.toggleAttribute('aria-current', j === i));
      current = i;
    };
    const update = () => {
      const t = demo.currentTime;
      const i = Math.max(0, chapters.findIndex((c) => t >= +c.dataset.start && t < +c.dataset.end));
      setActive(i);
      const c = chapters[i];
      const p = (t - c.dataset.start) / (c.dataset.end - c.dataset.start);
      c.style.setProperty('--p', Math.min(1, Math.max(0, p)).toFixed(3));
    };
    demo.addEventListener('timeupdate', update);
    chapters.forEach((c) => c.addEventListener('click', () => {
      demo.currentTime = +c.dataset.start + 0.05;
      update();
      const r = demo.getBoundingClientRect();
      if (r.top < 0 || r.bottom > innerHeight) demo.scrollIntoView({ behavior: reduce ? 'auto' : 'smooth', block: 'center' });
      demo.play().catch(() => {});
    }));
    setActive(0);

    if (reduce) {
      demo.controls = true;
    } else {
      // toca só quando está na tela
      new IntersectionObserver(([e]) => {
        demoVisible = e.isIntersecting;
        if (e.isIntersecting) demo.play().catch(() => {});
        else demo.pause();
      }, { threshold: 0.35 }).observe(demo);
    }
  }

  // Vídeo completo em tela cheia: horizontal no computador, vertical no celular
  const dialog = document.querySelector('.video-dialog');
  if (dialog && typeof dialog.showModal === 'function') {
    const video = dialog.querySelector('video');
    document.querySelectorAll('[data-open-video]').forEach((b) => b.addEventListener('click', () => {
      const vertical = matchMedia('(max-aspect-ratio: 4/5)').matches;
      dialog.classList.toggle('is-vertical', vertical);
      const src = vertical ? video.dataset.vertical : video.dataset.landscape;
      if (video.getAttribute('src') !== src) {
        video.src = src;
        video.poster = vertical ? video.dataset.posterVertical : video.dataset.posterLandscape;
      }
      if (demo) demo.pause();
      dialog.showModal();
      video.currentTime = 0;
      if (!reduce) video.play().catch(() => {});
    }));
    const close = () => dialog.close();
    dialog.querySelector('[data-close-video]').addEventListener('click', close);
    dialog.addEventListener('click', (e) => { if (e.target === dialog) close(); });
    dialog.addEventListener('close', () => {
      video.pause();
      if (demo && demoVisible && !reduce) demo.play().catch(() => {});
    });
  } else {
    document.querySelectorAll('[data-open-video]').forEach((b) => b.hidden = true);
  }
})();
