package api;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static specs.BookstoreSpec.*;

import io.qameta.allure.Step;
import models.AddBooksRequest;
import models.AddBooksResponse;
import models.BookResponse;
import models.ErrorResponse;
import models.UpdateBookRequest;
import models.UserAccountResponse;

public class BookstoreApi {
  private static final String BOOKSTORE_PATH = "/BookStore/v1";

  @Step("Delete all books from the user's collection")
  public void deleteAllBooks(String userId, String token) {
    given(authenticatedRequestSpec(token))
        .queryParam("UserId", userId)
        .when()
        .delete(BOOKSTORE_PATH + "/Books")
        .then()
        .spec(statusCodeResponseSpec(204));
  }

  @Step("Add a book to the user's collection")
  public AddBooksResponse addBook(AddBooksRequest addBookPayload, String token) {
    return given(authenticatedJsonRequestSpec(token))
        .body(addBookPayload)
        .when()
        .post(BOOKSTORE_PATH + "/Books")
        .then()
        .spec(statusCodeResponseSpec(201))
        .body(matchesJsonSchemaInClasspath("schemas/add-books-schema.json"))
        .extract()
        .as(AddBooksResponse.class);
  }

  @Step("Get specific book info")
  public BookResponse getBook(String isbn) {
    return given(jsonRequestSpec())
        .queryParam("ISBN", isbn)
        .when()
        .get(BOOKSTORE_PATH + "/Book")
        .then()
        .spec(statusCodeResponseSpec(200))
        .extract()
        .as(BookResponse.class);
  }

  @Step("Update a book in the user's collection")
  public UserAccountResponse updateBook(
      String currentIsbn, UpdateBookRequest payload, String token) {
    return given(authenticatedJsonRequestSpec(token))
        .pathParam("ISBN", currentIsbn)
        .body(payload)
        .when()
        .put(BOOKSTORE_PATH + "/Books/{ISBN}")
        .then()
        .spec(statusCodeResponseSpec(200))
        .extract()
        .as(UserAccountResponse.class);
  }

  @Step("Attempt to add a book and expect an error")
  public ErrorResponse addBookExpectingError(
      AddBooksRequest payload, String token, int statusCode) {
    return given(authenticatedJsonRequestSpec(token))
        .body(payload)
        .when()
        .post(BOOKSTORE_PATH + "/Books")
        .then()
        .spec(statusCodeResponseSpec(statusCode))
        .extract()
        .as(ErrorResponse.class);
  }
}
