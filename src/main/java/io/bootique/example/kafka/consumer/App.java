package io.bootique.example.kafka.consumer;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;

/**
 * Main app class.
 */
public class App implements Module {

    public static void main(String[] args) {
        Bootique.app("--config=classpath:defaults.yml").args(args).autoLoadModules().module(App.class).run();
    }

    @Override
    public void configure(Binder binder) {

        // TODO: use ImplicitCommandLine strategy when this becomes available:
        // https://github.com/nhl/bootique/issues/26
        BQCoreModule.contributeCommands(binder).addBinding().to(KafkaConsumerCommand.class);
    }


}
