package io.bootique.example.kafka.consumer;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.nhl.bootique.BQCoreModule;
import com.nhl.bootique.Bootique;

/**
 * Main app class.
 */
public class App implements Module {

    public static void main(String[] args) {
        Bootique.app(args).autoLoadModules().module(App.class).run();
    }

    @Override
    public void configure(Binder binder) {

        // TODO: use ImplicitCommandLine strategy when this becomes available:
        // https://github.com/nhl/bootique/issues/26
        BQCoreModule.contributeCommands(binder).addBinding().to(KafkaConsumerCommand.class);
    }


}
