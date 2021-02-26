package me.austinlm.keystrokes.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.StringTextComponent;

/**
 * A hacky implementation of {@link SliderPercentageOption} which hides all of the underlying
 * settings logic and accepts integers.
 */
public class ColorSlider extends SliderPercentageOption {

	public ColorSlider(String baseText, Supplier<Integer> getter, Consumer<Integer> setter) {
		super(null, 0, 255, 0, (s) -> getter.get().doubleValue(), (s, v) -> setter.accept(v.intValue()),
				(s, o) -> new StringTextComponent(baseText + ": " + getter.get() + ""));
	}
}
