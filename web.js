
(function(){
  const WEB_SITES = [

  {cat:"사제찾기", ico:"✝", name:"서울대교구 사제찾기",
   op:"서울대교구", prov:"서울관구", url:"https://aos.catholic.or.kr/pro10315",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"의정부교구 사제찾기",
   op:"의정부교구", prov:"서울관구", url:"http://ucatholic.or.kr/bbs/board.php?bo_table=priest",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"인천교구 사제찾기",
   op:"인천교구", prov:"서울관구", url:"http://www.caincheon.or.kr/father/father_list.do",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"수원교구 사제찾기",
   op:"수원교구", prov:"서울관구", url:"https://www.casuwon.or.kr/priest/priest",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"춘천교구 사제찾기",
   op:"춘천교구", prov:"서울관구", url:"https://www.cccatholic.or.kr/diocese/priest/priest",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"원주교구 사제찾기",
   op:"원주교구", prov:"서울관구", url:"http://www.wjcatholic.or.kr/company/sajedan",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"대전교구 사제찾기",
   op:"대전교구", prov:"서울관구", url:"https://www.djcatholic.or.kr/home/pages/priest_list.php",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"대구대교구 사제찾기",
   op:"대구대교구", prov:"대구관구", url:"https://www.daegu-archdiocese.or.kr/page/priest.html?srl=priest",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"청주교구 사제찾기",
   op:"청주교구", prov:"대구관구", url:"https://www.cdcj.or.kr/diocese/priest/priest",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"안동교구 사제찾기",
   op:"안동교구", prov:"대구관구", url:"https://www.acatholic.or.kr/sub2/sub1.asp",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"마산교구 사제찾기",
   op:"마산교구", prov:"대구관구", url:"https://cathms.kr/saje",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"부산교구 사제찾기",
   op:"부산교구", prov:"대구관구", url:"https://www.catholicbusan.or.kr/clergy/priest",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"광주대교구 사제찾기",
   op:"광주대교구", prov:"광주관구", url:"https://www.gjcatholic.or.kr/priest/priests",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"전주교구 사제찾기",
   op:"전주교구", prov:"광주관구", url:"https://www.jcatholic.or.kr/theme/main/pages/priest.php?st=diocese",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"제주교구 사제찾기",
   op:"제주교구", prov:"광주관구", url:"https://www.diocesejeju.or.kr/diocese_father",
   desc:"교구 사제 검색 바로가기"},
  {cat:"사제찾기", ico:"✝", name:"군종교구 사제찾기",
   op:"군종교구", prov:"군종교구", url:"https://www.gunjong.or.kr/organization/index.asp?SearchArmyCd=",
   desc:"교구 사제 검색 바로가기"},

  {cat:"중앙기구", ico:"🏛️", name:"한국천주교주교회의 (CBCK)",
   op:"한국천주교주교회의", url:"https://cbck.or.kr",
   desc:"한국 천주교 공식 문헌·교리·전례·교회법·발표 자료 제공"},
  {cat:"중앙기구", ico:"⛪", name:"시복시성 주교특별위원회",
   op:"한국천주교주교회의", url:"https://cbck.or.kr/koreanmartyrs",
   desc:"한국 순교자·성지·교회사 관련 자료 제공"},

  {cat:"신앙 포털", ico:"✝️", name:"서울대교구 굿뉴스",
   op:"천주교 서울대교구", url:"https://www.catholic.or.kr",
   desc:"매일미사·성경·성인·기도문·전례력·신앙자료 제공"},
  {cat:"신앙 포털", ico:"📒", name:"가톨릭 주소록",
   op:"천주교 서울대교구", url:"https://m.catholic.or.kr/web/addr/",
   desc:"교구·본당·기관 연락처와 주소를 모바일에서 빠르게 확인"},
  {cat:"신앙 포털", ico:"📘", name:"가톨릭 사전",
   op:"서울대교구 굿뉴스", url:"https://maria.catholic.or.kr/mobile/dictionary/dictionary.asp",
   desc:"가톨릭 교리·전례·성경 용어를 모바일 사전으로 조회"},
  {cat:"신앙 포털", ico:"🌟", name:"성인/축일",
   op:"서울대교구 굿뉴스", url:"https://maria.catholic.or.kr/mobile/sa_ho/list/list.asp?menugubun=saint&today=on",
   desc:"오늘의 성인과 가톨릭 성인 정보를 확인"},
  {cat:"신앙 포털", ico:"📖", name:"성경",
   op:"서울대교구 굿뉴스", url:"https://maria.catholic.or.kr/mobile/bible/read/bible_list.asp",
   desc:"가톨릭 성경과 신앙 자료를 모바일에서 확인"},
  {cat:"신앙 포털", ico:"🎼", name:"가톨릭 성가",
   op:"서울대교구 굿뉴스", url:"https://maria.catholic.or.kr/mobile/sungga/",
   desc:"가톨릭 성가 검색과 악보 자료 제공"},
  {cat:"신앙 포털", ico:"🎵", name:"가톨릭 생활성가",
   op:"서울대교구 굿뉴스", url:"https://maria.catholic.or.kr/mobile/ccm/main.asp",
   desc:"생활성가와 CCM 자료 제공"},

  {cat:"미디어", ico:"📺", name:"cpbc 가톨릭평화방송",
   op:"가톨릭평화방송", url:"https://www.cpbc.co.kr",
   desc:"매일미사·강론·라디오·영상·신앙 프로그램 제공"},
  {cat:"미디어", ico:"▶️", name:"cpbc 유튜브 채널",
   op:"가톨릭평화방송", url:"https://www.youtube.com/@cpbc",
   desc:"미사·강론·방송 프로그램 영상 제공"},
  {cat:"미디어", ico:"📹", name:"가톨릭신문 유튜브 채널",
   op:"가톨릭신문사", url:"https://www.youtube.com/@KoreaCatholictimes",
   desc:"가톨릭 뉴스·인터뷰·영상 콘텐츠 제공"},
  {cat:"뉴스", ico:"🌍", name:"바티칸 뉴스 (한국어)",
   op:"바티칸 공식 미디어", url:"https://www.vaticannews.va/ko.html",
   desc:"교황청 공식 뉴스·교회 전 세계 소식 한국어 서비스"},

  {cat:"뉴스", ico:"📰", name:"가톨릭신문",
   op:"가톨릭신문사", url:"https://www.catholictimes.org",
   desc:"교회 뉴스·사목·교구 소식·인터뷰·사회 이슈 기사 제공"},
  {cat:"뉴스", ico:"📄", name:"가톨릭평화신문",
   op:"cpbc", url:"https://news.cpbc.co.kr",
   desc:"가톨릭 뉴스·인물 기사·사목 기사 제공"},

  {cat:"출판·교육", ico:"📚", name:"가톨릭출판사",
   op:"가톨릭출판사", url:"https://www.catholicbook.kr",
   desc:"교리·영성·교육용 도서 및 신앙서적 제공"},
  {cat:"출판·교육", ico:"🕊️", name:"성바오로딸",
   op:"성바오로딸수도회", url:"https://www.pauline.or.kr",
   desc:"묵상·영성·생활 신앙 콘텐츠 제공"},

  {cat:"교구", ico:"⛪", prov:"서울관구", name:"서울대교구",
   op:"천주교 서울대교구", url:"https://aos.catholic.or.kr",
   desc:"교구 공지·교육·사목 자료·기관 정보 제공"},
  {cat:"교구", ico:"⛪", prov:"서울관구", name:"인천교구",
   op:"천주교 인천교구", url:"http://www.caincheon.or.kr/",
   desc:"교구 공지·사목 자료·기관 정보 제공"},
  {cat:"교구", ico:"⛪", prov:"서울관구", name:"수원교구",
   op:"천주교 수원교구", url:"https://www.casuwon.or.kr/",
   desc:"교구 소식·교육·행사·사목 자료 제공"},
  {cat:"교구", ico:"⛪", prov:"서울관구", name:"의정부교구",
   op:"천주교 의정부교구", url:"http://www.ucatholic.or.kr/",
   desc:"교구 공지·본당 안내·사목 자료 제공"},
  {cat:"교구", ico:"⛪", prov:"서울관구", name:"춘천교구",
   op:"천주교 춘천교구", url:"https://www.cccatholic.or.kr/",
   desc:"교구 공지·기관 안내·행사 정보 제공"},
  {cat:"교구", ico:"⛪", prov:"서울관구", name:"원주교구",
   op:"천주교 원주교구", url:"http://www.wjcatholic.or.kr/",
   desc:"교구 소식·교육 자료·공지 제공"},
  {cat:"교구", ico:"⛪", prov:"서울관구", name:"대전교구",
   op:"천주교 대전교구", url:"https://www.djcatholic.or.kr/home/",
   desc:"교구 행사·교육·공지·사목 자료 제공"},
  {cat:"교구", ico:"⛪", prov:"대구관구", name:"대구대교구",
   op:"천주교 대구대교구", url:"https://www.daegu-archdiocese.or.kr/",
   desc:"교구 소식·본당·기관 정보·행사 안내 제공"},
  {cat:"교구", ico:"⛪", prov:"대구관구", name:"청주교구",
   op:"천주교 청주교구", url:"https://www.cdcj.or.kr/",
   desc:"교구 공지·교육·사목 자료 제공"},
  {cat:"교구", ico:"⛪", prov:"대구관구", name:"안동교구",
   op:"천주교 안동교구", url:"https://www.acatholic.or.kr/",
   desc:"교구 소식·본당 안내·행사 정보 제공"},
  {cat:"교구", ico:"⛪", prov:"대구관구", name:"부산교구",
   op:"천주교 부산교구", url:"https://www.catholicbusan.or.kr/",
   desc:"교구 소식·행사·교육 자료 제공"},
  {cat:"교구", ico:"⛪", prov:"대구관구", name:"마산교구",
   op:"천주교 마산교구", url:"https://cathms.kr/",
   desc:"교구 공지·기관 정보·교육 자료 제공"},
  {cat:"교구", ico:"⛪", prov:"광주관구", name:"광주대교구",
   op:"천주교 광주대교구", url:"https://www.gjcatholic.or.kr/",
   desc:"교구 공지·사목 자료·기관 안내 제공"},
  {cat:"교구", ico:"⛪", prov:"광주관구", name:"전주교구",
   op:"천주교 전주교구", url:"https://jcatholic.or.kr/index.php",
   desc:"교구 행사·공지·사목 자료 제공"},
  {cat:"교구", ico:"⛪", prov:"광주관구", name:"제주교구",
   op:"천주교 제주교구", url:"https://www.diocesejeju.or.kr/",
   desc:"교구 소식·기관 안내·행사 정보 제공"},
];
  const TRAIL_ITEMS = [
  {n:"천주교 서울 순례길",       op:"서울대교구",            t:"d", r:"서울시",                   lat:37.5644,lng:127.0104, ico:"✝️", url:"https://martyrs.or.kr/_web/mpilgrims/about.html"},
  {n:"성지순례길 '디딤길'",      op:"수원교구",              t:"d", r:"경기 수원시",               lat:37.2832,lng:127.0170, ico:"🙏", url:"https://www.casuwon.or.kr/holyland/pilgrimage"},
  {n:"원주교구 순례길 '님의 길'",   op:"원주교구",              t:"d", r:"강원 원주·횡성, 충북 제천", lat:37.3420,lng:127.9200, ico:"🌿", url:"https://sunraegil.seoji.net/course/all"},
  {n:"한티가는길",               op:"대구대교구",            t:"d", r:"경북 칠곡 (가실성당~한티순교성지)",  lat:36.0168,lng:128.6299, ico:"⛰️", url:"https://hantigil.or.kr/index"},
  {n:"광주대교구 순례길",        op:"광주대교구",            t:"d", r:"전남 나주·영광",             lat:35.0369,lng:126.7152, ico:"🕊️", url:"https://www.gjcatholic.or.kr/holyland/pilgrimage/noan_naju"},
  {n:"천주교 제주교구 순례길",       op:"제주교구",              t:"d", r:"제주",                      lat:33.4463,lng:126.3027, ico:"🌊", url:"http://santoviaggio.com/"},
  {n:"사제와 함께하는 도보순례", op:"안동교구",              t:"d", r:"경북 문경·상주",             lat:36.8001,lng:128.2113, ico:"👣", url:"https://www.acatholic.or.kr/sub4/sub2.asp"},
  {n:"전주교구 교우촌 도보순례",   op:"전주교구",              t:"d", r:"전북 전주·완주",             lat:35.8031,lng:127.1677, ico:"🌾", url:"https://www.jcatholic.or.kr/theme/main/pages/pilgrimage01.html"},
  {n:"보령 갈매못 성지순례길",   op:"보령시",                t:"l", r:"충남 보령",                 lat:36.4280,lng:126.5075, ico:"🌅", url:"https://www.brcn.go.kr/tour/sub02_02_02.do"},
  {n:"내포 천주교 순례길",       op:"사단법인 내포문화숲길", t:"l", r:"충남 예산·서산",             lat:36.7127,lng:126.5380, ico:"🌲", url:"https://naepotrail.org/course/catholic"},
  {n:"버그내순례길",             op:"당진시청",              t:"l", r:"충남 당진",                 lat:36.8199,lng:126.7848, ico:"🏞️", url:"https://beogeunae.dangjin.go.kr/pil1.html"}
];
  const WEB_CAT_COLORS = {
    "사제찾기":"#0F766E",
    "중앙기구":"#8B1C2A",
    "신앙 포털":"#1A6B3C",
    "미디어":"#1A4F8B",
    "뉴스":"#5A3E8B",
    "출판·교육":"#7A5230",
    "교구":"#4A6A4A"
  };
  const WEB_PROV_COLORS = {
    "서울관구":"#2563EB",
    "대구관구":"#B7791F",
    "광주관구":"#7C3AED",
    "군종교구":"#64748B"
  };
  const WEB_CAT_BG = {
    "사제찾기":"#eef7f5",
    "중앙기구":"#fdf0f0",
    "신앙 포털":"#eef7f2",
    "미디어":"#eef3fd",
    "뉴스":"#f3effe",
    "출판·교육":"#f8f3ee",
    "교구":"#f0f5f0"
  };
  const TRAIL_COLORS = {d:'#1D4ED8', l:'#2A8040'};
  /* V8-1-14-471: 순례길 지도 확대/축소 체감 개선을 위해 마커 이미지를 캐시하고
     같은 이미지/지도 상태를 반복 적용하지 않는다. */
  const TRAIL_MARKER_IMG_CACHE = Object.create(null);
  function trailMarkerImageCached(key, maker){
    try{
      if(TRAIL_MARKER_IMG_CACHE[key]) return TRAIL_MARKER_IMG_CACHE[key];
      const img = maker();
      TRAIL_MARKER_IMG_CACHE[key] = img;
      return img;
    }catch(_e){ return maker(); }
  }
  function trailSetMarkerImage(marker, img, key){
    if(!marker || !img) return;
    try{
      if(key && marker.__trailImgKey === key) return;
      marker.setImage(img);
      if(key) marker.__trailImgKey = key;
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function trailSetMarkerMap(marker, map){
    if(!marker) return;
    try{
      if(marker.__trailMapTarget === map) return;
      marker.setMap(map);
      marker.__trailMapTarget = map;
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function trailMarkerImg(i, big){
    const d = TRAIL_ITEMS[i];
    const color = TRAIL_COLORS[d && d.t] || '#1D4ED8';
    const key = 'trail:'+String(i)+':' + (big?'1':'0');
    return trailMarkerImageCached(key, function(){
      return new kakao.maps.MarkerImage(trailMkSvg(color, !!big), new kakao.maps.Size(big?54:42,big?66:52), {offset:new kakao.maps.Point(big?27:21,big?66:52)});
    });
  }

  function getHantiRouteData(){
    return window.CATHOLIC_HANTI_ROUTE_DATA && window.CATHOLIC_HANTI_ROUTE_DATA.id === 'hanti'
      ? window.CATHOLIC_HANTI_ROUTE_DATA : null;
  }
  function getDowonTestRouteData(){
    return window.CATHOLIC_DOWON_TEST_ROUTE_DATA && window.CATHOLIC_DOWON_TEST_ROUTE_DATA.id === 'dowon_test_loop'
      ? window.CATHOLIC_DOWON_TEST_ROUTE_DATA : null;
  }
  function getCompanyTestRouteData(){
    return window.CATHOLIC_COMPANY_TEST_ROUTE_DATA && window.CATHOLIC_COMPANY_TEST_ROUTE_DATA.id === 'company_test_route'
      ? window.CATHOLIC_COMPANY_TEST_ROUTE_DATA : null;
  }
  function getActiveHantiRouteData(){
    return (trailState && trailState.hantiActiveRouteData) || getHantiRouteData();
  }
  function getActiveHantiRouteTitle(){
    var data = getActiveHantiRouteData();
    return data && data.name || '한티가는길';
  }
  function isHantiTrailItem(d){ return !!(d && d.n === '한티가는길'); }
  function clearHantiRouteOverlays(){
    try{
      (trailState.hantiPolylines || []).forEach(function(line){ try{ line.setMap(null); }catch(_e){} });
      (trailState.hantiProgressPolylines || []).forEach(function(line){ try{ line.setMap(null); }catch(_e){} });
      clearHantiRouteDirectionOverlays();
      (trailState.hantiStampOverlays || []).forEach(function(ov){ try{ ov.setMap(null); }catch(_e){} });
      try{ if(trailState.hantiGpsTracePolyline) trailState.hantiGpsTracePolyline.setMap(null); }catch(_e){}
      removeHantiRouteControls();
      clearHantiLocationGuideOverlay();
      stopHantiGpxFollow();
      removeHantiTestPanel();
    }catch(e){ console.warn('[가톨릭길동무]', e); }
    trailState.hantiPolylines = [];
    trailState.hantiProgressPolylines = [];
    trailState.hantiRouteDirectionOverlays = [];
    trailState.hantiStampOverlays = [];
    trailState.hantiGpsTracePolyline = null;
    trailState.hantiGpsTracePoints = [];
    trailState.hantiVisible = false;
    trailState.hantiActiveRouteData = null;
    trailState.hantiActiveRouteId = '';
    trailState.hantiRouteViewMode = false;
    trailState.hantiLastRoutePointIndex = null;
    trailState.hantiLastProgressM = 0;
    trailState.hantiArrivedStampIds = {};
  }
  function setHantiRouteActive(active){
    try{
      if(trailState) trailState.hantiRouteActive = !!active;
      if(active) localStorage.setItem('catholic_hanti_route_active_v1', '1');
      else localStorage.removeItem('catholic_hanti_route_active_v1');
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function isHantiRouteActive(){
    try{
      return !!(trailState && trailState.hantiRouteActive) || localStorage.getItem('catholic_hanti_route_active_v1') === '1';
    }catch(_e){ return !!(trailState && trailState.hantiRouteActive); }
  }
  var HANTI_ROUTE_FOLLOW_RESUME_KEY = 'catholic_hanti_route_follow_resume_v1';
  var HANTI_ROUTE_FOLLOW_ACTIVE_KEY = 'catholic_hanti_route_follow_active_v1';
  var HANTI_ROUTE_FOLLOW_RESUME_MAX_MS = Number.POSITIVE_INFINITY;

  function getHantiRouteDataById(id){
    var routeId = String(id || 'hanti');
    if(routeId === 'dowon_test_loop' || routeId === 'dowon') return getDowonTestRouteData() || getHantiRouteData();
    if(routeId === 'company_test_route' || routeId === 'company') return getCompanyTestRouteData() || getHantiRouteData();
    return getHantiRouteData();
  }
  function hantiSafeJsonParse(raw){
    if(!raw) return null;
    try{ return JSON.parse(raw); }catch(_e){ return null; }
  }
  function getSavedHantiFollowResumeState(){
    try{
      var raw = sessionStorage.getItem(HANTI_ROUTE_FOLLOW_RESUME_KEY) || localStorage.getItem(HANTI_ROUTE_FOLLOW_RESUME_KEY) || '';
      var state = hantiSafeJsonParse(raw);
      if(!state || !state.followActive) return null;
      var at = Number(state.savedAt || state.at || 0);
      if(!Number.isFinite(at) || !at){
        clearHantiFollowResumeState('invalid');
        return null;
      }
      if(Number.isFinite(HANTI_ROUTE_FOLLOW_RESUME_MAX_MS) && Date.now() - at > HANTI_ROUTE_FOLLOW_RESUME_MAX_MS){
        clearHantiFollowResumeState('expired');
        return null;
      }
      return state;
    }catch(_e){ return null; }
  }
  function clearHantiFollowResumeState(reason){
    try{ sessionStorage.removeItem(HANTI_ROUTE_FOLLOW_RESUME_KEY); }catch(_e){}
    try{ localStorage.removeItem(HANTI_ROUTE_FOLLOW_RESUME_KEY); }catch(_e){}
    try{ localStorage.removeItem(HANTI_ROUTE_FOLLOW_ACTIVE_KEY); }catch(_e){}
  }
  function buildHantiFollowResumeState(reason){
    try{
      var data = getActiveHantiRouteData() || getHantiRouteData();
      var center = null, level = null;
      if(trailState && trailState.map && window.kakao && kakao.maps){
        try{
          var c = trailState.map.getCenter && trailState.map.getCenter();
          center = c ? {lat:c.getLat(), lng:c.getLng()} : null;
          level = trailState.map.getLevel ? trailState.map.getLevel() : null;
        }catch(_e){}
      }
      return {
        savedAt: Date.now(),
        reason: reason || 'hanti-follow-resume',
        routeId: (data && data.id) || (trailState && trailState.hantiActiveRouteId) || 'hanti',
        routeActive: !!(trailState && (trailState.hantiRouteViewMode || trailState.hantiVisible || trailState.hantiRouteActive)),
        followActive: !!(trailState && trailState.hantiFollowActive),
        returnTrailIndex: Number.isFinite(Number(trailState && trailState.hantiReturnTrailIndex)) ? Number(trailState.hantiReturnTrailIndex) : getHantiMainTrailIndex(),
        selected: Number.isFinite(Number(trailState && trailState.selected)) ? Number(trailState.selected) : getHantiMainTrailIndex(),
        center: center,
        level: level,
        lastRoutePointIndex: Number.isFinite(Number(trailState && trailState.hantiLastRoutePointIndex)) ? Number(trailState.hantiLastRoutePointIndex) : null,
        lastProgressM: Number.isFinite(Number(trailState && trailState.hantiLastProgressM)) ? Number(trailState.hantiLastProgressM) : 0,
        arrivedStampIds: (trailState && trailState.hantiArrivedStampIds) || {},
        showGpsTrace: !!(trailState && trailState.hantiShowGpsTrace),
        routeReverse: !!(trailState && trailState.hantiRouteReverse),
        infoCardOpen: false
      };
    }catch(e){ console.warn('[가톨릭길동무]', e); return null; }
  }
  function saveHantiFollowResumeState(reason){
    try{
      if(!(trailState && trailState.hantiFollowActive)){
        if(reason && /manual-stop|close-route|test-close/i.test(String(reason))) clearHantiFollowResumeState(reason);
        return false;
      }
      var state = buildHantiFollowResumeState(reason || 'save');
      if(!(state && state.followActive)) return false;
      var raw = JSON.stringify(state);
      sessionStorage.setItem(HANTI_ROUTE_FOLLOW_RESUME_KEY, raw);
      localStorage.setItem(HANTI_ROUTE_FOLLOW_RESUME_KEY, raw);
      localStorage.setItem(HANTI_ROUTE_FOLLOW_ACTIVE_KEY, '1');
      return true;
    }catch(e){ console.warn('[가톨릭길동무]', e); return false; }
  }
  function hasHantiFollowResumeState(){
    try{
      if(trailState && trailState.hantiFollowActive) return true;
      if(localStorage.getItem(HANTI_ROUTE_FOLLOW_ACTIVE_KEY) === '1' && getSavedHantiFollowResumeState()) return true;
      return !!getSavedHantiFollowResumeState();
    }catch(_e){ return !!(trailState && trailState.hantiFollowActive); }
  }
  function restoreMapViewFromHantiState(state){
    try{
      if(!(state && state.center && trailState && trailState.map && window.kakao && kakao.maps)) return;
      var lat = Number(state.center.lat), lng = Number(state.center.lng);
      if(Number.isFinite(lat) && Number.isFinite(lng)) trailState.map.setCenter(new kakao.maps.LatLng(lat, lng));
      if(Number.isFinite(Number(state.level)) && trailState.map.setLevel) trailState.map.setLevel(Number(state.level));
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function openHantiMainInfoCardPreservingRoute(reason){
    try{
      var idx = Number.isFinite(Number(trailState && trailState.hantiReturnTrailIndex)) && Number(trailState.hantiReturnTrailIndex) >= 0 ? Number(trailState.hantiReturnTrailIndex) : getHantiMainTrailIndex();
      var d = TRAIL_ITEMS[idx];
      if(!d || !isHantiTrailItem(d)) return;
      trailState.selected = idx;
      trailState.hantiSecretTitleReady = true;
      var bdg = ig$('trail-sh-bdg');
      var region = ig$('trail-sh-region');
      var ico = ig$('trail-sh-ico');
      var name = ig$('trail-sh-name');
      var sub = ig$('trail-sh-sub');
      var url = ig$('trail-sh-url');
      var body = ig$('trail-sh-body');
      var foot = ig$('trail-sh-foot');
      if(bdg){ bdg.textContent = d.op; bdg.className = 'trail-sh-bdg ' + d.t; }
      if(region) region.textContent = '📍 ' + d.r;
      if(ico){ ico.textContent = d.ico; ico.className = 'trail-sh-ico ' + d.t; }
      if(name) name.textContent = d.n;
      if(sub) sub.textContent = d.op + ' · ' + d.r;
      if(url) url.textContent = '';
      if(body) body.onclick = function(ev){ if(ev){ ev.preventDefault(); ev.stopPropagation(); } };
      if(foot) foot.onclick = function(ev){ if(ev){ ev.preventDefault(); ev.stopPropagation(); } };
      ensureHantiMainSheetActions(d);
      bindHantiSecretTapTarget(name);
      if(hantiTestModeEnabled()) ensureHantiTestPanel(); else removeHantiTestPanel();
      ig$('trail-sheet')?.classList.add('open');
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function restoreHantiFollowFromBackground(reason){
    var state = getSavedHantiFollowResumeState();
    if(!state && trailState && trailState.hantiFollowActive) state = buildHantiFollowResumeState(reason || 'runtime-restore');
    if(!(state && state.followActive)) return false;
    try{
      var data = getHantiRouteDataById(state.routeId);
      if(!data) return false;
      if(!ig$('trail-view')?.classList.contains('open')) enterIntegratedView('trail-view');
      trailState.view = 'map';
      trailState.pendingFitBounds = false;
      trailState.restoreCenter = state.center || null;
      trailState.restoreLevel = state.level || null;
      trailState.hantiReturnTrailIndex = Number.isFinite(Number(state.returnTrailIndex)) ? Number(state.returnTrailIndex) : getHantiMainTrailIndex();
      trailState.hantiShowGpsTrace = state.showGpsTrace !== false;
      trailState.hantiRouteReverse = !!state.routeReverse;
      initTrailModule();
      trailSetView('map');
      setTimeout(function(){
        try{
          if(!(trailState && trailState.map && window.kakao && kakao.maps)) return;
          openHantiFullRoute(data, {source:'background-resume', noFitBounds:true});
          trailState.hantiLastRoutePointIndex = Number.isFinite(Number(state.lastRoutePointIndex)) ? Number(state.lastRoutePointIndex) : null;
          trailState.hantiLastProgressM = Number.isFinite(Number(state.lastProgressM)) ? Number(state.lastProgressM) : 0;
          trailState.hantiArrivedStampIds = state.arrivedStampIds || {};
          try{ Object.keys(trailState.hantiArrivedStampIds || {}).forEach(markHantiStampOverlayArrived); }catch(_e){}
          if(Number.isFinite(Number(trailState.hantiLastRoutePointIndex))){
            var resumeRouteInfo = {pointIndex:Number(trailState.hantiLastRoutePointIndex), progressM:Number(trailState.hantiLastProgressM || 0), routeReverse:!!trailState.hantiRouteReverse};
            drawHantiProgressToIndex(resumeRouteInfo);
            updateHantiRouteDirectionArrows(resumeRouteInfo);
          }
          restoreMapViewFromHantiState(state);
          closeTrailSheetOnly();
          startHantiGpxFollow({resume:true, silent:true, backgroundResume:true});
          saveHantiFollowResumeState('background-restored-route-screen');
          try{ relayoutTrailMap(120, 'hanti-background-resume'); }catch(_e){}
        }catch(e){ console.warn('[가톨릭길동무]', e); }
      }, 140);
      return true;
    }catch(e){ console.warn('[가톨릭길동무]', e); return false; }
  }

  try{
    window.oaiSaveHantiBackgroundRouteState = saveHantiFollowResumeState;
    window.oaiShouldKeepHantiRouteOnBackgroundReturn = hasHantiFollowResumeState;
    window.oaiRestoreHantiBackgroundRouteResume = restoreHantiFollowFromBackground;
  }catch(_e){}
  function syncTrailLocButtonForSheet(open){
    try{
      var panel = ig$('trail-panel-map');
      var sheet = ig$('trail-sheet');
      if(!panel) return;
      if(!open || !sheet){
        panel.classList.remove('trail-sheet-is-open');
        panel.style.removeProperty('--trail-sheet-open-height');
        return;
      }
      requestAnimationFrame(function(){
        try{
          var rect = sheet.getBoundingClientRect ? sheet.getBoundingClientRect() : null;
          var h = rect && rect.height ? Math.ceil(rect.height) : 0;
          if(h > 0) panel.style.setProperty('--trail-sheet-open-height', h + 'px');
          panel.classList.add('trail-sheet-is-open');
        }catch(e){ console.warn('[가톨릭길동무]', e); }
      });
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }

  function closeTrailSheetOnly(){
    try{ ig$('trail-sheet')?.classList.remove('open'); syncTrailLocButtonForSheet(false); }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function restoreHantiRouteIfActive(){
    if(!isHantiRouteActive()) return;
    if(!(trailState && trailState.map && window.kakao && kakao.maps)) return;
    showHantiRouteOverlays();
  }
  function handleTrailMapClick(){
    if(trailState && trailState.hantiVisible){
      closeTrailSheetOnly();
      return;
    }
    trailCloseSheet();
  }
  function hantiStampMarkerClass(stamp){
    var cls = 'hanti-stamp-marker';
    if(stamp && stamp.role === 'start') cls += ' start';
    else if(stamp && stamp.role === 'finish') cls += ' finish';
    if(stamp && stamp.useGpxCoordinate === false) cls += ' exception';
    if(stamp && isHantiStampArrived(stamp.id)) cls += ' arrived';
    return cls;
  }
  function showHantiStampInfo(stamp){
    if(!stamp) return;
    var bdg = ig$('trail-sh-bdg');
    var region = ig$('trail-sh-region');
    var ico = ig$('trail-sh-ico');
    var name = ig$('trail-sh-name');
    var sub = ig$('trail-sh-sub');
    var url = ig$('trail-sh-url');
    var body = ig$('trail-sh-body');
    var foot = ig$('trail-sh-foot');
    if(bdg){ bdg.textContent = stamp.id || '스탬프'; bdg.className = 'trail-sh-bdg d'; }
    var activeData = getActiveHantiRouteData();
    var isTestRoute = activeData && activeData.type === 'test_route';
    if(region) region.textContent = isTestRoute ? ('🧪 ' + ((activeData && activeData.name) || '테스트 GPX') + ' 지점') : '📍 한티가는길 스탬프';
    if(ico){ ico.textContent = stamp.role === 'finish' ? '🏁' : (stamp.role === 'start' ? '⛪' : (isTestRoute ? '📍' : '✝️')); ico.className = 'trail-sh-ico d'; }
    if(name) name.textContent = stamp.name || '';
    if(sub) sub.textContent = isTestRoute ? 'GPX 따라가기 테스트 · 실제 기록 저장 없음' : ((stamp.en || '') + (stamp.role === 'finish' ? ' · 완료 지점' : ''));
    removeTrailSheetActions();
    if(hantiTestModeEnabled()) ensureHantiTestPanel(); else removeHantiTestPanel();
    if(stamp && (stamp.id === '3-4' || stamp.id === '4-1')){
      setTrailHantiNote('동명읍 안에서는 여러 길로 동명성당에 도착할 수 있습니다. 표시된 경로선은 참고용입니다.');
    }else{
      setTrailHantiNote('');
    }
    if(url) url.textContent = '스탬프 위치 확인용';
    if(body) body.onclick = function(ev){ if(ev){ ev.preventDefault(); ev.stopPropagation(); } };
    if(foot) foot.onclick = function(ev){ if(ev){ ev.preventDefault(); ev.stopPropagation(); } };
    ig$('trail-sheet')?.classList.add('open');
  }
  function createHantiStampOverlay(stamp){
    if(!(stamp && Number.isFinite(Number(stamp.lat)) && Number.isFinite(Number(stamp.lng)))) return null;
    var el = document.createElement('button');
    el.type = 'button';
    el.className = hantiStampMarkerClass(stamp);
    el.setAttribute('aria-label', getActiveHantiRouteTitle() + ' ' + (stamp.id || '') + ' ' + (stamp.name || ''));
    el.setAttribute('data-hanti-stamp-id', stamp.id || '');
    el.innerHTML = '<span class="hanti-stamp-id">' + esc(stamp.id || '') + '</span><span class="hanti-stamp-name">' + esc(stamp.name || '') + '</span>';
    el.addEventListener('click', function(ev){
      try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){}
      showHantiStampInfo(stamp);
      if(trailState.map && window.kakao && kakao.maps){
        try{ trailState.map.panTo(new kakao.maps.LatLng(Number(stamp.lat), Number(stamp.lng))); }catch(_e){}
      }
    });
    return new kakao.maps.CustomOverlay({
      content: el,
      position: new kakao.maps.LatLng(Number(stamp.lat), Number(stamp.lng)),
      xAnchor: .5, yAnchor: .5, zIndex: 80
    });
  }

  function findHantiStampById(data, id){
    var list = data && data.stamps || [];
    for(var i=0;i<list.length;i++){ if(list[i] && list[i].id === id) return list[i]; }
    return null;
  }
  function hantiDistanceScoreFromLatLng(ll, stamp){
    if(!(ll && stamp)) return Infinity;
    var lat = Number(stamp.lat), lng = Number(stamp.lng);
    if(!Number.isFinite(lat) || !Number.isFinite(lng)) return Infinity;
    var dLat = ll.getLat() - lat;
    var dLng = ll.getLng() - lng;
    return dLat*dLat + dLng*dLng;
  }
  function hantiDistanceMeters(lat1, lng1, lat2, lng2){
    lat1 = Number(lat1); lng1 = Number(lng1); lat2 = Number(lat2); lng2 = Number(lng2);
    if(!Number.isFinite(lat1) || !Number.isFinite(lng1) || !Number.isFinite(lat2) || !Number.isFinite(lng2)) return Infinity;
    var R = 6371008.8;
    var p1 = lat1 * Math.PI / 180, p2 = lat2 * Math.PI / 180;
    var dp = (lat2 - lat1) * Math.PI / 180;
    var dl = (lng2 - lng1) * Math.PI / 180;
    var a = Math.sin(dp/2) * Math.sin(dp/2) + Math.cos(p1) * Math.cos(p2) * Math.sin(dl/2) * Math.sin(dl/2);
    return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  }
  function hantiNow(){ return Date.now ? Date.now() : new Date().getTime(); }
  function hantiBearingDegrees(lat1, lng1, lat2, lng2){
    lat1 = Number(lat1); lng1 = Number(lng1); lat2 = Number(lat2); lng2 = Number(lng2);
    if(!Number.isFinite(lat1) || !Number.isFinite(lng1) || !Number.isFinite(lat2) || !Number.isFinite(lng2)) return null;
    var p1 = lat1 * Math.PI / 180, p2 = lat2 * Math.PI / 180;
    var dl = (lng2 - lng1) * Math.PI / 180;
    var y = Math.sin(dl) * Math.cos(p2);
    var x = Math.cos(p1) * Math.sin(p2) - Math.sin(p1) * Math.cos(p2) * Math.cos(dl);
    var brng = Math.atan2(y, x) * 180 / Math.PI;
    brng = (brng + 360) % 360;
    return Number.isFinite(brng) ? brng : null;
  }
  function hantiRouteBearingFromInfo(routeInfo){
    try{
      var data = getActiveHantiRouteData();
      var pts = hantiFlattenRoutePoints(data);
      if(!(routeInfo && pts && pts.length)) return null;
      var idx = Math.max(0, Math.min(pts.length - 1, Number(routeInfo.pointIndex || 0)));
      return hantiRouteBearingAtPointIndex(pts, idx, isHantiRouteReverse());
    }catch(_e){ return null; }
  }
  function hantiResolveHeading(lat, lng, routeInfo, pos){
    var heading = pos && pos.coords && Number(pos.coords.heading);
    if(Number.isFinite(heading) && heading >= 0) return heading;
    if(trailState && Number.isFinite(Number(trailState.hantiLastGpsLat)) && Number.isFinite(Number(trailState.hantiLastGpsLng))){
      var moved = hantiDistanceMeters(trailState.hantiLastGpsLat, trailState.hantiLastGpsLng, lat, lng);
      if(Number.isFinite(moved) && moved >= 3){
        heading = hantiBearingDegrees(trailState.hantiLastGpsLat, trailState.hantiLastGpsLng, lat, lng);
        if(Number.isFinite(heading)) return heading;
      }
    }
    heading = hantiRouteBearingFromInfo(routeInfo);
    if(Number.isFinite(heading)) return heading;
    if(trailState && Number.isFinite(Number(trailState.hantiLastHeading))) return Number(trailState.hantiLastHeading);
    return 0;
  }
  function buildTrailMyLocationElement(heading, follow){
    var dot = document.createElement('div');
    dot.className = 'trail-myloc' + (follow ? ' follow' : '');
    if(follow){
      var h = Number(heading);
      if(!Number.isFinite(h)) h = 0;
      dot.style.setProperty('--hanti-bearing', h.toFixed(1) + 'deg');
      dot.setAttribute('aria-label', '진행 방향');
    }
    return dot;
  }
  function isHantiRouteReverse(){
    return !!(trailState && trailState.hantiRouteReverse);
  }
  function updateHantiRouteReverseButton(){
    var btn = ig$('trail-hanti-route-reverse');
    if(!btn) return;
    var reverse = isHantiRouteReverse();
    btn.classList.toggle('on', reverse);
    btn.setAttribute('aria-pressed', reverse ? 'true' : 'false');
    btn.textContent = reverse ? '↔ 반대 진행 중' : '↔ 경로 반대';
    btn.title = reverse ? '한티가는길을 반대 방향으로 따라가는 중입니다. 누르면 정방향으로 바뀝니다.' : '한티가는길을 반대 방향으로 따라갑니다.';
  }
  function getHantiRouteTotalDistanceM(data, pts){
    var total = Number(data && data.stats && data.stats.routeDistanceM);
    if(!Number.isFinite(total) || total <= 0){
      total = (pts && pts.length) ? Number(pts[pts.length - 1].routeDistanceM || 0) : 0;
    }
    return Number.isFinite(total) && total > 0 ? total : 0;
  }
  function hantiDirectionalProgressM(routeDistanceM, total, reverse){
    routeDistanceM = Number(routeDistanceM || 0);
    total = Number(total || 0);
    if(!Number.isFinite(routeDistanceM)) routeDistanceM = 0;
    if(!Number.isFinite(total) || total <= 0) return Math.max(0, routeDistanceM);
    return reverse ? Math.max(0, total - routeDistanceM) : Math.max(0, routeDistanceM);
  }
  function refreshHantiRouteAfterDirectionChange(){
    try{
      updateHantiRouteReverseButton();
      var data = getActiveHantiRouteData();
      var pts = hantiFlattenRoutePoints(data);
      var total = getHantiRouteTotalDistanceM(data, pts);
      if(Number.isFinite(Number(trailState && trailState.hantiLastGpsLat)) && Number.isFinite(Number(trailState && trailState.hantiLastGpsLng))){
        var info = findNearestHantiRoutePointFromCoords(trailState.hantiLastGpsLat, trailState.hantiLastGpsLng, {follow:false});
        if(info){
          trailState.hantiLastRoutePointIndex = Number(info.pointIndex);
          trailState.hantiLastProgressM = Number(info.progressM || 0);
          drawHantiProgressToIndex(info);
          updateHantiRouteDirectionArrows(info);
          showHantiLocationGuide(trailState.hantiLastGpsLat, trailState.hantiLastGpsLng, {follow:!!trailState.hantiFollowActive, routeInfo:info});
          return;
        }
      }
      if(pts.length){
        var idx = isHantiRouteReverse() ? pts.length - 1 : 0;
        var info2 = {pointIndex:idx, progressM:0, totalDistanceM:total, routeReverse:isHantiRouteReverse()};
        updateHantiRouteDirectionArrows(info2);
      }
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function setHantiRouteReverse(reverse, opts){
    opts = opts || {};
    if(!trailState) return;
    var next = !!reverse;
    var changed = trailState.hantiRouteReverse !== next;
    trailState.hantiRouteReverse = next;
    if(changed && !opts.keepProgress){
      resetHantiFollowSession();
    }
    refreshHantiRouteAfterDirectionChange();
    if(trailState.hantiFollowActive) saveHantiFollowResumeState(next ? 'reverse-route-on' : 'reverse-route-off');
  }
  function clearHantiAutoPanResumeTimer(){
    try{ if(trailState && trailState.hantiAutoPanResumeTimer) clearTimeout(trailState.hantiAutoPanResumeTimer); }catch(_e){}
    if(trailState) trailState.hantiAutoPanResumeTimer = 0;
  }
  function markHantiProgrammaticMapMove(ms){
    if(!trailState) return;
    trailState.hantiProgrammaticMoveUntil = hantiNow() + (ms || 900);
  }
  function centerHantiMapOnLatLng(lat, lng, opts){
    opts = opts || {};
    if(!(trailState && trailState.map && window.kakao && kakao.maps)) return false;
    lat = Number(lat); lng = Number(lng);
    if(!Number.isFinite(lat) || !Number.isFinite(lng)) return false;
    try{
      var ll = new kakao.maps.LatLng(lat, lng);
      markHantiProgrammaticMapMove(opts.fast ? 700 : 1200);
      if(typeof trailState.map.panTo === 'function') trailState.map.panTo(ll);
      else trailState.map.setCenter(ll);
      if(opts.level && trailState.map.getLevel && trailState.map.setLevel && trailState.map.getLevel() > opts.level){
        markHantiProgrammaticMapMove(1400);
        trailState.map.setLevel(opts.level);
      }
      return true;
    }catch(e){ console.warn('[가톨릭길동무]', e); return false; }
  }
  function resumeHantiAutoPanNow(reason){
    if(!trailState) return false;
    clearHantiAutoPanResumeTimer();
    trailState.hantiAutoPanPaused = false;
    trailState.hantiAutoPanHoldUntil = 0;
    if(trailState.hantiFollowActive && Number.isFinite(Number(trailState.hantiLastGpsLat)) && Number.isFinite(Number(trailState.hantiLastGpsLng))){
      return centerHantiMapOnLatLng(trailState.hantiLastGpsLat, trailState.hantiLastGpsLng, {level:4});
    }
    return false;
  }
  function pauseHantiAutoPanAfterUserMove(reason){
    if(!(trailState && trailState.hantiFollowActive && trailState.hantiRouteViewMode)) return;
    var now = hantiNow();
    if(trailState.hantiProgrammaticMoveUntil && now < trailState.hantiProgrammaticMoveUntil) return;
    trailState.hantiAutoPanPaused = true;
    trailState.hantiAutoPanHoldUntil = now + 5000;
    clearHantiAutoPanResumeTimer();
    trailState.hantiAutoPanResumeTimer = setTimeout(function(){
      try{ resumeHantiAutoPanNow('auto-resume-after-user-map-move'); }catch(e){ console.warn('[가톨릭길동무]', e); }
    }, 5050);
  }
  function shouldHantiAutoPanFollow(){
    if(!(trailState && trailState.hantiFollowActive)) return false;
    if(!trailState.hantiAutoPanPaused) return true;
    if(hantiNow() >= Number(trailState.hantiAutoPanHoldUntil || 0)){
      trailState.hantiAutoPanPaused = false;
      trailState.hantiAutoPanHoldUntil = 0;
      return true;
    }
    return false;
  }
  function bindHantiRouteMapInteractionHandlers(){
    try{
      if(!(trailState && trailState.map && window.kakao && kakao.maps) || trailState.hantiMapInteractionBound) return;
      trailState.hantiMapInteractionBound = true;
      kakao.maps.event.addListener(trailState.map, 'dragstart', function(){ pauseHantiAutoPanAfterUserMove('dragstart'); });
      kakao.maps.event.addListener(trailState.map, 'zoom_changed', function(){ pauseHantiAutoPanAfterUserMove('zoom_changed'); });
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function hantiFormatDistance(m){
    m = Number(m);
    if(!Number.isFinite(m)) return '';
    if(m >= 1000) return (m >= 10000 ? Math.round(m/1000) : (m/1000).toFixed(1)) + 'km';
    return Math.round(m) + 'm';
  }
  function findNearestHantiStampFromCoords(lat, lng){
    var data = getActiveHantiRouteData();
    var list = data && data.stamps || [];
    var best = null, bestD = Infinity;
    list.forEach(function(stamp){
      if(!(stamp && Number.isFinite(Number(stamp.lat)) && Number.isFinite(Number(stamp.lng)))) return;
      var d = hantiDistanceMeters(lat, lng, stamp.lat, stamp.lng);
      if(d < bestD){ bestD = d; best = stamp; }
    });
    return best ? { stamp: best, distanceM: bestD } : null;
  }
  function hantiFlattenRoutePoints(data){
    var pts = [];
    (data && data.routeSegments || []).forEach(function(seg){
      (seg.points || []).forEach(function(p){
        var lat = Number(p && p.lat), lng = Number(p && p.lng);
        if(!Number.isFinite(lat) || !Number.isFinite(lng)) return;
        pts.push({
          lat: lat,
          lng: lng,
          routeIndex: Number.isFinite(Number(p.routeIndex)) ? Number(p.routeIndex) : pts.length,
          routeDistanceM: Number.isFinite(Number(p.routeDistanceM)) ? Number(p.routeDistanceM) : null
        });
      });
    });
    var cum = 0;
    for(var i=0;i<pts.length;i++){
      if(i > 0 && pts[i].routeDistanceM == null){
        cum += hantiDistanceMeters(pts[i-1].lat, pts[i-1].lng, pts[i].lat, pts[i].lng);
        pts[i].routeDistanceM = cum;
      }else if(i === 0 && pts[i].routeDistanceM == null){
        pts[i].routeDistanceM = 0;
      }else{
        cum = Number(pts[i].routeDistanceM) || cum;
      }
      if(!Number.isFinite(pts[i].routeIndex)) pts[i].routeIndex = i;
    }
    return pts;
  }
  function findNearestHantiRoutePointFromCoords(lat, lng, opts){
    opts = opts || {};
    var data = getActiveHantiRouteData();
    var pts = hantiFlattenRoutePoints(data);
    if(!pts.length) return null;
    var reverse = isHantiRouteReverse();
    var total = getHantiRouteTotalDistanceM(data, pts);
    var best = null, bestD = Infinity, bestIdx = -1;
    var start = 0, end = pts.length - 1;
    if(opts.follow && Number.isFinite(Number(trailState.hantiLastRoutePointIndex))){
      var prev = Number(trailState.hantiLastRoutePointIndex);
      start = reverse ? Math.max(0, prev - 45) : Math.max(0, prev - 6);
      end = reverse ? Math.min(pts.length - 1, prev + 6) : Math.min(pts.length - 1, prev + 45);
    }
    for(var i=start;i<=end;i++){
      var d = hantiDistanceMeters(lat, lng, pts[i].lat, pts[i].lng);
      if(d < bestD){ bestD = d; best = pts[i]; bestIdx = i; }
    }
    if(opts.follow && bestD > 120){
      for(var j=0;j<pts.length;j++){
        var gd = hantiDistanceMeters(lat, lng, pts[j].lat, pts[j].lng);
        if(gd < bestD){ bestD = gd; best = pts[j]; bestIdx = j; }
      }
    }else if(opts.follow && !Number.isFinite(Number(trailState.hantiLastRoutePointIndex)) && data && data.type === 'test_route'){
      var startBest = null, startBestD = Infinity, startBestIdx = -1;
      var searchM = data.id === 'company_test_route' ? 220 : 650;
      for(var k=0;k<pts.length;k++){
        var rd = Number(pts[k].routeDistanceM || 0);
        if(!reverse && rd > searchM) break;
        if(reverse && total > 0 && rd < total - searchM) continue;
        var sd = hantiDistanceMeters(lat, lng, pts[k].lat, pts[k].lng);
        if(sd < startBestD){ startBestD = sd; startBest = pts[k]; startBestIdx = k; }
      }
      if(startBest && startBestD <= bestD + 35){ best = startBest; bestD = startBestD; bestIdx = startBestIdx; }
    }
    if(!best) return null;
    var directionalProgress = hantiDirectionalProgressM(best.routeDistanceM, total, reverse);
    if(opts.follow){
      var prevIdx = Number(trailState.hantiLastRoutePointIndex);
      if(Number.isFinite(prevIdx)){
        if(!reverse && bestIdx < prevIdx - 6){
          bestIdx = Math.max(0, Math.min(pts.length - 1, prevIdx));
          best = pts[bestIdx];
          bestD = hantiDistanceMeters(lat, lng, best.lat, best.lng);
          directionalProgress = hantiDirectionalProgressM(best.routeDistanceM, total, reverse);
        }else if(reverse && bestIdx > prevIdx + 6){
          bestIdx = Math.max(0, Math.min(pts.length - 1, prevIdx));
          best = pts[bestIdx];
          bestD = hantiDistanceMeters(lat, lng, best.lat, best.lng);
          directionalProgress = hantiDirectionalProgressM(best.routeDistanceM, total, reverse);
        }
      }
      trailState.hantiLastRoutePointIndex = bestIdx;
      trailState.hantiLastProgressM = Math.max(Number(trailState.hantiLastProgressM || 0), Number(directionalProgress || 0));
    }
    var progressM = opts.follow ? Math.max(Number(directionalProgress || 0), Number(trailState.hantiLastProgressM || 0)) : Number(directionalProgress || 0);
    return { point: best, pointIndex: bestIdx, distanceM: bestD, totalDistanceM: total, progressM: progressM, actualRouteDistanceM:Number(best.routeDistanceM || 0), routeReverse:reverse, progressRate: total > 0 ? progressM / total : 0 };
  }
  function hantiStampDirectionalProgressM(stamp, total, reverse){
    if(!(stamp && Number.isFinite(Number(stamp.routeDistanceM)))) return null;
    return hantiDirectionalProgressM(Number(stamp.routeDistanceM), total, reverse);
  }
  function findNextHantiStampByProgress(progressM){
    var data = getActiveHantiRouteData();
    var pts = hantiFlattenRoutePoints(data);
    var total = getHantiRouteTotalDistanceM(data, pts);
    var reverse = isHantiRouteReverse();
    var list = (data && data.stamps || []).slice().filter(function(s){ return Number.isFinite(Number(s.routeDistanceM)); });
    list.sort(function(a,b){
      var ap = hantiStampDirectionalProgressM(a, total, reverse);
      var bp = hantiStampDirectionalProgressM(b, total, reverse);
      return Number(ap || 0) - Number(bp || 0);
    });
    for(var i=0;i<list.length;i++){
      var sp = hantiStampDirectionalProgressM(list[i], total, reverse);
      if(Number(sp) + 15 >= Number(progressM || 0)) return list[i];
    }
    return list.length ? list[list.length-1] : null;
  }
  function hantiRouteStatusLabel(routeDistanceM){
    var d = Number(routeDistanceM);
    if(!Number.isFinite(d)) return '';
    if(d <= 30) return 'GPX 경로 위';
    if(d <= 80) return 'GPX 경로 근처';
    return 'GPX 경로 이탈 가능';
  }
  function clearHantiProgressOverlays(){
    try{ (trailState.hantiProgressPolylines || []).forEach(function(line){ try{ line.setMap(null); }catch(_e){} }); }catch(_e){}
    if(trailState) trailState.hantiProgressPolylines = [];
  }
  function drawHantiProgressToIndex(routeInfo){
    if(!(routeInfo && trailState && trailState.map && window.kakao && kakao.maps)) return;
    var data = getActiveHantiRouteData();
    var pts = hantiFlattenRoutePoints(data);
    if(!pts.length) return;
    var idx = Math.max(0, Math.min(pts.length - 1, Number(routeInfo.pointIndex || 0)));
    var reverse = !!routeInfo.routeReverse || isHantiRouteReverse();
    clearHantiProgressOverlays();
    var path = [];
    if(reverse){
      for(var r=pts.length - 1;r>=idx;r--){
        path.push(new kakao.maps.LatLng(Number(pts[r].lat), Number(pts[r].lng)));
      }
    }else{
      for(var i=0;i<=idx;i++){
        path.push(new kakao.maps.LatLng(Number(pts[i].lat), Number(pts[i].lng)));
      }
    }
    if(path.length > 1){
      var line = new kakao.maps.Polyline({
        map: trailState.map, path:path,
        strokeWeight:6, strokeColor:'#16A34A', strokeOpacity:.95,
        strokeStyle:'solid', zIndex:39
      });
      trailState.hantiProgressPolylines.push(line);
    }
  }
  function clearHantiRouteDirectionOverlays(){
    try{ (trailState && trailState.hantiRouteDirectionOverlays || []).forEach(function(ov){ try{ ov.setMap(null); }catch(_e){} }); }catch(_e){}
    if(trailState) trailState.hantiRouteDirectionOverlays = [];
  }
  function findHantiPointIndexAtDistance(pts, targetM, startIdx){
    if(!(pts && pts.length)) return -1;
    var start = Math.max(0, Math.min(pts.length - 1, Number(startIdx || 0)));
    targetM = Number(targetM);
    if(!Number.isFinite(targetM)) return -1;
    for(var i=start;i<pts.length;i++){
      if(Number(pts[i].routeDistanceM || 0) >= targetM) return i;
    }
    return pts.length - 1;
  }
  function hantiRouteBearingAtPointIndex(pts, idx, reverse){
    if(!(pts && pts.length > 1)) return null;
    idx = Math.max(0, Math.min(pts.length - 1, Number(idx || 0)));
    var cur = pts[idx];
    if(!cur) return null;
    if(reverse){
      for(var prevIdx = idx - 1; prevIdx >= Math.max(0, idx - 10); prevIdx--){
        var prev = pts[prevIdx];
        if(prev && hantiDistanceMeters(cur.lat, cur.lng, prev.lat, prev.lng) >= 4){
          return hantiBearingDegrees(cur.lat, cur.lng, prev.lat, prev.lng);
        }
      }
      for(var nextIdxFallback = idx + 1; nextIdxFallback < Math.min(pts.length, idx + 10); nextIdxFallback++){
        var nextFallback = pts[nextIdxFallback];
        if(nextFallback && hantiDistanceMeters(nextFallback.lat, nextFallback.lng, cur.lat, cur.lng) >= 4){
          return hantiBearingDegrees(nextFallback.lat, nextFallback.lng, cur.lat, cur.lng);
        }
      }
      return null;
    }
    for(var nextIdx = idx + 1; nextIdx < Math.min(pts.length, idx + 10); nextIdx++){
      var next = pts[nextIdx];
      if(next && hantiDistanceMeters(cur.lat, cur.lng, next.lat, next.lng) >= 4){
        return hantiBearingDegrees(cur.lat, cur.lng, next.lat, next.lng);
      }
    }
    for(var prevIdx2 = idx - 1; prevIdx2 >= Math.max(0, idx - 10); prevIdx2--){
      var prev2 = pts[prevIdx2];
      if(prev2 && hantiDistanceMeters(prev2.lat, prev2.lng, cur.lat, cur.lng) >= 4){
        return hantiBearingDegrees(prev2.lat, prev2.lng, cur.lat, cur.lng);
      }
    }
    return null;
  }
  function createHantiRouteDirectionArrowOverlay(point, bearing, strong){
    if(!(point && trailState && trailState.map && window.kakao && kakao.maps)) return null;
    var lat = Number(point.lat), lng = Number(point.lng), b = Number(bearing);
    if(!Number.isFinite(lat) || !Number.isFinite(lng) || !Number.isFinite(b)) return null;
    var el = document.createElement('div');
    el.className = 'hanti-route-dir-arrow' + (strong ? ' near' : '');
    el.style.setProperty('--hanti-route-bearing', b.toFixed(1) + 'deg');
    el.setAttribute('aria-label', '경로 진행 방향');
    el.innerHTML = '<span class="hanti-route-dir-arrow-shape" aria-hidden="true"></span>';
    return new kakao.maps.CustomOverlay({
      content: el,
      position: new kakao.maps.LatLng(lat, lng),
      xAnchor: .5, yAnchor: .5, zIndex: 62
    });
  }
  function updateHantiRouteDirectionArrows(routeInfo){
    clearHantiRouteDirectionOverlays();
    if(!(routeInfo && trailState && trailState.map && window.kakao && kakao.maps)) return;
    if(!(trailState.hantiFollowActive && trailState.hantiRouteViewMode)) return;
    var data = getActiveHantiRouteData();
    var pts = hantiFlattenRoutePoints(data);
    if(pts.length < 2) return;
    var reverse = !!routeInfo.routeReverse || isHantiRouteReverse();
    var total = getHantiRouteTotalDistanceM(data, pts);
    var progressM = Number(routeInfo.progressM || 0);
    var baseIdx = Math.max(0, Math.min(pts.length - 1, Number(routeInfo.pointIndex || 0)));
    if(!Number.isFinite(progressM)) progressM = hantiDirectionalProgressM(pts[baseIdx].routeDistanceM, total, reverse);
    var isTestRoute = data && data.type === 'test_route';
    var firstGap = isTestRoute ? 35 : 70;
    var interval = isTestRoute ? 80 : 150;
    var maxArrows = isTestRoute ? 5 : 6;
    var made = 0;
    var lastIdx = -1;
    for(var n=0;n<maxArrows;n++){
      var targetProgressM = progressM + firstGap + interval * n;
      if(total && targetProgressM > total + 25) break;
      var actualM = reverse ? Math.max(0, total - targetProgressM) : targetProgressM;
      var idx = findHantiPointIndexAtDistance(pts, actualM, reverse ? 0 : baseIdx + 1);
      if(idx < 0 || idx >= pts.length) continue;
      if(lastIdx >= 0 && hantiDistanceMeters(pts[lastIdx].lat, pts[lastIdx].lng, pts[idx].lat, pts[idx].lng) < interval * .45) continue;
      var bearing = hantiRouteBearingAtPointIndex(pts, idx, reverse);
      if(!Number.isFinite(Number(bearing))) continue;
      var ov = createHantiRouteDirectionArrowOverlay(pts[idx], bearing, made === 0);
      if(!ov) continue;
      ov.setMap(trailState.map);
      trailState.hantiRouteDirectionOverlays.push(ov);
      lastIdx = idx;
      made++;
    }
  }
  function clearHantiGpsTraceOverlay(){
    try{ if(trailState && trailState.hantiGpsTracePolyline) trailState.hantiGpsTracePolyline.setMap(null); }catch(_e){}
    if(trailState) trailState.hantiGpsTracePolyline = null;
  }
  function renderHantiGpsTrace(){
    if(!(trailState && trailState.map && window.kakao && kakao.maps)) return;
    clearHantiGpsTraceOverlay();
    if(!trailState.hantiShowGpsTrace) return;
    var pts = trailState.hantiGpsTracePoints || [];
    if(pts.length < 2) return;
    var path = pts.map(function(p){ return new kakao.maps.LatLng(Number(p.lat), Number(p.lng)); });
    trailState.hantiGpsTracePolyline = new kakao.maps.Polyline({
      map: trailState.map, path:path,
      strokeWeight:3, strokeColor:'#2563EB', strokeOpacity:.78,
      strokeStyle:'solid', zIndex:40
    });
  }
  function appendHantiGpsTracePoint(lat, lng){
    if(!trailState) return;
    lat = Number(lat); lng = Number(lng);
    if(!Number.isFinite(lat) || !Number.isFinite(lng)) return;
    var pts = trailState.hantiGpsTracePoints || (trailState.hantiGpsTracePoints = []);
    var last = pts.length ? pts[pts.length - 1] : null;
    if(!last || hantiDistanceMeters(last.lat, last.lng, lat, lng) >= 3){
      pts.push({lat:lat, lng:lng, at:Date.now()});
      if(pts.length > 2000) pts.splice(0, pts.length - 2000);
      renderHantiGpsTrace();
    }
  }
  function updateHantiVisualModeButtons(){
    var on = !!(trailState && trailState.hantiShowGpsTrace);
    var a = ig$('trail-hanti-visual-gpx-only');
    var b = ig$('trail-hanti-visual-with-gps');
    if(a) a.classList.toggle('on', !on);
    if(b) b.classList.toggle('on', on);
  }
  function setHantiGpsTraceVisible(on){
    if(!trailState) return;
    trailState.hantiShowGpsTrace = !!on;
    if(on) renderHantiGpsTrace(); else clearHantiGpsTraceOverlay();
    updateHantiVisualModeButtons();
  }
  function isHantiStampArrived(id){
    return !!(id && trailState && trailState.hantiArrivedStampIds && trailState.hantiArrivedStampIds[id]);
  }
  function markHantiStampOverlayArrived(id){
    if(!id) return;
    try{
      document.querySelectorAll('[data-hanti-stamp-id="' + String(id).replace(/"/g, '') + '"]').forEach(function(el){
        el.classList.add('arrived');
      });
    }catch(_e){}
  }
  function checkHantiWaypointArrivals(lat, lng){
    var data = getActiveHantiRouteData();
    var newly = [];
    (data && data.stamps || []).forEach(function(stamp){
      if(!(stamp && stamp.id && Number.isFinite(Number(stamp.lat)) && Number.isFinite(Number(stamp.lng)))) return;
      if(isHantiStampArrived(stamp.id)) return;
      var radius = Number(stamp.autoStampRadiusM || 50);
      if(!Number.isFinite(radius) || radius <= 0) radius = 50;
      var d = hantiDistanceMeters(lat, lng, stamp.lat, stamp.lng);
      if(d <= radius){
        trailState.hantiArrivedStampIds[stamp.id] = true;
        markHantiStampOverlayArrived(stamp.id);
        newly.push(stamp);
      }
    });
    return newly;
  }
  function resetHantiFollowSession(){
    clearHantiProgressOverlays();
    clearHantiRouteDirectionOverlays();
    clearHantiGpsTraceOverlay();
    if(trailState){
      trailState.hantiGpsTracePoints = [];
      trailState.hantiAutoPanPaused = false;
      trailState.hantiAutoPanHoldUntil = 0;
      clearHantiAutoPanResumeTimer();
      trailState.hantiLastRoutePointIndex = null;
      trailState.hantiLastProgressM = 0;
      trailState.hantiArrivedStampIds = {};
    }
    try{ document.querySelectorAll('.hanti-stamp-marker.arrived').forEach(function(el){ el.classList.remove('arrived'); }); }catch(_e){}
    updateHantiVisualModeButtons();
  }
  function hantiUrlTestModeEnabled(){
    try{
      var params = new URLSearchParams(window.location.search || '');
      var q = String(params.get('hantiTest') || '').toLowerCase();
      if(q === '1' || q === 'true' || q === 'yes') return true;
      var hash = String(window.location.hash || '');
      if(/(?:^|[?#&])hantiTest=(1|true|yes)(?:&|$)/i.test(hash)) return true;
      return false;
    }catch(_e){ return false; }
  }
  function hantiTestModeEnabled(){
    if(trailState && trailState.hantiTestClosed) return false;
    return !!(trailState && trailState.hantiTestUnlocked);
  }
  function unlockHantiTestMode(){
    if(trailState){
      trailState.hantiTestUnlocked = true;
      trailState.hantiTestClosed = false;
    }
    ensureHantiTestPanel();
    try{
      var panel = ig$('trail-hanti-test-panel');
      if(panel && panel.scrollIntoView) panel.scrollIntoView({block:'nearest', behavior:'smooth'});
    }catch(_e){}
  }
  function bindHantiSecretTapTarget(el){
    if(!el || el.__hantiSecretTapBound) return;
    el.__hantiSecretTapBound = true;
    el.addEventListener('click', function(ev){
      try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){}
      if(!(trailState && trailState.hantiSecretTitleReady)) return;
      var now = Date.now();
      if(!trailState.hantiTestTapStartedAt || now - trailState.hantiTestTapStartedAt > 3500){
        trailState.hantiTestTapStartedAt = now;
        trailState.hantiTestTapCount = 0;
      }
      trailState.hantiTestTapCount = (trailState.hantiTestTapCount || 0) + 1;
      if(trailState.hantiTestTapCount >= 5){
        trailState.hantiTestTapCount = 0;
        unlockHantiTestMode();
      }
    });
  }
  function removeHantiTestPanel(){
    try{ var old = ig$('trail-hanti-test-panel'); if(old) old.remove(); }catch(_e){}
  }
  function closeHantiTestMode(){
    if(trailState){
      trailState.hantiTestUnlocked = false;
      trailState.hantiTestClosed = true;
      trailState.hantiTestTapCount = 0;
      trailState.hantiTestTapStartedAt = 0;
    }
    stopHantiGpxFollow();
    removeHantiTestPanel();
  }
  function stopHantiGpxFollow(opts){
    opts = opts || {};
    try{
      if(trailState && trailState.hantiFollowWatchId != null && navigator.geolocation){
        navigator.geolocation.clearWatch(trailState.hantiFollowWatchId);
      }
    }catch(_e){}
    clearHantiRouteDirectionOverlays();
    if(trailState){
      trailState.hantiFollowWatchId = null;
      trailState.hantiFollowActive = false;
      trailState.hantiAutoPanPaused = false;
      trailState.hantiAutoPanHoldUntil = 0;
      clearHantiAutoPanResumeTimer();
    }
    if(!opts.keepResumeState) clearHantiFollowResumeState(opts.reason || 'manual-stop');
    var btn = ig$('trail-hanti-follow-toggle');
    if(btn) btn.textContent = 'GPX 따라가기 시작';
    var routeBtn = ig$('trail-hanti-route-follow');
    if(routeBtn) routeBtn.textContent = '▶ 경로 따라가기';
    updateHantiRouteReverseButton();
  }
  function startHantiGpxFollow(opts){
    opts = opts || {};
    if(!(window.navigator && navigator.geolocation)){ if(!opts.silent) alert('위치 서비스를 지원하지 않습니다.'); return; }
    if(!(trailState && trailState.map && window.kakao && kakao.maps)){ initTrailModule(); return; }
    if(trailState.hantiFollowActive){
      if(opts.resume){
        try{ if(trailState.hantiFollowWatchId != null && navigator.geolocation) navigator.geolocation.clearWatch(trailState.hantiFollowWatchId); }catch(_e){}
        trailState.hantiFollowWatchId = null;
        trailState.hantiFollowActive = false;
      }else{
        stopHantiGpxFollow({reason:'manual-stop'});
        return;
      }
    }
    if(!opts.resume) resetHantiFollowSession();
    trailState.hantiFollowActive = true;
    trailState.hantiAutoPanPaused = false;
    trailState.hantiAutoPanHoldUntil = 0;
    clearHantiAutoPanResumeTimer();
    bindHantiRouteMapInteractionHandlers();
    saveHantiFollowResumeState(opts.resume ? 'resume-start' : 'manual-start');
    var btn = ig$('trail-hanti-follow-toggle');
    if(btn) btn.textContent = 'GPX 따라가기 중지';
    var routeBtn = ig$('trail-hanti-route-follow');
    if(routeBtn) routeBtn.textContent = '■ 정지';
    updateHantiRouteReverseButton();
    var first = !opts.resume;
    function onpos(pos){
      var lat = pos.coords.latitude, lng = pos.coords.longitude;
      var routeInfo = findNearestHantiRoutePointFromCoords(lat, lng, {follow:true});
      var heading = hantiResolveHeading(lat, lng, routeInfo, pos);
      showHantiLocationGuide(lat, lng, {follow:true, routeInfo:routeInfo});
      try{
        var ll = new kakao.maps.LatLng(lat, lng);
        if(trailState.myOverlay) trailState.myOverlay.setMap(null);
        var dot = buildTrailMyLocationElement(heading, true);
        trailState.myOverlay = new kakao.maps.CustomOverlay({content:dot, position:ll, yAnchor:.5, zIndex:100});
        trailState.myOverlay.setMap(trailState.map);
        trailState.hantiLastGpsLat = lat;
        trailState.hantiLastGpsLng = lng;
        trailState.hantiLastHeading = heading;
        if(first){
          centerHantiMapOnLatLng(lat, lng, {level:4});
        }else if(shouldHantiAutoPanFollow()){
          centerHantiMapOnLatLng(lat, lng, {level:4});
        }
        first = false;
        saveHantiFollowResumeState('position-update');
      }catch(e){ console.warn('[가톨릭길동무]', e); }
    }
    function onerr(e){
      stopHantiGpxFollow({reason:'location-error'});
      if(!opts.silent) alert(e && e.code === 1 ? '위치 권한을 허용해 주세요.' : '위치를 가져올 수 없습니다.');
    }
    try{
      trailState.hantiFollowWatchId = navigator.geolocation.watchPosition(onpos, onerr, {enableHighAccuracy:true, timeout:15000, maximumAge:2000});
    }catch(e){ stopHantiGpxFollow({reason:'watch-start-error'}); if(!opts.silent) alert('위치 추적을 시작할 수 없습니다.'); }
  }
  function hantiAutoStampJudgement(nearest){
    if(!(nearest && nearest.stamp)) return {ok:false, radius:0};
    var radius = Number(nearest.stamp.autoStampRadiusM || 70);
    if(!Number.isFinite(radius) || radius <= 0) radius = 70;
    return {ok:Number(nearest.distanceM) <= radius, radius:radius};
  }
  function updateHantiTestResult(sourceStamp, nearest){
    var out = ig$('trail-hanti-test-result');
    if(!out || !(nearest && nearest.stamp)) return;
    var judge = hantiAutoStampJudgement(nearest);
    var label = (nearest.stamp.id || '') + ' ' + (nearest.stamp.name || '');
    var dist = hantiFormatDistance(nearest.distanceM);
    var activeData = getActiveHantiRouteData();
    var done = nearest.stamp.role === 'finish' ? (activeData && activeData.id === 'hanti' ? ' · 5-5 완료 지점' : ' · 완료 지점') : '';
    out.innerHTML = '<strong>테스트 결과</strong>' +
      '<span>테스트 위치: ' + esc((sourceStamp && sourceStamp.id || '') + ' ' + (sourceStamp && sourceStamp.name || '')) + '</span>' +
      '<span>가장 가까운 스탬프: ' + esc(label) + (dist ? ' · 약 ' + esc(dist) : '') + done + '</span>' +
      '<span>자동도장 판정: ' + (judge.ok ? '가능' : '불가') + ' / 기준 반경 ' + Math.round(judge.radius) + 'm</span>' +
      '<span>실제 순례기록 저장: 안 함</span>';
  }
  function runHantiTestStamp(stampId){
    var data = getActiveHantiRouteData();
    var stamp = findHantiStampById(data, stampId);
    if(!(stamp && Number.isFinite(Number(stamp.lat)) && Number.isFinite(Number(stamp.lng)))) return;
    var lat = Number(stamp.lat), lng = Number(stamp.lng);
    showHantiLocationGuide(lat, lng, {test:true, sourceStamp:stamp});
    try{
      if(trailState && trailState.map && window.kakao && kakao.maps){
        var ll = new kakao.maps.LatLng(lat, lng);
        trailState.map.panTo(ll);
        if(trailState.map.getLevel && trailState.map.setLevel && trailState.map.getLevel() > 5) trailState.map.setLevel(5);
      }
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function getHantiMainTrailIndex(){
    try{
      for(var i=0;i<TRAIL_ITEMS.length;i++){ if(isHantiTrailItem(TRAIL_ITEMS[i])) return i; }
    }catch(_e){}
    return -1;
  }
  function setTrailGeneralSheetMode(active){
    try{
      var sheet = ig$('trail-sheet');
      if(sheet) sheet.classList.toggle('trail-general-sheet', !!active);
    }catch(_e){}
  }
  function removeTrailSheetActions(){
    try{ var old = ig$('trail-sh-actions'); if(old) old.remove(); }catch(_e){}
    try{ ig$('trail-sheet')?.classList.remove('hanti-main-card', 'trail-general-sheet'); }catch(_e){}
  }
  function ensureHantiMainSheetActions(item){
    var foot = ig$('trail-sh-foot');
    if(!foot) return;
    removeTrailSheetActions();
    try{ ig$('trail-sheet')?.classList.add('hanti-main-card'); }catch(_e){}
    var actions = document.createElement('div');
    actions.id = 'trail-sh-actions';
    actions.className = 'trail-sh-actions hanti-main-actions';
    actions.innerHTML =
      '<button id="trail-sh-home-btn" type="button" class="trail-sh-action-btn">홈페이지</button>' +
      '<button id="trail-sh-full-route-btn" type="button" class="trail-sh-action-btn primary">전체경로보기</button>';
    foot.parentNode.insertBefore(actions, foot.nextSibling);
    var home = ig$('trail-sh-home-btn');
    if(home) home.onclick = function(ev){
      try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){}
      if(item && item.url) openExternalUrl(item.url, {module:'trail', view:'map'});
    };
    var route = ig$('trail-sh-full-route-btn');
    if(route) route.onclick = function(ev){
      try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){}
      trailState.hantiReturnTrailIndex = Number.isFinite(Number(trailState.selected)) && trailState.selected >= 0 ? trailState.selected : getHantiMainTrailIndex();
      openHantiFullRoute(getHantiRouteData(), {source:'hanti-main'});
    };
  }
  function setTrailZoomControlVisible(show){
    try{
      if(!(trailState && trailState.map && trailState.trailZoomControl && window.kakao && kakao.maps)) return;
      if(show && trailState.trailZoomControlVisible === false){
        trailState.map.addControl(trailState.trailZoomControl, kakao.maps.ControlPosition.RIGHT);
        trailState.trailZoomControlVisible = true;
      }else if(!show && trailState.trailZoomControlVisible !== false){
        trailState.map.removeControl(trailState.trailZoomControl);
        trailState.trailZoomControlVisible = false;
      }
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function setHantiRouteMapChrome(active){
    var on = !!active;
    try{ ig$('trail-view')?.classList.toggle('hanti-route-mode', on); }catch(_e){}
    try{ ig$('trail-panel-map')?.classList.toggle('hanti-route-mode', on); }catch(_e){}
    setTrailZoomControlVisible(!on);
  }
  function removeHantiRouteControls(){
    try{
      var panel = ig$('trail-panel-map');
      var toolbar = ig$('trail-hanti-route-controls');
      var loc = ig$('trail-loc-btn');
      if(panel && loc && toolbar && loc.parentNode === toolbar) panel.appendChild(loc);
      if(toolbar) toolbar.remove();
      var x = ig$('trail-hanti-route-x');
      if(x) x.remove();
      setHantiRouteMapChrome(false);
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function ensureHantiRouteControls(){
    var panel = ig$('trail-panel-map');
    if(!panel) return;
    setHantiRouteMapChrome(true);
    var controls = ig$('trail-hanti-route-controls');
    if(!controls){
      controls = document.createElement('div');
      controls.id = 'trail-hanti-route-controls';
      controls.className = 'hanti-route-controls';
      panel.appendChild(controls);
    }
    controls.innerHTML = '';
    var row = document.createElement('div');
    row.className = 'hanti-route-control-row';
    controls.appendChild(row);
    var follow = document.createElement('button');
    follow.id = 'trail-hanti-route-follow';
    follow.type = 'button';
    follow.className = 'hanti-route-control-btn primary';
    follow.textContent = trailState.hantiFollowActive ? '■ 정지' : '▶ 경로 따라가기';
    row.appendChild(follow);
    var loc = ig$('trail-loc-btn');
    if(loc) row.appendChild(loc);
    var reverse = document.createElement('button');
    reverse.id = 'trail-hanti-route-reverse';
    reverse.type = 'button';
    reverse.className = 'hanti-route-control-btn reverse';
    controls.appendChild(reverse);
    updateHantiRouteReverseButton();
    follow.onclick = function(ev){
      try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){}
      startHantiGpxFollow();
    };
    reverse.onclick = function(ev){
      try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){}
      setHantiRouteReverse(!isHantiRouteReverse());
    };
    var x = ig$('trail-hanti-route-x');
    if(!x){
      x = document.createElement('button');
      x.id = 'trail-hanti-route-x';
      x.type = 'button';
      x.className = 'hanti-route-x';
      x.setAttribute('aria-label', '경로보기 닫기');
      x.textContent = '×';
      panel.appendChild(x);
    }
    x.onclick = function(ev){
      try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){}
      closeHantiFullRouteToSheet('route-x');
    };
  }
  function openHantiFullRoute(routeData, opts){
    opts = opts || {};
    var data = routeData || getHantiRouteData();
    if(!(data && trailState && trailState.map && window.kakao && kakao.maps)) return;
    if(!Number.isFinite(Number(trailState.hantiReturnTrailIndex)) || trailState.hantiReturnTrailIndex < 0){
      trailState.hantiReturnTrailIndex = Number.isFinite(Number(trailState.selected)) && trailState.selected >= 0 ? trailState.selected : getHantiMainTrailIndex();
    }
    showHantiRouteOverlays(data);
    trailState.hantiRouteViewMode = true;
    setHantiRouteActive(true);
    closeTrailSheetOnly();
    removeTrailSheetActions();
    setHantiRouteMapChrome(true);
    ensureHantiRouteControls();
    if(opts && opts.noFitBounds) restoreMapViewFromHantiState(opts.resumeState || null);
    else fitHantiRouteBounds(data);
    if(trailState.hantiFollowActive) saveHantiFollowResumeState('route-open');
  }
  function closeHantiFullRouteToSheet(reason){
    if(!(trailState && (trailState.hantiRouteViewMode || trailState.hantiVisible))) return false;
    var idx = Number.isFinite(Number(trailState.hantiReturnTrailIndex)) && trailState.hantiReturnTrailIndex >= 0 ? Number(trailState.hantiReturnTrailIndex) : getHantiMainTrailIndex();
    stopHantiGpxFollow({reason:'close-route'});
    if(trailState) trailState.hantiRouteReverse = false;
    clearHantiRouteOverlays();
    removeHantiRouteControls();
    setHantiRouteActive(false);
    trailState.hantiRouteViewMode = false;
    trailState.hantiSecretTitleReady = false;
    if(idx >= 0){
      setTimeout(function(){
        try{ window.trailOpenSheet(idx); }catch(e){ console.warn('[가톨릭길동무]', e); }
      }, 60);
    }
    return true;
  }
  try{ window._oaiTrailBackHandle = function(reason){ return closeHantiFullRouteToSheet(reason || 'back'); }; }catch(_e){}

  function activateHantiTestRoute(routeId){
    var data = getHantiRouteDataById(routeId);
    if(!(data && trailState && trailState.map && window.kakao && kakao.maps)) return;
    trailState.hantiReturnTrailIndex = Number.isFinite(Number(trailState.selected)) && trailState.selected >= 0 ? trailState.selected : getHantiMainTrailIndex();
    trailState.hantiRouteReverse = false;
    openHantiFullRoute(data, {source:'hidden-test'});
  }
  function ensureHantiTestPanel(){
    if(!hantiTestModeEnabled()){ removeHantiTestPanel(); return; }
    var data = getActiveHantiRouteData();
    var info = ig$('trail-sh-name') && ig$('trail-sh-name').parentElement;
    if(!(data && info)) return;
    var panel = ig$('trail-hanti-test-panel');
    if(!panel){
      panel = document.createElement('div');
      panel.id = 'trail-hanti-test-panel';
      panel.className = 'hanti-test-panel';
      info.appendChild(panel);
    }
    var activeId = data.id || 'hanti';
    var stamps = data.stamps || [];
    var options = stamps.map(function(s){
      return '<option value="' + esc(s.id || '') + '">' + esc((s.id || '') + ' ' + (s.name || '')) + '</option>';
    }).join('');
    panel.innerHTML = '<div class="hanti-test-head"><div class="hanti-test-title">' + esc(data.name || '한티가는길') + ' 테스트 모드</div>' +
      '<button id="trail-hanti-test-close" type="button" class="hanti-test-close">테스트 닫기</button></div>' +
      '<div class="hanti-test-help">숨은 테스트입니다. 실제 순례기록은 저장하지 않습니다. 도원동/회사 근처 테스트 GPX는 사용자가 직접 올린 파일 기준입니다.</div>' +
      '<div class="hanti-test-switch" aria-label="테스트 경로 선택">' +
      '<button id="trail-hanti-test-route-hanti" type="button" class="' + (activeId === 'hanti' ? 'on' : '') + '">한티가는길</button>' +
      '<button id="trail-hanti-test-route-dowon" type="button" class="' + (activeId === 'dowon_test_loop' ? 'on' : '') + '">도원동 테스트 루프</button>' +
      '<button id="trail-hanti-test-route-company" type="button" class="' + (activeId === 'company_test_route' ? 'on' : '') + '">회사 근처 테스트</button>' +
      '</div>' +
      '<div class="hanti-test-follow"><button id="trail-hanti-follow-toggle" type="button">GPX 따라가기 시작</button></div>' +
      '<div class="hanti-test-visual" aria-label="화면 표시 방식 선택">' +
      '<button id="trail-hanti-visual-gpx-only" type="button" class="' + (!trailState.hantiShowGpsTrace ? 'on' : '') + '">GPX 색변화만</button>' +
      '<button id="trail-hanti-visual-with-gps" type="button" class="' + (trailState.hantiShowGpsTrace ? 'on' : '') + '">파란 이동선 함께</button>' +
      '</div>' +
      '<div class="hanti-test-row"><select id="trail-hanti-test-select" aria-label="테스트할 지점 선택">' + options + '</select>' +
      '<button id="trail-hanti-test-run" type="button">테스트</button></div>' +
      '<div id="trail-hanti-test-result" class="hanti-test-result">지점을 선택하고 테스트를 누르세요.</div>';
    panel.onclick = function(ev){ try{ ev.stopPropagation(); }catch(_e){} };
    var closeBtn = ig$('trail-hanti-test-close');
    if(closeBtn){
      closeBtn.onclick = function(ev){
        try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){}
        closeHantiTestMode();
      };
    }
    var hantiBtn = ig$('trail-hanti-test-route-hanti');
    if(hantiBtn) hantiBtn.onclick = function(ev){ try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){} activateHantiTestRoute('hanti'); };
    var dowonBtn = ig$('trail-hanti-test-route-dowon');
    if(dowonBtn) dowonBtn.onclick = function(ev){ try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){} activateHantiTestRoute('dowon'); };
    var companyBtn = ig$('trail-hanti-test-route-company');
    if(companyBtn) companyBtn.onclick = function(ev){ try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){} activateHantiTestRoute('company'); };
    var followBtn = ig$('trail-hanti-follow-toggle');
    if(followBtn) followBtn.onclick = function(ev){ try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){} startHantiGpxFollow(); };
    var gpxOnlyBtn = ig$('trail-hanti-visual-gpx-only');
    if(gpxOnlyBtn) gpxOnlyBtn.onclick = function(ev){ try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){} setHantiGpsTraceVisible(false); };
    var withGpsBtn = ig$('trail-hanti-visual-with-gps');
    if(withGpsBtn) withGpsBtn.onclick = function(ev){ try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){} setHantiGpsTraceVisible(true); };
    updateHantiVisualModeButtons();
    var sel = ig$('trail-hanti-test-select');
    var btn = ig$('trail-hanti-test-run');
    if(btn) btn.onclick = function(ev){ try{ ev.preventDefault(); ev.stopPropagation(); }catch(_e){} runHantiTestStamp(sel && sel.value); };
    if(sel) sel.onchange = function(){ runHantiTestStamp(sel.value); };
  }
  function clearHantiLocationGuideOverlay(){
    try{ if(trailState && trailState.hantiLocationOverlay) trailState.hantiLocationOverlay.setMap(null); }catch(_e){}
    if(trailState) trailState.hantiLocationOverlay = null;
  }
  function showHantiLocationGuide(lat, lng, opts){
    opts = opts || {};
    if(!(trailState && trailState.hantiVisible && trailState.map && window.kakao && kakao.maps)) return;
    var nearest = findNearestHantiStampFromCoords(lat, lng);
    if(!(nearest && nearest.stamp)) return;
    var routeInfo = opts.routeInfo || findNearestHantiRoutePointFromCoords(lat, lng, {follow:!!opts.follow});
    var nextStamp = routeInfo ? findNextHantiStampByProgress(routeInfo.progressM) : null;
    var label = (nearest.stamp.id || '') + ' ' + (nearest.stamp.name || '');
    var dist = hantiFormatDistance(nearest.distanceM);
    var routeDist = routeInfo ? hantiFormatDistance(routeInfo.distanceM) : '';
    var nextLabel = nextStamp ? ((nextStamp.id || '') + ' ' + (nextStamp.name || '')) : '';
    var remainText = '';
    if(nextStamp && routeInfo && Number.isFinite(Number(nextStamp.routeDistanceM))){
      var nextProgressM = hantiStampDirectionalProgressM(nextStamp, Number(routeInfo.totalDistanceM || 0), !!routeInfo.routeReverse || isHantiRouteReverse());
      remainText = hantiFormatDistance(Math.max(0, Number(nextProgressM || 0) - Number(routeInfo.progressM || 0)));
    }
    var judge = hantiAutoStampJudgement(nearest);
    var status = routeInfo ? hantiRouteStatusLabel(routeInfo.distanceM) : '';
    var progress = routeInfo && Number.isFinite(routeInfo.progressRate) ? Math.max(0, Math.min(100, routeInfo.progressRate * 100)) : null;
    var newlyArrived = [];
    if(opts.follow){
      appendHantiGpsTracePoint(lat, lng);
      if(routeInfo){
        drawHantiProgressToIndex(routeInfo);
        updateHantiRouteDirectionArrows(routeInfo);
      }
      newlyArrived = checkHantiWaypointArrivals(lat, lng) || [];
    }
    if(opts.test){
      setTrailHantiNote('테스트 위치 기준 가까운 지점: ' + label + (dist ? ' · 약 ' + dist : '') + ' / 실제 기록 저장 안 함');
      updateHantiTestResult(opts.sourceStamp, nearest);
    }else{
      var msg = (opts.follow ? ('GPX 따라가기 중' + (isHantiRouteReverse() ? '(역방향)' : '')) : '현재 위치 확인') + ': ' + (status || '경로 확인') + (routeDist ? ' · 경로까지 ' + routeDist : '');
      if(nextLabel) msg += ' / 다음 지점: ' + nextLabel + (remainText ? ' · 약 ' + remainText : '');
      if(progress != null) msg += ' / 진행률 ' + Math.round(progress) + '%';
      if(newlyArrived.length) msg += ' / ' + newlyArrived.map(function(s){ return (s.id || '') + ' ' + (s.name || ''); }).join(', ') + ' 도착 확인';
      msg += ' / 자동도장 OFF';
      setTrailHantiNote(msg);
    }
    clearHantiLocationGuideOverlay();
    var el = document.createElement('div');
    el.className = 'hanti-location-guide' + (opts.test ? ' test' : '');
    el.innerHTML = '<strong>' + (opts.test ? '테스트 위치' : (opts.follow ? ('GPX 따라가기' + (isHantiRouteReverse() ? ' · 역방향' : '')) : '현재 위치')) + '</strong>' +
      '<span>' + esc(status || label) + (routeDist ? ' · 경로까지 ' + esc(routeDist) : '') + '</span>' +
      (nextLabel ? '<span>다음: ' + esc(nextLabel) + (remainText ? ' · 약 ' + esc(remainText) : '') + '</span>' : '');
    trailState.hantiLocationOverlay = new kakao.maps.CustomOverlay({
      content: el,
      position: new kakao.maps.LatLng(Number(lat), Number(lng)),
      xAnchor: .5, yAnchor: 1.65, zIndex: 101
    });
    trailState.hantiLocationOverlay.setMap(trailState.map);
  }
  function findNearestHantiPathIndex(path, stamp){
    if(!(path && path.length && stamp)) return -1;
    var best = -1, bestScore = Infinity;
    for(var i=0;i<path.length;i++){
      var score = hantiDistanceScoreFromLatLng(path[i], stamp);
      if(score < bestScore){ bestScore = score; best = i; }
    }
    return best;
  }
  function findNearestHantiPathIndexByPoint(path, point){
    if(!(path && path.length && point)) return -1;
    var lat = Number(point.lat), lng = Number(point.lng);
    if(!Number.isFinite(lat) || !Number.isFinite(lng)) return -1;
    var best = -1, bestScore = Infinity;
    for(var i=0;i<path.length;i++){
      var ll = path[i];
      if(!ll) continue;
      var dLat = ll.getLat() - lat;
      var dLng = ll.getLng() - lng;
      var score = dLat*dLat + dLng*dLng;
      if(score < bestScore){ bestScore = score; best = i; }
    }
    return best;
  }
  function hantiLatLngPathFromPoints(points){
    var out = [];
    if(!(points && points.length) || !(window.kakao && kakao.maps)) return out;
    points.forEach(function(p){
      var lat = Number(p && p.lat), lng = Number(p && p.lng);
      if(Number.isFinite(lat) && Number.isFinite(lng)) out.push(new kakao.maps.LatLng(lat, lng));
    });
    return out;
  }
  function drawHantiPolyline(path, option){
    if(!(path && path.length > 1 && trailState.map && window.kakao && kakao.maps)) return null;
    var line = new kakao.maps.Polyline({
      map: trailState.map,
      path: path,
      strokeWeight: option && option.weight || 5,
      strokeColor: option && option.color || '#B7791F',
      strokeOpacity: option && option.opacity || .86,
      strokeStyle: option && option.style || 'solid',
      zIndex: option && option.zIndex || 30
    });
    trailState.hantiPolylines.push(line);
    return line;
  }
  function createHantiFlexibleGuideOverlay(path){
    if(!(path && path.length && trailState.map && window.kakao && kakao.maps)) return null;
    var mid = path[Math.floor(path.length * 0.60)] || path[Math.floor(path.length / 2)];
    if(!mid) return null;
    var el = document.createElement('div');
    el.className = 'hanti-flex-guide';
    el.innerHTML = '<strong>동명읍 구간</strong><span>여러 길 가능 · 경로선은 참고용 · 4-1 동명성당</span>';
    return new kakao.maps.CustomOverlay({
      content: el,
      position: mid,
      xAnchor: .5, yAnchor: 1.2, zIndex: 75
    });
  }
  function setTrailHantiNote(text){
    var info = ig$('trail-sh-name') && ig$('trail-sh-name').parentElement;
    if(!info) return;
    var old = ig$('trail-sh-note');
    if(!text){ if(old) old.remove(); return; }
    var note = old || document.createElement('div');
    note.id = 'trail-sh-note';
    note.className = 'trail-sh-note';
    note.textContent = text;
    if(!old) info.appendChild(note);
  }
  function showHantiRouteOverlays(routeData){
    var data = routeData || getHantiRouteData();
    if(!(data && trailState.map && window.kakao && kakao.maps)) return;
    clearHantiRouteOverlays();
    try{
      /* V8-1-14-471: 원본 GPX segment를 강제 병합하지 않고 그대로 그린다.
         여러 segment를 하나로 합치면 없는 길이 직선처럼 이어질 수 있으므로, 테스트 루프도 같은 방식으로 표시한다. */
      var isTestRoute = data && data.type === 'test_route';
      trailState.hantiActiveRouteData = data;
      trailState.hantiActiveRouteId = data.id || 'hanti';
      (data.routeSegments || []).forEach(function(seg){
        var path = hantiLatLngPathFromPoints(seg && seg.points);
        if(path.length > 1){
          drawHantiPolyline(path, {
            weight: isTestRoute ? 5 : 5,
            color: '#B7791F',
            opacity: isTestRoute ? .68 : .86,
            zIndex: 30
          });
        }
      });
      (data.stamps || []).forEach(function(stamp){
        var ov = createHantiStampOverlay(stamp);
        if(!ov) return;
        ov.setMap(trailState.map);
        trailState.hantiStampOverlays.push(ov);
      });
      trailState.hantiVisible = true;
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  function fitHantiRouteBounds(routeData){
    var data = routeData || getActiveHantiRouteData();
    if(!(data && trailState.map && window.kakao && kakao.maps)) return;
    try{
      var bounds = new kakao.maps.LatLngBounds();
      var count = 0;
      (data.routeSegments || []).forEach(function(seg){
        (seg.points || []).forEach(function(p){
          if(!Number.isFinite(Number(p.lat)) || !Number.isFinite(Number(p.lng))) return;
          bounds.extend(new kakao.maps.LatLng(Number(p.lat), Number(p.lng)));
          count++;
        });
      });
      if(count > 1){ trailState.map.setBounds(bounds); }
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  const RETURN_KEY = 'catholic_integrated_return_v2';
  const trailState = {inited:false, map:null, trailZoomControl:null, trailZoomControlVisible:true, markers:[], selected:-1, myOverlay:null, view:'map', pendingOpenIndex:null, restoreCenter:null, restoreLevel:null, needsHardReset:false, pendingFitBounds:false, hantiPolylines:[], hantiProgressPolylines:[], hantiRouteDirectionOverlays:[], hantiStampOverlays:[], hantiGpsTracePolyline:null, hantiGpsTracePoints:[], hantiShowGpsTrace:true, hantiVisible:false, hantiLocationOverlay:null, hantiRouteActive:false, hantiRouteViewMode:false, hantiRouteReverse:false, hantiReturnTrailIndex:-1, hantiSecretTitleReady:false, hantiActiveRouteData:null, hantiActiveRouteId:'', hantiFollowWatchId:null, hantiFollowActive:false, hantiLastRoutePointIndex:null, hantiLastProgressM:0, hantiArrivedStampIds:{}, hantiLastGpsLat:null, hantiLastGpsLng:null, hantiLastHeading:0, hantiAutoPanPaused:false, hantiAutoPanHoldUntil:0, hantiAutoPanResumeTimer:0, hantiProgrammaticMoveUntil:0, hantiMapInteractionBound:false, hantiTestUnlocked:false, hantiTestClosed:false, hantiTestTapCount:0, hantiTestTapStartedAt:0};
  const webState = {built:false, curCat:'⭐ 즐겨찾기'};
  const WEB_FAV_KEY = 'web_favorites_v1';
  const MY_DIOCESE_KEY = 'oai_my_diocese_name';
  let webFavs = [];
  function wfLoad(){ try{ webFavs=JSON.parse(localStorage.getItem(WEB_FAV_KEY)||'[]'); }catch(e){ webFavs=[]; } }
  function wfSave(){ try{ localStorage.setItem(WEB_FAV_KEY, JSON.stringify(webFavs)); }catch(e){ console.warn("[가톨릭길동무]", e); } }
  function wfHas(url){ return webFavs.includes(url); }
  function webDefaultCat(){
    return webFavs && webFavs.length ? '⭐ 즐겨찾기' : '교구';
  }
  function wfToggle(url){
    const hadFavs = !!(webFavs && webFavs.length);
    if(wfHas(url)) webFavs=webFavs.filter(u=>u!==url);
    else webFavs.push(url);
    wfSave();
    const hasFavs = !!(webFavs && webFavs.length);
    if(hadFavs !== hasFavs){
      rebuildWebCats();
      return;
    }
    var favBtn = document.getElementById('web-cat_⭐ 즐겨찾기');
    if(favBtn){ favBtn.innerHTML = '⭐ 즐겨찾기'; }
  }
  wfLoad();

  function ig$(id){ return document.getElementById(id); }
  function esc(s){ return String(s ?? '').replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m])); }
  function shortUrl(url){ return String(url||'').replace(/^https?:\/\//,'').replace(/\/$/,''); }
  function getMyDioceseName(){
    try{ return (localStorage.getItem(MY_DIOCESE_KEY) || '').trim(); }catch(e){ return ''; }
  }
  function normalizeDioceseName(name){
    return String(name || '')
      .replace(/^천주교\s*/, '')
      .replace(/\s+/g, '')
      .trim();
  }
  function isMyDioceseWebItem(item, myName){
    if(!item || !myName) return false;
    var itemName = String(item.name || '').trim();
    if(item.cat === '사제찾기'){
      return itemName === myName + ' 사제찾기' || itemName.indexOf(myName) === 0;
    }
    if(item.cat === '교구'){
      return itemName === myName;
    }
    return false;
  }
  function isMyDioceseTrailItem(item, myName){
    if(!item || !myName) return false;
    var my = normalizeDioceseName(myName);
    var op = normalizeDioceseName(item.op);
    return !!(my && op && op === my);
  }
  function webCategoryRank(cat){
    var order = {
      '교구': 0,
      '사제찾기': 1,
      '중앙기구': 2,
      '신앙 포털': 3,
      '미디어': 4,
      '뉴스': 5,
      '출판·교육': 6
    };
    return Object.prototype.hasOwnProperty.call(order, cat) ? order[cat] : 99;
  }

  function sortWebItemsForMyDiocese(items){
    if(!Array.isArray(items) || items.length < 2) return items;
    var myName = getMyDioceseName();
    var isFavTab = webState.curCat === '⭐ 즐겨찾기';
    var shouldPreferMyDiocese = !!(myName && (isFavTab || webState.curCat === '사제찾기' || webState.curCat === '교구'));
    return items.slice().sort(function(a,b){
      if(isFavTab){
        var ca = webCategoryRank(a && a.cat);
        var cb = webCategoryRank(b && b.cat);
        if(ca !== cb) return ca - cb;
      }
      if(shouldPreferMyDiocese){
        var aa = isMyDioceseWebItem(a, myName) ? 0 : 1;
        var bb = isMyDioceseWebItem(b, myName) ? 0 : 1;
        if(aa !== bb) return aa - bb;
      }
      return WEB_SITES.indexOf(a) - WEB_SITES.indexOf(b);
    });
  }
  function myDioceseBadgeHtml(){
    return '<span class="web-my-diocese-badge">나의 교구</span>';
  }
  function trailMyDioceseBadgeHtml(){
    return '<span class="trail-my-diocese-badge">나의 교구</span>';
  }
  function webProvinceBadgeHtml(prov){
    if(!prov) return '';
    return '<span class="web-province-inline">' + esc(prov) + '</span>';
  }
  function webCardNameHtml(item){
    if(item && (item.cat === '사제찾기' || item.cat === '교구')){
      var dioName = item.cat === '사제찾기' ? String(item.op || item.name || '').replace(/\s*사제찾기\s*$/, '') : String(item.name || '');
      var tail = item.cat === '사제찾기' ? '사제찾기' : '홈페이지';
      return '<span class="web-diocese-name-main">' + esc(dioName) + '</span>'
        + '<span class="web-card-name-tail">' + esc(tail) + '</span>';
    }
    return esc(item && item.name || '');
  }
  function hideIntegratedViews(){
    ig$('web-view')?.classList.remove('open');
    ig$('trail-view')?.classList.remove('open');
    if(typeof trailCloseSheet === 'function') trailCloseSheet();
  }
  function clearIntegratedReturnState(reason){
    try{ sessionStorage.removeItem(RETURN_KEY); }catch(e){ console.warn("[가톨릭길동무]", e); }
  }
  function nowMs(){
    try{ return Date.now ? Date.now() : new Date().getTime(); }catch(_e){ return new Date().getTime(); }
  }
  function isFreshIntegratedReturnState(state){
    try{
      var ts = Number(state && state.ts || 0);
      if(!ts) return true;
      return (nowMs() - ts) <= (30 * 60 * 1000);
    }catch(_e){ return true; }
  }
  function saveReturnState(state){
    try{
      if(state && typeof state === 'object') state.ts = nowMs();
      sessionStorage.setItem(RETURN_KEY, JSON.stringify(state));
    }catch(e){ console.warn("[가톨릭길동무]", e); }
  }
  function prepareExternalUrl(url){
    var raw = String(url || '').trim().replace(/^hthttp:\/\//i,'http://').replace(/^http\/\//i,'http://');
    if(/^http:\/\//i.test(raw)) return raw;
    url = (typeof normalizeCatholicExternalUrl === 'function')
          ? normalizeCatholicExternalUrl(url)
          : String(url || '').trim();
    return url || null;
  }
  function openExternalUrl(url, state){
    url = prepareExternalUrl(url);
    if(!url) return;

    if(state && state.module === 'web'){
      try{
        state.cat = webState.curCat || webDefaultCat();
        state.scroll = ig$('web-list') ? (ig$('web-list').scrollTop || 0) : (state.scroll || 0);
        state.catScroll = ig$('web-cats') ? (ig$('web-cats').scrollLeft || 0) : (state.catScroll || 0);
        saveReturnState(state);
      }catch(e){ console.warn("[가톨릭길동무]", e); }
      if(typeof oaiSmoothNavigate === 'function') oaiSmoothNavigate(url, 'web-external');
      else { try{ if(typeof markExternalReturnStabilize === 'function') markExternalReturnStabilize('web-external'); }catch(e){ console.warn("[가톨릭길동무]", e); } try{ location.href = url; }catch(e){ try{ location.assign(url); }catch(_){ } } }
      return;
    }

    if(state && state.module === 'trail'){
      try{
        state.view = trailState.view || state.view || 'map';
        state.scroll = ig$('trail-list') ? (ig$('trail-list').scrollTop || 0) : (state.scroll || 0);
        var selectedIndex = Number(trailState && trailState.selected);
        var selectedItem = Number.isFinite(selectedIndex) && selectedIndex >= 0 ? TRAIL_ITEMS[selectedIndex] : null;
        var generalSheetOpen = !!(selectedItem && !isHantiTrailItem(selectedItem) && ig$('trail-sheet') && ig$('trail-sheet').classList.contains('open'));
        if(generalSheetOpen){
          state.selected = selectedIndex;
          state.sheetOpen = true;
        }
        if(trailState.map && window.kakao && kakao.maps){
          var c = trailState.map.getCenter();
          state.center = c ? {lat:c.getLat(), lng:c.getLng()} : null;
          state.level = trailState.map.getLevel ? trailState.map.getLevel() : null;
        }
        saveReturnState(state);
      }catch(e){ console.warn("[가톨릭길동무]", e); }
      if(typeof oaiSmoothNavigate === 'function') oaiSmoothNavigate(url, 'trail-external');
      else { try{ if(typeof markExternalReturnStabilize === 'function') markExternalReturnStabilize('trail-external'); }catch(e){ console.warn("[가톨릭길동무]", e); } try{ location.href = url; }catch(e){ try{ location.assign(url); }catch(_){ } } }
      return;
    }
    try{ sessionStorage.removeItem(RETURN_KEY); }catch(e){ console.warn("[가톨릭길동무]", e); }
    if(typeof oaiSmoothNavigate === 'function') oaiSmoothNavigate(url, 'integrated-external');
    else { try{ if(typeof markExternalReturnStabilize === 'function') markExternalReturnStabilize('integrated-external'); }catch(e){ console.warn("[가톨릭길동무]", e); } location.href = url; }
    return;
  }

  const _origGoToCover = window.goToCover;
  window.goToCover = function(){
    clearIntegratedReturnState('go-to-cover');
    hideIntegratedViews();
    if(typeof _origGoToCover === 'function') return _origGoToCover();
  };

  function enterIntegratedView(id){
    try{ if(typeof window.oaiClearMapInfoSelection === 'function') window.oaiClearMapInfoSelection('integrated-view:'+id); }catch(e){ console.warn('[가톨릭길동무]', e); }
    hideIntegratedViews();
    _screen = 'map';
    if(typeof window.oaiSetMainMapLayerHidden === 'function') window.oaiSetMainMapLayerHidden(true);
    document.documentElement.classList.add('app-active');
    document.documentElement.classList.remove('parish-mode','retreat-mode');
    const cover = ig$('cover');
    if(cover) cover.style.display = 'none';
    var target=ig$(id);
    if(target){
      target.classList.add('open');
      if(typeof oaiEnterView==='function') oaiEnterView(target);
    }
  }

  window.openWebView = function(opts){
    const restore = !!(opts && opts.restore);
    if(!restore){
      resetWebTransientState();
      webState.curCat = webDefaultCat();
      const list = ig$('web-list');
      if(list){
        list.style.scrollBehavior = 'auto';
        list.scrollTop = 0;
        list.style.scrollBehavior = '';
      }
      const cats = ig$('web-cats');
      if(cats) cats.scrollLeft = 0;
    }
    enterIntegratedView('web-view');
    initWebModule();
    scheduleWebCatSync(webState.curCat || webDefaultCat());
  };

  window.openTrailView = function(opts){
    const restore = !!(opts&&opts.restore);
    const forceRebuild = !!(opts&&(opts.forceRebuild||opts.hardReset));
    if(forceRebuild || trailState.needsHardReset){
      hardResetTrailModule();
      trailState.needsHardReset = false;
    }
    if(!restore){
      clearIntegratedReturnState('trail-open-fresh');
      trailState.view='map';
      trailState.pendingOpenIndex=null;
      trailState.restoreCenter=null;
      trailState.restoreLevel=null;
      trailState.pendingFitBounds=true;
      closeTrailSheetOnly();
      const list=ig$('trail-list');
      if(list) list.scrollTop=0;
    }else{
      trailState.pendingFitBounds=false;
    }
    enterIntegratedView('trail-view');
    initTrailModule();
    trailSetView(trailState.view || 'map');
    if(!restore){
      relayoutTrailMap(120, 'trail-open-stable');
    }
  };

  function restoreIntegratedState(){
    let raw = null;
    try{ raw = sessionStorage.getItem(RETURN_KEY); }catch(e){ console.warn("[가톨릭길동무]", e); }
    if(!raw) return;
    let state = null;
    try{ state = JSON.parse(raw); }catch(e){ console.warn("[가톨릭길동무]", e); }
    try{ sessionStorage.removeItem(RETURN_KEY); }catch(e){ console.warn("[가톨릭길동무]", e); }
    if(!state || !state.module) return;
    if(!isFreshIntegratedReturnState(state)){
      clearIntegratedReturnState('stale-return-state');
      return;
    }

    if(state.module === 'web'){
      try{
        if(!ig$('web-view')?.classList.contains('open')) enterIntegratedView('web-view');
        webState.curCat = state.cat || webState.curCat || webDefaultCat();
        initWebModule();
        applyWebCatState(webState.curCat || webDefaultCat());
        renderWebList();
        scheduleWebCatSync(webState.curCat || webDefaultCat());
        var webList = ig$('web-list');
        if(webList){
          var wy = Number(state.scroll || 0);
          webList.style.scrollBehavior = 'auto';
          webList.scrollTop = wy;
          setTimeout(function(){ try{ webList.scrollTop = wy; }catch(_e){} }, 80);
          setTimeout(function(){ try{ webList.scrollTop = wy; }catch(_e){} }, 220);
          webList.style.scrollBehavior = '';
        }
        var webCats = ig$('web-cats');
        if(webCats && Number.isFinite(Number(state.catScroll))){
          var cx = Number(state.catScroll || 0);
          webCats.scrollLeft = cx;
          setTimeout(function(){ try{ webCats.scrollLeft = cx; }catch(_e){} }, 80);
        }
      }catch(e){ console.warn("[가톨릭길동무]", e); }
      return;
    }

    if(state.module === 'trail'){
      try{
        if(!ig$('trail-view')?.classList.contains('open')) enterIntegratedView('trail-view');
        trailState.view = state.view || trailState.view || 'map';
        trailState.restoreCenter = state.center || null;
        trailState.restoreLevel = state.level || null;
        trailState.pendingFitBounds = false;
        initTrailModule();
        trailSetView(trailState.view || 'map');
        if(state.view === 'list' || trailState.view === 'list'){
          var list = ig$('trail-list');
          if(list){
            var y = Number(state.scroll || 0);
            list.style.scrollBehavior='auto';
            list.scrollTop = y;
            setTimeout(function(){ try{ list.scrollTop = y; }catch(_e){} }, 80);
            setTimeout(function(){ try{ list.scrollTop = y; }catch(_e){} }, 220);
            list.style.scrollBehavior='';
          }
        }else if(state.sheetOpen && Number.isFinite(Number(state.selected))){
          var si = Number(state.selected);
          var selectedItem = TRAIL_ITEMS[si];
          if(selectedItem && !isHantiTrailItem(selectedItem)){
            setTimeout(function(){ try{ trailOpenSheet(si); }catch(_e){} }, 140);
          }
        }
      }catch(e){ console.warn("[가톨릭길동무]", e); }
      return;
    }
  }

  window.addEventListener('pageshow', function(ev){
    try{
      if(ev && ev.persisted && ig$('trail-view') && ig$('trail-view').classList.contains('open')){
        sessionStorage.removeItem(RETURN_KEY);
        return;
      }
    }catch(e){ console.warn('[가톨릭길동무]', e); }
    setTimeout(function(){
      try{
        if(window.oaiReturnConductorBusy && window.oaiReturnConductorBusy(['category-return','passive'])){ setTimeout(restoreIntegratedState, 380); return; }
        if(window.oaiReturnConductorRequest) window.oaiReturnConductorRequest('category-return', {ms:900});
        restoreIntegratedState();
        setTimeout(function(){ try{ if(window.oaiReturnConductorFinish) window.oaiReturnConductorFinish('category-return'); }catch(_e){} }, 520);
      }catch(e){ console.warn('[가톨릭길동무]', e); restoreIntegratedState(); }
    }, 0);
  });

  function scheduleHantiFollowBackgroundRestore(reason){
    try{
      if(!hasHantiFollowResumeState()) return;
      setTimeout(function(){ restoreHantiFollowFromBackground(reason || 'background-visible'); }, 180);
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }
  document.addEventListener('visibilitychange', function(){
    try{
      if(document.visibilityState === 'hidden') saveHantiFollowResumeState('visibility-hidden');
      else scheduleHantiFollowBackgroundRestore('visibility-visible');
    }catch(e){ console.warn('[가톨릭길동무]', e); }
  }, {passive:true});
  window.addEventListener('pagehide', function(){
    try{ saveHantiFollowResumeState('pagehide'); }catch(e){ console.warn('[가톨릭길동무]', e); }
  }, {passive:true});
  window.addEventListener('pageshow', function(){
    scheduleHantiFollowBackgroundRestore('pageshow');
  }, {passive:true});

  function resetWebTransientState(){
    try{ sessionStorage.removeItem(RETURN_KEY); }catch(e){ console.warn("[가톨릭길동무]", e); }
    try{ document.activeElement && document.activeElement.blur && document.activeElement.blur(); }catch(e){ console.warn("[가톨릭길동무]", e); }
    const list = ig$('web-list');
    if(list){
      list.style.scrollBehavior = 'auto';
      list.scrollTop = 0;
      list.style.scrollBehavior = '';
    }
    const empty = ig$('web-empty');
    if(empty) empty.classList.remove('show');
  }

  function webCatLabel(cat){
    if(cat === '교구') return '교구 홈페이지';
    if(cat === '중앙기구') return '중앙기관';
    return cat;
  }

  function webOrderedCats(){
    const cats = [];
    const priority = ['교구', '사제찾기', '중앙기구'];
    if(webFavs && webFavs.length) cats.push('⭐ 즐겨찾기');
    priority.forEach(function(cat){
      if(!cats.includes(cat) && WEB_SITES.some(function(s){ return s.cat === cat; })) cats.push(cat);
    });
    WEB_SITES.forEach(function(s){ if(!cats.includes(s.cat)) cats.push(s.cat); });
    return cats;
  }

  function rebuildWebCats(){
    const wrap = ig$('web-cats');
    if(!wrap) return;
    if(!(webFavs && webFavs.length) && webState.curCat === '⭐ 즐겨찾기') webState.curCat = '교구';
    webState.built = false;
    wrap.innerHTML = '';
    initWebModule();
  }

  function initWebModule(){
    if(webState.built){
      scheduleWebCatSync(webState.curCat||webDefaultCat());
      renderWebList();
      return;
    }
    webState.built = true;
    const wrap = ig$('web-cats');
    if(!wrap) return;
    const cats = webOrderedCats();
    cats.forEach(c => {
      const btn = document.createElement('button');
      btn.className = 'web-cat-btn' + (c===webState.curCat ? ' on' : '');
      btn.id = 'web-cat_' + c;
      btn.dataset.webCat = c;
      btn.dataset.catColor = c; // CSS 선택자용
      btn.setAttribute('aria-pressed', c===webState.curCat ? 'true' : 'false');
      const count = c==='⭐ 즐겨찾기' ? WEB_SITES.filter(s => wfHas(s.url)).length : WEB_SITES.filter(s => s.cat===c).length;
      btn.innerHTML = esc(webCatLabel(c)) + (c==='⭐ 즐겨찾기' ? '' : '<span class="cnt">' + count + '</span>');
      btn.addEventListener('click', function(){ setWebCat(c); });
      wrap.appendChild(btn);
    });
    scheduleWebCatSync(webState.curCat||webDefaultCat());
    renderWebList();
  }

  function applyWebCatState(cat){
    webState.curCat = cat || webDefaultCat();
    const btns = document.querySelectorAll('#web-cats .web-cat-btn');
    if(!btns.length) return false;
    btns.forEach(btn => {
      const name = btn.dataset.webCat || btn.id.replace('web-cat_','');
      const active = name===webState.curCat;
      btn.classList.toggle('on', active);
      btn.setAttribute('aria-pressed', active ? 'true' : 'false');
      btn.style.background = '';
      btn.style.color = '';
      btn.style.borderColor = '';
      btn.style.boxShadow = '';
    });
    return true;
  }

  function keepWebActiveCatVisible(cat, behavior){
    const activeCat = cat || webState.curCat || webDefaultCat();
    const activeBtn = ig$('web-cat_' + activeCat) || document.querySelector('#web-cats .web-cat-btn.on');
    if(!activeBtn) return;
    try{
      activeBtn.scrollIntoView({behavior: behavior || 'smooth', block:'nearest', inline:'center'});
    }catch(e){
      const wrap = ig$('web-cats');
      if(wrap) wrap.scrollLeft = Math.max(0, activeBtn.offsetLeft - (wrap.clientWidth - activeBtn.offsetWidth) / 2);
    }
  }

  function scheduleWebCatSync(cat){
    const nextCat = cat || webState.curCat || webDefaultCat();
    applyWebCatState(nextCat);
    keepWebActiveCatVisible(nextCat, 'auto');
    requestAnimationFrame(function(){ applyWebCatState(nextCat); });
    setTimeout(function(){ applyWebCatState(nextCat); }, 60);
  }

  function setWebCat(cat){
    resetWebTransientState();
    const nextCat = cat || webDefaultCat();
    applyWebCatState(nextCat);
    keepWebActiveCatVisible(nextCat, 'smooth');
    renderWebList();
    const list = ig$('web-list');
    if(list){
      list.style.scrollBehavior = 'auto';
      list.scrollTop = 0;
      list.style.scrollBehavior = '';
    }
    requestAnimationFrame(function(){ applyWebCatState(nextCat); });
  }
  window.setWebCat = setWebCat;

  function renderWebList(){
    const wrap = ig$('web-list');
    const empty = ig$('web-empty');
    if(!wrap || !empty) return;
    applyWebCatState(webState.curCat || webDefaultCat());
    Array.from(wrap.querySelectorAll('.web-card')).forEach(el => el.remove());
    const filtered = sortWebItemsForMyDiocese(webState.curCat==='⭐ 즐겨찾기' ? WEB_SITES.filter(s => wfHas(s.url)) : WEB_SITES.filter(s => s.cat===webState.curCat));
    const countEl = ig$('web-count');
    if(countEl) countEl.textContent = filtered.length + '개';
    empty.classList.toggle('show', filtered.length===0);
    const showProvHd = (webState.curCat === '교구');
    let lastProv = null;
    filtered.forEach(s => {
      const color = ((s.cat==='교구' || s.cat==='사제찾기') && s.prov)
        ? (WEB_PROV_COLORS[s.prov] || WEB_CAT_COLORS[s.cat] || '#555')
        : (WEB_CAT_COLORS[s.cat] || '#555');
      const bg = WEB_CAT_BG[s.cat] || '#f8f8f8';
      const isDioceseCard = (s.cat === '교구');
      const isPriestCard = (s.cat === '사제찾기');
      const isMyWebCard = isMyDioceseWebItem(s, getMyDioceseName());
      const cardClass = 'web-card' + (s.cat==='사제찾기' ? ' web-priest-card' : '') + (isMyWebCard ? ' web-my-diocese-card' : '');
      const card = document.createElement('div');
      card.className = cardClass;
      if(isDioceseCard){
        card.setAttribute('aria-label', s.name + ' 홈페이지 새창 열기');
      }
      if(isPriestCard){
        card.setAttribute('aria-label', s.name + ' 새창 열기');
      }
      const badgeText = ((s.cat==='교구' || s.cat==='사제찾기') && s.prov) ? esc(s.prov) : esc(s.cat);
      const topRight = (s.cat==='교구' || s.cat==='사제찾기') ? (isMyWebCard ? myDioceseBadgeHtml() : '') : esc(s.op);
      const cardName = webCardNameHtml(s);
      const cardDesc = s.cat==='교구' ? '교구 공식 홈페이지' : esc(s.desc);
      const icoStyle = s.cat==='사제찾기' ? 'color:' + color + ';font-weight:900;font-family:Georgia,serif' : '';
      card.innerHTML = `
        <div class="web-card-top">
          <span class="web-card-badge" style="background:${color}">${badgeText}</span>
          <span class="web-card-op">${topRight}</span>
        </div>
        <div class="web-card-body">
          <div class="web-card-ico" style="${icoStyle}">${esc(s.ico)}</div>
          <div class="web-card-info">
            <div class="web-card-name">${cardName}</div>
            <div class="web-card-desc">${cardDesc}</div>
          </div>
        </div>
        <div class="web-card-foot">
          <span class="web-card-url">${esc(shortUrl(s.url))}</span>
          <span class="web-fav-btn ${wfHas(s.url)?'on':''}" data-url="${esc(s.url)}" title="즐겨찾기">★</span>
        </div>`;
      card.addEventListener('click', function(e){
        const fb = e.target.closest('.web-fav-btn');
        if(fb){
          e.stopPropagation();
          wfToggle(s.url);
          fb.classList.toggle('on', wfHas(s.url));
          if(webState.curCat==='⭐ 즐겨찾기') renderWebList();
          return;
        }
        if(isDioceseCard){
          openExternalUrl(s.url, { module:'web' });
          return;
        }
        openExternalUrl(s.url, { module:'web' });
      });
      wrap.appendChild(card);
    });
  }

  function ensureKakaoSdk(cb){
    if(window.kakao && window.kakao.maps){
      try{ kakao.maps.load(cb); }catch(e){ cb(); }
      return;
    }
    if(window.__trailKakaoLoading){
      window.__trailKakaoQueue = window.__trailKakaoQueue || [];
      window.__trailKakaoQueue.push(cb);
      return;
    }
    window.__trailKakaoLoading = true;
    window.__trailKakaoQueue = [cb];
    const sc = document.createElement('script');
    const key = (typeof JSKEY!=='undefined' && JSKEY) ? JSKEY : '';
    sc.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=' + key + '&autoload=false';
    sc.onload = function(){
      kakao.maps.load(function(){
        const q = window.__trailKakaoQueue || [];
        window.__trailKakaoLoading = false;
        window.__trailKakaoQueue = [];
        q.forEach(fn => { try{ fn(); }catch(e){ console.warn("[가톨릭길동무]", e); } });
      });
    };
    sc.onerror = function(){
      window.__trailKakaoLoading = false;
      const el = ig$('trail-map');
      if(el) el.innerHTML = '<div style="height:100%;display:flex;align-items:center;justify-content:center;flex-direction:column;gap:10px;padding:28px;text-align:center;color:#6b7280"><div style="font-size:40px">🗺️</div><div style="font-size:15px;font-weight:700;color:#1D4ED8">지도를 불러올 수 없습니다</div><div style="font-size:12px;line-height:1.7">카카오내비 도메인 설정을 확인해 주세요.</div></div>';
    };
    document.head.appendChild(sc);
  }

  function trailMkSvg(color, big){
    const w = big ? 54 : 42, h = big ? 66 : 52;
    let s = '<svg xmlns="http://www.w3.org/2000/svg" width="'+w+'" height="'+h+'" viewBox="0 0 100 124">';
    s += '<ellipse cx="50" cy="121" rx="'+(big?24:20)+'" ry="'+(big?8:6)+'" fill="rgba(0,0,0,'+(big?0.2:0.12)+')" />';
    if(big) s += '<path d="M50 1C27 1 8 19 8 41 8 70 50 121 50 121 50 121 92 70 92 41 92 19 73 1 50 1Z" fill="white" opacity="0.25"/>';
    s += '<path d="M50 5C29.5 5 13 21.5 13 42 13 69 50 119 50 119 50 119 87 69 87 42 87 21.5 70.5 5 50 5Z" fill="'+color+'" stroke="white" stroke-width="'+(big?5:3.5)+'"/>';
    s += '<rect x="44" y="17" width="12" height="48" rx="3" fill="white"/>';
    s += '<rect x="29" y="30" width="42" height="12" rx="3" fill="white"/>';
    s += '</svg>';
    return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(s);
  }

  function trailIsPlainMapOpen(){
    try{
      var viewOpen = ig$('trail-view') && ig$('trail-view').classList.contains('open');
      var mapOn = ig$('trail-panel-map') && ig$('trail-panel-map').classList.contains('on');
      var sheetOpen = ig$('trail-sheet') && ig$('trail-sheet').classList.contains('open');
      return !!(viewOpen && mapOn && !sheetOpen && trailState.selected < 0 && !trailState.myOverlay);
    }catch(_e){ return false; }
  }

  function trailDefaultCenter(){
    return new kakao.maps.LatLng(36.10, 127.85);
  }

  function relayoutTrailMap(delay, reason){
    const wait = Number.isFinite(Number(delay)) ? Number(delay) : 0;
    const isFoldViewport = /viewport|resize|fold|orientation|settle|late|final|android-fold/i.test(String(reason || ''));
    /* V8-1-14-471: 순례길 지도 relayout은 공통 Fold 관리자 흐름에서만 보정한다. */
    setTimeout(function(){
      if(!(trailState.map && window.kakao && window.kakao.maps)){
        return;
      }
      try{
        const plain = trailIsPlainMapOpen() && isFoldViewport;
        const currentCenter = (trailState.map.getCenter ? trailState.map.getCenter() : null);
        const currentLevel = (trailState.map.getLevel ? trailState.map.getLevel() : null);
        const targetCenter = plain ? trailDefaultCenter() : currentCenter;
        const targetLevel = plain ? 13 : currentLevel;
        /* V8-1-14-471:
           순례길은 Fold 전환 때 오래된 컨테이너 폭으로 먼저 그려졌다가 중앙으로 이동해 보였다.
           지도는 잠시 숨긴 상태에서 relayout→level→center 순서로 한 번 확정하고 그 뒤에만 보인다. */
        trailState.map.relayout();
        if(targetLevel != null && typeof trailState.map.setLevel === 'function') trailState.map.setLevel(targetLevel);
        if(targetCenter && typeof trailState.map.setCenter === 'function') trailState.map.setCenter(targetCenter);
        if(isFoldViewport){
          setTimeout(function(){
            try{
              if(trailState.map){
                trailState.map.relayout();
                if(targetLevel != null && typeof trailState.map.setLevel === 'function') trailState.map.setLevel(targetLevel);
                if(targetCenter && typeof trailState.map.setCenter === 'function') trailState.map.setCenter(targetCenter);
              }
            }catch(_e){}
              }, 180);
        }
      }catch(e){ console.warn("[가톨릭길동무]", e); }
      if(trailState.markers.length !== TRAIL_ITEMS.length) syncTrailMarkers();
    }, wait);
  }

  try{ window.relayoutTrailMap = relayoutTrailMap; }catch(_e){}

  function hardResetTrailModule(){
    clearHantiRouteOverlays();
    try{ if(trailState.myOverlay) trailState.myOverlay.setMap(null); }catch(e){ console.warn("[가톨릭길동무]", e); }
    trailState.myOverlay = null;
    trailState.markers.forEach(function(marker){ try{ marker.setMap(null); }catch(e){ console.warn("[가톨릭길동무]", e); } });
    trailState.markers = [];
    if(trailState.selected >= trailState.markers.length) trailState.selected = -1;
    trailState.inited = false;
    trailState.map = null;
    trailState.pendingFitBounds = false;
    const container = ig$('trail-map');
    if(container) container.innerHTML = '';
    if(typeof closeTrailSheetOnly === 'function') closeTrailSheetOnly();
  }

  function fitTrailMapToBounds(){
    if(!(trailState.map && window.kakao && window.kakao.maps)) return;
    try{
      // V8-1-14-471:
      // setBounds는 되살리지 않고 중심 이동은 1회만 유지한다.
      // 순례길 첫 화면이 너무 확대되어 보이지 않도록 기본 줌을 한 단계 넓게 둔다.
      if(typeof trailState.map.setLevel === "function") trailState.map.setLevel(13);
      trailState.map.setCenter(trailDefaultCenter());
    }catch(e){ console.warn("[가톨릭길동무]", e); }
  }

  function syncTrailMarkers(){
    if(!(trailState.map && window.kakao && window.kakao.maps)) return;
    if(trailState.markers.length !== TRAIL_ITEMS.length){
      trailState.markers.forEach(function(marker){ try{ marker.setMap(null); }catch(e){ console.warn("[가톨릭길동무]", e); } });
      trailState.markers = [];
      TRAIL_ITEMS.forEach(function(d, i){
        const marker = new kakao.maps.Marker({
          position:new kakao.maps.LatLng(d.lat,d.lng),
          map:trailState.map,
          image:trailMarkerImg(i,false),
          zIndex:1
        });
        marker.__trailMapTarget=trailState.map;
        marker.__trailImgKey='trail:'+String(i)+':0';
        marker.__trailZIndex=1;
        kakao.maps.event.addListener(marker, 'click', function(){ trailSelectMarker(i); trailOpenSheet(i); });
        trailState.markers.push(marker);
      });
      return;
    }
    trailState.markers.forEach(function(marker, i){
      trailSetMarkerMap(marker, trailState.map);
      if(i !== trailState.selected){
        trailSetMarkerImage(marker, trailMarkerImg(i,false), 'trail:'+String(i)+':0');
        try{ if(marker.__trailZIndex!==1){ marker.setZIndex(1); marker.__trailZIndex=1; } }catch(e){ console.warn("[가톨릭길동무]", e); }
      }
    });
    if(trailState.selected >= trailState.markers.length) trailState.selected = -1;
  }

  function initTrailModule(){
    buildTrailList();
    if(trailState.inited){
      syncTrailMarkers();
      if(trailState.map){
        relayoutTrailMap(90, 'trail-init-stable');
        if(trailState.pendingFitBounds){
          setTimeout(function(){ fitTrailMapToBounds(); trailState.pendingFitBounds = false; }, 90);
        }
      }
      return;
    }
    ensureKakaoSdk(function(){
      if(trailState.inited){
        if(trailState.map){ relayoutTrailMap(90, 'trail-init-stable'); }
        return;
      }
      const container = ig$('trail-map');
      if(!container || !(window.kakao && window.kakao.maps)) return;
      trailState.map = new kakao.maps.Map(container, { center:trailDefaultCenter(), level:13 });
      trailState.trailZoomControl = new kakao.maps.ZoomControl();
      trailState.map.addControl(trailState.trailZoomControl, kakao.maps.ControlPosition.RIGHT);
      trailState.trailZoomControlVisible = true;
      if(trailState.restoreCenter && Number.isFinite(Number(trailState.restoreCenter.lat)) && Number.isFinite(Number(trailState.restoreCenter.lng))){
        try{ trailState.map.setCenter(new kakao.maps.LatLng(Number(trailState.restoreCenter.lat), Number(trailState.restoreCenter.lng))); }catch(e){ console.warn("[가톨릭길동무]", e); }
      }
      if(Number.isFinite(Number(trailState.restoreLevel))){
        try{ trailState.map.setLevel(Number(trailState.restoreLevel)); }catch(e){ console.warn("[가톨릭길동무]", e); }
      }
      trailState.restoreCenter = null;
      trailState.restoreLevel = null;
      syncTrailMarkers();
      if(trailState.pendingFitBounds){
        setTimeout(function(){ fitTrailMapToBounds(); trailState.pendingFitBounds = false; }, 80);
      }
      relayoutTrailMap(90, 'trail-map-open-stable');
      kakao.maps.event.addListener(trailState.map,'click', handleTrailMapClick);
      bindHantiRouteMapInteractionHandlers();
      trailState.inited = true;
      setTimeout(restoreHantiRouteIfActive, 120);
      if(Number.isInteger(trailState.pendingOpenIndex) && TRAIL_ITEMS[trailState.pendingOpenIndex]){
        const idx = trailState.pendingOpenIndex;
        trailState.pendingOpenIndex = null;
        setTimeout(function(){
          trailSelectMarker(idx);
          trailOpenSheet(idx);
        }, 120);
      }
    });
  }

  function getTrailItemsForList(){
    if(!Array.isArray(TRAIL_ITEMS) || TRAIL_ITEMS.length < 2) return TRAIL_ITEMS;
    var myName = getMyDioceseName();
    if(!myName) return TRAIL_ITEMS;
    return TRAIL_ITEMS.slice().sort(function(a,b){
      var aa = isMyDioceseTrailItem(a, myName) ? 0 : 1;
      var bb = isMyDioceseTrailItem(b, myName) ? 0 : 1;
      if(aa !== bb) return aa - bb;
      return TRAIL_ITEMS.indexOf(a) - TRAIL_ITEMS.indexOf(b);
    });
  }

  function buildTrailList(){
    const wrap = ig$('trail-list');
    if(!wrap) return;
    wrap.innerHTML = '';
    const countEl = ig$('trail-count');
    if(countEl) countEl.textContent = TRAIL_ITEMS.length + '개';
    const myName = getMyDioceseName();
    getTrailItemsForList().forEach(function(d){
      const card = document.createElement('div');
      const isMyTrailCard = isMyDioceseTrailItem(d, myName);
      const isHantiCard = isHantiTrailItem(d);
      card.className = 'trail-card ' + (isHantiCard ? 'trail-hanti-card' : 'trail-general-card') + (isMyTrailCard ? ' trail-my-diocese-card' : '');
      if(isHantiCard){
        card.innerHTML = `
        <div class="trail-r1">
          <span class="trail-bdg ${d.t}">${esc(d.op)}</span>
          <span class="trail-reg">📍 ${esc(d.r)}</span>
          ${isMyTrailCard ? trailMyDioceseBadgeHtml() : ''}
        </div>
        <div class="trail-r2">
          <div class="trail-ico ${d.t}">${esc(d.ico)}</div>
          <div class="trail-nm">${esc(d.n)}</div>
        </div>
        <div class="trail-foot">
          <span class="trail-url">${esc(shortUrl(d.url))}</span>
          <span class="trail-arr">›</span>
        </div>`;
      }else{
        card.innerHTML = `
        <div class="trail-general-head">
          <span class="trail-bdg ${d.t}">${esc(d.op)}</span>
          ${isMyTrailCard ? trailMyDioceseBadgeHtml() : ''}
        </div>
        <div class="trail-general-main">
          <div class="trail-ico ${d.t}">${esc(d.ico)}</div>
          <div class="trail-general-copy">
            <div class="trail-nm">${esc(d.n)}</div>
            <div class="trail-general-region">📍 ${esc(d.r)}</div>
          </div>
        </div>
        <div class="trail-general-foot">
          <span class="trail-url">${esc(shortUrl(d.url))}</span>
          <span class="trail-general-open">웹사이트 열기</span>
        </div>`;
      }
      card.addEventListener('click', function(){
        openExternalUrl(d.url, {
          module:'trail',
          view:'list',
          scroll:(ig$('trail-list')?.scrollTop||0)
        });
      });
      wrap.appendChild(card);
    });
  }

  function trailSelectMarker(i){
    if(!(trailState.map && window.kakao && window.kakao.maps)) return;
    if(trailState.selected >= 0 && trailState.markers[trailState.selected]){
      trailSetMarkerImage(trailState.markers[trailState.selected], trailMarkerImg(trailState.selected,false), 'trail:'+String(trailState.selected)+':0');
      try{ if(trailState.markers[trailState.selected].__trailZIndex!==1){ trailState.markers[trailState.selected].setZIndex(1); trailState.markers[trailState.selected].__trailZIndex=1; } }catch(e){ console.warn('[가톨릭길동무]', e); }
    }
    trailState.selected = i;
    if(trailState.markers[i]){
      trailSetMarkerImage(trailState.markers[i], trailMarkerImg(i,true), 'trail:'+String(i)+':1');
      try{ if(trailState.markers[i].__trailZIndex!==999){ trailState.markers[i].setZIndex(999); trailState.markers[i].__trailZIndex=999; } }catch(e){ console.warn('[가톨릭길동무]', e); }
    }
  }

  window.trailOpenSheet = function(i){
    const d = TRAIL_ITEMS[i];
    if(!d) return;
    const hantiSelected = isHantiTrailItem(d);
    if(hantiSelected){
      setHantiRouteActive(false);
      clearHantiRouteOverlays();
      trailState.hantiReturnTrailIndex = i;
      trailState.hantiSecretTitleReady = true;
    }else{
      setHantiRouteActive(false);
      clearHantiRouteOverlays();
      trailState.hantiSecretTitleReady = false;
    }
    ig$('trail-sh-bdg').textContent = d.op;
    ig$('trail-sh-bdg').className = 'trail-sh-bdg ' + d.t;
    ig$('trail-sh-region').textContent = '📍 ' + d.r;
    ig$('trail-sh-ico').textContent = d.ico;
    ig$('trail-sh-ico').className = 'trail-sh-ico ' + d.t;
    ig$('trail-sh-name').textContent = d.n;
    ig$('trail-sh-sub').textContent = d.op + ' · ' + d.r;
    if(hantiSelected){
      setTrailHantiNote('');
      // V8-1-14-471: 버튼과 중복되는 '홈페이지 · 전체경로보기' 안내문구는 숨긴다.
      ig$('trail-sh-url').textContent = '';
      ensureHantiMainSheetActions(d);
      setTrailGeneralSheetMode(false);
      bindHantiSecretTapTarget(ig$('trail-sh-name'));
      if(hantiTestModeEnabled()) ensureHantiTestPanel(); else removeHantiTestPanel();
    }else{
      removeTrailSheetActions();
      setTrailGeneralSheetMode(true);
      removeHantiTestPanel();
      setTrailHantiNote('');
      ig$('trail-sh-url').textContent = shortUrl(d.url);
    }
    var openLabel = document.querySelector('#trail-sh-foot .trail-sh-arr');
    if(openLabel) openLabel.textContent = hantiSelected ? '›' : '웹사이트 열기';
    const openFn = function(){
      openExternalUrl(d.url, {
        module:'trail',
        view:'map'
      });
    };
    if(hantiSelected){
      ig$('trail-sh-body').onclick = function(ev){ if(ev){ ev.preventDefault(); ev.stopPropagation(); } };
      ig$('trail-sh-foot').onclick = function(ev){ if(ev){ ev.preventDefault(); ev.stopPropagation(); } };
    }else{
      ig$('trail-sh-body').onclick = openFn;
      ig$('trail-sh-foot').onclick = openFn;
    }
    ig$('trail-sheet').classList.add('open');
    syncTrailLocButtonForSheet(true);
    if(trailState.map && window.kakao && window.kakao.maps){
      trailState.map.panTo(new kakao.maps.LatLng(d.lat,d.lng));
    }
  };

  window.trailCloseSheet = function(){
    if(trailState && trailState.hantiRouteViewMode){
      closeHantiFullRouteToSheet('trail-close-sheet-route-mode');
      return;
    }
    closeTrailSheetOnly();
    if(trailState && trailState.hantiVisible){
      setHantiRouteActive(false);
      clearHantiRouteOverlays();
    }else{
      clearHantiRouteOverlays();
    }
    trailState.hantiSecretTitleReady = false;
    removeTrailSheetActions();
    if(!(trailState.map && window.kakao && window.kakao.maps)) return;
    if(trailState.selected >= 0 && trailState.markers[trailState.selected]){
      const idx = trailState.selected;
      trailSetMarkerImage(trailState.markers[idx], trailMarkerImg(idx,false), 'trail:'+String(idx)+':0');
      try{ if(trailState.markers[idx].__trailZIndex!==1){ trailState.markers[idx].setZIndex(1); trailState.markers[idx].__trailZIndex=1; } }catch(e){ console.warn('[가톨릭길동무]', e); }
      trailState.selected = -1;
    }
  };

  window.trailSetView = function(v){
    trailState.view = v;
    ig$('trail-panel-map')?.classList.toggle('on', v==='map');
    ig$('trail-panel-list')?.classList.toggle('on', v==='list');
    ig$('trail-tab-map')?.classList.toggle('on', v==='map');
    ig$('trail-tab-list')?.classList.toggle('on', v==='list');
    try{ if(typeof window.oaiKeepActiveTabsVisible === 'function') window.oaiKeepActiveTabsVisible('trail'); }catch(e){ console.warn('[가톨릭길동무]', e); }
    if(v==='map'){
      closeTrailSheetOnly();
      initTrailModule();
      syncTrailMarkers();
      if(isHantiRouteActive()) setTimeout(restoreHantiRouteIfActive, 120);
      relayoutTrailMap(90, 'trail-set-view-stable');
    } else {
      trailCloseSheet();
      buildTrailList();
      const list = ig$('trail-list');
      if(list){
        list.style.scrollBehavior = 'auto';
        list.scrollTop = 0;
        list.style.scrollBehavior = '';
      }
    }
  };

  window.trailMyLoc = function(){
    if(!(window.navigator && navigator.geolocation)){ alert('위치 서비스를 지원하지 않습니다.'); return; }
    if(!(trailState.map && window.kakao && window.kakao.maps)){ initTrailModule(); return; }
    function show(pos){
      const lat = pos.coords.latitude, lng = pos.coords.longitude;
      const ll = new kakao.maps.LatLng(lat, lng);
      if(trailState && trailState.hantiFollowActive){
        resumeHantiAutoPanNow('my-location-button');
        centerHantiMapOnLatLng(lat, lng, {level:4, fast:true});
      }else{
        centerHantiMapOnLatLng(lat, lng, {level:(trailState.hantiVisible ? 4 : 7), fast:true});
      }
      if(trailState.myOverlay) trailState.myOverlay.setMap(null);
      const routeInfo = trailState.hantiFollowActive ? findNearestHantiRoutePointFromCoords(lat, lng, {follow:false}) : null;
      const heading = hantiResolveHeading(lat, lng, routeInfo, pos);
      const dot = buildTrailMyLocationElement(heading, !!trailState.hantiFollowActive);
      trailState.myOverlay = new kakao.maps.CustomOverlay({content:dot, position:ll, yAnchor:.5, zIndex:100});
      trailState.myOverlay.setMap(trailState.map);
      if(trailState.hantiFollowActive){
        trailState.hantiLastGpsLat = lat; trailState.hantiLastGpsLng = lng; trailState.hantiLastHeading = heading;
      }
      /* Manual 내위치 버튼은 현재 위치로 지도 중심과 마커만 이동한다.
         한티가는길의 진행 안내/오버레이는 '경로 따라가기' watchPosition 흐름에서만 표시한다. */
      if(!trailState.hantiFollowActive) clearHantiLocationGuideOverlay();
    }
    navigator.geolocation.getCurrentPosition(show, function(e){
      alert(e && e.code===1 ? '위치 권한을 허용해 주세요.' : '위치를 가져올 수 없습니다.');
    }, {enableHighAccuracy:false, timeout:5000, maximumAge:60000});
    navigator.geolocation.getCurrentPosition(show, function(){}, {enableHighAccuracy:true, timeout:12000, maximumAge:0});
  };

  document.addEventListener('DOMContentLoaded', function(){
    setWebCat(webState.curCat);
  });
})();
