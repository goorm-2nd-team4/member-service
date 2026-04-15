import { useState } from "react";
import { useNavigate, Link } from "react-router-dom"; 
import Button from "../components/Button";
import InputField from "../components/InputField";
import api from "../api/axios";

const Login: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      setError("");

      const res = await api.post("/api/auth/login", {
        email,
        password,
      });

      localStorage.setItem("token", res.data.data.token);
      navigate("/members"); 

    } catch (err: any) {
      console.error(err);

      const status = err.response?.status;

      if (status === 401) {
        setError("비밀번호가 일치하지 않습니다.");
      } else if (status === 404) {
        setError("존재하지 않는 회원입니다.");
      } else {
        setError("로그인 실패");
      }
    }
  };

  return (
    <div className="auth-container">
      <h2>로그인</h2>
      <form onSubmit={handleLogin}>
        <InputField 
          label="이메일" 
          value={email} 
          onChange={(e) => setEmail(e.target.value)} 
        />

        <InputField 
          label="비밀번호" 
          type="password" 
          value={password} 
          onChange={(e) => setPassword(e.target.value)} 
        />

        <p className="input-error">{error || "\u00A0"}</p>

        <Button type="submit">로그인</Button>
      </form>

      <p className="auth-footer">
        계정이 없으신가요? <Link to="/register">회원가입</Link>
      </p>
    </div>
  );
};

export default Login;