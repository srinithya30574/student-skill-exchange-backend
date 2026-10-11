import { useEffect, useState } from "react";
import { Link, NavLink, useNavigate, useLocation } from "react-router-dom";
import { getUser, logoutUser } from "../services/session";
import { getReceivedRequests } from "../services/requestService";
import { getUnreadCount } from "../services/notificationService";

// We will add one line here every time we build a new page
const links = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/profile", label: "Profile" },
  { to: "/skills", label: "Skills" },
  { to: "/find-students", label: "Find Students" },
  { to: "/matches", label: "Matches" },
  { to: "/requests", label: "Requests" },
  { to: "/connections", label: "Connections" },
  { to: "/notifications", label: "Notifications" },
  { to: "/ratings", label: "Ratings" },
];

function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const user = getUser();
  const [pendingCount, setPendingCount] = useState(0);
  const [unreadCount, setUnreadCount] = useState(0);

  // Check the counts when the page changes, or when notifications change
  useEffect(() => {
    async function loadCounts() {
      if (!user) {
        return;
      }

      try {
        const response = await getReceivedRequests(user.id, "PENDING");
        setPendingCount(response.data.length);
      } catch (err) {
        setPendingCount(0);
      }

      try {
        const response = await getUnreadCount(user.id);
        setUnreadCount(response.data.unreadCount);
      } catch (err) {
        setUnreadCount(0);
      }
    }

    loadCounts();

    window.addEventListener("notificationsChanged", loadCounts);
    return () => {
      window.removeEventListener("notificationsChanged", loadCounts);
    };
  }, [location.pathname]); // eslint-disable-line react-hooks/exhaustive-deps

  function handleLogout() {
    logoutUser();
    navigate("/login");
  }

  function getBadgeNumber(path) {
    if (path === "/requests") return pendingCount;
    if (path === "/notifications") return unreadCount;
    return 0;
  }

  return (
    <nav className="navbar">
      <Link to="/dashboard" className="navbar-brand">
        Student Skill Exchange Platform
      </Link>

      <div className="navbar-links">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => (isActive ? "nav-link active" : "nav-link")}
          >
            {link.label}
            {getBadgeNumber(link.to) > 0 && (
              <span className="nav-badge">{getBadgeNumber(link.to)}</span>
            )}
          </NavLink>
        ))}
      </div>

      <div className="navbar-user">
        <span>{user ? user.name : ""}</span>
        <button className="btn-small" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;