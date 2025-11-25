import { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";
import "./VerifyReservation.css";

function VerifyReservation() {
  const [searchParams] = useSearchParams();
  const [reservation, setReservation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const verifyReservation = async () => {
      const token = searchParams.get("token");
      const rid = searchParams.get("rid");

      if (!token || !rid) {
        setError("Invalid QR code. Missing required parameters.");
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get(`/api/verify-reservation`, {
          params: { token, rid },
        });
        setReservation(response.data);
        setLoading(false);
      } catch (err) {
        console.error("Verification error:", err);
        setError(
          err.response?.data?.error ||
            "Failed to verify reservation. Please try again."
        );
        setLoading(false);
      }
    };

    verifyReservation();
  }, [searchParams]);

  if (loading) {
    return (
      <div className="verify-container">
        <div className="verify-card">
          <div className="loading-spinner"></div>
          <h2>Verifying Reservation...</h2>
          <p>Please wait while we verify your QR code</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="verify-container">
        <div className="verify-card error-card">
          <div className="error-icon">❌</div>
          <h2>Verification Failed</h2>
          <p className="error-message">{error}</p>
          <button onClick={() => navigate("/")} className="btn-primary">
            Go to Home
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="verify-container">
      <div className="verify-card success-card">
        <div className="success-icon">✅</div>
        <h1>Reservation Verified</h1>
        <p className="subtitle">This reservation is valid</p>

        <div className="reservation-details">
          <div className="detail-section">
            <h3>Reservation Information</h3>
            <div className="detail-row">
              <span className="label">Reservation ID:</span>
              <span className="value">#{reservation.id}</span>
            </div>
            <div className="detail-row">
              <span className="label">User ID:</span>
              <span className="value">#{reservation.userId}</span>
            </div>
            <div className="detail-row">
              <span className="label">Status:</span>
              <span
                className={`badge badge-${reservation.status?.toLowerCase()}`}
              >
                {reservation.status}
              </span>
            </div>
            <div className="detail-row">
              <span className="label">Reserved At:</span>
              <span className="value">
                {new Date(reservation.reservedAt).toLocaleString()}
              </span>
            </div>
          </div>

          <div className="detail-section">
            <h3>Stall Information</h3>
            <div className="stall-grid">
              {reservation.stallIds?.map((stallId) => (
                <div key={stallId} className="stall-badge">
                  Stall #{stallId}
                </div>
              ))}
            </div>
            <div className="stall-count">
              Total Stalls: {reservation.stallIds?.length || 0}
            </div>
          </div>

          {reservation.literaryGenres &&
            reservation.literaryGenres.length > 0 && (
              <div className="detail-section">
                <h3>Literary Genres</h3>
                <div className="genre-list">
                  {reservation.literaryGenres.map((genre, index) => (
                    <span key={index} className="genre-tag">
                      {genre}
                    </span>
                  ))}
                </div>
              </div>
            )}
        </div>

        <div className="verify-footer">
          <p className="verify-note">
            ✓ This QR code has been successfully verified
          </p>
          <p className="verify-timestamp">
            Verified on: {new Date().toLocaleString()}
          </p>
        </div>

        <button onClick={() => navigate("/")} className="btn-secondary">
          Back to Home
        </button>
      </div>
    </div>
  );
}

export default VerifyReservation;
