package me.austinlm.keystrokes.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import me.austinlm.keystrokes.KeystrokesMod;
import me.austinlm.keystrokes.hud.HUDDrawer;
import me.austinlm.keystrokes.hud.Key;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.util.text.StringTextComponent;

/**
 * Main mod settings GUI.
 *
 * Since forge offers no default mod settings GUI helpers anymore, we have to do a lot of wheel
 * reinventing here in order to create our own menu.
 */
public class SettingsGui extends Screen {

	private final HUDDrawer drawer;
	private final List<Key> keys;
	private OptionsRowList optionsRowList;

	public SettingsGui(List<Key> keys, HUDDrawer drawer) {
		super(new StringTextComponent("Keytrokes Settings"));
		this.drawer = drawer;
		this.keys = keys;
	}

	@Override
	protected void init() {
		this.optionsRowList = new OptionsRowList(this.minecraft, this.width, this.height, 32,
				this.height - 32, 25);
		// No dirt BG
		this.optionsRowList.func_244605_b(false);
		this.optionsRowList.func_244606_c(false);
		// Colors
		this.optionsRowList.addOptions(
				new AbstractOption[]{new ColorSlider("Background Red", drawer::getBgRed, drawer::setBgRed),
						new ColorSlider("Background Green", drawer::getBgGreen, drawer::setBgGreen),
						new ColorSlider("Background Blue", drawer::getBgBlue, drawer::setBgBlue)});
		this.optionsRowList.addOptions(
				new AbstractOption[]{new ColorSlider("Text Red", drawer::getTextRed, drawer::setTextRed),
						new ColorSlider("Text Green", drawer::getTextGreen, drawer::setTextGreen),
						new ColorSlider("Text Blue", drawer::getTextBlue, drawer::setTextBlue)});
		// Toggles for each key
		List<AbstractOption> keyOptions = Lists.newArrayList();
		for (Key key : this.keys) {
			keyOptions.add(
					new BooleanOption(key.getType().getBinding().getKeyDescription(), (s) -> key.shouldDraw(),
							(s, v) -> key.setShown(v)));
		}
		// In a list for easy 2-per-line creation
		this.optionsRowList.addOptions(keyOptions.toArray(new AbstractOption[]{}));
		// Buttons at bottom
		this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20,
				new StringTextComponent("Edit Layout"), (p_213082_1_) -> {
			this.minecraft.displayGuiScreen(new EditGui(this.keys, this.drawer));
		}));
		this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, DialogTexts.GUI_DONE,
				(p_213085_1_) -> {
					this.minecraft.displayGuiScreen(null);
				}));
		this.children.add(this.optionsRowList);
	}

	@Override
	public void closeScreen() {
		KeystrokesMod.INSTANCE.saveConfig();
		super.closeScreen();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		this.optionsRowList.render(matrixStack, mouseX, mouseY, partialTicks);
		drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 5, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}
