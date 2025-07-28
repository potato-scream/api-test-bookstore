package tests;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
public class TestBase {
        public static String baseUri = "https://demoqa.com";

        @BeforeAll
        static void setUp() {
            RestAssured.baseURI = baseUri;
    }
}
