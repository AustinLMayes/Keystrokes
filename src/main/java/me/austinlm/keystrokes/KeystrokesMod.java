package me.austinlm.keystrokes;

import com.google.common.collect.Sets;
import java.util.Set;
import me.austinlm.keystrokes.gui.EditGui;
import me.austinlm.keystrokes.hud.HUDDrawer;
import me.austinlm.keystrokes.hud.Key;
import me.austinlm.keystrokes.hud.KeyType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("keystrokes")
public class KeystrokesMod {

	public static KeystrokesMod INSTANCE;
	// 90 = Z
	private final KeyBinding openGui = new KeyBinding("Open GUI", 90, "Keystrokes");
	private final HUDDrawer hudDrawer = new HUDDrawer();
	private final Set<Key> keys = Sets.newHashSet();
	// CPS Trackers
	private final ActionCounter attackCount = new ActionCounter(1000);
	private final ActionCounter useCount = new ActionCounter(1000);
	private ModConfig config;

	public KeystrokesMod() {
		INSTANCE = this;

		// Register all keys
		for (KeyType type : KeyType.values()) {
			keys.add(new Key(type));
		}

		// Create config spec
		ForgeConfigSpec.Builder builder = new Builder();
		KeystrokesConfig.createSpec(builder, this.keys);
		ModLoadingContext.get().registerConfig(Type.CLIENT, builder.build());

		// Load config
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadingFinished);

		// Listeners
		MinecraftForge.EVENT_BUS.addListener(this::incrementKeyClicks);
		MinecraftForge.EVENT_BUS.addListener(this::incrementMouseClicks);
		MinecraftForge.EVENT_BUS.register(hudDrawer);
	}

	private void loadingFinished(ModConfigEvent event) {
		config = event.getConfig();
		KeystrokesConfig.load(config, this.keys);

		ClientRegistry.registerKeyBinding(openGui);
	}

	public void saveConfig() {
		KeystrokesConfig.saveState(config, this.keys);
		config.save();
	}

	/**
	 * Flushes any cached data stored in keys when bindings are updated
	 */
	private void flushCaches() {
		if (Minecraft.getInstance().currentScreen instanceof ControlsScreen) {
			for (Key key : this.keys) {
				key.flushCache();
			}
		}
	}

	private void incrementMouseClicks(MouseInputEvent event) {
		incrementClicks(event, event.getAction());
	}

	private void incrementKeyClicks(KeyInputEvent event) {
		// Open the edit GUI
		if (openGui.isPressed()) {
			Minecraft.getInstance().displayGuiScreen(new EditGui(this.keys, this.hudDrawer));
		}
		incrementClicks(event, event.getAction());
	}

	/**
	 * Increments CPS counters.
	 */
	private void incrementClicks(InputEvent event, int action) {
		if (action == GLFW.GLFW_PRESS) {
			// NOTE: isPressed() changes press time (which is why isPressed() only returns true once)
			// and this fires before the arm swings (which also checks isPressed())
			// thus canceling out the swing animation.
			if (Minecraft.getInstance().gameSettings.keyBindAttack.isKeyDown()) {
				this.attackCount.action();
			}
			if (Minecraft.getInstance().gameSettings.keyBindUseItem.isKeyDown()) {
				this.useCount.action();
			}
			flushCaches();
		}
	}

	public double getAttackAverage() {
		return attackCount.getActionsInPeriod();
	}

	public double getUseAverage() {
		return useCount.getActionsInPeriod();
	}

	public Set<Key> getKeys() {
		return keys;
	}
}
