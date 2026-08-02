package config;

import org.example.config.DBConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class DBConfigTest {
    private Dotenv dotenv;

    @BeforeEach
    void setUp() {
        dotenv = Mockito.mock(Dotenv.class);

        when(dotenv.get("DB_USER")).thenReturn("root");
        when(dotenv.get("DB_PASSWORD")).thenReturn("secret");
    }

    @Test
    void constructor_DBUrlIsEmpty_ShouldThrowIllegalStateException(){
        when(dotenv.get("DB_URL")).thenReturn("");

        assertThrows(IllegalStateException.class, () -> {new DBConfig(dotenv);});
    }

    @Test
    void constructor_ValidEnvVariable_ShouldCreateObject() {
        when(dotenv.get("DB_URL")).thenReturn("jdbc:postgresql://localhost:5432/database");

        DBConfig config = new DBConfig(dotenv);
        assertEquals("jdbc:postgresql://localhost:5432/database", config.getUrl());
        assertEquals("root", config.getUsername());
        assertEquals("secret", config.getPassword());
    }

}