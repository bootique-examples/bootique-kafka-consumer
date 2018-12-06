package io.bootique.example.kafka.consumer;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import io.bootique.meta.application.OptionMetadata;

import java.util.logging.Level;

/**
 * Main app class.
 */
public class App implements Module {

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
                .valueOptionalWithDefault("INFO")
                .description("If enabled, Kafka client will print extra debugging information to STDOUT.").build();
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder)
                .setDefaultCommand(KafkaConsumerCommand.class)
                .addOption(verboseOption())
                .mapConfigPath(VERBOSE_OPT, "log.loggers.org.apache.kafka.level")
                .mapConfigPath(VERBOSE_OPT, "log.loggers.io.bootique.level");

        BQCoreModule.extend(binder)
                .setLogLevel("org.apache.kafka", Level.WARNING)
                .setLogLevel("io.bootique", Level.WARNING);
    }
}
