import React from "react";

interface InputProps {
  label: string;
  type?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

const InputField: React.FC<InputProps> = ({ label, type = "text", value, onChange }) => {
  return (
    <div style={{ marginBottom: "1rem" }}>
      <label style={{ display: "block", marginBottom: "0.25rem" }}>{label}</label>
      <input type={type} value={value} onChange={onChange} style={{ padding: "0.5rem", width: "100%" }} />
    </div>
  );
};

export default InputField;