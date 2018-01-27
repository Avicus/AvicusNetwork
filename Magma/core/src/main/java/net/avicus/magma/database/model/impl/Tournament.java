package net.avicus.magma.database.model.impl;

import java.util.Date;
import lombok.Getter;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

public class Tournament extends Model {

  @Getter
  @Id
  @Column
  private int id;

  @Getter
  @Column
  private String name;

  @Getter
  @Column
  private String slug;

  @Getter
  @Column
  private int min;

  @Getter
  @Column
  private int max;

  @Getter
  @Column(name = "open_at")
  private Date open;

  @Getter
  @Column(name = "close_at")
  private Date close;
}
