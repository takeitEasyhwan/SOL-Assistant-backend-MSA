package com.donttouch.common_service.global.util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import static java.lang.System.exit;

@Slf4j
@Profile("stg")
@Component
@ConfigurationProperties(prefix = "ssh")
@Validated
@Getter
@Setter
public class SshTunnelingInitializer {

    private String remoteJumpHost;
    private String user;
    private int sshPort;
    private String privateKey; // 예: "ssh/ssh.pem"
    private String databaseUrl;
    private int databasePort;

    private Session session;

    @PreDestroy
    public void closeSSH() {
        try {
            if (session != null && session.isConnected()) {
                session.disconnect();
                log.info("SSH session disconnected");
            }
        } catch (Exception ignore) {
            // swallow
        }
    }

    /**
     * SSH 터널링 세션 생성 및 포트 포워딩
     *
     * @return forwardedPort 로컬 포워딩된 DB 포트
     */
    public Integer buildSshConnection() {
        Integer forwardedPort = null;

        try {
            log.info("{}@{}:{}:{} with privateKey", user, remoteJumpHost, sshPort, databasePort);
            log.info("Start SSH tunneling...");

            JSch jSch = new JSch();

            // classpath에서 privateKey 읽기 -> 임시 파일 생성
            Resource resource = new ClassPathResource(privateKey);
            File tempKey = File.createTempFile("ssh-key", ".pem");
            tempKey.deleteOnExit();

            try (InputStream in = resource.getInputStream();
                 OutputStream out = new FileOutputStream(tempKey)) {
                in.transferTo(out);
            }

            // JSch에 임시 파일 경로 전달
            jSch.addIdentity(tempKey.getAbsolutePath());
            session = jSch.getSession(user, remoteJumpHost, sshPort);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            log.info("Connecting SSH session...");
            session.connect();
            log.info("SSH session connected");

            // 로컬 PC의 남는 포트와 원격 DB 포트 연결
            log.info("Start port forwarding...");
            forwardedPort = session.setPortForwardingL(0, databaseUrl, databasePort);
            log.info("Port forwarding success. Local forwarded port: {}", forwardedPort);

        } catch (Exception e) {
            log.error("Failed to create SSH tunneling: {}", e.getMessage(), e);
            this.closeSSH();
            exit(1);
        }

        return forwardedPort;
    }
}
