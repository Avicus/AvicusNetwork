package net.avicus.magma.api.graph.types.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.avicus.magma.api.graph.types.achievement.Achievement;
import net.avicus.magma.api.graph.types.achievement_pursuit.AchievementPursuit;
import net.avicus.magma.api.graph.types.achievement_receiver.AchievementReceiver;
import net.avicus.magma.api.graph.types.alert.Alert;
import net.avicus.magma.api.graph.types.announcement.Announcement;
import net.avicus.magma.api.graph.types.appeal.Appeal;
import net.avicus.magma.api.graph.types.backpack_gadget.BackpackGadget;
import net.avicus.magma.api.graph.types.credit_transaction.CreditTransaction;
import net.avicus.magma.api.graph.types.death.Death;
import net.avicus.magma.api.graph.types.experience_transaction.ExperienceTransaction;
import net.avicus.magma.api.graph.types.friend.Friend;
import net.avicus.magma.api.graph.types.livestream.Livestream;
import net.avicus.magma.api.graph.types.map_rating.MapRating;
import net.avicus.magma.api.graph.types.membership.Membership;
import net.avicus.magma.api.graph.types.objective.Objective;
import net.avicus.magma.api.graph.types.objective_type.ObjectiveType;
import net.avicus.magma.api.graph.types.present.Present;
import net.avicus.magma.api.graph.types.present_finder.PresentFinder;
import net.avicus.magma.api.graph.types.prestige_level.PrestigeLevel;
import net.avicus.magma.api.graph.types.prestige_season.PrestigeSeason;
import net.avicus.magma.api.graph.types.punishment.Punishment;
import net.avicus.magma.api.graph.types.rank.Rank;
import net.avicus.magma.api.graph.types.registration.Registration;
import net.avicus.magma.api.graph.types.report.Report;
import net.avicus.magma.api.graph.types.reserved_slot.ReservedSlot;
import net.avicus.magma.api.graph.types.server.Server;
import net.avicus.magma.api.graph.types.server_booster.ServerBooster;
import net.avicus.magma.api.graph.types.server_category.ServerCategory;
import net.avicus.magma.api.graph.types.server_group.ServerGroup;
import net.avicus.magma.api.graph.types.session.Session;
import net.avicus.magma.api.graph.types.setting.Setting;
import net.avicus.magma.api.graph.types.team.Team;
import net.avicus.magma.api.graph.types.team_member.TeamMember;
import net.avicus.magma.api.graph.types.tournament.Tournament;
import net.avicus.magma.api.graph.types.user.User;
import net.avicus.magma.api.graph.types.user_detail.UserDetail;
import net.avicus.magma.api.graph.types.username.Username;

public class BaseQuery extends AbstractResponse<BaseQuery> {

  public BaseQuery(JsonObject fields) throws SchemaViolationError {
    for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
      String key = field.getKey();
      String fieldName = getFieldName(key);
      switch (fieldName) {
        case "achievementPursuits": {
          List<AchievementPursuit> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<AchievementPursuit> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              AchievementPursuit optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new AchievementPursuit(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "achievementReceivers": {
          List<AchievementReceiver> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<AchievementReceiver> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              AchievementReceiver optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new AchievementReceiver(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "achievements": {
          List<Achievement> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Achievement> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Achievement optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Achievement(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "alerts": {
          List<Alert> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Alert> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Alert optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Alert(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "allAfterAlert": {
          List<Alert> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Alert> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Alert optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Alert(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "allLiveLivestreams": {
          List<Livestream> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Livestream> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Livestream optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Livestream(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "announcements": {
          List<Announcement> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Announcement> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Announcement optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Announcement(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "appeals": {
          List<Appeal> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Appeal> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Appeal optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Appeal(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "backpackGadgets": {
          List<BackpackGadget> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<BackpackGadget> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              BackpackGadget optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new BackpackGadget(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "creditTransactions": {
          List<CreditTransaction> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<CreditTransaction> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              CreditTransaction optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new CreditTransaction(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "deaths": {
          List<Death> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Death> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Death optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Death(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "experienceTransactions": {
          List<ExperienceTransaction> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<ExperienceTransaction> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              ExperienceTransaction optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new ExperienceTransaction(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "friends": {
          List<Friend> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Friend> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Friend optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Friend(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "livestreams": {
          List<Livestream> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Livestream> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Livestream optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Livestream(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "mapRatings": {
          List<MapRating> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<MapRating> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              MapRating optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new MapRating(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "memberships": {
          List<Membership> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Membership> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Membership optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Membership(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "objectiveTypes": {
          List<ObjectiveType> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<ObjectiveType> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              ObjectiveType optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new ObjectiveType(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "objectives": {
          List<Objective> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Objective> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Objective optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Objective(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "presentFinders": {
          List<PresentFinder> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<PresentFinder> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              PresentFinder optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new PresentFinder(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "presents": {
          List<Present> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Present> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Present optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Present(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "prestigeLevels": {
          List<PrestigeLevel> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<PrestigeLevel> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              PrestigeLevel optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new PrestigeLevel(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "prestigeSeasons": {
          List<PrestigeSeason> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<PrestigeSeason> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              PrestigeSeason optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new PrestigeSeason(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "punishments": {
          List<Punishment> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Punishment> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Punishment optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Punishment(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "ranks": {
          List<Rank> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Rank> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Rank optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Rank(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "registrations": {
          List<Registration> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Registration> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Registration optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Registration(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "reports": {
          List<Report> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Report> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Report optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Report(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "reservedSlots": {
          List<ReservedSlot> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<ReservedSlot> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              ReservedSlot optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new ReservedSlot(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "serverBoosters": {
          List<ServerBooster> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<ServerBooster> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              ServerBooster optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new ServerBooster(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "serverCategories": {
          List<ServerCategory> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<ServerCategory> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              ServerCategory optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new ServerCategory(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "serverGroups": {
          List<ServerGroup> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<ServerGroup> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              ServerGroup optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new ServerGroup(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "servers": {
          List<Server> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Server> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Server optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Server(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "sessions": {
          List<Session> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Session> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Session optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Session(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "settings": {
          List<Setting> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Setting> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Setting optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Setting(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "teamMembers": {
          List<TeamMember> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<TeamMember> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              TeamMember optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new TeamMember(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "teams": {
          List<Team> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Team> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Team optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Team(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "tournaments": {
          List<Tournament> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Tournament> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Tournament optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Tournament(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "userDetails": {
          List<UserDetail> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<UserDetail> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              UserDetail optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new UserDetail(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "usernames": {
          List<Username> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<Username> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              Username optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new Username(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "users": {
          List<User> optional1 = null;
          if (!field.getValue().isJsonNull()) {
            List<User> list1 = new ArrayList<>();
            for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
              User optional2 = null;
              if (!element1.isJsonNull()) {
                optional2 = new User(jsonAsObject(element1, key));
              }

              list1.add(optional2);
            }

            optional1 = list1;
          }

          responseData.put(key, optional1);

          break;
        }

        case "__typename": {
          responseData.put(key, jsonAsString(field.getValue(), key));
          break;
        }
        default: {
          throw new SchemaViolationError(this, key, field.getValue());
        }
      }
    }
  }

  public String getGraphQlTypeName() {
    return "BaseQuery";
  }

  /**
   * Search for all AchievementPursuits
   */

  public List<AchievementPursuit> getAchievementPursuits() {
    return (List<AchievementPursuit>) get("achievementPursuits");
  }

  public BaseQuery setAchievementPursuits(List<AchievementPursuit> arg) {
    optimisticData.put(getKey("achievementPursuits"), arg);
    return this;
  }

  /**
   * Search for all AchievementReceivers
   */

  public List<AchievementReceiver> getAchievementReceivers() {
    return (List<AchievementReceiver>) get("achievementReceivers");
  }

  public BaseQuery setAchievementReceivers(List<AchievementReceiver> arg) {
    optimisticData.put(getKey("achievementReceivers"), arg);
    return this;
  }

  /**
   * Search for all Achievements
   */

  public List<Achievement> getAchievements() {
    return (List<Achievement>) get("achievements");
  }

  public BaseQuery setAchievements(List<Achievement> arg) {
    optimisticData.put(getKey("achievements"), arg);
    return this;
  }

  /**
   * Search for all Alerts
   */

  public List<Alert> getAlerts() {
    return (List<Alert>) get("alerts");
  }

  public BaseQuery setAlerts(List<Alert> arg) {
    optimisticData.put(getKey("alerts"), arg);
    return this;
  }

  /**
   * Find all created after the given date.
   */

  public List<Alert> getAllAfterAlert() {
    return (List<Alert>) get("allAfterAlert");
  }

  public BaseQuery setAllAfterAlert(List<Alert> arg) {
    optimisticData.put(getKey("allAfterAlert"), arg);
    return this;
  }

  /**
   * Find all channels which are currently live.
   */

  public List<Livestream> getAllLiveLivestreams() {
    return (List<Livestream>) get("allLiveLivestreams");
  }

  public BaseQuery setAllLiveLivestreams(List<Livestream> arg) {
    optimisticData.put(getKey("allLiveLivestreams"), arg);
    return this;
  }

  /**
   * Search for all Announcements
   */

  public List<Announcement> getAnnouncements() {
    return (List<Announcement>) get("announcements");
  }

  public BaseQuery setAnnouncements(List<Announcement> arg) {
    optimisticData.put(getKey("announcements"), arg);
    return this;
  }

  /**
   * Search for all Appeals
   */

  public List<Appeal> getAppeals() {
    return (List<Appeal>) get("appeals");
  }

  public BaseQuery setAppeals(List<Appeal> arg) {
    optimisticData.put(getKey("appeals"), arg);
    return this;
  }

  /**
   * Search for all BackpackGadgets
   */

  public List<BackpackGadget> getBackpackGadgets() {
    return (List<BackpackGadget>) get("backpackGadgets");
  }

  public BaseQuery setBackpackGadgets(List<BackpackGadget> arg) {
    optimisticData.put(getKey("backpackGadgets"), arg);
    return this;
  }

  /**
   * Search for all CreditTransactions
   */

  public List<CreditTransaction> getCreditTransactions() {
    return (List<CreditTransaction>) get("creditTransactions");
  }

  public BaseQuery setCreditTransactions(List<CreditTransaction> arg) {
    optimisticData.put(getKey("creditTransactions"), arg);
    return this;
  }

  /**
   * Search for all Deaths
   */

  public List<Death> getDeaths() {
    return (List<Death>) get("deaths");
  }

  public BaseQuery setDeaths(List<Death> arg) {
    optimisticData.put(getKey("deaths"), arg);
    return this;
  }

  /**
   * Search for all ExperienceTransactions
   */

  public List<ExperienceTransaction> getExperienceTransactions() {
    return (List<ExperienceTransaction>) get("experienceTransactions");
  }

  public BaseQuery setExperienceTransactions(List<ExperienceTransaction> arg) {
    optimisticData.put(getKey("experienceTransactions"), arg);
    return this;
  }

  /**
   * Search for all Friends
   */

  public List<Friend> getFriends() {
    return (List<Friend>) get("friends");
  }

  public BaseQuery setFriends(List<Friend> arg) {
    optimisticData.put(getKey("friends"), arg);
    return this;
  }

  /**
   * Search for all Livestreams
   */

  public List<Livestream> getLivestreams() {
    return (List<Livestream>) get("livestreams");
  }

  public BaseQuery setLivestreams(List<Livestream> arg) {
    optimisticData.put(getKey("livestreams"), arg);
    return this;
  }

  /**
   * Search for all MapRatings
   */

  public List<MapRating> getMapRatings() {
    return (List<MapRating>) get("mapRatings");
  }

  public BaseQuery setMapRatings(List<MapRating> arg) {
    optimisticData.put(getKey("mapRatings"), arg);
    return this;
  }

  /**
   * Search for all Memberships
   */

  public List<Membership> getMemberships() {
    return (List<Membership>) get("memberships");
  }

  public BaseQuery setMemberships(List<Membership> arg) {
    optimisticData.put(getKey("memberships"), arg);
    return this;
  }

  /**
   * Search for all ObjectiveTypes
   */

  public List<ObjectiveType> getObjectiveTypes() {
    return (List<ObjectiveType>) get("objectiveTypes");
  }

  public BaseQuery setObjectiveTypes(List<ObjectiveType> arg) {
    optimisticData.put(getKey("objectiveTypes"), arg);
    return this;
  }

  /**
   * Search for all Objectives
   */

  public List<Objective> getObjectives() {
    return (List<Objective>) get("objectives");
  }

  public BaseQuery setObjectives(List<Objective> arg) {
    optimisticData.put(getKey("objectives"), arg);
    return this;
  }

  /**
   * Search for all PresentFinders
   */

  public List<PresentFinder> getPresentFinders() {
    return (List<PresentFinder>) get("presentFinders");
  }

  public BaseQuery setPresentFinders(List<PresentFinder> arg) {
    optimisticData.put(getKey("presentFinders"), arg);
    return this;
  }

  /**
   * Search for all Presents
   */

  public List<Present> getPresents() {
    return (List<Present>) get("presents");
  }

  public BaseQuery setPresents(List<Present> arg) {
    optimisticData.put(getKey("presents"), arg);
    return this;
  }

  /**
   * Search for all PrestigeLevels
   */

  public List<PrestigeLevel> getPrestigeLevels() {
    return (List<PrestigeLevel>) get("prestigeLevels");
  }

  public BaseQuery setPrestigeLevels(List<PrestigeLevel> arg) {
    optimisticData.put(getKey("prestigeLevels"), arg);
    return this;
  }

  /**
   * Search for all PrestigeSeasons
   */

  public List<PrestigeSeason> getPrestigeSeasons() {
    return (List<PrestigeSeason>) get("prestigeSeasons");
  }

  public BaseQuery setPrestigeSeasons(List<PrestigeSeason> arg) {
    optimisticData.put(getKey("prestigeSeasons"), arg);
    return this;
  }

  /**
   * Search for all Punishments
   */

  public List<Punishment> getPunishments() {
    return (List<Punishment>) get("punishments");
  }

  public BaseQuery setPunishments(List<Punishment> arg) {
    optimisticData.put(getKey("punishments"), arg);
    return this;
  }

  /**
   * Search for all Ranks
   */

  public List<Rank> getRanks() {
    return (List<Rank>) get("ranks");
  }

  public BaseQuery setRanks(List<Rank> arg) {
    optimisticData.put(getKey("ranks"), arg);
    return this;
  }

  /**
   * Search for all Registrations
   */

  public List<Registration> getRegistrations() {
    return (List<Registration>) get("registrations");
  }

  public BaseQuery setRegistrations(List<Registration> arg) {
    optimisticData.put(getKey("registrations"), arg);
    return this;
  }

  /**
   * Search for all Reports
   */

  public List<Report> getReports() {
    return (List<Report>) get("reports");
  }

  public BaseQuery setReports(List<Report> arg) {
    optimisticData.put(getKey("reports"), arg);
    return this;
  }

  /**
   * Search for all ReservedSlots
   */

  public List<ReservedSlot> getReservedSlots() {
    return (List<ReservedSlot>) get("reservedSlots");
  }

  public BaseQuery setReservedSlots(List<ReservedSlot> arg) {
    optimisticData.put(getKey("reservedSlots"), arg);
    return this;
  }

  /**
   * Search for all ServerBoosters
   */

  public List<ServerBooster> getServerBoosters() {
    return (List<ServerBooster>) get("serverBoosters");
  }

  public BaseQuery setServerBoosters(List<ServerBooster> arg) {
    optimisticData.put(getKey("serverBoosters"), arg);
    return this;
  }

  /**
   * Search for all ServerCategories
   */

  public List<ServerCategory> getServerCategories() {
    return (List<ServerCategory>) get("serverCategories");
  }

  public BaseQuery setServerCategories(List<ServerCategory> arg) {
    optimisticData.put(getKey("serverCategories"), arg);
    return this;
  }

  /**
   * Search for all ServerGroups
   */

  public List<ServerGroup> getServerGroups() {
    return (List<ServerGroup>) get("serverGroups");
  }

  public BaseQuery setServerGroups(List<ServerGroup> arg) {
    optimisticData.put(getKey("serverGroups"), arg);
    return this;
  }

  /**
   * Search for all Servers
   */

  public List<Server> getServers() {
    return (List<Server>) get("servers");
  }

  public BaseQuery setServers(List<Server> arg) {
    optimisticData.put(getKey("servers"), arg);
    return this;
  }

  /**
   * Search for all Sessions
   */

  public List<Session> getSessions() {
    return (List<Session>) get("sessions");
  }

  public BaseQuery setSessions(List<Session> arg) {
    optimisticData.put(getKey("sessions"), arg);
    return this;
  }

  /**
   * Search for all Settings
   */

  public List<Setting> getSettings() {
    return (List<Setting>) get("settings");
  }

  public BaseQuery setSettings(List<Setting> arg) {
    optimisticData.put(getKey("settings"), arg);
    return this;
  }

  /**
   * Search for all TeamMembers
   */

  public List<TeamMember> getTeamMembers() {
    return (List<TeamMember>) get("teamMembers");
  }

  public BaseQuery setTeamMembers(List<TeamMember> arg) {
    optimisticData.put(getKey("teamMembers"), arg);
    return this;
  }

  /**
   * Search for all Teams
   */

  public List<Team> getTeams() {
    return (List<Team>) get("teams");
  }

  public BaseQuery setTeams(List<Team> arg) {
    optimisticData.put(getKey("teams"), arg);
    return this;
  }

  /**
   * Search for all Tournaments
   */

  public List<Tournament> getTournaments() {
    return (List<Tournament>) get("tournaments");
  }

  public BaseQuery setTournaments(List<Tournament> arg) {
    optimisticData.put(getKey("tournaments"), arg);
    return this;
  }

  /**
   * Search for all UserDetails
   */

  public List<UserDetail> getUserDetails() {
    return (List<UserDetail>) get("userDetails");
  }

  public BaseQuery setUserDetails(List<UserDetail> arg) {
    optimisticData.put(getKey("userDetails"), arg);
    return this;
  }

  /**
   * Search for all Usernames
   */

  public List<Username> getUsernames() {
    return (List<Username>) get("usernames");
  }

  public BaseQuery setUsernames(List<Username> arg) {
    optimisticData.put(getKey("usernames"), arg);
    return this;
  }

  /**
   * Search for all Users
   */

  public List<User> getUsers() {
    return (List<User>) get("users");
  }

  public BaseQuery setUsers(List<User> arg) {
    optimisticData.put(getKey("users"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "achievementPursuits":
        return true;

      case "achievementReceivers":
        return true;

      case "achievements":
        return true;

      case "alerts":
        return true;

      case "allAfterAlert":
        return true;

      case "allLiveLivestreams":
        return true;

      case "announcements":
        return true;

      case "appeals":
        return true;

      case "backpackGadgets":
        return true;

      case "creditTransactions":
        return true;

      case "deaths":
        return true;

      case "experienceTransactions":
        return true;

      case "friends":
        return true;

      case "livestreams":
        return true;

      case "mapRatings":
        return true;

      case "memberships":
        return true;

      case "objectiveTypes":
        return true;

      case "objectives":
        return true;

      case "presentFinders":
        return true;

      case "presents":
        return true;

      case "prestigeLevels":
        return true;

      case "prestigeSeasons":
        return true;

      case "punishments":
        return true;

      case "ranks":
        return true;

      case "registrations":
        return true;

      case "reports":
        return true;

      case "reservedSlots":
        return true;

      case "serverBoosters":
        return true;

      case "serverCategories":
        return true;

      case "serverGroups":
        return true;

      case "servers":
        return true;

      case "sessions":
        return true;

      case "settings":
        return true;

      case "teamMembers":
        return true;

      case "teams":
        return true;

      case "tournaments":
        return true;

      case "userDetails":
        return true;

      case "usernames":
        return true;

      case "users":
        return true;

      default:
        return false;
    }
  }
}
