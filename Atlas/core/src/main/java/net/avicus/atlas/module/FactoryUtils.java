package net.avicus.atlas.module;

import java.util.List;
import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.ChecksFactory;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.projectiles.CustomProjectile;
import net.avicus.atlas.module.projectiles.ProjectilesFactory;
import net.avicus.atlas.module.regions.RegionsFactory;
import net.avicus.atlas.util.xml.XmlAttribute;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.magma.util.region.Region;

public class FactoryUtils {

  public static Check resolveRequiredCheckChild(Match match, XmlAttribute checkId,
      XmlElement inlineCheck) {
    Optional<Check> check = resolveCheckChild(match, checkId, inlineCheck);
    if (check.isPresent()) {
      return check.get();
    }
    throw new XmlException(checkId.getElement(), "Missing required check.");
  }

  public static Check resolveRequiredCheckChild(Match match, XmlAttribute checkId,
      Optional<XmlElement> inlineCheck) {
    Optional<Check> check = resolveCheckChild(match, checkId, inlineCheck);
    if (check.isPresent()) {
      return check.get();
    }
    throw new XmlException(checkId.getElement(), "Missing required check.");
  }

  public static Optional<Check> resolveCheckChild(Match match, XmlAttribute checkId,
      XmlElement parent) {
    return resolveCheckChild(match, checkId, Optional.of(parent));
  }

  public static Optional<Check> resolveCheckChild(Match match, XmlAttribute checkId,
      Optional<XmlElement> parent) {
    Optional<XmlElement> inline = parent;
    if (inline.isPresent()) {
      if (inline.get().getChildren().size() > 1) {
        throw new XmlException(inline.get(), "Only one child element check can be provided.");
      }
      if (inline.get().getChildren().size() == 1) {
        inline = Optional.of(inline.get().getChildren().get(0));
      }
    }

    return resolveCheck(match, checkId, inline);

  }

  public static Check resolveRequiredCheck(Match match, XmlAttribute checkId,
      Optional<XmlElement> inlineCheck) {
    Optional<Check> check = resolveCheck(match, checkId, inlineCheck);
    if (check.isPresent()) {
      return check.get();
    }
    throw new XmlException(checkId.getElement(), "Missing required check.");
  }

  public static Optional<Check> resolveCheck(Match match, XmlAttribute checkId,
      Optional<XmlElement> inlineCheck) {
    Optional<Check> check = Optional.empty();

    if (checkId.isValuePresent()) {
      check = match.getRegistry().get(Check.class, checkId.asRequiredString(), true);
    } else if (inlineCheck.isPresent()) {
      XmlElement child = inlineCheck.get();
      check = Optional
          .of(match.getFactory().getFactory(ChecksFactory.class).parseCheck(match, child));
    }

    return check;
  }

  public static Loadout resolveRequiredLoadout(Match match, XmlAttribute loadoutId,
      Optional<XmlElement> inlineLoadout) {
    Optional<Loadout> loadout = resolveLoadout(match, loadoutId, inlineLoadout);
    if (loadout.isPresent()) {
      return loadout.get();
    }
    throw new XmlException(loadoutId.getElement(), "Missing required loadout.");
  }

  public static Optional<Loadout> resolveLoadout(Match match, XmlAttribute loadoutId,
      Optional<XmlElement> inlineLoadout) {
    Optional<Loadout> loadout = Optional.empty();

    if (loadoutId.isValuePresent()) {
      loadout = match.getRegistry().get(Loadout.class, loadoutId.asRequiredString(), true);
    } else if (inlineLoadout.isPresent()) {
      loadout = Optional.of(match.getFactory().getFactory(LoadoutsFactory.class)
          .parseLoadout(match, inlineLoadout.get()));
    }

    return loadout;
  }

  public static Optional<CustomProjectile> resolveProjectile(Match match, XmlAttribute projectileId,
      Optional<XmlElement> inlineProjectile) {
    Optional<CustomProjectile> projectile = Optional.empty();

    if (projectileId.isValuePresent()) {
      projectile = match.getRegistry()
          .get(CustomProjectile.class, projectileId.asRequiredString(), true);
    } else if (inlineProjectile.isPresent()) {
      projectile = Optional.of(match.getFactory().getFactory(ProjectilesFactory.class)
          .parseProjectile(match, inlineProjectile.get()));
    }

    return projectile;
  }

  public static <T extends Region> T resolveRequiredRegionAs(Match match, Class<T> type,
      XmlAttribute regionId, Optional<XmlElement> inlineRegion) {
    Optional<T> region = resolveRegionAs(match, type, regionId, inlineRegion);
    if (region.isPresent()) {
      return region.get();
    }
    throw new XmlException(regionId.getElement(), "Missing required region.");
  }

  @SuppressWarnings("unchecked")
  public static <T extends Region> Optional<T> resolveRegionAs(Match match, Class<T> type,
      XmlAttribute regionId, Optional<XmlElement> inlineRegion) {
    Optional<Region> region = Optional.empty();

    if (regionId.isValuePresent()) {
      region = match.getRegistry().get(Region.class, regionId.asRequiredString(), true);
    } else if (inlineRegion.isPresent()) {
      region = Optional.of(match.getFactory().getFactory(RegionsFactory.class)
          .parseRegionAs(match, inlineRegion.get(), type));
    }

    if (region.isPresent()) {
      if (!type.isAssignableFrom(region.get().getClass())) {
        String error =
            "Region type mismatch. Expected \"" + type.getSimpleName() + "\" but got \"" + region
                .get().getClass().getSimpleName() + "\".";
        throw new XmlException(regionId.getElement(), error);
      }
    }

    return (Optional<T>) region;
  }

  public static <T extends Region> T resolveRequiredRegion(Match match, Class<T> type,
      XmlAttribute regionId, Optional<XmlElement> inlineRegion) {
    Optional<T> region = resolveRegion(match, type, regionId, inlineRegion);
    if (region.isPresent()) {
      return region.get();
    }
    throw new XmlException(regionId.getElement(), "Missing required region.");
  }

  public static <T extends Region> T resolveRequiredRegionChild(Match match, Class<T> type,
      XmlAttribute regionId, Optional<XmlElement> inlineRegion) {
    Optional<XmlElement> child = Optional.empty();
    if (inlineRegion.isPresent()) {
      List<XmlElement> children = inlineRegion.get().getChildren();
      if (children.size() > 1 || children.isEmpty()) {
        throw new XmlException(inlineRegion.get(), "Region count mismatch. Expected 1 child.");
      }
      child = Optional.of(children.get(0));
    }
    Optional<T> region = resolveRegion(match, type, regionId, child);
    if (region.isPresent()) {
      return region.get();
    }
    throw new XmlException(regionId.getElement(), "Missing required region.");
  }

  @SuppressWarnings("unchecked")
  public static <T extends Region> Optional<T> resolveRegion(Match match, Class<T> type,
      XmlAttribute regionId, Optional<XmlElement> inlineRegion) {
    Optional<Region> region = Optional.empty();

    if (regionId.isValuePresent()) {
      region = match.getRegistry().get(Region.class, regionId.asRequiredString(), true);
    } else if (inlineRegion.isPresent()) {
      region = Optional.of(match.getFactory().getFactory(RegionsFactory.class)
          .parseRegion(match, inlineRegion.get()));
    }

    if (region.isPresent()) {
      if (!type.isAssignableFrom(region.get().getClass())) {
        String error =
            "Region type mismatch. Expected \"" + type.getSimpleName() + "\" but got \"" + region
                .get().getClass().getSimpleName() + "\".";
        throw new XmlException(regionId.getElement(), error);
      }
    }

    return (Optional<T>) region;
  }
}
