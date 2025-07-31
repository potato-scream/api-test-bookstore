/* (C) 2025 potato-scream */
package utils;

import com.github.javafaker.Faker;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestData {

  private final Faker faker = new Faker();

  public String getUserName() {
    return faker.name().username();
  }

  public String getPassword() {
    // Надежно генерируем каждую обязательную часть пароля
    String upperCase = faker.regexify("[A-Z]{1}");
    String lowerCase = faker.regexify("[a-z]{1}");
    String digit = faker.number().digit();
    String specialChar = faker.options().option("!", "@", "#", "$", "%", "^", "&", "*");
    String otherChars = faker.lorem().characters(4, 12);
    String combinedChars = upperCase + lowerCase + digit + specialChar + otherChars;
    List<String> chars = Arrays.asList(combinedChars.split(""));
    Collections.shuffle(chars);

    return String.join("", chars);
  }

  public String getRandomString(int length) {
    return faker.lorem().characters(length);
  }

  public String getUUIDPattern() {
    return "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
  }

  public String getDateTimePattern() {
    return "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z";
  }
}
