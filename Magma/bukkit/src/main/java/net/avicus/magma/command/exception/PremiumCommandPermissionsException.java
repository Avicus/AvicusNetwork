package net.avicus.magma.command.exception;

import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import java.util.Arrays;
import java.util.List;
import net.avicus.compendium.commands.exception.AbstractTranslatableCommandException;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.magma.util.MagmaTranslations;

public class PremiumCommandPermissionsException extends CommandPermissionsException {

  public static final LocalizedText MESSAGE = AbstractTranslatableCommandException
      .error(MagmaTranslations.ERROR_COMMANDS_PERMISSION_PREMIUM);
  public static final List<String> PREMIUM_COMMANDS = Arrays.asList(
      "rtp",
      "namehistory"
  );

  public LocalizedText asTranslatable() {
    return MESSAGE;
  }
}
