package net.findsnow.btabrine.common.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.findsnow.btabrine.BTABrine;
import net.findsnow.btabrine.client.model.HerobrineNightmareModel;
import net.findsnow.btabrine.client.model.HerobrineStalkerModel;
import net.findsnow.btabrine.client.model.HerobrineWanderModel;
import net.findsnow.btabrine.client.renderer.HerobrineNightmareRenderer;
import net.findsnow.btabrine.client.renderer.HerobrineStalkerRenderer;
import net.findsnow.btabrine.client.renderer.HerobrineWanderRenderer;
import net.findsnow.btabrine.common.entity.HerobrineNightmareEntity;
import net.findsnow.btabrine.common.entity.HerobrineStalkingEntity;
import net.findsnow.btabrine.common.entity.HerobrineWanderingEntity;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.ModelHelper;

public class BTABEntities {
	public static int entityID = 300;

    public static void registerEntities() {
		// NIGHTMARE HEROBRINE
	    EntityHelper.createEntity(
		    HerobrineNightmareEntity.class,
		    NamespaceID.getPermanent(BTABrine.MOD_ID, "herobrineNightmare"),
		    "entity.btabrine.herobrine_nightmare",
		    "HerobrineNightmare",
		    entityID++
	    );
	    // STALKER HEROBRINE
	    EntityHelper.createEntity(
		    HerobrineStalkingEntity.class,
		    NamespaceID.getPermanent(BTABrine.MOD_ID, "herobrineStalker"),
		    "entity.btabrine.herobrine_stalker",
		    "herobrineStalker",
		    entityID++
	    );
	    // WANDER HEROBRINE
	    EntityHelper.createEntity(
		    HerobrineWanderingEntity.class,
		    NamespaceID.getPermanent(BTABrine.MOD_ID, "herobrineWanderer"),
		    "entity.btabrine.herobrine_wanderer",
		    "herobrineWanderer",
		    entityID++
	    );
    }

	@Environment(EnvType.CLIENT)
	public static void registerRenderers() {
		// NEW
		ModelHelper.setEntityModel(HerobrineNightmareEntity.class,  () -> new HerobrineNightmareRenderer(new HerobrineNightmareModel(0), 1));
		ModelHelper.setEntityModel(HerobrineStalkingEntity.class,  () -> new HerobrineStalkerRenderer(new HerobrineStalkerModel(0), 1));
		ModelHelper.setEntityModel(HerobrineWanderingEntity.class,  () -> new HerobrineWanderRenderer(new HerobrineWanderModel(0), 1));
	}
}
