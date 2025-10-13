//package com.donttouch.common_service.global.config.ssh;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//@Configuration
//public class SshKeyLoader {
//
//    @Bean
//    public String sshPrivateKeyPath() throws Exception {
//        Resource resource = new ClassPathResource("ssh/ssh.pem");
//        File tempFile = File.createTempFile("ssh-key", ".pem");
//        tempFile.deleteOnExit();
//
//        try (InputStream in = resource.getInputStream();
//             OutputStream out = new FileOutputStream(tempFile)) {
//            in.transferTo(out);
//        }
//
//        return tempFile.getAbsolutePath();
//    }
//}