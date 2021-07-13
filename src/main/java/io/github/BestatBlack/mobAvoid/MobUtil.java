package io.github.BestatBlack.mobAvoid;

import java.time.Instant;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import org.bukkit.entity.*;

/**
 * Provides utility methods for the main class' logic.
 * An instance is never constructed.
 */
public class MobUtil {

	/** 
	 * Keeps track of players with lost immunity.
	 * Does not persist through restarts.
	 */
	private static final ConcurrentHashMap<UUID,Instant> timeout = new ConcurrentHashMap<>();

	/** Time, in seconds, until a player gets immunity back.*/
	private static Long timeoutDuration;

	/**
	 * Reads the timeout duration from configuration.
	 * Timeout duration is given in seconds.
	 * Default is 10 minutes (600 seconds).
	 *
	 * @param conf the plugin configuration file.
	 */
	public static void readTimeoutDuration(FileConfiguration conf) {
		MobUtil.timeoutDuration = conf.getLong("timeoutDuration");
	}

	/** 
	 * @return Should the mob's targeting be cancelled.
	 * @param e the targeting event.
	 */
	public static boolean isImmune(EntityTargetLivingEntityEvent e) {
		if(e.getTarget() instanceof Player) {
			Player p = (Player) e.getTarget();
			Entity ent = e.getEntity();

			if(ent.getCustomName() == null && p.hasPermission(getMobPerm(ent))) {
				if(Instant.now().isAfter( timeout.getOrDefault(p.getUniqueId(), Instant.MIN)) ) {
					return true;
				}
			}
		}
		return false;
	}

	/** 
	 * @return The permission for immunity to the mob.
	 * @param mob the mob to get the permission name for.
	 */
	public static String getMobPerm(Entity mob) {
		return "mobavoid.".concat(
				mob.getType().toString().replace("_","").toLowerCase());
	}

	/**
	 * Removes, if appropriate, if a player's immunity for {@link #timeoutDuration}.
	 * A player will lose immunity if they attack a mob they have immunity to.
	 * Players with the * permission are exempt.
	 * Consecutive offenses will reset the timer to the most recent attack.
	 *
	 * @param e the damage event.
	 */
	public static void loseImmune(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();

			if(p.hasPermission("mobavoid.*")) {
				return;
			}

			if(p.hasPermission(getMobPerm(e.getEntity()))) {
				timeout.put(p.getUniqueId(), java.time.Instant.now().plusSeconds(timeoutDuration));
			}
		}
	}
}
