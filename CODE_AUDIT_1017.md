# V8-1-14-1017 코드 정리 보고

이번 버전은 기능 추가보다 구조 정리를 우선했습니다.

## 정리한 항목

### 1. 스크롤 안내 중복 제거
- route 전용 화살표 DOM 삭제
- route 전용 상/하단 페이드 CSS 삭제
- 순례 planner 전용 화살표/페이드/헤더 그림자/하단 막대 중복 CSS 삭제
- route 전용 observer와 순례 planner 전용 observer를 제거하고 공통 manager로 통합
- 위/아래 표시 디자인을 52×5px 동일 막대로 통일

### 2. 공통 스크롤 적용 범위
- 일반 sheet body
- 검색 결과
- 성지/성당/피정 목록
- 지도 정보카드
- 길찾기 다중 경유지
- 길찾기 결과
- Fold 길찾기
- 순례완료/순례계획/순례하기
- 설정
- 기도문 목록/상세
- 가톨릭 웹사이트
- 순례길
- 스탬프북
- 안내 목록
- 외부 미사 fallback panel
- 관구·교구 iframe 내부 목록/검색/관구 정보

※ 외부 도메인의 매일미사 iframe 본문 자체는 브라우저 보안상 부모 앱에서 내부 scrollTop을 읽을 수 없으므로 공통 막대의 정확한 상/하단 판정 대상에서 제외합니다.

### 3. 뒤로가기 상태 판정 중복 제거
- back-controller.js의 hasOpenAppSurface / hasVisibleAppLayer가 같은 화면 목록을 각각 관리하던 구조를 hasKnownOpenLayer 한 곳으로 통합

### 4. 빌드 버전 관리 방식 수정
- 과거 패치 주석의 버전 문자열을 일괄 치환하지 않음
- 실제 실행에 필요한 build/query/cache/version 값만 갱신

## 유지한 동작
- 진행 중 순례는 한 코스만
- 미사/기도문의 순례 복귀 버튼은 active follow가 있을 때만 표시
- 미사/기도문 순례 복귀 버튼은 독립 floating layer 유지
- 지도에서 추가 초기 scrollTop=0 / +경유지 추가 시 아래 자동 스크롤
- 설정 헤더 sticky
- 순례코스 보기 / 다음 순례지 길찾기 / 카카오내비
