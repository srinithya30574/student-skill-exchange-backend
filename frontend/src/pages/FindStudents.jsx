import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import { getLevelClass } from "../components/MatchCard";
import { getUser } from "../services/session";
import { searchStudents } from "../services/searchService";
import { getCategories } from "../services/skillService";
import { getMatches } from "../services/matchService";
import { getConnections } from "../services/connectionService";
import {
  getSentRequests,
  getReceivedRequests,
  sendRequest,
} from "../services/requestService";

const departments = ["CSE", "IT", "ECE", "EEE", "MECH", "CIVIL", "AI&DS", "OTHER"];

const categoryLabels = {
  PROGRAMMING: "Programming",
  WEB_DEVELOPMENT: "Web Development",
  DATABASE: "Database",
  DATA_SCIENCE: "Data Science",
  AI_ML: "AI/ML",
  DESIGN: "Design",
  COMMUNICATION: "Communication",
  OTHER: "Other",
};

const emptyFilters = { skill: "", category: "", department: "", year: "" };

function getErrorMessage(err) {
  if (err.response && err.response.data && err.response.data.message) {
    return err.response.data.message;
  }
  return "Could not connect to the server. Is the backend running?";
}

function listOrDash(list) {
  return list && list.length > 0 ? list.join(", ") : "-";
}

function FindStudents() {
  const user = getUser();

  const [filters, setFilters] = useState(emptyFilters);
  const [students, setStudents] = useState([]);
  const [categories, setCategories] = useState([]);
  const [matchMap, setMatchMap] = useState({}); // student id -> match info
  const [connectedIds, setConnectedIds] = useState([]);
  const [sentIds, setSentIds] = useState([]); // I sent them a pending request
  const [receivedIds, setReceivedIds] = useState([]); // they sent me a pending request

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searching, setSearching] = useState(false);
  const [searchError, setSearchError] = useState("");
  const [success, setSuccess] = useState("");

  const [requestingId, setRequestingId] = useState(null); // whose request box is open
  const [message, setMessage] = useState("");
  const [sending, setSending] = useState(false);
  const [sendError, setSendError] = useState("");

  // Runs once, when the page opens
  useEffect(() => {
    async function loadPage() {
      try {
        const [catRes, matchRes, connRes, sentRes, receivedRes, studentsRes] =
          await Promise.all([
            getCategories(),
            getMatches(user.id),
            getConnections(user.id),
            getSentRequests(user.id),
            getReceivedRequests(user.id, "PENDING"),
            searchStudents({ excludeUserId: user.id }),
          ]);

        setCategories(catRes.data);

        const map = {};
        matchRes.data.forEach((m) => {
          map[m.userId] = m;
        });
        setMatchMap(map);

        setConnectedIds(connRes.data.map((c) => c.userId));
        setSentIds(
          sentRes.data.filter((r) => r.status === "PENDING").map((r) => r.receiverId)
        );
        setReceivedIds(receivedRes.data.map((r) => r.senderId));
        setStudents(studentsRes.data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadPage();
  }, [user.id]);

  // Calls the search API with only the filters that are filled in
  async function runSearch(activeFilters) {
    const params = { excludeUserId: user.id };
    if (activeFilters.skill.trim() !== "") params.skill = activeFilters.skill.trim();
    if (activeFilters.category !== "") params.category = activeFilters.category;
    if (activeFilters.department !== "") params.department = activeFilters.department;
    if (activeFilters.year !== "") params.year = activeFilters.year;

    setSearching(true);
    setSearchError("");
    setSuccess("");
    setRequestingId(null);

    try {
      const response = await searchStudents(params);
      setStudents(response.data);
    } catch (err) {
      setSearchError(getErrorMessage(err));
    } finally {
      setSearching(false);
    }
  }

  function handleFilterChange(e) {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  }

  function handleSearch(e) {
    e.preventDefault();
    runSearch(filters);
  }

  function handleClear() {
    setFilters(emptyFilters);
    runSearch(emptyFilters);
  }

  function startRequest(student) {
    setRequestingId(student.id);
    setMessage(`Hi ${student.name}! I would like to exchange skills with you.`);
    setSendError("");
    setSuccess("");
  }

  async function handleSendRequest(e, student) {
    e.preventDefault();
    setSendError("");
    setSending(true);

    try {
      await sendRequest({
        senderId: user.id,
        receiverId: student.id,
        message: message,
      });
      setSentIds([...sentIds, student.id]);
      setRequestingId(null);
      setSuccess(`Request sent to ${student.name}.`);
    } catch (err) {
      setSendError(getErrorMessage(err));
    } finally {
      setSending(false);
    }
  }

  if (loading) {
    return (
      <Layout>
        <p>Loading...</p>
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

  // Best matches first
  function scoreOf(id) {
    return matchMap[id] ? matchMap[id].matchScore : 0;
  }
  const sortedStudents = [...students].sort((a, b) => scoreOf(b.id) - scoreOf(a.id));

  // Draws the right thing at the bottom of a card: a status, a form, or a button
  function renderAction(student) {
    if (connectedIds.includes(student.id)) {
      return <span className="status-badge status-connected">✓ Connected</span>;
    }
    if (sentIds.includes(student.id)) {
      return <span className="status-badge status-pending">Request sent (waiting)</span>;
    }
    if (receivedIds.includes(student.id)) {
      return (
        <span className="status-badge status-incoming">
          Wants to connect with you (see Requests)
        </span>
      );
    }
    if (requestingId === student.id) {
      return (
        <form className="request-box" onSubmit={(e) => handleSendRequest(e, student)}>
          {sendError && <div className="message-error">{sendError}</div>}
          <textarea
            rows="3"
            maxLength="500"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            required
          />
          <div className="button-row">
            <button className="btn btn-inline" type="submit" disabled={sending}>
              {sending ? "Sending..." : "Send"}
            </button>
            <button
              className="btn btn-inline btn-secondary"
              type="button"
              onClick={() => setRequestingId(null)}
            >
              Cancel
            </button>
          </div>
        </form>
      );
    }
    return (
      <button className="btn btn-inline" onClick={() => startRequest(student)}>
        Send Request
      </button>
    );
  }

  return (
    <Layout>
      <h1 className="page-title">Find Students</h1>

      {/* Search and filters */}
      <div className="card">
        <form onSubmit={handleSearch}>
          <div className="filter-grid">
            <div className="form-group">
              <label>Skill (they can teach)</label>
              <input
                type="text"
                name="skill"
                placeholder="e.g. React"
                value={filters.skill}
                onChange={handleFilterChange}
              />
            </div>

            <div className="form-group">
              <label>Skill category</label>
              <select name="category" value={filters.category} onChange={handleFilterChange}>
                <option value="">All categories</option>
                {categories.map((c) => (
                  <option key={c} value={c}>
                    {categoryLabels[c] || c}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Department</label>
              <select
                name="department"
                value={filters.department}
                onChange={handleFilterChange}
              >
                <option value="">All departments</option>
                {departments.map((d) => (
                  <option key={d} value={d}>
                    {d}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Year</label>
              <select name="year" value={filters.year} onChange={handleFilterChange}>
                <option value="">All years</option>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
              </select>
            </div>
          </div>

          <div className="button-row">
            <button className="btn btn-inline" type="submit" disabled={searching}>
              {searching ? "Searching..." : "Search"}
            </button>
            <button
              className="btn btn-inline btn-secondary"
              type="button"
              onClick={handleClear}
            >
              Clear
            </button>
          </div>
        </form>
      </div>

      {success && <div className="message-success results-gap">{success}</div>}
      {searchError && <div className="message-error results-gap">{searchError}</div>}

      <h2 className="section-title">
        {sortedStudents.length} student{sortedStudents.length === 1 ? "" : "s"} found
      </h2>

      {sortedStudents.length === 0 ? (
        <div className="card">
          <p className="muted">No students match your search. Try changing or clearing the filters.</p>
        </div>
      ) : (
        <div className="cards-grid">
          {sortedStudents.map((student) => {
            const match = matchMap[student.id];
            const score = match ? match.matchScore : 0;
            const levelClass = getLevelClass(score);

            return (
              <div className="card student-card" key={student.id}>
                <div className="match-header">
                  <div>
                    <h3>{student.name}</h3>
                    <p className="muted">
                      {student.department} | Year {student.year}
                    </p>
                  </div>
                  <div className={`score-badge ${levelClass}`}>{score}%</div>
                </div>

                <p className={`match-level ${levelClass}`}>
                  {match ? match.matchLevel : "Low Match"}
                </p>

                {student.bio && <p className="student-bio">{student.bio}</p>}

                <p>
                  <strong>Teaches:</strong>
                </p>
                {student.teachSkills.length === 0 ? (
                  <p className="muted small">Nothing yet</p>
                ) : (
                  <div className="chip-list">
                    {student.teachSkills.map((name) => (
                      <span className="chip chip-teach" key={name}>
                        {name}
                      </span>
                    ))}
                  </div>
                )}

                <p>
                  <strong>Wants to learn:</strong>
                </p>
                {student.learnSkills.length === 0 ? (
                  <p className="muted small">Nothing yet</p>
                ) : (
                  <div className="chip-list">
                    {student.learnSkills.map((name) => (
                      <span className="chip chip-learn" key={name}>
                        {name}
                      </span>
                    ))}
                  </div>
                )}

                {match && (
                  <div className="match-detail">
                    <p>
                      <strong>They can teach you:</strong> {listOrDash(match.theyCanTeachYou)}
                    </p>
                    <p>
                      <strong>You can teach them:</strong> {listOrDash(match.youCanTeachThem)}
                    </p>
                  </div>
                )}

                <div className="card-action">{renderAction(student)}</div>
              </div>
            );
          })}
        </div>
      )}
    </Layout>
  );
}

export default FindStudents;