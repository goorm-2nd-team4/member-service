import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { mockLogin } from "../api/mock"; 
import Button from "../components/Button";
import InputField from "../components/InputField";

const Login: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setError("");

      const res = await mockLogin(email, password); 

      localStorage.setItem("token", res.token);
      navigate("/members"); // 회원관리 페이지 이동
    } catch (err: any) {
      setError(err);
    }
  };

  return (
    <div className="auth-container">
      <h2>로그인</h2>
      <form onSubmit={handleLogin}>
        <InputField label="이메일" value={email} onChange={(e) => setEmail(e.target.value)} />
        <InputField label="비밀번호" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />

        {error && <p className="input-error">{error}</p>}

        <Button type="submit">로그인</Button>
      </form>

      <p className="auth-footer">
        계정이 없으신가요? <Link to="/register">회원가입</Link>
      </p>
    </div>
  );
};

export default Login;