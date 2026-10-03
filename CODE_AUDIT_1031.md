# CODE AUDIT 1031 — 설정/순례 하단 UI 정리

## 범위
- 기준: V8-1-14-1030 전체본
- 전체 파일: 84개
- 설정 관련 문자열/코드가 발견된 파일: 9개

## 설정 관련 파일
- index.html (90 refs)
- privacy.html (6 refs)
- web.js (3 refs)
- style.css (136 refs)
- qa-firebase.html (2 refs)
- app.js (118 refs)
- prayer-data.js (7 refs)
- js/myfaith.js (18 refs)
- js/back-controller.js (6 refs)

## 실제 설정 UI 핵심 구현
- index.html: 설정/기록/본당 선택 UI 마크업
- app.js: 설정 열기/닫기, 본당·교구, 백업/복원, 자주 가는 장소, 이벤트 처리
- style.css: 설정 화면 및 자주 가는 장소 UI
- js/back-controller.js: 설정/서브모달 뒤로가기 판정
- js/myfaith.js: 설정과 연동되는 신앙생활 화면 일부
- 나머지 파일의 '설정' 표기는 문구/도움말/개별 기능 설정 참조이며 중복 설정 UI 구현이 아님

## 제거/통합한 중복
1. `.oai-settings-panel`
   - 기본 overflow:auto
   - 후행 overflow-x 숨김
   - CLEANUP-1017 overflow-y/overflow-x
   세 군데를 기본 rule 한 곳으로 통합.
2. `.oai-settings-head`
   - 기본 header 디자인과 후행 sticky header patch를 한 rule로 통합.
3. `.oai-settings-group`
   - 기본 테두리/그림자와 후행 색상 patch를 한 rule로 통합.
4. `설정 > 자주 가는 장소`
   - V844 구형 디자인 block 제거.
   - V845 단일 디자인만 유지.
5. `.oai-settings-panel::-webkit-scrollbar`
   - 동일 rule 중복 제거, 한 곳만 유지.

## 순례하기 하단 정렬
- `순례코스 보기`의 과거 `margin-top:9px!important` 제거.
- `내 위치에서 다시 거리 계산`과 `순례코스 보기`를 동일 44px, margin 0, stretch로 통일.
- 1030의 Fold patch는 제거하고 1031 최종 rule 하나로 재작성.
- Fold 2열은 `minmax(0,1fr)` 두 칸으로 유지.

## 구조 검사
- index.html 중복 ID: 0개
- app.js 중복 named function declaration: 14개
- 정리 후 동일 context의 settings selector 중복: 0개

### 중복 ID
{}

### 중복 함수
{
  "now": 3,
  "isNativeAndroid": 3,
  "finish": 8,
  "close": 4,
  "done": 2,
  "run": 4,
  "showModal": 2,
  "hideModal": 2,
  "add": 2,
  "finishOne": 2,
  "stillCurrent": 2,
  "ok": 2,
  "apply": 3,
  "moveDrag": 2
}

### 남은 동일-context settings selector 중복
- 없음
