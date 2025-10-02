//package com.donttouch.common_service.global.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.sql.DataSource;
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//
//@Configuration
//public class DataSourceConfig {
//
//    @Autowired
//    private SSHTunnelConfig sshTunnelConfig;
//
//    @Value("${spring.datasource.driver-class-name}")
//    private String driverClassName;
//
//    @Value("${spring.datasource.username}")
//    private String username;
//
//    @Value("${spring.datasource.password}")
//    private String password;
//
//    @Value("${spring.datasource.url}")
//    private String originalUrl;
//
//    @Bean
//    @Primary
//    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod")
//    public DataSource dataSourceWithSSHTunnel() {
//        HikariConfig config = new HikariConfig();
//
//        // SSH 터널을 통한 데이터베이스 URL 생성
//        int tunnelPort = sshTunnelConfig.getLocalPort();
//        String tunnelUrl = "jdbc:mysql://localhost:" + tunnelPort + "/sinhan-assistant";
//
//        config.setJdbcUrl(tunnelUrl);
//        config.setDriverClassName(driverClassName);
//        config.setUsername(username);
//        config.setPassword(password);
//
//        // 커넥션 풀 설정
//        config.setMaximumPoolSize(10);
//        config.setMinimumIdle(5);
//        config.setConnectionTimeout(30000);
//        config.setIdleTimeout(600000);
//        config.setMaxLifetime(1800000);
//
//        return new HikariDataSource(config);
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
//    public DataSource dataSourceForDev() {
//        HikariConfig config = new HikariConfig();
//
//        config.setJdbcUrl(originalUrl);
//        config.setDriverClassName(driverClassName);
//        config.setUsername(username);
//        config.setPassword(password);
//
//        // 개발 환경용 설정
//        config.setMaximumPoolSize(5);
//        config.setMinimumIdle(2);
//        config.setConnectionTimeout(20000);
//
//        return new HikariDataSource(config);
//    }
//}
