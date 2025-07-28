package api;

import io.qameta.allure.Step;
import models.AddBooksRequest;
import models.AddBooksResponse;

import static io.restassured.RestAssured.given;
import static specs.BookstoreSpec.*;

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
                .extract().as(AddBooksResponse.class);
    }
}