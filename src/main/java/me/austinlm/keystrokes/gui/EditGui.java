package me.austinlm.keystrokes.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.Set;
import me.austinlm.keystrokes.KeystrokesMod;
import me.austinlm.keystrokes.hud.HUDDrawer;
import me.austinlm.keystrokes.hud.Key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

/**
 * GUI which allows {@link Key}s to be moved around.
 */
public class EditGui extends Screen {

	private final Set<Key> keys;
	private final HUDDrawer drawer;
	// Current key being dragged by the user
	private Optional<Key> draggingKey = Optional.empty();

	public EditGui(Set<Key> keys, HUDDrawer drawer) {
		super(new StringTextComponent(""));
		this.keys = keys;
		this.drawer = drawer;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY,
			float partialTicks) {
		// Main drawer not active when GUI is open
		drawer.draw(matrixStack, Minecraft.getInstance());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		draggingKey = keys.stream().filter(k -> k.isInside(mouseX, mouseY)).findAny();
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		draggingKey = Optional.empty();
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX,
			double dragY) {
		draggingKey.ifPresent(k -> k.getLocation().relative(dragX, dragY));

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public void closeScreen() {
		KeystrokesMod.INSTANCE.saveConfig();
		super.closeScreen();
	}
}
