package me.austinlm.keystrokes;

import com.electronwill.nightconfig.core.CommentedConfig;
import javax.annotation.Nullable;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * X,Y vector which keeps a constant position on screen regardless of window scale.
 *
 * Implementations must call {@link #setScreenResolution(int, int)} every time the resolution
 * changes for this to work properly.
 */
public class ScaleSafeVector {

	// Base location of the main keys
	public static ScaleSafeVector TOP_LEFT = new ScaleSafeVector(0.85, 0.08);
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private double x;
	private double y;

	public ScaleSafeVector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public ScaleSafeVector(ScaleSafeVector vector) {
		this.x = vector.x;
		this.y = vector.y;
	}

	/**
	 * Create a config spec with information that {@link #fromConfig(CommentedConfig,
	 * ScaleSafeVector)} expects to see
	 */
	public static void defineConfigSpec(ForgeConfigSpec.Builder builder, ScaleSafeVector def) {
		builder.push("location");
		builder.defineInRange("x", def == null ? 0.5 : def.x, 0, 1);
		builder.defineInRange("y", def == null ? 0.5 : def.y, 0, 1);
		builder.pop();
	}

	/**
	 * Parse a vector from the supplied configuration section. If the config does not contain data,
	 * the default value will be returned.
	 */
	public static ScaleSafeVector fromConfig(CommentedConfig config,
			@Nullable ScaleSafeVector defaultVec) {
		if (!config.contains("x") && !config.contains("y") && defaultVec == null) {
			return null;
		}
		double x = config.contains("x") ? config.get("x") : defaultVec.x;
		double y = config.contains("y") ? config.get("y") : defaultVec.x;
		return new ScaleSafeVector(x, y);
	}

	public static void setScreenResolution(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}

	/**
	 * @return X coordinate based on screen resolution
	 */
	public double getX() {
		return Math.round(screenWidth * this.x);
	}

	/**
	 * @return Y coordinate based on screen resolution
	 */
	public double getY() {
		return Math.round(screenHeight * this.y);
	}

	public double getRawX() {
		return this.x;
	}

	public double getRawY() {
		return this.y;
	}

	public ScaleSafeVector add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * {@link #add(double, double)} but on a new instance
	 */
	public ScaleSafeVector addCopy(double x, double y) {
		ScaleSafeVector copy = new ScaleSafeVector(this);
		return copy.add(x, y);
	}

	public void relative(double x, double y) {
		add(x / screenWidth, y / screenHeight);
	}
}
