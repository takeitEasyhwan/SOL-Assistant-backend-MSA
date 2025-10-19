//package com.donttouch.common_service.global.util;
//
//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.Session;
//import jakarta.annotation.PreDestroy;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.annotation.Validated;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Properties;
//
//import static java.lang.System.exit;
//
//@Slf4j
//@Profile("stg")
//@Component
//@ConfigurationProperties(prefix = "ssh")
//@Getter
//@Setter
//public class SshTunnelingInitializer {
//
//    private String remoteJumpHost;
//    private String user;
//    private int sshPort;
//    private String privateKey;
//    private String databaseUrl;        // master
//    private String readOnly;           // read replica
//    private int databasePort;
//
//    // 세션 2개 관리
//    private Session masterSession;
//    private Session replicaSession;
//
//    @PreDestroy
//    public void closeSSH() {
//        disconnect(masterSession, "Master");
//        disconnect(replicaSession, "Replica");
//    }
//
//    private void disconnect(Session session, String name) {
//        try {
//            if (session != null && session.isConnected()) {
//                session.disconnect();
//                log.info("{} SSH session disconnected", name);
//            }
//        } catch (Exception ignore) {}
//    }
//
//    /**
//     * SSH 터널링 생성 (read/write 구분)
//     *
//     * @param isReadReplica true면 Read Replica용
//     * @return forwardedPort
//     */
//    public Integer buildSshConnection(boolean isReadReplica) {
//        Integer forwardedPort = null;
//
//        try {
//            String targetHost = isReadReplica ? readOnly : databaseUrl;
//            String targetType = isReadReplica ? "Replica" : "Master";
//
//            JSch jSch = new JSch();
//            Resource resource = new ClassPathResource(privateKey);
//            File tempKey = File.createTempFile("ssh-key", ".pem");
//            tempKey.deleteOnExit();
//
//            try (InputStream in = resource.getInputStream();
//                 OutputStream out = new FileOutputStream(tempKey)) {
//                in.transferTo(out);
//            }
//
//            jSch.addIdentity(tempKey.getAbsolutePath());
//            Session session = jSch.getSession(user, remoteJumpHost, sshPort);
//
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking", "no");
//            session.setConfig(config);
//
//            log.info("[SSH] Connecting {} session...", targetType);
//            session.connect();
//            log.info("[SSH] {} session connected", targetType);
//
//            forwardedPort = session.setPortForwardingL(0, targetHost, databasePort);
//            log.info("[SSH] {} Port forwarding success. local: {}, remote: {}", targetType, forwardedPort, targetHost);
//
//            if (isReadReplica) this.replicaSession = session;
//            else this.masterSession = session;
//
//        } catch (Exception e) {
//            log.error("Failed to create SSH tunneling: {}", e.getMessage(), e);
//            this.closeSSH();
//            System.exit(1);
//        }
//
//        return forwardedPort;
//    }
//}
