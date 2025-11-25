import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import logo from "../assets/bookfair_logo.png";

const Navbar = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();

  const handleLogout = (e) => {
    e.target.classList.add('btn-glow');
    setTimeout(() => {
      logout();
    }, 500);
  };

  return (
    <nav className="navbar">
      <div className="navbar-content">
        <div className="navbar-brand">
          <img 
            src={logo} 
            alt="Bookfair Logo" 
            className="navbar-logo" 
            onClick={() => navigate('/dashboard')}
            style={{ cursor: 'pointer' }}
          />
          <h1>Employee Portal - Bookfair Management</h1>
        </div>
        <div className="navbar-links">
          <Link to="/dashboard">Dashboard</Link>
          <Link to="/stalls">Stall Management</Link>
          <Link to="/reservations">Reservations</Link>
          <span className="navbar-user">
            <svg 
              width="20" 
              height="20" 
              viewBox="0 0 16 16" 
              fill="currentColor"
              style={{ marginRight: '6px', verticalAlign: 'middle' }}
            >
              <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10z"/>
            </svg>
            {user?.contactPerson || "Admin"}
          </span>
          <button className="btn-logout" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
