package io.bootique.example.kafka.consumer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nhl.bootique.cli.Cli;
import com.nhl.bootique.cli.CliOption;
import com.nhl.bootique.command.CommandMetadata;
import com.nhl.bootique.command.CommandOutcome;
import com.nhl.bootique.command.CommandWithMetadata;
import com.nhl.bootique.log.BootLogger;
import com.nhl.bootique.shutdown.ShutdownManager;
import io.bootique.kafka.client.KafkaClientFactory;
import io.bootique.kafka.client.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Runs Kafka consumer. See
 * <a href="http://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0.9-consumer-client">this article</a>
 * for background on the consumer API.
 */
public class KafkaConsumerCommand extends CommandWithMetadata {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerCommand.class);

    private static final String TOPIC_OPT = "topic";
    private static final String BOOTSTRAP_SERVER_OPT = "bootstrap-server";

    private static final String DEFAULT_CLUSTER_NAME = "default";
    private static final String DEFAULT_GROUP_NAME = "bootique-consumer";

    private Provider<KafkaClientFactory> kafkaProvider;
    private BootLogger bootLogger;
    private ShutdownManager shutdownManager;

    @Inject
    public KafkaConsumerCommand(Provider<KafkaClientFactory> kafkaProvider, BootLogger bootLogger, ShutdownManager shutdownManager) {
        super(CommandMetadata.builder("consumer").addOption(topicOption()).addOption(clusterOption()));
        this.kafkaProvider = kafkaProvider;
        this.bootLogger = bootLogger;
        this.shutdownManager = shutdownManager;
    }

    private static CliOption topicOption() {
        return CliOption.builder(TOPIC_OPT).description("Kafka topic to consume. Can be specified multiple times.")
                .valueRequired("topic_name").build();
    }

    private static CliOption clusterOption() {
        return CliOption.builder(BOOTSTRAP_SERVER_OPT).description("Single Kafka bootstrap server. " +
                "Can be specified multiple times. Optional. " +
                "If omitted, will be read from YAML or environment variable BQ_KAFKACLIENT_BOOTSTRAPSERVERS_DEFAULT.")
                .valueRequired("host:port").build();
    }

    @Override
    public CommandOutcome run(Cli cli) {

        Collection<String> topics = cli.optionStrings(TOPIC_OPT);
        if (topics.isEmpty()) {
            return CommandOutcome.failed(-1, "No --topic specified");
        }

        ConsumerConfig<byte[], String> config = ConsumerConfig
                .charValueConfig()
                .autoCommit(true)
                .group(DEFAULT_GROUP_NAME)
                .bootstrapServers(cli.optionStrings(BOOTSTRAP_SERVER_OPT))
                .build();

        Consumer<byte[], String> consumer = kafkaProvider.get().createConsumer(DEFAULT_CLUSTER_NAME, config);

        try {

            shutdownManager.addShutdownHook(() -> {
                consumer.wakeup();
                // give consumer time to wakeup and stop...
                Thread.sleep(500);
            });

            LOGGER.info("Will consume topics: " + topics);

            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<byte[], String> records = consumer.poll(1000);
                for (ConsumerRecord<byte[], String> r : records) {
                    bootLogger.stdout(r.topic() + "_" + r.partition() + "_" + r.offset() + ": " + r.value());
                }
            }
        } catch (WakeupException e) {
            // expected on shutdown..
            LOGGER.info("Stopping consumer...");
        } finally {
            consumer.close();
        }

        return CommandOutcome.succeeded();
    }
}
