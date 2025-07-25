package tests;

import api.AccountApi;
import api.BookstoreApi;
import io.qameta.allure.Story;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteBooksTest extends TestBase {
    static final String ISBN_TO_ADD = "9781449325862";
    static String userId;
    static String token;
    static AccountApi accountApi = new AccountApi();
    static BookstoreApi bookstoreApi = new BookstoreApi();

    @BeforeAll
    static void loginAndPrepare() {
        LoginRequest loginCredentials = new LoginRequest(login, password);

        LoginResponse loginResponse = accountApi.login(loginCredentials);

        step("Extract userId and token from login response", () -> {
            userId = loginResponse.getUserId();
            token = loginResponse.getToken();
        });

        step("Verify extracted data is not null or empty", () -> {
            assertNotNull(userId, "UserID should not be null after successful login");
            assertNotNull(token, "Token should not be null after successful login");
            assertFalse(userId.isEmpty(), "UserID should not be empty");
            assertFalse(token.isEmpty(), "Token should not be empty");
        });

        step("Initial cleanup of user's book collection", () ->
                bookstoreApi.deleteAllBooks(userId, token)
        );
    }

    @Test
    @Tag("demoqa")
    @Tag("bookstore")
    @Story("Book Management")
    @DisplayName("Successful adding and deleting of a book from the user collection")
    void addAndDeleteBookTest() {
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

        DeleteBooksResponse userInfo = step("Get user info to verify deletion", () ->
                accountApi.getUserInfo(userId, token)
        );

        step("Verify that the book collection is empty", () -> {
            assertNotNull(userInfo);
            assertNotNull(userInfo.getBooks());
            assertTrue(userInfo.getBooks().isEmpty(), "User's book list should be empty after deletion");
        });
    }
}
