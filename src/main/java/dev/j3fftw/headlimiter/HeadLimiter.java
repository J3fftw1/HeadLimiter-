package dev.j3fftw.headlimiter;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.GitHubBuildsUpdater;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class HeadLimiter extends JavaPlugin implements Listener {

    private static HeadLimiter instance;

    @Override
    public void onEnable() {
        instance = this;
        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();

        Utils.loadPermissions();

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("headlimiter").setExecutor(new CountCommand());

        new MetricsService(this).start();

        if (getConfig().getBoolean("auto-update", true) && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "J3fftw1/HeadLimiter/master").start();
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public boolean isCargo(SlimefunItem sfItem) {
        return sfItem.isItem(SlimefunItems.CARGO_INPUT_NODE)
            || sfItem.isItem(SlimefunItems.CARGO_OUTPUT_NODE)
            || sfItem.isItem(SlimefunItems.CARGO_OUTPUT_NODE_2)
            || sfItem.isItem(SlimefunItems.CARGO_CONNECTOR_NODE)
            || sfItem.isItem(SlimefunItems.CARGO_MANAGER);
    }

    public boolean isAndroid(SlimefunItem sfItem) {
        return sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_FARMER)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_MINER)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_WOODCUTTER)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_BUTCHER)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_FISHERMAN)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_2)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_2_FISHERMAN)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_2_FARMER)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_2_BUTCHER)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_3)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_3_FISHERMAN)
            || sfItem.isItem(SlimefunItems.PROGRAMMABLE_ANDROID_3_BUTCHER);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();

        if (!e.isCancelled()
            && (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD)
            && !Utils.canBypass(player)
        ) {
            final SlimefunItem sfItem = SlimefunItem.getByItem(e.getItemInHand());
            if (sfItem == null) {
                return;
            }

            if (isCargo(sfItem)) {
                final int maxAmount = Utils.getMaxHeads(player);
                Utils.countCargo(block.getChunk(),
                    result -> Utils.onCheck(player, block, maxAmount, result.getTotal(), sfItem));
            }

            if (getConfig().getBoolean("androids-limit", false) && isAndroid(sfItem)) {
                final int maxAmount = Utils.getMaxHeads(player);
                Utils.countAndroids(block.getChunk(),
                    result -> Utils.onCheck(player, block, maxAmount, result.getTotal(), sfItem));
            }
        }
    }

    public static HeadLimiter getInstance() {
        return instance;
    }
}
