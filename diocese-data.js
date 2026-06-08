/* ═══════════════════════════════════════════════════════════
   교구지도 데이터 로딩 점검 파일  (diocese-data.js)
   V2-175: 기존 단일 diocese-data.js를 다음 두 파일로 분리했습니다.
   - diocese-meta.js   : 교구 기본 정보 / 관구 매핑 / 아이콘
   - diocese-search.js : 지역 검색 / 중복지역 / 동명이역 데이터

   이 파일은 새 데이터를 다시 정의하지 않고, 로딩 상태만 점검합니다.
   기존 diocese.html 함수명·뒤로가기·외부홈페이지 복귀 로직은 변경하지 않았습니다.
═══════════════════════════════════════════════════════════ */
(function(){
  'use strict';
  try{
    var required=['DIOCESE_META','OVERLAP_SUMMARY','REGION_OVERLAP_PLAIN','NAME_SAME','SEARCH_DB','GW_OF','DIOCESE_ICO'];
    var missing=[];
    required.forEach(function(name){
      try{
        if(typeof eval(name)==='undefined') missing.push(name);
      }catch(e){ missing.push(name); }
    });
    if(missing.length){
      console.warn('[가톨릭길동무] 교구지도 데이터 로딩 누락:', missing.join(', '));
    }
  }catch(e){
    console.warn('[가톨릭길동무] 교구지도 데이터 점검 실패', e);
  }
})();
