(function(){
  'use strict';
  if(window.__OAI_CONTROLLED_AUTO_UPDATE__) return;
  window.__OAI_CONTROLLED_AUTO_UPDATE__ = true;

  var APP_VERSION = window.OAI_APP_BUILD_VERSION || 'V8-1-14-666';
  var CHECK_URL = './version.json';
  var RELOAD_KEY = 'oai_auto_update_reloaded_version';
  var checking = false;

  window.APP_VERSION = APP_VERSION;

  function fetchRemoteVersion(){
    return fetch(CHECK_URL + '?t=' + Date.now(), {
      cache:'no-store',
      credentials:'same-origin',
      headers:{'Cache-Control':'no-cache'}
    }).then(function(res){
      if(!res || !res.ok) throw new Error('version check failed');
      return res.json();
    }).then(function(data){
      return String(data && data.version || '').trim();
    });
  }

  function waitForActivation(reg, targetVersion){
    return new Promise(function(resolve){
      var done=false;
      function finish(){
        if(done) return;
        done=true;
        resolve();
      }
      var timer=setTimeout(finish, 5000);
      function activated(){
        clearTimeout(timer);
        finish();
      }
      try{
        var sw=reg && (reg.installing || reg.waiting);
        if(sw){
          if(sw.state==='activated') return activated();
          sw.addEventListener('statechange', function(){
            if(sw.state==='activated') activated();
          });
        }else{
          setTimeout(activated, 250);
        }
      }catch(_e){ activated(); }
    });
  }

  function applyUpdate(targetVersion){
    if(!('serviceWorker' in navigator)) {
      sessionStorage.setItem(RELOAD_KEY, targetVersion);
      location.reload();
      return Promise.resolve();
    }
    return navigator.serviceWorker.register(
      './sw.js?v=' + encodeURIComponent(targetVersion),
      {updateViaCache:'none'}
    ).then(function(reg){
      return reg.update().catch(function(){}).then(function(){
        return waitForActivation(reg, targetVersion);
      });
    }).catch(function(){}).then(function(){
      try{ sessionStorage.setItem(RELOAD_KEY, targetVersion); }catch(_e){}
      location.reload();
    });
  }

  function checkForUpdate(reason){
    if(checking || document.visibilityState==='hidden') return Promise.resolve(false);
    checking=true;
    return fetchRemoteVersion().then(function(remote){
      if(!remote || remote===APP_VERSION) return false;
      try{
        if(sessionStorage.getItem(RELOAD_KEY)===remote) return false;
      }catch(_e){}
      return applyUpdate(remote).then(function(){ return true; });
    }).catch(function(err){
      console.warn('[가톨릭길동무] update check skipped:', reason || '', err && err.message || err);
      return false;
    }).finally(function(){ checking=false; });
  }

  function registerCurrentServiceWorker(){
    if(!('serviceWorker' in navigator)) return;
    navigator.serviceWorker.register(
      './sw.js?v=' + encodeURIComponent(APP_VERSION),
      {updateViaCache:'none'}
    ).then(function(reg){
      try{ reg.update(); }catch(_e){}
    }).catch(function(){});
  }

  window.oaiCheckForAppUpdate = checkForUpdate;

  function boot(){
    registerCurrentServiceWorker();
    setTimeout(function(){ checkForUpdate('cold-start'); }, 350);
  }
  if(document.readyState==='loading'){
    document.addEventListener('DOMContentLoaded', boot, {once:true});
  }else{
    boot();
  }

  /* app.js dispatches this only after a >=30 minute background return.
     Short returns intentionally do not check for updates. */
  window.addEventListener('oai-long-background-return', function(){
    setTimeout(function(){ checkForUpdate('long-background-return'); }, 300);
  }, {passive:true});
})();