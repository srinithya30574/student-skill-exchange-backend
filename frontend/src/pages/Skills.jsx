import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import { getUser } from "../services/session";
import {
  getUserSkills,
  getCategories,
  getAllSkills,
  addUserSkill,
  updateUserSkill,
  removeUserSkill,
} from "../services/skillService";

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

function formatCategory(category) {
  return categoryLabels[category] || category;
}

function getErrorMessage(err) {
  if (err.response && err.response.data && err.response.data.message) {
    return err.response.data.message;
  }
  return "Could not connect to the server. Is the backend running?";
}

const emptyForm = { skillName: "", category: "PROGRAMMING", skillType: "TEACH" };

function Skills() {
  const user = getUser();

  const [skills, setSkills] = useState([]); // my skills
  const [allSkills, setAllSkills] = useState([]); // every skill in the system
  const [categories, setCategories] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null); // null = adding, a number = editing
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState("");
  const [success, setSuccess] = useState("");

  // Runs once, when the page opens
  useEffect(() => {
    async function loadPage() {
      try {
        const [skillsRes, categoriesRes, allSkillsRes] = await Promise.all([
          getUserSkills(user.id),
          getCategories(),
          getAllSkills(),
        ]);
        setSkills(skillsRes.data);
        setCategories(categoriesRes.data);
        setAllSkills(allSkillsRes.data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    }

    loadPage();
  }, [user.id]);

  // Reload the lists after add / edit / remove
  async function refreshSkills() {
    const [skillsRes, allSkillsRes] = await Promise.all([
      getUserSkills(user.id),
      getAllSkills(),
    ]);
    setSkills(skillsRes.data);
    setAllSkills(allSkillsRes.data);
  }

  // If the typed name already exists in the system, its category is fixed
  const existingSkill = allSkills.find(
    (s) => s.name.toLowerCase() === form.skillName.trim().toLowerCase()
  );
  const shownCategory = existingSkill ? existingSkill.category : form.category;

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setFormError("");
    setSuccess("");
    setSaving(true);

    const data = {
      skillName: form.skillName.trim(),
      category: shownCategory,
      skillType: form.skillType,
    };

    try {
      if (editingId === null) {
        await addUserSkill(user.id, data);
        setSuccess(`"${data.skillName}" was added.`);
      } else {
        await updateUserSkill(user.id, editingId, data);
        setSuccess(`"${data.skillName}" was updated.`);
      }

      await refreshSkills();
      setForm(emptyForm);
      setEditingId(null);
    } catch (err) {
      setFormError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  function startEditing(skill) {
    setEditingId(skill.id);
    setForm({
      skillName: skill.skillName,
      category: skill.category,
      skillType: skill.skillType,
    });
    setFormError("");
    setSuccess("");
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function cancelEditing() {
    setEditingId(null);
    setForm(emptyForm);
    setFormError("");
  }

  async function handleRemove(skill) {
    const sure = window.confirm(`Remove "${skill.skillName}" from your skills?`);
    if (!sure) {
      return;
    }

    setFormError("");
    setSuccess("");

    try {
      await removeUserSkill(user.id, skill.id);
      if (editingId === skill.id) {
        cancelEditing();
      }
      await refreshSkills();
      setSuccess(`"${skill.skillName}" was removed.`);
    } catch (err) {
      setFormError(getErrorMessage(err));
    }
  }

  if (loading) {
    return (
      <Layout>
        <p>Loading skills...</p>
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

  // One list box, used twice (TEACH and LEARN)
  function renderSkillList(title, list, chipClass) {
    return (
      <div className="card">
        <h2>
          {title} ({list.length})
        </h2>
        {list.length === 0 ? (
          <p className="muted">No skills added yet.</p>
        ) : (
          <ul className="skill-list">
            {list.map((skill) => (
              <li key={skill.id} className="skill-item">
                <div>
                  <span className={`chip ${chipClass}`}>{skill.skillName}</span>
                  <span className="muted small skill-category">
                    {formatCategory(skill.category)}
                  </span>
                </div>
                <div className="skill-actions">
                  <button className="btn-link" onClick={() => startEditing(skill)}>
                    Edit
                  </button>
                  <button className="btn-link btn-link-danger" onClick={() => handleRemove(skill)}>
                    Remove
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    );
  }

  return (
    <Layout>
      <h1 className="page-title">My Skills</h1>

      {success && <div className="message-success">{success}</div>}

      {/* Add / Edit form */}
      <div className="card">
        <h2>{editingId === null ? "Add a Skill" : "Edit Skill"}</h2>

        {formError && <div className="message-error">{formError}</div>}

        <form onSubmit={handleSubmit}>
          <div className="skill-form-row">
            <div className="form-group">
              <label>Skill name</label>
              <input
                type="text"
                name="skillName"
                list="skill-suggestions"
                placeholder="e.g. Java"
                value={form.skillName}
                onChange={handleChange}
                required
              />
              <datalist id="skill-suggestions">
                {allSkills.map((s) => (
                  <option key={s.id} value={s.name} />
                ))}
              </datalist>
            </div>

            <div className="form-group">
              <label>Category</label>
              <select
                name="category"
                value={shownCategory}
                onChange={handleChange}
                disabled={Boolean(existingSkill)}
              >
                {categories.map((c) => (
                  <option key={c} value={c}>
                    {formatCategory(c)}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Type</label>
              <select name="skillType" value={form.skillType} onChange={handleChange}>
                <option value="TEACH">I can teach this</option>
                <option value="LEARN">I want to learn this</option>
              </select>
            </div>
          </div>

          {existingSkill && (
            <p className="muted small hint">
              This skill already exists, so its category ({formatCategory(existingSkill.category)})
              is kept.
            </p>
          )}

          <div className="button-row">
            <button className="btn btn-inline" type="submit" disabled={saving}>
              {saving ? "Saving..." : editingId === null ? "Add Skill" : "Save Changes"}
            </button>
            {editingId !== null && (
              <button
                className="btn btn-inline btn-secondary"
                type="button"
                onClick={cancelEditing}
              >
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      {/* The two lists */}
      <div className="two-columns profile-section">
        {renderSkillList("Skills I Can Teach", teachSkills, "chip-teach")}
        {renderSkillList("Skills I Want to Learn", learnSkills, "chip-learn")}
      </div>
    </Layout>
  );
}

export default Skills;