package io.apollosoftware.naturalnavigation.configuration;

import io.apollosoftware.lib.configuration.Configuration;
import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.NaturalNavigation;

public class LanguageConfiguration extends Configuration<NaturalNavigation> {

    public LanguageConfiguration() {
        super("language.yml");
    }

    public void afterLoad() {
        Message.load(conf);
    }

    public void onSave() {

    }
}
