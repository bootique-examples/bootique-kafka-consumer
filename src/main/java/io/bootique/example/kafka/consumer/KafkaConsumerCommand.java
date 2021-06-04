package io.bootique.example.kafka.consumer;

import io.bootique.kafka.client.consumer.KafkaConsumerFactory;
import io.bootique.kafka.client.consumer.KafkaConsumerRunner;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.log.BootLogger;

import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.Duration;
import java.util.Collection;

/**
 * Runs Kafka consumer. See
 * <a href="http://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache   -kafka-0.9-consumer-client">this article</a>
 * for background on the consumer API.
 */
public class KafkaConsumerCommand extends CommandWithMetadata {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerCommand.class);

    private static final String TOPIC_OPT = "topic";
    private static final String DEFAULT_GROUP_NAME = "bootique-consumer";

    private Provider<KafkaConsumerFactory> kafkaProvider;
    private BootLogger bootLogger;

    @Inject
    public KafkaConsumerCommand(Provider<KafkaConsumerFactory> kafkaProvider, BootLogger bootLogger) {
        super(CommandMetadata.builder("consumer")
                .addOption(topicOption())
        );
        this.kafkaProvider = kafkaProvider;
        this.bootLogger = bootLogger;
    }

    private static OptionMetadata topicOption() {
        return OptionMetadata.builder(TOPIC_OPT).description("Kafka topic to consume. Can be specified multiple times.")
                .valueRequired("topic_name").build();
    }

    @Override
    public CommandOutcome run(Cli cli) {

        Collection<String> topics = cli.optionStrings(TOPIC_OPT);
        if (topics.isEmpty()) {
            return CommandOutcome.failed(-1, "No --topic specified");
        }

        KafkaConsumerRunner<byte[], String> consumer = kafkaProvider.get()
                .charValueConsumer()
                .autoCommit(true)
                .group(DEFAULT_GROUP_NAME)
                .cluster(App.DEFAULT_CLUSTER_NAME)
                .topics(topics.toArray(new String[0]))
                .pollInterval(Duration.ofSeconds(1))
                .create();

        try {
            LOGGER.info("Will consume topics: " + topics);
            consumer.forEach(record
                    -> bootLogger.stdout(record.topic() + "_" + record.partition() + "_" + record.offset() + ": " + record.value()));
        } catch (WakeupException e) {
            // expected on shutdown..
            LOGGER.info("Stopping consumer...");
        } finally {
            consumer.close();
        }

        return CommandOutcome.succeeded();
    }
}
