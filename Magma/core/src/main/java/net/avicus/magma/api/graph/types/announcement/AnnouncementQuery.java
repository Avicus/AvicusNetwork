package net.avicus.magma.api.graph.types.announcement;

import com.shopify.graphql.support.Query;

public class AnnouncementQuery extends Query<AnnouncementQuery> {

  public AnnouncementQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * The text of the announcement.
   */
  public AnnouncementQuery body() {
    startField("body");

    return this;
  }

  /**
   * Date when this announcement was created.
   */
  public AnnouncementQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * If the announcement should be shown.
   */
  public AnnouncementQuery enabled() {
    startField("enabled");

    return this;
  }

  /**
   * If the announcement should show in lobbies. This will be ignored if tips is also enabled.
   */
  public AnnouncementQuery lobby() {
    startField("lobby");

    return this;
  }

  /**
   * If the announcement should be used for MOTDs.
   */
  public AnnouncementQuery motd() {
    startField("motd");

    return this;
  }

  /**
   * If the announcement should be used as an MOTD format.
   */
  public AnnouncementQuery motdFormat() {
    startField("motd_format");

    return this;
  }

  /**
   * A minecraft permission needed to view the announcement in game.
   */
  public AnnouncementQuery permission() {
    startField("permission");

    return this;
  }

  /**
   * If the announcement should be displayed as a title when a user joins a lobby,
   */
  public AnnouncementQuery popup() {
    startField("popup");

    return this;
  }

  /**
   * If the announcement should be shown periodically in game.
   */
  public AnnouncementQuery tips() {
    startField("tips");

    return this;
  }

  /**
   * Date when this announcement was last updated.
   */
  public AnnouncementQuery updatedAt() {
    startField("updated_at");

    return this;
  }

  /**
   * If the announcement should be displayed at the top of the website.
   */
  public AnnouncementQuery web() {
    startField("web");

    return this;
  }
}
