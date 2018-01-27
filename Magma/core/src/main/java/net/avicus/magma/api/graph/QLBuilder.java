package net.avicus.magma.api.graph;

import net.avicus.magma.api.graph.mutations.MutationQuery;
import net.avicus.magma.api.graph.mutations.MutationQueryDefinition;
import net.avicus.magma.api.graph.types.base.BaseQueryQuery;
import net.avicus.magma.api.graph.types.base.BaseQueryQueryDefinition;

public class QLBuilder {

  public static StringBuilder query(BaseQueryQueryDefinition queryDef) {
    StringBuilder queryString = new StringBuilder("{");
    BaseQueryQuery query = new BaseQueryQuery(queryString);
    queryDef.define(query);
    queryString.append('}');
    return queryString;
  }

  public static StringBuilder mutation(MutationQueryDefinition queryDef) {
    StringBuilder queryString = new StringBuilder("mutation{");
    MutationQuery query = new MutationQuery(queryString);
    queryDef.define(query);
    queryString.append('}');
    return queryString;
  }
}
