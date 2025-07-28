package tests;

import api.AccountApi;
import api.BookstoreApi;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.*;
import org.junit.jupiter.api.*;
import utils.TestData;

import java.util.Collections;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Feature("Book Management")
public class DeleteBooksTest extends TestBase {
    static final String ISBN_TO_ADD = "9781449325862";
    String userId;
    String token;
    LoginRequest credentials;

    AccountApi accountApi = new AccountApi();
    BookstoreApi bookstoreApi = new BookstoreApi();
    TestData data = new TestData();

    @BeforeEach
    void setup() {
        credentials = new LoginRequest(data.getUserName(), data.getPassword());
        RegistrationResponse registrationResponse = accountApi.registerUser(credentials);
        userId = registrationResponse.getUserId();

        GenerateTokenResponse tokenResponse = accountApi.generateToken(credentials);
        token = tokenResponse.getToken();
    }

    @AfterEach
    void tearDown() {
        accountApi.deleteUser(userId, token);
    }

    @Test
    @Tag("demoqa")
    @Tag("bookstore")
    @Story("PROJ-1234: Book Collection Management")
    @TmsLink("TC-ACC-05")
    @DisplayName("Successful adding and deleting of a book from the user collection")
    void addAndDeleteBookTest() {
        step("Initial cleanup of user's book collection", () ->
                bookstoreApi.deleteAllBooks(userId, token)
        );

        List<AddBooksRequest.IsbnItem> isbnList = Collections.singletonList(
                new AddBooksRequest.IsbnItem(ISBN_TO_ADD)
        );
        AddBooksRequest addBookPayload = new AddBooksRequest(userId, isbnList);

        AddBooksResponse addBooksResponse = step("Add a book to the collection", () ->
                bookstoreApi.addBook(addBookPayload, token)
        );

        step("Verify successful response on book addition", () -> {
            assertNotNull(addBooksResponse);
            assertNotNull(addBooksResponse.getBooks());
            assertFalse(addBooksResponse.getBooks().isEmpty());
            assertEquals(ISBN_TO_ADD, addBooksResponse.getBooks().get(0).getIsbn());
        });

        step("Delete all books from the collection", () ->
                bookstoreApi.deleteAllBooks(userId, token)
        );

        UserAccountResponse userInfo = step("Get user info to verify deletion", () ->
                accountApi.getUserInfo(userId, token)
        );

        step("Verify that the book collection is empty for the correct user", () -> {
            assertThat(userInfo).isNotNull()
                    .satisfies(user -> {
                        assertThat(user.getUserId()).isEqualTo(userId);
                        assertThat(user.getUsername()).isEqualTo(credentials.getUserName());
                        assertThat(user.getBooks()).isEmpty();
                    });
        });
    }
}
