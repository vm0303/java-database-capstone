## Section 1: Architecture summary

This Spring Boot application will use both MVC and RESTful API controllers to model a full scale clinic application. The
MVC controllers will handle the web interface for users, while the RESTful API controllers will provide endpoints for
external clients to interact with the application programmatically.
Thymeleaf will be used for the admin and doctor dashboard, while the REST APIs will server the other modules.
This application will interact with two databases: MySQL for the main application data and MongoDB for prescription
data.
All the controllers will route requests through a common service layer, where it will help us delegate to the
appropriate repositories for data access.

MySQL will use JPA entities to model the data, while MongoDB will use document models.

## Section 2: Numbered flow of data and control

1. **User Interface Layer**
   The system supports multiple user types and interaction patterns. Users can access the application through two
   primary mechanisms: Thymeleaf-based web dashboards such as `AdminDashboard` and `DoctorDashboard`, which are
   traditional HTML pages rendered on the server and delivered to the browser, and REST API clients like mobile apps or
   frontend modules (e.g., Appointments, PatientDashboard, and PatientRecord) that interact with the backend via HTTP
   and receive JSON responses. This separation allows the system to support both interactive browser views for
   administrative and clinical staff, as well as scalable API-based integrations for patient-facing applications and
   external consumers.


2. **Controller Layer**
   When a user interacts with the application (e.g., clicking a button or submitting a form), the request is routed to a
   backend controller based on the URL path and the HTTP method. Requests for server-rendered views are handled by
   Thymeleaf Controllers (specifically `DashboardController` in the `mvc` package), which are annotated with
   `@Controller` and return `.html` template names that will be filled with dynamic data and rendered in the browser.
   Requests from API consumers are handled by REST Controllers (`AdminController`, `DoctorController`,
   `PatientController`, `AppointmentController`, and `PrescriptionController`), which are annotated with
   `@RestController` and process the input, call backend logic, and return responses in JSON format. These controllers
   serve as the entry points into the backend application logic, enforcing request validation through annotations like
   `@Valid` and coordinating the request/response flow.


3. **Token Validation and Security**
   For secured endpoints, the controller performs authentication and authorization by calling the shared `Service` class
   to validate JWT tokens. The `validateToken` method in the service layer checks if the provided token is valid for a
   specific user role (admin, doctor, or patient) by delegating to `TokenService`. If the token is invalid or expired,
   the system returns a 401 Unauthorized response with an appropriate error message, preventing unauthorized access to
   protected resources. For example, when a patient attempts to book an appointment, the system validates their patient
   token before allowing the operation to proceed. This security checkpoint ensures that only authenticated users with
   proper roles can access their respective functionalities.


4. **Service Layer**
   All controllers delegate business logic to the Service Layer, which acts as the heart of the backend system. This
   layer applies business rules and validations, coordinates workflows across multiple entities, and ensures a clean
   separation between controller logic and data access. Specialized services like `DoctorService`, `PatientService`,
   `AppointmentService`, and `PrescriptionService` handle domain-specific operations (CRUD operations, authentication,
   availability checks), while the shared `Service` class provides cross-cutting functionality like token validation,
   filtering operations, and general-purpose validations. For instance, before booking an appointment, the
   `validateAppointment` method checks doctor availability by comparing the requested time slot against existing
   appointments to prevent double-booking. Methods in this layer are typically annotated with `@Transactional` to ensure
   database operations are consistent and wrapped in a single transaction, maintaining data integrity across multiple
   repository calls.


5. **Repository Layer**
   The service layer communicates with the Repository Layer to perform data access operations. This layer includes two
   types of repositories: MySQL Repositories (`AdminRepository`, `DoctorRepository`, `PatientRepository`, and
   `AppointmentRepository`) which use Spring Data JPA and extend `JpaRepository` to manage structured relational data
   with automatically inherited CRUD methods, and MongoDB Repository (`PrescriptionRepository`) which uses Spring Data
   MongoDB and extends `MongoRepository` to manage document-based prescription records. These repositories are annotated
   with `@Repository` and abstract the database access logic by exposing a simple, declarative interface for fetching
   and persisting data. Custom query methods like `findByUsername` in `AdminRepository` or `findByAppointmentId` in
   `PrescriptionRepository` are automatically implemented by Spring Data based on method naming conventions, eliminating
   the need for manual query writing in most cases.


6. **Database Access and Model Binding**
   Each repository interfaces directly with the underlying database engine, and retrieved data is automatically mapped
   into Java model classes through model binding. MySQL stores all core entities that benefit from a normalized
   relational schema and constraints—such as admins, doctors, patients, and appointments—as tables, and Spring Data JPA
   converts these rows into Java objects annotated with `@Entity`. MongoDB stores flexible and nested data structures,
   such as prescriptions which may vary in format and require rapid schema evolution, as BSON/JSON documents in
   collections, and Spring Data MongoDB loads these into document objects annotated with `@Document`. This dual-database
   setup leverages the strengths of both structured relational storage (MySQL for transactional integrity and complex
   relationships) and unstructured document storage (MongoDB for flexibility and schema-less design), providing optimal
   data persistence strategies for different types of information.


7. **Response Flow and Client Delivery**
   Finally, the bound models are used in the response layer to complete the request-response cycle. In MVC flows, models
   are passed from the controller to Thymeleaf templates located in the `resources/templates` directory, where they are
   dynamically rendered as HTML using Thymeleaf's expression language (e.g., `${doctor.name}`) and sent to the browser
   as a complete web page—for example, the admin dashboard displaying a list of all doctors. In REST flows, the same
   models or transformed Data Transfer Objects (DTOs like `AppointmentDTO` or `Login`) are automatically serialized into
   JSON format by Spring's Jackson library and sent back to the client as part of an HTTP response with appropriate
   status codes (200 OK for success, 401 Unauthorized for authentication failures, 404 Not Found for missing resources,
   or 500 Internal Server Error for exceptions). This marks the end of the data flow, delivering either a fully rendered
   web page for browser-based users or structured API data for programmatic consumers.
