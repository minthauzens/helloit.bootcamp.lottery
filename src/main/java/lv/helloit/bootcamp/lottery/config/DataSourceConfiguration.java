package lv.helloit.bootcamp.lottery.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/lotteryDB";
    public static final String DB_USERNAME = "lotteryDBUser";
    public static final String DB_PASSWORD = "mX560^UveyUd&#eH";

    public static final String DB_URL_DEV = "jdbc:postgresql://localhost:5432/lotteryDBDev";
    public static final String DB_USERNAME_DEV = "lotteryDBUserDev";
    public static final String DB_PASSWORD_DEV = "q4cqu&&AmbEV43q%";

    @Bean
    @ConditionalOnProperty(name = "lottery.db.postgresql.enabled", havingValue = "true")
    DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(DB_URL);
        ds.setUsername(DB_USERNAME);
        ds.setPassword(DB_PASSWORD);
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
    @Bean
    @ConditionalOnProperty(name = "lottery.db.postgresql.enabled", havingValue = "false")
    DataSource dataSourceDev() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(DB_URL_DEV);
        ds.setUsername(DB_USERNAME_DEV);
        ds.setPassword(DB_PASSWORD_DEV);
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
