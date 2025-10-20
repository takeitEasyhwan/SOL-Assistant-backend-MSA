package com.donttouch.common_service.global.config.ssh;

import com.donttouch.common_service.global.config.database.DataSourceRouter;
import com.donttouch.common_service.global.util.SshTunnelingInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Profile("stg")
@Configuration
@RequiredArgsConstructor
public class SshDataSourceConfig {

    private final SshTunnelingInitializer initializer;

    /** ---------------- Master/Slave DataSource 생성 ---------------- **/

    @Bean(name = "writeDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public DataSourceProperties writeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "writeDataSource")
    public DataSource writeDataSource(@Qualifier("writeDataSourceProperties") DataSourceProperties props) {
        Integer forwardedPort = initializer.buildSshConnection(false);
        String url = props.getUrl().replace("[forwardedPort]", forwardedPort.toString());
        log.info("[SSH] Write DB connected through port forwarding: {}", url);
        return DataSourceBuilder.create()
                .url(url)
                .username(props.getUsername())
                .password(props.getPassword())
                .driverClassName(props.getDriverClassName())
                .build();
    }

    @Bean(name = "readDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSourceProperties readDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "readDataSource")
    public DataSource readDataSource(@Qualifier("readDataSourceProperties") DataSourceProperties props) {
        Integer forwardedPort = initializer.buildSshConnection(true);
        String url = props.getUrl().replace("[forwardedPort]", forwardedPort.toString());
        log.info("[SSH] Read DB connected through port forwarding: {}", url);
        return DataSourceBuilder.create()
                .url(url)
                .username(props.getUsername())
                .password(props.getPassword())
                .driverClassName(props.getDriverClassName())
                .build();
    }

    /** ---------------- Routing DataSource 설정 ---------------- **/
    @Bean
    @Primary
    public DataSource routingDataSource(
            @Qualifier("writeDataSource") DataSource writeDataSource,
            @Qualifier("readDataSource") DataSource readDataSource) {

        DataSourceRouter router = new DataSourceRouter();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("write", writeDataSource);
        targetDataSources.put("read", readDataSource);

        router.setTargetDataSources(targetDataSources);
        router.setDefaultTargetDataSource(writeDataSource); // 기본은 Master
        router.afterPropertiesSet();

        // LazyConnectionDataSourceProxy로 실제 Connection 열릴 때 라우팅
        return new LazyConnectionDataSourceProxy(router);
    }
}