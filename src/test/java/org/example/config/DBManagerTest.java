package org.example.config;

import org.example.config.DBConfig;
import org.example.config.DBManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DBManagerTest {
    @Mock
    private DBConfig config;

    @InjectMocks
    private DBManager dbManager;

    @Test
    void getConnection_GetUrlToInexistentDataBase_ShouldThrowSQLException()
    {
        when(config.getUrl()).thenReturn("jdbc:invalid-url");
        when(config.getUsername()).thenReturn("root");
        when(config.getPassword()).thenReturn("secret");

        assertThrows(SQLException.class, () -> dbManager.getConnection());
    }
}