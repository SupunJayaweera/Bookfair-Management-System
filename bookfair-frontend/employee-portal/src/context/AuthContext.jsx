import { createContext, useState, useContext, useEffect } from "react";
import axios from "axios";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem("employeeToken"));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    }
    setLoading(false);
  }, [token]);

  const login = async (email, password) => {
    const response = await axios.post("/api/auth/login", { email, password });
    const { token: newToken, user: userData } = response.data;

    // Check if user is employee or admin
    if (userData.role !== "EMPLOYEE" && userData.role !== "ADMIN") {
      throw new Error("Unauthorized. Employee access only.");
    }

    setToken(newToken);
    setUser(userData);
    localStorage.setItem("employeeToken", newToken);
    axios.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;

    return response.data;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem("employeeToken");
    delete axios.defaults.headers.common["Authorization"];
  };

  const value = {
    user,
    token,
    login,
    logout,
    isAuthenticated: !!token,
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
