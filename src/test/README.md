# ImmoFDS Backend - Test Structure

## Overview

The test suite is organized in 4 categories, from fastest/simplest to slowest/most complete.

| Category | Annotations | Spring Context | Speed | What's real, what's fake |
|---|---|---|---|---|
| Pure unit | none | None | ~1ms | Class under test is real, dependencies mocked manually |
| Service | `@ExtendWith(MockitoExtension.class)` | None | ~10ms | Service is real, all dependencies are Mockito mocks |
| Controller | `@WebMvcTest` | Partial (web layer only) | ~2s | MVC + validation + JSON real, services mocked |
| Integration | `@SpringBootTest` + Testcontainers | Full | ~10-30s | Everything real, real PostgreSQL in Docker |

---

## 1. Pure Unit Tests

**Files:** `BelgianPostalCodeValidatorTest`, `PropertySpecificationTest`

No Spring, no application context, no beans. The class under test is instantiated manually.

```java
class BelgianPostalCodeValidatorTest {

    private BelgianPostalCodeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BelgianPostalCodeValidator();        // manual instantiation
        context = mock(ConstraintValidatorContext.class);     // mock the Jakarta interface
    }

    @Test
    void validBelgianPostalCodes_shouldBeValid(String postalCode) {
        assertThat(validator.isValid(postalCode, context)).isTrue();
    }
}
```

### Key concepts

- **No annotations on the class** - JUnit 5 discovers test methods via `@Test` alone.
- **`@ParameterizedTest` + `@ValueSource`** - runs the same test method with multiple inputs, avoiding duplication:
  ```java
  @ParameterizedTest
  @ValueSource(strings = {"1000", "1050", "4000", "7000", "9999"})
  void validBelgianPostalCodes_shouldBeValid(String postalCode) { ... }
  ```
- **AssertJ** (`assertThat(...)`) - fluent assertion library included by `spring-boot-starter-test`. More readable than JUnit's `assertEquals`:
  ```java
  assertThat(result).isTrue();          // AssertJ
  assertTrue(result);                    // JUnit (less readable)
  ```

---

## 2. Service Tests (Mockito)

**Files:** `ContactRequestServiceTest`, `AuthServiceTest`, `UserServiceTest`, `PropertyServiceTest`

Test service classes in isolation by replacing all their dependencies with Mockito mocks. No Spring context is loaded.

```java
@ExtendWith(MockitoExtension.class)                          // 1
class ContactRequestServiceTest {

    @Mock                                                     // 2
    private ContactRequestRepository contactRequestRepository;

    @Mock
    private ContactRequestMapper contactRequestMapper;

    @InjectMocks                                              // 3
    private ContactRequestService contactRequestService;
}
```

### Annotations explained

1. **`@ExtendWith(MockitoExtension.class)`** - tells JUnit 5 to activate Mockito's annotation processing (`@Mock`, `@InjectMocks`). This replaces the old JUnit 4 `@RunWith(MockitoJUnitRunner.class)`.

2. **`@Mock`** - creates a fake object. By default all methods return `null`/`0`/`false`. All calls are recorded for later verification.

3. **`@InjectMocks`** - creates a **real** instance of the service and injects all `@Mock` fields into its constructor. The service logic runs for real, but everything it calls is fake.

### Test method pattern (Arrange / Act / Assert)

```java
@Test
void createGeneralContact_shouldSaveAndReturn() {
    // ARRANGE - program mock behavior
    when(contactRequestMapper.toEntity(request)).thenReturn(entity);
    when(contactRequestRepository.save(entity)).thenReturn(entity);
    when(contactRequestMapper.toResponse(entity)).thenReturn(response);

    // ACT - call the real service method
    ContactRequestResponse result = contactRequestService.createGeneralContact(request);

    // ASSERT - check the result and verify interactions
    assertThat(result).isNotNull();
    verify(contactRequestRepository).save(entity);
}
```

### Key Mockito APIs

| API | Purpose |
|---|---|
| `when(mock.method(args)).thenReturn(value)` | Program what a mock returns when called |
| `when(...).thenThrow(exception)` | Program a mock to throw an exception |
| `when(...).thenAnswer(invocation -> ...)` | Dynamic return value based on arguments |
| `verify(mock).method(args)` | Assert that a mock method was called |
| `verify(mock, never()).method()` | Assert that a mock method was NOT called |
| `any()`, `any(Class.class)` | Argument matcher: accept any value |
| `mock(SomeClass.class)` | Create a mock inline (without `@Mock`) |

### Testing exceptions

```java
@Test
void getContactById_notFound_shouldThrow() {
    when(contactRequestRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> contactRequestService.getContactById(999L))
            .isInstanceOf(ResourceNotFoundException.class);
}
```

`assertThatThrownBy` is AssertJ's way to verify that a lambda throws a specific exception. You can chain `.hasMessageContaining("expiré")` to check the message too.

---

## 3. Controller Tests (`@WebMvcTest`)

**Files:** `PublicContactControllerTest`, `PublicPropertyControllerTest`

Controllers need Spring's HTTP machinery (routing, JSON serialization, validation). `@WebMvcTest` loads **only the web layer** - no database, no services, no repositories.

```java
@WebMvcTest(controllers = PublicContactController.class,                    // 1
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}))  // 2
@AutoConfigureMockMvc(addFilters = false)                                   // 3
class PublicContactControllerTest {

    @Autowired
    private MockMvc mockMvc;                                                // 4

    @MockitoBean                                                            // 5
    private ContactRequestService contactRequestService;
}
```

### Annotations explained

1. **`@WebMvcTest(controllers = ...)`** - loads a sliced Spring context containing only:
   - The specified controller
   - Spring MVC infrastructure (Jackson, validation, request mapping, exception handlers)
   - **NOT** services, **NOT** repositories, **NOT** database

   This is the key difference with `@SpringBootTest` - much faster because it skips the majority of the application.

2. **`excludeFilters`** - our `JwtAuthenticationFilter` is `@Component`-annotated, so `@WebMvcTest` tries to create it. But it depends on `JwtTokenProvider` -> `JwtConfig` -> application properties... a whole chain of beans irrelevant for public endpoint tests. `excludeFilters` tells Spring to skip those classes.

3. **`@AutoConfigureMockMvc(addFilters = false)`** - disables all servlet filters (including the Spring Security filter chain). Since we test public endpoints, we don't want requests blocked by JWT authentication.

4. **`MockMvc`** - simulates HTTP requests without starting a real HTTP server. No port, no Tomcat. Requests go directly through Spring's `DispatcherServlet` in memory.

5. **`@MockitoBean`** - Spring Boot 4.0 replacement for the deprecated `@MockBean`. Creates a Mockito mock **and registers it as a Spring bean**, replacing the real bean in the context. When the controller calls `contactRequestService.createGeneralContact(...)`, it hits the mock.

### Testing a successful request

```java
@Test
void submitGeneralContact_validRequest_shouldReturn201() throws Exception {
    // Program the mock service
    when(contactRequestService.createGeneralContact(any()))
            .thenReturn(mock(ContactRequestResponse.class));

    // Raw JSON simulating what the frontend sends
    String json = """
            {
                "firstName": "Jean",
                "lastName": "Dupont",
                "email": "jean@example.com",
                "phone": "+32 470 12 34 56",
                "message": "Bonjour, je souhaite des informations."
            }
            """;

    // Perform HTTP POST and verify status
    mockMvc.perform(post("/api/v1/public/contacts/general")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isCreated());                    // HTTP 201
}
```

### Testing validation (bad request)

```java
@Test
void submitGeneralContact_missingFields_shouldReturn400() throws Exception {
    String json = """
            { "firstName": "", "email": "invalid" }
            """;

    mockMvc.perform(post("/api/v1/public/contacts/general")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isBadRequest());                 // HTTP 400
}
```

**No `when(...)` setup needed here.** The Jakarta validation annotations on the DTO (`@NotBlank`, `@Email`) reject the request *before* the controller method is even called. Spring returns 400 automatically. This tests that validation annotations work end-to-end through the HTTP layer.

### Verifying JSON response content

```java
mockMvc.perform(get("/api/v1/public/properties"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].reference").value("IMM-2026-00001"))
        .andExpect(jsonPath("$.totalElements").value(1));
```

`jsonPath("$.field")` uses JSONPath expressions to dig into the response body and check specific fields. Common patterns:
- `$.field` - root-level field
- `$.content[0].name` - first element of an array, then a field
- `$` - the root (use with `.isArray()` to verify the response is an array)

### MockMvc cheat sheet

| Method | Purpose |
|---|---|
| `get("/path")`, `post("/path")` | HTTP method |
| `.contentType(MediaType.APPLICATION_JSON)` | Set Content-Type header |
| `.content(jsonString)` | Set request body |
| `.header("Authorization", "Bearer ...")` | Set any HTTP header |
| `.param("key", "value")` | Set query parameter |
| `status().isOk()` | Expect HTTP 200 |
| `status().isCreated()` | Expect HTTP 201 |
| `status().isBadRequest()` | Expect HTTP 400 |
| `status().isNotFound()` | Expect HTTP 404 |
| `jsonPath("$.field").value("x")` | Check a JSON field value |
| `jsonPath("$").isArray()` | Check response is a JSON array |

---

## 4. Integration Tests (Testcontainers)

**Requires Docker to run.** These tests load the **entire** Spring application context with a real PostgreSQL database running in a Docker container.

```java
@SpringBootTest                                      // full application context
@AutoConfigureMockMvc                                // provides MockMvc
@ActiveProfiles("test")                              // uses application-test.yml
@Testcontainers                                      // manages Docker containers
abstract class IntegrationTestBase {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource                           // injects DB URL into Spring config
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

### How it differs from `@WebMvcTest`

- `@SpringBootTest` loads **everything**: controllers, services, repositories, security, Flyway migrations
- A real PostgreSQL container is started via Docker (Testcontainers library)
- `@DynamicPropertySource` overrides the database connection properties at runtime with the container's random port
- Flyway runs the real migrations against the test database
- Tests exercise the full request lifecycle: HTTP -> Controller -> Service -> Repository -> PostgreSQL

---

## TestDataFactory

Shared utility class that creates pre-filled entities and DTOs with Belgian defaults:

```java
public final class TestDataFactory {

    public static Property createProperty() {
        Property property = new Property();
        property.setReference("IMM-2026-00001");
        property.setCity("Bruxelles");
        property.setProvince(Province.BRUXELLES_CAPITALE);
        // ... all fields with sensible defaults
        return property;
    }

    public static GeneralContactRequest createGeneralContactRequest() {
        return new GeneralContactRequest("Jean", "Dupont", "jean.dupont@example.com", ...);
    }
}
```

Avoids duplicating entity creation across test methods. Every test that needs a `Property` or `User` calls `TestDataFactory.createProperty()` instead of setting up 20 fields each time.

---

## Running Tests

```bash
# All unit tests (no Docker needed)
mvn test -Dtest="!be.feysdigitalservices.immofds.integration.*"

# Only controller tests
mvn test -Dtest="PublicContactControllerTest,PublicPropertyControllerTest"

# Only service tests
mvn test -Dtest="*ServiceTest"

# Everything including integration tests (Docker required)
mvn test

# With verbose output
mvn test -Dsurefire.useFile=false
```
