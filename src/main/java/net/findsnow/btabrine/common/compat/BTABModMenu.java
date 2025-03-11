package net.findsnow.btabrine.common.compat;

import io.github.prospector.modmenu.api.ModMenuApi;
import io.github.prospector.modmenu.util.TriConsumer;
import net.findsnow.btabrine.client.screens.BTABConfigScreen;
import net.findsnow.btabrine.common.util.BTABrineConfig;
import net.minecraft.client.gui.Screen;

import java.util.function.Function;

public class BTABModMenu implements ModMenuApi {

	@Override
	public String getModId() {
		return "btabrine";
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return BTABConfigScreen::new;
	}

	@Override
	public void attachCustomBadges(TriConsumer<String, Integer, Integer> consumer) {
		ModMenuApi.super.attachCustomBadges(consumer);
	}
 }
