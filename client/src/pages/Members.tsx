//로그인 확인 임시 회원관리페이지
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Members = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
    }
  }, [navigate]);

  return <h1>회원관리 페이지 (로그인 성공)</h1>;
};

export default Members;