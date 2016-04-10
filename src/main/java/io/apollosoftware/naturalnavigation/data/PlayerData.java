package io.apollosoftware.naturalnavigation.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.enums.PlayerState;
import io.apollosoftware.naturalnavigation.lib.Title;
import io.apollosoftware.naturalnavigation.storage.PlayerStorage;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.mysql.ByteArray;
import io.apollosoftware.naturalnavigation.enums.PlayerState;
import io.apollosoftware.naturalnavigation.lib.Title;
import io.apollosoftware.naturalnavigation.storage.PlayerStorage;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.UUID;

@DatabaseTable(tableName = "players", daoClass = PlayerStorage.class)
public class PlayerData {


    @DatabaseField(id = true, persisterClass = ByteArray.class, columnDefinition = "BINARY(16) NOT NULL")
    private byte[] id;

    @Setter
    @DatabaseField(columnDefinition = "VARCHAR(17)", canBeNull = true)
    private String name;

    @DatabaseField(columnDefinition = "TINYINT(1)")
    private int isDead = 0;

    private UUID uuid = null;

    @Getter
    @Setter
    private Location deathLocation;


    PlayerData() {
        // for ormlite
    }

    public PlayerData(UUID uuid, String name) {
        this.id = toBytes(uuid);
        this.uuid = uuid;
        this.name = name;
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.id = toBytes(uuid);
    }

    public UUID getUUID() {
        if (uuid == null) {
            uuid = fromBytes(id);
        }
        return uuid;
    }

    public boolean isDead() {
        return isDead == 1;
    }

    public void isDead(final Player player, boolean isDead) {
        if (isDead) {
            this.isDead = 1;
        } else {
            Bukkit.getScheduler().runTask(NaturalNavigation.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (getState(player) == PlayerState.PLAYING) {
                        for (Player p : Bukkit.getOnlinePlayers()) p.showPlayer(player);
                        player.setGameMode(GameMode.SURVIVAL);

                        if (!NaturalNavigation.getPlugin().getWorld(player).isKeepInventory())
                            player.getInventory().clear();

                        new Title("").resetTitle(player);
                        player.setAllowFlight(false);
                    } else {
                        for (Player p : Bukkit.getOnlinePlayers()) p.showPlayer(player);
                        player.setGameMode(GameMode.ADVENTURE);

                        if (!NaturalNavigation.getPlugin().getWorld(player).isKeepInventory())
                            player.getInventory().clear();

                        player.setAllowFlight(true);
                        new Title(Message.getString("actionBarStart")).sendActionBar(player);
                        NaturalNavigation.getPlugin().setInventoryLayout(PlayerData.this, player);
                    }
                }
            });

            this.isDead = 0;
        }
    }

    public PlayerState getState(Player player) {
        if (isDead()) return PlayerState.DEAD;
        else if (player.hasPermission("nav.start_game") || !NaturalNavigation.getPlugin().getConfiguration().isPreGameStateEnabled())
            return PlayerState.PLAYING;
        return PlayerState.PRE;
    }

    public String getName() {

        if (Bukkit.getPlayer(uuid) != null && !Bukkit.getPlayer(uuid).getName().equals(name)) {
            this.name = Bukkit.getPlayer(uuid).getName();
            try {
                NaturalNavigation.getPlugin().getPlayerStorage().update(this);
            } catch (SQLException ignored) {

            }
        }
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PlayerData) && ((PlayerData) obj).getUUID().equals(uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public static UUID fromBytes(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: " + array.length);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        long mostSignificant = byteBuffer.getLong();
        long leastSignificant = byteBuffer.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }

    public static byte[] toBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

}
