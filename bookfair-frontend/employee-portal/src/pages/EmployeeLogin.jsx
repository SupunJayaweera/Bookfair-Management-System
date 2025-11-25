import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import toast from "react-hot-toast";

const EmployeeLogin = () => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await login(formData.email, formData.password);
      toast.success("Login successful!");
      navigate("/dashboard");
    } catch (error) {
      toast.error(
        error.message || "Login failed. Please check your credentials."
      );
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="employee-badge">🔐 EMPLOYEE PORTAL ACCESS</div>
        <h1>Bookfair Management System</h1>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Employee Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="employee@bookfair.lk"
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>
          <button type="submit" className="btn-primary">
            Employee Login
          </button>
        </form>
        <p
          style={{
            marginTop: "20px",
            textAlign: "center",
            color: "#666",
            fontSize: "14px",
          }}
        >
          This portal is restricted to authorized exhibition organizers only.
        </p>
      </div>
    </div>
  );
};

export default EmployeeLogin;
