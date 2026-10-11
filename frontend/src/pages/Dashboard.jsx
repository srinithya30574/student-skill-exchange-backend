import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import MatchCard from "../components/MatchCard";
import { getDashboard } from "../services/dashboardService";
import { getUser } from "../services/session";

function Dashboard() {
  const user = getUser();

  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  // Runs once, when the page opens
  useEffect(() => {
    async function loadDashboard() {
      try {
        const response = await getDashboard(user.id);
        setData(response.data);
      } catch (err) {
        if (err.response && err.response.data && err.response.data.message) {
          setError(err.response.data.message);
        } else {
          setError("Could not connect to the server. Is the backend running?");
        }
      } finally {
        setLoading(false);
      }
    }

    loadDashboard();
  }, [user.id]);

  if (loading) {
    return (
      <Layout>
        <p>Loading dashboard...</p>
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

   const stats = [
    { label: "Matches", value: data.matchCount, icon: "🤝" },
    { label: "Pending Requests", value: data.pendingRequests, icon: "📨" },
    { label: "Connections", value: data.connectionCount, icon: "👥" },
    { label: "Unread Notifications", value: data.unreadNotifications, icon: "🔔" },
  ];
  return (
    <Layout>
      {/* Welcome */}
      <div className="welcome">
        <h1>Welcome, {data.name}!</h1>
        <p className="muted">
          {data.totalRatings > 0
            ? `Your rating: ${data.averageRating} / 5 (${data.totalRatings} rating${
                data.totalRatings === 1 ? "" : "s"
              })`
            : "No ratings yet"}
        </p>
      </div>

      {/* Stat cards */}
      <div className="stats-grid">
        {stats.map((stat) => (
                    <div className="card stat-card" key={stat.label}>
            <div className="stat-icon">{stat.icon}</div>
            <div className="stat-value">{stat.value}</div>
            <div className="stat-label">{stat.label}</div>
          </div>
        ))}
      </div>

      {/* Skills */}
      <div className="two-columns">
        <div className="card">
          <h2>Skills I Teach</h2>
          {data.teachSkills.length === 0 ? (
            <p className="muted">No skills added yet.</p>
          ) : (
            <div className="chip-list">
              {data.teachSkills.map((skill) => (
                <span className="chip chip-teach" key={skill.id}>
                  {skill.skillName}
                </span>
              ))}
            </div>
          )}
        </div>

        <div className="card">
          <h2>Skills I Want to Learn</h2>
          {data.learnSkills.length === 0 ? (
            <p className="muted">No skills added yet.</p>
          ) : (
            <div className="chip-list">
              {data.learnSkills.map((skill) => (
                <span className="chip chip-learn" key={skill.id}>
                  {skill.skillName}
                </span>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Recommended matches */}
      <h2 className="section-title">Recommended Matches</h2>
      {data.recommendedMatches.length === 0 ? (
        <div className="card">
          <p className="muted">
            No matches yet. Add skills you can teach and skills you want to learn to get matches.
          </p>
        </div>
      ) : (
        <div className="cards-grid">
          {data.recommendedMatches.map((match) => (
            <MatchCard key={match.userId} match={match} />
          ))}
        </div>
      )}

      {/* Notifications */}
      <h2 className="section-title">Recent Notifications</h2>
      <div className="card">
        {data.recentNotifications.length === 0 ? (
          <p className="muted">No notifications yet.</p>
        ) : (
          <ul className="notification-list">
            {data.recentNotifications.map((n) => (
              <li key={n.id} className={n.read ? "" : "unread"}>
                <span>{n.message}</span>
                <span className="muted small">{new Date(n.createdAt).toLocaleString()}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </Layout>
  );
}

export default Dashboard;