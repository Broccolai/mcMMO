package com.gmail.nossr50.commands.exceptions;

import co.aikar.commands.InvalidCommandArgument;
import com.gmail.nossr50.locale.LocaleManager;

public class InvalidCommandSender extends InvalidCommandArgument {
    public InvalidCommandSender(LocaleManager localeManager) {
        super(localeManager.getString("Commands.NoConsole"), false);
    }
}
