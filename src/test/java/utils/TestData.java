package utils;

import com.github.javafaker.Faker;

public class TestData {

    private final Faker faker = new Faker();

    public String getUserName() {
        return faker.name().username();
    }

    public String getPassword() {

        return faker.internet().password(8, 16, true, false, true) + "!";
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
