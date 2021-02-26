package me.austinlm.keystrokes.hud;

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

		float color = key.getType().getBinding().isKeyDown() ? 0.4f : 0.2f;
		// Move to the key's locaion
		GL46.glTranslated(key.getLocation().getX(), key.getLocation().getY(), 0.0);
		// background color
		GL46.glColor4f(color, color, color, 0.6f);
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
			minecraft.fontRenderer.func_243246_a(matrixStack, component, x, y, Color.GREEN.getRGB());
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
}
