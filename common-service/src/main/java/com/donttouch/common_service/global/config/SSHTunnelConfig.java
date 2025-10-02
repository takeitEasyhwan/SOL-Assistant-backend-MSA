//package com.donttouch.common_service.global.config;
//
//import com.jcraft.jsch.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import java.io.File;
//import java.util.Properties;
//
//@Configuration
//public class SSHTunnelConfig {
//
//    private static final Logger logger = LoggerFactory.getLogger(SSHTunnelConfig.class);
//
//    @Value("${ssh.ssh_host}")
//    private String sshHost;
//
//    @Value("${ssh.ssh_port}")
//    private int sshPort;
//
//    @Value("${ssh.ssh_user}")
//    private String sshUser;
//
//    @Value("${ssh.ssh_key}")
//    private String sshKeyPath;
//
//    @Value("${ssh.database_host}")
//    private String databaseHost;
//
//    @Value("${ssh.database_port}")
//    private int databasePort;
//
//    @Value("${ssh.tunnel_port:13306}")
//    private int tunnelPort;
//
//    private Session session;
//    private int localPort;
//
//    @PostConstruct
//    public void createSSHTunnel() {
//        try {
//            JSch jsch = new JSch();
//
//            // SSH 키 파일 경로 확인
//            File keyFile = new File(sshKeyPath);
//            if (!keyFile.exists()) {
//                logger.error("SSH 키 파일을 찾을 수 없습니다: {}", sshKeyPath);
//                throw new RuntimeException("SSH 키 파일이 존재하지 않습니다: " + sshKeyPath);
//            }
//
//            // SSH 키 파일 권한 확인 (Linux/Mac에서만)
//            if (System.getProperty("os.name").toLowerCase().contains("nix") ||
//                System.getProperty("os.name").toLowerCase().contains("mac")) {
//                if (keyFile.canRead() && keyFile.canWrite()) {
//                    logger.warn("SSH 키 파일의 권한이 너무 넓습니다. chmod 600을 권장합니다.");
//                }
//            }
//
//            // SSH 키 추가
//            jsch.addIdentity(sshKeyPath);
//
//            // SSH 세션 생성
//            session = jsch.getSession(sshUser, sshHost, sshPort);
//
//            // SSH 연결 설정
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking", "no");
//            config.put("PreferredAuthentications", "publickey");
//            session.setConfig(config);
//
//            // SSH 연결
//            session.connect();
//            logger.info("SSH 연결 성공: {}@{}:{}", sshUser, sshHost, sshPort);
//
//            // 포트 포워딩 설정
//            localPort = session.setPortForwardingL(0, databaseHost, databasePort);
//            logger.info("SSH 터널 생성 성공: localhost:{} -> {}:{}", localPort, databaseHost, databasePort);
//
//            // 시스템 프로퍼티에 터널 포트 설정
//            System.setProperty("ssh.tunnel.port", String.valueOf(localPort));
//
//        } catch (Exception e) {
//            logger.error("SSH 터널 생성 실패", e);
//            throw new RuntimeException("SSH 터널 생성에 실패했습니다", e);
//        }
//    }
//
//    @PreDestroy
//    public void closeSSHTunnel() {
//        if (session != null && session.isConnected()) {
//            session.disconnect();
//            logger.info("SSH 터널 연결 종료");
//        }
//    }
//
//    public int getLocalPort() {
//        return localPort;
//    }
//}
