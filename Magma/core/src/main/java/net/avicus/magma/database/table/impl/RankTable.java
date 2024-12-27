package net.avicus.magma.database.table.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.quest.database.Database;
import net.avicus.quest.model.ModelList;
import net.avicus.quest.model.Table;

public class RankTable extends Table<Rank> {

  public RankTable(Database database, String name, Class<Rank> model) {
    super(database, name, model);
  }

  public List<Rank> all() {
    return select().execute();
  }

  public List<Rank> allStaff() {
    return select().where("is_staff", true).execute();
  }

  public Optional<Rank> findByName(String name) {
    List<Rank> list = select().where("name", name).execute();
    if (list.size() > 0) {
      return Optional.of(list.get(0));
    }
    return Optional.empty();
  }


  public Optional<Rank> findById(int id) {
    List<Rank> list = select().where("id", id).execute();
    if (list.size() > 0) {
      return Optional.of(list.get(0));
    }
    return Optional.empty();
  }

  public Rank getOrCreate(String name) {
    Rank rank = select().where("name", name).limit(1).execute().first();
    if (rank == null) {
      rank = new Rank(name);
      insert(rank);
    }

    return rank;
  }

  public List<Rank> inheritanceTree(Rank parent) {
    List<Rank> tree = new ArrayList<>();

    Optional<Rank> directDescendant = inheritance(parent);

    if (directDescendant.isPresent()) {
      tree.add(directDescendant.get());
      tree.addAll(inheritanceTree(directDescendant.get()));
    }

    return tree;
  }

  public Optional<Rank> inheritance(Rank rank) {
    if (rank.getInheritenceId() == 0) {
      return Optional.empty();
    }

    ModelList<Rank> res = select().where("id", rank.getInheritenceId()).limit(1).execute();

    if (res.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(res.first());
  }

  public List<String> getTSPermissions(Rank rank) {
    if (rank.getTsPermissionsRaw() == null) {
      return new ArrayList<>();
    }

    String perms = rank.getTsPermissionsRaw();

    for (Rank child : inheritanceTree(rank)) {
      perms = perms + "\n" + child.getTsPermissionsRaw();
    }

    return Splitter.on("\n").splitToList(perms);
  }

  public List<String> getPermissions(Rank rank) {
    if (rank.getPermissionsRaw() == null) {
      return new ArrayList<>();
    }

    String perms = rank.getPermissionsRaw();

    for (Rank child : inheritanceTree(rank)) {
      perms = perms + "\n" + child.getPermissionsRaw();
    }

    return Splitter.on("\n").splitToList(perms);
  }

  public List<String> getCategoryPermissions(Rank rank, int serverCategory) {
    if (rank.getCategoryPermissionsRaw() == null) {
      return new ArrayList<>();
    }

    Gson gson = new Gson();

    String perms = rank.getCategoryPermissionsRaw();

    HashMap<String, JsonArray> catPerms = Maps.newHashMap();

    Map jsonMap = gson.fromJson(perms, HashMap.class);

    new JsonParser().parse(gson.toJson(jsonMap)).getAsJsonObject().entrySet().forEach(e -> {
      if (e.getValue().isJsonNull() || !e.getValue().isJsonArray()) {
        return;
      }
      catPerms.put(e.getKey(), e.getValue().getAsJsonArray());
    });

    inheritanceTree(rank).forEach((r) -> {
      Map jsonMapInner = gson.fromJson(r.getCategoryPermissionsRaw(), HashMap.class);
      new JsonParser().parse(gson.toJson(jsonMapInner)).getAsJsonObject().entrySet().forEach(e -> {
        if (e.getValue().isJsonNull() || !e.getValue().isJsonArray()) {
          return;
        }

        if (catPerms.containsKey(e.getKey())) {
          catPerms.get(e.getKey()).addAll(e.getValue().getAsJsonArray());
        } else {
          catPerms.put(e.getKey(), e.getValue().getAsJsonArray());
        }
      });
    });

    if (!catPerms.containsKey(serverCategory + "")) {
      return new ArrayList<>();
    }

    JsonArray lines = catPerms.get(serverCategory + "");
    List<String> result = new ArrayList<>();

    lines.forEach((line) -> result.add(line.getAsString()));
    return result;
  }


}
