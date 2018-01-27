import java.util.Arrays;
import lombok.Setter;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.teams.Team;
import org.junit.Test;

public class BalanceTests {

  @Test
  public void minotaurTest() {
    FakeTeam team1 = new FakeTeam(1, 1, 1);
    FakeTeam team2 = new FakeTeam(4, 15, 15);
    FakeTeam[] all = new FakeTeam[]{team1, team2};

    assert isGroupBalanced(team1, 1, all);

    team1.setSize(1);
    team2.setSize(4);

    assert isGroupBalanced(team1, 0, all);
    assert isGroupBalanced(team2, 0, all);

    team1.setSize(1);
    team2.setSize(15);

    assert isGroupBalanced(team1, 0, all);
    assert isGroupBalanced(team2, 0, all);

    team1.setSize(1);
    team2.setSize(7);

    assert isGroupBalanced(team1, 0, all);
    assert isGroupBalanced(team2, 0, all);
  }

  @Test
  public void oneToOneTest() {
    FakeTeam team1 = new FakeTeam(2, 16, 20);
    FakeTeam team2 = new FakeTeam(2, 16, 20);
    FakeTeam[] all = new FakeTeam[]{team1, team2};

    team1.setSize(1);
    team2.setSize(1);

    assert isGroupBalanced(team1, 1, all);

    team1.setSize(2);
    team2.setSize(2);

    assert isGroupBalanced(team1, 1, all);
    team1.setSize(3);

    assert isGroupBalanced(team1, 0, all);
    assert isGroupBalanced(team2, 0, all);

    // 2v4 is not ok
    assert !isGroupBalanced(team1, 1, all);

    team1.setSize(6);
    team2.setSize(9);

    assert isGroupBalanced(team1, 0, all);
    assert isGroupBalanced(team2, 0, all);

    team1.setSize(6);
    team2.setSize(10);

    assert !isGroupBalanced(team1, 0, all);
    assert !isGroupBalanced(team2, 0, all);
  }

  private double minExpectedGroupSizeRatio(Group group, Group[] allGroups) {
    int min = group.getMinPlayers();
    int totalMin = Arrays.asList(allGroups).stream().mapToInt(Group::getMinPlayers).sum();
    return totalMin == 0 ? 0 : (double) min / (double) totalMin;
  }

  private double maxExpectedGroupSizeRatio(Group group, Group[] allGroups) {
    int max = group.getMaxOverfill();
    int totalMax = Arrays.asList(allGroups).stream().mapToInt(Group::getMaxOverfill).sum();
    return totalMax == 0 ? 0 : (double) max / (double) totalMax;
  }

  private double currentGroupSizeRatio(Group group, int additionalPlayers, Group[] allGroups) {
    int size = group.size() + additionalPlayers;
    int totalSize =
        Arrays.asList(allGroups).stream().mapToInt(Group::size).sum() + additionalPlayers;

    return totalSize == 0 ? 0 : (double) size / (double) totalSize;
  }

  public boolean isGroupBalanced(Group group, int additionalPlayers, Group... allGroups) {
    int newSize = additionalPlayers + group.size();

    if (newSize < group.getMinPlayers()) {
      return true;
    }

    boolean checkRatios = false;
    for (Group test : allGroups) {
      if (test.isSpectator()) {
        continue;
      }

      int difference = Math.abs(newSize - test.size());

      if (difference > 1) {
        checkRatios = true;
        break;
      }
    }

    if (checkRatios) {
      double minExpected = minExpectedGroupSizeRatio(group, allGroups);
      double maxExpected = maxExpectedGroupSizeRatio(group, allGroups);
      double low = Math.min(minExpected, maxExpected);
      double high = Math.max(minExpected, maxExpected);

      double current = currentGroupSizeRatio(group, additionalPlayers, allGroups);
      for (double x = low; x <= high; x += 0.06) {
        double diff = Math.abs(x - current);
        if (diff < 0.12) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  public static class FakeTeam extends Team {

    @Setter
    private int size;

    public FakeTeam(int min, int max, int maxOverfill) {
      super(null, null, null, min, max, maxOverfill);
    }

    @Override
    public int size() {
      return this.size;
    }
  }
}
