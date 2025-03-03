package net.findsnow.btabrine.common.event;

import net.findsnow.btabrine.common.entity.HerobrineNightmareEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

import java.util.Random;

public class SleepEvent {
	private static final Random random = new Random();
	private static final float NIGHTMARE_CHANCE = 0.05F;

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
		nightmareDuration = 60 + random.nextInt(120);

		spawnHerobrine(player);
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
		double herobrineY = bedY + 0.5;
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

		herobrineNightmare = new HerobrineNightmareEntity(world);
		herobrineNightmare.setPos(herobrineX, herobrineY, herobrineZ);
		herobrineNightmare.setWatchTarget(player);
		herobrineNightmare.setLifespan(nightmareDuration + 20);

		float yaw = 0;
		switch (bedDirection) {
			case 0: // South (+Z)
				yaw = 0;
				break;
			case 1: // West (-X)
				yaw = 90;
				break;
			case 2: // North (-Z)
				yaw = 180;
				break;
			case 3: // East (+X)
				yaw = 270;
				break;
		}
		herobrineNightmare.setRot(yaw, 0);
		world.entityJoinedWorld(herobrineNightmare);
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
