package io.bootique.example.kafka.consumer;

import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.meta.application.OptionMetadata;

import java.util.logging.Level;

/**
 * Main app class.
 */
public class App implements BQModule {

    static final String DEFAULT_CLUSTER_NAME = "default_cluster";
    private static final String BOOTSTRAP_SERVER_OPT = "bootstrap";
    private static final String VERBOSE_OPT = "verbose";

    public static void main(String[] args) {
        Bootique.app("--config=classpath:defaults.yml")
                .args(args)
                .autoLoadModules()
                .module(App.class)
                .exec()
                .exit();
    }

    private static OptionMetadata verboseOption() {
        return OptionMetadata.builder(VERBOSE_OPT)
                .description("If enabled, Kafka client will print extra debugging information to STDOUT.").build();
    }

    private static OptionMetadata bootstrapOption() {
        return OptionMetadata
                .builder(BOOTSTRAP_SERVER_OPT)
                .description("Single Kafka bootstrap server.")
                .valueRequired("host:port")
                .build();
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder)
                .addOption(bootstrapOption())
                .mapConfigPath(BOOTSTRAP_SERVER_OPT, "kafkaclient.clusters." + DEFAULT_CLUSTER_NAME)
                .addOption(verboseOption())
                .mapConfigResource(VERBOSE_OPT, "classpath:verbose.yml")
                .setDefaultCommand(KafkaConsumerCommand.class);

        BQCoreModule.extend(binder)
                .setLogLevel("org.apache.kafka", Level.WARNING)
                .setLogLevel("io.bootique", Level.WARNING);
    }
}
