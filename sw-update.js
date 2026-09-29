(function(){
  'use strict';
  if(window.__OAI_CONTROLLED_AUTO_UPDATE__) return;
  window.__OAI_CONTROLLED_AUTO_UPDATE__ = true;

  function versionParts(v){
    var m=String(v||'').match(/V?(\d+)-(\d+)-(\d+)-(\d+)/i);
    return m ? [Number(m[1]),Number(m[2]),Number(m[3]),Number(m[4])] : null;
  }
  function compareVersions(a,b){
    var aa=versionParts(a), bb=versionParts(b);
    if(!aa || !bb) return String(a||'').localeCompare(String(b||''));
    for(var i=0;i<4;i++){ if(aa[i]!==bb[i]) return aa[i]-bb[i]; }
    return 0;
  }
  function currentDocumentVersion(){
    var values=[];
    try{ if(window.OAI_APP_BUILD_VERSION) values.push(String(window.OAI_APP_BUILD_VERSION)); }catch(_e){}
    try{ var b=document.getElementById('cover-update-btn'); if(b&&b.getAttribute('data-target-version')) values.push(b.getAttribute('data-target-version')); }catch(_e){}
    try{ var m=document.getElementById('oai-build-marker'); if(m&&m.textContent) values.push(String(m.textContent).trim()); }catch(_e){}
    var best='';
    values.forEach(function(v){ if(v && (!best || compareVersions(v,best)>0)) best=v; });
    return best || 'V8-1-14-875';
  }
  var APP_VERSION = currentDocumentVersion();
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
      if(!remote || compareVersions(remote,APP_VERSION)<=0) return false;
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
