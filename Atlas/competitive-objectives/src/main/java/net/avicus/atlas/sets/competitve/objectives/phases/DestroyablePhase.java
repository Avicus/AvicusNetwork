package net.avicus.atlas.sets.competitve.objectives.phases;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.RegisterableObject;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import org.joda.time.Duration;

/**
 * A destroyable phase is a set material (and) data of a {@link DestroyableObjective} that is
 * applied to the blocks in this objective after a set delay. <p> A destroyable can have an infinite
 * amount of phases that can be rotated through. Only one phase is active at a time. <p> Phases will
 * also change the materials required to repair the objective.
 */
@Getter
@ToString(exclude = "match")
public class DestroyablePhase implements RegisterableObject<DestroyablePhase> {

  /**
   * Match that the phase operates in.
   */
  private final Match match;

  /**
   * ID used for reference in XML.
   */
  private final String id;

  /**
   * Name of the phase.
   */
  private final LocalizedXmlString name;
  /**
   * Message used for the countdown.
   * FORMAT:
   * {0} = the name of the phase.
   * {1} = the amount of time until the phase will be applied.
   */
  private final LocalizedXmlString countdownMessage;
  /**
   * The message that is displayed when the mode is successfully applied.
   */
  private final LocalizedXmlString changeSuccessMessage;
  /**
   * The message that is displayed when the mode fails to be applied.
   */
  private final LocalizedXmlString changeFailMessage;

  /**
   * Material that the blocks will be changed to.
   */
  private final LinkedHashMap<MultiMaterialMatcher, SingleMaterialMatcher> materials;

  /**
   * Delay before the phase is applied.
   * (The countdown will be started at match start OR when the preceding phase is applied.)
   */
  @Setter
  private Duration delay;

  /**
   * Check to be ran before the phase is applied.
   */
  private final Optional<Check> changeCheck;
  /**
   * Number of times the phase should be attempted to be re-applied if the check fails.
   */
  private final int retryAttempts;
  /**
   * Delay between retry attempts.
   */
  private final Optional<Duration> retryDelay;
  /**
   * Phase that will be applied if the check fails and there are no retry attempts remaining.
   */
  private Optional<DestroyablePhase> failPhase;
  /**
   * Phase that will be applied if the check passes.
   */
  private Optional<DestroyablePhase> passPhase;

  private final HashMap<DestroyableObjective, AtomicInteger> failures = new HashMap<>();

  /**
   * Constructor.
   *
   * @param match match that the phase operates in
   * @param name name of the phase
   * @param countdownMessage Message used for the countdown FORMAT: {0} = the name of the phase {1}
   * = the amount of time until the phase will be applied
   * @param changeSuccessMessage the message that is displayed when the mode is successfully
   * applied
   * @param changeFailMessage the message that is displayed when the mode fails to be applied
   * @param materials material that the blocks will be changed to
   * @param delay delay before the phase is applied. (The countdown will be started at match start
   * OR when the preceding phase is applied.)
   * @param changeCheck check to be ran before the phase is applied
   * @param retryAttempts number of times the phase should be attempted to be re-applied if the
   * check fails
   * @param retryDelay delay between retry attempts
   * @param failPhase phase that will be applied if the check fails and there are no retry attempts
   * remaining
   * @param passPhase phase that will be applied if the check passes
   */
  public DestroyablePhase(Match match,
      String id,
      LocalizedXmlString name,
      LocalizedXmlString countdownMessage,
      LocalizedXmlString changeSuccessMessage,
      LocalizedXmlString changeFailMessage,
      LinkedHashMap<MultiMaterialMatcher, SingleMaterialMatcher> materials,
      Duration delay,
      Optional<Check> changeCheck,
      int retryAttempts,
      Optional<Duration> retryDelay,
      Optional<DestroyablePhase> failPhase,
      Optional<DestroyablePhase> passPhase) {
    this.match = match;
    this.id = id;
    this.name = name;
    this.countdownMessage = countdownMessage;
    this.changeSuccessMessage = changeSuccessMessage;
    this.changeFailMessage = changeFailMessage;
    this.materials = materials;
    this.delay = delay;
    this.changeCheck = changeCheck;
    this.retryAttempts = retryAttempts;
    this.retryDelay = retryDelay;
    this.failPhase = failPhase;
    this.passPhase = passPhase;
  }

  /**
   * Apply the phase to the objective.
   * The apply check should be ran before this is performed.
   * <p>
   * FIXME: Handle if a block is in mid-break.
   *
   * @param objective objective to apply the phase to
   */
  public void applyPhase(DestroyableObjective objective) {
    objective.getRemaining().forEach(
        block -> {
          this.materials.forEach(objective::updateMaterial);
          if (objective.getMaterials().matches(block.getType(), block.getData())) {
            this.materials.forEach((find, replace) -> {
              if (find.matches(block.getType(), block.getData())) {
                block.setType(replace.getMaterial());
                block.setData(replace.getData().orElse((byte) 0));
              }
            });
          }
        });
  }

  /**
   * Determine if the phase should be applied to the objective.
   *
   * @param objective objective to run the check on
   * @return if the phase should be applied to the objective
   */
  public boolean shouldApply(DestroyableObjective objective) {
    CheckContext context = new CheckContext(this.match);
    return !this.changeCheck.isPresent() || this.changeCheck.get().test(context).passes();
  }

  public Optional<PhaseApplyCountdown> attemptApply(List<DestroyableObjective> objectives) {
    boolean pass = true;

    for (DestroyableObjective objective : objectives) {
      if (!shouldApply(objective)) {
        pass = false;
        this.failures.putIfAbsent(objective, new AtomicInteger());
        this.failures.get(objective).addAndGet(1);
      }
    }

    Optional<DestroyablePhase> next = getNextPhase(pass);

    if (pass) {
      this.match.broadcast(this.changeSuccessMessage.toText());
      objectives.forEach(this::applyPhase);
      if (next.isPresent()) {
        return Optional
            .of(new PhaseApplyCountdown(next.get().getMatch(), next.get().getDelay(), next.get(),
                objectives));
      }
    } else {
      this.match.broadcast(this.changeFailMessage.toText());

      List<DestroyableObjective> toRetry = this.failures.entrySet().stream()
          .filter(entry -> entry.getValue().get() < this.retryAttempts).map(Map.Entry::getKey)
          .collect(Collectors.toList());

      if (!(toRetry.isEmpty())) {
        if (this.retryDelay.isPresent()) {
          return Optional.of(new PhaseApplyCountdown(match, retryDelay.get(), this, toRetry));
        }
      }

      if (next.isPresent()) {
        return Optional
            .of(new PhaseApplyCountdown(next.get().getMatch(), next.get().getDelay(), next.get(),
                objectives));
      }
    }

    return Optional.empty();
  }

  /**
   * Get the phase that should be used after this one based on if this one was applied successfully.
   * *
   *
   * @param pass if the current phase was successfully applied
   * @return the phase after this one based on application success
   */
  public Optional<DestroyablePhase> getNextPhase(boolean pass) {
    return pass ? this.passPhase : this.failPhase;
  }

  @Override
  public DestroyablePhase getObject() {
    return this;
  }

  public List<String> describeReplacementStrategy() {
    List<String> res = Lists.newArrayList();
    getMaterials().forEach((find, replace) -> res.add("Replaces " +
        StringUtil.listToEnglishCompound(
            find.getMatchers().stream()
                .map(SingleMaterialMatcher::describe)
                .collect(Collectors.toList())
        ) + " with " + replace.describe()));

    return res;
  }

  public void removePhase(DestroyablePhase phase) {
    if (this.passPhase.isPresent()) {
      if (this.passPhase.get().equals(phase)) this.passPhase = Optional.empty();
      else this.passPhase.get().removePhase(phase);
    }
    if (this.failPhase.isPresent()) {
      if (this.failPhase.get().equals(phase)) this.failPhase = Optional.empty();
      else this.failPhase.get().removePhase(phase);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DestroyablePhase that = (DestroyablePhase) o;
    return Objects.equal(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
