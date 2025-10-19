package com.donttouch.internal_assistant_service.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.tracking-redis")
public class TrackingRedisConfig {

    private String host;
    private int port;
    private String username;
    private String password;

    @Bean(name = "trackingRedisTemplate")
    public StringRedisTemplate trackingRedisTemplate() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        if (password != null && !password.isEmpty()) {
            config.setPassword(password);
        }
        if (username != null && !username.isEmpty()) {
            config.setUsername(username);
        }

        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();

        return new StringRedisTemplate(factory);
    }

}
