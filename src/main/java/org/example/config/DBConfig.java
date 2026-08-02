package org.example.config;
import io.github.cdimascio.dotenv.Dotenv;

public class DBConfig {
    private final String url;
    private final String username;
    private final String password;

    public DBConfig()
    {
        this(Dotenv.load());
    }

    public DBConfig(Dotenv dotenv) {
        this.url = validateAndGet(dotenv, "DB_URL");
        this.username = validateAndGet(dotenv, "DB_USER");
        this.password = validateAndGet(dotenv, "DB_PASSWORD");
    }

    private String validateAndGet(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("DBConfig error " + key + " is missing or is empty!");
        }
        return value;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
