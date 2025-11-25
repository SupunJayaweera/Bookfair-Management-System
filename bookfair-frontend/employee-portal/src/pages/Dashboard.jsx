import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Navbar from "../components/Navbar";

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalStalls: 0,
    availableStalls: 0,
    reservedStalls: 0,
    totalReservations: 0,
  });
  const navigate = useNavigate();

  const handleNavigation = (e, path) => {
    e.target.classList.add('btn-glow');
    setTimeout(() => {
      navigate(path);
    }, 500);
  };

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const [stallsRes, reservationsRes] = await Promise.all([
        axios.get("/api/stalls"),
        axios.get("/api/reservations"),
      ]);

      const stalls = stallsRes.data;
      const reservations = reservationsRes.data;

      setStats({
        totalStalls: stalls.length,
        availableStalls: stalls.filter((s) => s.available).length,
        reservedStalls: stalls.filter((s) => !s.available).length,
        totalReservations: reservations.length,
      });
    } catch (error) {
      console.error("Failed to fetch stats", error);
    }
  };

  return (
    <>
      <Navbar />
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h2>Exhibition Management Dashboard</h2>
          <p>Monitor stall availability and reservations</p>
        </div>

        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-value">{stats.totalStalls}</div>
            <div className="stat-label">Total Stalls</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{stats.availableStalls}</div>
            <div className="stat-label">Available Stalls</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{stats.reservedStalls}</div>
            <div className="stat-label">Reserved Stalls</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{stats.totalReservations}</div>
            <div className="stat-label">Total Reservations</div>
          </div>
        </div>

        <div className="dashboard-cards">
          <div className="dashboard-card">
            <h3>Stall Management</h3>
            <p>
              View all stalls, check availability status, and initialize stall
              data for the exhibition venue.
            </p>
            <button
              className="btn-secondary"
              onClick={(e) => handleNavigation(e, "/stalls")}
            >
              Manage Stalls
            </button>
          </div>

          <div className="dashboard-card">
            <h3>Reservation Tracking</h3>
            <p>
              Monitor all reservations made by vendors and publishers. View
              detailed information about each reservation.
            </p>
            <button
              className="btn-secondary"
              onClick={(e) => handleNavigation(e, "/reservations")}
            >
              View Reservations
            </button>
          </div>

          <div className="dashboard-card">
            <h3>Reports & Analytics</h3>
            <p>
              Generate reports on stall utilization, revenue projections, and
              exhibitor demographics.
            </p>
            <button className="btn-secondary" disabled>
              Coming Soon
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default Dashboard;
