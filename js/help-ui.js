/* V1186: 기능 도움말 표시 전용 - GPS/순례 저장 기능에는 관여하지 않음 */
(function(){
  'use strict';
  const HELP = {
    settings:['설정','내 교구·본당, GPS 방문 기록, 자주 가는 장소와 백업을 관리합니다.\n각 박스 오른쪽 ?에서 세부 설정과 주의사항을 확인하세요.'],
    settings_diocese:['교구·본당','내 교구와 본당을 선택합니다.\n본당 설정은 GPS 자동 방문과 내 본당 제외 기능의 기준이 됩니다.\n방문 스탬프를 관리하려면 아래 방문 기록 설정도 확인하세요.'],
    settings_visits:['방문 기록','위치 권한을 허용하면 방문한 성지·성당을 GPS로 확인하고 스탬프에 기록합니다.\n내 본당 자동 기록 제한을 설정할 수 있습니다.\n설정을 바꾸더라도 이미 찍힌 스탬프는 지워지지 않습니다.'],
    settings_frequent:['자주 가는 장소','집·본당·직장 등 자주 가는 장소를 최대 5곳 저장합니다.\n닉네임으로 구분하고 길찾기의 출발·경유·도착을 빠르게 선택할 수 있습니다.\n장소를 바꾸거나 삭제해도 기존 순례 방문기록은 바뀌지 않습니다.'],
    settings_backup:['내 기록 보관','GPS 스탬프, 순례코스, 사진·메모 등 내 기록을 백업하거나 복원합니다.\n복원하면 기기에 저장된 현재 기록이 교체될 수 있습니다.\n복원 전 반드시 최신 전체 백업을 따로 보관하세요.'],
    diocese:['관구·교구 지도','한국 천주교의 관구와 교구 정보를 지도에서 살펴볼 수 있습니다.'],
    prayer:['주요 기도문','가톨릭 주요 기도문을 찾아 읽을 수 있습니다.\n굿뉴스 기도문 바로가기도 이용할 수 있습니다.'],
    pilgrimage:['순례코스','[순례계획] 장소 검색에서 성지·성당을 선택하거나 지도에서 성지 마커를 추가하고, 핸들로 방문 순서를 변경합니다. 출발·도착을 지정해 저장할 수 있습니다.\n[순례하기] 저장한 코스에서 순례하기를 누르면 GPS 방문 확인이 시작됩니다. 진행 중에는 다음 순례지와 거리 확인, 길찾기 등을 이용합니다.\n[순례완료] 실제 GPS 방문 기록에 따라 완료한 코스를 날짜별로 확인합니다. 순례완료의 연도·월 필터와 정렬을 사용할 수 있습니다.\n[자동 코스] 일반 성당 한 곳만 방문한 날은 자동 순례코스가 생성되지 않습니다. 성지 1곳 이상 또는 일반 성당 2곳 이상 방문한 날이 대상입니다. 완료된 날짜는 중복 등록하지 않습니다.\n[GPS 기록 보호] GPS로 완료한 코스는 전체 삭제와 원본 방문 장소 삭제·순서 변경이 불가합니다. GPS 원본 스탬프와 날짜는 유지됩니다.\n[누락 장소 보완] 순례완료 코스에서 ＋ 순례 장소 추가로 성지·성당을 검색하거나 지도에서 추가로 성지 마커를 선택해 수동 보완할 수 있습니다. 수동 장소에만 수동 표시와 삭제 ×, 순서 이동 핸들이 나타납니다.\n[순례코스보기] 저장된 방문 순서를 지도로 확인합니다. GPS 완료 코스는 읽기 전용이며 수동 추가 장소도 순서에 포함됩니다. 길찾기 지도는 경유지 최대 10곳 제한이 있습니다.\n[미등록 GPS 코스 확인] 실제 등록 가능한 과거 방문 날짜가 있을 때에만 순례완료에 표시됩니다. 이미 등록된 날짜나 당일 방문은 대상이 아닙니다.'],
    websites:['가톨릭 주요 웹사이트','교구와 가톨릭 기관 홈페이지를 분류별로 찾아볼 수 있습니다.'],
    trails:['전국 순례길','순례길을 지도나 목록에서 살펴보고 경로를 확인할 수 있습니다.'],
    feedback:['문의·건의','앱의 오류, 자료 수정, 기능 개선 의견을 남길 수 있습니다.'],
    shrine:['성지','성지 이름·지역으로 검색하거나 내 주변 성지를 찾아볼 수 있습니다.\n장소를 선택하면 위치, 순례 정보와 길찾기를 확인합니다.\n현장 GPS 방문이 확인되면 스탬프북에 기록됩니다.'],
    parish:['성당','성당을 이름·지역으로 검색하고 위치와 정보를 확인합니다.\nGPS 방문은 성당 스탬프북에 기록됩니다.\n일반 성당 1곳만 방문한 날은 자동 순례코스가 생성되지 않습니다. 성지 겸 성당은 성지로 우선 계산합니다.'],
    retreat:['피정','피정 시설을 이름·지역 또는 내 주변에서 찾아봅니다.\n시설 정보를 열어 위치와 연락처, 길찾기를 확인하세요.\n프로그램 일정과 예약 가능 여부는 시설에 직접 확인해야 합니다.'],
    nearby:['내 주변','GPS로 현재 위치를 확인해 가까운 장소를 보여줍니다.\n성지·성당·피정 분류를 바꾸고 장소를 선택해 상세 정보나 길찾기를 열 수 있습니다.\n위치 권한을 허용해야 정확하게 검색할 수 있습니다.'],
    region:['지역검색','지역명 또는 주소로 장소를 찾습니다.\n검색 대상과 지역을 확인하고 결과를 선택해 상세 정보나 길찾기를 열 수 있습니다.'],
    find:['찾기 목록','장소 이름이나 주소로 성지·성당·피정 시설을 찾습니다.\n검색 결과에서 원하는 장소를 선택하면 위치·상세 정보·길찾기를 이용할 수 있습니다.'],

    shrine_nearby:['성지 · 내 주변','현재 위치에서 가까운 성지를 보여줍니다.\n목록이나 지도에서 장소를 선택해 상세 정보와 길찾기를 확인하세요.\nGPS 위치 권한이 꺼져 있으면 주변 검색이 부정확할 수 있습니다.'],
    parish_nearby:['성당 · 내 주변','GPS로 가까운 성당을 찾아 목록이나 지도에 표시합니다.\n성당을 선택하면 상세 정보와 길찾기를 볼 수 있습니다.\n일반 성당 한 곳만 방문한 날에는 자동 순례코스가 생성되지 않습니다.'],
    retreat_nearby:['피정 · 내 주변','내 위치에서 가까운 피정 시설을 찾습니다.\n시설을 선택하면 위치와 길찾기를 확인할 수 있습니다.\n실제 프로그램 운영과 예약은 시설에 확인하세요.'],
    shrine_find:['성지 · 목록','성지 이름이나 주소를 입력해 검색합니다.\n결과를 누르면 성지 정보와 지도, 길찾기를 확인할 수 있습니다.\nGPS 방문 스탬프는 현장 방문이 확인됐을 때 기록됩니다.'],
    parish_find:['성당 · 목록','성당 이름이나 주소를 입력해 검색합니다.\n원하는 성당을 선택해 위치와 상세 정보, 길찾기를 확인하세요.'],
    retreat_find:['피정 · 목록','피정의 집 이름이나 지역을 입력해 검색합니다.\n시설 위치와 정보를 확인한 뒤 일정·예약 여부를 직접 확인하세요.'],
    shrine_region:['성지 · 지역검색','지역을 입력하거나 선택해 해당 지역의 성지를 찾습니다.\n검색 결과의 장소를 누르면 정보·지도·길찾기를 볼 수 있습니다.'],
    parish_region:['성당 · 지역검색','지역을 입력하거나 선택해 해당 지역의 성당을 찾습니다.\n검색 결과에서 성당 위치와 길찾기를 확인하세요.'],
    retreat_region:['피정 · 지역검색','지역을 입력하거나 선택해 피정 시설을 찾습니다.\n시설의 위치를 확인하고 예약 안내는 시설에 문의하세요.'],
    shrine_book:['순례 스탬프북','GPS로 방문 확인된 성지를 스탬프와 방문 날짜별로 확인합니다.\n성지 상세에서 방문 횟수와 날짜를 확인할 수 있습니다.\n스탬프 기록은 순례완료 코스와 별도로 보관됩니다.'],
    parish_book:['성당 방문 스탬프북','GPS로 방문 확인된 성당을 스탬프와 방문 날짜별로 확인합니다.\n내 본당 자동 기록 제한은 설정에서 관리합니다.\n일반 성당 한 곳만 방문했어도 스탬프는 남지만 자동 코스는 생성되지 않습니다.'],
    shrine_detail:['순례 기록','이 성지의 GPS 순례 날짜와 횟수를 확인합니다.\n사진·메모 관리는 설정의 개발자 도구에서만 선택적으로 표시합니다.'],
    parish_detail:['성당 방문기록','GPS 방문 날짜와 횟수를 확인합니다.\n일반 성당 한 곳만 방문한 날은 자동 순례코스를 생성하지 않습니다.'],
    directions:['길찾기','출발지와 도착지를 지정하고 필요하면 경유지를 추가합니다.\n자주 가는 장소를 빠르게 선택하거나 지도에서 장소를 고를 수 있습니다.\n경로 검색 후 거리·예상 경로를 확인하고 내비게이션으로 이동할 수 있습니다.'],
    stamps:['방문 스탬프','GPS로 확인한 방문기록을 날짜별로 보관합니다.\n사진과 메모 등 개인 기록은 별도로 유지됩니다.']
  };
  let lastFocused=null;
  let helpHistoryPending=false;
  // 도움말이 열리면 그 창만을 위한 기록 한 칸을 둔다.
  // 뒤로가기는 아래 캡처 리스너가 우선 소비해 앱 화면 전환과 충돌하지 않는다.
  window.addEventListener('popstate',function(event){
    if(helpHistoryPending){
      helpHistoryPending=false;
      if(window.CGMHelp && window.CGMHelp.isOpen())hide(false);
      event.stopImmediatePropagation();
    }
  },true);
  document.addEventListener('backbutton',function(event){
    if(window.CGMHelp&&window.CGMHelp.isOpen()){
      event.preventDefault();event.stopImmediatePropagation();hide();
    }
  },true);
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
    if(!helpHistoryPending){
      try{history.pushState({cgm_help_overlay:true},'',location.href);helpHistoryPending=true;}catch(_e){}
    }
  }
  function hide(consumeHistory=true){
    const layer=document.getElementById('cgm-help-overlay');
    if(layer){layer.hidden=true;layer.style.display='none';}
    if(lastFocused&&lastFocused.focus)lastFocused.focus();
    if(consumeHistory&&helpHistoryPending){
      try{history.back();}catch(_e){helpHistoryPending=false;}
    }
  }
  window.CGMHelp = {isOpen:function(){const x=document.getElementById('cgm-help-overlay');return !!(x&&!x.hidden);},close:function(){if(this.isOpen()){hide();return true;}return false;}};
  // 공통 캡처 이벤트 한 곳에서 처리하여 이중 호출/중복 바인딩을 방지한다.
  document.addEventListener('click',function(e){
    const target=e.target&&e.target.closest&&e.target.closest('[data-cgm-help]');
    if(!target)return;
    e.preventDefault();e.stopPropagation();
    show(target.getAttribute('data-cgm-help'));
  },true);

  function setup(){
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
    // 스탬프북과 세 카테고리 시트는 HTML/화면 생성 시 도움말 버튼을 직접 만듭니다.
    // 중복 삽입/DOM 전체 변경 감시는 사용하지 않습니다.

  }
  if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',setup,{once:true});else setup();
})();

