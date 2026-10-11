import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerStudent } from "../services/authService";
const departments = ["CSE", "IT", "ECE", "EEE", "MECH", "CIVIL", "AI&DS", "OTHER"];
function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({

    name: "",
    email: "",
    password: "",
    department: "",
    year: "",
    bio: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  // Runs every time the student types in any field
  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  // Runs when the student clicks the Register button
  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      await registerStudent({
        name: form.name,
        email: form.email,
        password: form.password,
        department: form.department,
        year: form.year === "" ? null : Number(form.year),
        bio: form.bio,
      });

            navigate("/login");
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
        <p className="subtitle">Create your account</p>

        {error && <div className="message-error">{error}</div>}
        {success && <div className="message-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Name</label>
            <input
              type="text"
              name="name"
              value={form.name}
              onChange={handleChange}
              required
            />
          </div>

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
            <label>Password (at least 6 characters)</label>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Department</label>
              <select
                name="department"
                value={form.department}
                onChange={handleChange}
                required
              >
                <option value="">Select</option>
                {departments.map((dept) => (
                  <option key={dept} value={dept}>
                    {dept}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Year</label>
              <select
                name="year"
                value={form.year}
                onChange={handleChange}
                required
              >
                <option value="">Select</option>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Bio</label>
            <textarea
              name="bio"
              rows="3"
              maxLength="500"
              value={form.bio}
              onChange={handleChange}
            />
          </div>
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Creating account..." : "Register"}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;