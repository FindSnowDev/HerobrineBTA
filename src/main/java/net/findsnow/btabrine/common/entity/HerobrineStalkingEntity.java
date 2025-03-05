package net.findsnow.btabrine.common.entity;

import net.findsnow.btabrine.common.entity.base.HerobrineBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldTypes;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Random;

public class HerobrineStalkingEntity extends HerobrineBase {

	private static final float SPAWN_CHANCE = 0.05F;
	private static final float IMMEDIATE_DESPAWN_DISTANCE = 1.0F;
	private static final float STALK_DISTANCE = 15.0F;
	private static final float WINDOW_CHECK_RANGE = 15.0F;

	private int lifespan = 1200;
	private Player targetPlayer;
	private boolean isFleeing = false;
	private int ticksExisted = 0;
	private Random random = new Random();
	private Vec3 lastPos;

	public HerobrineStalkingEntity(@Nullable World world) {
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
			faceEntity(targetPlayer, 360.0F, 360.0F);
			float distanceToPlayer = getDistanceTo(targetPlayer);

			if (ticksExisted % 20 == 0) {
				lastPos = Vec3.getTempVec3(this.x, this.y, this.z);

				if (world.getWorldTime() % 24000 > 13000 && world.getWorldTime() % 24000 < 23000) {
					checkForWindowsAndHomes();
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

	private void checkForWindowsAndHomes() {
		if (random.nextInt(100) != 0) return;

		int blockCount = 0;
		int totalChecks = 0;

		for (int x = -3; x <= 3; x++) {
			for (int y = -1; y <= 3; y++){
				for (int z = -3; z <= 3; z++) {
					int blockId = world.getBlockId(
						(int) targetPlayer.x + x,
						(int) targetPlayer.y + y,
						(int) targetPlayer.z + z
					);
					if (blockId != 0) {
						blockCount++;
					}
					totalChecks++;
				}
			}
		}
		if ((double) blockCount / totalChecks > 0.5) {
			tryPositionAtWindow();
		}
	}

	private void tryPositionAtWindow() {
		for (int range = 5; range <= WINDOW_CHECK_RANGE; range += 5) {
			for (int i = 0; i < 10; i++) {
				double angle = random.nextDouble() * Math.PI * 2;
				int checkX = (int) (targetPlayer.x + Math.sin(angle) * range);
				int checkZ = (int) (targetPlayer.z + Math.cos(angle) * range);

				int groundY = findGroundLevel(checkX, checkZ);
				if (groundY > 0) {
					for (int y = groundY; y < groundY + 8; y++) {
						if (isWindowBlock(checkX, y, checkZ)) {
							setPos(checkX + 0.5, y + 0.5, checkZ + 0.5);
							moveSpeed = 0;
							isFleeing = false;
							return;
						}
					}
				}
			}
		}
	}

	private boolean isWindowBlock(int checkX, int checkY, int checkZ) {
		int blockId = world.getBlockId(checkX, checkY, checkZ);
		return blockId == Blocks.GLASS.id() || blockId == Blocks.GLASS_TINTED.id();
	}

	private int findGroundLevel(int checkX, int checkZ) {
		for (int y = (int) targetPlayer.y + 5; y > (int) targetPlayer.y - 10; y--) {
			if (world.getBlockId(checkX, y, checkZ) != 0 && world.getBlockId(checkX, y + 1, checkZ) == 0) {
				return y + 1;
			}
		}
		return -1;
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

		boolean isNight = world.getWorldTime() % 24000 > 13000 && world.getWorldTime() % 24000 < 23000;
		boolean playerInCave = isPlayerUnderground(player, world);
		boolean playerNearHome = isPlayerNearHome(player, world);

		if (!isNight && !playerInCave) {
			return false;
		}

		if (!playerInCave && !playerNearHome) {
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
				HerobrineStalkingEntity herobrine = new HerobrineStalkingEntity(world);
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
			if (entity instanceof HerobrineStalkingEntity && !entity.removed) {
				return true;
			}
		}
		return false;
	}

	private static boolean isPlayerNearHome(Player player, World world) {
		int homeRadius = 32;
		int homeBlockCount = 0;

		for (int x = -homeRadius; x <= homeRadius; x++) {
			for (int y = -2; y <= 4; y++) {
				for (int z = -homeRadius; z <= homeRadius; z++) {
					int blockId = world.getBlockId(
						(int) player.x + x,
						(int) player.y + y,
						(int) player.z + z
					);
					if (blockId != 0 && blockId != Blocks.GRASS.id() && blockId != Blocks.DIRT.id()) {
						homeBlockCount++;
					}
				}
			}
		}
		return homeBlockCount > 50;
	}

	public static boolean isPlayerUnderground(Player player, World world) {
		boolean canSeeSky = world.canBlockSeeTheSky((int)player.x, (int)player.y, (int)player.z);

		if (world.getWorldType().equals(WorldTypes.OVERWORLD_EXTENDED) && !canSeeSky && player.y < 130) {
			return true;
		} else if (world.getWorldType().equals(WorldTypes.OVERWORLD_DEFAULT) && !canSeeSky && player.y < 50) {
			return true;
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
