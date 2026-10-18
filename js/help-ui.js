/* V1186: 기능 도움말 표시 전용 - GPS/순례 저장 기능에는 관여하지 않음 */
(function(){
  'use strict';
  const HELP = {
    settings:['설정','내 교구·본당, 방문 기록, 자주 가는 장소, Google Drive 자동보관과 사진 전송 네트워크를 관리합니다.\n각 항목 옆 ?에서 기능과 주의사항을 확인할 수 있습니다.'],
    settings_diocese:['교구·본당','교구는 처음 사용할 때 설정하는 기본 정보입니다. 본당은 선택 사항으로, 설정하지 않아도 앱을 이용할 수 있습니다.\n내 본당은 방문 기록과 자동 순례코스 처리에서 별도 기준으로 사용됩니다.'],
    settings_visits:['방문 기록','GPS 위치 권한을 허용하면 성지·성당 방문을 확인하고 스탬프에 기록할 수 있습니다.\n내 본당 자동 기록 제한을 설정할 수 있으며, 설정을 변경해도 이미 저장된 방문기록은 유지됩니다.\nGPS가 확인되지 않는 경우 방문 화면에서 직접 등록할 수 있습니다.'],
    settings_frequent:['자주 가는 장소','집·본당·직장 등 자주 가는 장소를 최대 5곳 저장합니다.\n닉네임으로 구분하고 길찾기의 출발·경유·도착을 빠르게 선택할 수 있습니다.\n장소를 바꾸거나 삭제해도 기존 순례 방문기록은 바뀌지 않습니다.'],
    settings_backup:['내 기록 보관','[기록 자동보관]\nGoogle 계정 연결과 백업 확인이 완료된 경우 방문기록·스탬프·순례계획 등을 Google Drive에 자동보관할 수 있습니다. 미연결 상태나 백업 확인 실패 시에는 자동 업로드하지 않습니다.\n\n[기존 기록 가져오기]\nGoogle Drive에 기존 기록이 있으면 확인 후 가져올 수 있습니다. 가져오기는 휴대폰의 기록에 영향을 줄 수 있으므로 백업 계정과 내용을 확인하세요.\n\n[개인 사진]\n성지·성당에 등록한 사진은 앱에 저장되고, 선택한 Wi-Fi/모바일 데이터 설정에 따라 Google Drive 사진 전송을 진행합니다. 썸네일과 큰 사진은 따로 내려받을 수 있어 복원에 시간이 걸릴 수 있습니다.\n연결이나 다운로드가 실패해도 기존 기록·사진을 임의로 삭제하지 않습니다.'],
    visit_photo:['내가 올린 사진은 어떻게 보관되나요?','[사진 등록]\n방문 날짜별로 성지·성당 사진을 최대 15장까지 등록하고, 메모를 작성할 수 있습니다. 사진은 앱에 먼저 저장됩니다.\n\n[Google Drive 사진 보관]\nGoogle Drive 연결 및 사진 전송 네트워크 설정에 따라 개인 사진을 백업합니다. Wi-Fi 전용을 선택하면 모바일 데이터에서는 사진 전송을 기다립니다. 방문기록 자동보관과 사진 전송은 서로 다른 작업입니다.\n\n[썸네일·원본 복원]\n앱을 다시 설치해 Google Drive 기록을 가져오면 작은 썸네일을 우선 복원하고, 큰 사진은 별도로 내려받습니다. 사진이 아직 보이지 않으면 다운로드 진행이나 네트워크 상태를 확인하세요.\n\n[주의]\n업로드·복원 완료를 확인하기 전에는 앱 데이터 삭제나 초기화를 하지 마세요.'],
    diocese:['관구·교구 지도','한국 천주교의 관구와 교구 정보를 지도에서 살펴볼 수 있습니다.'],
    prayer:['주요 기도문','가톨릭 주요 기도문을 찾아 읽거나 검색할 수 있습니다.\n\n[즐겨찾기] 기도문 목록 또는 상세 화면의 별표(☆)를 누르면 저장됩니다. 저장한 기도문은 「⭐ 즐겨찾기」에서 모아 볼 수 있습니다. 별표를 다시 누르면 즐겨찾기에서 해제됩니다.\n\n굿뉴스 기도문 바로가기도 이용할 수 있습니다.'],
    pilgrimage:['순례코스','상단에서 순례계획과 순례완료를 전환합니다.\n계획을 세우고 순례하기로 GPS 방문을 확인하거나, 완료된 코스를 날짜별로 확인할 수 있습니다. 현재 열려 있는 탭의 ?를 누르면 자세한 도움말을 볼 수 있습니다.'],
    pilgrimage_plan:['순례계획','[계획 세우기]\n출발지·순례할 성지와 성당·도착지를 지정하고, 필요하면 순서를 바꿔 저장합니다. 성지를 하나 이상 포함하는 코스로 계획하세요.\n\n[순례하기]\n저장한 코스에서 순례하기를 시작하면 실제 GPS 방문 장소를 확인하고 다음 순례지 길찾기를 사용할 수 있습니다. 방문 순서는 계획과 달라도 실제 GPS 방문 순서로 기록될 수 있습니다.\n\n[완료]\n계획의 장소를 모두 방문하면 완료 기록으로 관리됩니다. 순례종료 버튼을 누르는 것과 GPS 방문 완료는 다릅니다.'],
    pilgrimage_complete:['순례완료','[완료 기록]\n순례완료 탭에서 날짜별 코스를 확인하고 연도·월 필터 및 정렬을 사용할 수 있습니다.\n\n[GPS 기록 보호]\nGPS로 확인한 방문 날짜와 장소는 임의로 수정·삭제하지 않습니다. 별도로 수동 등록한 장소는 해당 화면에서 허용하는 범위 안에서 관리합니다.\n\n[지난 순례 등록]\n누락된 지난 순례는 제공되는 수동 등록 기능으로 방문 날짜와 장소를 입력해 기록할 수 있습니다. 실제 GPS 인증 기록과 수동 등록 기록은 구분됩니다.'],
    pilgrimage_detail_plan:['순례계획 상세','저장한 순례계획에서 출발지·순례 장소·도착지와 순서를 확인하고 수정할 수 있습니다.\n순례하기를 시작하면 GPS로 방문한 장소를 기록하며, 진행 중에는 다음 순례지 길찾기를 이용합니다.\n순례종료는 방문 완료와 다릅니다.'],
    pilgrimage_detail_complete:['순례완료 코스 상세','완료된 순례코스의 날짜와 실제 방문 장소를 확인합니다.\nGPS 인증 방문기록의 날짜·장소는 수정하거나 삭제할 수 없습니다. 수동 보완 장소는 화면에서 허용된 범위에서 관리하며, 순례코스보기에서 경로를 확인합니다.'],
    websites:['가톨릭 주요 웹사이트','교구와 가톨릭 기관 홈페이지를 분류별로 찾아볼 수 있습니다.\n\n[즐겨찾기] 웹사이트 카드의 별표(★)를 누르면 즐겨찾기에 저장됩니다. 「⭐ 즐겨찾기」 분류에서 모아 볼 수 있으며, 별표를 다시 누르면 해제됩니다.'],
    trails:['전국 순례길','순례길을 지도나 목록에서 살펴보고 경로를 확인할 수 있습니다.'],
    feedback:['문의·건의','앱의 오류, 자료 수정, 기능 개선 의견을 남길 수 있습니다.'],
    shrine:['성지','성지 이름·지역으로 검색하거나 내 주변 성지를 찾아볼 수 있습니다.\n장소를 선택하면 위치, 순례 정보와 길찾기를 확인합니다.\n현장 GPS 방문이 확인되면 스탬프북에 기록됩니다.'],
    parish:['성당','성당을 이름·지역으로 검색하고 위치와 정보를 확인합니다.\nGPS 방문은 성당 스탬프북에 기록됩니다.'],
    retreat:['피정','피정 시설을 이름·지역 또는 내 주변에서 찾아봅니다.\n시설 정보를 열어 위치와 연락처, 길찾기를 확인하세요.\n프로그램 일정과 예약 가능 여부는 시설에 직접 확인해야 합니다.'],
    nearby:['내 주변','GPS로 현재 위치를 확인해 가까운 성지·성당·피정 시설을 찾습니다.\n분류를 선택하고 장소 카드에서 상세 정보와 길찾기를 이용할 수 있습니다. 위치 권한을 거부했다면 위치 기능 사용 시 앱에서 다시 권한을 요청할 수 있습니다.'],
    region:['지역검색','지역명 또는 주소로 장소를 찾습니다.\n검색 대상과 지역을 확인하고 결과를 선택해 상세 정보나 길찾기를 열 수 있습니다.'],
    find:['찾기 목록','장소 이름이나 주소로 성지·성당·피정 시설을 찾습니다.\n검색 결과에서 원하는 장소를 선택하면 위치·상세 정보·길찾기를 이용할 수 있습니다.'],

    shrine_nearby:['성지 · 내 주변','현재 위치에서 가까운 성지를 보여줍니다.\n목록이나 지도에서 장소를 선택해 상세 정보와 길찾기를 확인하세요.\nGPS 위치 권한이 꺼져 있으면 주변 검색이 부정확할 수 있습니다.'],
    parish_nearby:['성당 · 내 주변','GPS로 가까운 성당을 찾아 목록이나 지도에 표시합니다.\n성당을 선택하면 상세 정보와 길찾기를 볼 수 있습니다.'],
    retreat_nearby:['피정 · 내 주변','내 위치에서 가까운 피정 시설을 찾습니다.\n시설을 선택하면 위치와 길찾기를 확인할 수 있습니다.\n실제 프로그램 운영과 예약은 시설에 확인하세요.'],
    shrine_find:['성지 · 목록','성지 이름이나 주소를 입력해 검색합니다.\n결과를 누르면 성지 정보와 지도, 길찾기를 확인할 수 있습니다.\nGPS 방문 스탬프는 현장 방문이 확인됐을 때 기록됩니다.'],
    parish_find:['성당 · 목록','성당 이름이나 주소를 입력해 검색합니다.\n원하는 성당을 선택해 위치와 상세 정보, 길찾기를 확인하세요.'],
    retreat_find:['피정 · 목록','피정의 집 이름이나 지역을 입력해 검색합니다.\n시설 위치와 정보를 확인한 뒤 일정·예약 여부를 직접 확인하세요.'],
    shrine_region:['성지 · 지역검색','지역을 입력하거나 선택해 해당 지역의 성지를 찾습니다.\n검색 결과의 장소를 누르면 정보·지도·길찾기를 볼 수 있습니다.'],
    parish_region:['성당 · 지역검색','지역을 입력하거나 선택해 해당 지역의 성당을 찾습니다.\n검색 결과에서 성당 위치와 길찾기를 확인하세요.'],
    retreat_region:['피정 · 지역검색','지역을 입력하거나 선택해 피정 시설을 찾습니다.\n시설의 위치를 확인하고 예약 안내는 시설에 문의하세요.'],
    shrine_book:['순례 스탬프북','방문한 성지, 미방문 성지, 신규 성지와 통계를 확인합니다.\n성지를 선택하면 방문 날짜·횟수와 사진·메모를 볼 수 있습니다. GPS로 등록한 방문과 직접 등록한 기록은 구분됩니다.'],
    parish_book:['성당 방문 스탬프북','방문한 성당, 미방문 성당과 통계를 확인합니다.\n성당을 선택하면 방문 날짜·횟수와 사진·메모를 볼 수 있습니다. 성당만 방문했다고 자동 순례코스가 항상 만들어지는 것은 아닙니다.'],
    shrine_detail:['순례 기록','이 성지의 순례 횟수와 방문 날짜를 확인할 수 있습니다.\n「나의 기록」에서 방문 날짜별 사진을 추가하거나 메모를 작성·수정할 수 있습니다. 사진은 Google Drive 연결 상태에 따라 백업·복원됩니다.'],
    parish_detail:['성당 방문기록','이 성당의 방문 횟수와 날짜를 확인할 수 있습니다.\n「나의 기록」에서 방문 날짜별 사진을 추가하거나 메모를 작성·수정할 수 있습니다.'],
    directions:['길찾기','출발지와 도착지를 지정하고 필요하면 경유지를 추가합니다.\n\n[즐겨찾기·자주 가는 장소] 지정한 장소 옆 빈 별표(☆)를 눌러 자주 가는 장소에 저장할 수 있습니다. 최대 5곳까지 저장되며, 빠른 선택에서 다시 사용할 수 있습니다. 채워진 별표(★)를 누르면 저장이 해제됩니다.\n\n경로 검색 후 거리·예상 경로를 확인하고 내비게이션으로 이동할 수 있습니다.'],
    stamps:['방문 스탬프','성지·성당 방문 스탬프를 날짜별로 확인할 수 있습니다.\n장소를 열면 사진·메모를 남길 수 있으며, 사진은 작은 썸네일을 먼저 표시합니다. 다운로드 중에는 일부 사진 표시가 늦어질 수 있습니다.']
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
    if(key==='pilgrimage'){
      const detail=document.getElementById('oai-pilgrimage-detail-view');
      const inDetail=detail&&!detail.hidden&&getComputedStyle(detail).display!=='none';
      const list=document.getElementById('oai-pilgrimage-list-view');
      const tab=(list&&list.getAttribute('data-course-tab'))||'complete';
      key=inDetail?(tab==='plan'?'pilgrimage_detail_plan':'pilgrimage_detail_complete'):(tab==='plan'?'pilgrimage_plan':'pilgrimage_complete');
    }
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
    layer.querySelector('#cgm-help-text').textContent=data[1].replace(/\n(?=\[)/g,'\n\n');
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

