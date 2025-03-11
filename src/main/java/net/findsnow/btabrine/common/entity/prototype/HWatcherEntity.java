package net.findsnow.btabrine.common.entity.prototype;

import net.findsnow.btabrine.common.entity.base.HerobrineBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class HWatcherEntity extends HerobrineBase {

	private static final float SPAWN_CHANCE = 0.05F;
	private static final float IMMEDIATE_DESPAWN_DISTANCE = 1.0F;
	private static final float STALK_DISTANCE = 15.0F;
	private static final float WINDOW_CHECK_RANGE = 15.0F;

	private boolean isFleeing = false;
	private int lifespan = 1200;
	private Player targetPlayer;
	private int ticksExisted = 0;
	private Random random = new Random();

	private Vec3 lastPos;

	public HWatcherEntity(@Nullable World world) {
		super(world);
		this.isJumping = false;
		this.moveSpeed = 0;
		this.lastPos = Vec3.getTempVec3(this.x, this.y, this.z);
	}

	@Override
	public void tick() {
		super.tick();
		ticksExisted++;

		lastPos = Vec3.getTempVec3(this.x, this.y, this.z);

		if (lifespan > 0) {
			lifespan--;
			if (lifespan <= 0) {
				placeRedstoneTorch();
				this.remove();
				System.out.println("Stalking Herobrine despawned due to lifespan");
				return;
			}
		}

		// start finding a player if one isn't present
		if (targetPlayer == null || !targetPlayer.isAlive()) {
			findNearestPlayer();
		}

		if (targetPlayer != null) {
			// Track the player's position for potential teleporting later
			faceEntity(targetPlayer, 360.0F, 360.0F);

			faceEntity(targetPlayer, 360.0F, 360.0F);
			float distanceToPlayer = getDistanceTo(targetPlayer);

			if (ticksExisted % 20 == 0) {
				lastPos = Vec3.getTempVec3(this.x, this.y, this.z);

				if (entityDetector.isNightTime(world)) {
					if (entityDetector.checkForWindowsNearPlayer(targetPlayer, world)) {
						tryPositionAtWindow();
					}
				}
			}
			if (distanceToPlayer < (STALK_DISTANCE - IMMEDIATE_DESPAWN_DISTANCE) + 1.7F) {
				placeRedstoneTorch();
				remove();
				System.out.println("Herobrine has despawned!");
			} else {
				moveSpeed = 0.0F;
			}
		}
	}

	private void tryPositionAtWindow() {
		entityDetector.findWindowPos(targetPlayer, world, WINDOW_CHECK_RANGE, this);
	}

	private float getDistanceTo(Player targetPlayer) {
		float dx = (float) (this.x - targetPlayer.x);
		float dy = (float) (this.y - targetPlayer.y);
		float dz = (float) (this.z - targetPlayer.z);
		return MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
	}

	private void faceEntity(Player targetPlayer, float v, float v1) {
		if (targetPlayer != null && !targetPlayer.removed) {
			double deltaX = targetPlayer.x - this.x;
			double deltaY = (targetPlayer.y + targetPlayer.getHeadHeight()) - (this.y + this.getHeadHeight());
			double deltaZ = targetPlayer.z - this.z;

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

	private void placeRedstoneTorch() {
		if (random.nextInt(100) < 70) {
			if (world != null) {
				int torchX = MathHelper.floor(lastPos.x);
				int torchY = MathHelper.floor(lastPos.y);
				int torchZ = MathHelper.floor(lastPos.z);

				while (torchY > 0 && world.getBlockId(torchX, torchY - 1, torchZ) == 0) {
					torchY--;
				}

				int blockBelow = world.getBlockId(torchX, torchY - 1, torchZ);
				if (blockBelow != 0 && world.getBlockId(torchX, torchY, torchZ) == 0) {
					world.setBlockWithNotify(torchX, torchY, torchZ, Blocks.TORCH_REDSTONE_ACTIVE.id());
					System.out.println("Placed redstone torch at " + torchX + ", " + torchY + ", " + torchZ);
				} else {
					System.out.println("Failed to place torch: invalid position");
				}
			}
		}
	}

	private void findNearestPlayer() {
		if (world != null && world.players != null) {
			Player nearestPlayer = null;
			double nearestDistance = Double.MAX_VALUE;
			double searchRadius = 64.0;

			for (Player player : world.players) {
				double distance = getDistanceTo(player);
				if (distance <= searchRadius && distance < nearestDistance) {
					nearestPlayer = player;
					nearestDistance = distance;
				}
			}
			targetPlayer = nearestPlayer;
		}
	}

	public static boolean trySpawn(World world, Player player) {
		Random random = new Random();
		if (random.nextFloat() > SPAWN_CHANCE) {
			return false;
		}

		HWatcherEntity hStalkingEntity = new HWatcherEntity(world);

		boolean isNight = hStalkingEntity.entityDetector.isNightTime(world);
		boolean playerInCave = hStalkingEntity.entityDetector.isPlayerUnderground(player, world);
		boolean playerNearHome = hStalkingEntity.entityDetector.isPlayerNearHome(player, world);
		boolean playerInWild = hStalkingEntity.entityDetector.isPLayerInWild(player, world);

		if (!isNight && (!playerInCave || !playerNearHome && !playerInWild)) {
			return false;
		}

		if (hasExistingHerobrineStalker(world)) {
			return false;
		}

		int tryCount = 0;
		while (tryCount < 10) {
			double angle = random.nextDouble() * Math.PI * 2;
			double distance = 10.0 + random.nextDouble() * 10.0;

			int spawnX = (int) (player.x + Math.sin(angle) * distance);
			int spawnZ = (int) (player.z + Math.cos(angle) * distance);

			int spawnY = -1;
			for (int y = (int)player.y + 5; y > (int)player.y - 10; y--) {
				if (world.getBlockId(spawnX, y, spawnZ) != 0 &&
					world.getBlockId(spawnX, y + 1, spawnZ) == 0 &&
					world.getBlockId(spawnX, y + 2, spawnZ) == 0) {
					spawnY = y + 1;
					break;
				}
			}
			if (spawnY != -1) {
				HWatcherEntity herobrine = new HWatcherEntity(world);
				herobrine.setPos(spawnX + 0.5, spawnY, spawnZ + 0.5);
				herobrine.targetPlayer = player;
				world.entityJoinedWorld(herobrine);
				System.out.println("Stalker Herobrine is now spawned at " + spawnX + ", " + spawnY + ", " + spawnZ);
				return true;
			}

			tryCount++;
		}
		return false;
	}

	public static boolean hasExistingHerobrineStalker(World world) {
		List<Entity> entities = world.getLoadedEntityList();
		for (Entity entity : entities) {
			if (entity instanceof HWatcherEntity && !entity.removed) {
				return true;
			}
		}
		return false;
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
