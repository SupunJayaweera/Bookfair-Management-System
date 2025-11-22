import { useState, useEffect } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import Navbar from "../components/Navbar";

const StallManagement = () => {
  const [stalls, setStalls] = useState([]);
  const [filteredStalls, setFilteredStalls] = useState([]);
  const [filters, setFilters] = useState({
    availability: "all",
    size: "all",
    search: "",
  });

  useEffect(() => {
    fetchStalls();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [filters, stalls]);

  const fetchStalls = async () => {
    try {
      const response = await axios.get("/api/stalls");
      setStalls(response.data);
    } catch (error) {
      toast.error("Failed to load stalls");
    }
  };

  const applyFilters = () => {
    let filtered = [...stalls];

    if (filters.availability !== "all") {
      const isAvailable = filters.availability === "available";
      filtered = filtered.filter((s) => s.available === isAvailable);
    }

    if (filters.size !== "all") {
      filtered = filtered.filter((s) => s.size === filters.size.toUpperCase());
    }

    if (filters.search) {
      filtered = filtered.filter((s) =>
        s.stallName.toLowerCase().includes(filters.search.toLowerCase())
      );
    }

    setFilteredStalls(filtered);
  };

  const handleInitializeStalls = async () => {
    try {
      await axios.post("/api/stalls/initialize");
      toast.success("Stalls initialized successfully");
      fetchStalls();
    } catch (error) {
      toast.error("Failed to initialize stalls");
    }
  };

  return (
    <>
      <Navbar />
      <div className="page-container">
        <div className="page-header">
          <div>
            <h2>Stall Management</h2>
            <p>View and manage all exhibition stalls</p>
          </div>
          <button className="btn-init" onClick={handleInitializeStalls}>
            Initialize Stalls
          </button>
        </div>

        <div className="filters">
          <div className="filter-group">
            <label>Availability</label>
            <select
              value={filters.availability}
              onChange={(e) =>
                setFilters({ ...filters, availability: e.target.value })
              }
            >
              <option value="all">All Stalls</option>
              <option value="available">Available</option>
              <option value="reserved">Reserved</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Size</label>
            <select
              value={filters.size}
              onChange={(e) => setFilters({ ...filters, size: e.target.value })}
            >
              <option value="all">All Sizes</option>
              <option value="small">Small</option>
              <option value="medium">Medium</option>
              <option value="large">Large</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Search</label>
            <input
              type="text"
              placeholder="Search by stall name..."
              value={filters.search}
              onChange={(e) =>
                setFilters({ ...filters, search: e.target.value })
              }
            />
          </div>
        </div>

        {filteredStalls.length === 0 ? (
          <div className="empty-state">
            <h3>No Stalls Found</h3>
            <p>Initialize stalls or adjust your filters</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Stall Name</th>
                  <th>Size</th>
                  <th>Dimensions</th>
                  <th>Price/Day</th>
                  <th>Position</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {filteredStalls.map((stall) => (
                  <tr key={stall.id}>
                    <td>
                      <strong>{stall.stallName}</strong>
                    </td>
                    <td>
                      <span
                        className={`badge badge-${stall.size.toLowerCase()}`}
                      >
                        {stall.size}
                      </span>
                    </td>
                    <td>
                      {stall.width}m × {stall.length}m
                    </td>
                    <td>Rs. {stall.pricePerDay.toLocaleString()}</td>
                    <td>
                      ({stall.positionX}, {stall.positionY})
                    </td>
                    <td>
                      <span
                        className={`badge ${
                          stall.available ? "badge-available" : "badge-reserved"
                        }`}
                      >
                        {stall.available ? "Available" : "Reserved"}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div style={{ marginTop: "20px", textAlign: "center", color: "#666" }}>
          Showing {filteredStalls.length} of {stalls.length} stalls
        </div>
      </div>
    </>
  );
};

export default StallManagement;
