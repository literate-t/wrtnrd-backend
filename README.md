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
  - 도메인이 같은 경우 쿠키를 통해 전달 `HttpOnly`, `Secure`
  - 지금 배포 환경에서는 프론트엔드와 백엔드가 도메인이 다르기 때문에 `Authorization` 헤더를 사용한다
### 사용 이유
- `JWT`을 직접 다루면서 인증 필터 커스터마이징 연습
## TODO
- 배포
  - `koyeb` 무료 버전(속도가 느림)
- 관리자 모드
