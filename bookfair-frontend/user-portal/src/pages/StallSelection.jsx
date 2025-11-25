import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import toast from "react-hot-toast";
import Navbar from "../components/Navbar";

const StallSelection = () => {
  const [stalls, setStalls] = useState([]);
  const [selectedStalls, setSelectedStalls] = useState([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchStalls();
  }, []);

  const fetchStalls = async () => {
    try {
      const response = await axios.get("/api/stalls");
      setStalls(response.data);
    } catch (error) {
      toast.error("Failed to load stalls");
    }
  };

  const handleStallClick = (stall) => {
    if (!stall.available) return;

    if (selectedStalls.find((s) => s.id === stall.id)) {
      setSelectedStalls(selectedStalls.filter((s) => s.id !== stall.id));
    } else {
      if (selectedStalls.length >= 3) {
        toast.error("Maximum 3 stalls can be selected");
        return;
      }
      setSelectedStalls([...selectedStalls, stall]);
    }
  };

  const handleConfirmReservation = async () => {
    try {
      const stallIds = selectedStalls.map((s) => s.id);
      console.log("Sending reservation request:", { stallIds });
      const response = await axios.post("/api/reservations", { stallIds });
      console.log("Reservation response:", response.data);
      toast.success("Reservation confirmed! Check your email for QR code.");
      setShowConfirmModal(false);
      navigate("/my-reservations");
    } catch (error) {
      console.error("Reservation error:", error);
      console.error("Error response:", error.response?.data);
      const errorMessage =
        error.response?.data?.error ||
        error.response?.data?.message ||
        "Reservation failed";
      toast.error(errorMessage);
    }
  };

  const getStallClassName = (stall) => {
    let className = "stall-item";
    if (!stall.available) className += " reserved";
    else if (selectedStalls.find((s) => s.id === stall.id))
      className += " selected";
    else className += " available";
    return className;
  };

  return (
    <>
      <Navbar />
      <div className="stalls-container">
        <div className="stalls-header">
          <h2>Select Your Stalls</h2>
          <p>Choose up to 3 stalls for your exhibition space</p>
        </div>

        <div className="venue-map">
          <h3>Exhibition Venue Map</h3>
          <div className="stalls-grid">
            {stalls.map((stall) => (
              <div
                key={stall.id}
                className={getStallClassName(stall)}
                onClick={() => handleStallClick(stall)}
                title={`${stall.stallName} - ${stall.size} - Rs.${stall.pricePerDay}/day`}
              >
                <div className="stall-name">{stall.stallName}</div>
                <div className="stall-size">{stall.size}</div>
              </div>
            ))}
          </div>

          <div className="legend">
            <div className="legend-item">
              <div className="legend-box available"></div>
              <span>Available</span>
            </div>
            <div className="legend-item">
              <div className="legend-box reserved"></div>
              <span>Reserved</span>
            </div>
            <div className="legend-item">
              <div className="legend-box selected"></div>
              <span>Selected</span>
            </div>
          </div>
        </div>

        {selectedStalls.length > 0 && (
          <div className="selection-summary">
            <h3>Selected Stalls ({selectedStalls.length}/3)</h3>
            <div className="selected-stalls">
              {selectedStalls.map((stall) => (
                <div key={stall.id} className="selected-stall-badge">
                  {stall.stallName} - {stall.size} - Rs.{stall.pricePerDay}/day
                </div>
              ))}
            </div>
            <button
              className="btn-confirm"
              onClick={() => setShowConfirmModal(true)}
            >
              Confirm Reservation
            </button>
          </div>
        )}

        {showConfirmModal && (
          <div
            className="modal-overlay"
            onClick={() => setShowConfirmModal(false)}
          >
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h3>Confirm Your Reservation</h3>
              </div>
              <p>You are about to reserve the following stalls:</p>
              <ul style={{ margin: "20px 0", lineHeight: "2" }}>
                {selectedStalls.map((stall) => (
                  <li key={stall.id}>
                    <strong>{stall.stallName}</strong> - {stall.size}(
                    {stall.width}m x {stall.length}m) - Rs.{stall.pricePerDay}
                    /day
                  </li>
                ))}
              </ul>
              <p style={{ marginTop: "15px", color: "#666", fontSize: "14px" }}>
                A confirmation email with your QR code entry pass will be sent
                to your email address.
              </p>
              <div className="modal-actions">
                <button
                  className="btn-cancel"
                  onClick={() => setShowConfirmModal(false)}
                >
                  Cancel
                </button>
                <button
                  className="btn-confirm"
                  onClick={handleConfirmReservation}
                >
                  Confirm
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default StallSelection;
