import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Layout from "../components/Layout";
import MatchCard from "../components/MatchCard";
import { getUser } from "../services/session";
import { getMatches } from "../services/matchService";
import { getConnections } from "../services/connectionService";
import { getSentRequests, getReceivedRequests } from "../services/requestService";

const levelTabs = [
  { key: "ALL", label: "All" },
  { key: "Excellent Match", label: "Excellent" },
  { key: "Good Match", label: "Good" },
  { key: "Partial Match", label: "Partial" },
  { key: "Low Match", label: "Low" },
];

function getErrorMessage(err) {
  if (err.response && err.response.data && err.response.data.message) {
    return err.response.data.message;
  }
  return "Could not connect to the server. Is the backend running?";
}

function Matches() {
  const user = getUser();

  const [matches, setMatches] = useState([]);
  const [connectedIds, setConnectedIds] = useState([]);
  const [sentIds, setSentIds] = useState([]);
  const [receivedIds, setReceivedIds] = useState([]);

  const [activeTab, setActiveTab] = useState("ALL");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Runs once, when the page opens
  useEffect(() => {
    async function loadPage() {
      try {
        const [matchRes, connRes, sentRes, receivedRes] = await Promise.all([
          getMatches(user.id),
          getConnections(user.id),
          getSentRequests(user.id),
          getReceivedRequests(user.id, "PENDING"),
        ]);

        setMatches(matchRes.data);
        setConnectedIds(connRes.data.map((c) => c.userId));
        setSentIds(
          sentRes.data.filter((r) => r.status === "PENDING").map((r) => r.receiverId)
        );
        setReceivedIds(receivedRes.data.map((r) => r.senderId));
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadPage();
  }, [user.id]);

  if (loading) {
    return (
      <Layout>
        <p>Loading matches...</p>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout>
        <div className="message-error">{error}</div>
      </Layout>
    );
  }

  // How many matches are in each level
  function countLevel(key) {
    if (key === "ALL") return matches.length;
    return matches.filter((m) => m.matchLevel === key).length;
  }

  const shownMatches =
    activeTab === "ALL" ? matches : matches.filter((m) => m.matchLevel === activeTab);

  // The badge at the bottom of a card
  function renderStatus(match) {
    if (connectedIds.includes(match.userId)) {
      return (
        <div className="card-action">
          <span className="status-badge status-connected">✓ Connected</span>
        </div>
      );
    }
    if (sentIds.includes(match.userId)) {
      return (
        <div className="card-action">
          <span className="status-badge status-pending">Request sent (waiting)</span>
        </div>
      );
    }
    if (receivedIds.includes(match.userId)) {
      return (
        <div className="card-action">
          <span className="status-badge status-incoming">
            Wants to connect with you (see Requests)
          </span>
        </div>
      );
    }
    return null;
  }

  return (
    <Layout>
      <h1 className="page-title">My Matches</h1>

      {/* How the score works */}
      <div className="card formula-box">
        <h2>How is the match score calculated?</h2>
        <p>
          A good skill exchange works <strong>both ways</strong>. We look at everything the two of
          you want to learn, and check how much of it the other person can teach.
        </p>

        <div className="formula">
          Match Score = (skills you want that they teach + skills they want that you teach) ÷
          (skills you want to learn + skills they want to learn) × 100
        </div>

        <p className="muted">
          <strong>Example:</strong> You teach Java and want React. They teach React and want Java.
          You want 1 skill and they teach it. They want 1 skill and you teach it. So the score is
          (1 + 1) ÷ (1 + 1) × 100 = <strong>100%</strong>.
        </p>

        <div className="level-legend">
          <span className="legend-item level-excellent">100% Excellent</span>
          <span className="legend-item level-good">75% to 99% Good</span>
          <span className="legend-item level-partial">50% to 74% Partial</span>
          <span className="legend-item level-low">Below 50% Low</span>
        </div>
      </div>

      {/* Level tabs */}
      <div className="tab-row">
        {levelTabs.map((tab) => (
          <button
            key={tab.key}
            className={activeTab === tab.key ? "tab-button active" : "tab-button"}
            onClick={() => setActiveTab(tab.key)}
          >
            {tab.label} ({countLevel(tab.key)})
          </button>
        ))}
      </div>

      {/* The matches */}
      {matches.length === 0 ? (
        <div className="card">
          <p className="muted">
            No matches yet. A match needs a skill you want to learn that another student teaches, or
            a skill you teach that another student wants to learn. Add your skills on the{" "}
            <Link to="/skills">Skills page</Link>, then check back.
          </p>
        </div>
      ) : shownMatches.length === 0 ? (
        <div className="card">
          <p className="muted">You have no matches in this level.</p>
        </div>
      ) : (
        <>
          <p className="muted">
            Showing {shownMatches.length} match{shownMatches.length === 1 ? "" : "es"}, best first.
            To send a request, go to <Link to="/find-students">Find Students</Link>.
          </p>
          <div className="cards-grid">
            {shownMatches.map((match) => (
              <MatchCard key={match.userId} match={match}>
                {renderStatus(match)}
              </MatchCard>
            ))}
          </div>
        </>
      )}
    </Layout>
  );
}

export default Matches;