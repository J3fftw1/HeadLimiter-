package dev.j3fftw.headlimiter;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import dev.j3fftw.headlimiter.blocklimiter.BlockLimiter;
import dev.j3fftw.headlimiter.blocklimiter.ChunkContent;
import dev.j3fftw.headlimiter.blocklimiter.Group;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.ChunkPosition;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
@CommandAlias("headlimiter|hl")
@Description("HeadLimiter command")
public class MainCommand extends BaseCommand {

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("groups")
    @Description("List all groups")
    public void groups(Player p) {
        p.sendMessage(ChatColor.YELLOW + "Groups:");
        final String groups = BlockLimiter.getInstance().getGroups().stream().map(Group::getGroupName).collect(Collectors.joining(", "));
        p.sendMessage(ChatColor.GOLD + groups);
    }

    @Subcommand("count")
    @Syntax("<group>")
    @CommandCompletion("@groups")
    @Description("Display the count of blocks in a specific group")
    public void count(Player p, String groupName) {
        Group group = null;
        for (Group g : BlockLimiter.getInstance().getGroups()) {
            if (g.getGroupName().equalsIgnoreCase(groupName)) {
                group = g;
                break;
            }
        }

        if (group == null) {
            p.sendMessage(ChatColor.RED + "Group not found");
            return;
        }

        if (group.getItems().isEmpty()) {
            p.sendMessage(ChatColor.RED + "This group does not limit any items");
            return;
        }

        final ChunkPosition chunkPosition = new ChunkPosition(p.getLocation().getChunk());
        final ChunkContent content = BlockLimiter.getInstance().getChunkContent(chunkPosition);

        p.sendMessage(ChatColor.YELLOW + "====== " + group.getGroupName() + " ======");

        final int total;
        if (content == null) {
            total = 0;
        } else {
            total = content.getGroupTotal(group.getItems().iterator().next());
        }

        p.sendMessage(ChatColor.YELLOW + "Placed blocks: " + ChatColor.GOLD + total + "/" + group.getPermissibleAmount(p));

        if (Utils.canBypass(p)) {
            p.sendMessage(ChatColor.YELLOW + "You can bypass the limit");
        }
    }
}
