function Avatar({ name }) {
  const initials = (name || "?")
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((word) => word[0].toUpperCase())
    .join("");

  return <span className="avatar">{initials}</span>;
}

export default Avatar;