import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Sidebar from "./Sidebar";
import Topbar from "./Topbar";
import { getUser } from "../services/session";
import { getReceivedRequests } from "../services/requestService";
import { getUnreadCount } from "../services/notificationService";

function Layout({ children }) {
  const location = useLocation();
  const user = getUser();

  const [menuOpen, setMenuOpen] = useState(false);
  const [pendingCount, setPendingCount] = useState(0);
  const [unreadCount, setUnreadCount] = useState(0);

  // Load the two numbers, and reload them when notifications change
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

  return (
    <div className="app-shell">
      <Sidebar
        open={menuOpen}
        pendingCount={pendingCount}
        unreadCount={unreadCount}
        onNavigate={() => setMenuOpen(false)}
      />

      {menuOpen && <div className="sidebar-overlay" onClick={() => setMenuOpen(false)} />}

      <div className="app-main">
        <Topbar unreadCount={unreadCount} onMenuClick={() => setMenuOpen(!menuOpen)} />
        <main className="page-container">{children}</main>
      </div>
    </div>
  );
}

export default Layout;