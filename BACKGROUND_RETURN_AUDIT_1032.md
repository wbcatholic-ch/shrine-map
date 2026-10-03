# BACKGROUND_RETURN_AUDIT_1032

- 30분 미만: 현재 화면 유지 + GPS 갱신
- 30분 이상 일반: 커버 복귀
- 30분 이상 매일미사: 매일미사 화면 유지/복원
- 30분 이상 기도문: 기도문 즐겨찾기 목록으로 복귀
- 30분 이상 활성 순례 + 순례 관련 화면: 활성 순례하기 상세로 복귀
- 미사/기도문은 활성 순례보다 우선
- context 저장을 visibilitychange(hidden)+pagehide로 공통화
- context session/local 이중 저장 + 24시간 stale 제한
- 최근 추가 modal까지 overlay/critical 목록 최신화
- onboarding 실제 ID 사용
- background 복귀 중 Google Drive 안내 modal defer
- GPS 기록은 즉시 저장, 축하 popup만 중요 modal 종료 뒤 defer
- Android resume cycle / GPS 재확인 / external return 분리 유지
