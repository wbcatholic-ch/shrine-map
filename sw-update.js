(function(){
  'use strict';
  if(window.__APP_CACHE_LIFECYCLE_GUARD__) return;
  window.__APP_CACHE_LIFECYCLE_GUARD__ = true;

  var APP_VERSION = window.OAI_APP_BUILD_VERSION || 'V8-1-14-663';
  var SW_BUILD_VERSION = APP_VERSION;
  window.OAI_APP_BUILD_VERSION = APP_VERSION;
  window.APP_VERSION = APP_VERSION;

  /* V8-1-14-663:
     새 service worker가 설치·활성화될 때 현재 문서를 자동 새로고침하지 않는다.
     index.html과 버전 자산은 이미 현재 빌드로 로드되어 있으므로 즉시 reload할 필요가 없고,
     기존 자동 reload는 첫 진입 인트로 직후 로딩 십자가를 한 번 더 노출했다.
     service worker는 뒤에서 조용히 갱신하고, 새 캐시는 다음 탐색/앱 실행부터 사용한다. */
  function clearLegacyAutoUpdateState(){
    try{
      localStorage.removeItem('oai_auto_update_pending_version');
      var remove=[];
      for(var i=0;i<sessionStorage.length;i++){
        var key=sessionStorage.key(i) || '';
        if(key.indexOf('oai_auto_update_done_')===0) remove.push(key);
      }
      for(var j=0;j<remove.length;j++) sessionStorage.removeItem(remove[j]);
      sessionStorage.removeItem('oai_stable_auto_reload_reason');
      sessionStorage.removeItem('oai_soft_refresh_requested');
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }

  function registerServiceWorker(){
    if(!('serviceWorker' in navigator)) return;
    try{
      navigator.serviceWorker.register('./sw.js?v=' + encodeURIComponent(SW_BUILD_VERSION || APP_VERSION), { updateViaCache: 'none' })
        .then(function(reg){
          try{ reg.update(); }catch(e){ console.warn('[가톨릭길동무]', e); }
        })
        .catch(function(){});
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }

  clearLegacyAutoUpdateState();
  if(document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', registerServiceWorker, {once:true});
  }else{
    registerServiceWorker();
  }
})();
