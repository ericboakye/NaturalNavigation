package io.apollosoftware.naturalnavigation.menu;

import io.apollosoftware.naturalnavigation.enums.TeleportType;
import io.apollosoftware.naturalnavigation.enums.TeleportType;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TeleportRequest {

    private long startTime;
    private String playerName;
    private String cooldownName;
    private long lengthInMillis;
    private long endTime;

    @Getter
    private TeleportType teleportType;

    private static HashMap<String, String> requests = new HashMap<String, String>();

    public static String getRequester(String name) {
        return requests.get(name);
    }

    public static String createRequest(String name, String name2) {
        return requests.put(name, name2);
    }

    public TeleportRequest(TeleportType teleportType, String cooldownName, String player, long lengthInMillis) {
        this.teleportType = teleportType;
        this.cooldownName = cooldownName;
        this.startTime = System.currentTimeMillis();
        this.playerName = player;
        this.lengthInMillis = lengthInMillis;
        this.endTime = (this.startTime + this.lengthInMillis);
    }


    public static class Cooldown {
        private static Set<TeleportRequest> cooldowns = new HashSet<>();

        public static void addCooldown(TeleportType type, String cooldownName, String player,
                                       long lengthInMillis) {
            TeleportRequest pc = new TeleportRequest(type, cooldownName, player,
                    lengthInMillis);
            Iterator it = cooldowns.iterator();

            while (it.hasNext()) {
                TeleportRequest iterated = (TeleportRequest) it.next();
                if ((iterated.getPlayerName().equalsIgnoreCase(pc.getPlayerName()))
                        && (iterated.getCooldownName().equalsIgnoreCase(pc
                        .getCooldownName()))) {
                    it.remove();
                }
            }

            cooldowns.add(pc);
        }

        public static TeleportRequest getCooldown(String cooldownName,
                                                  String playerName) {
            for (TeleportRequest pc : cooldowns) {
                if ((pc.getCooldownName().equalsIgnoreCase(cooldownName))
                        && (pc.getPlayerName().equalsIgnoreCase(playerName))) {
                    return pc;
                }
            }

            return null;
        }

        public static void delete(String cooldownName,
                                  String playerName) {
            for (TeleportRequest pc : cooldowns) {
                if ((pc.getCooldownName().equalsIgnoreCase(cooldownName))
                        && (pc.getPlayerName().equalsIgnoreCase(playerName))) {
                    cooldowns.remove(pc);
                    return;
                }
            }

        }
    }

    public boolean isOver() {
        return this.endTime < System.currentTimeMillis();
    }

    public int getTimeLeft() {
        return (int) (this.endTime - System.currentTimeMillis());
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getCooldownName() {
        return this.cooldownName;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.endTime = (this.startTime + this.lengthInMillis);
    }


}
