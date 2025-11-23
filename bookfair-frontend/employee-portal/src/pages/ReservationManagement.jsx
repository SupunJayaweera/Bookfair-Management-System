import { useState, useEffect } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import Navbar from "../components/Navbar";

const ReservationManagement = () => {
  const [reservations, setReservations] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredReservations, setFilteredReservations] = useState([]);

  useEffect(() => {
    fetchReservations();
  }, []);

  useEffect(() => {
    if (searchTerm) {
      const filtered = reservations.filter(
        (r) =>
          r.id.toString().includes(searchTerm) ||
          r.userId.toString().includes(searchTerm)
      );
      setFilteredReservations(filtered);
    } else {
      setFilteredReservations(reservations);
    }
  }, [searchTerm, reservations]);

  const fetchReservations = async () => {
    try {
      const response = await axios.get("/api/reservations");
      setReservations(response.data);
    } catch (error) {
      toast.error("Failed to load reservations");
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <>
      <Navbar />
      <div className="page-container">
        <div className="page-header">
          <div>
            <h2>Reservation Management</h2>
            <p>View all stall reservations</p>
          </div>
        </div>

        <div className="filters">
          <div className="filter-group">
            <label>Search Reservations</label>
            <input
              type="text"
              className="search-box"
              placeholder="Search by reservation ID or user ID..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        {filteredReservations.length === 0 ? (
          <div className="empty-state">
            <h3>No Reservations Found</h3>
            <p>
              {searchTerm
                ? "Try adjusting your search"
                : "No reservations have been made yet"}
            </p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>User ID</th>
                  <th>Stalls</th>
                  <th>Status</th>
                  <th>Reserved At</th>
                  <th>Literary Genres</th>
                  <th>QR Code</th>
                </tr>
              </thead>
              <tbody>
                {filteredReservations.map((reservation) => (
                  <tr key={reservation.id}>
                    <td>
                      <strong>#{reservation.id}</strong>
                    </td>
                    <td>User #{reservation.userId}</td>
                    <td>
                      <div
                        style={{
                          display: "flex",
                          gap: "5px",
                          flexWrap: "wrap",
                        }}
                      >
                        {Array.from(reservation.stallIds).map((stallId) => (
                          <span
                            key={stallId}
                            style={{
                              background: "#3498db",
                              color: "white",
                              padding: "3px 8px",
                              borderRadius: "10px",
                              fontSize: "12px",
                            }}
                          >
                            {stallId}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td>
                      <span
                        className={`badge badge-${reservation.status.toLowerCase()}`}
                      >
                        {reservation.status}
                      </span>
                    </td>
                    <td>{formatDate(reservation.reservedAt)}</td>
                    <td>
                      {reservation.literaryGenres &&
                      reservation.literaryGenres.length > 0 ? (
                        <div className="genres-list">
                          {Array.from(reservation.literaryGenres).map(
                            (genre, idx) => (
                              <span key={idx} className="genre-badge">
                                {genre}
                              </span>
                            )
                          )}
                        </div>
                      ) : (
                        <span style={{ color: "#999", fontSize: "12px" }}>
                          Not added
                        </span>
                      )}
                    </td>
                    <td>
                      <code
                        style={{
                          fontSize: "11px",
                          background: "#f8f9fa",
                          padding: "4px 8px",
                          borderRadius: "4px",
                        }}
                      >
                        {reservation.qrCode.substring(0, 12)}...
                      </code>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div style={{ marginTop: "20px", textAlign: "center", color: "#666" }}>
          Showing {filteredReservations.length} of {reservations.length}{" "}
          reservations
        </div>
      </div>
    </>
  );
};

export default ReservationManagement;
