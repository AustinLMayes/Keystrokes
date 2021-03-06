package me.austinlm.keystrokes;

import me.austinlm.keystrokes.hud.Key;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Config loading/saving logic
 */
public class KeystrokesConfig {

	/**
	 * Create the forge spec for the specified keys
	 */
	public static void createSpec(ForgeConfigSpec.Builder builder, KeystrokesMod mod) {
		mod.getHudDrawer().createSpec(builder);
		for (Key key : mod.getKeys()) {
			builder.push(key.getType().name().toLowerCase());
			builder.define("shown", key.shouldDraw());
			ScaleSafeVector.defineConfigSpec(builder, key.getLocation());
			builder.pop();
		}
	}

	/**
	 * Update key data with fresh config data
	 */
	public static void load(ModConfig config, KeystrokesMod mod) {
		mod.getHudDrawer().loadData(config.getConfigData());
		for (Key key : mod.getKeys()) {
			String basePath = key.getType().name().toLowerCase() + ".";
			key.setShown(config.getConfigData().get(basePath + "shown"));
			key.setLocation(ScaleSafeVector
					.fromConfig(config.getConfigData().get(basePath + "location"), key.getLocation()));
		}
	}

	/**
	 * Copy the current in-memory state of each key into the config
	 */
	public static void saveState(ModConfig config, KeystrokesMod mod) {
		mod.getHudDrawer().saveDate(config.getConfigData());
		for (Key key : mod.getKeys()) {
			String basePath = key.getType().name().toLowerCase() + ".";
			config.getConfigData().set(basePath + "shown", key.shouldDraw());
			if (key.getLocation() != null) {
				config.getConfigData().set(basePath + "location.x", key.getLocation().getRawX());
				config.getConfigData().set(basePath + "location.y", key.getLocation().getRawY());
			}
		}
	}
}
