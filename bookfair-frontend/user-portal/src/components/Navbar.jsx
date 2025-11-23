import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Navbar = () => {
  const { logout, user } = useAuth();

  return (
    <nav className="navbar">
      <div className="navbar-content">
        <h1>🎪 Colombo International Bookfair</h1>
        <div className="navbar-links">
          <Link to="/dashboard">Dashboard</Link>
          <Link to="/stalls">Book Stalls</Link>
          <Link to="/my-reservations">My Reservations</Link>
          <span>Welcome, {user?.businessName || "User"}</span>
          <button className="btn-logout" onClick={logout}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
