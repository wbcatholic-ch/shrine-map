# R2 사진 목록 자동 연동 설정

앱이 `photos.json`을 읽어 새 사진을 자동 반영하려면 R2 버킷의 CORS 규칙에 아래 설정이 필요합니다.

```json
[
  {
    "AllowedOrigins": ["*"],
    "AllowedMethods": ["GET", "HEAD"],
    "AllowedHeaders": ["*"],
    "ExposeHeaders": [],
    "MaxAgeSeconds": 3600
  }
]
```

사진 파일은 이미 공개 주소로 표시되지만, 이 설정이 있어야 앱이 사진 순서와 세로 사진 여부가 담긴 `photos.json`도 읽을 수 있습니다.
