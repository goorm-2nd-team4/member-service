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

export const mockRegister = (
  name: string,
  email: string,
  password: string
) => {
  return new Promise<{ message: string }>((resolve) => {
    setTimeout(() => {
      console.log("회원가입:", { name, email, password });
      resolve({ message: "회원가입 성공" });
    }, 500);
  });
};