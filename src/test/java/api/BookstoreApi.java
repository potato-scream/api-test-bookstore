/* (C) 2025 potato-scream */
package api;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static specs.BookstoreSpec.*;

import io.qameta.allure.Step;
import models.AddBooksRequest;
import models.AddBooksResponse;

public class BookstoreApi {
  private static final String BOOK_PATH = "/BookStore/v1/Books";

  @Step("Delete all books from the user's collection via API")
  public void deleteAllBooks(String userId, String token) {
    given(authenticatedRequestSpec(token))
        .queryParam("UserId", userId)
        .when()
        .delete(BOOK_PATH)
        .then()
        .spec(statusCodeResponseSpec(204));
  }

  @Step("Add a book to the user's collection via API")
  public AddBooksResponse addBook(AddBooksRequest addBookPayload, String token) {
    return given(authenticatedJsonRequestSpec(token))
        .body(addBookPayload)
        .when()
        .post(BOOK_PATH)
        .then()
        .spec(statusCodeResponseSpec(201))
        .body(matchesJsonSchemaInClasspath("schemas/add-books-schema.json"))
        .extract()
        .as(AddBooksResponse.class);
  }
}
