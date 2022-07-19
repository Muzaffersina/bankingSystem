package com.msa.bankingsystem.core.apacheKafka.configs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class ConfigKafkaTopics {

	@Value(value = "${kafka.bootstrapAddress}")
	public String bootstrapAddress;

	@Value(value = "${kafka.topicName}")
	public String topicName;

	@Bean
	public KafkaAdmin kafkaAdmin() {

		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic createNewTopic() {
		return new NewTopic(topicName, 1, (short) 1);
	}

	@Bean
	public TopicDescription topicDescribe() {
		List<TopicPartitionInfo> info = null;
		return new TopicDescription(topicName, false, info);
	}

}