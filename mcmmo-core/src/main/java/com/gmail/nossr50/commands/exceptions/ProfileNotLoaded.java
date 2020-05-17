package com.gmail.nossr50.commands.exceptions;

import co.aikar.commands.InvalidCommandArgument;
import com.gmail.nossr50.locale.LocaleManager;

public class ProfileNotLoaded extends InvalidCommandArgument {
    public ProfileNotLoaded(LocaleManager localeManager) {
        super(localeManager.getString("Commands.NotLoaded"), false);
    }
}
