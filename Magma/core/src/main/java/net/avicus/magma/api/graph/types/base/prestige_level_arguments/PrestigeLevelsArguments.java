package net.avicus.magma.api.graph.types.base.prestige_level_arguments;

import com.shopify.graphql.support.Arguments;

public class PrestigeLevelsArguments extends Arguments {

  private final StringBuilder builder;

  public PrestigeLevelsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public PrestigeLevelsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public PrestigeLevelsArguments userId(Integer value) {
    if (value != null) {
      startArgument("user_id");
      builder().append(value);
    }
    return this;
  }

  public PrestigeLevelsArguments seasonId(Integer value) {
    if (value != null) {
      startArgument("season_id");
      builder().append(value);
    }
    return this;
  }

  public PrestigeLevelsArguments level(Integer value) {
    if (value != null) {
      startArgument("level");
      builder().append(value);
    }
    return this;
  }
}
