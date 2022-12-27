# Likelion Final Personal Project - SNS 

## API
| 분류    |  HTTP  | URL                            |    설명     | 구현 완료 |
|-------|:------:|:-------------------------------|:---------:|:-----:|
| users |  POST  | /api/v1/users/join             |   회원가입    |  [v]  |
| users |  POST  | /api/v1/users/login            |  로그인, 토큰  |  [v]  |
| posts |  GET   | /api/v1/posts                  |  포스트 리스트  |  [v]  |
| posts |  GET   | /api/v1/posts/{postsId}        | 포스트 상세 내용 |  [v]  |
| posts |  POST  | /api/v1/posts/                 |  포스트 등록   |  [v]  |
| posts |  PUT   | /api/v1/posts/{id}             |  포스트 수정   |  [v]  |
| posts | DELETE | /api/v1/posts/{id}             |  포스트 삭제   |  [v]  |

## 도전과제
| 분류    |  HTTP  | URL                            |    설명     | 구현 완료 |
|-------|:------:|:-------------------------------|:---------:|:-----:|
| users |  POST  | /api/v1/users/{id}/role/change | 유저 역할 변경  |  [v]  |

### Admin
- ADMIN 회원은 일반 회원의 권한을 ADMIN으로 승격시킬 수 있습니다.
- ADMIM 회원은 모든 사용자의글과 댓글에 수정, 삭제를 할 수 있습니다.

## ERD
![erd2](https://user-images.githubusercontent.com/101695482/209633615-ee1af6f9-716d-4cb5-8177-10f06a7e1546.png)

## 구현 과정
### User Join & Login
https://chordplaylist.tistory.com/209

### Post
https://chordplaylist.tistory.com/215
https://chordplaylist.tistory.com/219

### Test Code
https://chordplaylist.tistory.com/224

## Config
### Swagger
https://chordplaylist.tistory.com/167

### Spring Security
https://chordplaylist.tistory.com/179

#### Encrypt Config
https://chordplaylist.tistory.com/184

### JWT(Json Web Token)
https://chordplaylist.tistory.com/188

### Auditing
https://chordplaylist.tistory.com/204

## ETC
### Response
https://chordplaylist.tistory.com/202
### Global Exception
https://chordplaylist.tistory.com/203
### JWT Exception
https://chordplaylist.tistory.com/220
### Gitlab 미러링
https://chordplaylist.tistory.com/216

## 회고
### 리팩토링
