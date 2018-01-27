package net.avicus.magma.database;

import lombok.Getter;
import net.avicus.magma.Enableable;
import net.avicus.magma.database.model.impl.Achievement;
import net.avicus.magma.database.model.impl.AchievementPursuit;
import net.avicus.magma.database.model.impl.AchievementReceiver;
import net.avicus.magma.database.model.impl.Announcement;
import net.avicus.magma.database.model.impl.BackpackGadget;
import net.avicus.magma.database.model.impl.CreditTransaction;
import net.avicus.magma.database.model.impl.Death;
import net.avicus.magma.database.model.impl.Discussion;
import net.avicus.magma.database.model.impl.ExperienceLeaderboardEntry;
import net.avicus.magma.database.model.impl.ExperienceTransaction;
import net.avicus.magma.database.model.impl.Friend;
import net.avicus.magma.database.model.impl.IPBan;
import net.avicus.magma.database.model.impl.LeaderboardEntry;
import net.avicus.magma.database.model.impl.MapRating;
import net.avicus.magma.database.model.impl.ObjectiveCompletion;
import net.avicus.magma.database.model.impl.ObjectiveType;
import net.avicus.magma.database.model.impl.PrestigeLevel;
import net.avicus.magma.database.model.impl.PrestigeSeason;
import net.avicus.magma.database.model.impl.Punishment;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.Report;
import net.avicus.magma.database.model.impl.ReservedSlot;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.model.impl.ServerGroup;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.magma.database.model.impl.Setting;
import net.avicus.magma.database.model.impl.Team;
import net.avicus.magma.database.model.impl.TeamMember;
import net.avicus.magma.database.model.impl.TeamSpeakUser;
import net.avicus.magma.database.model.impl.Tournament;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.database.model.impl.UserDetail;
import net.avicus.magma.database.model.impl.Username;
import net.avicus.magma.database.model.impl.Vote;
import net.avicus.magma.database.table.impl.AchievementPursuitTable;
import net.avicus.magma.database.table.impl.AchievementReceiverTable;
import net.avicus.magma.database.table.impl.AchievementTable;
import net.avicus.magma.database.table.impl.AnnouncementTable;
import net.avicus.magma.database.table.impl.BackpackGadgetTable;
import net.avicus.magma.database.table.impl.CreditTransactionTable;
import net.avicus.magma.database.table.impl.DeathTable;
import net.avicus.magma.database.table.impl.DiscussionTable;
import net.avicus.magma.database.table.impl.ExperienceLeaderboardTable;
import net.avicus.magma.database.table.impl.ExperienceTransactionTable;
import net.avicus.magma.database.table.impl.FriendTable;
import net.avicus.magma.database.table.impl.IPBanTable;
import net.avicus.magma.database.table.impl.LeaderboardTable;
import net.avicus.magma.database.table.impl.MapRatingTable;
import net.avicus.magma.database.table.impl.ObjectiveTable;
import net.avicus.magma.database.table.impl.ObjectiveTypeTable;
import net.avicus.magma.database.table.impl.PrestigeLevelTable;
import net.avicus.magma.database.table.impl.PrestigeSeasonTable;
import net.avicus.magma.database.table.impl.PunishmentTable;
import net.avicus.magma.database.table.impl.RankMemberTable;
import net.avicus.magma.database.table.impl.RankTable;
import net.avicus.magma.database.table.impl.ReportTable;
import net.avicus.magma.database.table.impl.ReservedSlotTable;
import net.avicus.magma.database.table.impl.ServerCategoryTable;
import net.avicus.magma.database.table.impl.ServerGroupTable;
import net.avicus.magma.database.table.impl.ServerTable;
import net.avicus.magma.database.table.impl.SessionTable;
import net.avicus.magma.database.table.impl.SettingTable;
import net.avicus.magma.database.table.impl.TeamMemberTable;
import net.avicus.magma.database.table.impl.TeamSpeakUserTable;
import net.avicus.magma.database.table.impl.TeamTable;
import net.avicus.magma.database.table.impl.TournamentTable;
import net.avicus.magma.database.table.impl.UserDetailTable;
import net.avicus.magma.database.table.impl.UserTable;
import net.avicus.magma.database.table.impl.UsernameTable;
import net.avicus.magma.database.table.impl.VoteTable;
import net.avicus.quest.database.DatabaseConfig;

public class Database extends net.avicus.quest.database.Database implements Enableable {

  @Getter
  private final UserTable users;
  @Getter
  private final UserDetailTable userDetails;
  @Getter
  private final UsernameTable usernames;
  @Getter
  private final PunishmentTable punishments;
  @Getter
  private final ServerTable servers;
  @Getter
  private final ServerGroupTable serverGroups;
  @Getter
  private final ServerCategoryTable serverCategories;
  @Getter
  private final SessionTable sessions;
  @Getter
  private final FriendTable friends;
  @Getter
  private final RankTable ranks;
  @Getter
  private final RankMemberTable rankMembers;
  @Getter
  private final ReportTable reports;
  @Getter
  private final LeaderboardTable leaderboard;
  @Getter
  private final ExperienceLeaderboardTable xpLeaderBoard;
  @Getter
  private final DeathTable deaths;
  @Getter
  private final ObjectiveTable objectiveCompletions;
  @Getter
  private final ObjectiveTypeTable objectiveTypes;
  @Getter
  private final TeamTable teams;
  @Getter
  private final TeamMemberTable teamMembers;
  @Getter
  private final ReservedSlotTable reservedSlots;
  @Getter
  private final BackpackGadgetTable backpackGadgets;
  @Getter
  private final CreditTransactionTable creditTransactions;
  @Getter
  private final AnnouncementTable announcements;
  @Getter
  private final SettingTable settings;
  @Getter
  private final TeamSpeakUserTable teamSpeakUsers;
  @Getter
  private final TournamentTable tournaments;
  @Getter
  private final IPBanTable ipBans;
  @Getter
  private final MapRatingTable mapRatings;
  @Getter
  private final PrestigeLevelTable prestigeLevels;
  @Getter
  private final PrestigeSeasonTable seasons;
  @Getter
  private final ExperienceTransactionTable xpTransations;
  @Getter
  private final DiscussionTable discussions;
  @Getter
  private final AchievementTable achievements;
  @Getter
  private final AchievementReceiverTable receivers;
  @Getter
  private final AchievementPursuitTable pursuits;
  @Getter
  private final VoteTable votes;

  public Database(DatabaseConfig config) {
    super(config);

    this.users = new UserTable(this, "users", User.class);
    this.userDetails = new UserDetailTable(this, "user_details", UserDetail.class);
    this.usernames = new UsernameTable(this, "usernames", Username.class);
    this.punishments = new PunishmentTable(this, "punishments", Punishment.class);
    this.servers = new ServerTable(this, "servers", Server.class);
    this.serverGroups = new ServerGroupTable(this, "server_groups", ServerGroup.class);
    this.serverCategories = new ServerCategoryTable(this, "server_categories",
        ServerCategory.class);
    this.sessions = new SessionTable(this, "sessions", Session.class);
    this.friends = new FriendTable(this, "friends", Friend.class);
    this.ranks = new RankTable(this, "ranks", Rank.class);
    this.rankMembers = new RankMemberTable(this, "memberships", RankMember.class);
    this.reports = new ReportTable(this, "reports", Report.class);
    this.leaderboard = new LeaderboardTable(this, "leaderboard_entries", LeaderboardEntry.class);
    this.xpLeaderBoard = new ExperienceLeaderboardTable(this, "experience_leaderboard_entries",
        ExperienceLeaderboardEntry.class);
    this.deaths = new DeathTable(this, "deaths", Death.class);
    this.objectiveCompletions = new ObjectiveTable(this, "objectives", ObjectiveCompletion.class);
    this.objectiveTypes = new ObjectiveTypeTable(this, "objective_types", ObjectiveType.class);
    this.teams = new TeamTable(this, "teams", Team.class);
    this.teamMembers = new TeamMemberTable(this, "team_members", TeamMember.class);
    this.reservedSlots = new ReservedSlotTable(this, "reserved_slots", ReservedSlot.class);
    this.backpackGadgets = new BackpackGadgetTable(this, "backpack_gadgets", BackpackGadget.class);
    this.creditTransactions = new CreditTransactionTable(this, "credits", CreditTransaction.class);
    this.announcements = new AnnouncementTable(this, "announcements", Announcement.class);
    this.settings = new SettingTable(this, "settings", Setting.class);
    this.teamSpeakUsers = new TeamSpeakUserTable(this, "teamspeak_users", TeamSpeakUser.class);
    this.tournaments = new TournamentTable(this, "tournaments", Tournament.class);
    this.ipBans = new IPBanTable(this, "ip_bans", IPBan.class);
    this.mapRatings = new MapRatingTable(this, "map_ratings", MapRating.class);
    this.seasons = new PrestigeSeasonTable(this, "prestige_seasons", PrestigeSeason.class);
    this.prestigeLevels = new PrestigeLevelTable(this, "prestige_levels", PrestigeLevel.class);
    this.xpTransations = new ExperienceTransactionTable(this, "experience_transactions",
        ExperienceTransaction.class);
    this.discussions = new DiscussionTable(this, "discussions", Discussion.class);
    this.achievements = new AchievementTable(this, "achievements", Achievement.class);
    this.receivers = new AchievementReceiverTable(this, "achievement_receivers",
        AchievementReceiver.class);
    this.pursuits = new AchievementPursuitTable(this, "achievement_pursuits",
        AchievementPursuit.class);
    this.votes = new VoteTable(this, "votes", Vote.class);
  }

  @Override
  public void enable() {
    this.connect();
  }

  @Override
  public void disable() {
    this.disconnect();
  }
}
