package net.avicus.atlas.module.executors.types;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import org.joda.time.Duration;

/**
 * An executor that executes a child executor at a pre-defined rate/count.
 */
@ToString(exclude = "match")
public class LoopingExecutor extends Executor {

  private final Match match;
  private final Executor child;
  private final Duration interval;
  private
  @Nullable
  final Integer loopCount;
  private final Check stopCheck;

  @Getter
  private HashMap<AtlasTask, AtomicInteger> runs = Maps.newHashMap();

  public LoopingExecutor(String id, Check check, Match match, Executor child, Duration interval,
      Integer loopCount, Check stopCheck) {
    super(id, check);
    this.match = match;
    this.child = child;
    this.interval = interval;
    this.loopCount = loopCount;
    this.stopCheck = stopCheck;
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    Check stopCheck = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("stop"), element.getChild("stop"));
    Executor executor = match.getRegistry()
        .get(Executor.class, element.getAttribute("execute").asRequiredString(), true).get();
    Duration interval = element.getAttribute("interval").asRequiredDuration();
    Integer loopCount = element.getAttribute("repetitions").asInteger().orElse(null);
    return new LoopingExecutor(id, check, match, executor, interval, loopCount, stopCheck);
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Perform At Fixed Rate")
        .tagName("loop")
        .description(
            "An executor that is used to perform an executor at a fixed rate that can be dynamically ended based on checks.")
        .attribute("execute", Attributes.idOf(true, "executor"))
        .attribute("stop", Attributes.check(true, "before the loop is halted"))
        .attribute("interval", Attributes.duration(true, true,
            "Check that will run with each execution to see if it should be ended early."))
        .attribute("repetitions", new GenericAttribute(Integer.class, false,
            "Number of times to execute. If this is not given, the executor will never stop."))
        .build();
  }

  @Override
  public void execute(CheckContext context) {
    new AtlasTask() {
      @Override
      public void run() {
        if (Atlas.getMatch() == null || !match.getId().equalsIgnoreCase(Atlas.getMatch().getId())) {
          cancel0();
        }

        runs.putIfAbsent(this, new AtomicInteger());

        if (loopCount != null && runs.get(this).intValue() >= loopCount) {
          cancel0();
        }

        runs.get(this).addAndGet(1);

        if (stopCheck.test(context).passes()) {
          cancel0();
        }

        child.execute(context);

        runs.get(this).addAndGet(1);
      }
    }.repeat(0, (int) (this.interval.getMillis() / 1000)
        * 20); // Using this as opposed to duration standard seconds to support less than a second dirations.
  }
}
