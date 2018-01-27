package net.avicus.magma.api;

import com.shopify.graphql.support.SchemaViolationError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.avicus.magma.api.graph.MutationResponse;
import net.avicus.magma.api.graph.QueryResponse;
import net.avicus.magma.api.graph.types.base.BaseQuery;
import net.avicus.magma.api.http.Singleton;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

public final class APIClient {

  private final String baseUrl;
  private final String apiKey;

  public APIClient(String baseUrl, String apiKey) throws IOException {
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    getClient().execute(new HttpGet(this.baseUrl));
  }

  public static CloseableHttpClient getClient() {
    // The thread safe client is held by the singleton.
    return Singleton.Client.get();
  }

  public static void shutdown() throws InterruptedException {
    // Shutdown the monitor.
    Singleton.Client.monitor.shutdown();
  }

  private HttpPost buildPost(String query) {
    ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
    postParameters.add(new BasicNameValuePair("key", this.apiKey));
    postParameters.add(new BasicNameValuePair("query", query));
    //Build the server URI together with the parameters you wish to pass
    try {
      URIBuilder uriBuilder = new URIBuilder(this.baseUrl);
      uriBuilder.addParameters(postParameters);

      HttpPost post = new HttpPost(uriBuilder.build());
      post.setHeader("Content-Type", "application/graphql");

      return post;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Optional<BaseQuery> query(StringBuilder builder) {
    try {
      CloseableHttpResponse response = getClient().execute(buildPost(builder.toString()));
      return readResponse(response, (stream) -> {
        String json = new BufferedReader(new InputStreamReader(stream))
            .lines().collect(Collectors.joining("\n"));
        try {
          return Optional.ofNullable(QueryResponse.fromJson(json).getData());
        } catch (SchemaViolationError e) {
          e.printStackTrace();
        }
        return Optional.empty();
      });
    } catch (IOException e) {
      System.out.println("Failed to load data from API!");
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public Optional<MutationResponse> mutation(StringBuilder builder) {
    try {
      CloseableHttpResponse response = getClient().execute(buildPost(builder.toString()));
      return readResponse(response, (stream) -> {
        String json = new BufferedReader(new InputStreamReader(stream))
            .lines().collect(Collectors.joining("\n"));
        try {
          return Optional.of(MutationResponse.fromJson(json));
        } catch (SchemaViolationError e) {
          e.printStackTrace();
        }
        return Optional.empty();
      });
    } catch (IOException e) {
      System.out.println("Failed to load data from API!");
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public <R> R readResponse(CloseableHttpResponse response,
      Function<InputStream, R> function) throws IOException {
    // What was read.
    R red = null;
    try {
      // What happened?
      if (response.getStatusLine().getStatusCode() == 200) {
        // Roll out the results
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          InputStream content = entity.getContent();
          try {
            red = function.apply(content);
          } finally {
            // Always close the content.
            content.close();
          }
        }
      } else {
        // The finally below will clean up.
        throw new IOException("HTTP Response: " + response.getStatusLine().getStatusCode());
      }
    } finally {
      // Always close the response.
      response.close();
    }

    return red;
  }
}
