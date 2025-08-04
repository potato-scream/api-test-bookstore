package api;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.emptyOrNullString;
import static specs.BookstoreSpec.*;

import io.qameta.allure.Step;
import models.*;

public class AccountApi {

  private static final String ACCOUNT_PATH = "/Account/v1";

  @Step("Get user info")
  public UserAccountResponse getUserInfo(String userId, String token) {
    return given(authenticatedRequestSpec(token))
        .when()
        .get(ACCOUNT_PATH + "/User/" + userId)
        .then()
        .spec(statusCodeResponseSpec(200))
        .body(matchesJsonSchemaInClasspath("schemas/user-account-schema.json"))
        .extract()
        .as(UserAccountResponse.class);
  }

  @Step("Register a new user")
  public RegistrationResponse registerUser(LoginRequest credentials) {
    return given(jsonRequestSpec())
        .body(credentials)
        .when()
        .post(ACCOUNT_PATH + "/User")
        .then()
        .spec(statusCodeResponseSpec(201))
        .body(matchesJsonSchemaInClasspath("schemas/registration-schema.json"))
        .extract()
        .as(RegistrationResponse.class);
  }

  @Step("Attempt to register a user and expect an error")
  public ErrorResponse registerUserExpectingError(LoginRequest credentials, int statusCode) {
    return given(jsonRequestSpec())
        .body(credentials)
        .when()
        .post(ACCOUNT_PATH + "/User")
        .then()
        .spec(statusCodeResponseSpec(statusCode))
        .extract()
        .as(ErrorResponse.class);
  }

  @Step("Generate user token")
  public GenerateTokenResponse generateToken(LoginRequest credentials) {
    return given(jsonRequestSpec())
        .body(credentials)
        .when()
        .post(ACCOUNT_PATH + "/GenerateToken")
        .then()
        .spec(statusCodeResponseSpec(200))
        .body(matchesJsonSchemaInClasspath("schemas/generate-token-schema.json"))
        .extract()
        .as(GenerateTokenResponse.class);
  }

  @Step("Log in and get a token")
  public LoginResponse login(LoginRequest credentials) {
    return given(jsonRequestSpec())
        .body(credentials)
        .when()
        .post(ACCOUNT_PATH + "/Login")
        .then()
        .spec(statusCodeResponseSpec(200))
        .body(matchesJsonSchemaInClasspath("schemas/login-schema.json"))
        .extract()
        .as(LoginResponse.class);
  }

  @Step("Delete user")
  public void deleteUser(String userId, String token) {
    given(authenticatedRequestSpec(token))
        .when()
        .delete(ACCOUNT_PATH + "/User/" + userId)
        .then()
        .spec(statusCodeResponseSpec(204))
        .body(emptyOrNullString());
  }

  @Step("Attempt to check authorization for a non-existent user")
  public ErrorResponse checkNonExistentUserIsAuthorized(LoginRequest credentials) {
    return given(jsonRequestSpec())
        .body(credentials)
        .when()
        .post(ACCOUNT_PATH + "/Authorized")
        .then()
        .spec(statusCodeResponseSpec(404))
        .extract()
        .as(ErrorResponse.class);
  }

  @Step("Attempt to delete a user with an invalid token")
  public ErrorResponse deleteUserExpectingError(String userId, String token) {
    return given(authenticatedRequestSpec(token))
        .delete(ACCOUNT_PATH + "/User/" + userId)
        .then()
        .spec(statusCodeResponseSpec(401))
        .extract()
        .as(ErrorResponse.class);
  }

  @Step("Attempt to get user info for a non-existent user")
  public ErrorResponse getUserInfoExpectingError(String userId, String token) {
    return given(authenticatedRequestSpec(token))
        .when()
        .get(ACCOUNT_PATH + "/User/" + userId)
        .then()
        .spec(statusCodeResponseSpec(401))
        .extract()
        .as(ErrorResponse.class);
  }
}
