# Tenant Portal

Tenant Portal contains a vanilla JavaScript frontend served by a unified Spring Boot backend with database persistence, session authentication, and role-based authorization.

> **Status:** Authentication and frontend session integration are implemented. Payment and chat integration remain intentionally out of scope.

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

**Frontend:** HTML, CSS, vanilla JavaScript served from Spring Boot static resources

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

The application starts on `http://localhost:8080`. Open
`http://localhost:8080/index.html` for the login page. The frontend uses the
same-origin server session cookie; it does not store passwords, session IDs, or
tokens in browser storage.

For local development, the bootstrap admin username defaults to `admin`. Set
`BOOTSTRAP_ADMIN_USERNAME` and `BOOTSTRAP_ADMIN_PASSWORD` before starting the
application, rather than relying on fallback credentials:

```powershell
$env:BOOTSTRAP_ADMIN_USERNAME = "admin"
$env:BOOTSTRAP_ADMIN_PASSWORD = "use-a-local-development-password"
mvn spring-boot:run
```

Tenant registration is public and always creates a `TENANT` account. The
administrator account is created only by the backend bootstrap initializer.
After login, the backend role determines whether the user is redirected to the
tenant or admin dashboard; the frontend does not accept a user-selected role.

Logout is performed with `POST /api/auth/logout`. It invalidates the server
session and requires the CSRF token sent by the frontend. The frontend obtains
the CSRF token through `GET /api/auth/csrf` and sends it in the
`X-XSRF-TOKEN` header for protected state-changing requests.

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
│   └── static/
│       ├── css/
│       ├── js/
│       ├── index.html
│       ├── register.html
│       ├── tenantdashboard.html
│       ├── admin final.html
│       └── chat.html
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

### Current authenticated user

```http
GET /api/auth/me
```

Returns only the authenticated username and role:

```json
{"username":"tenant1","role":"TENANT"}
```

### CSRF token

```http
GET /api/auth/csrf
```

The response initializes the `XSRF-TOKEN` cookie used for protected
state-changing requests. The cookie is readable by the frontend so it can be
copied into the `X-XSRF-TOKEN` request header.

### Logout

```http
POST /api/auth/logout
X-XSRF-TOKEN: <token-from-XSRF-TOKEN-cookie>
```

The server invalidates the session and clears the session and CSRF cookies.

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
- [x] Connect login and registration frontend to the authentication APIs
- [x] Add authenticated role discovery and session logout
- [x] Protect logout with CSRF
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
