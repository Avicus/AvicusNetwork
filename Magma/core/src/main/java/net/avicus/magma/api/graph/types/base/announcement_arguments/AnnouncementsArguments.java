package net.avicus.magma.api.graph.types.base.announcement_arguments;

import com.shopify.graphql.support.Arguments;

public class AnnouncementsArguments extends Arguments {

  private final StringBuilder builder;

  public AnnouncementsArguments(StringBuilder builder) {
    super(builder, true);
    this.builder = builder;
  }

  private StringBuilder builder() {
    return builder;
  }

  public AnnouncementsArguments id(Integer value) {
    if (value != null) {
      startArgument("id");
      builder().append(value);
    }
    return this;
  }

  public AnnouncementsArguments motd(Boolean value) {
    if (value != null) {
      startArgument("motd");
      builder().append(value);
    }
    return this;
  }

  public AnnouncementsArguments lobby(Boolean value) {
    if (value != null) {
      startArgument("lobby");
      builder().append(value);
    }
    return this;
  }

  public AnnouncementsArguments tips(Boolean value) {
    if (value != null) {
      startArgument("tips");
      builder().append(value);
    }
    return this;
  }

  public AnnouncementsArguments web(Boolean value) {
    if (value != null) {
      startArgument("web");
      builder().append(value);
    }
    return this;
  }

  public AnnouncementsArguments popup(Boolean value) {
    if (value != null) {
      startArgument("popup");
      builder().append(value);
    }
    return this;
  }

  public AnnouncementsArguments motdFormat(Boolean value) {
    if (value != null) {
      startArgument("motd_format");
      builder().append(value);
    }
    return this;
  }

  public AnnouncementsArguments enabled(Boolean value) {
    if (value != null) {
      startArgument("enabled");
      builder().append(value);
    }
    return this;
  }
}
