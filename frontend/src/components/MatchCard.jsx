import Avatar from "./Avatar";

export function getLevelClass(score) {
  if (score >= 100) return "level-excellent";
  if (score >= 75) return "level-good";
  if (score >= 50) return "level-partial";
  return "level-low";
}

function listOrDash(list) {
  return list && list.length > 0 ? list.join(", ") : "-";
}

function MatchCard({ match, children }) {
  const levelClass = getLevelClass(match.matchScore);

  return (
    <div className="card match-card">
      <div className="match-header">
        <div className="person">
          <Avatar name={match.name} />
          <div>
            <h3>{match.name}</h3>
            <p className="muted">
              {match.department} | Year {match.year}
            </p>
          </div>
        </div>
        <div className={`score-badge ${levelClass}`}>{match.matchScore}%</div>
      </div>

      <div className="progress">
        <div
          className={`progress-fill fill-${levelClass}`}
          style={{ width: `${match.matchScore}%` }}
        />
      </div>

      <p className={`match-level ${levelClass}`}>{match.matchLevel}</p>

      <p>
        <strong>They can teach you:</strong> {listOrDash(match.theyCanTeachYou)}
      </p>
      <p>
        <strong>You can teach them:</strong> {listOrDash(match.youCanTeachThem)}
      </p>

      {children}
    </div>
  );
}

export default MatchCard;