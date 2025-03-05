package net.findsnow.btabrine.common.entity;

import net.findsnow.btabrine.common.entity.base.HerobrineDreamBase;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class HerobrineNightmareEntity extends HerobrineDreamBase {
	private Entity targetEntity;
	private int lifespan;
	private float targetYaw;
	private float targetPitch;
	private boolean shouldLookAtTarget = true;

	public HerobrineNightmareEntity(@Nullable World world) {
		super(world);
		this.shouldLookAtTarget(shouldLookAtTarget);
	}

	public void setWatchTarget(Entity entity) {
		this.targetEntity = entity;
	}

	public void setLifespan(int ticks) {
		this.lifespan = ticks;
	}

	@Override
	public void tick() {
		super.tick();

		if (lifespan > 0) {
			lifespan--;
			if (lifespan <= 0) {
				this.remove();
				return;
			}
		}

		if (targetEntity != null && !targetEntity.removed) {
			double deltaX = targetEntity.x - this.x;
			double deltaY = (targetEntity.y + targetEntity.getHeadHeight()) - (this.y + this.getHeadHeight());
			double deltaZ = targetEntity.z - this.z;

			double distance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
			float yaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
			float pitch = (float) -(Math.atan2(deltaY, distance) * 180.0D / Math.PI);

			this.yRot = yaw;
			this.xRot = pitch;

			this.yRotO = this.yRot;
			this.xRotO = this.xRot;
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	public void shouldLookAtTarget(boolean lookAtTarget) {
		this.shouldLookAtTarget = lookAtTarget;
	}
}
