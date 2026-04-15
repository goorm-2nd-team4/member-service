import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { logout } from "../utils/auth";
import Button from "../components/Button";

interface Member {
  id: number;
  email: string;
  name: string;
  role: string;
}

const Members: React.FC = () => {
  const [members, setMembers] = useState<Member[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    const fetchMembers = async () => {
      try {
        const res = await api.get("/api/members");
        setMembers(res.data.data);
      } catch (err) {
        console.error("데이터 로딩 실패", err);
      }
    };

    fetchMembers();
  }, [navigate]);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div style={{ padding: "2rem", maxWidth: "800px", margin: "0 auto" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "2rem" }}>
        <h1>회원 관리 시스템 (Admin)</h1>
        <Button onClick={handleLogout}>로그아웃</Button>
      </div>

      <table style={{ width: "100%", borderCollapse: "collapse", backgroundColor: "white", borderRadius: "8px", overflow: "hidden", boxShadow: "0 4px 6px rgba(0,0,0,0.1)" }}>
        <thead style={{ backgroundColor: "#007aff", color: "white" }}>
          <tr>
            <th style={{ padding: "12px", textAlign: "left" }}>ID</th>
            <th style={{ padding: "12px", textAlign: "left" }}>이메일</th>
            <th style={{ padding: "12px", textAlign: "left" }}>성함</th>
          </tr>
        </thead>
        <tbody>
          {members.length > 0 ? (
            members.map((m) => (
              <tr key={m.id} style={{ borderBottom: "1px solid #eee" }}>
                <td style={{ padding: "12px" }}>{m.id}</td>
                <td style={{ padding: "12px" }}>{m.email}</td>
                <td style={{ padding: "12px" }}>{m.name}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={4} style={{ padding: "20px", textAlign: "center" }}>등록된 회원이 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default Members;