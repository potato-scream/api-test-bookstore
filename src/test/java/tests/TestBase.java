package tests;

import config.CredentialsConfig;
import io.restassured.RestAssured;
import java.io.FileReader;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {
  public static CredentialsConfig config =
      ConfigFactory.create(CredentialsConfig.class, System.getProperties());

  @BeforeAll
  static void setUp() {
    // --- НАЧАЛО ДИАГНОСТИЧЕСКОГО БЛОКА ---
    try (FileReader reader = new FileReader("src/test/resources/config/credentials.properties")) {
      Properties properties = new Properties();
      properties.load(reader);
      System.out.println("Manual check - Login from file: " + properties.getProperty("login"));
      System.out.println(
          "Manual check - Password from file: " + properties.getProperty("password"));
    } catch (Exception e) {
      System.out.println("Manual check FAILED with error: " + e.getMessage());
    }
    // --- КОНЕЦ ДИАГНОСТИЧЕСКОГО БЛОКА ---

    System.out.println("Owner - Login from config: " + config.login());
    System.out.println("Owner - Password from config: " + config.password());
    RestAssured.baseURI = "https://demoqa.com";
  }
}
