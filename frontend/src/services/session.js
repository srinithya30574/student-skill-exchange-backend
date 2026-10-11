const STORAGE_KEY = "skillExchangeUser";

// Save the logged-in student in the browser
export function saveUser(user) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
}

// Get the logged-in student (or null if nobody is logged in)
export function getUser() {
  const text = localStorage.getItem(STORAGE_KEY);
  if (!text) {
    return null;
  }
  try {
    return JSON.parse(text);
  } catch (e) {
    return null;
  }
}

// Log out
export function logoutUser() {
  localStorage.removeItem(STORAGE_KEY);
}