package ca.q0r.msocial.commands;

import ca.q0r.msocial.MSocial;
import ca.q0r.msocial.types.LocaleType;
import com.miraclem4n.mchat.api.API;
import com.miraclem4n.mchat.types.IndicatorType;
import com.miraclem4n.mchat.util.MessageUtil;
import com.miraclem4n.mchat.util.MiscUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.TreeMap;

public class MuteCommand implements CommandExecutor {
    MSocial plugin;

    public MuteCommand(MSocial instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mchatmute")
                || !MiscUtil.hasCommandPerm(sender, "mchat.mute"))
            return true;

        if (args.length < 1)
            return false;

        String target = args[0];
        Player player = plugin.getServer().getPlayer(args[0]);

        if (player != null)
            target = player.getName();

        TreeMap<String, String> rMap = new TreeMap<String, String>();

        rMap.put("player", target);

        if (plugin.isMuted.get(target) != null && plugin.isMuted.get(target)) {
            plugin.isMuted.put(target, false);

            rMap.put("muted", "unmuted");
            rMap.put("mute", "mute");

            MessageUtil.sendMessage(sender, API.replace(LocaleType.MESSAGE_MUTE_MISC.getVal(), rMap, IndicatorType.LOCALE_VAR));
        } else {
            plugin.isMuted.put(target, true);

            rMap.put("muted", "muted");
            rMap.put("mute", "unmute");

            MessageUtil.sendMessage(sender, API.replace(LocaleType.MESSAGE_MUTE_MISC.getVal(), rMap, IndicatorType.LOCALE_VAR));
        }

        return true;
    }
}
