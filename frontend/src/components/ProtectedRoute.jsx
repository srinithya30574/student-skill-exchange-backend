import { Navigate } from "react-router-dom";
import { getUser } from "../services/session";

function ProtectedRoute({ children }) {
  if (!getUser()) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default ProtectedRoute;