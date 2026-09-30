# Tenant Portal

Tenant Portal now contains the original static frontend prototype and a unified Spring Boot backend with database persistence, session authentication, and role-based authorization.

> **Status:** Backend foundation implemented. Payment and chat integration remain intentionally out of scope for this phase.

## 💡 Why This Project?

This project wasn't just about building a UI — it was built to practice the complete software engineering process behind a real application. As a team, we worked through requirement gathering, effort and cost estimation using COCOMO, project tracking and task management using Jira, and structured design before writing any code. The Tenant Portal frontend itself models a real-world use case (property/tenant management) while the actual learning objective was understanding how SDLC methodology, estimation models, and project management tools come together in a team software project.

## Features

- Tenant registration with BCrypt password hashing
- Session-based login with server-side credential verification
- `ADMIN` and `TENANT` roles
- Tenant self-service profile endpoint
- Admin endpoint for listing all tenants
- H2 file-backed local development database
- Authorization tests covering tenant isolation and admin access

## 🛠️ Tech Stack

**Frontend:** HTML, CSS, JavaScript (unchanged)

**Backend:** Java 21, Spring Boot, Spring Security, Spring Data JPA, H2, Maven

**Process & Tools:**
- **Jira** — sprint planning, task tracking, and team collaboration
- **COCOMO Model** — software cost and effort estimation
- *(Add any other SDLC tools/artifacts used — e.g. Gantt charts, UML diagrams, requirement docs)*

## 📸 Screenshots

*(Add screenshots of the UI here once available)*

## 🚀 Getting Started (Local Setup)

### Prerequisites
- Java 21 or newer
- Maven 3.9 or newer

### Running Locally

```bash
# Clone the repository
git clone <your-repo-url>
cd tenant-portal

# Run tests
mvn test

# Start the backend
mvn spring-boot:run
```

The backend starts on `http://localhost:8080`. The existing HTML frontend is not connected to these APIs yet.

The original root-level `TenantPortalApplication.java` and `RentPortalBackendApplication.java` files are preserved as legacy prototypes. Maven only compiles the unified application under `src/main/java`, so those prototype entry points are not active.

## 📁 Folder Structure

```
tenant-portal/
├── src/main/java/com/tenantportal/
│   ├── configuration/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── security/
│   └── service/
├── src/main/resources/
├── src/test/java/com/tenantportal/
├── pom.xml
├── index.html
├── tenantdashboard.html
├── admin final.html
└── README.md
```

## 📄 SDLC Artifacts

*(Optional but recommended — link or list any project artifacts your team produced)*
- Requirement Specification Document
- COCOMO effort estimation report
- Jira board / sprint reports
- Design documents / wireframes

## API quick reference

### Register a tenant

```http
POST /api/auth/register
Content-Type: application/json

{
	"username": "tenant1",
	"password": "tenant-password",
	"fullName": "Tenant One",
	"email": "tenant1@example.com",
	"phone": "9000000001",
	"flatNumber": "A-101"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{"username":"tenant1","password":"tenant-password"}
```

The response creates an authenticated server-side session. Send its session cookie with later requests.

### Tenant profile

```http
GET /api/tenant/me
```

The server resolves the tenant from the authenticated session. No tenant ID is accepted.

### Admin tenant list

```http
GET /api/admin/tenants
```

This endpoint requires the `ADMIN` role.

## Roadmap

- [x] Backend integration foundation
- [x] Authentication and role authorization foundation
- [ ] Connect the existing frontend to the new APIs
- [ ] Add complaints and rent/payment persistence
- [ ] Add chat integration
- [ ] Deploy frontend (Vercel/Netlify)

## 👥 Team Project

Built collaboratively as a team project, with emphasis on SDLC process and project management tooling.

## 👤 Author

**Tanishka K**
Third-year CSE student, Madras Institute of Technology, Anna University

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
