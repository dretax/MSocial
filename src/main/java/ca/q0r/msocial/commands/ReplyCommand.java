package ca.q0r.msocial.commands;

import ca.q0r.msocial.MSocial;
import ca.q0r.msocial.types.ConfigType;
import ca.q0r.msocial.types.LocaleType;
import com.miraclem4n.mchat.api.API;
import com.miraclem4n.mchat.api.Parser;
import com.miraclem4n.mchat.types.IndicatorType;
import com.miraclem4n.mchat.util.MessageUtil;
import com.miraclem4n.mchat.util.MiscUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.TreeMap;

public class ReplyCommand implements CommandExecutor {
    MSocial plugin;

    public ReplyCommand(MSocial instance) {
        plugin = instance;
    }

    String message = "";

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("pmchatreply")
                || !MiscUtil.hasCommandPerm(sender, "mchat.pm.reply"))
            return true;

        //TODO Allow Console's to PM
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "Console's can't send PM's.");
            return true;
        }

        Player player = (Player) sender;
        String pName = player.getName();
        String world = player.getWorld().getName();

        message = "";

        for (String arg : args)
            message += " " + arg;

        String rName = plugin.lastPMd.get(pName);

        if (rName == null) {
            MessageUtil.sendMessage(player, LocaleType.MESSAGE_PM_NO_PM.getVal());
            return true;
        }

        Player recipient = plugin.getServer().getPlayer(rName);

        if (!MiscUtil.isOnlineForCommand(sender, recipient))
            return true;

        String senderName = Parser.parsePlayerName(pName, world);

        TreeMap<String, String> rMap = new TreeMap<String, String>();

        rMap.put("recipient", Parser.parsePlayerName(rName, recipient.getWorld().getName()));
        rMap.put("sender", Parser.parsePlayerName(senderName, world));
        rMap.put("msg", message);

        player.sendMessage(API.replace(LocaleType.FORMAT_PM_SENT.getVal(), rMap, IndicatorType.LOCALE_VAR));

        if (plugin.spoutB) {
            sendSpoutMessage(player, recipient, message);
            return true;
        }

        plugin.lastPMd.put(rName, pName);

        recipient.sendMessage(API.replace(LocaleType.FORMAT_PM_RECEIVED.getVal(), rMap, IndicatorType.LOCALE_VAR));
        MessageUtil.log(API.replace(LocaleType.FORMAT_PM_RECEIVED.getVal(), rMap, IndicatorType.LOCALE_VAR));

        return true;
    }

    void sendSpoutMessage(Player player, Player recipient, final String message) {
        if (ConfigType.OPTION_SPOUT.getBoolean()) {
            final SpoutPlayer sRecipient = (SpoutPlayer) recipient;

            if (sRecipient.isSpoutCraftEnabled()) {
                plugin.lastPMd.put(recipient.getName(), player.getName());

                sRecipient.sendNotification(LocaleType.MESSAGE_SPOUT_PM.getVal(), player.getName(), Material.PAPER);

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        for (int i = 0; i < ((message.length() / 40) + 1); i++) {
                            sendRunnableNotification(sRecipient, formatPM(message, ((40 * i) + 1), ((i * 40) + 20)), formatPM(message, ((i * 40) + 21), ((i * 40) + 40)), i);
                        }
                    }
                }, 2 * 20);

            }
        }
    }

    void sendRunnableNotification(final SpoutPlayer recipient, final String messageA, final String messageB, Integer delay) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                recipient.sendNotification(messageA, messageB, Material.PAPER);
            }
        }, 40 * delay);
    }

    String formatPM(String message, Integer start, Integer finish) {
        while (message.length() <= finish) message += " ";
        return message.substring(start, finish);
    }
}
