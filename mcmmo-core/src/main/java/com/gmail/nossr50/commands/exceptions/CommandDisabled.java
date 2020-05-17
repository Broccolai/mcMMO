package com.gmail.nossr50.commands.exceptions;

import co.aikar.commands.InvalidCommandArgument;
import com.gmail.nossr50.locale.LocaleManager;

public class CommandDisabled extends InvalidCommandArgument {
    public CommandDisabled(LocaleManager localeManager) {
        super(localeManager.getString("Commands.Disabled"));
    }
}
