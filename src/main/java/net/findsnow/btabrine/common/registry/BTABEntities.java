package net.findsnow.btabrine.common.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.findsnow.btabrine.BTABrine;
import net.findsnow.btabrine.client.model.HerobrineModel;
import net.findsnow.btabrine.client.renderer.HerobrineRenderer;
import net.findsnow.btabrine.common.entity.HerobrineEntity;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.ModelHelper;

public class BTABEntities {
	public static int entityID = 300;

    public static void registerEntities() {
	    EntityHelper.createEntity(
		    HerobrineEntity.class,
		    NamespaceID.getPermanent(BTABrine.MOD_ID, "herobrine"),
		    "entity.btabrine.herobrine",
		    "Herobrine",
		    entityID++
	    );
    }

	@Environment(EnvType.CLIENT)
	public static void registerRenderers() {
		ModelHelper.setEntityModel(HerobrineEntity.class,  () -> new HerobrineRenderer(new HerobrineModel(0), new HerobrineModel(0), 1));
	}
}
