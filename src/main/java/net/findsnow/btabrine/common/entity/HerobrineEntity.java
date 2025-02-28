package net.findsnow.btabrine.common.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HerobrineEntity extends MobMonster {
	private Player player;
	private int stalkingCooldown;
	private int stalkingTimeRemaining = 0;

	private static final int STALKING_RANGE = 32;
	private static final int MIN_DIST = 12;
	private static float MOVEMENT_SPEED;

	public HerobrineEntity(@Nullable World world) {
		super(world);
		this.textureIdentifier = NamespaceID.getPermanent("btabrine", "herobrine");
		this.moveSpeed = MOVEMENT_SPEED = 0.0F;
		this.stalkingCooldown = 0;
		this.avoidWaterAndLava();
		this.roamRandomPath();
		this.fireImmune = true;
	}


	// Data
	@Override
	protected void defineSynchedData() {
		this.entityData.define(20, (byte) 1, Byte.class); // For glowing eyes
		this.entityData.define(21, (byte) 0, Byte.class); // For stalking
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("Eyes", this.getEyesGlow());
		tag.putInt("StalkingCooldown", this.stalkingCooldown);
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setEyesGlow(tag.getBoolean("Eyes"));
		this.stalkingCooldown = tag.getInteger("StalkingCooldown");
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	// Glowing Eyes
	public boolean getEyesGlow() {
		return (this.entityData.getByte(20) & 1) != 0;
	}

	public void setEyesGlow(boolean flag) {
		if (flag) {
			this.entityData.set(20, (byte) 1);
		} else {
			this.entityData.set(20, (byte) 0);
		}
	}

	public boolean isBeingWatched() {
		return (this.entityData.getByte(21) & 1) != 0;
	}

	public void setBeingWatched(boolean flag) {
		this.entityData.set(21, (byte) (flag ? 1 : 0));
	}

	// AI & Movement
	@Override
	public void tick() {
		super.tick();

		if (this.stalkingCooldown > 0) {
			this.stalkingCooldown--;
		}

		this.player = this.world.getClosestPlayerToEntity(this, STALKING_RANGE);
		this.moveSpeed = isBeingWatched() ? 0.0F : MOVEMENT_SPEED;

		if (player != null && isPlayerLookingAt(player)) {
			runAwayFromPlayer();
		}

		updateStalkingBehavior();
	}

	private void updateStalkingBehavior() {
		if (stalkingCooldown > 0 || player == null) {
			return;
		}

		if (!isBeingWatched() && this.random.nextInt(100) == 10) {
			Vec3 playerLookDirection = player.getViewVector(1.0F).normalize();
			double stalkingDist = MIN_DIST + this.random.nextDouble() * 5.0;

			double posX = player.x - playerLookDirection.x * stalkingDist;
			double posY = player.y;
			double posZ = player.z - playerLookDirection.z * stalkingDist;

			if (isSafeLocation(posX, posY, posZ)) {
				this.pathToEntity = this.world.getEntityPathToXYZ(
					this,
					(int) posX,
					(int) posY,
					(int) posZ,
					16.0F
				);
				this.setBeingWatched(true);
				this.stalkingTimeRemaining = 800;
				System.out.println("Herobrine has begun stalking at " + posX + ", " + posY);
			}
		}

		if (isBeingWatched()) {
			double deltaX = player.x - this.x;
			double deltaZ = player.z - this.z;
			this.yRot = (float) (Math.atan2(deltaZ, deltaX) * 180 / Math.PI) - 90.0F;

			if (isPlayerLookingAt(player)) {
				this.pathToEntity = null;
				this.xd = 0;
				this.yd = 0;
				this.zd = 0;
			}

			if (stalkingTimeRemaining > 0) {
				stalkingTimeRemaining--;
			}
			if (stalkingTimeRemaining <= 0) {
				disappear();
			}
		}
	}

	private boolean isPlayerLookingAt(Player player) {
		if (player == null) {
			return false;
		}
		Vec3 playerLookDirection = player.getViewVector(1.0f).normalize();
		Vec3 entityToPlayerDirection = Vec3.getTempVec3(
			this.x - player.x,
			this.bb.minY + (double)this.bbHeight - player.y + (double)player.cameraPitch,
			this.z - player.z);
		entityToPlayerDirection = entityToPlayerDirection.normalize();
		double angleBetweenDirections = entityToPlayerDirection.dotProduct(playerLookDirection);
		double entityToPlayerDistance = entityToPlayerDirection.length();
		double thresholdAngles = 1.0D - 0.025D / entityToPlayerDistance;
		return angleBetweenDirections > thresholdAngles && player.canEntityBeSeen(this);
	}

	private void runAwayFromPlayer() {
		if (isBeingWatched()) {
			disappear();
		}
	}

	private void disappear() {
		System.out.println("Herobrine disappeared!");
		this.stalkingCooldown = 3600;
		this.setBeingWatched(false);
		world.playSoundAtEntity(null, this, "btabrine:mob.herobrine.vanish", 1.0F, 1.0F);
		this.remove();
	}

	private void avoidWaterAndLava() {
		if (this.world != null && this.pathToEntity != null && !this.pathToEntity.isDone()) {
			Vec3 nextPos = this.pathToEntity.getPos(this);
			int nextX = (int)nextPos.x;
			int nextY = (int)nextPos.y;
			int nextZ = (int)nextPos.z;

			if (this.world.getBlockMaterial(nextX, nextY, nextZ).isLiquid() || this.world.getBlockMaterial(nextX, nextY -1, nextZ).isLiquid()) {
				this.pathToEntity = null;
			}
		}
	}

	@Override
	protected void roamRandomPath() {
		super.roamRandomPath();
		if (this.world != null) {
			boolean canMoveToPoint = false;
			int x = -1;
			int y = -1;
			int z = -1;
			float bestPathWeight = -9999.0F;

			for (int l = 0; l < 10l; l++) {
				int x1 = MathHelper.floor(this.x + (double)this.random.nextInt(13) - 6.0);
				int y1 = MathHelper.floor(this.y + (double)this.random.nextInt(7) - 3.0);
				int z1 = MathHelper.floor(this.z + (double)this.random.nextInt(13) - 6.0);

				if (isNearLiquid(x1, y1, z1)) {
					continue;
				}

				float currentPathWeight = this.getBlockPathWeight(x1, y1, z1);
				if (currentPathWeight > bestPathWeight) {
					bestPathWeight = currentPathWeight;
					x = x1;
					y = y1;
					z = z1;
					canMoveToPoint = true;
				}
			}

            if (canMoveToPoint) {
                this.pathToEntity = this.world.getEntityPathToXYZ(this, x, y, z, 10.0F);
            }
		}
	}

	private boolean isNearLiquid(int x, int y, int z) {
		if (this.world.getBlockMaterial(x, y, z).isLiquid() || this.world.getBlockMaterial(x, y -1, z).isLiquid()){
			return true;
		}
		if (this.world.getBlockMaterial(x + 1, y, z).isLiquid() ||
			this.world.getBlockMaterial(x -1, y, z).isLiquid() ||
			this.world.getBlockMaterial(x, y, z +1).isLiquid() ||
			this.world.getBlockMaterial(x, y, z -1).isLiquid()) {
			return true;
		}
		return false;
	}

	@Override
	protected float getBlockPathWeight(int x, int y, int z) {
		float weight = super.getBlockPathWeight(x, y, z);
		if (this.world.getBlockMaterial(x, y, z).isLiquid() ||
		this.world.getBlockMaterial(x, y -1, z).isLiquid()) {
			weight = -1000.0F;
		}
		return weight;
	}

	private boolean isSafeLocation(double posX, double posY, double posZ) {
		if (!this.world.getBlockMaterial((int)posX, (int)posY -1, (int)posZ).isSolid()) {
			return false;
		}
		if (!this.world.isAirBlock((int)posX, (int)posY, (int)posZ) || !this.world.isAirBlock((int)posX, (int)posY + 1, (int)posZ)) {
			return false;
		}
		if (this.world.getBlockMaterial((int)posX, (int)posY, (int)posZ).isLiquid() || this.world.getBlockMaterial((int)posX, (int)posY + 1, (int)posZ).isLiquid()) {
			return false;
		}
		return true;
	}

	@Override
	protected String getHurtSound() {
		return null;
	}

	@Override
	public void knockBack(Entity entity, int i, double d, double d1) {
	}

	@Override
	protected String getDeathSound() {
		return null;
	}
}
