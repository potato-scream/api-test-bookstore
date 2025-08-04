package tests;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import api.AccountApi;
import api.BookstoreApi;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.Collections;
import java.util.List;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Feature("Book Management")
public class BookstoreTests extends TestBase {
  static final String GIT_POCKET_GUIDE_ISBN = "9781449325862";
  static final String LEARNING_JS_DESIGN_PATTERNS_ISBN = "9781449331818";
  static final String NON_EXISTENT_ISBN = "9781449325860";

  static String userId;
  static String token;

  static AccountApi accountApi = new AccountApi();
  static BookstoreApi bookstoreApi = new BookstoreApi();

  @BeforeAll
  static void setup() {
    // ИСПОЛЬЗУЕМ СТАТИЧНОГО ПОЛЬЗОВАТЕЛЯ ДЛЯ СТАБИЛЬНОСТИ
    LoginRequest credentials = new LoginRequest(config.login(), config.password());
    LoginResponse loginResponse = accountApi.login(credentials);
    userId = loginResponse.getUserId();
    token = loginResponse.getToken();
  }

  @Test
  @Tag("bookstore")
  @Story("Book Collection")
  @DisplayName("Successful adding and deleting of a book from the user collection")
  void addAndDeleteBookTest() {
    step(
        "Initial cleanup of user's book collection",
        () -> bookstoreApi.deleteAllBooks(userId, token));

    List<AddBooksRequest.IsbnItem> isbnList =
        Collections.singletonList(new AddBooksRequest.IsbnItem(GIT_POCKET_GUIDE_ISBN));
    AddBooksRequest addBookPayload = new AddBooksRequest(userId, isbnList);

    AddBooksResponse addBooksResponse =
        step("Add a book to the collection", () -> bookstoreApi.addBook(addBookPayload, token));

    step(
        "Verify successful response on book addition",
        () -> {
          assertNotNull(addBooksResponse);
          assertNotNull(addBooksResponse.getBooks());
          assertFalse(addBooksResponse.getBooks().isEmpty());
          assertEquals(GIT_POCKET_GUIDE_ISBN, addBooksResponse.getBooks().get(0).getIsbn());
        });

    step("Delete all books from the collection", () -> bookstoreApi.deleteAllBooks(userId, token));

    UserAccountResponse userInfo =
        step("Get user info to verify deletion", () -> accountApi.getUserInfo(userId, token));

    step(
        "Verify that the book collection is empty for the correct user",
        () -> {
          assertThat(userInfo)
              .isNotNull()
              .satisfies(
                  user -> {
                    assertThat(user.getUserId()).isEqualTo(userId);
                    assertThat(user.getUsername()).isEqualTo(config.login());
                    assertThat(user.getBooks()).isEmpty();
                  });
        });
  }

  @Test
  @Tag("bookstore")
  @Story("Book Info")
  @DisplayName("Successful retrieval of specific book information")
  void getSpecificBookInfoTest() {
    BookResponse bookResponse = bookstoreApi.getBook(GIT_POCKET_GUIDE_ISBN);

    step(
        "Verify book details",
        () -> {
          assertThat(bookResponse.getIsbn()).isEqualTo(GIT_POCKET_GUIDE_ISBN);
          assertThat(bookResponse.getTitle()).isEqualTo("Git Pocket Guide");
          assertThat(bookResponse.getAuthor()).isEqualTo("Richard E. Silverman");
          assertThat(bookResponse.getPublisher()).isEqualTo("O'Reilly Media");
        });
  }

  @Test
  @Tag("bookstore")
  @Story("Book Collection")
  @DisplayName("Successful update of a book in the user's collection")
  void updateBookInCollectionTest() {
    step(
        "Setup: add initial book to collection",
        () -> {
          List<AddBooksRequest.IsbnItem> isbnList =
              Collections.singletonList(new AddBooksRequest.IsbnItem(GIT_POCKET_GUIDE_ISBN));
          AddBooksRequest addBookPayload = new AddBooksRequest(userId, isbnList);
          bookstoreApi.addBook(addBookPayload, token);
        });

    UpdateBookRequest updatePayload =
        new UpdateBookRequest(userId, LEARNING_JS_DESIGN_PATTERNS_ISBN);
    UserAccountResponse updateResponse =
        bookstoreApi.updateBook(GIT_POCKET_GUIDE_ISBN, updatePayload, token);

    step(
        "Verify that the book was updated in the collection",
        () -> {
          assertThat(updateResponse.getBooks()).hasSize(1);
          assertThat(updateResponse.getBooks().get(0).getIsbn())
              .isEqualTo(LEARNING_JS_DESIGN_PATTERNS_ISBN);
        });

    step("Cleanup: delete all books", () -> bookstoreApi.deleteAllBooks(userId, token));
  }

  @Test
  @Tag("bookstore")
  @Story("Book Collection (Negative)")
  @DisplayName("Attempt to add a book with a non-existent ISBN")
  void addNonExistentIsbnTest() {
    List<AddBooksRequest.IsbnItem> isbnList =
        Collections.singletonList(new AddBooksRequest.IsbnItem(NON_EXISTENT_ISBN));
    AddBooksRequest addBookPayload = new AddBooksRequest(userId, isbnList);

    ErrorResponse errorResponse = bookstoreApi.addBookExpectingError(addBookPayload, token, 400);

    step(
        "Verify error message",
        () -> {
          assertThat(errorResponse.getCode()).isEqualTo("1205");
          assertThat(errorResponse.getMessage())
              .isEqualTo("ISBN supplied is not available in Books Collection!");
        });
  }
}
