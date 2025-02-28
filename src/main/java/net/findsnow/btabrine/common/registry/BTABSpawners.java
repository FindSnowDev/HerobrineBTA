package net.findsnow.btabrine.common.registry;

import net.findsnow.btabrine.common.entity.HerobrineEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

public class BTABSpawners {

	// This class don't do anything right now brah
	// This is supposed to check that only one Herobrine exists in the world at all times, will work on later

	public static boolean herobrineExists = false;
	private static HerobrineEntity activeEntity = null;
	private static final int SPAWN_ATTEMPT = 6000;
	private static int cooldown = 0;

	public static void tickSpawner(World world) {
		// if Herobrine exists but entity is dead then reset
		if (herobrineExists && (activeEntity == null || activeEntity.removed)) {
			herobrineExists = false;
			activeEntity = null;
			cooldown = SPAWN_ATTEMPT;
			return;
		}

		if (cooldown > 0) {
			cooldown--;
			return;
		}
		if (herobrineExists) {
			return;
		}
		Player player = world.getClosestPlayer(0, 64, 0, 128.0);
		if (player != null && !world.isDaytime()) {
			trySpawnNearPlayer(world, player) ;
		}
	}

	private static void trySpawnNearPlayer(World world, Player player) {
		for (int attempts = 0; attempts < 32; attempts++) {
			double angle = world.rand.nextDouble() * Math.PI * 2;
			double distance = 24 + world.rand.nextDouble() * 8;

			int x = (int)(player.x + Math.cos(angle) * distance);
			int z = (int)(player.z + Math.sin(angle) * distance);
			int y = world.getHeightValue(x, z);

			if (world.getBlockLightValue(x, y, z) <= 7) {
				if (world.getBlockId(x, y - 1, z) != 0 &&
					world.getBlockId(x, y, z) == 0 &&
					world.getBlockId(x, y + 1, z) == 0) {
					HerobrineEntity herobrine = new HerobrineEntity(world);
					herobrine.setPos(x + 0.5, y, z + 0.5);

					if (world.entityJoinedWorld(herobrine)) {
						herobrineExists = true;
						activeEntity = herobrine;
						return;
					}
				}
			}
		}
		cooldown = SPAWN_ATTEMPT;
	}

	public static void removeHerobrine() {
		if (activeEntity != null) {
			activeEntity.remove();
		}
		herobrineExists = false;
		activeEntity = null;
	}

	public static boolean doesHerobrineExist() {
		return herobrineExists && activeEntity != null && !activeEntity.removed;
	}
}
