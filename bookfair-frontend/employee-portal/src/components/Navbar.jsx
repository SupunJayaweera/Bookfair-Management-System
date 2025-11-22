import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Navbar = () => {
  const { logout, user } = useAuth();

  return (
    <nav className="navbar">
      <div className="navbar-content">
        <h1>🏢 Employee Portal - Bookfair Management</h1>
        <div className="navbar-links">
          <Link to="/dashboard">Dashboard</Link>
          <Link to="/stalls">Stall Management</Link>
          <Link to="/reservations">Reservations</Link>
          <span>Employee: {user?.contactPerson || "Admin"}</span>
          <button className="btn-logout" onClick={logout}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
