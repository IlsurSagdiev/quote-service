package ru.sberbank.pprb.quote.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Конфигурация kafka
 * @author SagdievIA
 * @since 15.01.2025
 */
@Slf4j
@Configuration
public class QuoteKafkaConfig {

    public static final String QUOTE_KAFKA_PRODUCER_FACTORY = "quoteKafkaProducerFactory";
    public static final String QUOTE_KAFKA_TEMPLATE = "quoteKafkaTemplate";

    public static final String QUOTE_KAFKA_LISTENER_CONTAINER_FACTORY = "kafkaListenerContainerFactory";
    public static final String QUOTE_KAFKA_CONSUMER_FACTORY = "kafkaConsumerFactory";

    private final KafkaProperties quoteKafkaProperties;

    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    @Value("${spring.kafka.properties.ssl.enabled}")
    private Boolean sslEnabled = false;

    @Value("${spring.kafka.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.ssl.truststore.location}")
    private String trustStoreLocation;

    @Value("${spring.kafka.properties.ssl.truststore.password}")
    private String trustStorePassword;

    @Value("${spring.kafka.properties.ssl.keystore.location}")
    private String keyStoreLocation;

    @Value("${spring.kafka.properties.ssl.keystore.password}")
    private String keyStorePassword;

    @Value("${spring.kafka.properties.ssl.key.password}")
    private String keyPassword;

    @Value("${spring.kafka.properties.ssl.truststore-type}")
    private String trustStoreType;

    @Value("${spring.kafka.properties.ssl.keystore-type}")
    private String keyStoreType;

    public QuoteKafkaConfig(final KafkaProperties kafkaProperties) {
        this.quoteKafkaProperties = kafkaProperties;
    }

    @Bean({QUOTE_KAFKA_TEMPLATE})
    public KafkaTemplate<?, ?> kafkaTemplate(@Qualifier(QUOTE_KAFKA_PRODUCER_FACTORY) final ProducerFactory<?,
            ?> fisKafkaProducerFactory) {
        return new KafkaTemplate<>(fisKafkaProducerFactory);
    }

    @Bean(QUOTE_KAFKA_PRODUCER_FACTORY)
    public ProducerFactory<?, ?> producerFactory() {
        Map<String, Object> configProps = buildCommonProperties();
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        configProps.putAll(quoteKafkaProperties.getProducer().getProperties());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(QUOTE_KAFKA_CONSUMER_FACTORY)
    public ConsumerFactory<Object, Object> consumerFactory(ObjectProvider<DefaultKafkaConsumerFactoryCustomizer>
                                                                   customizers) {
        Map<String, Object> properties = buildCommonProperties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.putAll(quoteKafkaProperties.getConsumer().getProperties());
        DefaultKafkaConsumerFactory<Object, Object> factory = new DefaultKafkaConsumerFactory<>(properties);
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));

        return factory;
    }

    @Bean({QUOTE_KAFKA_LISTENER_CONTAINER_FACTORY})
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            @Qualifier(QUOTE_KAFKA_CONSUMER_FACTORY) final ConsumerFactory<Object, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);

        return factory;
    }

    private Map<String, Object> buildCommonProperties() {
        Map<String, Object> properties = new HashMap<>();
        if (quoteKafkaProperties.getBootstrapServers() != null) {
            properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, quoteKafkaProperties.getBootstrapServers());
        }
        if (quoteKafkaProperties.getClientId() != null) {
            properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, quoteKafkaProperties.getClientId());
        }
        if (sslEnabled) {
            properties.putAll(quoteKafkaProperties.getSsl().buildProperties(null));
            properties.putAll(quoteKafkaProperties.getSecurity().buildProperties());
            if (!CollectionUtils.isEmpty(quoteKafkaProperties.getProperties())) {
                properties.putAll(quoteKafkaProperties.getProperties());
            }
        }
        return properties;
    }
}
