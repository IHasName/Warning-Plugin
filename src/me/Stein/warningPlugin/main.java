package me.Stein.warningPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener {

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		loadConfig();
	}
	
	public void loadConfig() {
		FileConfiguration cfg = getConfig();
		cfg.options().copyDefaults(true);
		saveConfig();
	}
	
	public void onDisable() {}
	
	public boolean onCommand(CommandSender s, Command cmd, String cmdlabel,String[] args) {
		Player p = (Player)s;
		if(s instanceof ConsoleCommandSender) {
			s.sendMessage("Versuche es Ingame...");
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("warning")) {
			if(s.hasPermission("warning.use")) {
				if(args.length == 0) {
					s.sendMessage("§cZu wenig Argumente.\n/warning <warn,reset,undo> <Spieler> <[Grund]>");
					return true;
				}
			}else{
				s.sendMessage("§cDu hast nicht genug Rechte um einen Spieler hierdurch zu bannen!");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("warn")) {
				if(s.hasPermission("warning.use")) {
					if(args.length <= 1) {
						s.sendMessage("§cZu wenig Argumente.\n/warning warn <Spieler> <Grund>");
						return true;
					}else {
						Player towarn = Bukkit.getPlayer(args[1]);
						if(towarn == null) {
							s.sendMessage("§cDer Spieler " + args[1] + " konnte nicht gefunden werden");
							return true;
						}
						reloadConfig();
						if(getConfig().getString("warnings." + towarn.getUniqueId()) == null) {
							getConfig().set("warnings." + towarn.getUniqueId() + ".warninglevel", 0);
							getConfig().set("warnings." + towarn.getUniqueId() + ".playername", args[0]);
							getConfig().set("warnings." + towarn.getUniqueId() + ".lastby", p.getName());
							saveConfig();
						}
						if(getConfig().getInt("warnings." + towarn.getUniqueId() + ".warninglevel") < 3) {
							getConfig().set("warnings." + towarn.getUniqueId() + ".warninglevel", getConfig().getInt("warnings." + towarn.getUniqueId() + ".warninglevel") + 1);
							getConfig().set("warnings." + towarn.getUniqueId() + ".lastby", p.getName());
							saveConfig();
							reloadConfig();
							towarn.kickPlayer(args[2].replace('&', '§') + "\n\n§cWarnung: (" + getConfig().getInt("warnings." + towarn.getUniqueId() + ".warninglevel") + "/3)\nVerwarnt von " + getConfig().getString("warnings." + towarn.getUniqueId() + ".lastby"));
							for(Player all : Bukkit.getOnlinePlayers()) {
								if(all.hasPermission("warning.see.kick")) {
									all.sendMessage("§c" + towarn.getName() + " wurde von " + s.getName() + " verwarnt");
									all.sendMessage("§cVerwarnungen von " + towarn.getName() + ": " + getConfig().getInt("warnings." + towarn.getUniqueId() + ".warninglevel"));
								}
							}
						} else {
							if(s.hasPermission("warning.ban")) {
								getConfig().set("warnings." + towarn.getUniqueId() + ".warninglevel", 4);
								getConfig().set("warnings." + towarn.getUniqueId() + ".lastby", p.getName());
								saveConfig();
								towarn.kickPlayer(args[2].replace('&', '§') + "\n\n§cWarnung: (" + getConfig().getInt("warnings." + towarn.getUniqueId() + ".warninglevel") + "/3)\nVerwarnt von " + getConfig().getString("warnings." + towarn.getUniqueId() + ".lastby"));
								for(Player all : Bukkit.getOnlinePlayers()) {
									if(all.hasPermission("warning.see.ban")) {
										all.sendMessage("§c" + towarn.getName() + " wurde von " + s.getName() + " durch mehr als 3 Verwarnungen gebannt");
									}
								}
							}
							s.sendMessage("§cDu hast nicht genug Rechte um einen Spieler hierdurch zu bannen!");
							return true;
						}
					return true;
					}
				}
			}
			
			if(args[0].equalsIgnoreCase("reset")) {
				if(s.hasPermission("warning.use.reset")) {
					if(args.length == 0) {
						s.sendMessage("§cZu wenig Argumente.\n/warning reset <Spieler>");
						return true;
					}
					Player resetwarn = Bukkit.getPlayer(args[1]);
					if(resetwarn == null) {
						s.sendMessage("§cDer Spieler " + args[1] + " konnte nicht gefunden werden");
						return true;
					}
					reloadConfig();
					if(getConfig().getString("warnings." + resetwarn.getUniqueId()) == null) {
						s.sendMessage("§c" + resetwarn.getName() + " wurde nie verwarnt");
						return true;
					}
					getConfig().set("warnings." + resetwarn.getUniqueId() + ".warninglevel", null);
					getConfig().set("warnings." + resetwarn.getUniqueId() + ".playername", null);
					getConfig().set("warnings." + resetwarn.getUniqueId() + ".lastby", null);
					getConfig().set("warnings." + resetwarn.getUniqueId(), null);
					saveConfig();
					reloadConfig();
					s.sendMessage("§eDie Warnungen von §7" + resetwarn.getName() + " §ewurden entfernt");
					return true;
				}
				s.sendMessage("§cDu hast nicht genug Rechte um diesen Command zu benutzen!");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("undo")) {
				if(s.hasPermission("warning.use.undo")) {
					if(args.length == 0) {
						s.sendMessage("§cZu wenig Argumente.\n/warning reset <Spieler>");
						return true;
					}
					Player undowarn = Bukkit.getPlayer(args[1]);
					if(undowarn == null) {
						s.sendMessage("§cDer Spieler " + args[1] + " konnte nicht gefunden werden");
						return true;
					}
					getConfig().set("warnings." + undowarn.getUniqueId() + ".warninglevel", getConfig().getInt("warnings." + undowarn.getUniqueId() + ".warninglevel") - 1);
					getConfig().set("warnings." + undowarn.getUniqueId() + ".lastby", "Unknown");
					saveConfig();
					reloadConfig();
					s.sendMessage("§eDie Warnungen von §7" + undowarn.getName() + " §ewurden um 1 verringert.\nVorher" + getConfig().getInt("warnings." + undowarn.getUniqueId() + ".warninglevel"));
					s.sendMessage("§eVorher:" + (getConfig().getInt("warnings." + undowarn.getUniqueId() + ".warninglevel") + 1) + " Jetzt: " + getConfig().getInt("warnings." + undowarn.getUniqueId() + ".warninglevel"));
					return true;
				}
				s.sendMessage("§cDu hast nicht genug Rechte um diesen Command zu benutzen!");
				return true;
				}
			}
		return false;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void join(AsyncPlayerPreLoginEvent e) {
		reloadConfig();
		if(getConfig().getInt("warnings." + e.getUniqueId() + ".warninglevel") > 3) {
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "\n" + getConfig().getString("whenbanned").replace('&', '§') + "\n§cGebannt von: " + getConfig().getString("warnings." + e.getUniqueId() + ".lastby"));
		}
	}
}