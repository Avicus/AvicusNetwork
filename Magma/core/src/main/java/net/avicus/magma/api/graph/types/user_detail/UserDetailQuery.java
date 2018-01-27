package net.avicus.magma.api.graph.types.user_detail;

import com.shopify.graphql.support.Query;

public class UserDetailQuery extends Query<UserDetailQuery> {

  public UserDetailQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Raw HTML of the about page text of the user.
   */
  public UserDetailQuery about() {
    startField("about");

    return this;
  }

  /**
   * Gravatar ID of the user.
   */
  public UserDetailQuery avatar() {
    startField("avatar");

    return this;
  }

  /**
   * Path to the cover art on the profile.
   */
  public UserDetailQuery coverArt() {
    startField("cover_art");

    return this;
  }

  /**
   * Email of the user used for gravatar
   */
  public UserDetailQuery email() {
    startField("email");

    return this;
  }

  /**
   * If the user has confirmed their email.
   */
  public UserDetailQuery emailStatus() {
    startField("email_status");

    return this;
  }

  /**
   * Facebook name of the user
   */
  public UserDetailQuery facebook() {
    startField("facebook");

    return this;
  }

  /**
   * Gender of the user.
   */
  public UserDetailQuery gender() {
    startField("gender");

    return this;
  }

  /**
   * Github username of the user.
   */
  public UserDetailQuery github() {
    startField("github");

    return this;
  }

  /**
   * Instagram handle of the user.
   */
  public UserDetailQuery instagram() {
    startField("instagram");

    return this;
  }

  /**
   * List of things the user is interested in.
   */
  public UserDetailQuery interests() {
    startField("interests");

    return this;
  }

  /**
   * Skype username of the user.
   */
  public UserDetailQuery skype() {
    startField("skype");

    return this;
  }

  /**
   * Steam ID of the user.
   */
  public UserDetailQuery steam() {
    startField("steam");

    return this;
  }

  /**
   * Twitch username of the user.
   */
  public UserDetailQuery twitch() {
    startField("twitch");

    return this;
  }

  /**
   * Twitter handle of the user.
   */
  public UserDetailQuery twitter() {
    startField("twitter");

    return this;
  }

  /**
   * Id of the user that these details represent.
   */
  public UserDetailQuery userId() {
    startField("user_id");

    return this;
  }
}
