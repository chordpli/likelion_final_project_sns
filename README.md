# Likelion Final Personal Project - SNS 

## API 체크리스트
http://ec2-3-37-127-126.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/

| 분류      |  HTTP  | URL                                      |    설명     | 구현 완료 |
|---------|:------:|:-----------------------------------------|:---------:|:-----:|
| users   |  POST  | /api/v1/users/join                       |   회원가입    |  [v]  |
| users   |  POST  | /api/v1/users/login                      |  로그인, 토큰  |  [v]  |
| posts   |  GET   | /api/v1/posts                            |  포스트 리스트  |  [v]  |
| posts   |  GET   | /api/v1/posts/{postsId}                  | 포스트 상세 내용 |  [v]  |
| posts   |  POST  | /api/v1/posts/                           |  포스트 등록   |  [v]  |
| posts   |  PUT   | /api/v1/posts/{id}                       |  포스트 수정   |  [v]  |
| posts   | DELETE | /api/v1/posts/{id}                       |  포스트 삭제   |  [v]  |
| comment |  GET   | /api/v1/posts/{postId}/comments[?page=0] |   댓글 조회   |  [v]  |
| comment |  POST  | /api/v1/posts/{postId}/comments          |   댓글 작성   |  [v]  |
| comment |  PUT   | /api/v1/posts/{postId}/comments/{id}     |   댓글 수정   |  [v]  |
| comment | DELETE | /api/v1/posts/{postId}/comments/{id}     |   댓글 삭제   |  [v]  |
| likes   |  POST  | /api/v1/posts/{postId}/likes             |  좋아요 누르기  |  [v]  |
| likes   |  GET   | /api/v1/posts/{postId}/likes             | 좋아요 개수 확인 |  [v]  |
| my feed |  GET   | /api/v1/posts/my                         | 마이 피드 조회  |  [v]  |
| alarm   |  GET   | /api/v1/alarms                           |  알람 리스트   |  [v]  |

## 도전과제

### Admin
| 분류    |  HTTP  | URL                            |    설명     | 구현 완료 |
|-------|:------:|:-------------------------------|:---------:|:-----:|
| users |  POST  | /api/v1/users/{id}/role/change | 유저 역할 변경  |  [v]  |
- ADMIN 회원은 일반 회원의 권한을 ADMIN으로 승격시킬 수 있습니다.
- ADMIM 회원은 모든 사용자의글과 댓글에 수정, 삭제를 할 수 있습니다.

### UI
- 회원 가입 구현 완료 </br>
http://ec2-3-37-127-126.ap-northeast-2.compute.amazonaws.com:8080/

## ERD
![erd2](https://user-images.githubusercontent.com/101695482/209633615-ee1af6f9-716d-4cb5-8177-10f06a7e1546.png){: width="30%" height="30%"}

## 1주차 미션 요약 구현 과정
### [개요](https://chordplaylist.tistory.com/198)

### [User Join & Login](https://chordplaylist.tistory.com/209)
- 테이블을 제공받고 공통된 컬럼을 BaseEntity로 분할하였다.
  - 날짜를 자동 입력하게 해야 하므로 Auditing을 구현하였다.
    - Auditing에 대해 자료를 찾다 보면 MainApplication에 @EnableJpaAuditing을 붙이는 경우가 있어서 무슨 차이인지 찾아보았다.
      - 테스트 코드 작성 시 @WebMvcTest와의 충돌이 발생할 수 있으므로, 따로 config 클래스를 만들어주는 것이 좋다.
- Request DTO, Response DTO를 왜 만드는지, 어떻게 사용해야 하는지 알게 됨.
- JwtExceptio 처리를 진행하면서 효율적인 JwtFilter가 무엇인지 고민하고 수정함.

### Post
#### [Post 작성, 단건 조회, 포스트 리스트](https://chordplaylist.tistory.com/215)
#### [Post 수정, 삭제](https://chordplaylist.tistory.com/219)
- ERD를 확인하고 1:N의 관계를 엔티티 클래스에서 어떻게 연결해야 하는지에 대해 고민을 많이 했다.
  - User(1) : N(Post)이므로 Post Entity에 @ManyToOne을 사용하여 User와 연결하였다.
    - 외래 키는 항상 다(N) 쪽에 존재하며, 연관관계의 주인이 된다.
- LocalDateTime을 사용하면서 DB에 소수점 아래의 시간까지 저장되어서 yyyy-MM-dd hh:mm:ss으로 저장하기 위해 많은 시간을 쏟았다. 정답은 Entity가 아닌 dto에서 정보를 풀러올 때 포맷을 해야 했다.

### [Test Code](https://chordplaylist.tistory.com/224)
- 모든 테스트 코드가 난관이었다. 이전에 했던 모든 프로젝트를 다 열어서 하나하나 작성했다. 
- 테스트 코드만 약 1000줄가량 작성하게 되었는데, 작성한 코드 내에 어노테이션, Junit과 관련된 메서드에 대해 익숙해졌다.

## Config
### 배포 부터 CI-CD
https://chordplaylist.tistory.com/201
- 가장 막막했던 첫 날 작업.
  - 배포 스크립트 작성, 크론탭 설정이 가장 복잡했으나 하나하나 다시 실습하고 블로그 작성하여 수월하게 작성할 수 있는 수준으로 끌어올림.
    - EC2 프리티어를 사용했지만, 계속 DB가 내려가는 현상이 발생하여 EC2 t3.small로 다시 생성 후 배포 스크립트 작성, 크론탭 설정 완료.

### [Swagger 적용](https://chordplaylist.tistory.com/167)
### [Spring Security 적용](https://chordplaylist.tistory.com/179)
#### [Encrypt Config 적용](https://chordplaylist.tistory.com/184)
### [JWT(Json Web Token) 적용](https://chordplaylist.tistory.com/188)
- JWT에서 발생하는 에러의 경우 DispatcherServlet 부분 이전의 Filter에서 처리된다는 것을 알고 예외 발생 처리를 하는 것이 힘들었으나, 덕분에 Spring MVC 환경에 대해서 더 공부하게 되었다.
### [Auditing 적용](https://chordplaylist.tistory.com/204)

## ETC
### [Custom Response 적용](https://chordplaylist.tistory.com/202)
### [Global Exception 적용](https://chordplaylist.tistory.com/203)
### [JWT Exception 적용](https://chordplaylist.tistory.com/220)
### [Gitlab 미러링 적용](https://chordplaylist.tistory.com/216)
### [Docker Image Tage \<NONE> 삭제 자동화](https://chordplaylist.tistory.com/210)

## 2주차 미션 요약 구현 과정
### [개요](https://chordplaylist.tistory.com/232)


## 2주차 미션 중 이슈 정리


## 회고
### 리팩토링
#### 전체
- 어느 부분에서 예외가 발생할지 모르기 때문에, 기존에 배운 부분 외에도 더 조건을 걸어야 하는 부분이 뭐가 있을까?
#### Post
- 수정 메서드의 경우 Setter를 사용 후 Save를 하는 것이 최선일까?
- 삭제 메서드의 경우 postId를 매개변수로 넣는 것이 좋을까, post Entity를 매개변수로 넣는 것이 좋을까?
#### Test Code
- 테스트 코드를 다양하게 사용할 순 없을까?