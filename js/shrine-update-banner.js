/* V8-1-14-91: 성지순례 기능 업데이트 안내 배너
   Google Play 테스트 단계에서만 표시하고, 승인 후 제거 예정. */
(function(){
  'use strict';
  var ENABLED = window.OAI_SHRINE_UPDATE_BANNER_ENABLED !== false;
  var HIDE_FOREVER_KEY = 'oai_shrine_update_banner_v91_hidden_forever';
  var HIDE_UNTIL_KEY = 'oai_shrine_update_banner_v91_hide_until';
  var FIRST_DATE_KEY = 'oai_shrine_update_banner_v91_first_date';
  var SESSION_KEY = 'oai_shrine_update_banner_v91_session_shown';
  function todayKey(){
    var d = new Date();
    var m = String(d.getMonth()+1).padStart(2,'0');
    var day = String(d.getDate()).padStart(2,'0');
    return d.getFullYear() + '-' + m + '-' + day;
  }
  function nextLocalMidnight(){
    var d = new Date();
    return new Date(d.getFullYear(), d.getMonth(), d.getDate()+1, 0, 0, 0, 0).getTime();
  }
  function isInstalledRun(){
    var ua='';
    try{ ua=String(navigator.userAgent||'').toLowerCase(); }catch(_e){}
    if(/kakaotalk|kakaostory|kakao/.test(ua)) return false;
    try{ if(window.matchMedia && (window.matchMedia('(display-mode: standalone)').matches || window.matchMedia('(display-mode: fullscreen)').matches || window.matchMedia('(display-mode: minimal-ui)').matches)) return true; }catch(_e){}
    try{ if(window.navigator && window.navigator.standalone) return true; }catch(_e){}
    try{ if(document.referrer && String(document.referrer).indexOf('android-app://')===0) return true; }catch(_e){}
    try{ if(/; wv\)|\bwv\b/.test(ua)) return true; }catch(_e){}
    try{ if(window.OAI_FORCE_SHRINE_UPDATE_BANNER === true) return true; }catch(_e){}
    return false;
  }
  function hiddenForever(){ try{ return localStorage.getItem(HIDE_FOREVER_KEY)==='1'; }catch(_e){ return false; } }
  function hiddenToday(){
    try{
      var until = parseInt(localStorage.getItem(HIDE_UNTIL_KEY)||'0',10)||0;
      return until && Date.now && Date.now() < until;
    }catch(_e){ return false; }
  }
  function markFirstShownDate(){
    try{ if(!localStorage.getItem(FIRST_DATE_KEY)) localStorage.setItem(FIRST_DATE_KEY, todayKey()); }catch(_e){}
  }
  function shouldShowNeverButton(){
    try{
      var first = localStorage.getItem(FIRST_DATE_KEY) || '';
      return !!(first && first !== todayKey());
    }catch(_e){ return false; }
  }
  function hideForever(){ try{ localStorage.setItem(HIDE_FOREVER_KEY,'1'); }catch(_e){} hide(); }
  function hideForToday(){ try{ localStorage.setItem(HIDE_UNTIL_KEY, String(nextLocalMidnight())); }catch(_e){} hide(); }
  function markSession(){ try{ sessionStorage.setItem(SESSION_KEY,'1'); }catch(_e){} }
  function shownThisSession(){ try{ return sessionStorage.getItem(SESSION_KEY)==='1'; }catch(_e){ return false; } }
  function coverReady(){
    var cover=document.getElementById('cover');
    if(!cover) return false;
    try{
      var st=getComputedStyle(cover);
      if(st.display==='none' || st.visibility==='hidden' || st.opacity==='0') return false;
    }catch(_e){}
    try{ if(document.documentElement.classList.contains('app-active')) return false; }catch(_e){}
    try{ if(document.documentElement.classList.contains('oai-cover-booting') || document.documentElement.classList.contains('oai-cover-revealing')) return false; }catch(_e){}
    return true;
  }
  function hide(){ var el=document.getElementById('shrine-update-banner'); if(el) el.classList.remove('show'); }
  function create(){
    var el=document.getElementById('shrine-update-banner');
    if(el) return el;
    el=document.createElement('section');
    el.id='shrine-update-banner';
    el.setAttribute('aria-label','성지순례 기능 업데이트 안내');
    el.innerHTML=''+
      '<div class="shrine-update-card" role="dialog" aria-modal="false">'+
        '<div class="shrine-update-head">'+
          '<div class="shrine-update-icon" aria-hidden="true">🙏</div>'+
          '<div class="shrine-update-title">성지순례 기능 업데이트 안내</div>'+
          '<button type="button" class="shrine-update-x" data-shrine-update-close aria-label="닫기">×</button>'+
        '</div>'+
        '<div class="shrine-update-body">'+
          '<b>성지순례 기록 기능과 경로검색 기능이 추가되었습니다.</b>'+
          '<div class="shrine-update-list">'+
            '<div>• 성지찾기 카드 및 정보카드에서 <b>순례등록</b> 버튼으로 수동 순례등록</div>'+
            '<div>• 성지 근처에서는 <b>GPS 자동 감지</b>로 오늘 순례등록</div>'+
            '<div>• 스탬프북에서 순례한 성지, 미방문 성지, 신규 성지 확인</div>'+
            '<div>• 경로검색에서 <b>경유지 최대 3곳</b>까지 추가</div>'+
          '</div>'+
        '</div>'+
        '<div class="shrine-update-actions">'+
          '<button type="button" class="shrine-update-go" data-shrine-update-go>성지순례 바로가기</button>'+
          '<button type="button" class="shrine-update-later" data-shrine-update-later></button>'+
          '<button type="button" class="shrine-update-close" data-shrine-update-close>닫기</button>'+
        '</div>'+
      '</div>';
    document.body.appendChild(el);
    el.addEventListener('click',function(e){
      var t=e.target;
      if(!t || !t.closest) return;
      if(t.closest('[data-shrine-update-later]')){
        e.preventDefault();
        if(shouldShowNeverButton()) hideForever();
        else hideForToday();
        return;
      }
      if(t.closest('[data-shrine-update-close]')){ e.preventDefault(); markSession(); hide(); return; }
      if(t.closest('[data-shrine-update-go]')){
        e.preventDefault(); markSession(); hide();
        setTimeout(function(){
          var btn=document.getElementById('cc-3');
          try{ if(btn) btn.click(); }catch(_e){}
        },30);
      }
    }, true);
    return el;
  }
  function updateActionLabel(el){
    try{
      var btn = el && el.querySelector ? el.querySelector('[data-shrine-update-later]') : null;
      if(btn) btn.textContent = shouldShowNeverButton() ? '다시 보지 않기' : '하루 동안 안 보기';
    }catch(_e){}
  }
  function show(){
    if(!ENABLED || hiddenForever() || hiddenToday() || shownThisSession() || !isInstalledRun()) return;
    if(!coverReady()){ setTimeout(show,350); return; }
    markFirstShownDate();
    var el=create();
    updateActionLabel(el);
    markSession();
    setTimeout(function(){ el.classList.add('show'); },60);
  }
  function boot(){ [900,1800,3200,6200].forEach(function(ms){ setTimeout(show,ms); }); }
  if(document.readyState==='loading') document.addEventListener('DOMContentLoaded', boot, {once:true});
  else boot();
  window.addEventListener('pageshow', function(){ setTimeout(show,900); });
  window.addEventListener('focus', function(){ setTimeout(show,900); });
})();
