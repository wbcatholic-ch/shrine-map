/* sw-update.js — 서비스워커 캐시 버전 관리
   앱 버전 변경 감지 → 강제 새로고침 처리
   원본 index.html Block F 에서 분리 */

(function(){
  'use strict';
  if(window.__APP_CACHE_LIFECYCLE_GUARD__) return;
  window.__APP_CACHE_LIFECYCLE_GUARD__ = true;
  // APP_VERSION:      화면 표시용 단축 버전 (build marker, data-target-version)
  // SW_BUILD_VERSION:  SW 등록·캐시 키용 전체 버전 (sw.js BUILD_VERSION과 일치해야 함)
  // ★ 버전 업그레이드 시 두 값 모두 수정, sw.js BUILD_VERSION과 SW_BUILD_VERSION을 동일하게 맞출 것
  var APP_VERSION = 'V-2';
  var SW_BUILD_VERSION = 'V2-110';
  window.APP_VERSION = APP_VERSION;

  function now(){ return Date.now ? Date.now() : new Date().getTime(); }
  function isTypingTarget(el){
    if(!el) return false;
    var tag = (el.tagName || '').toLowerCase();
    return tag === 'input' || tag === 'textarea' || el.isContentEditable;
  }
  function isTransientOpen(){
    try{
      if(document.getElementById('srch-modal') && document.getElementById('srch-modal').classList.contains('open')) return true;
      if(document.getElementById('sheet-route') && document.getElementById('sheet-route').classList.contains('open')) return true;
      if(document.getElementById('missa-view') && document.getElementById('missa-view').classList.contains('open')) return true;
      if(document.getElementById('mass-quick-modal') && document.getElementById('mass-quick-modal').classList.contains('show')) return true;
      if(document.querySelector && document.querySelector('.guide-modal.show')) return true;
    }catch(e){ console.warn("[가톨릭길동무]", e); }
    return false;
  }
  function hasExternalReturnPending(){
    try{
      return sessionStorage.getItem('oai_external_nav_pending') === '1' ||
             sessionStorage.getItem('oai_external_nav_pagehide') === '1' ||
             sessionStorage.getItem('oai_external_nav_kind') === 'my-faith-life';
    }catch(e){ return false; }
  }
  function canBackgroundRefresh(){
    try{ if(isTypingTarget(document.activeElement) || isTransientOpen() || hasExternalReturnPending()) return false; }catch(e){ console.warn("[가톨릭길동무]", e); }
    return true;
  }
  function stableReload(reason){
    if(!canBackgroundRefresh()) return false;
    try{ sessionStorage.setItem(SS.STABLE_AUTO_RELOAD_REASON, reason || 'maintenance'); }catch(e){ console.warn("[가톨릭길동무]", e); }
    try{ if(typeof window.oaiPrepareRefreshVeil === 'function') window.oaiPrepareRefreshVeil(reason || 'background-reload', 1000, 12000); }catch(e){ console.warn("[가톨릭길동무]", e); }
    try{ if(typeof window.oaiMarkRefreshHistoryCompact === 'function') window.oaiMarkRefreshHistoryCompact(reason || 'background-reload'); }catch(e){ console.warn("[가톨릭길동무]", e); }
    setTimeout(function(){ try{ location.reload(); }catch(e){ location.href = location.href; } }, 120);
    return true;
  }
  function softBackgroundReturn(reason){
    if(!canBackgroundRefresh()) return false;
    try{ sessionStorage.removeItem(SS.STABLE_AUTO_RELOAD_REASON); }catch(e){ console.warn("[가톨릭길동무]", e); }
    try{
      if(typeof window.oaiSoftBackgroundReturn === 'function'){
        window.oaiSoftBackgroundReturn(reason || 'background-return');
        return true;
      }
    }catch(e){ console.warn("[가톨릭길동무]", e); }
    return false;
  }
  function clearReturnFlagsForBackground(){
    try{
      sessionStorage.removeItem(SS.MASS_QUICK_RETURN);
      sessionStorage.removeItem(SS.MASS_QUICK_RETURN_TS);
      sessionStorage.removeItem(SS.PRAYER_QUICK_RETURN);
      sessionStorage.removeItem(SS.PRAYER_QUICK_RETURN_TS);
      sessionStorage.removeItem(SS.PRAYER_FROM_QUICK_LOCK);
      sessionStorage.removeItem(SS.EXTERNAL_RETURN_STABILIZE);
      sessionStorage.removeItem(SS.EXTERNAL_NAV_PENDING);
      sessionStorage.removeItem(SS.EXTERNAL_NAV_STARTED_AT);
      sessionStorage.removeItem(SS.EXTERNAL_NAV_PAGEHIDE);
      localStorage.removeItem(SS.MASS_QUICK_RETURN);
      localStorage.removeItem(SS.MASS_QUICK_RETURN_TS);
      window.__MASS_QUICK_RETURN__ = false;
      window.__MASS_QUICK_FROM_PRAYER__ = false;
      window.__OAI_PRAYER_FROM_QUICK_LOCK__ = false;
    }catch(e){ console.warn("[가톨릭길동무]", e); }
  }
  function resetToCoverForBackground(){
    // V2-110: 장시간 백그라운드 복귀에서도 커버 이동/자동 새로고침을 하지 않는다.
    // 현재 화면을 유지하고 필요한 화면 높이·지도 resize만 조용히 한 번 정리한다.
    if(!canBackgroundRefresh()) return false;
    clearReturnFlagsForBackground();
    try{ sessionStorage.setItem(SS.BACKGROUND_COVER_RESET, String(now())); }catch(e){ console.warn("[가톨릭길동무]", e); }
    return softBackgroundReturn('background-long-return');
  }

  /* 백그라운드 복귀 정책
     - 15분 이상: 현재 화면 유지 + 조용한 화면 안정화
     - 30분 이상: 커버 이동/자동 새로고침 없이 현재 화면 유지
     - 입력/길찾기/팝업 등 사용 중 화면에서는 보류 */
  var hiddenAt = 0;
  var BACKGROUND_SOFT_RELOAD_AFTER = 15 * 60 * 1000;
  var BACKGROUND_COVER_RESET_AFTER = 30 * 60 * 1000;
  document.addEventListener('visibilitychange', function(){
    if(document.visibilityState === 'hidden'){
      hiddenAt = now();
      try{ sessionStorage.setItem(SS.HIDDEN_AT, String(hiddenAt)); }catch(e){ console.warn("[가톨릭길동무]", e); }
      return;
    }
    if(document.visibilityState === 'visible'){
      var last = hiddenAt;
      try{ last = Math.max(last, parseInt(sessionStorage.getItem(SS.HIDDEN_AT) || '0', 10) || 0); }catch(e){ console.warn("[가톨릭길동무]", e); }
      if(!last) return;
      var elapsed = now() - last;
      if(elapsed >= BACKGROUND_COVER_RESET_AFTER){
        setTimeout(function(){ resetToCoverForBackground(); }, 350);
      }else if(elapsed >= BACKGROUND_SOFT_RELOAD_AFTER){
        setTimeout(function(){ softBackgroundReturn('background-soft-return'); }, 350);
      }
    }
  }, true);

  /* 서비스워커는 캐시를 매번 지우지 않고, 버전이 바뀔 때만 오래된 캐시를 정리합니다. */
  function registerServiceWorker(){
    if(!('serviceWorker' in navigator)) return;
    try{
      navigator.serviceWorker.register('./sw.js?v=' + encodeURIComponent(SW_BUILD_VERSION || APP_VERSION), { updateViaCache: 'none' })
        .then(function(reg){ try{ reg.update(); }catch(e){ console.warn("[가톨릭길동무]", e); } })
        .catch(function(){});
    }catch(e){ console.warn("[가톨릭길동무]", e); }
  }
  if(document.readyState === 'loading') document.addEventListener('DOMContentLoaded', registerServiceWorker, {once:true});
  else registerServiceWorker();
})();
