import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Layout from "../components/Layout";
import { getUser } from "../services/session";
import {
  getReceivedRequests,
  getSentRequests,
  acceptRequest,
  rejectRequest,
} from "../services/requestService";

function getErrorMessage(err) {
  if (err.response && err.response.data && err.response.data.message) {
    return err.response.data.message;
  }
  return "Could not connect to the server. Is the backend running?";
}

function statusClass(status) {
  if (status === "ACCEPTED") return "status-connected";
  if (status === "REJECTED") return "status-rejected";
  return "status-pending";
}

function statusLabel(status) {
  if (status === "ACCEPTED") return "Accepted";
  if (status === "REJECTED") return "Rejected";
  return "Pending";
}

function Requests() {
  const user = getUser();

  const [received, setReceived] = useState([]);
  const [sent, setSent] = useState([]);
  const [activeTab, setActiveTab] = useState("RECEIVED");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionError, setActionError] = useState("");
  const [success, setSuccess] = useState("");
  const [busyId, setBusyId] = useState(null); // the request being answered

  // Reload both lists from the backend
  async function loadRequests() {
    const [receivedRes, sentRes] = await Promise.all([
      getReceivedRequests(user.id),
      getSentRequests(user.id),
    ]);
    setReceived(receivedRes.data);
    setSent(sentRes.data);
  }

  // Runs once, when the page opens
  useEffect(() => {
    async function loadPage() {
      try {
        const [receivedRes, sentRes] = await Promise.all([
          getReceivedRequests(user.id),
          getSentRequests(user.id),
        ]);
        setReceived(receivedRes.data);
        setSent(sentRes.data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadPage();
  }, [user.id]);

  async function handleAnswer(request, accept) {
    setActionError("");
    setSuccess("");
    setBusyId(request.id);

    try {
      if (accept) {
        await acceptRequest(request.id, user.id);
        setSuccess(`You accepted ${request.senderName}'s request. You are now connected!`);
      } else {
        await rejectRequest(request.id, user.id);
        setSuccess(`You rejected ${request.senderName}'s request.`);
      }
            await loadRequests();
      window.dispatchEvent(new Event("notificationsChanged"));
    } catch (err) {
      setActionError(getErrorMessage(err));
      // The list may be out of date, so refresh it
      try {
        await loadRequests();
      } catch (e) {
        // ignore: the error message above is already shown
      }
    } finally {
      setBusyId(null);
    }
  }

  if (loading) {
    return (
      <Layout>
        <p>Loading requests...</p>
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

  const pendingCount = received.filter((r) => r.status === "PENDING").length;

  return (
    <Layout>
      <h1 className="page-title">Requests</h1>

      {success && <div className="message-success">{success}</div>}
      {actionError && <div className="message-error">{actionError}</div>}

      {/* Tabs */}
      <div className="tab-row">
        <button
          className={activeTab === "RECEIVED" ? "tab-button active" : "tab-button"}
          onClick={() => setActiveTab("RECEIVED")}
        >
          Received ({received.length}
          {pendingCount > 0 ? `, ${pendingCount} pending` : ""})
        </button>
        <button
          className={activeTab === "SENT" ? "tab-button active" : "tab-button"}
          onClick={() => setActiveTab("SENT")}
        >
          Sent ({sent.length})
        </button>
      </div>

      {/* Received */}
      {activeTab === "RECEIVED" &&
        (received.length === 0 ? (
          <div className="card">
            <p className="muted">
              You have not received any requests yet. Add your skills on the{" "}
              <Link to="/skills">Skills page</Link> so other students can find you.
            </p>
          </div>
        ) : (
          <div className="request-list">
            {received.map((r) => (
              <div className="card request-card" key={r.id}>
                <div className="request-top">
                  <div>
                    <h3>{r.senderName}</h3>
                    <p className="muted small">{new Date(r.createdAt).toLocaleString()}</p>
                  </div>
                  <span className={`status-badge ${statusClass(r.status)}`}>
                    {statusLabel(r.status)}
                  </span>
                </div>

                <p className="request-message">{r.message}</p>

                {r.status === "PENDING" && (
                  <div className="button-row">
                    <button
                      className="btn btn-inline"
                      disabled={busyId === r.id}
                      onClick={() => handleAnswer(r, true)}
                    >
                      Accept
                    </button>
                    <button
                      className="btn btn-inline btn-danger"
                      disabled={busyId === r.id}
                      onClick={() => handleAnswer(r, false)}
                    >
                      Reject
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        ))}

            {/* Sent */}
      {activeTab === "SENT" &&
        (sent.length === 0 ? (
          <div className="card">
            <p className="muted">
              You have not sent any requests yet. Go to{" "}
              <Link to="/find-students">Find Students</Link> to send one.
            </p>
          </div>
        ) : (
          <div className="card table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>To</th>
                  <th>Message</th>
                  <th>Sent on</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {sent.map((r) => (
                  <tr key={r.id}>
                    <td>
                      <strong>{r.receiverName}</strong>
                    </td>
                    <td>{r.message}</td>
                    <td>{new Date(r.createdAt).toLocaleDateString()}</td>
                    <td>
                      <span className={`status-badge ${statusClass(r.status)}`}>
                        {statusLabel(r.status)}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ))}
    </Layout>
  );
}

export default Requests;