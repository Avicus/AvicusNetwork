package net.avicus.magma;

/**
 * Along the way, I got lazy and used generic strings to represent the name and URL of the network,
 * even though it is stored in the config for some plugins, and should be localized.
 * If you ever decide to use this code, it would probably be wise to actually fix places that use
 * these references.
 * - Austin
 */
public class NetworkIdentification {

  public static String NAME = "Your Cool Network";
  public static String URL = "some.cool.site";
}
