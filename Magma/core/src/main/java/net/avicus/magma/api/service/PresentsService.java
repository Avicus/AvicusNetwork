package net.avicus.magma.api.service;

import java.util.Optional;
import javafx.util.Pair;
import net.avicus.magma.api.APIClient;
import net.avicus.magma.api.graph.MutationResponse;
import net.avicus.magma.api.graph.QLBuilder;
import net.avicus.magma.api.graph.inputs.PresentFindInput;
import net.avicus.magma.api.graph.types.present.Present;
import net.avicus.magma.database.model.impl.User;

public class PresentsService extends Service<Present> {

  public PresentsService(APIClient client) {
    super(client, "presents");
  }

  public Pair<Boolean, String> find(User who, String family, String slug) {
    Optional<MutationResponse> res = getClient().mutation(QLBuilder.mutation(
        m -> m.presentFind(new PresentFindInput(who.getId(), slug, family).setCreate(true),
            q -> q.success().message())));
    return new Pair<>(res.map(p -> p.getData().getPresentFind().isSuccess()).orElse(false),
        res.map(p -> p.getData().getPresentFind().getMessage()).orElse("Failed to contact API!"));
  }
}