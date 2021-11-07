package sample;

import org.junit.jupiter.api.Assertions;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class SQLTest {

    @org.junit.jupiter.api.Test
    void login() {
        SQL sql = new SQL();
        assertNotNull(sql.login("700-25234", "1234"));
    }

    @org.junit.jupiter.api.Test
    void register() {
        SQL sql = new SQL();
        assertNotNull(sql.register("alessio", "venturini", "alessio.venturini@noser.com",
                                    "sattler 19",new Date(2018,12, 10), "1234"));
    }
}