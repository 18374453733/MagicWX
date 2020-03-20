package com.hdpfans.app.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import com.hdpfans.app.App;
import com.hdpfans.app.internal.di.modules.ApplicationModule;
import com.hdpfans.app.internal.di.modules.BuildersModule;
import com.hdpfans.app.internal.di.modules.provider.ConfigModule;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        ConfigModule.class,
        BuildersModule.class,
})
public interface ApplicationComponent extends AndroidInjector<App> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<App> {
        abstract Builder configModule(ConfigModule configModule);

        @Override
        public void seedInstance(App instance) {
            configModule(new ConfigModule(instance));
        }
    }

}
