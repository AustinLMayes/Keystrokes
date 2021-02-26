package me.austinlm.keystrokes.hud;

import me.austinlm.keystrokes.ScaleSafeVector;
import net.minecraft.util.text.ITextComponent;

/**
 * A single key button
 */
public class Key {

	private final KeyType type;
	private ScaleSafeVector location;
	private float width;
	private float height;
	private ITextComponent cachedText;
	private boolean shown;

	public Key(KeyType type) {
		this.type = type;
		this.location = type.getDefaultLocation();
		this.shown = location != null;
	}

	public void flushCache() {
		this.cachedText = null;
	}

	public ITextComponent getText() {
		if (cachedText == null) {
			cachedText = type.getBinding().func_238171_j_();
		}

		return cachedText;
	}

	public ScaleSafeVector getLocation() {
		return location;
	}

	public void setLocation(ScaleSafeVector location) {
		this.location = location;
	}

	public KeyType getType() {
		return type;
	}

	/**
	 * Set drawn dimensions of the box. Used for drag hover check.
	 */
	public void setDimensions(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public boolean isInside(double x, double y) {
		return (x > this.location.getX() && x < this.location.getX() + width) &&
				(y > this.location.getY() && y < this.location.getY() + height);
	}

	/**
	 * If the key should be shown. Will still not be shown if no location is set.
	 */
	public void setShown(boolean shown) {
		this.shown = shown;
	}

	public boolean shouldDraw() {
		return shown && this.location != null;
	}
}
