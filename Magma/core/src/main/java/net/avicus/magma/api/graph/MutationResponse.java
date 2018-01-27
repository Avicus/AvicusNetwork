package net.avicus.magma.api.graph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shopify.graphql.support.Error;
import com.shopify.graphql.support.SchemaViolationError;
import com.shopify.graphql.support.TopLevelResponse;
import java.util.List;
import net.avicus.magma.api.graph.mutations.Mutation;

public class MutationResponse {

  private TopLevelResponse response;
  private Mutation data;

  public MutationResponse(TopLevelResponse response) throws SchemaViolationError {
    this.response = response;
    this.data = response.getData() != null ? new Mutation(response.getData()) : null;
  }

  public static MutationResponse fromJson(String json) throws SchemaViolationError {
    final TopLevelResponse response = new Gson().fromJson(json, TopLevelResponse.class);
    return new MutationResponse(response);
  }

  public Mutation getData() {
    return data;
  }

  public List<Error> getErrors() {
    return response.getErrors();
  }

  public String toJson() {
    return new Gson().toJson(response);
  }

  public String prettyPrintJson() {
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(response);
  }
}
