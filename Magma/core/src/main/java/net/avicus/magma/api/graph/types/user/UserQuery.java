package net.avicus.magma.api.graph.types.user;

import com.shopify.graphql.support.Query;

public class UserQuery extends Query<UserQuery> {

  public UserQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Date when this user was created.
   */
  public UserQuery createdAt() {
    startField("created_at");

    return this;
  }

  /**
   * The discord client ID of the user.
   */
  public UserQuery discordId() {
    startField("discord_id");

    return this;
  }

  /**
   * The Minecrft locale the user had set when they last logged in.
   */
  public UserQuery locale() {
    startField("locale");

    return this;
  }

  /**
   * The last version of Minecraft the user logged in with.
   */
  public UserQuery mcVersion() {
    startField("mc_version");

    return this;
  }

  /**
   * Tracking information for the user.
   */
  public UserQuery tracker() {
    startField("tracker");

    return this;
  }

  /**
   * The username of the user when they last logged in.
   */
  public UserQuery username() {
    startField("username");

    return this;
  }

  /**
   * The Minecraft UUID of the user.
   */
  public UserQuery uuid() {
    startField("uuid");

    return this;
  }

  /**
   * The verification key assigned to the user during registration.
   */
  public UserQuery verifyKey() {
    startField("verify_key");

    return this;
  }

  /**
   * If the user successfullly verified their identitity with the server during registration.
   */
  public UserQuery verifyKeySuccess() {
    startField("verify_key_success");

    return this;
  }
}
