package net.avicus.magma.api.graph.mutations.user_login.disallow_scope;

import com.shopify.graphql.support.Query;
import net.avicus.magma.api.graph.types.punishment.PunishmentQuery;
import net.avicus.magma.api.graph.types.punishment.PunishmentQueryDefinition;

public class DisallowScopeQuery extends Query<DisallowScopeQuery> {

  public DisallowScopeQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * If the login is disallowed due to lack of permissions.
   */
  public DisallowScopeQuery permissions() {
    startField("permissions");

    return this;
  }

  /**
   * If the login is disallowed due to a punishment.
   */
  public DisallowScopeQuery punishment() {
    startField("punishment");

    return this;
  }

  /**
   * The punishment that disallowed the login.
   */
  public DisallowScopeQuery punishmentData(PunishmentQueryDefinition queryDef) {
    startField("punishment_data");

    builder().append('{');
    queryDef.define(new PunishmentQuery(builder()));
    builder().append('}');

    return this;
  }
}
