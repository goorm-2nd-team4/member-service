# 🔐 Auth (Login / Register) 구현 (전성우)

## 📌 작업 범위

* 로그인 (`/login`)
* 회원가입 (`/register`)
* 인증 토큰 저장
* API 구조 구성 (Mock 기반)

---

## 📁 프로젝트 구조

src
 ├── api
 │   ├── axios.ts        # API 설정 (baseURL, interceptor)
 │   └── mock.ts         # Mock API (현재 테스트용)
 ├── components
 │   ├── Button.tsx
 │   ├── InputField.tsx
 │   └── Header.tsx
 ├── pages
 │   ├── Login.tsx       # 로그인 페이지
 │   ├── Register.tsx    # 회원가입 페이지
 │   └── Members.tsx     # (임시) 로그인 후 이동 페이지
 ├── utils
 │   └── auth.ts         # 인증 유틸 (logout 등)
```

---

## 🔑 인증 흐름

### ✔ 로그인

1. 이메일 / 비밀번호 입력
2. Mock API 호출 (`mockLogin`)
3. 성공 시:

   * localStorage에 token 저장
   * `/members` 이동

---

### ✔ 회원가입

1. 입력값 검증 (비밀번호 일치)
2. Mock API 호출 (`mockRegister`)
3. 성공 시 `/login` 이동

---

## 🧠 토큰 처리 구조

### ✔ 저장

```id="tok123"
localStorage.setItem("token", token);
```

### ✔ 자동 포함 (axios)

```id="tok456"
Authorization: Bearer {token}
```

👉 `axios.ts`에서 interceptor로 자동 처리됨

---

## 🚪 로그아웃

```ts id="logout123"
import { logout } from "../utils/auth";

logout();
```

👉 Members 페이지에서 사용하면 됨

---

## ⚙️ Mock → 실제 API 변경 방법

```id="change123"
// 현재
mockLogin()

// 변경
api.post("/auth/login")
```

👉 이 부분만 바꾸면 백엔드 연결 완료

---

## 🧪 테스트 계정

```id="test123"
email: test@test.com
password: 1234
```

---

## ⚠️ 협업 주의사항

* `/members` 페이지는 다른 팀원 담당
* 로그인 성공 시 이동만 처리되어 있음
* 인증 로직 (token, interceptor)은 공통 사용

---

## 🎯 팀원이 작업할 때

### 📌 Members 페이지에서 해야 할 것

```ts id="member123"
import { logout } from "../utils/auth";

const handleLogout = () => {
  logout();
  navigate("/login");
};
```

---

## 💡 요약

* 로그인/회원가입 → 완성 상태
* 인증 흐름 → 구축 완료
* Mock → 실제 API로 쉽게 교체 가능
