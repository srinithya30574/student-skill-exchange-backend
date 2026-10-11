import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import { getUser, saveUser } from "../services/session";
import { getUserById, updateProfile } from "../services/userService";
import { getUserSkills } from "../services/skillService";
import { getConnections } from "../services/connectionService";
import { getRatingSummary, getRatingsReceived } from "../services/ratingService";

const departments = ["CSE", "IT", "ECE", "EEE", "MECH", "CIVIL", "AI&DS", "OTHER"];

function getErrorMessage(err) {
  if (err.response && err.response.data && err.response.data.message) {
    return err.response.data.message;
  }
  return "Could not connect to the server. Is the backend running?";
}

function stars(rating) {
  return "★".repeat(rating) + "☆".repeat(5 - rating);
}

function Profile() {
  const user = getUser();

  const [profile, setProfile] = useState(null);
  const [skills, setSkills] = useState([]);
  const [connections, setConnections] = useState([]);
  const [summary, setSummary] = useState(null);
  const [ratings, setRatings] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({ name: "", department: "", year: "", bio: "" });
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");
  const [success, setSuccess] = useState("");

  // Runs once, when the page opens
  useEffect(() => {
    async function loadProfile() {
      try {
        const [profileRes, skillsRes, connectionsRes, summaryRes, ratingsRes] =
          await Promise.all([
            getUserById(user.id),
            getUserSkills(user.id),
            getConnections(user.id),
            getRatingSummary(user.id),
            getRatingsReceived(user.id),
          ]);

        setProfile(profileRes.data);
        setSkills(skillsRes.data);
        setConnections(connectionsRes.data);
        setSummary(summaryRes.data);
        setRatings(ratingsRes.data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadProfile();
  }, [user.id]);

  function startEditing() {
    setForm({
      name: profile.name,
      department: profile.department || "",
      year: profile.year ? String(profile.year) : "",
      bio: profile.bio || "",
    });
    setFormError("");
    setSuccess("");
    setEditing(true);
  }

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSave(e) {
    e.preventDefault();
    setFormError("");
    setSaving(true);

    try {
      const response = await updateProfile(user.id, {
        name: form.name,
        department: form.department,
        year: form.year === "" ? null : Number(form.year),
        bio: form.bio,
      });

      setProfile(response.data); // show the new details
      saveUser(response.data); // update the saved session (so the Navbar name changes)
      setEditing(false);
      setSuccess("Profile updated successfully.");
    } catch (err) {
      setFormError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <Layout>
        <p>Loading profile...</p>
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

  const teachSkills = skills.filter((s) => s.skillType === "TEACH");
  const learnSkills = skills.filter((s) => s.skillType === "LEARN");

  return (
    <Layout>
      <h1 className="page-title">My Profile</h1>

      {success && <div className="message-success">{success}</div>}

      {/* Details card */}
      <div className="card">
        {!editing ? (
          <>
            <div className="profile-top">
              <div>
                <h2 className="profile-name">{profile.name}</h2>
                <p className="muted">{profile.email}</p>
              </div>
              <button className="btn btn-inline" onClick={startEditing}>
                Edit Profile
              </button>
            </div>

            <div className="info-grid">
              <div>
                <div className="info-label">Department</div>
                <div>{profile.department || "-"}</div>
              </div>
              <div>
                <div className="info-label">Year</div>
                <div>{profile.year || "-"}</div>
              </div>
              <div>
                <div className="info-label">Rating</div>
                <div>
                  {summary && summary.totalRatings > 0
                    ? `★ ${summary.averageRating} / 5 (${summary.totalRatings})`
                    : "No ratings yet"}
                </div>
              </div>
            </div>

            <div className="info-label">Bio</div>
            <p className="profile-bio">{profile.bio ? profile.bio : "No bio added yet."}</p>
          </>
        ) : (
          <form onSubmit={handleSave}>
            <h2>Edit Profile</h2>

            {formError && <div className="message-error">{formError}</div>}

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
                <select name="year" value={form.year} onChange={handleChange} required>
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
                rows="4"
                maxLength="500"
                value={form.bio}
                onChange={handleChange}
              />
            </div>

            <div className="button-row">
              <button className="btn btn-inline" type="submit" disabled={saving}>
                {saving ? "Saving..." : "Save Changes"}
              </button>
              <button
                className="btn btn-inline btn-secondary"
                type="button"
                onClick={() => setEditing(false)}
              >
                Cancel
              </button>
            </div>
          </form>
        )}
      </div>

      {/* Skills */}
      <div className="two-columns profile-section">
        <div className="card">
          <h2>Skills I Teach</h2>
          {teachSkills.length === 0 ? (
            <p className="muted">No skills added yet.</p>
          ) : (
            <div className="chip-list">
              {teachSkills.map((skill) => (
                <span className="chip chip-teach" key={skill.id}>
                  {skill.skillName}
                </span>
              ))}
            </div>
          )}
        </div>

        <div className="card">
          <h2>Skills I Want to Learn</h2>
          {learnSkills.length === 0 ? (
            <p className="muted">No skills added yet.</p>
          ) : (
            <div className="chip-list">
              {learnSkills.map((skill) => (
                <span className="chip chip-learn" key={skill.id}>
                  {skill.skillName}
                </span>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Connections */}
      <h2 className="section-title">My Connections ({connections.length})</h2>
      {connections.length === 0 ? (
        <div className="card">
          <p className="muted">No connections yet. Send a request to a matching student.</p>
        </div>
      ) : (
        <div className="cards-grid">
          {connections.map((c) => (
            <div className="card" key={c.userId}>
              <h3 className="connection-name">{c.name}</h3>
              <p className="muted">
                {c.department} | Year {c.year}
              </p>
              <p>
                <strong>Teaches:</strong>{" "}
                {c.teachSkills.length > 0 ? c.teachSkills.join(", ") : "-"}
              </p>
              <p>
                <strong>Wants to learn:</strong>{" "}
                {c.learnSkills.length > 0 ? c.learnSkills.join(", ") : "-"}
              </p>
            </div>
          ))}
        </div>
      )}

      {/* Feedback received */}
      <h2 className="section-title">Feedback I Received</h2>
      <div className="card">
        {ratings.length === 0 ? (
          <p className="muted">No feedback yet.</p>
        ) : (
          <ul className="feedback-list">
            {ratings.map((r) => (
              <li key={r.id}>
                <div className="feedback-top">
                  <strong>{r.reviewerName}</strong>
                  <span className="stars">{stars(r.rating)}</span>
                </div>
                <p className="feedback-text">{r.feedback ? r.feedback : "(No written feedback)"}</p>
              </li>
            ))}
          </ul>
        )}
      </div>
    </Layout>
  );
}

export default Profile;