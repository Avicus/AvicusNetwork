package net.avicus.hook.wrapper;


import java.util.List;

@FunctionalInterface
public interface TeamSpeakCommand {

  void execute(HookClient sender, List<String> args) throws Exception;
}
