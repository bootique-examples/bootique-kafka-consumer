package io.bootique.example.kafka.consumer;

import com.google.inject.Binder;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.cli.Cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Level;

/**
 * Main app class.
 */
public class App implements Module {

    private static final String VERBOSE_OPT = "verbose";

    public static void main(String[] args) {
        Bootique.app("--config=classpath:defaults.yml").args(args).autoLoadModules().module(App.class).run();
    }

    private static OptionMetadata verboseOption() {
        return OptionMetadata.builder(VERBOSE_OPT)
                .description("If enabled, Kafka client will print extra debugging information to STDOUT.").build();
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).setDefaultCommand(KafkaConsumerCommand.class).addOption(verboseOption());

        // TODO: perhaps we'll invent less verbose API for conditional setting of log levels per
        // https://github.com/bootique/bootique/issues/101
        BQCoreModule.contributeLogLevels(binder).addBinding("org.apache.kafka").to(Key.get(Level.class, KafkaLogLevel.class));
        BQCoreModule.contributeLogLevels(binder).addBinding("io.bootique").to(Key.get(Level.class, BootiqueLogLevel.class));
    }


    @KafkaLogLevel
    @Provides
    @Singleton
    Level provideKafkaLogLevel(Cli cli) {
        return cli.hasOption(VERBOSE_OPT) ? Level.INFO : Level.WARNING;
    }

    @BootiqueLogLevel
    @Provides
    @Singleton
    Level provideBootiqueLogLevel(Cli cli) {
        return cli.hasOption(VERBOSE_OPT) ? Level.INFO : Level.WARNING;
    }

    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @interface KafkaLogLevel {
    }

    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @interface BootiqueLogLevel {
    }
}
