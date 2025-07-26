package api;

import io.qameta.allure.Step;
import models.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static specs.BookstoreSpec.*;

public class AccountApi {

    @Step("Get user info via API")
    public UserAccountResponse getUserInfo(String userId, String token) {
        return given(authenticatedRequestSpec(token))
                .when()
                .get("/Account/v1/User/" + userId)
                .then()
                .spec(statusCodeResponseSpec(200))
                .extract().as(UserAccountResponse.class);
    }

    @Step("Register a new user via API")
    public RegistrationResponse registerUser(LoginRequest credentials) {
        return given(jsonRequestSpec())
                .body(credentials)
                .when()
                .post("/Account/v1/User")
                .then()
                .spec(statusCodeResponseSpec(201))
                .extract().as(RegistrationResponse.class);
    }

    @Step("Attempt to register a user and expect an error")
    public ErrorResponse registerUserExpectingError(LoginRequest credentials, int statusCode) {
        return given(jsonRequestSpec())
                .body(credentials)
                .when()
                .post("/Account/v1/User")
                .then()
                .spec(statusCodeResponseSpec(statusCode))
                .extract().as(ErrorResponse.class);
    }

    @Step("Generate user token via API")
    public GenerateTokenResponse generateToken(LoginRequest credentials) {
        return given(jsonRequestSpec())
                .body(credentials)
                .when()
                .post("/Account/v1/GenerateToken")
                .then()
                .spec(statusCodeResponseSpec(200))
                .extract().as(GenerateTokenResponse.class);
    }

    @Step("Log in via API and get a token")
    public LoginResponse login(LoginRequest credentials) {
        return given(jsonRequestSpec())
                .body(credentials)
                .when()
                .post("/Account/v1/Login")
                .then()
                .spec(statusCodeResponseSpec(200))
                .extract().as(LoginResponse.class);
    }

    @Step("Delete user via API")
    public void deleteUser(String userId, String token) {
        given(authenticatedRequestSpec(token))
                .when()
                .delete("/Account/v1/User/" + userId)
                .then()
                .spec(statusCodeResponseSpec(204))
                .body(emptyOrNullString());
    }

    @Step("Check user authorization via API")
    public Boolean checkUserIsAuthorized(LoginRequest credentials) {
        return given(jsonRequestSpec())
                .body(credentials)
                .when()
                .post("/Account/v1/Authorized")
                .then()
                .spec(statusCodeResponseSpec(200))
                .extract().as(Boolean.class);
    }

    @Step("Attempt to check authorization for a non-existent user")
    public ErrorResponse checkNonExistentUserIsAuthorized(LoginRequest credentials) {
        return given(jsonRequestSpec())
                .body(credentials)
                .when()
                .post("/Account/v1/Authorized")
                .then()
                .spec(statusCodeResponseSpec(404))
                .extract().as(ErrorResponse.class);
    }

    @Step("Attempt to delete a user with an invalid token")
    public ErrorResponse deleteUserExpectingError(String userId, String token) {
        return given(authenticatedRequestSpec(token))
                .delete("/Account/v1/User/" + userId)
                .then()
                .spec(statusCodeResponseSpec(401))
                .extract().as(ErrorResponse.class);
    }
}
