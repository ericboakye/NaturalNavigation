package io.apollosoftware.naturalnavigation.menu.submenu;

import com.bobacadodl.JSONChatLib.JSONChatMessage;
import io.apollosoftware.lib.gui.GUIButton;
import io.apollosoftware.lib.gui.GUIButtonClickEvent;
import io.apollosoftware.lib.gui.GUIItem;
import io.apollosoftware.lib.gui.Menu;
import io.apollosoftware.lib.lang.Message;
import io.apollosoftware.naturalnavigation.NaturalNavigation;
import io.apollosoftware.naturalnavigation.data.PlayerData;
import io.apollosoftware.naturalnavigation.enums.TeleportType;
import io.apollosoftware.naturalnavigation.menu.NavigationMenu;
import io.apollosoftware.naturalnavigation.menu.TeleportRequest;
import io.apollosoftware.naturalnavigation.menu.button.ActionButton;
import io.apollosoftware.naturalnavigation.menu.page.Page;
import io.apollosoftware.naturalnavigation.menu.page.PlayersPage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.apollosoftware.naturalnavigation.menu.TeleportRequest.Cooldown;
import static io.apollosoftware.naturalnavigation.menu.TeleportRequest.createRequest;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class PlayersSubmenu extends Menu<NaturalNavigation> {

    public static final int REQUEST_COOLDOWN = 60; // SECONDS

    @Getter
    private ConcurrentHashMap<Integer, Page> pages = new ConcurrentHashMap<>();

    @Getter
    private PlayerData data;

    public PlayersSubmenu(String title, final PlayerData data) {
        super(title, Menu.round(Bukkit.getOnlinePlayers().size() + 2), data.getUUID());
        this.data = data;
        setDestroyOnExit(true);

        final Player player = Bukkit.getPlayer(data.getUUID());

        if (Bukkit.getOnlinePlayers().size() <= 1) {
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();
                }
            });
            Message.create("noPlayers").sendTo(player);
            return;
        }

        Map<UUID, Player> players = new ConcurrentHashMap<>();

        for (Player p : Bukkit.getOnlinePlayers())
            if (!p.getUniqueId().equals(player.getUniqueId()))
                players.put(p.getUniqueId(), p);


        Iterator<? extends Player> playerIterator = players.values().iterator();

        int size = Menu.round(Bukkit.getOnlinePlayers().size() + 1);

        for (int i = 0; i < size; i++) {
            if (i == 26) continue;
            if (playerIterator.hasNext()) set(i, new PlayerButton(playerIterator.next()));
        }

        int extraPages = 0;
        for (Page page : pages.values()) page.remove();
        pages.clear();

        if (playerIterator.hasNext()) {
            PlayersPage page = null;
            playerLoop:
            while (playerIterator.hasNext()) {

                if (!pages.containsKey(extraPages)) {
                    page = new PlayersPage(this, extraPages);
                    pages.put(extraPages, page);
                }

                pageLoop:
                for (int row = 0; row < 4; row++)
                    for (int col = 0; col < 7; col++) {
                        int slot = (((row + 1) * 9) + 1) + col;

                        if (page.isFull()) {
                            extraPages++;
                            break pageLoop;
                        }

                        if (!playerIterator.hasNext()) {

                            if (page.isFull()) {
                                extraPages++;
                                page = new PlayersPage(this, extraPages);
                                pages.put(extraPages, page);
                            }

                            break playerLoop;
                        }
                        page.add(slot, new PlayerButton(playerIterator.next()));
                    }
            }
        }

        GUIItem nextPage;

        if (extraPages >= 1) {
            nextPage = new GUIButton(Material.PAPER) {

                @Override
                public void action(final GUIButtonClickEvent event) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        public void run() {
                            pages.get(0).open(event.getWhoClicked());
                        }
                    });
                }
            };
            nextPage.setDisplayName(ChatColor.GREEN + "Next Page");
            set(26, nextPage);
        }

        for (Page page : pages.values())
            page.update();


        GUIButton backButton = new GUIButton(Material.BARRIER) {
            @Override
            public void action(GUIButtonClickEvent event) throws Exception {
                event.getWhoClicked().closeInventory();
                PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getWhoClicked());
                new NavigationMenu(data.getState(event.getWhoClicked())).open(event.getWhoClicked());
            }
        };

        backButton.setDisplayName(ChatColor.RED + "Back");
        set(getInventorySize() - 1, backButton);
    }

    public class PlayerButton
            extends ActionButton {

        private Player target;

        public PlayerButton(Player target) {
            super(new ItemStack(Material.SKULL_ITEM));
            this.target = target;


            getItemStack().setDurability((short) 3);
            ItemStack skull = getItemStack();
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            skull.setItemMeta(meta);

            setDisplayName(ChatColor.RED + target.getName());

            setLore(Arrays.asList("&6Left-Click: &fGo", "&6Right-Click: &fBring"));
        }


        @Override
        public void onLeftClick(GUIButtonClickEvent event) throws Exception {
            event.getWhoClicked().closeInventory();
            if (!target.isOnline()) {
                Message.create("playerNotOnline").sendTo(event.getWhoClicked());
                return;
            }
            request(event.getWhoClicked(), TeleportType.GO);
        }

        @Override
        public void onRightClick(GUIButtonClickEvent event) throws Exception {
            event.getWhoClicked().closeInventory();
            PlayerData data = plugin.getPlayerStorage().createIfNotExists(event.getWhoClicked());

            if (data.isDead()) return;

            if (!target.isOnline()) {
                Message.create("playerNotOnline").sendTo(event.getWhoClicked());
                return;
            }
            event.getWhoClicked().closeInventory();
            request(event.getWhoClicked(), TeleportType.BRING);
        }

        @Override
        public void onShiftLeftClick(GUIButtonClickEvent event) throws Exception {

        }

        @Override
        public void onShiftRightClick(GUIButtonClickEvent event) throws Exception {

        }


        private void request(Player player, TeleportType type) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            TeleportRequest tr = Cooldown.getCooldown(target.getName(), player.getName());

            String json = "[{\"text\":\"" + player.getName() + " wants to teleport to you. \",\"color\":\"blue\"},{\"text\":\"[Accept]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpyes\"}},{\"text\":\" \",\"color\":\"none\"},{\"text\":\"[Deny]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpno\"}}]";
            String json2 = "[{\"text\":\"" + player.getName() + " wants to bring you to them. \",\"color\":\"dark_purple\"},{\"text\":\"[Accept]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpyes\"}},{\"text\":\" \",\"color\":\"none\"},{\"text\":\"[Deny]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tpno\"}}]";

            if (tr == null) {
                Cooldown.addCooldown(type, target.getName(),
                        player.getName(), REQUEST_COOLDOWN * 1000);
                if (type == TeleportType.GO) {
                    JSONChatMessage.sendToPlayer(target, json);
                    Message.create("sendRequestGo").param(target.getName()).sendTo(player);
                } else {
                    JSONChatMessage.sendToPlayer(target, json2);
                    Message.create("sendRequestBring").param(target.getName()).sendTo(player);
                }
                createRequest(target.getName(), player.getName());
                return;
            }

            if (tr.isOver()) {
                Cooldown.addCooldown(type, target.getName(),
                        player.getName(), REQUEST_COOLDOWN * 1000);

                if (type == TeleportType.GO) {
                    JSONChatMessage.sendToPlayer(target, json);
                    Message.create("sendRequestGo").param(target.getName()).sendTo(player);
                } else {
                    JSONChatMessage.sendToPlayer(target, json2);
                    Message.create("sendRequestBring").param(target.getName()).sendTo(player);
                }

                createRequest(target.getName(), player.getName());
                return;
            }
            Message.create("waitForRequest").param(tr.getTimeLeft() / 1000).sendTo(player);
        }

    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

}
