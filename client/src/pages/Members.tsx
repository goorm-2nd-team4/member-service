import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { logout } from "../utils/auth";
import Button from "../components/Button";

interface Member {
  id: number;
  email: string;
  name: string;
  age: number;
}

const Members: React.FC = () => {
  const [members, setMembers] = useState<Member[]>([]);
  const [newName, setNewName] = useState("");
  const [newEmail, setNewEmail] = useState("");
  const [newAge, setNewAge] = useState("");

  const [editingId, setEditingId] = useState<number | null>(null);
  const [editName, setEditName] = useState("");
  const [editAge, setEditAge] = useState("");

  const navigate = useNavigate();

  const fetchMembers = async () => {
    try {
      const res = await api.get("/api/members");
      setMembers(res.data);
    } catch (err) { console.error(err); }
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) { navigate("/login"); return; }
    fetchMembers();
  }, [navigate]);

  // 이메일 형식 체크 함수
  const validateEmail = (email: string) => {
    return String(email).toLowerCase().match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/);
  };

  const handleCreate = async () => {
    // 1. 모든 필드 입력 여부 체크 (AND 조건)
    if (!newName.trim() || !newEmail.trim() || !newAge) {
      return alert("이름, 이메일, 나이를 모두 정확히 입력해주세요.");
    }
    // 2. 이메일 형식 체크
    if (!validateEmail(newEmail)) {
      return alert("올바른 이메일 형식(example@abc.com)을 입력해주세요.");
    }

    try {
      await api.post("/api/members", {
        name: newName,
        email: newEmail,
        age: Number(newAge),
        password: "1234"
      });
      setNewName(""); setNewEmail(""); setNewAge("");
      fetchMembers();
    } catch (err: any) {
      alert("회원 등록에 실패했습니다. 데이터를 다시 확인하세요.");
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    await api.delete(`/api/members/${id}`);
    fetchMembers();
  };

  const startEdit = (m: Member) => {
    setEditingId(m.id);
    setEditName(m.name);
    setEditAge(String(m.age));
  };

  const handleUpdate = async (id: number) => {
    if (!editName.trim() || !editAge) return alert("수정할 내용을 입력하세요.");
    try {
      await api.put(`/api/members/${id}`, { name: editName, age: Number(editAge) });
      setEditingId(null);
      fetchMembers();
    } catch (err) { alert("수정 실패!"); }
  };

  const handleLogout = () => { logout(); navigate("/login"); };

  return (
    <div style={{ padding: "20px", display: "flex", flexDirection: "column", height: "100vh", backgroundColor: "#f1f3f5", fontFamily: "sans-serif" }}>

      {/* 상단 헤더 - 고정 */}
      <div style={{ maxWidth: "1000px", width: "100%", margin: "0 auto", backgroundColor: "white", padding: "20px", borderRadius: "15px", boxShadow: "0 4px 12px rgba(0,0,0,0.1)", marginBottom: "20px" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
          <h2 style={{ color: "#007aff", margin: 0 }}>🛡️ 회원 관리 시스템 (Admin)</h2>
          <Button onClick={handleLogout}>로그아웃</Button>
        </div>

        {/* 등록 폼 */}
        <div style={{ display: "flex", gap: "10px", alignItems: "center", padding: "10px", backgroundColor: "#f8f9fa", borderRadius: "10px" }}>
          <input placeholder="이름" value={newName} onChange={e => setNewName(e.target.value)} style={{ flex: 1, padding: "10px", borderRadius: "8px", border: "1px solid #ddd" }}/>
          <input placeholder="이메일" value={newEmail} onChange={e => setNewEmail(e.target.value)} style={{ flex: 2, padding: "10px", borderRadius: "8px", border: "1px solid #ddd" }}/>
          <input placeholder="나이" type="number" value={newAge} onChange={e => setNewAge(e.target.value)} style={{ width: "80px", padding: "10px", borderRadius: "8px", border: "1px solid #ddd" }}/>
          <button onClick={handleCreate} style={{ padding: "10px 20px", backgroundColor: "#007aff", color: "white", border: "none", borderRadius: "8px", cursor: "pointer", fontWeight: "bold" }}>추가</button>
        </div>
      </div>

      {/* 테이블 영역 - 여기만 스크롤 됨 */}
      <div style={{ maxWidth: "1000px", width: "100%", margin: "0 auto", backgroundColor: "white", borderRadius: "15px", boxShadow: "0 4px 12px rgba(0,0,0,0.1)", flex: 1, overflowY: "auto", padding: "20px", color: "#333" }}>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead style={{ position: "sticky", top: 0, backgroundColor: "white", zIndex: 1 }}>
            <tr style={{ borderBottom: "2px solid #007aff", textAlign: "left", color: "#333" }}>
              <th style={{ padding: "15px" }}>ID</th>
              <th style={{ padding: "15px" }}>이름</th>
              <th style={{ padding: "15px" }}>이메일</th>
              <th style={{ padding: "15px" }}>나이</th>
              <th style={{ padding: "15px" }}>관리</th>
            </tr>
          </thead>
          <tbody>
            {members.length > 0 ? (
              members.map(m => (
                <tr key={m.id} style={{ borderBottom: "1px solid #eee" }}>
                  <td style={{ padding: "15px" }}>{m.id}</td>
                  <td style={{ padding: "15px" }}>
                    {editingId === m.id ?
                      <input value={editName} onChange={e => setEditName(e.target.value)} style={{ padding: "5px", width: "100%" }}/> :
                      <strong>{m.name}</strong>
                    }
                  </td>
                  <td style={{ padding: "15px", color: "#666" }}>{m.email}</td>
                  <td style={{ padding: "15px" }}>
                    {editingId === m.id ?
                      <input type="number" value={editAge} onChange={e => setEditAge(e.target.value)} style={{ padding: "5px", width: "60px" }}/> :
                      `${m.age}세`
                    }
                  </td>
                  <td style={{ padding: "15px" }}>
                    {editingId === m.id ? (
                      <>
                        <button onClick={() => handleUpdate(m.id)} style={{ marginRight: "5px", cursor: "pointer" }}>💾</button>
                        <button onClick={() => setEditingId(null)} style={{ cursor: "pointer" }}>❌</button>
                      </>
                    ) : (
                      <>
                        <button onClick={() => startEdit(m)} style={{ marginRight: "10px", color: "#007aff", border: "none", background: "none", cursor: "pointer" }}>수정</button>
                        <button onClick={() => handleDelete(m.id)} style={{ color: "#ff4d4f", border: "none", background: "none", cursor: "pointer" }}>삭제</button>
                      </>
                    )}
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={5} style={{ padding: "40px", textAlign: "center", color: "#999" }}>등록된 회원이 없습니다.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Members;