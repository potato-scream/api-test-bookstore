package tests;

import api.AccountApi;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import models.ErrorResponse;
import models.GenerateTokenResponse;
import models.LoginRequest;
import models.RegistrationResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import utils.TestData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Account Management")
public class AccountTests extends TestBase {

    private final TestData data = new TestData();
    private final AccountApi accountApi = new AccountApi();
    private final List<UserCleanupData> usersToCleanup = new ArrayList<>();

    static Stream<Arguments> emptyDataProviderForLoginRequest() {
        TestData localData = new TestData();
        String userName = localData.getUserName();
        String password = localData.getPassword();
        return Stream.of(
                Arguments.of(new LoginRequest(userName, "")),
                Arguments.of(new LoginRequest(userName, null)),
                Arguments.of(new LoginRequest("", password)),
                Arguments.of(new LoginRequest(null, password))
        );
    }

    @AfterEach
    void cleanupCreatedUsers() {
        for (UserCleanupData user : usersToCleanup) {
            accountApi.deleteUser(user.userId(), user.token());
        }
        usersToCleanup.clear();
    }

    @Test
    @Tag("POSITIVE")
    @Story("User Registration")
    @DisplayName("Successful new user registration")
    void successfulRegistrationTest() {
        LoginRequest credentials = new LoginRequest(data.getUserName(), data.getPassword());

        RegistrationResponse registrationResponse = accountApi.registerUser(credentials);

        step("Add user to cleanup list", () -> {
            GenerateTokenResponse tokenResponse = accountApi.generateToken(credentials);
            usersToCleanup.add(new UserCleanupData(registrationResponse.getUserId(), tokenResponse.getToken()));
        });

        step("Verify response data", () -> {
            assertThat(registrationResponse.getUserId()).matches(data.getUUIDPattern());
            assertThat(registrationResponse.getUsername()).isEqualTo(credentials.getUserName());
            assertThat(registrationResponse.getBooks()).isEmpty();
        });
    }

    @Test
    @Tag("NEGATIVE")
    @Story("User Registration")
    @DisplayName("Registration fails when password policy is not met")
    void unsuccessfulRegistrationWithIncorrectPasswordTest() {
        LoginRequest credentials = new LoginRequest(data.getUserName(), data.getRandomString(10));

        ErrorResponse errorResponse = accountApi.registerUserExpectingError(credentials, 400);

        step("Verify error response", () -> {
            assertThat(errorResponse.getCode()).isEqualTo("1300");
            assertThat(errorResponse.getMessage()).contains("Passwords must have at least one non alphanumeric character");
        });
    }

    @Test
    @Tag("NEGATIVE")
    @Story("User Registration")
    @DisplayName("Registration fails if user already exists")
    void unsuccessfulRegistrationUserAlreadyExistsTest() {
        LoginRequest credentials = new LoginRequest(data.getUserName(), data.getPassword());

        RegistrationResponse registrationResponse = step("Setup: create a new user", () ->
                accountApi.registerUser(credentials)
        );
        step("Add user to cleanup list", () -> {
            GenerateTokenResponse tokenResponse = accountApi.generateToken(credentials);
            usersToCleanup.add(new UserCleanupData(registrationResponse.getUserId(), tokenResponse.getToken()));
        });

        ErrorResponse errorResponse = step("Attempt to register the same user again", () ->
                accountApi.registerUserExpectingError(credentials, 406)
        );

        step("Verify error response", () -> {
            assertThat(errorResponse.getCode()).isEqualTo("1204");
            assertThat(errorResponse.getMessage()).isEqualTo("User exists!");
        });
    }

    @ParameterizedTest
    @MethodSource("emptyDataProviderForLoginRequest")
    @Tag("NEGATIVE")
    @Story("User Registration")
    @DisplayName("Registration fails with empty username or password")
    void unsuccessfulRegistrationEmptyUserNameOrPasswordTest(LoginRequest request) {
        ErrorResponse errorResponse = accountApi.registerUserExpectingError(request, 400);

        step("Verify error response", () -> {
            assertThat(errorResponse.getCode()).isEqualTo("1200");
            assertThat(errorResponse.getMessage()).isEqualTo("UserName and Password required.");
        });
    }

    @Test
    @Tag("POSITIVE")
    @Story("User Deletion")
    @DisplayName("Successful user deletion")
    void successfulDeleteUserTest() {
        LoginRequest credentials = new LoginRequest(data.getUserName(), data.getPassword());

        RegistrationResponse registrationResponse = step("Setup: create a new user", () ->
                accountApi.registerUser(credentials)
        );
        GenerateTokenResponse tokenResponse = step("Setup: generate token", () ->
                accountApi.generateToken(credentials)
        );

        step("Perform user deletion", () ->
                accountApi.deleteUser(registrationResponse.getUserId(), tokenResponse.getToken())
        );

        step("Verify user is deleted", () -> {
            ErrorResponse error = accountApi.checkNonExistentUserIsAuthorized(credentials);
            assertThat(error.getCode()).isEqualTo("1207");
            assertThat(error.getMessage()).isEqualTo("User not found!");
        });
    }

    @Test
    @Tag("NEGATIVE")
    @Story("User Deletion")
    @DisplayName("Deletion fails with an invalid token")
    void deleteUserWithInvalidTokenTest() {
        LoginRequest credentials = new LoginRequest(data.getUserName(), data.getPassword());

        RegistrationResponse registrationResponse = step("Setup: create a new user", () ->
                accountApi.registerUser(credentials)
        );
        step("Add user to cleanup list", () -> {
            GenerateTokenResponse tokenResponse = accountApi.generateToken(credentials);
            usersToCleanup.add(new UserCleanupData(registrationResponse.getUserId(), tokenResponse.getToken()));
        });

        ErrorResponse errorResponse = step("Attempt to delete user with a random token", () ->
                accountApi.deleteUserExpectingError(registrationResponse.getUserId(), data.getRandomString(170))
        );

        step("Verify error response", () -> {
            assertThat(errorResponse.getCode()).isEqualTo("1200");
            assertThat(errorResponse.getMessage()).isEqualTo("User not authorized!");
        });
    }

    @Test
    @Tag("POSITIVE")
    @Story("Token Generation")
    @DisplayName("Successful token generation")
    void successfulGenerationTokenTest() {
        LoginRequest credentials = new LoginRequest(data.getUserName(), data.getPassword());

        RegistrationResponse registrationResponse = step("Setup: create a new user", () ->
                accountApi.registerUser(credentials)
        );

        GenerateTokenResponse generateTokenResponse = step("Perform token generation", () ->
                accountApi.generateToken(credentials)
        );

        step("Verify token response", () -> {
            assertThat(generateTokenResponse.getToken()).isNotBlank();
            assertThat(generateTokenResponse.getExpires()).matches(data.getDateTimePattern());
            assertThat(generateTokenResponse.getStatus()).isEqualTo("Success");
            assertThat(generateTokenResponse.getResult()).isEqualTo("User authorized successfully.");
        });

        step("Cleanup: delete created user", () -> {
            accountApi.deleteUser(registrationResponse.getUserId(), generateTokenResponse.getToken());
        });
    }

    private record UserCleanupData(String userId, String token) {
    }
}
