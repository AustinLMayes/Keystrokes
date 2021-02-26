package me.austinlm.keystrokes.hud;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.List;
import me.austinlm.keystrokes.KeystrokesMod;
import me.austinlm.keystrokes.ScaleSafeVector;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.opengl.GL46;

/**
 * Main draw logic
 */
@EventBusSubscriber
public class HUDDrawer {

	// Base scale of each box
	private static final float BOX_SCALE = 1.25f;
	// Scale of info text (just cps for now)
	private static final float INFO_SCALE = 0.8f;

	// Paddings to add on each side of the box
	private static final float PADDING_X = 10f;
	private static final float PADDING_Y = 6f;

	private int bgRed = 0;
	private int bgGreen = 0;
	private int bgBlue = 0;
	private Color textColor = Color.WHITE;

	@SubscribeEvent
	public void onDraw(RenderGameOverlayEvent.Post event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (event.getType() == ElementType.ALL && !minecraft.isGamePaused()) {
			draw(event.getMatrixStack(), minecraft);
		}
	}

	/**
	 * Draw all keys
	 */
	public void draw(MatrixStack stack, Minecraft minecraft) {
		GL46.glPushMatrix();
		// Keep screen res fresh
		ScaleSafeVector.setScreenResolution(minecraft.getMainWindow().getScaledWidth(),
				minecraft.getMainWindow().getScaledHeight());
		for (Key key : KeystrokesMod.INSTANCE.getKeys()) {
			// Each key in own matrix for scaling
			GL46.glPushMatrix();
			draw(stack, minecraft, key);
			GL46.glPopMatrix();
		}
		GL46.glPopMatrix();
	}

	/**
	 * Toggle OpenGL properties for drawing keys
	 */
	private void setGLProps(boolean set) {
		if (set) {
			RenderSystem.scaled(BOX_SCALE, BOX_SCALE, BOX_SCALE);
			RenderSystem.enableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.shadeModel(GL46.GL_SMOOTH);
			GL46.glBegin(GL46.GL_QUADS);
		} else {
			GL46.glEnd();
			RenderSystem.shadeModel(GL46.GL_FLAT);
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
		}
	}

	/**
	 * Draw a single key (if it should be shown)
	 */
	public void draw(MatrixStack matrixStack, Minecraft minecraft, Key key) {
		if (!key.shouldDraw()) {
			return;
		}
		// Each line of the button
		List<ITextComponent> text = Lists.newArrayList();
		// First line is header
		text.add(key.getText());
		// Add info lines
		if (key.getType() == KeyType.ATTACK || key.getType() == KeyType.USE) {
			double cps = key.getType() == KeyType.ATTACK ? KeystrokesMod.INSTANCE.getAttackAverage()
					: KeystrokesMod.INSTANCE.getUseAverage();
			text.add(new StringTextComponent(String.format("CPS: %.2f", cps)));
		}
		// First row is always largest
		float width = minecraft.fontRenderer.getStringPropertyWidth(text.get(0));
		width += PADDING_X;
		float height = (minecraft.fontRenderer.FONT_HEIGHT * text.size()) + (PADDING_Y * 2);

		// Needed for drag check
		key.setDimensions(width, height);

		float alpha = key.getType().getBinding().isKeyDown() ? 1f : 0.5f;
		// Move to the key's locaion
		GL46.glTranslated(key.getLocation().getX(), key.getLocation().getY(), 0.0);
		// background color
		GL46.glColor4f(this.bgRed, this.bgGreen, this.bgBlue, alpha);
		setGLProps(true);
		// Draw the box
		GL46.glVertex2d(0.0, height); // Top left
		GL46.glVertex2d(width, height); // Top right
		GL46.glVertex2d(width, 0.0); // Bottom right
		GL46.glVertex2d(0.0, 0.0); // Bottom left
		setGLProps(false);

		// Loop through the lines of the button and draw them
		float y = PADDING_Y;
		boolean header = true;
		for (ITextComponent component : text) {
			// Different matrix for scaling
			GL46.glPushMatrix();
			// Center
			float x = (width - minecraft.fontRenderer.getStringPropertyWidth(component)) / 2;
			if (!header) {
				// Scale down for info
				RenderSystem.scaled(INFO_SCALE, INFO_SCALE, INFO_SCALE);
				x = x + x * (INFO_SCALE / 2); // Re-center
			}
			// Component render
			minecraft.fontRenderer.func_243246_a(matrixStack, component, x, y, this.textColor.getRGB());
			// Calculate next line's height
			float textHeight = minecraft.fontRenderer.FONT_HEIGHT;
			// Larger height
			if (header) {
				textHeight *= BOX_SCALE;
			}
			y = y + textHeight + PADDING_Y;
			GL46.glPopMatrix();
			// TODO: Multiple headers?
			header = false;
		}
	}

	// BEGIN COLOR METHODS
	// These are all separate for easy reference in the settings class

	public int getBgRed() {
		return bgRed;
	}

	public void setBgRed(int bgRed) {
		this.bgRed = bgRed;
	}

	public int getBgGreen() {
		return bgGreen;
	}

	public void setBgGreen(int bgGreen) {
		this.bgGreen = bgGreen;
	}

	public int getBgBlue() {
		return bgBlue;
	}

	public void setBgBlue(int bgBlue) {
		this.bgBlue = bgBlue;
	}

	public int getTextRed() {
		return this.textColor.getRed();
	}

	public void setTextRed(int red) {
		this.textColor = new Color(red, this.textColor.getGreen(), this.textColor.getBlue());
	}

	public int getTextGreen() {
		return this.textColor.getGreen();
	}

	public void setTextGreen(int green) {
		this.textColor = new Color(this.textColor.getRed(), green, this.textColor.getBlue());
	}

	public int getTextBlue() {
		return this.textColor.getBlue();
	}

	public void setTextBlue(int blue) {
		this.textColor = new Color(this.textColor.getRed(), this.textColor.getGreen(), blue);
	}

	// END COLOR METHODS

	/**
	 * Creates config spec used for loading.saving colors
	 */
	public void createSpec(ForgeConfigSpec.Builder builder) {
		builder.push("colors");
		builder.define("background", new Color(this.bgRed, this.bgGreen, this.bgBlue).getRGB());
		builder.define("text", this.textColor.getRGB());
		builder.pop();
	}

	/**
	 * Save colors to config
	 */
	public void saveDate(CommentedConfig config) {
		config.set("colors.background", new Color(this.bgRed, this.bgGreen, this.bgBlue).getRGB());
		config.set("colors.text", this.textColor.getRGB());
	}

	/**
	 * Load colors from config
	 */
	public void loadData(CommentedConfig config) {
		if (config.contains("colors.background")) {
			Color bgColor = new Color(config.get("colors.background"));
			this.bgRed = bgColor.getRed();
			this.bgGreen = bgColor.getGreen();
			this.bgBlue = bgColor.getBlue();
		}

		if (config.contains("colors.text")) {
			this.textColor = new Color(config.get("colors.text"));
		}
	}
}
