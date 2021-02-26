package me.austinlm.keystrokes.hud;

import javax.annotation.Nullable;
import me.austinlm.keystrokes.ScaleSafeVector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

/**
 * Mapper of Minecraft game settings to user-readable key types.
 *
 * Types with no set location are automatically disabled on initial mod setup. These can be enabled
 * manually in the mod's config.
 */
public enum KeyType {
	MOVE_FORWARD(ScaleSafeVector.TOP_LEFT.addCopy(-0.05, 0),
			Minecraft.getInstance().gameSettings.keyBindForward),
	MOVE_LEFT(ScaleSafeVector.TOP_LEFT.addCopy(-0.1, 0.1),
			Minecraft.getInstance().gameSettings.keyBindLeft),
	MOVE_BACK(ScaleSafeVector.TOP_LEFT.addCopy(-0.05, 0.1),
			Minecraft.getInstance().gameSettings.keyBindBack),
	MOVE_RIGHT(ScaleSafeVector.TOP_LEFT.addCopy(0, 0.1),
			Minecraft.getInstance().gameSettings.keyBindRight),
	MOVE_JUMP(ScaleSafeVector.TOP_LEFT.addCopy(-0.075, 0.2),
			Minecraft.getInstance().gameSettings.keyBindJump),
	MOVE_SNEAK(null, Minecraft.getInstance().gameSettings.keyBindSneak),
	MOVE_SPRINT(null, Minecraft.getInstance().gameSettings.keyBindSprint),
	ATTACK(ScaleSafeVector.TOP_LEFT.addCopy(-0.2, 0.3),
			Minecraft.getInstance().gameSettings.keyBindAttack),
	USE(ScaleSafeVector.TOP_LEFT.addCopy(0, 0.3),
			Minecraft.getInstance().gameSettings.keyBindUseItem),
	DROP(null, Minecraft.getInstance().gameSettings.keyBindDrop),
	PERSPECTIVE(null, Minecraft.getInstance().gameSettings.keyBindTogglePerspective),
	HOTBAR_1(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[0]),
	HOTBAR_2(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[1]),
	HOTBAR_3(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[2]),
	HOTBAR_4(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[3]),
	HOTBAR_5(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[4]),
	HOTBAR_6(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[5]),
	HOTBAR_7(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[6]),
	HOTBAR_8(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[7]),
	HOTBAR_9(null, Minecraft.getInstance().gameSettings.keyBindsHotbar[8]),
	;

	private final @Nullable
	ScaleSafeVector defaultLocation;
	private final KeyBinding binding;

	KeyType(@Nullable ScaleSafeVector defaultLocation,
			KeyBinding binding) {
		this.defaultLocation = defaultLocation;
		this.binding = binding;
	}

	@Nullable
	public ScaleSafeVector getDefaultLocation() {
		return defaultLocation;
	}

	public KeyBinding getBinding() {
		return binding;
	}
}
