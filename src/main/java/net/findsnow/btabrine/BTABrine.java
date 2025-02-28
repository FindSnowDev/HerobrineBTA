package net.findsnow.btabrine;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.findsnow.btabrine.common.entity.HerobrineEntity;
import net.findsnow.btabrine.common.registry.BTABEntities;
import net.minecraft.client.sound.SoundRepository;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.sound.SoundTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.SoundHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import static net.minecraft.core.sound.SoundTypes.loadSoundsJson;
import static net.minecraft.core.sound.SoundTypes.register;


public class BTABrine implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
    public static final String MOD_ID = "btabrine";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("BTABrine initialized.");

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
	}
}
