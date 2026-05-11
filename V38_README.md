# 가톨릭길동무 V38 정리본

## 이번 패키지 정리 목적

이전 압축파일 안에 이전 버전/이전 버전 이름이 들어간 안내 파일과 Worker 파일이 함께 있어 혼란이 생길 수 있어서 정리했습니다.

## 정리 기준

- 앱 본체 버전: V38
- Worker 파일명: `cloudflare_worker_kakao_rest_proxy.js`
- Worker 안내: `WORKER_INSTALL_GUIDE.md`
- GitHub 업로드 보안 안내: `GITHUB_UPLOAD_SECURITY_NOTE.md`

## 주의

Worker 코드는 앱 버전과 별도입니다.  
앱이 V39, V40으로 올라가도 Worker 코드가 바뀌지 않으면 Worker를 다시 배포할 필요 없습니다.
