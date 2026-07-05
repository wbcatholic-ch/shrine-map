(function(){
  'use strict';
  if(window.__APP_CACHE_LIFECYCLE_GUARD__) return;
  window.__APP_CACHE_LIFECYCLE_GUARD__ = true;

  var APP_VERSION = window.OAI_APP_BUILD_VERSION || 'V8-1-14-589';
  var SW_BUILD_VERSION = APP_VERSION;
  window.OAI_APP_BUILD_VERSION = APP_VERSION;
  window.APP_VERSION = APP_VERSION;

  /* V8-1-14-589:
     오래 미사용 후 복귀는 app.js의 공통 복귀 지휘자만 담당한다.
     예전 sw-update의 background 자동 reload / cover reset 함수들은
     인트로와 로딩 보호창을 다시 시작시켜 복귀 품질을 낮췄으므로 제거했다.
     앱 파일 갱신은 service worker update 확인과 사용자의 새로고침 흐름에서 처리한다. */
  function now(){ return Date.now ? Date.now() : new Date().getTime(); }
  var hiddenAt = 0;
  document.addEventListener('visibilitychange', function(){
    try{
      if(document.visibilityState === 'hidden'){
        hiddenAt = now();
        sessionStorage.setItem('oai_hidden_at', String(hiddenAt));
        return;
      }
      if(document.visibilityState === 'visible'){
        hiddenAt = 0;
        sessionStorage.removeItem('oai_hidden_at');
        sessionStorage.removeItem('oai_stable_auto_reload_reason');
        sessionStorage.removeItem('oai_background_cover_reset_requested');
      }
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }, true);

  /* V8-1-14-589:
     일반 사용자는 새로고침을 직접 하지 않으므로, 새 service worker가 적용되면
     카테고리 사용 중에는 기다렸다가 커버 화면처럼 안전한 시점에만 조용히 다시 불러온다. */
  var AUTO_UPDATE_PENDING_KEY = 'oai_auto_update_pending_version';
  var AUTO_UPDATE_DONE_KEY = 'oai_auto_update_done_' + APP_VERSION;
  var autoUpdateTimer = 0;

  function isSafeAutoUpdateReloadScreen(){
    try{
      if(document.visibilityState && document.visibilityState !== 'visible') return false;
      var root = document.documentElement;
      if(root && (root.classList.contains('app-active') || root.classList.contains('oai-cover-booting') || root.classList.contains('oai-cover-revealing') || root.classList.contains('oai-background-return-intro'))) return false;
      if(typeof window._isAppScreenActive === 'function' && window._isAppScreenActive()) return false;
      if(typeof window._isCoverScreenVisible === 'function' && !window._isCoverScreenVisible()) return false;
      var cover = document.getElementById('cover');
      if(cover){
        var st = window.getComputedStyle ? window.getComputedStyle(cover) : null;
        if(st && (st.display === 'none' || st.visibility === 'hidden' || Number(st.opacity || 1) < 0.35)) return false;
      }
      if(document.querySelector('#mass-quick-modal.show,#cover-menu-modal.show,#oai-refresh-content-dialog,#oai-cache-clear-dialog')) return false;
      return true;
    }catch(e){ return false; }
  }

  function reloadForAutoUpdate(reason){
    try{
      if(sessionStorage.getItem(AUTO_UPDATE_DONE_KEY) === '1') return true;
      sessionStorage.setItem(AUTO_UPDATE_DONE_KEY, '1');
      localStorage.removeItem(AUTO_UPDATE_PENDING_KEY);
      sessionStorage.setItem('oai_soft_refresh_requested', String(now()));
      sessionStorage.setItem('oai_stable_auto_reload_reason', reason || 'service-worker-update');
      try{ if(typeof oaiMarkRefreshHistoryCompact === 'function') oaiMarkRefreshHistoryCompact('auto-update'); }catch(_e){}
      try{ if(typeof _clearMassQuickReturnForReload === 'function') _clearMassQuickReturnForReload(); }catch(_e){}
      var run = function(){
        try{ location.reload(); }catch(e){ location.href = location.href.split('#')[0]; }
      };
      try{
        if(typeof oaiPrepareRefreshVeil === 'function' && typeof oaiAfterRefreshVeilPaint === 'function'){
          oaiPrepareRefreshVeil('auto-update', 520, 1800, true, 180, false);
          oaiAfterRefreshVeilPaint(function(){ setTimeout(run, 180); });
          return true;
        }
      }catch(_e){}
      setTimeout(run, 220);
      return true;
    }catch(e){
      console.warn('[가톨릭길동무]', e);
      return false;
    }
  }

  function tryAutoUpdateReload(reason){
    try{
      var pending = localStorage.getItem(AUTO_UPDATE_PENDING_KEY);
      if(!pending) return false;
      if(sessionStorage.getItem(AUTO_UPDATE_DONE_KEY) === '1') return false;
      if(isSafeAutoUpdateReloadScreen()) return reloadForAutoUpdate(reason || 'safe-cover');
      if(!autoUpdateTimer){
        autoUpdateTimer = setInterval(function(){
          try{
            if(!localStorage.getItem(AUTO_UPDATE_PENDING_KEY)){ clearInterval(autoUpdateTimer); autoUpdateTimer = 0; return; }
            if(isSafeAutoUpdateReloadScreen()){ clearInterval(autoUpdateTimer); autoUpdateTimer = 0; reloadForAutoUpdate('safe-cover-waited'); }
          }catch(e){ console.warn('[가톨릭길동무]', e); }
        }, 1500);
      }
    }catch(e){ console.warn('[가톨릭길동무]', e); }
    return false;
  }

  function markAutoUpdatePending(reason){
    try{
      if(sessionStorage.getItem(AUTO_UPDATE_DONE_KEY) === '1') return;
      localStorage.setItem(AUTO_UPDATE_PENDING_KEY, APP_VERSION + '|' + String(now()) + '|' + (reason || 'update'));
      tryAutoUpdateReload(reason || 'update');
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }

  function watchRegistration(reg){
    try{
      if(!reg) return;
      if(reg.waiting && navigator.serviceWorker.controller) markAutoUpdatePending('waiting-worker');
      reg.addEventListener('updatefound', function(){
        try{
          var worker = reg.installing;
          if(!worker) return;
          worker.addEventListener('statechange', function(){
            try{
              if(worker.state === 'installed' && navigator.serviceWorker.controller) markAutoUpdatePending('installed-worker');
            }catch(e){ console.warn('[가톨릭길동무]', e); }
          });
        }catch(e){ console.warn('[가톨릭길동무]', e); }
      });
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }

  function registerServiceWorker(){
    if(!('serviceWorker' in navigator)) return;
    try{
      navigator.serviceWorker.register('./sw.js?v=' + encodeURIComponent(SW_BUILD_VERSION || APP_VERSION), { updateViaCache: 'none' })
        .then(function(reg){
          watchRegistration(reg);
          try{ reg.update(); }catch(e){ console.warn('[가톨릭길동무]', e); }
          setTimeout(function(){ try{ if(reg.waiting && navigator.serviceWorker.controller) markAutoUpdatePending('waiting-after-update'); }catch(_e){} }, 1200);
        })
        .catch(function(){});
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }

  if('serviceWorker' in navigator){
    navigator.serviceWorker.addEventListener('controllerchange', function(){
      markAutoUpdatePending('controllerchange');
    });
  }
  ['pageshow','focus'].forEach(function(ev){
    window.addEventListener(ev, function(){ tryAutoUpdateReload(ev); }, true);
  });
  document.addEventListener('visibilitychange', function(){
    if(document.visibilityState === 'visible') tryAutoUpdateReload('visible');
  }, true);
  if(document.readyState === 'loading') document.addEventListener('DOMContentLoaded', function(){ registerServiceWorker(); tryAutoUpdateReload('dom'); }, {once:true});
  else { registerServiceWorker(); tryAutoUpdateReload('ready'); }
})();
