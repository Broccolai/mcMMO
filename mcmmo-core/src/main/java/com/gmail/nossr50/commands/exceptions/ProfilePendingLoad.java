package com.gmail.nossr50.commands.exceptions;

import co.aikar.commands.InvalidCommandArgument;
import com.gmail.nossr50.locale.LocaleManager;

public class ProfilePendingLoad extends InvalidCommandArgument {
    public ProfilePendingLoad(LocaleManager localeManager) {
        super(localeManager.getString("Profile.PendingLoad"));
    }
}
