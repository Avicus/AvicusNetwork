package net.avicus.magma.api.graph.types.rank;

import com.shopify.graphql.support.Query;

public class RankQuery extends Query<RankQuery> {

  public RankQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Background color of the badge given to users who have this rank on the website.
   */
  public RankQuery badgeColor() {
    startField("badge_color");

    return this;
  }

  /**
   * Color of the text in the badge given to users who have this rank on the website.
   */
  public RankQuery badgeTextColor() {
    startField("badge_text_color");

    return this;
  }

  /**
   * Date when this rank was created.
   */
  public RankQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * Color of usernames of users who are in this rank on the website.
   */
  public RankQuery htmlColor() {
    startField("html_color");

    return this;
  }

  /**
   * ID of the rank which this one inherits permissions from.
   */
  public RankQuery inheritanceId() {
    startField("inheritance_id");

    return this;
  }

  /**
   * If the users inside of this rank should be marked as staff.
   */
  public RankQuery isStaff() {
    startField("is_staff");

    return this;
  }

  /**
   * List of permissions given to any user in the rank across the network.
   */
  public RankQuery mcPerms() {
    startField("mc_perms");

    return this;
  }

  /**
   * Prefix before users who have this rank in game.
   */
  public RankQuery mcPrefix() {
    startField("mc_prefix");

    return this;
  }

  /**
   * Suffix after users who have this rank in game
   */
  public RankQuery mcSuffix() {
    startField("mc_suffix");

    return this;
  }

  /**
   * The name of the rank.
   */
  public RankQuery name() {
    startField("name");

    return this;
  }

  /**
   * Sort order of this rank when being used to determine the color/prefix/suffix a user should
   * receive.
   */
  public RankQuery priority() {
    startField("priority");

    return this;
  }

  /**
   * Permissions given to users who have this rank on TeamSpeak
   */
  public RankQuery tsPerms() {
    startField("ts_perms");

    return this;
  }

  /**
   * Date when this rank was last updated.
   */
  public RankQuery updatedAt() {
    startField("updated_at");

    return this;
  }
}
