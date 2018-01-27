package net.avicus.hook;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import net.avicus.magma.redis.RedisHandler;

public class ServerActionHandler implements RedisHandler {

  @Override
  public String[] channels() {
    return new String[]{"server-actions"};
  }

  @Override
  public void handle(JsonObject json) {
    String server = json.get("server").getAsString().toLowerCase();
    String action = json.get("action").getAsString();
    String path = "../servers/" + server;

    if (!new File(path).exists()) {
      return;
    }

    try {
      switch (action) {
        case "start":
          Main.getHook().getLog().info(String.format("Starting %s...", server));
          Process start = Runtime.getRuntime()
              .exec(String.format("screen -S %s -d -m sh run.sh", server), null, new File(path));
          outputCommand(start);
          return;
        case "stop":
          Main.getHook().getLog().info(String.format("Stopping %s...", server));
          // Complex array because of dumb java eval.
          String[] command = {"screen", "-p", "0",
              "-S", server, "-X", "eval", "stuff \"stop\"\\015"};
          Process stop = Runtime.getRuntime().exec(command, null);
          outputCommand(stop);
          return;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void outputCommand(Process process) throws Exception {
    process.waitFor();
    StringBuilder output = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line = "";
    while ((line = reader.readLine()) != null) {
      output.append(line + "\n");
    }
    System.out.println("Command Output: " + output);
  }
}
