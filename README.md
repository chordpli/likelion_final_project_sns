# Likelion Final Personal Project - SNS 

## 프로젝트 기술스택

- 자바 : JAVA 11
- 개발 툴 : SpringBoot 2.7.6
- 필수 라이브러리 : SpringBoot Web, MySQL, Spring Data JPA, Lombok, Spring Security, JWT, Swagger
- 빌드 : Gradle 7.4
- DB : MySql, Reids
- CLOUD : AWS EC2
- CI/CD : Docker, GitLab

## API 체크리스트
### [SWAGGER](http://ec2-3-37-127-126.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/)

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
![erd2](https://user-images.githubusercontent.com/101695482/211044249-472720f4-c0a1-42fd-a8f5-e885deba9da1.png){: width="30%" height="30%"}

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
### [배포 부터 CI-CD](https://chordplaylist.tistory.com/201)
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

## 1주차 미션 회고
### 리팩토링
#### 전체
- 어느 부분에서 예외가 발생할지 모르기 때문에, 기존에 배운 부분 외에도 더 조건을 걸어야 하는 부분이 뭐가 있을까?
#### Post
- 수정 메서드의 경우 Setter를 사용 후 Save를 하는 것이 최선일까?
- 삭제 메서드의 경우 postId를 매개변수로 넣는 것이 좋을까, post Entity를 매개변수로 넣는 것이 좋을까?
#### Test Code
- 테스트 코드를 다양하게 사용할 순 없을까?
- 
## 2주차 미션 요약 구현 과정
### [개요](https://chordplaylist.tistory.com/232)
### Comment
#### [Comment 작성 및 수정 / Test](https://chordplaylist.tistory.com/231)
- 처음, builder로 update 로직을 구현했을 때 createdAt의 정보가 null로 받아지는 현상으로 인해, 많은 고민을 하게 되었다. 다시 setter로 수정하여, createdAt의 정보를 받아올 수 있었으나, set 메서드를 사용하며 발생하는 찝찝함을 지울 수 없었다. 회고 팀의 정보 공유로 @Modifying에 대해 공부하여 쿼리를 직접 날리고, 모든 정보를 이상 없이 받아오는 로직을 완성했다.
#### [Comment 조회 및 삭제 / Test](https://chordplaylist.tistory.com/234)
### [Like 좋아요 누르기, 좋아요 개수 Count](https://chordplaylist.tistory.com/235)
- 좋아요를 다시 눌렀을 때 예외 처리가 발생하는 것이 아닌, 좋아요를 취소할 수 있도록 로직을 수정하였다.
  - 그로 인해 발생하는 중복 메서드들을 따로 분리하여 재사용성을 증가시켰다.
### [Alarm 기능 개발](https://chordplaylist.tistory.com/238)
- Comment와 Like가 작성되고, 다시 삭제되었을 때 해당 Alarm을 다시 삭제할 수 있도록 설계하였다.
  - 회고 팀과 인스타그램으로 실험해 보았다.
- Comment를 삭제했을 때 특정 Comment Alarm만 삭제하기 위해 comment Id를 매개변수로 삭제할 수 있는 메서드를 추가했다.
  - 해당 로직을 사용하지 않으면 특정 유저의 모든 Comment Alarm이 삭제된다.
### [JPA Soft Delete 구현](https://chordplaylist.tistory.com/240)
- @SQLDelete의 사용법, @Where의 사용법에 대해 공부하는 시간을 가질 수 있었다.
- 우리가 하는 과제의 경우에는 DeletedAt을 사용하여 Delete를 할 때 현재 시간이 해당 컬럼에 저장되는 방법으로 구현하였다.
  - 타 글들을 찾아보면 boolean으로 체크하는 경우가 있는데 개인적으로 시간을 넣는 방법이 추후에 정렬하거나, 특정 데이터를 뽑아낼 때 유리할 것으로 보이기 때문에 선호한다.

## 2주차 미션 중 이슈 정리
### [MySQL 예약어 이슈](https://chordplaylist.tistory.com/236)
### [테스트 코드 생성자 이슈](https://chordplaylist.tistory.com/233)
- 한 4-5시간은 해당 이슈로 인해 고민했던 것 같다. Controller 분리로 해결되면서 실마리를 찾았던 이슈였다.
- 컨트롤러 필드에 존재하는 클래스들이 테스트 코드에도 그대로 있어야 한다는 걸.. 생각하지 못했다.
### [MySQL CURRENT_TIMESTAMP 시간 이슈](https://chordplaylist.tistory.com/241)
### [JPA @OneToMany, cascade, orphanRemoval 이슈](https://chordplaylist.tistory.com/242)
- 삭제만 신경 쓰면서 Modify를 잊었던 큰 실수. Casecade, orphanRemoval을 사용하기 위해 1:N N:1로 연결하였으므로, Save를 할 때 수정된 Post 내용뿐 아니라 Post에 있는 Likes와 Comments도 잊지 말고 연결시켜 주어야 했다. 
### [REDIS 설치](https://chordplaylist.tistory.com/243)
- Docker의 Ubuntu에 설치하고서 Spring Redis host 주소를 local로 작성하여 에러가 계속 발생했었다.
  - EC2의 보안 그룹에서 REDIS port를 뚫은 뒤 host주 소에 EC2 DNS를 입력하니 이상 없이 연결되었다.

## 2주차 미션 회고
### 리팩토링
- comment 수정 후, DB로부터 다시 정보를 받아오기 위한 로직이 깔끔하지 않다.
#### 전체
- Refresh-Token을 사용해 보고 싶다. 재발급 받는 로직을 프론트에서 api를 날려야 하는 건지, Token filter에서 해결해야 하는 건지 모르겠다.
- 
