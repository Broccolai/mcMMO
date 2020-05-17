package com.gmail.nossr50.commands.exceptions;

import co.aikar.commands.InvalidCommandArgument;
import com.gmail.nossr50.locale.LocaleManager;

public class InvalidCommandPermissions extends InvalidCommandArgument {
    public InvalidCommandPermissions(LocaleManager localeManager) {
        super(localeManager.getString("mcMMO.NoPermission", false));
    }
}
