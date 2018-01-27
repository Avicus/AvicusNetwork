package net.avicus.magma.api.graph.types.setting;

import com.shopify.graphql.support.Query;

public class SettingQuery extends Query<SettingQuery> {

  public SettingQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Key of the setting.
   */
  public SettingQuery key() {
    startField("key");

    return this;
  }

  /**
   * ID of the user that this setting is for.
   */
  public SettingQuery userId() {
    startField("user_id");

    return this;
  }

  /**
   * Value of the setting.
   */
  public SettingQuery value() {
    startField("value");

    return this;
  }
}
