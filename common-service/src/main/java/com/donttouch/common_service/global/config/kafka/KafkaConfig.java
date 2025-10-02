//package com.donttouch.common_service.global.config.kafka;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//@Configuration
//public class KafkaConfig {
//
//    @Value("${community.commands.topic.checkStock}")
//    private String checkStockTopic;
//
//    @Value("${community.commands.topic.finalizePost}")
//    private String finalizePostCommandsTopic;
//
//    @Value("${community.commands.topic.rollbackPost}")
//    private String rollbackPostCommandsTopic;
//
//    private final static Integer TOPIC_REPLICATION_FACTOR = 3;
//    private final static Integer TOPIC_PARTITIONS = 3;
//
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
//        return new KafkaTemplate<>(producerFactory);
//    }
//
//    @Bean
//    public NewTopic createCheckStockTopic() {
//        return TopicBuilder.name(checkStockTopic)
//                .partitions(TOPIC_PARTITIONS)
//                .replicas(TOPIC_REPLICATION_FACTOR)
//                .build();
//    }
//
//    @Bean
//    public NewTopic createFinalizePostCommandsTopic() {
//        return TopicBuilder.name(finalizePostCommandsTopic)
//                .partitions(TOPIC_PARTITIONS)
//                .replicas(TOPIC_REPLICATION_FACTOR)
//                .build();
//    }
//
//    @Bean
//    public NewTopic createRollbackPostCommandsTopic() {
//        return TopicBuilder.name(rollbackPostCommandsTopic)
//                .partitions(TOPIC_PARTITIONS)
//                .replicas(TOPIC_REPLICATION_FACTOR)
//                .build();
//    }
//}