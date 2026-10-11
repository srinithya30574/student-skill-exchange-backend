# Student Skill Exchange Platform

A full-stack web application where students list the skills they can teach and the skills they want to learn, get matched with students who complement them, and exchange knowledge.

**Example:** Student A teaches Java and wants to learn React. Student B teaches React and wants to learn Java. The system finds this pair and shows a **100% match**.

## Features

- Student registration and login (passwords are hashed with BCrypt)
- Profile view and edit, with average rating
- Skill management: add, edit and remove skills (Teach / Learn) in 8 categories
- Search students by skill, with filters for category, department and year
- Smart skill matching with a match score and level
- Skill exchange requests: send, accept and reject
- Connections list
- Notifications with unread count
- Ratings (1 to 5 stars) and feedback, with average rating
- Dashboard with statistics and recommended matches

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | React.js, Vite, JavaScript, HTML/CSS, Axios, React Router |
| Backend | Java 21, Spring Boot, Spring Web, Spring Data JPA, Maven, REST APIs |
| Database | MySQL |
| Tools | IntelliJ IDEA, VS Code, MySQL Workbench, Postman, Git/GitHub |

## How Matching Works

For two students A and B:

Match Score = (skills A wants that B teaches + skills B wants that A teaches) / (skills A wants to learn + skills B wants to learn) × 100

| Score | Level |
|---|---|
| 100% | Excellent Match |
| 75% to 99% | Good Match |
| 50% to 74% | Partial Match |
| Below 50% | Low Match |

Skills are compared by skill id, and a score of 0% means nothing is in common. If neither student wants to learn anything, the score is 0%.

## Project Structure

- **backend:** Spring Boot application
  - controller: receives HTTP requests
  - service: business logic
  - repository: database access
  - entity: database tables
  - dto: data sent to and from the frontend
  - config: CORS settings
- **frontend:** React application
  - components: reusable interface elements
  - pages: application pages
  - services: API calls
- **docs:** database and API documentation

## How to Run

### Requirements
JDK 17 or newer, Node.js 18 or newer, MySQL 8, Git.

### Create the database
Open MySQL Workbench and run:

```sql
CREATE DATABASE student_skill_exchange;
```

The tables are created automatically when the backend starts.

### Run the backend
1. Open the backend folder in IntelliJ IDEA.
2. Copy `application.properties.example` and name the copy `application.properties`.
3. Replace `YOUR_MYSQL_PASSWORD` with your MySQL password in the real configuration file.
4. Run `StudentSkillExchangeApplication`. The API starts at http://localhost:8080.

### Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 and register an account.

## Documentation

- Database documentation: `docs/DATABASE.md`
- API documentation: `docs/API.md`

## Screenshots

Screenshots of the Dashboard, Matches, Find Students and Requests pages will be added in `docs/screenshots`.

## Known Limitations and Future Improvements

- Login uses a simple saved session. Future: JWT authentication with Spring Security.
- Ratings are allowed after connecting. Future: an exchange-completed status.
- Notifications refresh when changing pages. Future: real-time updates using WebSockets.
- Future improvements include chat, profile pictures and email notifications.

## Author

SUDHAGONI SRINITHYA , B.TECH, ANURAG UNIVERSITY