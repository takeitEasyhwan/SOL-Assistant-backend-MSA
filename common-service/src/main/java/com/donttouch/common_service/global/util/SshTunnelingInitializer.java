package com.donttouch.common_service.global.util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Properties;

import static java.lang.System.exit;

@Slf4j
@Profile("stg")
@Component
@ConfigurationProperties(prefix = "ssh")
@Validated
@Setter
public class SshTunnelingInitializer {

    private String remoteJumpHost;
    private String user;
    private int sshPort;
    private String databaseUrl;
    private int databasePort;

    private Session session;

    @PreDestroy
    public void closeSSH() {
        session.disconnect();
    }

    public Integer buildSshConnection() {
        Integer forwardedPort = null;

        try {
            log.info("{}@{}:{}:{} with privateKey", user, remoteJumpHost, sshPort, databasePort);

            JSch jSch = new JSch();

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);


            forwardedPort = session.setPortForwardingL(0, databaseUrl, databasePort);

        } catch (Exception e) {
            this.closeSSH();
            exit(1);
        }

        return forwardedPort;
    }
}
