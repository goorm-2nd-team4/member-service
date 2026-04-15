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
  const [newName, setNewName] = useState("");
  const [newEmail, setNewEmail] = useState("");

  const [editingId, setEditingId] = useState<number | null>(null);
  const [editName, setEditName] = useState("");

  const navigate = useNavigate();

  const fetchMembers = async () => {
    try {
      const res = await api.get("/api/members");
      setMembers(res.data.data); 
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      navigate("/login");
      return;
    }

    fetchMembers();
  }, [navigate]);

  // 이메일 형식 체크
  const validateEmail = (email: string) => {
    return String(email).toLowerCase().match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/);
  };

  const handleCreate = async () => {
    if (!newName.trim() || !newEmail.trim()) {
      return alert("이름과 이메일을 입력해주세요.");
    }

    if (!validateEmail(newEmail)) {
      return alert("올바른 이메일 형식을 입력해주세요.");
    }

    try {
      await api.post("/api/members", {
        name: newName,
        email: newEmail,
        password: "1234",
      });

      setNewName("");
      setNewEmail("");

      fetchMembers();
    } catch {
      alert("회원 등록 실패");
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
  };

  const handleUpdate = async (id: number) => {
    if (!editName.trim()) return alert("수정할 이름 입력");

    try {
      await api.put(`/api/members/${id}`, {
        name: editName,
      });

      setEditingId(null);
      fetchMembers();
    } catch {
      alert("수정 실패");
    }
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="members-page">
      <div className="members-header">
        <h2>🛡️ 회원 관리 시스템</h2>
        {/* 로그아웃 버튼 스타일 적용 */}
        <button onClick={handleLogout} className="btn-logout">로그아웃</button>
      </div>

      <div className="members-card">
        <div className="form-row">
          <input
            placeholder="이름"
            value={newName}
            onChange={e => setNewName(e.target.value)}
          />
          <input
            placeholder="이메일"
            value={newEmail}
            onChange={e => setNewEmail(e.target.value)}
          />
          <button onClick={handleCreate}>추가</button>
        </div>
      </div>

      <div className="members-card table-card">
        <table className="members-table">
          <thead>
            <tr>
              <th className="col-id">ID</th>
              <th className="col-name">이름</th>
              <th className="col-email">이메일</th>
              <th className="col-action">관리</th>
            </tr>
          </thead>

          <tbody>
            {members.map(m => (
              <tr key={m.id}>
                <td>{m.id}</td>
                <td>
                  {editingId === m.id ? (
                    <input
                      className="edit-input"
                      value={editName}
                      onChange={e => setEditName(e.target.value)}
                      autoFocus
                    />
                  ) : (
                    <strong>{m.name}</strong>
                  )}
                </td>
                <td>{m.email}</td>
                <td>
                  {editingId === m.id ? (
                    <>
                      <button className="btn-save" onClick={() => handleUpdate(m.id)}>저장</button>
                      <button className="btn-cancel" onClick={() => setEditingId(null)}>취소</button>
                    </>
                  ) : (
                    <>
                      <button className="btn-edit" onClick={() => startEdit(m)}>수정</button>
                      <button className="btn-delete" onClick={() => handleDelete(m.id)}>삭제</button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Members;