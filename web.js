
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
  /* V8-1-14-454: 순례길 지도 확대/축소 체감 개선을 위해 마커 이미지를 캐시하고
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
      (trailState.hantiStampOverlays || []).forEach(function(ov){ try{ ov.setMap(null); }catch(_e){} });
      clearHantiLocationGuideOverlay();
      removeHantiTestPanel();
    }catch(e){ console.warn('[가톨릭길동무]', e); }
    trailState.hantiPolylines = [];
    trailState.hantiStampOverlays = [];
    trailState.hantiVisible = false;
    trailState.hantiActiveRouteData = null;
    trailState.hantiActiveRouteId = '';
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
  function closeTrailSheetOnly(){
    try{ ig$('trail-sheet')?.classList.remove('open'); }catch(e){ console.warn('[가톨릭길동무]', e); }
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
    var isTestLoop = activeData && activeData.id === 'dowon_test_loop';
    if(region) region.textContent = isTestLoop ? '🧪 도원동 테스트 지점' : '📍 한티가는길 스탬프';
    if(ico){ ico.textContent = stamp.role === 'finish' ? '🏁' : (stamp.role === 'start' ? '⛪' : (isTestLoop ? '📍' : '✝️')); ico.className = 'trail-sh-ico d'; }
    if(name) name.textContent = stamp.name || '';
    if(sub) sub.textContent = isTestLoop ? 'GPX 따라가기 테스트 · 실제 기록 저장 없음' : ((stamp.en || '') + (stamp.role === 'finish' ? ' · 완료 지점' : ''));
    ensureHantiTestPanel();
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
    return !!(trailState && trailState.hantiTestUnlocked) || hantiUrlTestModeEnabled();
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
      if(!(trailState && trailState.hantiVisible)) return;
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
    removeHantiTestPanel();
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
    var done = nearest.stamp.role === 'finish' ? ' · 5-5 완료 지점' : '';
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
  function activateHantiTestRoute(routeId){
    var data = routeId === 'dowon' ? getDowonTestRouteData() : getHantiRouteData();
    if(!(data && trailState && trailState.map && window.kakao && kakao.maps)) return;
    showHantiRouteOverlays(data);
    if(routeId === 'dowon'){
      setTrailHantiNote('도원동 동네 테스트 루프입니다. 실제 순례기록은 저장하지 않고, 내 위치가 GPX 선을 따라가는지만 확인합니다.');
    }else{
      setTrailHantiNote('동명읍 안에서는 여러 길로 동명성당에 도착할 수 있습니다. 표시된 경로선은 참고용입니다.');
    }
    fitHantiRouteBounds(data);
    ensureHantiTestPanel();
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
      '<div class="hanti-test-help">숨은 테스트입니다. 실제 순례기록은 저장하지 않습니다.</div>' +
      '<div class="hanti-test-switch" aria-label="테스트 경로 선택">' +
      '<button id="trail-hanti-test-route-hanti" type="button" class="' + (activeId === 'hanti' ? 'on' : '') + '">한티가는길</button>' +
      '<button id="trail-hanti-test-route-dowon" type="button" class="' + (activeId === 'dowon_test_loop' ? 'on' : '') + '">도원동 테스트 루프</button>' +
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
    var label = (nearest.stamp.id || '') + ' ' + (nearest.stamp.name || '');
    var dist = hantiFormatDistance(nearest.distanceM);
    var judge = hantiAutoStampJudgement(nearest);
    if(opts.test){
      setTrailHantiNote('테스트 위치 기준 가까운 스탬프: ' + label + (dist ? ' · 약 ' + dist : '') + ' / 자동도장 판정: ' + (judge.ok ? '가능' : '불가') + ' / 실제 기록 저장 안 함');
      updateHantiTestResult(opts.sourceStamp, nearest);
    }else{
      setTrailHantiNote('현재 위치 기준 가까운 스탬프: ' + label + (dist ? ' · 약 ' + dist : '') + ' / 자동도장은 아직 꺼져 있습니다.');
    }
    clearHantiLocationGuideOverlay();
    var el = document.createElement('div');
    el.className = 'hanti-location-guide' + (opts.test ? ' test' : '');
    el.innerHTML = '<strong>' + (opts.test ? '테스트 위치' : '가까운 스탬프') + '</strong><span>' + esc(label) + (dist ? ' · 약 ' + esc(dist) : '') + '</span>';
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
    bindHantiSecretTapTarget(note);
  }
  function showHantiRouteOverlays(routeData){
    var data = routeData || getHantiRouteData();
    if(!(data && trailState.map && window.kakao && kakao.maps)) return;
    clearHantiRouteOverlays();
    try{
      /* V8-1-14-454: 원본 GPX segment를 강제 병합하지 않고 그대로 그린다.
         여러 segment를 하나로 합치면 없는 길이 직선처럼 이어질 수 있으므로, 테스트 루프도 같은 방식으로 표시한다. */
      var isTestLoop = data.id === 'dowon_test_loop';
      (data.routeSegments || []).forEach(function(seg){
        var path = hantiLatLngPathFromPoints(seg && seg.points);
        if(path.length > 1){
          drawHantiPolyline(path, {
            weight: isTestLoop ? 4 : 5,
            color: isTestLoop ? '#2E7D32' : '#B7791F',
            opacity: isTestLoop ? .9 : .86,
            zIndex: isTestLoop ? 34 : 30
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
      trailState.hantiActiveRouteData = data;
      trailState.hantiActiveRouteId = data.id || 'hanti';
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
  const trailState = {inited:false, map:null, markers:[], selected:-1, myOverlay:null, view:'map', pendingOpenIndex:null, restoreCenter:null, restoreLevel:null, needsHardReset:false, pendingFitBounds:false, hantiPolylines:[], hantiStampOverlays:[], hantiVisible:false, hantiLocationOverlay:null, hantiRouteActive:false, hantiActiveRouteData:null, hantiActiveRouteId:'', hantiTestUnlocked:false, hantiTestClosed:false, hantiTestTapCount:0, hantiTestTapStartedAt:0};
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
  function saveReturnState(state){
    try{ sessionStorage.setItem(RETURN_KEY, JSON.stringify(state)); }catch(e){ console.warn("[가톨릭길동무]", e); }
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

    if(state && state.module === 'trail'){
      try{
        state.view = trailState.view || state.view || 'map';
        state.scroll = ig$('trail-list') ? (ig$('trail-list').scrollTop || 0) : (state.scroll || 0);
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

    if(state.module === 'web'){
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
            list.style.scrollBehavior='';
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
      const icoBg = '#F5F0E8';
      const icoStyle = 'background:' + icoBg + (s.cat==='사제찾기' ? ';color:' + color + ';font-weight:900;font-family:Georgia,serif' : '');
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
    /* V8-1-14-454: 순례길 지도 relayout은 공통 Fold 관리자 흐름에서만 보정한다. */
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
        /* V8-1-14-454:
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
      // V8-1-14-454:
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
      trailState.map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);
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
      card.className = 'trail-card' + (isMyTrailCard ? ' trail-my-diocese-card' : '');
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
      setHantiRouteActive(true);
      showHantiRouteOverlays();
    }else{
      setHantiRouteActive(false);
      clearHantiRouteOverlays();
    }
    ig$('trail-sh-bdg').textContent = d.op;
    ig$('trail-sh-bdg').className = 'trail-sh-bdg ' + d.t;
    ig$('trail-sh-region').textContent = '📍 ' + d.r;
    ig$('trail-sh-ico').textContent = d.ico;
    ig$('trail-sh-ico').className = 'trail-sh-ico ' + d.t;
    ig$('trail-sh-name').textContent = d.n;
    ig$('trail-sh-sub').textContent = d.op + ' · ' + d.r;
    if(hantiSelected){
      setTrailHantiNote('동명읍 안에서는 여러 길로 동명성당에 도착할 수 있습니다. 표시된 경로선은 참고용입니다.');
      ensureHantiTestPanel();
      ig$('trail-sh-url').textContent = '공식 홈페이지 열기';
    }else{
      removeHantiTestPanel();
      setTrailHantiNote('');
      ig$('trail-sh-url').textContent = shortUrl(d.url);
    }
    const openFn = function(){
      openExternalUrl(d.url, {
        module:'trail',
        view:'map'
      });
    };
    ig$('trail-sh-body').onclick = openFn;
    ig$('trail-sh-foot').onclick = openFn;
    ig$('trail-sheet').classList.add('open');
    if(trailState.map && window.kakao && window.kakao.maps){
      if(hantiSelected){
        setTimeout(function(){ fitHantiRouteBounds(); }, 80);
      }else{
        trailState.map.panTo(new kakao.maps.LatLng(d.lat,d.lng));
      }
    }
  };

  window.trailCloseSheet = function(){
    closeTrailSheetOnly();
    if(trailState && trailState.hantiVisible){
      setHantiRouteActive(false);
      clearHantiRouteOverlays();
    }else{
      clearHantiRouteOverlays();
    }
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
      setTimeout(restoreHantiRouteIfActive, 120);
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
      const ll = new kakao.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
      trailState.map.panTo(ll); trailState.map.setLevel(7);
      if(trailState.myOverlay) trailState.myOverlay.setMap(null);
      const dot = document.createElement('div');
      dot.className = 'trail-myloc';
      trailState.myOverlay = new kakao.maps.CustomOverlay({content:dot, position:ll, yAnchor:.5, zIndex:100});
      trailState.myOverlay.setMap(trailState.map);
      showHantiLocationGuide(pos.coords.latitude, pos.coords.longitude);
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
