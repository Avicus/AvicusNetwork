package net.avicus.atlas.module.executors.types;

import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An executor that takes items from player inventories based on criteria.
 */
@ToString
public class TakeItemExecutor extends Executor {

  private final MultiMaterialMatcher matcher;

  public TakeItemExecutor(String id, Check check, MultiMaterialMatcher matcher) {
    super(id, check);
    this.matcher = matcher;
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Take Items")
        .tagName("take-item")
        .description("An executor that takes items from player inventories based on criteria.")
        .attribute("take", Attributes.materialMatcher(true, true, "Items to take."))
        .build();
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    MultiMaterialMatcher matcher = element.getAttribute("take").asRequiredMultiMaterialMatcher();
    return new TakeItemExecutor(id, check, matcher);
  }

  @Override
  public void execute(CheckContext context) {
    Player player = context.getLast(PlayerVariable.class).map(PlayerVariable::getPlayer)
        .orElse(null);
    if (player != null) {
      int size = player.getInventory().getSize();
      for (int slot = 0; slot < size; slot++) {
        ItemStack itemStack = player.getInventory().getItem(slot);
        if (itemStack != null && itemStack.getType() != Material.AIR && this.matcher
            .matches(itemStack.getData())) {
          player.getInventory().clear(slot);
        }
      }
    }
  }
}
