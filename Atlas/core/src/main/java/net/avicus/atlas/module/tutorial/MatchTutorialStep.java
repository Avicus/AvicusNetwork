package net.avicus.atlas.module.tutorial;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.LocalizedXmlTitle;
import net.avicus.tutorial.api.AbstractTutorialStep;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

@ToString
public class MatchTutorialStep extends AbstractTutorialStep {

  private final boolean freeze;
  private final boolean clearInventory;
  private final boolean fly;
  private final Optional<Double> countdown;
  private final Optional<Vector> position;
  private final Optional<Float> yaw;
  private final Optional<Float> pitch;
  private final Optional<List<LocalizedXmlString>> chat;
  private final Optional<LocalizedXmlTitle> title;
  private final Optional<Map<Integer, ItemStack>> inventory;

  public MatchTutorialStep(boolean freeze,
      boolean clearInventory,
      boolean fly,
      Optional<Double> countdown,
      Optional<Vector> position,
      Optional<Float> yaw,
      Optional<Float> pitch,
      Optional<List<LocalizedXmlString>> chat,
      Optional<LocalizedXmlTitle> title,
      Optional<Map<Integer, ItemStack>> inventory) {
    this.freeze = freeze;
    this.clearInventory = clearInventory;
    this.fly = fly;
    this.countdown = countdown;
    this.position = position;
    this.yaw = yaw;
    this.pitch = pitch;
    this.chat = chat;
    this.title = title;
    this.inventory = inventory;
  }

  @Override
  public boolean isFlyEnabled() {
    return this.fly;
  }

  @Override
  public boolean isFrozen() {
    return this.freeze;
  }

  @Override
  public boolean isClearInventory() {
    return this.clearInventory;
  }

  @Override
  public Optional<Double> getCountdown() {
    return this.countdown;
  }

  @Override
  public Optional<String> getWorldName() {
    return Optional.empty();
  }

  @Override
  public Optional<GameMode> getGameMode() {
    return Optional.empty();
  }

  @Override
  public Optional<Vector> getPosition() {
    return this.position;
  }

  @Override
  public Optional<Float> getYaw() {
    return this.yaw;
  }

  @Override
  public Optional<Float> getPitch() {
    return this.pitch;
  }

  @Override
  public Optional<Title> getTitle(Player player) {
    return this.title.map((title) -> title.createTitle(player));
  }

  @Override
  public Optional<List<TextComponent>> getChat(Player player) {
    // Translate each line to a TextComponent for this player's language
    return this.chat.map((lines) -> lines.stream().map((line) -> {
      String text = line.render(player);
      return new TextComponent(text);
    }).collect(Collectors.toList()));
  }

  @Override
  public Optional<Map<Integer, ItemStack>> getInventory() {
    return this.inventory;
  }
}
