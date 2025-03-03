package net.findsnow.btabrine.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.findsnow.btabrine.client.model.HerobrineModel;
import net.findsnow.btabrine.common.entity.HerobrineEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.tessellator.Tessellator;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class HerobrineRenderer extends MobRenderer<HerobrineEntity> {

    public HerobrineRenderer(HerobrineModel model, HerobrineModel model1, float shadow) {
	    super(model, shadow);
		this.shadowSize = 0.5F;
		this.setArmorModel(model1);
		this.renderDispatcher = EntityRenderDispatcher.instance;
    }

	@Override
    public void render(Tessellator tessellator, HerobrineEntity entity, double x, double y, double z, float yaw, float partialTick) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        super.render(tessellator, entity, x, y, z, yaw, partialTick);
        GL11.glDisable(3042);
    }

	private boolean setEyeBrightness(HerobrineEntity herobrineEntity, int i, float f) {
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
	protected boolean prepareArmor(HerobrineEntity entity, int layer, float partialTick) {
		return setEyeBrightness(entity, layer, partialTick);
	}

	@Override
    protected void setupScale(HerobrineEntity entity, float partialTick) {
        float scale = 0.9375F;
        GL11.glScalef(scale, scale, scale);
    }

    @Override
    protected float getMaxDeathRotation(HerobrineEntity entity) {
        return 90.0F;
    }

	@Override
	protected void bindTexture(String texturePath) {
		super.bindTexture(texturePath);
	}
}
