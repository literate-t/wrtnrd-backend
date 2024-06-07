# 토이 프로젝트 Wrtnrd의 백엔드(API 서버)
- Wrtnrd는 Write and read의 약자로 말 그대로 쓰고 읽는다는 의미
## 사용 기술
### spring-boot-starter: 3.2.3
### spring-security-core: 6.2.2
### hibernate-core:6.4.4.Final
### postgresql: 42.6.1
### slf4j: 2.0.12
### jjwt: 0.11.5(jwt 발급 라이브러리)
## 인증
- `JWT` 사용
- `JWT` 정보는 쿠키에 포함
  - `HttpOnly`, `Secure`
### 사용 이유
- `JWT`을 직접 다루면서 인증 필터 커스터마이징 연습
## TODO
- 배포
  - `github action`
- 관리자 모드
