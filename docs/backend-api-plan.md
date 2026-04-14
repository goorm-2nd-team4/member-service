# 회원 API 문서

이 문서는 현재 코드 기준으로 동작하는 회원 API만 정리한다.

## 공통 사항

- Base URL: `/api`
- 응답 형식은 모두 아래 구조를 사용한다.

```json
{
  "message": "응답 메시지",
  "data": {}
}
```

## 1. 회원가입

- 메서드: `POST`
- URL: `/api/auth/register`

### 요청 본문

```json
{
  "email": "goorm@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "name": "구름"
}
```

### 성공 응답

- 상태 코드: `201 Created`

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

### 요청 검증

- `email`: 필수, 이메일 형식
- `password`: 필수
- `confirmPassword`: 필수
- `name`: 필수
- `password`와 `confirmPassword`는 일치해야 한다.

### 실패 응답 예시

- 상태 코드: `400 Bad Request`

```json
{
  "message": "입력값이 올바르지 않습니다.",
  "data": {
    "email": "형식이 올바르지 않습니다.",
    "name": "이름은 필수입니다."
  }
}
```

- 상태 코드: `409 Conflict`

```json
{
  "message": "이미 사용 중인 이메일입니다.",
  "data": null
}
```

## 2. 로그인

- 메서드: `POST`
- URL: `/api/auth/login`

### 요청 본문

```json
{
  "email": "goorm@example.com",
  "password": "password123"
}
```

### 성공 응답

- 상태 코드: `200 OK`

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

### 요청 검증

- `email`: 필수, 이메일 형식
- `password`: 필수

### 실패 응답 예시

- 상태 코드: `400 Bad Request`

```json
{
  "message": "입력값이 올바르지 않습니다.",
  "data": {
    "email": "형식이 올바르지 않습니다."
  }
}
```

- 상태 코드: `401 Unauthorized`

```json
{
  "message": "비밀번호가 일치하지 않습니다.",
  "data": null
}
```

- 상태 코드: `404 Not Found`

```json
{
  "message": "존재하지 않는 회원입니다.",
  "data": null
}
```

## 3. 회원 목록 조회

- 메서드: `GET`
- URL: `/api/members`

### 성공 응답

- 상태 코드: `200 OK`

```json
{
  "message": "회원 목록 조회 성공",
  "data": [
    {
      "id": 1,
      "email": "goorm@example.com",
      "name": "구름",
      "role": "USER"
    },
    {
      "id": 2,
      "email": "admin@example.com",
      "name": "관리자",
      "role": "ADMIN"
    }
  ]
}
```

## 4. 비고

- 현재 활성화된 회원 API : 회원가입, 로그인, 회원 목록 조회
- (회원 상세 조회 API는 현재  동작하지 않음.)
