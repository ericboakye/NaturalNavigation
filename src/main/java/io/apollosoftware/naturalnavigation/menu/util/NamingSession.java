package io.apollosoftware.naturalnavigation.menu.util;

import io.apollosoftware.naturalnavigation.NaturalNavigation;

import io.apollosoftware.lib.lang.Message;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * Programmed by Tevin on 8/8/2015.
 */
public class NamingSession {

    private static final long EXPIRY_IN_MILLIS = 20000;

    public class NamingEvent {

        private String name;

        public NamingEvent(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public interface NamingEventHandler {
        void onName(NamingEvent event) throws Exception;
    }

    @Getter
    private UUID uuid;
    @Getter
    private NamingEventHandler handler;
    private long endTime;


    private Listener listener;

    public NamingSession(final Player player, final NamingEventHandler handler) {
        this.uuid = player.getUniqueId();

        Message.create("naming").sendTo(player);
        player.closeInventory();

        this.handler = handler;
        endTime += System.currentTimeMillis() + EXPIRY_IN_MILLIS;

        final BukkitTask timeoutMessage = Bukkit.getScheduler().runTaskLater(NaturalNavigation.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Message.create("namingTimeout").sendTo(player);
                destroy();
            }
        }, (EXPIRY_IN_MILLIS / 1000) * 20);

        this.listener = new Listener() {

            @EventHandler
            public void onChat(AsyncPlayerChatEvent event) throws Exception {

                if (!event.getPlayer().getUniqueId().equals(uuid)) return;

                if (isOver()) {
                    destroy();
                    return;
                }

                NamingEvent clickEvent = new NamingEvent(event.getMessage());
                handler.onName(clickEvent);
                event.setCancelled(true);
                timeoutMessage.cancel();
                destroy();
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().getUniqueId().equals(uuid)) destroy();
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, NaturalNavigation.getPlugin());
    }


    public boolean isOver() {
        return endTime < System.currentTimeMillis();
    }

    public void destroy() {
        uuid = null;
        handler = null;

        HandlerList.unregisterAll(listener);

        listener = null;
    }
}