package net.avicus.magma.channel.distributed;

import net.avicus.magma.Magma;
import net.avicus.magma.module.Module;

public class DistributedChannels implements Module {

  @Override
  public void enable() {
    Magma.get().getRedis().register(new DistributedChannelRedisMessageConsumer());
  }
}
