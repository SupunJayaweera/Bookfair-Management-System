import { BrowserRouter as Router } from "react-router-dom";
import Navbar from "./components/Navbar";
import "./App.css";

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <h2 style={{ textAlign: "center", marginTop: "40px" }}>
          Frontend Setup – Navbar Preview
        </h2>
      </div>
    </Router>
  );
}

export default App;
