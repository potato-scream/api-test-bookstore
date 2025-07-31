/* (C) 2025 potato-scream */
package tests;

import config.CredentialsConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {
  public static String baseUri = "https://demoqa.com";
  public static CredentialsConfig config = ConfigFactory.create(CredentialsConfig.class);

  @BeforeAll
  static void setUp() {
    RestAssured.baseURI = baseUri;
  }
}
