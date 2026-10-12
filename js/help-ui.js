/* V1186: 기능 도움말 표시 전용 - GPS/순례 저장 기능에는 관여하지 않음 */
(function(){
  'use strict';
  const HELP = {
    settings:['설정','교구·본당, 방문 기록, 내 기록 보관을 관리합니다.\n각 항목을 변경하면 안내된 방식으로 적용됩니다.'],
    settings_diocese:['교구·본당','내 교구와 본당을 지정합니다.\n본당 지정은 GPS 방문기록 제한 설정에도 사용됩니다.'],
    settings_visits:['방문 기록','GPS로 확인한 성지·성당 방문을 기록합니다.\n내 본당 자동 기록 제한을 켜면 이후 자동 방문기록을 제한합니다. 이미 찍힌 스탬프는 삭제하지 않습니다.'],
    settings_frequent:['자주 가는 장소','집·본당·직장 등 자주 쓰는 장소를 최대 5개 저장합니다.\n길찾기의 ‘빠른 선택’에서 바로 불러오고, 닉네임을 바꾸거나 삭제할 수 있습니다.'],
    settings_backup:['내 기록 보관','개인 순례·방문 기록을 보관하고 복원합니다.\n백업 복원은 현재 기록을 교체할 수 있으므로 복원 전 원본을 보관하세요.'],
    diocese:['관구·교구 지도','한국 천주교의 관구와 교구 정보를 지도에서 살펴볼 수 있습니다.'],
    prayer:['주요 기도문','가톨릭 주요 기도문을 찾아 읽을 수 있습니다.\n굿뉴스 기도문 바로가기도 이용할 수 있습니다.'],
    pilgrimage:['순례코스','순례계획을 만들고 GPS로 순례한 장소를 순례완료에서 확인합니다.\n일반 성당 한 곳만 방문한 날에는 자동 코스를 만들지 않습니다.\nGPS 완료 코스는 삭제할 수 없으며 누락한 장소는 수동 추가할 수 있습니다.'],
    websites:['가톨릭 주요 웹사이트','교구와 가톨릭 기관 홈페이지를 분류별로 찾아볼 수 있습니다.'],
    trails:['전국 순례길','순례길을 지도나 목록에서 살펴보고 경로를 확인할 수 있습니다.'],
    feedback:['문의·건의','앱의 오류, 자료 수정, 기능 개선 의견을 남길 수 있습니다.'],
    shrine:['성지','전국 성지와 순교 사적지를 찾아보세요.\nGPS 방문이 확인되면 순례 스탬프가 기록됩니다.'],
    parish:['성당','성당의 위치와 정보를 검색할 수 있습니다.\n일반 성당은 같은 날 두 곳 이상 GPS 방문해야 단독 순례코스로 등록됩니다.'],
    retreat:['피정','피정의 집과 관련 정보를 찾아보세요.\n방문 전 각 시설의 운영·예약 정보를 확인하세요.'],
    nearby:['내 주변','현재 위치를 기준으로 주변 성지·성당·피정 장소를 찾습니다.\n위치 권한이 필요할 수 있습니다.'],
    region:['지역검색','지역이나 주소를 입력하여 성지·성당을 검색합니다.'],
    find:['찾기 목록','장소 이름이나 주소로 검색하고 목록에서 상세 정보를 확인하세요.'],

    shrine_nearby:['성지 · 내 주변','GPS 위치를 기준으로 가까운 성지를 찾습니다.\n위치 권한이 필요할 수 있습니다.'],
    parish_nearby:['성당 · 내 주변','현재 위치에서 가까운 성당을 확인합니다.\n일반 성당은 하루 두 곳 이상 방문해야 자동 순례코스가 됩니다.'],
    retreat_nearby:['피정 · 내 주변','현재 위치에서 가까운 피정 시설을 찾습니다.\n방문 전에 운영 정보를 확인해 주세요.'],
    shrine_find:['성지 · 목록','성지 이름이나 주소로 검색하고 상세 정보를 확인합니다.'],
    parish_find:['성당 · 목록','성당 이름이나 주소로 검색하고 상세 정보를 확인합니다.'],
    retreat_find:['피정 · 목록','피정 시설 이름이나 지역으로 검색합니다.\n예약 및 운영 여부는 시설에 확인하세요.'],
    shrine_region:['성지 · 지역검색','지역명으로 성지를 찾아볼 수 있습니다.'],
    parish_region:['성당 · 지역검색','지역명으로 성당을 찾아볼 수 있습니다.'],
    retreat_region:['피정 · 지역검색','지역명으로 피정 시설을 찾아볼 수 있습니다.'],
    shrine_book:['순례 스탬프북','GPS로 확인된 성지 방문기록을 볼 수 있습니다.\n스탬프와 사진·메모는 순례완료 코스와 별도로 보존됩니다.'],
    parish_book:['성당 방문 스탬프북','GPS로 확인된 성당 방문기록을 살펴봅니다.\n내 본당 자동 기록 제한은 설정에서 변경할 수 있습니다.'],
    shrine_detail:['순례 기록','이 성지의 GPS 순례 날짜와 횟수를 확인합니다.\n사진·메모 관리는 설정의 개발자 도구에서만 선택적으로 표시합니다.'],
    parish_detail:['성당 방문기록','GPS 방문 날짜와 횟수를 확인합니다.\n일반 성당 한 곳만 방문한 날은 자동 순례코스를 생성하지 않습니다.'],
    directions:['길찾기','출발지와 도착지를 지정하고 필요한 경유지를 추가하세요.\n장소 칸을 누르면 바꿀 수 있고, 길찾기 결과에서 내비를 실행할 수 있습니다.'],
    stamps:['방문 스탬프','GPS로 확인한 방문기록을 날짜별로 보관합니다.\n사진과 메모 등 개인 기록은 별도로 유지됩니다.']
  };
  let lastFocused=null;
  function show(key){
    const data=HELP[key];if(!data)return;
    lastFocused=document.activeElement;
    let layer=document.getElementById('cgm-help-overlay');
    if(!layer){
      layer=document.createElement('div');layer.id='cgm-help-overlay';layer.className='cgm-help-overlay';layer.hidden=true;
      layer.innerHTML='<div class="cgm-help-dialog" role="dialog" aria-modal="true" aria-labelledby="cgm-help-title"><h3 id="cgm-help-title"></h3><p id="cgm-help-text"></p><button type="button" class="cgm-help-close">확인</button></div>';
      document.body.appendChild(layer);
      layer.addEventListener('click',function(ev){if(ev.target===layer||ev.target.closest('.cgm-help-close'))hide();});
      layer.addEventListener('keydown',function(ev){if(ev.key==='Escape'){ev.preventDefault();hide();}if(ev.key==='Tab'){ev.preventDefault();layer.querySelector('button').focus();}});
    }
    layer.querySelector('#cgm-help-title').textContent=data[0];
    layer.querySelector('#cgm-help-text').textContent=data[1];
    layer.hidden=false;layer.style.display='flex';layer.querySelector('button').focus();
  }
  function hide(){const layer=document.getElementById('cgm-help-overlay');if(layer){layer.hidden=true;layer.style.display='none';}if(lastFocused&&lastFocused.focus)lastFocused.focus();}
  function connectHelpButton(button){
    if(!button||button.dataset.cgmHelpBound==='1')return;
    button.dataset.cgmHelpBound='1';
    button.addEventListener('click',function(e){
      e.preventDefault();e.stopPropagation();
      show(button.getAttribute('data-cgm-help'));
    },true);
  }
  // Every existing button gets its own handler. Newly-rendered module buttons are bound too.
  document.addEventListener('click',function(e){
    const target=e.target&&e.target.closest&&e.target.closest('[data-cgm-help]');
    if(!target)return;
    e.preventDefault();e.stopPropagation();
    show(target.getAttribute('data-cgm-help'));
  },true);
  function bindHelpButtons(){document.querySelectorAll('[data-cgm-help]').forEach(connectHelpButton);}

  function attachHelp(el,key){if(!el||el.querySelector(':scope > [data-cgm-help]'))return;
    const btn=document.createElement('button');btn.type='button';btn.className='cgm-help-icon';btn.setAttribute('data-cgm-help',key);btn.setAttribute('aria-label',HELP[key][0]+' 도움말');btn.textContent='?';connectHelpButton(btn);
    if(el.classList && el.classList.contains('sheet-hd')) { const title=el.querySelector('.sheet-title');el.insertBefore(btn,title?title.nextSibling:el.firstChild); } else el.appendChild(btn);
  }
  function setup(){
    bindHelpButtons();
    // V1191: 성지·성당·피정의 도움말은 모두 같은 sheet-hd 제목 줄에 배치한다.
    // 화면 제목이 비동기로 바뀌어도 설명을 현재 카테고리에 맞게 동기화한다.
    const sheetTitles=[['nearby-sheet-title','nearby'],['list-sheet-title','find'],['region-sheet-title','region']];
    // V1192: 화면 제목 글자 대신 실제 선택된 성지/성당/피정 모드를 사용한다.
    function currentCategoryKey(){
      const root=document.documentElement;
      if(root.classList.contains('retreat-mode'))return 'retreat';
      if(root.classList.contains('parish-mode'))return 'parish';
      return 'shrine';
    }
    function syncSheetHelp(){
      const category=currentCategoryKey();
      sheetTitles.forEach(function(row){
        const title=document.getElementById(row[0]);if(!title)return;
        const header=title.closest('.sheet-hd');if(!header)return;
        attachHelp(header,row[1]);
        const help=header.querySelector(':scope > [data-cgm-help]');if(!help)return;
        // 패널 목적은 유지하고 카테고리별 사용 설명만 현재 모드에 맞게 보완한다.
        const key=category+'_'+row[1];
        const resolved=HELP[key]?key:row[1];
        if(help.dataset.cgmHelp!==resolved){help.dataset.cgmHelp=resolved;help.setAttribute('aria-label',HELP[resolved][0]+' 도움말');}
      });
    }
    syncSheetHelp();
    // 모드가 변하는 순간 도움말도 전환한다. 본문 변경 감시는 필요하지 않다.
    const sheetObserver=new MutationObserver(syncSheetHelp);
    sheetObserver.observe(document.documentElement,{attributes:true,attributeFilter:['class']});
    // 동적으로 생성되는 방문 스탬프북은 열린 시점에 버튼을 붙임 (MutationObserver: 화면 안의 제목만 검사).
    const observer=new MutationObserver(function(){
      bindHelpButtons();
      document.querySelectorAll('.shrine-visit-cards-head .module-bar-title').forEach(function(t){
        const key=/성당/.test(t.textContent)?'parish_book':'shrine_book';attachHelp(t,key);
        const sub=t.parentNode.querySelector('.module-bar-sub');if(sub)sub.style.display='none';
      });
    });
    observer.observe(document.body,{childList:true,subtree:true});
  }
  if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',setup,{once:true});else setup();
})();

