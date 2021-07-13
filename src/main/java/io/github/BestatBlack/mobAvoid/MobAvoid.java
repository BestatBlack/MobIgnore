package io.github.BestatBlack.mobAvoid;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

/**
 * Processes server calls, using other classes for logic.
 */
public class MobAvoid extends JavaPlugin implements Listener {

	/**
	 * Registers events and handles the configuration.
	 * The configuration is only read here.
	 */
    @Override
    public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		this.saveDefaultConfig();
		MobUtil.readTimeoutDuration(this.getConfig());

		getLogger().info("MobAvoid is enabled.");
    }

    @Override
    public void onDisable() {
		getLogger().info("MobAvoid is disabled.");
    }

	/**
	 * Applies immunity to qualifying players.
	 * This is the primary function of the plugin.
	 * 
	 * @param e the event.
	 */
	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent e) {
		if(MobUtil.isImmune(e)) {
			e.setTarget(null);
			e.setCancelled(true);
		}
	}

	/** 
	 * Removes immunity for a time as necessary.
	 * @param e the event.
	 */
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		MobUtil.loseImmune(e);
	}
}
