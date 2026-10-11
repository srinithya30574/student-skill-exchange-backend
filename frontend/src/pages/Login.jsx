import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { loginStudent } from "../services/authService";
import { getUser, saveUser } from "../services/session";

function Login() {
  const navigate = useNavigate();

  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // Already logged in? Go straight to the dashboard.
  if (getUser()) {
    return <Navigate to="/dashboard" replace />;
  }

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await loginStudent({
        email: form.email,
        password: form.password,
      });

      saveUser(response.data); // remember who logged in
      navigate("/dashboard"); // go to the dashboard
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

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Student Skill Exchange Platform</h1>
        <p className="subtitle">Login to your account</p>

        {error && <div className="message-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <p className="auth-footer">
          New here? <Link to="/register">Create an account</Link>
        </p>
      </div>
    </div>
  );
}

export default Login;