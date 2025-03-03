package net.findsnow.btabrine.common.entity;

import net.minecraft.core.block.BlockLogicLayerBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class HerobrineStalkingEntity extends HerobrineEntity {

	private static final float SPAWN_CHANCE = 1.0f; // change this to something rare once im done testing
	private static final float FLEE_DISTANCE = 10.0F; // distance at which he runs away
	private static final float DESPAWN_DISTANCE = 16.0F; // distance at which he can de-spawn
	private static final float STALK_DISTANCE = 30.0F; // distance to maintain when stalking
	private static final float WINDOW_CHECK_RANGE = 30.0F; // distance to maintain when stalking windows

	private Player targetPlayer;
	private boolean isFleeing = false;
	private int ticksExisted = 0;
	private Random random = new Random();
	private Vec3 lastPos;

	public HerobrineStalkingEntity(@Nullable World world) {
		super(world);
		this.textureIdentifier = NamespaceID.getPermanent("btabrine", "herobrine");
		this.moveSpeed = 0.0F;
		this.roamRandomPath();
		this.fireImmune = true;
	}

	@Override
	public void tick() {
		super.tick();
		ticksExisted++;

		// start finding a player if one isn't present
		if (targetPlayer == null || !targetPlayer.isAlive()) {
			findNearestPlayer();
		}

		// if Herobrine got a target
		if (targetPlayer != null) {
			// make him always face player
			faceEntity(targetPlayer, 360.0F, 360.0F);
			float distanceToPlayer = getDistanceTo(targetPlayer);

			// get last position
			if (ticksExisted % 100 == 0) {
				lastPos = Vec3.getTempVec3(this.x, this.y, this.z);
				if (distanceToPlayer < FLEE_DISTANCE) {
					// if player gets too close, run away
					isFleeing = true;
					moveSpeed = 0.8F; // Faster Move Speed when Running
					fleeFromPlayer();
				} else if (isFleeing && distanceToPlayer > DESPAWN_DISTANCE) {
					// place red stone torch and de-spawn
					placeRedstoneTorch();
					remove();
				} else if (!isFleeing) {
					// normal stalking
					if (distanceToPlayer > STALK_DISTANCE + 5.0F) {
						// too far, get closer
						moveSpeed = 0.15F;
						moveTowards(targetPlayer, STALK_DISTANCE);
					} else if (distanceToPlayer < STALK_DISTANCE - 5.0F) {
						// too close, run away
						moveSpeed = 0.4F;
						moveAwayFrom(targetPlayer, STALK_DISTANCE);
					} else {
						// sweetspot, he'll stay here
						moveSpeed = 0.0F;
					}
					// stalker behavior, watch for homes at night
					if (world.getWorldTime() % 24000 > 13000 && world.getWorldTime() % 24000 < 23000) {
						checkForWindowsAndHomes();
					}
				}
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
			this.yBodyRot = yaw;
			this.xRot = pitch;

			this.yRotO = this.yRot;
			this.xRotO = this.xRot;
		}
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
	}

	private void placeRedstoneTorch() {
		if (lastPos != null) {
			int torchX = (int) lastPos.x;
			int torchY = (int) lastPos.y;
			int torchZ = (int) lastPos.z;

			while (world.getBlockId(torchX, torchY - 1, torchZ) == 0 && torchY > 0) {
				torchY--;
			}

			if (world.getBlockId(torchX, torchY - 1, torchZ) != 0) {
				world.setBlockWithNotify(torchX, torchY, torchZ, Blocks.TORCH_REDSTONE_ACTIVE.id());
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

	private void moveAwayFrom(Player targetPlayer, float stalkDistance) {
		double dx = this.x - targetPlayer.x;
		double dz = this.z - targetPlayer.z;

		double distance = Math.sqrt(dx * dx + dz * dz);

		if (distance < stalkDistance) {
			double scale = 0.1 / distance;
			xd = dx * scale;
			zd = dz * scale;
		} else {
			xd = 0;
            zd = 0;
		}
	}

	private void moveTowards(Player targetPlayer, float stalkDistance) {
		double dx = targetPlayer.x - this.x;
		double dz = targetPlayer.z - this.z;

		double distance = Math.sqrt(dx * dx + dz * dz);

		if (distance > stalkDistance) {
			double scale = 0.1 / distance;
			xd = dx * scale;
			zd = dz * scale;
		} else {
			xd = 0;
			zd = 0;
		}
	}

	private void fleeFromPlayer() {
		double fleeX = x - targetPlayer.x;
		double fleeZ = z - targetPlayer.z;

		double length = Math.sqrt(fleeX * fleeX + fleeZ * fleeZ);
		if (length > 0) {
			fleeX = fleeX / length * 0.4;
			fleeZ = fleeZ / length * 0.4;
		}

		xd = fleeX;
		zd = fleeZ;
	}

	private void findNearestPlayer() {
		List<Player> players = world.getEntitiesWithinAABB(
			Player.class,
			getBb().expand(64.0, 64.0, 64.0)
		);
		if (!players.isEmpty()) {
			Player nearest = null;
			double nearestDistance = Double.MAX_VALUE;

			for (Player player : players) {
				double distance = getDistanceTo(player);
				if (distance < nearestDistance) {
					nearest = player;
					nearestDistance = distance;
				}
			}
			targetPlayer = nearest;
		}
	}

	public static boolean trySpawn(World world, Player player) {
		Random random = new Random();
		if (random.nextFloat() > SPAWN_CHANCE) {
			return false;
		}

		boolean isNight = world.getWorldTime() % 24000 > 13000 && world.getWorldTime() % 24000 < 23000;
		boolean playerInCave = isPlayerUnderground(player, world);

		if (!isNight || !playerInCave) {
			return false;
		}

		int tryCount = 0;
		while (tryCount < 10) {
			double angle = random.nextDouble() * Math.PI * 2;
			double distance = 15.0 + random.nextDouble() * 10.0;

			int spawnX = (int) (player.x + Math.sin(angle) * distance);
			int spawnZ = (int) (player.z + Math.cos(angle) * distance);

			// Find Y position
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
				// Found a spot! Spawn Herobrine
				HerobrineStalkingEntity herobrine = new HerobrineStalkingEntity(world);
				herobrine.setPos(spawnX + 0.5, spawnY, spawnZ + 0.5);
				herobrine.targetPlayer = player;
				world.entityJoinedWorld(herobrine);
				return true;
			}

			tryCount++;
		}
		return false;
	}

	private static boolean isPlayerUnderground(Player player, World world) {
		boolean canSeeSky = world.canBlockSeeTheSky((int)player.x, (int)player.y, (int)player.z);

		if (world.getWorldType().equals(WorldTypes.OVERWORLD_EXTENDED) && !canSeeSky && player.y < 130) {
			return true;
		} else if (world.getWorldType().equals(WorldTypes.OVERWORLD_DEFAULT) && !canSeeSky && player.y < 50) {
			return true;
		}
		return false;
	}
}
