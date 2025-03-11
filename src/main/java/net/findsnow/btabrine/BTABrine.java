package net.findsnow.btabrine;

import net.fabricmc.api.ModInitializer;
import net.findsnow.btabrine.common.compat.BTABModMenu;
import net.findsnow.btabrine.common.registry.BTABAchievements;
import net.findsnow.btabrine.common.registry.BTABEntities;
import net.findsnow.btabrine.common.util.BTABrineConfig;
import net.minecraft.client.sound.SoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class BTABrine implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
    public static final String MOD_ID = "btabrine";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("BTABrine initialized.");
	    BTABrineConfig.loadConfig();
    }

	@Override
	public void onRecipesReady() {
	}

	@Override
	public void initNamespaces() {
	}

	@Override
	public void beforeGameStart() {
		BTABEntities.registerEntities();
		SoundRepository.registerNamespace("btabrine");
	}

	@Override
	public void afterGameStart() {
		BTABEntities.registerRenderers();
		BTABAchievements.registerAchievements();
	}
}
