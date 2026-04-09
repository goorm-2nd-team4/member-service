export const mockLogin = (email: string, password: string) => {
  return new Promise<{ token: string }>((resolve, reject) => {
    setTimeout(() => {
      if (email === "test@test.com" && password === "1234") {
        resolve({ token: "mock-token-123" });
      } else {
        reject("이메일 또는 비밀번호가 올바르지 않습니다.");
      }
    }, 500);
  });
};

export const mockRegister = (email: string, password: string) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ message: "회원가입 성공" });
    }, 500);
  });
};