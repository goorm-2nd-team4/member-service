# Member Service Backend Plan

## 1. Goal

- This project will use a separated structure:
  - React client: screen rendering, routing, form state, API calls
  - Spring Boot backend: member domain logic, validation, DB access, JSON API responses
- Backend should expose REST APIs for the React app instead of rendering Thymeleaf pages.
- Existing MVC template controllers can remain temporarily, but new development should target the REST API layer.

## 2. Recommended Backend Package Structure

```text
src/main/java/com/goorm/membership
├── api
│   └── member
│       └── MemberApiController
├── config
│   └── WebConfig
├── dto
│   ├── common
│   │   └── ApiResponse
│   └── member
│       ├── request
│       │   ├── LoginRequest
│       │   └── RegisterRequest
│       └── response
│           ├── LoginResponse
│           ├── MemberDetailResponse
│           └── MemberSummaryResponse
├── exception
│   ├── DuplicateEmailException
│   ├── GlobalExceptionHandler
│   ├── InvalidPasswordException
│   └── MemberNotFoundException
├── Model
│   ├── Member
│   └── Role
├── repository
│   └── MemberRepository
└── service
    └── MemberService
```

## 3. Class List

### Existing classes reused

- `Model.Member`
  - Member entity
- `Model.Role`
  - Member role enum
- `repository.MemberRepository`
  - JPA repository for member persistence
- `service.MemberService`
  - Member business logic
- `exception.DuplicateEmailException`
- `exception.InvalidPasswordException`
- `exception.MemberNotFoundException`

### New classes to add

- `api.member.MemberApiController`
  - REST endpoints for register, login, member list, member detail
- `dto.common.ApiResponse`
  - Common JSON response wrapper
- `dto.member.request.RegisterRequest`
  - Request body for register API
- `dto.member.request.LoginRequest`
  - Request body for login API
- `dto.member.response.MemberSummaryResponse`
  - Member list item response
- `dto.member.response.MemberDetailResponse`
  - Member detail response
- `dto.member.response.LoginResponse`
  - Login success response
- `exception.GlobalExceptionHandler`
  - Validation and business exception response mapping
- `config.WebConfig`
  - CORS config for React client

## 4. API Specification

### 4.1 Register

- Method: `POST`
- URL: `/api/members/register`
- Request

```json
{
  "email": "goorm@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "name": "구름"
}
```

- Success response: `201 Created`

```json
{
  "message": "회원가입 성공",
  "data": {
    "id": 1,
    "email": "goorm@example.com",
    "name": "구름",
    "role": "USER"
  }
}
```

### 4.2 Login

- Method: `POST`
- URL: `/api/members/login`
- Request

```json
{
  "email": "goorm@example.com",
  "password": "password123"
}
```

- Success response: `200 OK`

```json
{
  "message": "로그인 성공",
  "data": {
    "id": 1,
    "email": "goorm@example.com",
    "name": "구름",
    "role": "USER"
  }
}
```

### 4.3 Member list

- Method: `GET`
- URL: `/api/members`
- Success response: `200 OK`

```json
{
  "message": "회원 목록 조회 성공",
  "data": [
    {
      "id": 1,
      "email": "goorm@example.com",
      "name": "구름",
      "role": "USER"
    }
  ]
}
```

### 4.4 Member detail

- Method: `GET`
- URL: `/api/members/{id}`
- Success response: `200 OK`

```json
{
  "message": "회원 상세 조회 성공",
  "data": {
    "id": 1,
    "email": "goorm@example.com",
    "name": "구름",
    "role": "USER"
  }
}
```

## 5. Error Response Rule

All API errors should use a consistent JSON shape.

```json
{
  "message": "이미 사용 중인 이메일입니다.",
  "data": null
}
```

Validation errors should also return field-specific messages.

```json
{
  "message": "입력값이 올바르지 않습니다.",
  "data": {
    "email": "형식이 올바르지 않습니다.",
    "password": "비밀번호는 필수값입니다."
  }
}
```

## 6. DB Fields

`member`

- `id` bigint PK auto increment
- `email` varchar(100) not null unique
- `password` varchar(255) not null
- `name` varchar(50) not null
- `role` varchar(20) not null default `USER`

## 7. Work Order

1. Define REST package structure and response policy
2. Add request/response DTOs
3. Extend service methods for REST use cases
4. Add `MemberApiController`
5. Add global exception handling
6. Add CORS config for React local server
7. Add tests
8. Connect React request/response fields

## 8. Notes for Team Coordination

- Frontend should call only `/api/**` endpoints.
- Template pages such as `/join`, `/login`, `/members` are legacy MVC flow and should not be used by React.
- Password encryption and JWT can be added later by the auth/security owner.
- Before security is added, login API should focus on:
  - finding a member by email
  - checking password match
  - returning success or failure response
