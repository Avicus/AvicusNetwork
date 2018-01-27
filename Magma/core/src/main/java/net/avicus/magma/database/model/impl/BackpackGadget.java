package net.avicus.magma.database.model.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class BackpackGadget extends Model {

  private static final JsonParser parser = new JsonParser();
  @Getter
  @Id
  @Column
  private int id;
  @Getter
  @Column(name = "user_id")
  private int userId;
  @Getter
  @Column(name = "gadget_type")
  private String gadgetType;
  @Column(type = "TEXT")
  private String gadget;
  @Column(type = "TEXT")
  private String context;
  @Getter
  @Column(name = "created_at")
  private Date createdAt;
  @Getter
  @Setter
  @Column(name = "old_id")
  private int oldId;

  public BackpackGadget() {

  }

  public BackpackGadget(int userId, String gadgetType, String gadget, String context,
      Date createdAt) {
    this.userId = userId;
    this.gadgetType = gadgetType;
    this.gadget = gadget;
    this.context = context;
    this.createdAt = createdAt;
  }

  public BackpackGadget(int userId, String gadgetType, JsonObject gadget, JsonObject context,
      Date createdAt) {
    this(userId, gadgetType, gadget.toString(), context.toString(), createdAt);
  }

  public JsonObject getGadget() {
    return parser.parse(this.gadget).getAsJsonObject();
  }

  public JsonObject getContext() {
    return parser.parse(this.context).getAsJsonObject();
  }
}
