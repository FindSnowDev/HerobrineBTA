package net.findsnow.btabrine.common.event;

import net.findsnow.btabrine.common.entity.HerobrineNightmareEntity;
import net.findsnow.btabrine.common.registry.BTABAchievements;
import net.findsnow.btabrine.common.util.BTABrineConfig;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

import java.util.Random;

public class SleepEvent {
	private static final Random random = new Random();
	private static final float NIGHTMARE_CHANCE = 0.9F;

	private boolean isNightmareRunning = false;
	private int nightmareDuration = 0;
	private Player targetPlayer = null;
	private HerobrineNightmareEntity herobrineNightmare = null;

	public boolean shouldTriggerNightmare(Player player, World world) {
		if (world.isDaytime() || isNightmareRunning) {
			return false;
		}
		return random.nextFloat() < NIGHTMARE_CHANCE;
	}

	public void startNightmare(Player player) {
		isNightmareRunning = true;
		targetPlayer = player;
		nightmareDuration = 100;
		spawnHerobrine(player);
		player.triggerAchievement(BTABAchievements.herobrineAchievements.get(1));
	}

	public boolean updateNightmare() {
		if (!isNightmareRunning) {
			return false;
		}

		nightmareDuration--;
		if (nightmareDuration <= 0) {
			endNightmare(false);
			return false;
		}
		return true;
	}

	public void endNightmare(boolean b) {
		if (isNightmareRunning) {
			isNightmareRunning = false;
			despawnHerobrine();
			targetPlayer = null;
		}
	}

	public boolean isNightmareRunning() {
		return isNightmareRunning;
	}

	public Player getPlayer() {
		return targetPlayer;
	}

	private void spawnHerobrine(Player player) {
		World world = player.world;

		int bedX = MathHelper.floor(player.x);
		int bedY = MathHelper.floor(player.y);
		int bedZ = MathHelper.floor(player.z);

		int bedDirection = getBedDirection(bedX, bedY, bedZ);

		double herobrineX = bedX;
		double herobrineY = bedY;
		double herobrineZ = bedZ;

		double distance = 2;

		switch (bedDirection) {
			case 0:
				herobrineZ -= distance;
				break;
			case 1:
				herobrineX += distance;
				break;
			case 2:
				herobrineZ += distance;
				break;
			case 3:
				herobrineX -= distance;
				break;
		}

		herobrineX = findSafeX(world, herobrineX, herobrineY, herobrineZ);
		herobrineY = findSafeY(world, herobrineX, herobrineY, herobrineZ);
		herobrineZ = findSafeZ(world, herobrineX, herobrineY, herobrineZ);

		herobrineNightmare = new HerobrineNightmareEntity(world);
		herobrineNightmare.setPos(herobrineX, herobrineY, herobrineZ);
		herobrineNightmare.setWatchTarget(player);
		herobrineNightmare.setLifespan(nightmareDuration);

		float yaw = 0;
		switch (bedDirection) {
			case 0:
				yaw = 0;
				break;
			case 1:
				yaw = 90;
				break;
			case 2:
				yaw = 180;
				break;
			case 3:
				yaw = 270;
				break;
		}
		herobrineNightmare.setRot(yaw, 0);
		world.entityJoinedWorld(herobrineNightmare);
	}

	private double findSafeZ(World world, double x, double y, double z) {
		double width = 0.6;
		double halfWidth = width / 2;

		if (isPositionBlocked(world, x, y, z - halfWidth) || isPositionBlocked(world, x, y, z + halfWidth)) {
			for (int offset = 1; offset <= 3; offset++) {
				if (!isPositionBlocked(world, x, y, z + offset - halfWidth) && !isPositionBlocked(world, x, y, z + offset + halfWidth)) {
					return z + offset;
				}
				if (!isPositionBlocked(world, x, y, z - offset - halfWidth) && !isPositionBlocked(world, x, y, z - offset + halfWidth)) {
					return z - offset;
				}
			}
		}
		return z;
	}

	private double findSafeY(World world, double x, double y, double z) {
		double height = 1.8;

		if (isPositionBlocked(world, x, y, z) || isPositionBlocked(world, x, y + height, z)) {
			for (int offset = 1; offset <= 3; offset++) {
				if (!isPositionBlocked(world, x, y + offset, z) && !isPositionBlocked(world, x, y + offset + height, z)) {
					return y + offset;
				}
			}
			for (int offset = 1; offset <= 2; offset++) {
				if (!isPositionBlocked(world, x, y - offset, z) && !isPositionBlocked(world, x, y - offset + height, z)) {
					return y - offset;
				}
			}
		}
		return y;
	}

	private double findSafeX(World world, double x, double y, double z) {
		double width = 0.6;
		double halfWidth = width / 2;

		if (isPositionBlocked(world, x - halfWidth, y, z) || isPositionBlocked(world, x + halfWidth, y, z)) {
			for (int offset = 1; offset <= 3; offset++) {
				if (!isPositionBlocked(world, x + offset - halfWidth, y, z) && !isPositionBlocked(world, x + offset + halfWidth, y , z)) {
					return x + offset;
				}
				if (!isPositionBlocked(world, x - offset - halfWidth, y, z) && !isPositionBlocked(world, x - offset + halfWidth, y , z)) {
					return x - offset;
				}
 			}
		}
		return x;
	}

	private boolean isPositionBlocked(World world, double x, double y, double z) {
		int blockX = MathHelper.floor(x);
		int blockY = MathHelper.floor(y);
		int blockZ = MathHelper.floor(z);
		int blockId = world.getBlockId(blockX, blockY, blockZ);

		if (blockId != 0) {
			Block block = Blocks.blocksList[blockId];
			return block != null && block.getMaterial().isSolid();
		}
		return false;
	}

	private int getBedDirection(int bedX, int bedY, int bedZ) {
		if (targetPlayer == null || targetPlayer.world == null) {
			return 0;
		}
		try {
			int blockMetadata = targetPlayer.world.getBlockMetadata(bedX, bedY, bedZ);
			return blockMetadata & 0x3;
		} catch (Exception e) {
			return 0;
		}
	}

	private void despawnHerobrine() {
		if (herobrineNightmare != null  && !herobrineNightmare.isRemoved()) {
			herobrineNightmare.remove();
			herobrineNightmare = null;
		}
	}
}
