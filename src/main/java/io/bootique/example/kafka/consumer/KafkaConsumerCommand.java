package io.bootique.example.kafka.consumer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nhl.bootique.cli.Cli;
import com.nhl.bootique.cli.CliOption;
import com.nhl.bootique.command.CommandMetadata;
import com.nhl.bootique.command.CommandOutcome;
import com.nhl.bootique.command.CommandWithMetadata;
import io.bootique.kafka.client.KafkaClientFactory;
import io.bootique.kafka.client.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;

/**
 * Runs Kafka consumer.
 */
public class KafkaConsumerCommand extends CommandWithMetadata {

    private static final String TOPIC_OPT = "topic";
    private static final String BOOTSTRAP_SERVER_OPT = "bootstrap-server";

    private Provider<KafkaClientFactory> kafkaProvider;

    @Inject
    public KafkaConsumerCommand(Provider<KafkaClientFactory> kafkaProvider) {
        super(CommandMetadata.builder("consumer").addOption(topicOption()).addOption(clusterOption()));
        this.kafkaProvider = kafkaProvider;
    }

    private static CliOption topicOption() {
        return CliOption.builder(TOPIC_OPT).description("Kafka topic to consume").valueRequired("topic_name").build();
    }

    private static CliOption clusterOption() {
        return CliOption.builder(BOOTSTRAP_SERVER_OPT).description("Single Kafka bootstrap server. " +
                "Can be specified multiple times. Optional. " +
                "If omitted, will be read from YAML or environment variable BQ_KAFKACLIENT_BOOTSTRAPSERVERS_DEFAULT.")
                .valueRequired("host:port").build();
    }

    @Override
    public CommandOutcome run(Cli cli) {

        String topic = cli.optionString(TOPIC_OPT);
        if (topic == null) {
            return CommandOutcome.failed(-1, "No --topic specified");
        }

        ConsumerConfig<byte[], String> config = ConsumerConfig
                .charValueConfig()
                .autoCommit(true)
                .bootstrapServers(cli.optionStrings(BOOTSTRAP_SERVER_OPT))
                .build();

        Consumer<byte[], String> consumer = kafkaProvider.get().createConsumer(config);

        return CommandOutcome.succeeded();
    }

}
