package net.avicus.magma.api.graph.types.base;

import com.shopify.graphql.support.Query;
import net.avicus.magma.api.graph.types.achievement.AchievementQuery;
import net.avicus.magma.api.graph.types.achievement.AchievementQueryDefinition;
import net.avicus.magma.api.graph.types.achievement_pursuit.AchievementPursuitQuery;
import net.avicus.magma.api.graph.types.achievement_pursuit.AchievementPursuitQueryDefinition;
import net.avicus.magma.api.graph.types.achievement_receiver.AchievementReceiverQuery;
import net.avicus.magma.api.graph.types.achievement_receiver.AchievementReceiverQueryDefinition;
import net.avicus.magma.api.graph.types.alert.AlertQuery;
import net.avicus.magma.api.graph.types.alert.AlertQueryDefinition;
import net.avicus.magma.api.graph.types.announcement.AnnouncementQuery;
import net.avicus.magma.api.graph.types.announcement.AnnouncementQueryDefinition;
import net.avicus.magma.api.graph.types.appeal.AppealQuery;
import net.avicus.magma.api.graph.types.appeal.AppealQueryDefinition;
import net.avicus.magma.api.graph.types.backpack_gadget.BackpackGadgetQuery;
import net.avicus.magma.api.graph.types.backpack_gadget.BackpackGadgetQueryDefinition;
import net.avicus.magma.api.graph.types.base.achievement_arguments.AchievementsArguments;
import net.avicus.magma.api.graph.types.base.achievement_arguments.AchievementsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.achievement_pursuit_arguments.AchievementPursuitsArguments;
import net.avicus.magma.api.graph.types.base.achievement_pursuit_arguments.AchievementPursuitsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.achievement_receiver_arguments.AchievementReceiversArguments;
import net.avicus.magma.api.graph.types.base.achievement_receiver_arguments.AchievementReceiversArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.alert_arguments.AlertsArguments;
import net.avicus.magma.api.graph.types.base.alert_arguments.AlertsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.all_after_alert_arguments.AllAfterAlertArguments;
import net.avicus.magma.api.graph.types.base.all_after_alert_arguments.AllAfterAlertArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.announcement_arguments.AnnouncementsArguments;
import net.avicus.magma.api.graph.types.base.announcement_arguments.AnnouncementsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.appeal_arguments.AppealsArguments;
import net.avicus.magma.api.graph.types.base.appeal_arguments.AppealsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.backpack_gadget_arguments.BackpackGadgetsArguments;
import net.avicus.magma.api.graph.types.base.backpack_gadget_arguments.BackpackGadgetsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.credit_transaction_arguments.CreditTransactionsArguments;
import net.avicus.magma.api.graph.types.base.credit_transaction_arguments.CreditTransactionsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.death_arguments.DeathsArguments;
import net.avicus.magma.api.graph.types.base.death_arguments.DeathsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.experience_transaction_arguments.ExperienceTransactionsArguments;
import net.avicus.magma.api.graph.types.base.experience_transaction_arguments.ExperienceTransactionsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.friend_arguments.FriendsArguments;
import net.avicus.magma.api.graph.types.base.friend_arguments.FriendsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.livestream_arguments.LivestreamsArguments;
import net.avicus.magma.api.graph.types.base.livestream_arguments.LivestreamsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.map_rating_arguments.MapRatingsArguments;
import net.avicus.magma.api.graph.types.base.map_rating_arguments.MapRatingsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.membership_arguments.MembershipsArguments;
import net.avicus.magma.api.graph.types.base.membership_arguments.MembershipsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.objective_arguments.ObjectivesArguments;
import net.avicus.magma.api.graph.types.base.objective_arguments.ObjectivesArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.objective_type_arguments.ObjectiveTypesArguments;
import net.avicus.magma.api.graph.types.base.objective_type_arguments.ObjectiveTypesArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.present_arguments.PresentsArguments;
import net.avicus.magma.api.graph.types.base.present_arguments.PresentsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.present_finder_arguments.PresentFindersArguments;
import net.avicus.magma.api.graph.types.base.present_finder_arguments.PresentFindersArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.prestige_level_arguments.PrestigeLevelsArguments;
import net.avicus.magma.api.graph.types.base.prestige_level_arguments.PrestigeLevelsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.prestige_season_arguments.PrestigeSeasonsArguments;
import net.avicus.magma.api.graph.types.base.prestige_season_arguments.PrestigeSeasonsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.punishment_arguments.PunishmentsArguments;
import net.avicus.magma.api.graph.types.base.punishment_arguments.PunishmentsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.rank_arguments.RanksArguments;
import net.avicus.magma.api.graph.types.base.rank_arguments.RanksArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.registration_arguments.RegistrationsArguments;
import net.avicus.magma.api.graph.types.base.registration_arguments.RegistrationsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.report_arguments.ReportsArguments;
import net.avicus.magma.api.graph.types.base.report_arguments.ReportsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.reserved_slot_arguments.ReservedSlotsArguments;
import net.avicus.magma.api.graph.types.base.reserved_slot_arguments.ReservedSlotsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.server_arguments.ServersArguments;
import net.avicus.magma.api.graph.types.base.server_arguments.ServersArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.server_booster_arguments.ServerBoostersArguments;
import net.avicus.magma.api.graph.types.base.server_booster_arguments.ServerBoostersArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.server_category_arguments.ServerCategoriesArguments;
import net.avicus.magma.api.graph.types.base.server_category_arguments.ServerCategoriesArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.server_group_arguments.ServerGroupsArguments;
import net.avicus.magma.api.graph.types.base.server_group_arguments.ServerGroupsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.session_arguments.SessionsArguments;
import net.avicus.magma.api.graph.types.base.session_arguments.SessionsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.setting_arguments.SettingsArguments;
import net.avicus.magma.api.graph.types.base.setting_arguments.SettingsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.team_arguments.TeamsArguments;
import net.avicus.magma.api.graph.types.base.team_arguments.TeamsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.team_member_arguments.TeamMembersArguments;
import net.avicus.magma.api.graph.types.base.team_member_arguments.TeamMembersArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.tournament_arguments.TournamentsArguments;
import net.avicus.magma.api.graph.types.base.tournament_arguments.TournamentsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.user_arguments.UsersArguments;
import net.avicus.magma.api.graph.types.base.user_arguments.UsersArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.user_detail_arguments.UserDetailsArguments;
import net.avicus.magma.api.graph.types.base.user_detail_arguments.UserDetailsArgumentsDefinition;
import net.avicus.magma.api.graph.types.base.username_arguments.UsernamesArguments;
import net.avicus.magma.api.graph.types.base.username_arguments.UsernamesArgumentsDefinition;
import net.avicus.magma.api.graph.types.credit_transaction.CreditTransactionQuery;
import net.avicus.magma.api.graph.types.credit_transaction.CreditTransactionQueryDefinition;
import net.avicus.magma.api.graph.types.death.DeathQuery;
import net.avicus.magma.api.graph.types.death.DeathQueryDefinition;
import net.avicus.magma.api.graph.types.experience_transaction.ExperienceTransactionQuery;
import net.avicus.magma.api.graph.types.experience_transaction.ExperienceTransactionQueryDefinition;
import net.avicus.magma.api.graph.types.friend.FriendQuery;
import net.avicus.magma.api.graph.types.friend.FriendQueryDefinition;
import net.avicus.magma.api.graph.types.livestream.LivestreamQuery;
import net.avicus.magma.api.graph.types.livestream.LivestreamQueryDefinition;
import net.avicus.magma.api.graph.types.map_rating.MapRatingQuery;
import net.avicus.magma.api.graph.types.map_rating.MapRatingQueryDefinition;
import net.avicus.magma.api.graph.types.membership.MembershipQuery;
import net.avicus.magma.api.graph.types.membership.MembershipQueryDefinition;
import net.avicus.magma.api.graph.types.objective.ObjectiveQuery;
import net.avicus.magma.api.graph.types.objective.ObjectiveQueryDefinition;
import net.avicus.magma.api.graph.types.objective_type.ObjectiveTypeQuery;
import net.avicus.magma.api.graph.types.objective_type.ObjectiveTypeQueryDefinition;
import net.avicus.magma.api.graph.types.present.PresentQuery;
import net.avicus.magma.api.graph.types.present.PresentQueryDefinition;
import net.avicus.magma.api.graph.types.present_finder.PresentFinderQuery;
import net.avicus.magma.api.graph.types.present_finder.PresentFinderQueryDefinition;
import net.avicus.magma.api.graph.types.prestige_level.PrestigeLevelQuery;
import net.avicus.magma.api.graph.types.prestige_level.PrestigeLevelQueryDefinition;
import net.avicus.magma.api.graph.types.prestige_season.PrestigeSeasonQuery;
import net.avicus.magma.api.graph.types.prestige_season.PrestigeSeasonQueryDefinition;
import net.avicus.magma.api.graph.types.punishment.PunishmentQuery;
import net.avicus.magma.api.graph.types.punishment.PunishmentQueryDefinition;
import net.avicus.magma.api.graph.types.rank.RankQuery;
import net.avicus.magma.api.graph.types.rank.RankQueryDefinition;
import net.avicus.magma.api.graph.types.registration.RegistrationQuery;
import net.avicus.magma.api.graph.types.registration.RegistrationQueryDefinition;
import net.avicus.magma.api.graph.types.report.ReportQuery;
import net.avicus.magma.api.graph.types.report.ReportQueryDefinition;
import net.avicus.magma.api.graph.types.reserved_slot.ReservedSlotQuery;
import net.avicus.magma.api.graph.types.reserved_slot.ReservedSlotQueryDefinition;
import net.avicus.magma.api.graph.types.server.ServerQuery;
import net.avicus.magma.api.graph.types.server.ServerQueryDefinition;
import net.avicus.magma.api.graph.types.server_booster.ServerBoosterQuery;
import net.avicus.magma.api.graph.types.server_booster.ServerBoosterQueryDefinition;
import net.avicus.magma.api.graph.types.server_category.ServerCategoryQuery;
import net.avicus.magma.api.graph.types.server_category.ServerCategoryQueryDefinition;
import net.avicus.magma.api.graph.types.server_group.ServerGroupQuery;
import net.avicus.magma.api.graph.types.server_group.ServerGroupQueryDefinition;
import net.avicus.magma.api.graph.types.session.SessionQuery;
import net.avicus.magma.api.graph.types.session.SessionQueryDefinition;
import net.avicus.magma.api.graph.types.setting.SettingQuery;
import net.avicus.magma.api.graph.types.setting.SettingQueryDefinition;
import net.avicus.magma.api.graph.types.team.TeamQuery;
import net.avicus.magma.api.graph.types.team.TeamQueryDefinition;
import net.avicus.magma.api.graph.types.team_member.TeamMemberQuery;
import net.avicus.magma.api.graph.types.team_member.TeamMemberQueryDefinition;
import net.avicus.magma.api.graph.types.tournament.TournamentQuery;
import net.avicus.magma.api.graph.types.tournament.TournamentQueryDefinition;
import net.avicus.magma.api.graph.types.user.UserQuery;
import net.avicus.magma.api.graph.types.user.UserQueryDefinition;
import net.avicus.magma.api.graph.types.user_detail.UserDetailQuery;
import net.avicus.magma.api.graph.types.user_detail.UserDetailQueryDefinition;
import net.avicus.magma.api.graph.types.username.UsernameQuery;
import net.avicus.magma.api.graph.types.username.UsernameQueryDefinition;

public class BaseQueryQuery extends Query<BaseQueryQuery> {

  public BaseQueryQuery(StringBuilder builder) {
    super(builder);
  }

  private StringBuilder builder() {
    return _queryBuilder;
  }

  /**
   * Search for all AchievementPursuits
   */
  public BaseQueryQuery achievementPursuits(AchievementPursuitQueryDefinition queryDef) {
    return achievementPursuits(args -> {
    }, queryDef);
  }

  /**
   * Search for all AchievementPursuits
   */
  public BaseQueryQuery achievementPursuits(AchievementPursuitsArgumentsDefinition argsDef,
      AchievementPursuitQueryDefinition queryDef) {
    startField("achievementPursuits");

    AchievementPursuitsArguments args = new AchievementPursuitsArguments(builder());
    argsDef.define(args);
    AchievementPursuitsArguments.end(args);

    builder().append('{');
    queryDef.define(new AchievementPursuitQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all AchievementReceivers
   */
  public BaseQueryQuery achievementReceivers(AchievementReceiverQueryDefinition queryDef) {
    return achievementReceivers(args -> {
    }, queryDef);
  }

  /**
   * Search for all AchievementReceivers
   */
  public BaseQueryQuery achievementReceivers(AchievementReceiversArgumentsDefinition argsDef,
      AchievementReceiverQueryDefinition queryDef) {
    startField("achievementReceivers");

    AchievementReceiversArguments args = new AchievementReceiversArguments(builder());
    argsDef.define(args);
    AchievementReceiversArguments.end(args);

    builder().append('{');
    queryDef.define(new AchievementReceiverQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Achievements
   */
  public BaseQueryQuery achievements(AchievementQueryDefinition queryDef) {
    return achievements(args -> {
    }, queryDef);
  }

  /**
   * Search for all Achievements
   */
  public BaseQueryQuery achievements(AchievementsArgumentsDefinition argsDef,
      AchievementQueryDefinition queryDef) {
    startField("achievements");

    AchievementsArguments args = new AchievementsArguments(builder());
    argsDef.define(args);
    AchievementsArguments.end(args);

    builder().append('{');
    queryDef.define(new AchievementQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Alerts
   */
  public BaseQueryQuery alerts(AlertQueryDefinition queryDef) {
    return alerts(args -> {
    }, queryDef);
  }

  /**
   * Search for all Alerts
   */
  public BaseQueryQuery alerts(AlertsArgumentsDefinition argsDef, AlertQueryDefinition queryDef) {
    startField("alerts");

    AlertsArguments args = new AlertsArguments(builder());
    argsDef.define(args);
    AlertsArguments.end(args);

    builder().append('{');
    queryDef.define(new AlertQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Find all created after the given date.
   */
  public BaseQueryQuery allAfterAlert(AlertQueryDefinition queryDef) {
    return allAfterAlert(args -> {
    }, queryDef);
  }

  /**
   * Find all created after the given date.
   */
  public BaseQueryQuery allAfterAlert(AllAfterAlertArgumentsDefinition argsDef,
      AlertQueryDefinition queryDef) {
    startField("allAfterAlert");

    AllAfterAlertArguments args = new AllAfterAlertArguments(builder());
    argsDef.define(args);
    AllAfterAlertArguments.end(args);

    builder().append('{');
    queryDef.define(new AlertQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Find all channels which are currently live.
   */
  public BaseQueryQuery allLiveLivestreams(LivestreamQueryDefinition queryDef) {
    startField("allLiveLivestreams");

    builder().append('{');
    queryDef.define(new LivestreamQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Announcements
   */
  public BaseQueryQuery announcements(AnnouncementQueryDefinition queryDef) {
    return announcements(args -> {
    }, queryDef);
  }

  /**
   * Search for all Announcements
   */
  public BaseQueryQuery announcements(AnnouncementsArgumentsDefinition argsDef,
      AnnouncementQueryDefinition queryDef) {
    startField("announcements");

    AnnouncementsArguments args = new AnnouncementsArguments(builder());
    argsDef.define(args);
    AnnouncementsArguments.end(args);

    builder().append('{');
    queryDef.define(new AnnouncementQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Appeals
   */
  public BaseQueryQuery appeals(AppealQueryDefinition queryDef) {
    return appeals(args -> {
    }, queryDef);
  }

  /**
   * Search for all Appeals
   */
  public BaseQueryQuery appeals(AppealsArgumentsDefinition argsDef,
      AppealQueryDefinition queryDef) {
    startField("appeals");

    AppealsArguments args = new AppealsArguments(builder());
    argsDef.define(args);
    AppealsArguments.end(args);

    builder().append('{');
    queryDef.define(new AppealQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all BackpackGadgets
   */
  public BaseQueryQuery backpackGadgets(BackpackGadgetQueryDefinition queryDef) {
    return backpackGadgets(args -> {
    }, queryDef);
  }

  /**
   * Search for all BackpackGadgets
   */
  public BaseQueryQuery backpackGadgets(BackpackGadgetsArgumentsDefinition argsDef,
      BackpackGadgetQueryDefinition queryDef) {
    startField("backpackGadgets");

    BackpackGadgetsArguments args = new BackpackGadgetsArguments(builder());
    argsDef.define(args);
    BackpackGadgetsArguments.end(args);

    builder().append('{');
    queryDef.define(new BackpackGadgetQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all CreditTransactions
   */
  public BaseQueryQuery creditTransactions(CreditTransactionQueryDefinition queryDef) {
    return creditTransactions(args -> {
    }, queryDef);
  }

  /**
   * Search for all CreditTransactions
   */
  public BaseQueryQuery creditTransactions(CreditTransactionsArgumentsDefinition argsDef,
      CreditTransactionQueryDefinition queryDef) {
    startField("creditTransactions");

    CreditTransactionsArguments args = new CreditTransactionsArguments(builder());
    argsDef.define(args);
    CreditTransactionsArguments.end(args);

    builder().append('{');
    queryDef.define(new CreditTransactionQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Deaths
   */
  public BaseQueryQuery deaths(DeathQueryDefinition queryDef) {
    return deaths(args -> {
    }, queryDef);
  }

  /**
   * Search for all Deaths
   */
  public BaseQueryQuery deaths(DeathsArgumentsDefinition argsDef, DeathQueryDefinition queryDef) {
    startField("deaths");

    DeathsArguments args = new DeathsArguments(builder());
    argsDef.define(args);
    DeathsArguments.end(args);

    builder().append('{');
    queryDef.define(new DeathQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all ExperienceTransactions
   */
  public BaseQueryQuery experienceTransactions(ExperienceTransactionQueryDefinition queryDef) {
    return experienceTransactions(args -> {
    }, queryDef);
  }

  /**
   * Search for all ExperienceTransactions
   */
  public BaseQueryQuery experienceTransactions(ExperienceTransactionsArgumentsDefinition argsDef,
      ExperienceTransactionQueryDefinition queryDef) {
    startField("experienceTransactions");

    ExperienceTransactionsArguments args = new ExperienceTransactionsArguments(builder());
    argsDef.define(args);
    ExperienceTransactionsArguments.end(args);

    builder().append('{');
    queryDef.define(new ExperienceTransactionQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Friends
   */
  public BaseQueryQuery friends(FriendQueryDefinition queryDef) {
    return friends(args -> {
    }, queryDef);
  }

  /**
   * Search for all Friends
   */
  public BaseQueryQuery friends(FriendsArgumentsDefinition argsDef,
      FriendQueryDefinition queryDef) {
    startField("friends");

    FriendsArguments args = new FriendsArguments(builder());
    argsDef.define(args);
    FriendsArguments.end(args);

    builder().append('{');
    queryDef.define(new FriendQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Livestreams
   */
  public BaseQueryQuery livestreams(LivestreamQueryDefinition queryDef) {
    return livestreams(args -> {
    }, queryDef);
  }

  /**
   * Search for all Livestreams
   */
  public BaseQueryQuery livestreams(LivestreamsArgumentsDefinition argsDef,
      LivestreamQueryDefinition queryDef) {
    startField("livestreams");

    LivestreamsArguments args = new LivestreamsArguments(builder());
    argsDef.define(args);
    LivestreamsArguments.end(args);

    builder().append('{');
    queryDef.define(new LivestreamQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all MapRatings
   */
  public BaseQueryQuery mapRatings(MapRatingQueryDefinition queryDef) {
    return mapRatings(args -> {
    }, queryDef);
  }

  /**
   * Search for all MapRatings
   */
  public BaseQueryQuery mapRatings(MapRatingsArgumentsDefinition argsDef,
      MapRatingQueryDefinition queryDef) {
    startField("mapRatings");

    MapRatingsArguments args = new MapRatingsArguments(builder());
    argsDef.define(args);
    MapRatingsArguments.end(args);

    builder().append('{');
    queryDef.define(new MapRatingQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Memberships
   */
  public BaseQueryQuery memberships(MembershipQueryDefinition queryDef) {
    return memberships(args -> {
    }, queryDef);
  }

  /**
   * Search for all Memberships
   */
  public BaseQueryQuery memberships(MembershipsArgumentsDefinition argsDef,
      MembershipQueryDefinition queryDef) {
    startField("memberships");

    MembershipsArguments args = new MembershipsArguments(builder());
    argsDef.define(args);
    MembershipsArguments.end(args);

    builder().append('{');
    queryDef.define(new MembershipQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all ObjectiveTypes
   */
  public BaseQueryQuery objectiveTypes(ObjectiveTypeQueryDefinition queryDef) {
    return objectiveTypes(args -> {
    }, queryDef);
  }

  /**
   * Search for all ObjectiveTypes
   */
  public BaseQueryQuery objectiveTypes(ObjectiveTypesArgumentsDefinition argsDef,
      ObjectiveTypeQueryDefinition queryDef) {
    startField("objectiveTypes");

    ObjectiveTypesArguments args = new ObjectiveTypesArguments(builder());
    argsDef.define(args);
    ObjectiveTypesArguments.end(args);

    builder().append('{');
    queryDef.define(new ObjectiveTypeQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Objectives
   */
  public BaseQueryQuery objectives(ObjectiveQueryDefinition queryDef) {
    return objectives(args -> {
    }, queryDef);
  }

  /**
   * Search for all Objectives
   */
  public BaseQueryQuery objectives(ObjectivesArgumentsDefinition argsDef,
      ObjectiveQueryDefinition queryDef) {
    startField("objectives");

    ObjectivesArguments args = new ObjectivesArguments(builder());
    argsDef.define(args);
    ObjectivesArguments.end(args);

    builder().append('{');
    queryDef.define(new ObjectiveQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all PresentFinders
   */
  public BaseQueryQuery presentFinders(PresentFinderQueryDefinition queryDef) {
    return presentFinders(args -> {
    }, queryDef);
  }

  /**
   * Search for all PresentFinders
   */
  public BaseQueryQuery presentFinders(PresentFindersArgumentsDefinition argsDef,
      PresentFinderQueryDefinition queryDef) {
    startField("presentFinders");

    PresentFindersArguments args = new PresentFindersArguments(builder());
    argsDef.define(args);
    PresentFindersArguments.end(args);

    builder().append('{');
    queryDef.define(new PresentFinderQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Presents
   */
  public BaseQueryQuery presents(PresentQueryDefinition queryDef) {
    return presents(args -> {
    }, queryDef);
  }

  /**
   * Search for all Presents
   */
  public BaseQueryQuery presents(PresentsArgumentsDefinition argsDef,
      PresentQueryDefinition queryDef) {
    startField("presents");

    PresentsArguments args = new PresentsArguments(builder());
    argsDef.define(args);
    PresentsArguments.end(args);

    builder().append('{');
    queryDef.define(new PresentQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all PrestigeLevels
   */
  public BaseQueryQuery prestigeLevels(PrestigeLevelQueryDefinition queryDef) {
    return prestigeLevels(args -> {
    }, queryDef);
  }

  /**
   * Search for all PrestigeLevels
   */
  public BaseQueryQuery prestigeLevels(PrestigeLevelsArgumentsDefinition argsDef,
      PrestigeLevelQueryDefinition queryDef) {
    startField("prestigeLevels");

    PrestigeLevelsArguments args = new PrestigeLevelsArguments(builder());
    argsDef.define(args);
    PrestigeLevelsArguments.end(args);

    builder().append('{');
    queryDef.define(new PrestigeLevelQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all PrestigeSeasons
   */
  public BaseQueryQuery prestigeSeasons(PrestigeSeasonQueryDefinition queryDef) {
    return prestigeSeasons(args -> {
    }, queryDef);
  }

  /**
   * Search for all PrestigeSeasons
   */
  public BaseQueryQuery prestigeSeasons(PrestigeSeasonsArgumentsDefinition argsDef,
      PrestigeSeasonQueryDefinition queryDef) {
    startField("prestigeSeasons");

    PrestigeSeasonsArguments args = new PrestigeSeasonsArguments(builder());
    argsDef.define(args);
    PrestigeSeasonsArguments.end(args);

    builder().append('{');
    queryDef.define(new PrestigeSeasonQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Punishments
   */
  public BaseQueryQuery punishments(PunishmentQueryDefinition queryDef) {
    return punishments(args -> {
    }, queryDef);
  }

  /**
   * Search for all Punishments
   */
  public BaseQueryQuery punishments(PunishmentsArgumentsDefinition argsDef,
      PunishmentQueryDefinition queryDef) {
    startField("punishments");

    PunishmentsArguments args = new PunishmentsArguments(builder());
    argsDef.define(args);
    PunishmentsArguments.end(args);

    builder().append('{');
    queryDef.define(new PunishmentQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Ranks
   */
  public BaseQueryQuery ranks(RankQueryDefinition queryDef) {
    return ranks(args -> {
    }, queryDef);
  }

  /**
   * Search for all Ranks
   */
  public BaseQueryQuery ranks(RanksArgumentsDefinition argsDef, RankQueryDefinition queryDef) {
    startField("ranks");

    RanksArguments args = new RanksArguments(builder());
    argsDef.define(args);
    RanksArguments.end(args);

    builder().append('{');
    queryDef.define(new RankQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Registrations
   */
  public BaseQueryQuery registrations(RegistrationQueryDefinition queryDef) {
    return registrations(args -> {
    }, queryDef);
  }

  /**
   * Search for all Registrations
   */
  public BaseQueryQuery registrations(RegistrationsArgumentsDefinition argsDef,
      RegistrationQueryDefinition queryDef) {
    startField("registrations");

    RegistrationsArguments args = new RegistrationsArguments(builder());
    argsDef.define(args);
    RegistrationsArguments.end(args);

    builder().append('{');
    queryDef.define(new RegistrationQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Reports
   */
  public BaseQueryQuery reports(ReportQueryDefinition queryDef) {
    return reports(args -> {
    }, queryDef);
  }

  /**
   * Search for all Reports
   */
  public BaseQueryQuery reports(ReportsArgumentsDefinition argsDef,
      ReportQueryDefinition queryDef) {
    startField("reports");

    ReportsArguments args = new ReportsArguments(builder());
    argsDef.define(args);
    ReportsArguments.end(args);

    builder().append('{');
    queryDef.define(new ReportQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all ReservedSlots
   */
  public BaseQueryQuery reservedSlots(ReservedSlotQueryDefinition queryDef) {
    return reservedSlots(args -> {
    }, queryDef);
  }

  /**
   * Search for all ReservedSlots
   */
  public BaseQueryQuery reservedSlots(ReservedSlotsArgumentsDefinition argsDef,
      ReservedSlotQueryDefinition queryDef) {
    startField("reservedSlots");

    ReservedSlotsArguments args = new ReservedSlotsArguments(builder());
    argsDef.define(args);
    ReservedSlotsArguments.end(args);

    builder().append('{');
    queryDef.define(new ReservedSlotQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all ServerBoosters
   */
  public BaseQueryQuery serverBoosters(ServerBoosterQueryDefinition queryDef) {
    return serverBoosters(args -> {
    }, queryDef);
  }

  /**
   * Search for all ServerBoosters
   */
  public BaseQueryQuery serverBoosters(ServerBoostersArgumentsDefinition argsDef,
      ServerBoosterQueryDefinition queryDef) {
    startField("serverBoosters");

    ServerBoostersArguments args = new ServerBoostersArguments(builder());
    argsDef.define(args);
    ServerBoostersArguments.end(args);

    builder().append('{');
    queryDef.define(new ServerBoosterQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all ServerCategories
   */
  public BaseQueryQuery serverCategories(ServerCategoryQueryDefinition queryDef) {
    return serverCategories(args -> {
    }, queryDef);
  }

  /**
   * Search for all ServerCategories
   */
  public BaseQueryQuery serverCategories(ServerCategoriesArgumentsDefinition argsDef,
      ServerCategoryQueryDefinition queryDef) {
    startField("serverCategories");

    ServerCategoriesArguments args = new ServerCategoriesArguments(builder());
    argsDef.define(args);
    ServerCategoriesArguments.end(args);

    builder().append('{');
    queryDef.define(new ServerCategoryQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all ServerGroups
   */
  public BaseQueryQuery serverGroups(ServerGroupQueryDefinition queryDef) {
    return serverGroups(args -> {
    }, queryDef);
  }

  /**
   * Search for all ServerGroups
   */
  public BaseQueryQuery serverGroups(ServerGroupsArgumentsDefinition argsDef,
      ServerGroupQueryDefinition queryDef) {
    startField("serverGroups");

    ServerGroupsArguments args = new ServerGroupsArguments(builder());
    argsDef.define(args);
    ServerGroupsArguments.end(args);

    builder().append('{');
    queryDef.define(new ServerGroupQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Servers
   */
  public BaseQueryQuery servers(ServerQueryDefinition queryDef) {
    return servers(args -> {
    }, queryDef);
  }

  /**
   * Search for all Servers
   */
  public BaseQueryQuery servers(ServersArgumentsDefinition argsDef,
      ServerQueryDefinition queryDef) {
    startField("servers");

    ServersArguments args = new ServersArguments(builder());
    argsDef.define(args);
    ServersArguments.end(args);

    builder().append('{');
    queryDef.define(new ServerQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Sessions
   */
  public BaseQueryQuery sessions(SessionQueryDefinition queryDef) {
    return sessions(args -> {
    }, queryDef);
  }

  /**
   * Search for all Sessions
   */
  public BaseQueryQuery sessions(SessionsArgumentsDefinition argsDef,
      SessionQueryDefinition queryDef) {
    startField("sessions");

    SessionsArguments args = new SessionsArguments(builder());
    argsDef.define(args);
    SessionsArguments.end(args);

    builder().append('{');
    queryDef.define(new SessionQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Settings
   */
  public BaseQueryQuery settings(SettingQueryDefinition queryDef) {
    return settings(args -> {
    }, queryDef);
  }

  /**
   * Search for all Settings
   */
  public BaseQueryQuery settings(SettingsArgumentsDefinition argsDef,
      SettingQueryDefinition queryDef) {
    startField("settings");

    SettingsArguments args = new SettingsArguments(builder());
    argsDef.define(args);
    SettingsArguments.end(args);

    builder().append('{');
    queryDef.define(new SettingQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all TeamMembers
   */
  public BaseQueryQuery teamMembers(TeamMemberQueryDefinition queryDef) {
    return teamMembers(args -> {
    }, queryDef);
  }

  /**
   * Search for all TeamMembers
   */
  public BaseQueryQuery teamMembers(TeamMembersArgumentsDefinition argsDef,
      TeamMemberQueryDefinition queryDef) {
    startField("teamMembers");

    TeamMembersArguments args = new TeamMembersArguments(builder());
    argsDef.define(args);
    TeamMembersArguments.end(args);

    builder().append('{');
    queryDef.define(new TeamMemberQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Teams
   */
  public BaseQueryQuery teams(TeamQueryDefinition queryDef) {
    return teams(args -> {
    }, queryDef);
  }

  /**
   * Search for all Teams
   */
  public BaseQueryQuery teams(TeamsArgumentsDefinition argsDef, TeamQueryDefinition queryDef) {
    startField("teams");

    TeamsArguments args = new TeamsArguments(builder());
    argsDef.define(args);
    TeamsArguments.end(args);

    builder().append('{');
    queryDef.define(new TeamQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Tournaments
   */
  public BaseQueryQuery tournaments(TournamentQueryDefinition queryDef) {
    return tournaments(args -> {
    }, queryDef);
  }

  /**
   * Search for all Tournaments
   */
  public BaseQueryQuery tournaments(TournamentsArgumentsDefinition argsDef,
      TournamentQueryDefinition queryDef) {
    startField("tournaments");

    TournamentsArguments args = new TournamentsArguments(builder());
    argsDef.define(args);
    TournamentsArguments.end(args);

    builder().append('{');
    queryDef.define(new TournamentQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all UserDetails
   */
  public BaseQueryQuery userDetails(UserDetailQueryDefinition queryDef) {
    return userDetails(args -> {
    }, queryDef);
  }

  /**
   * Search for all UserDetails
   */
  public BaseQueryQuery userDetails(UserDetailsArgumentsDefinition argsDef,
      UserDetailQueryDefinition queryDef) {
    startField("userDetails");

    UserDetailsArguments args = new UserDetailsArguments(builder());
    argsDef.define(args);
    UserDetailsArguments.end(args);

    builder().append('{');
    queryDef.define(new UserDetailQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Usernames
   */
  public BaseQueryQuery usernames(UsernameQueryDefinition queryDef) {
    return usernames(args -> {
    }, queryDef);
  }

  /**
   * Search for all Usernames
   */
  public BaseQueryQuery usernames(UsernamesArgumentsDefinition argsDef,
      UsernameQueryDefinition queryDef) {
    startField("usernames");

    UsernamesArguments args = new UsernamesArguments(builder());
    argsDef.define(args);
    UsernamesArguments.end(args);

    builder().append('{');
    queryDef.define(new UsernameQuery(builder()));
    builder().append('}');

    return this;
  }

  /**
   * Search for all Users
   */
  public BaseQueryQuery users(UserQueryDefinition queryDef) {
    return users(args -> {
    }, queryDef);
  }

  /**
   * Search for all Users
   */
  public BaseQueryQuery users(UsersArgumentsDefinition argsDef, UserQueryDefinition queryDef) {
    startField("users");

    UsersArguments args = new UsersArguments(builder());
    argsDef.define(args);
    UsersArguments.end(args);

    builder().append('{');
    queryDef.define(new UserQuery(builder()));
    builder().append('}');

    return this;
  }
}
