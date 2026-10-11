import { Link, useNavigate } from "react-router-dom";
import Avatar from "./Avatar";
import { getUser, logoutUser } from "../services/session";

function Topbar({ unreadCount, onMenuClick }) {
  const navigate = useNavigate();
  const user = getUser();

  function handleLogout() {
    logoutUser();
    navigate("/login");
  }

  return (
    <header className="topbar">
      <button className="menu-button" onClick={onMenuClick} aria-label="Open menu">
        ☰
      </button>

      <div className="topbar-right">
        <Link to="/notifications" className="bell" aria-label="Notifications">
          🔔
          {unreadCount > 0 && <span className="bell-badge">{unreadCount}</span>}
        </Link>

        <Link to="/profile" className="topbar-user">
          <Avatar name={user ? user.name : ""} />
          <span className="topbar-name">{user ? user.name : ""}</span>
        </Link>

        <button className="btn-outline" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </header>
  );
}

export default Topbar;