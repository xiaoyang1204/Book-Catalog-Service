# AI Prompts & Tools Used

## Project: Book Catalog Service

### AI Tool: Craft (Tencent Cloud IDE Plugin)

---

## Prompt 1: Project Scaffolding

**Input:**
> Build a "Book Catalog Service": a Spring Boot microservice that allows clients to manage a collection of books (CRUD)
> 1. RESTful CRUD API (No UI needed)
> 2. Apply at least 2 design patterns in your code
> 3. Write unit tests and API tests. Aim for ≥ 80% code coverage
> 4. Should build it with AI tools
> 5. Should submit all the AI prompts, skills or any other AI related inputs used in the homework

**AI Action:**
- Analyzed existing workspace structure
- Created a Spring Boot 2.7.18 project with Maven wrapper
- Configured MyBatis + MySQL (production) / H2 (test), Validation, Lombok, and JaCoCo for coverage
- Set up layered package structure: `com.example.bookcatalog`

---

## Prompt 2: Create Domain Layer (Entity & DTOs)

**Input:**
> Create the Book entity and DTO classes (BookCreateRequest, BookUpdateRequest, BookResponse) with validation annotations

**AI Action:**
- Created `Book.java` entity with fields: id, title, author, isbn, publishedDate, description, available
- Used Lombok `@Builder`, `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Created `BookCreateRequest.java` with `@NotBlank` / `@Size` validation on title, author, isbn
- Created `BookUpdateRequest.java` with optional `@Size` validation (partial update support)
- Created `BookResponse.java` as output DTO

---

## Prompt 3: Create Exception Classes

**Input:**
> Create custom exception classes and a standardized API error response

**AI Action:**
- Created `BookNotFoundException.java` — thrown when book not found by id or isbn
- Created `DuplicateIsbnException.java` — thrown when creating/updating with duplicate ISBN
- Created `ApiError.java` — standardized error response with timestamp, status, error, message, and field-level errors

---

## Prompt 4: Create BookMapper (Builder Pattern)

**Input:**
> Create a mapper class to convert between Entity and DTOs using Builder pattern

**AI Action:**
- Created `BookMapper.java` as a utility class (private constructor)
- `toEntity(BookCreateRequest)` — maps create request to Book entity using `@Builder`
- `mergeToEntity(BookUpdateRequest, Book)` — partial update: only overwrites non-null fields
- `toResponse(Book)` — maps Book entity to BookResponse using `@Builder`

**Design Pattern Applied: Builder Pattern**
- **Where:** `BookMapper.java`, `Book.java`, `BookCreateRequest.java`, `BookUpdateRequest.java`, `BookResponse.java`
- **Why:** Decouples object construction from representation, makes mapping code readable and maintainable. Lombok `@Builder` generates the builder implementation automatically.

---

## Prompt 5: Create Repository Layer (MyBatis)

**Input:**
> Create the MyBatis repository interface and XML mapper for CRUD operations on books

**AI Action:**
- Created `BookRepository.java` interface with `@Mapper` annotation
- Methods: findById, findByIsbn, existsByIsbn, findAll, findByAuthorContainingIgnoreCase, findByTitleContainingIgnoreCase, findByAvailable, save, update, deleteById
- Created `BookRepository.xml` with SQL mappings for all operations
- Configured `map-underscore-to-camel-case: true` for automatic field mapping

---

## Prompt 6: Create Service Layer with Strategy Pattern

**Input:**
> Create the BookService and implement search functionality using Strategy pattern

**AI Action:**
- Created `BookService.java` with full CRUD + search logic
- Injected `List<BookSearchStrategy>` — Spring auto-collects all strategy beans
- `searchBooks(type, keyword)` — finds matching strategy via `supports(type)` and delegates search
- Created `BookSearchStrategy.java` interface with `supports(String)` and `search(String)` methods
- Created 3 strategy implementations:

| Strategy | Search Type | Behavior |
|---|---|---|
| `AuthorSearchStrategy` | `author` | Case-insensitive author search |
| `TitleSearchStrategy` | `title` | Case-insensitive title search |
| `AvailabilitySearchStrategy` | `available` / `unavailable` | Filter by availability status |

**Design Pattern Applied: Strategy Pattern**
- **Where:** `BookSearchStrategy.java` (interface), `AuthorSearchStrategy.java`, `TitleSearchStrategy.java`, `AvailabilitySearchStrategy.java`, `BookService.java`
- **Why:** Allows different search behaviors to be selected at runtime without modifying service code. New search types can be added by simply creating a new `@Component` implementing `BookSearchStrategy`.

---

## Prompt 7: Create Controller & Exception Handler

**Input:**
> Create the REST controller and global exception handler

**AI Action:**
- Created `BookController.java` with `@RestController` and `@RequestMapping("/api/v1/books")`
- Created `GlobalExceptionHandler.java` with `@RestControllerAdvice`
- Handles: BookNotFoundException → 404, DuplicateIsbnException → 409, MethodArgumentNotValidException → 400, generic Exception → 500

---

## Prompt 8: Create Configuration Files

**Input:**
> Create application.yml for production (MySQL) and test (H2) environments, plus database migration SQL

**AI Action:**
- `src/main/resources/application.yml` — MySQL datasource config
- `src/test/resources/application.yml` — H2 in-memory database config
- `src/main/resources/db/migration/V1__create_books_table.sql` — DDL with indexes and unique constraint on ISBN
- `src/test/resources/db/migration/V1__create_books_table.sql` — H2-compatible DDL

---

## Prompt 9: Create Unit Tests

**Input:**
> Write unit tests for all layers aiming for ≥ 80% code coverage

**AI Action:**
- `BookMapperTest.java` — tests all 3 mapping methods (toEntity, mergeToEntity, toResponse)
- `BookServiceTest.java` — tests create, getById, getByIsbn, getAll, search, update, delete, and error scenarios
- `BookRepositoryTest.java` — tests all repository methods with H2 in-memory database
- `GlobalExceptionHandlerTest.java` — tests all exception handler scenarios
- `AuthorSearchStrategyTest.java` — tests author search strategy
- `TitleSearchStrategyTest.java` — tests title search strategy
- `AvailabilitySearchStrategyTest.java` — tests availability search strategy

---

## Prompt 10: Create API Integration Tests

**Input:**
> Write API integration tests using Spring Boot Test and MockMvc

**AI Action:**
- `BookControllerApiTest.java` — full CRUD + search + error scenario integration tests
- Tests: create book, get by id, get by isbn, get all, search by author/title/available, update, delete, not found, duplicate ISBN, validation errors

---

## Project Structure

```
book-catalog-service/
├── pom.xml
├── AI_PROMPTS.md
├── mvnw / mvnw.cmd
├── start.bat
├── run_test.bat / run_test.ps1 / run_tests.bat
└── src/
    ├── main/
    │   ├── java/com/example/bookcatalog/
    │   │   ├── BookCatalogApplication.java
    │   │   ├── controller/
    │   │   │   ├── BookController.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── domain/
    │   │   │   ├── dto/
    │   │   │   │   ├── BookCreateRequest.java
    │   │   │   │   ├── BookUpdateRequest.java
    │   │   │   │   └── BookResponse.java
    │   │   │   ├── entity/
    │   │   │   │   └── Book.java
    │   │   │   ├── exception/
    │   │   │   │   ├── ApiError.java
    │   │   │   │   ├── BookNotFoundException.java
    │   │   │   │   └── DuplicateIsbnException.java
    │   │   │   └── mapper/
    │   │   │       └── BookMapper.java
    │   │   ├── repository/
    │   │   │   └── BookRepository.java
    │   │   └── service/
    │   │       ├── BookService.java
    │   │       └── strategy/
    │   │           ├── BookSearchStrategy.java
    │   │           ├── AuthorSearchStrategy.java
    │   │           ├── TitleSearchStrategy.java
    │   │           └── AvailabilitySearchStrategy.java
    │   └── resources/
    │       ├── application.yml
    │       ├── com/example/bookcatalog/repository/BookRepository.xml
    │       └── db/migration/V1__create_books_table.sql
    └── test/
        ├── java/com/example/bookcatalog/
        │   ├── controller/
        │   │   ├── BookControllerApiTest.java
        │   │   └── GlobalExceptionHandlerTest.java
        │   ├── domain/mapper/
        │   │   └── BookMapperTest.java
        │   ├── repository/
        │   │   └── BookRepositoryTest.java
        │   └── service/
        │       ├── BookServiceTest.java
        │       └── strategy/
        │           ├── AuthorSearchStrategyTest.java
        │           ├── TitleSearchStrategyTest.java
        │           └── AvailabilitySearchStrategyTest.java
        └── resources/
            ├── application.yml
            └── db/migration/V1__create_books_table.sql
```

---

## API Endpoints

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/v1/books` | Create a new book | 201 Created |
| GET | `/api/v1/books` | Get all books | 200 OK |
| GET | `/api/v1/books/{id}` | Get book by ID | 200 OK / 404 |
| GET | `/api/v1/books/isbn/{isbn}` | Get book by ISBN | 200 OK / 404 |
| GET | `/api/v1/books/search?type=author&keyword=xxx` | Search books by author | 200 OK |
| GET | `/api/v1/books/search?type=title&keyword=xxx` | Search books by title | 200 OK |
| GET | `/api/v1/books/search?type=available&keyword=available` | Search available books | 200 OK |
| GET | `/api/v1/books/search?type=available&keyword=unavailable` | Search unavailable books | 200 OK |
| PUT | `/api/v1/books/{id}` | Update a book | 200 OK / 404 / 409 |
| DELETE | `/api/v1/books/{id}` | Delete a book | 204 No Content |

---

## Design Patterns Summary

### 1. Builder Pattern
- **Files:** `BookMapper.java`, `Book.java`, `BookCreateRequest.java`, `BookUpdateRequest.java`, `BookResponse.java`
- **Usage:** Lombok `@Builder` generates builder classes for constructing Book entities and DTOs. `BookMapper` uses these builders for Entity ↔ DTO conversion.
- **Benefit:** Clean, readable object construction; immutable-like creation with optional parameters.

### 2. Strategy Pattern
- **Files:** `BookSearchStrategy.java` (interface), `AuthorSearchStrategy.java`, `TitleSearchStrategy.java`, `AvailabilitySearchStrategy.java`, `BookService.java`
- **Usage:** `BookService` injects a `List<BookSearchStrategy>`. When `searchBooks(type, keyword)` is called, it iterates strategies to find the one that `supports(type)` and delegates the search.
- **Benefit:** Open/Closed Principle — new search types can be added without modifying existing code. Simply create a new `@Component` implementing `BookSearchStrategy`.

---

## Test Coverage

| Test Class | Type | Scenarios |
|---|---|---|
| `BookControllerApiTest.java` | API Integration | Full CRUD, search, error handling, validation |
| `BookServiceTest.java` | Unit (Mockito) | All service methods, edge cases, exceptions |
| `BookRepositoryTest.java` | Integration (H2) | All repository methods |
| `BookMapperTest.java` | Unit | All mapping methods |
| `GlobalExceptionHandlerTest.java` | Unit | All exception types |
| `AuthorSearchStrategyTest.java` | Unit | Author search logic |
| `TitleSearchStrategyTest.java` | Unit | Title search logic |
| `AvailabilitySearchStrategyTest.java` | Unit | Availability search logic |

**Coverage Tool:** JaCoCo (configured in pom.xml)

---

## How to Run

```bash
# Build and run all tests
./mvnw clean test

# Run tests with coverage report
./mvnw clean test jacoco:report

# View coverage report (open in browser)
# target/site/jacoco/index.html

# Start the service
./mvnw spring-boot:run

# Or use the batch script
.\start.bat
```

---

## Technology Stack

| Component | Technology |
|---|---|
| Framework | Spring Boot 2.7.18 |
| ORM | MyBatis 2.3.2 |
| Database (Prod) | MySQL 8.x |
| Database (Test) | H2 In-Memory |
| Validation | Jakarta Validation (Hibernate Validator) |
| Code Generation | Lombok |
| Testing | JUnit 5, Mockito, Spring Boot Test |
| Coverage | JaCoCo 0.8.12 |
| Build Tool | Maven (with wrapper) |
| Java Version | 1.8+ |