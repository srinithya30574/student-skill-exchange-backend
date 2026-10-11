import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Layout from "../components/Layout";
import { getUser } from "../services/session";
import { getConnections } from "../services/connectionService";

function getErrorMessage(err) {
  if (err.response && err.response.data && err.response.data.message) {
    return err.response.data.message;
  }
  return "Could not connect to the server. Is the backend running?";
}

function Connections() {
  const user = getUser();

  const [connections, setConnections] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Runs once, when the page opens
  useEffect(() => {
    async function loadConnections() {
      try {
        const response = await getConnections(user.id);
        setConnections(response.data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadConnections();
  }, [user.id]);

  if (loading) {
    return (
      <Layout>
        <p>Loading connections...</p>
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

  // Keep only the connections whose name contains the typed text
  const shownConnections = connections.filter((c) =>
    c.name.toLowerCase().includes(search.trim().toLowerCase())
  );

  return (
    <Layout>
      <h1 className="page-title">My Connections ({connections.length})</h1>

      {connections.length === 0 ? (
        <div className="card">
          <p className="muted">
            You have no connections yet. Go to <Link to="/find-students">Find Students</Link> to
            send a request, or check your <Link to="/requests">Requests</Link> for ones waiting for
            your answer.
          </p>
        </div>
      ) : (
        <>
          <div className="card">
            <div className="form-group connection-search">
              <label>Search your connections by name</label>
              <input
                type="text"
                placeholder="e.g. Rahul"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
          </div>

          <h2 className="section-title">
            {shownConnections.length} connection{shownConnections.length === 1 ? "" : "s"} shown
          </h2>

          {shownConnections.length === 0 ? (
            <div className="card">
              <p className="muted">No connection matches that name.</p>
            </div>
          ) : (
            <div className="cards-grid">
              {shownConnections.map((c) => (
                <div className="card connection-card" key={c.userId}>
                  <div className="match-header">
                    <div>
                      <h3>{c.name}</h3>
                      <p className="muted">
                        {c.department} | Year {c.year}
                      </p>
                    </div>
                    <span className="status-badge status-connected">✓ Connected</span>
                  </div>

                  {c.bio && <p className="student-bio">{c.bio}</p>}

                  <p>
                    <strong>Teaches:</strong>
                  </p>
                  {c.teachSkills.length === 0 ? (
                    <p className="muted small">Nothing yet</p>
                  ) : (
                    <div className="chip-list">
                      {c.teachSkills.map((name) => (
                        <span className="chip chip-teach" key={name}>
                          {name}
                        </span>
                      ))}
                    </div>
                  )}

                  <p>
                    <strong>Wants to learn:</strong>
                  </p>
                  {c.learnSkills.length === 0 ? (
                    <p className="muted small">Nothing yet</p>
                  ) : (
                    <div className="chip-list">
                      {c.learnSkills.map((name) => (
                        <span className="chip chip-learn" key={name}>
                          {name}
                        </span>
                      ))}
                    </div>
                  )}

                  <p className="muted small connection-date">
                    Request sent on {new Date(c.requestedAt).toLocaleDateString()}
                  </p>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </Layout>
  );
}

export default Connections;