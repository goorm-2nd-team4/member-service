import React from "react";

interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  type?: "button" | "submit";
}

const Button: React.FC<ButtonProps> = ({ children, onClick, type = "button" }) => {
  return (
    <button type={type} onClick={onClick} style={{ padding: "0.5rem 1rem", cursor: "pointer" }}>
      {children}
    </button>
  );
};

export default Button;