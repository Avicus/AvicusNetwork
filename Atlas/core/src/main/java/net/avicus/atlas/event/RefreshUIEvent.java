package net.avicus.atlas.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RefreshUIEvent extends Event {

    /**
     * Event handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * Get the handlers of the event.
     *
     * @return the handlers of the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the handlers of the event.
     *
     * @return the handlers of the event
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
