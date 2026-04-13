//로그아웃
export const logout = () => {
  localStorage.removeItem("token");
};