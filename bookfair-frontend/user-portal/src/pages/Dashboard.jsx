import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { useAuth } from "../context/AuthContext";

const Dashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  return (
    <>
      <Navbar />
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h2>Welcome to Colombo International Bookfair</h2>
          <p>Manage your stall reservations and exhibition details</p>
        </div>

        <div className="dashboard-cards">
          <div className="dashboard-card">
            <h3>📚 Book a Stall</h3>
            <p>
              Browse available stalls and reserve your space at the exhibition.
              View the interactive venue map and select up to 3 stalls.
            </p>
            <button
              className="btn-secondary"
              onClick={() => navigate("/stalls")}
            >
              View Available Stalls
            </button>
          </div>

          <div className="dashboard-card">
            <h3>📋 My Reservations</h3>
            <p>
              View all your current reservations, download QR codes, and manage
              literary genres for your stalls.
            </p>
            <button
              className="btn-secondary"
              onClick={() => navigate("/my-reservations")}
            >
              View My Reservations
            </button>
          </div>

          <div className="dashboard-card">
            <h3>🏢 Business Profile</h3>
            <p>
              <strong>Business:</strong> {user?.businessName}
              <br />
              <strong>Contact:</strong> {user?.contactPerson}
              <br />
              <strong>Email:</strong> {user?.email}
            </p>
          </div>
        </div>

        <div
          style={{
            marginTop: "40px",
            padding: "30px",
            background: "white",
            borderRadius: "10px",
          }}
        >
          <h3>Exhibition Information</h3>
          <p style={{ marginTop: "15px", lineHeight: "1.8", color: "#666" }}>
            The Colombo International Bookfair is the largest book fair and
            exhibition in Sri Lanka. Reserve your stall today to showcase your
            literary works to thousands of book enthusiasts.
          </p>
          <ul style={{ marginTop: "20px", lineHeight: "2", color: "#666" }}>
            <li>✓ Stalls available in Small, Medium, and Large sizes</li>
            <li>✓ Maximum 3 stalls per business</li>
            <li>✓ QR code entry pass for exhibition access</li>
            <li>✓ Add literary genres to your reservation</li>
          </ul>
        </div>
      </div>
    </>
  );
};

export default Dashboard;
