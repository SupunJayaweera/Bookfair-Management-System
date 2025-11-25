import { useState, useEffect } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import Navbar from "../components/Navbar";

const MyReservations = () => {
  const [reservations, setReservations] = useState([]);
  const [selectedReservation, setSelectedReservation] = useState(null);
  const [genres, setGenres] = useState("");
  const [showGenreModal, setShowGenreModal] = useState(false);

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    try {
      const response = await axios.get("/api/reservations/my");
      setReservations(response.data);
    } catch (error) {
      toast.error("Failed to load reservations");
    }
  };

  const handleAddGenres = async () => {
    try {
      const genreList = genres
        .split(",")
        .map((g) => g.trim())
        .filter((g) => g);
      await axios.post(`/api/reservations/${selectedReservation.id}/genres`, {
        genres: genreList,
      });
      toast.success("Genres added successfully");
      setShowGenreModal(false);
      setGenres("");
      fetchReservations();
    } catch (error) {
      toast.error("Failed to add genres");
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <>
      <Navbar />
      <div className="reservations-container">
        <div className="dashboard-header">
          <h2>My Reservations</h2>
          <p>View and manage your stall reservations</p>
        </div>

        {reservations.length === 0 ? (
          <div
            style={{
              textAlign: "center",
              padding: "60px",
              background: "white",
              borderRadius: "10px",
            }}
          >
            <h3>No Reservations Yet</h3>
            <p style={{ marginTop: "15px", color: "#666" }}>
              You haven't made any reservations. Visit the stalls page to book
              your space.
            </p>
          </div>
        ) : (
          <div className="reservations-list">
            {reservations.map((reservation) => (
              <div key={reservation.id} className="reservation-card">
                <div className="reservation-header">
                  <div className="reservation-id">
                    Reservation #{reservation.id}
                  </div>
                  <span className="status-badge status-confirmed">
                    {reservation.status}
                  </span>
                </div>

                <div className="reservation-details">
                  <div className="detail-row">
                    <strong>Stalls:</strong>
                    <span>{reservation.stallIds.join(", ")}</span>
                  </div>
                  <div className="detail-row">
                    <strong>Reserved On:</strong>
                    <span>{formatDate(reservation.reservedAt)}</span>
                  </div>
                  {reservation.literaryGenres &&
                    reservation.literaryGenres.length > 0 && (
                      <div className="detail-row">
                        <strong>Literary Genres:</strong>
                        <span>{reservation.literaryGenres.join(", ")}</span>
                      </div>
                    )}
                </div>

                <div className="qr-code-section">
                  <h4>QR Code Entry Pass</h4>
                  <p
                    style={{
                      marginTop: "10px",
                      color: "#666",
                      fontSize: "14px",
                    }}
                  >
                    QR Code: <strong>{reservation.qrCode}</strong>
                  </p>
                  <p
                    style={{
                      marginTop: "10px",
                      color: "#666",
                      fontSize: "12px",
                    }}
                  >
                    Please check your email for the downloadable QR code image
                  </p>
                </div>

                <button
                  className="btn-secondary"
                  style={{ marginTop: "15px", width: "100%" }}
                  onClick={() => {
                    setSelectedReservation(reservation);
                    setShowGenreModal(true);
                  }}
                >
                  {reservation.literaryGenres &&
                  reservation.literaryGenres.length > 0
                    ? "Update Literary Genres"
                    : "Add Literary Genres"}
                </button>
              </div>
            ))}
          </div>
        )}

        {showGenreModal && (
          <div
            className="modal-overlay"
            onClick={() => setShowGenreModal(false)}
          >
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h3>Add Literary Genres</h3>
              </div>
              <p style={{ marginBottom: "15px", color: "#666" }}>
                Enter the literary genres you will be displaying/selling
                (comma-separated)
              </p>
              <input
                type="text"
                placeholder="e.g., Fiction, Non-Fiction, Children's Books, History"
                value={genres}
                onChange={(e) => setGenres(e.target.value)}
                style={{ marginBottom: "20px" }}
              />
              <div className="modal-actions">
                <button
                  className="btn-cancel"
                  onClick={() => setShowGenreModal(false)}
                >
                  Cancel
                </button>
                <button className="btn-confirm" onClick={handleAddGenres}>
                  Add Genres
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default MyReservations;
