import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/axios"; 
import Button from "../components/Button";
import InputField from "../components/InputField";

const Register: React.FC = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();

    // ✅ (유지) 프론트 기본 validation
    if (!name.trim()) {
      setError("성명을 입력해주세요.");
      return;
    }

    if (password !== confirmPassword) {
      setError("비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      setError("");

      await api.post("/api/auth/register", {
        name,
        email,
        password,
        confirmPassword,
      });

      navigate("/login"); 
    } catch (err) {
      setError("회원가입 실패");
      console.error(err);
    }
  };

  return (
    <div className="auth-container">
      <h2>회원가입</h2>

      <form onSubmit={handleRegister}>
        <InputField
          label="성명"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

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

        <InputField
          label="비밀번호 확인"
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />

        <p className="input-error">{error || "\u00A0"}</p>

        <Button type="submit">회원가입</Button>
      </form>

      <p className="auth-footer">
        이미 계정이 있나요? <Link to="/login">로그인</Link>
      </p>
    </div>
  );
};

export default Register;