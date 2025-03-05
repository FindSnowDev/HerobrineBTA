package net.findsnow.btabrine.client.renderer;

import net.findsnow.btabrine.client.model.HerobrineNightmareModel;
import net.findsnow.btabrine.common.entity.HerobrineNightmareEntity;
import net.findsnow.btabrine.common.entity.HerobrineStalkingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.client.render.tessellator.Tessellator;
import org.lwjgl.opengl.GL11;

public class HerobrineNightmareRenderer extends MobRenderer<HerobrineNightmareEntity> {

	public HerobrineNightmareRenderer(HerobrineNightmareModel model, float shadowSize) {
		super(model, shadowSize);
		this.shadowSize = 0.5F;
		this.setArmorModel(model);
		this.renderDispatcher = EntityRenderDispatcher.instance;
	}

	@Override
	public void render(Tessellator tessellator, HerobrineNightmareEntity entity, double x, double y, double z, float yaw, float partialTick) {
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		super.render(tessellator, entity, x, y, z, yaw, partialTick);
		GL11.glDisable(3042);
	}

	private boolean setEyeBrightness(HerobrineNightmareEntity herobrineEntity, int i, float f) {
		if (i == 0) {
			this.bindTexture("/assets/btabrine/textures/entity/herobrine/herobrine_eyes.png");
			float brightness = herobrineEntity.getBrightness(1.0F);
			if (Minecraft.getMinecraft().fullbright)
				brightness = 1.0F;
			if (LightmapHelper.isLightmapEnabled()) {
				LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
			}
			float f1 = (1.0F - brightness) * 0.2F;
			GL11.glEnable(3042);
			GL11.glDisable(3008);
			GL11.glBlendFunc(770, 771);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, f1  + 1.0F);
			return true;
		} else
			return false;
	}

	@Override
	protected boolean prepareArmor(HerobrineNightmareEntity entity, int layer, float partialTick) {
		return setEyeBrightness(entity, layer, partialTick);
	}

	@Override
	protected void setupScale(HerobrineNightmareEntity entity, float partialTick) {
		float scale = 0.9375F;
		GL11.glScalef(scale, scale, scale);
	}

	@Override
	protected float getMaxDeathRotation(HerobrineNightmareEntity entity) {
		return 90.0F;
	}

	@Override
	protected void bindTexture(String texturePath) {
		super.bindTexture(texturePath);
	}
}
