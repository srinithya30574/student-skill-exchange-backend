import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Layout from "../components/Layout";
import { getUser } from "../services/session";
import { getConnections } from "../services/connectionService";
import {
  getRatingSummary,
  getRatingsReceived,
  getRatingsGiven,
  submitRating,
} from "../services/ratingService";

function getErrorMessage(err) {
  if (err.response && err.response.data && err.response.data.message) {
    return err.response.data.message;
  }
  return "Could not connect to the server. Is the backend running?";
}

function stars(rating) {
  return "★".repeat(rating) + "☆".repeat(5 - rating);
}

function Ratings() {
  const user = getUser();

  const [connections, setConnections] = useState([]);
  const [given, setGiven] = useState([]);
  const [received, setReceived] = useState([]);
  const [summary, setSummary] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [ratingFor, setRatingFor] = useState(null); // the connection being rated
  const [starValue, setStarValue] = useState(0);
  const [feedback, setFeedback] = useState("");
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");
  const [success, setSuccess] = useState("");

  // Runs once, when the page opens
  useEffect(() => {
    async function loadPage() {
      try {
        const [connRes, givenRes, receivedRes, summaryRes] = await Promise.all([
          getConnections(user.id),
          getRatingsGiven(user.id),
          getRatingsReceived(user.id),
          getRatingSummary(user.id),
        ]);
        setConnections(connRes.data);
        setGiven(givenRes.data);
        setReceived(receivedRes.data);
        setSummary(summaryRes.data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadPage();
  }, [user.id]);

  function startRating(connection) {
    setRatingFor(connection);
    setStarValue(0);
    setFeedback("");
    setFormError("");
    setSuccess("");
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFormError("");

    if (starValue === 0) {
      setFormError("Please choose a star rating from 1 to 5.");
      return;
    }

    setSaving(true);
    try {
      await submitRating({
        reviewerId: user.id,
        reviewedUserId: ratingFor.userId,
        rating: starValue,
        feedback: feedback,
      });

      const givenRes = await getRatingsGiven(user.id);
      setGiven(givenRes.data);
      setSuccess(`Your rating for ${ratingFor.name} was saved.`);
      setRatingFor(null);
    } catch (err) {
      setFormError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <Layout>
        <p>Loading ratings...</p>
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

  const ratedIds = given.map((r) => r.reviewedUserId);

  return (
    <Layout>
      <h1 className="page-title">Ratings and Feedback</h1>

      {success && <div className="message-success">{success}</div>}

      {/* My average */}
      <div className="card">
        <h2>My Average Rating</h2>
        {summary && summary.totalRatings > 0 ? (
          <p className="average-line">
            <span className="stars">★</span> {summary.averageRating} / 5{" "}
            <span className="muted">
              ({summary.totalRatings} rating{summary.totalRatings === 1 ? "" : "s"})
            </span>
          </p>
        ) : (
          <p className="muted">No ratings yet. Ratings from your connections will appear here.</p>
        )}
      </div>

      {/* Rate a connection */}
      <h2 className="section-title">Rate a Connection</h2>
      {connections.length === 0 ? (
        <div className="card">
          <p className="muted">
            You can rate students after you connect with them. Go to{" "}
            <Link to="/find-students">Find Students</Link> to send a request.
          </p>
        </div>
      ) : (
        <div className="cards-grid">
          {connections.map((c) => {
            const alreadyRated = ratedIds.includes(c.userId);
            const isOpen = ratingFor && ratingFor.userId === c.userId;

            return (
              <div className="card" key={c.userId}>
                <h3 className="connection-name">{c.name}</h3>
                <p className="muted">
                  {c.department} | Year {c.year}
                </p>

                {alreadyRated ? (
                  <span className="status-badge status-connected">✓ You rated this student</span>
                ) : isOpen ? (
                  <form onSubmit={handleSubmit}>
                    {formError && <div className="message-error">{formError}</div>}

                    <div className="star-picker">
                      {[1, 2, 3, 4, 5].map((n) => (
                        <button
                          type="button"
                          key={n}
                          className={n <= starValue ? "star-button on" : "star-button"}
                          onClick={() => setStarValue(n)}
                          aria-label={`${n} star${n === 1 ? "" : "s"}`}
                        >
                          ★
                        </button>
                      ))}
                    </div>

                    <textarea
                      className="feedback-input"
                      rows="3"
                      maxLength="500"
                      placeholder="Write feedback (optional)"
                      value={feedback}
                      onChange={(e) => setFeedback(e.target.value)}
                    />

                    <div className="button-row">
                      <button className="btn btn-inline" type="submit" disabled={saving}>
                        {saving ? "Saving..." : "Submit Rating"}
                      </button>
                      <button
                        className="btn btn-inline btn-secondary"
                        type="button"
                        onClick={() => setRatingFor(null)}
                      >
                        Cancel
                      </button>
                    </div>
                  </form>
                ) : (
                  <button className="btn btn-inline" onClick={() => startRating(c)}>
                    Rate
                  </button>
                )}
              </div>
            );
          })}
        </div>
      )}

      {/* Received and given */}
      <div className="two-columns profile-section">
        <div className="card">
          <h2>Ratings I Received ({received.length})</h2>
          {received.length === 0 ? (
            <p className="muted">No ratings yet.</p>
          ) : (
            <ul className="feedback-list">
              {received.map((r) => (
                <li key={r.id}>
                  <div className="feedback-top">
                    <strong>{r.reviewerName}</strong>
                    <span className="stars">{stars(r.rating)}</span>
                  </div>
                  <p className="feedback-text">
                    {r.feedback ? r.feedback : "(No written feedback)"}
                  </p>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="card">
          <h2>Ratings I Gave ({given.length})</h2>
          {given.length === 0 ? (
            <p className="muted">You have not rated anyone yet.</p>
          ) : (
            <ul className="feedback-list">
              {given.map((r) => (
                <li key={r.id}>
                  <div className="feedback-top">
                    <strong>To: {r.reviewedUserName}</strong>
                    <span className="stars">{stars(r.rating)}</span>
                  </div>
                  <p className="feedback-text">
                    {r.feedback ? r.feedback : "(No written feedback)"}
                  </p>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </Layout>
  );
}

export default Ratings;