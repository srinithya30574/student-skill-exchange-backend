import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Layout from "../components/Layout";
import { getUser } from "../services/session";
import {
  getNotifications,
  markAsRead,
  markAllAsRead,
} from "../services/notificationService";

const typeInfo = {
  NEW_REQUEST: { label: "New Request", className: "type-request", link: "/requests", linkText: "View requests" },
  REQUEST_ACCEPTED: { label: "Accepted", className: "type-accepted", link: "/connections", linkText: "View connections" },
  REQUEST_REJECTED: { label: "Rejected", className: "type-rejected", link: "/requests", linkText: "View requests" },
  NEW_CONNECTION: { label: "New Connection", className: "type-connection", link: "/connections", linkText: "View connections" },
};

function getErrorMessage(err) {
  if (err.response && err.response.data && err.response.data.message) {
    return err.response.data.message;
  }
  return "Could not connect to the server. Is the backend running?";
}

// Tells the Navbar to refresh its unread number
function tellNavbar() {
  window.dispatchEvent(new Event("notificationsChanged"));
}

function Notifications() {
  const user = getUser();

  const [notifications, setNotifications] = useState([]);
  const [activeTab, setActiveTab] = useState("ALL");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionError, setActionError] = useState("");
  const [success, setSuccess] = useState("");
  const [busy, setBusy] = useState(false);

  // Runs once, when the page opens
  useEffect(() => {
    async function loadNotifications() {
      try {
        const response = await getNotifications(user.id);
        setNotifications(response.data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadNotifications();
  }, [user.id]);

  async function handleMarkOne(notification) {
    setActionError("");
    setSuccess("");

    try {
      await markAsRead(notification.id, user.id);
      setNotifications(
        notifications.map((n) => (n.id === notification.id ? { ...n, read: true } : n))
      );
      tellNavbar();
    } catch (err) {
      setActionError(getErrorMessage(err));
    }
  }

  async function handleMarkAll() {
    setActionError("");
    setSuccess("");
    setBusy(true);

    try {
      await markAllAsRead(user.id);
      setNotifications(notifications.map((n) => ({ ...n, read: true })));
      setSuccess("All notifications were marked as read.");
      tellNavbar();
    } catch (err) {
      setActionError(getErrorMessage(err));
    } finally {
      setBusy(false);
    }
  }

  if (loading) {
    return (
      <Layout>
        <p>Loading notifications...</p>
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

  const unreadCount = notifications.filter((n) => !n.read).length;
  const shown = activeTab === "UNREAD" ? notifications.filter((n) => !n.read) : notifications;

  return (
    <Layout>
      <div className="notif-header">
        <h1 className="page-title">Notifications</h1>
        <button
          className="btn btn-inline"
          onClick={handleMarkAll}
          disabled={busy || unreadCount === 0}
        >
          Mark all as read
        </button>
      </div>

      {success && <div className="message-success">{success}</div>}
      {actionError && <div className="message-error">{actionError}</div>}

      {/* Tabs */}
      <div className="tab-row">
        <button
          className={activeTab === "ALL" ? "tab-button active" : "tab-button"}
          onClick={() => setActiveTab("ALL")}
        >
          All ({notifications.length})
        </button>
        <button
          className={activeTab === "UNREAD" ? "tab-button active" : "tab-button"}
          onClick={() => setActiveTab("UNREAD")}
        >
          Unread ({unreadCount})
        </button>
      </div>

      {/* The list */}
      {notifications.length === 0 ? (
        <div className="card">
          <p className="muted">
            You have no notifications yet. You will be notified when someone sends you a request, or
            answers one of yours. Try <Link to="/find-students">Find Students</Link>.
          </p>
        </div>
      ) : shown.length === 0 ? (
        <div className="card">
          <p className="muted">You have no unread notifications. 🎉</p>
        </div>
      ) : (
        <div className="notif-list">
          {shown.map((n) => {
            const info = typeInfo[n.type] || { label: n.type, className: "", link: null };

            return (
              <div className={n.read ? "card notif-item" : "card notif-item notif-unread"} key={n.id}>
                <div className="notif-top">
                  <span className={`notif-type ${info.className}`}>{info.label}</span>
                  <span className="muted small">{new Date(n.createdAt).toLocaleString()}</span>
                </div>

                <p className="notif-message">{n.message}</p>

                <div className="notif-actions">
                  {info.link && (
                    <Link className="btn-link" to={info.link}>
                      {info.linkText}
                    </Link>
                  )}
                  {!n.read && (
                    <button className="btn-link" onClick={() => handleMarkOne(n)}>
                      Mark as read
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </Layout>
  );
}

export default Notifications;