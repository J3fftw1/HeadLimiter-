package dev.j3fftw.headlimiter;

import co.aikar.commands.PaperCommandManager;
import dev.j3fftw.headlimiter.blocklimiter.BlockLimiter;
import dev.j3fftw.headlimiter.blocklimiter.Group;
import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.BlobBuildUpdater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class HeadLimiter extends JavaPlugin {

    private static HeadLimiter instance;
    private BlockLimiter blockLimiter;


    @Override
    public void onEnable() {
        instance = this;
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        new MetricsService(this).start();

        if (getConfig().getBoolean("auto-update", true) && getDescription().getVersion().startsWith("Dev")) {
            new BlobBuildUpdater(this, getFile(), "HeadLimiter", "Dev").start();
        }

        this.blockLimiter = new BlockLimiter(this);
        loadConfig();
        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public BlockLimiter getBlockLimiter() {
        return blockLimiter;
    }

    public static HeadLimiter getInstance() {
        return instance;
    }

    private void loadConfig() {
        ConfigurationSection configurationSection = instance.getConfig().getConfigurationSection("block-limits");
        if (configurationSection == null) {
            throw new IllegalStateException("No configuration for groups is available.");
        }
        for (String key : configurationSection.getKeys(false)) {
            BlockLimiter.getInstance().getGroups().add(new Group(configurationSection.getConfigurationSection(key)));
        }
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.enableUnstableAPI("help");
        manager.enableUnstableAPI("brigadier");

        manager.getCommandCompletions().registerCompletion("groups", c -> BlockLimiter.getInstance().getGroups().stream().map(Group::getGroupName).toList());

        manager.registerCommand(new MainCommand());
    }
}
