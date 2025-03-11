package net.findsnow.btabrine.common.event;

import net.findsnow.btabrine.common.entity.HerobrineNightmareEntity;
import net.findsnow.btabrine.common.entity.HerobrineStalkingEntity;
import net.findsnow.btabrine.common.registry.BTABAchievements;
import net.findsnow.btabrine.common.util.BTABrineConfig;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weathers;

public class ShrineEvent {
	private final String herobrineID;

	private static final int GOLD_BLOCK_ID = Blocks.BLOCK_GOLD.id();
	private static final int MOSSY_COBBLE_ID = Blocks.COBBLE_STONE_MOSSY.id();
	private static final int NETHERRACK_ID = Blocks.NETHERRACK.id();
	private static final int FIRE_ID = Blocks.FIRE.id();

	public ShrineEvent() {
		this.herobrineID = "btabrine:herobrine";
	}

	public boolean checkShrineActivation(World world, int x, int y, int z, int blockID) {
		if (blockID == FIRE_ID && world.getBlockId(x, y-1, z) == NETHERRACK_ID) {
			if (isValidShrine(world, x, y-1, z)) {
				activateShrine(world, x, y, z);
				return true;
			}
		}
		return false;
	}

	private void activateShrine(World world, int x, int y, int z) {
		Player player = world.getClosestPlayer(x, y, z, 10.0);
		if (player == null) {
			return;
		}
            player.triggerAchievement(BTABAchievements.herobrineAchievements.get(0));

         if (BTABrineConfig.isShrineAppearanceEnabled()) {
            EntityLightning lightning = new EntityLightning(world);
            lightning.setPos(x + 0.5, y + 1.0, z + 0.5); // Position on the netherrack block
			world.entityJoinedWorld(lightning);

            HerobrineNightmareEntity herobrine = new HerobrineNightmareEntity(world);
            herobrine.setPos(x + 0.5, y + 1.0, z + 0.5); // Position on the netherrack block
            world.entityJoinedWorld(herobrine);
		}
	}

	private static boolean isValidShrine(World world, int x, int y, int z) {
		if (world.getBlockId(x, y-1, z) != MOSSY_COBBLE_ID) {
			if (world.getBlockId(x, y-1, z) != GOLD_BLOCK_ID) {
				return false;
			}
		}

		boolean hasGoldRing = true;
		hasGoldRing &= world.getBlockId(x+1, y-1, z) == GOLD_BLOCK_ID;
		hasGoldRing &= world.getBlockId(x-1, y-1, z) == GOLD_BLOCK_ID;
		hasGoldRing &= world.getBlockId(x, y-1, z+1) == GOLD_BLOCK_ID;
		hasGoldRing &= world.getBlockId(x, y-1, z-1) == GOLD_BLOCK_ID;
		hasGoldRing &= world.getBlockId(x+1, y-1, z+1) == GOLD_BLOCK_ID;
		hasGoldRing &= world.getBlockId(x+1, y-1, z-1) == GOLD_BLOCK_ID;
		hasGoldRing &= world.getBlockId(x-1, y-1, z+1) == GOLD_BLOCK_ID;
		hasGoldRing &= world.getBlockId(x-1, y-1, z-1) == GOLD_BLOCK_ID;

		return hasGoldRing;
	}
}
