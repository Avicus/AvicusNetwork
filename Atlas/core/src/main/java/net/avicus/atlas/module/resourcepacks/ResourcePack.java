package net.avicus.atlas.module.resourcepacks;

import lombok.ToString;
import net.avicus.atlas.match.registry.RegisterableObject;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import org.bukkit.entity.Player;

@ToString
public class ResourcePack implements RegisterableObject<ResourcePack> {

  private final String id;
  private final LocalizedXmlString name;
  private final String url;

  public ResourcePack(String id, LocalizedXmlString name, String url) {
    this.id = id;
    this.name = name;
    this.url = url;
  }

  public void requestDownload(Player player) {
    player.setResourcePack(this.url);
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public ResourcePack getObject() {
    return this;
  }
}
