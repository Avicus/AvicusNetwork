package net.avicus.magma.api.service;

import com.shopify.graphql.support.AbstractResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import net.avicus.magma.api.APIClient;
import net.avicus.magma.api.graph.QLBuilder;
import net.avicus.magma.api.graph.types.base.BaseQueryQueryDefinition;

@Getter(value = AccessLevel.PROTECTED)
public abstract class Service<M extends AbstractResponse> {

  private final APIClient client;
  private final String fieldId;

  public Service(APIClient client, String fieldId) {
    this.client = client;
    this.fieldId = fieldId;
  }

  public Optional<M> findOne(BaseQueryQueryDefinition definition) {
    List<M> res = client.query(QLBuilder.query(definition)).map(b -> (List<M>) b.get(this.fieldId))
        .orElse(new ArrayList<M>());
    return res.isEmpty() ? Optional.empty() : Optional.ofNullable(res.get(0));
  }

  public List<M> findMany(BaseQueryQueryDefinition definition) {
    return client.query(QLBuilder.query(definition)).map(b -> (List<M>) b.get(this.fieldId))
        .orElse(new ArrayList<M>());
  }
}
