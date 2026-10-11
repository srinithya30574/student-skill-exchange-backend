import { NavLink } from "react-router-dom";

const links = [
  { to: "/dashboard", label: "Dashboard", icon: "🏠" },
  { to: "/profile", label: "Profile", icon: "👤" },
  { to: "/skills", label: "Skills", icon: "🛠️" },
  { to: "/find-students", label: "Find Students", icon: "🔍" },
  { to: "/matches", label: "Matches", icon: "🤝" },
  { to: "/requests", label: "Requests", icon: "📨" },
  { to: "/connections", label: "Connections", icon: "👥" },
  { to: "/notifications", label: "Notifications", icon: "🔔" },
  { to: "/ratings", label: "Ratings", icon: "⭐" },
];

function Sidebar({ open, pendingCount, unreadCount, onNavigate }) {
  function getBadge(path) {
    if (path === "/requests") return pendingCount;
    if (path === "/notifications") return unreadCount;
    return 0;
  }

  return (
    <aside className={open ? "sidebar open" : "sidebar"}>
      <div className="sidebar-brand">
        <span className="sidebar-logo">🎓</span>
        <span>
          Student Skill
          <br />
          Exchange Platform
        </span>
      </div>

      <nav className="sidebar-links">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            onClick={onNavigate}
            className={({ isActive }) => (isActive ? "side-link active" : "side-link")}
          >
            <span className="side-icon">{link.icon}</span>
            <span>{link.label}</span>
            {getBadge(link.to) > 0 && <span className="nav-badge">{getBadge(link.to)}</span>}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}

export default Sidebar;