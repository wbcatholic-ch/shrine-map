# GitHub 업로드 보안 안내

## 현재 앱 버전

V38

## GitHub에 올려도 되는 파일

이 압축파일 안의 앱 파일은 GitHub 업로드용으로 정리되어 있습니다.

## GitHub에 올리면 안 되는 파일

아래 파일은 있으면 절대 올리지 마세요.

```text
config.js
.env
.env.*
serviceAccount.json
firebase-service-account.json
service-account.json
private-key.json
admin-key.json
secret.json
token.json
github-token.txt
netlify-token.txt
*.pem
*.p12
*.key
*.crt
```

## 정리된 보안 상태

- 공개 앱 코드에 `KAKAO_REST_KEY` 없음
- 카카오 REST API는 Cloudflare Worker를 통해 호출
- JavaScript Key는 지도 SDK용으로 앱에 남아 있으며, 카카오 개발자센터에서 도메인 제한 필요
- 문의·건의 관리자 비밀번호는 공개 앱 코드에서 제거된 상태
