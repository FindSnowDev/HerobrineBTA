package net.findsnow.btabrine.common.event;

import net.findsnow.btabrine.common.entity.HerobrineNightmareEntity;
import net.findsnow.btabrine.common.entity.HerobrineStalkingEntity;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weathers;

public class ShrineEvent {
	private final String herobrineID;
	boolean hasWarned = false;
	private static final String[] spawnPhrases = {"Only God can help you now.", "You don't know what you've done.", "You shouldn't have done that."};

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
		if (world == null || world.weatherManager == null) {
			return;
		}

		int chance = world.rand.nextInt(100);

		if (chance < 2) {

			world.weatherManager.overrideWeather(Weathers.getWeather(9), Weathers.OVERWORLD_CLEAR, 10, 1.0F, 1.0F);

			EntityLightning lightning = new EntityLightning(world);
			world.entityJoinedWorld(lightning);

			HerobrineNightmareEntity herobrine = new HerobrineNightmareEntity(world);
			world.entityJoinedWorld(herobrine);
		}
		else if (chance < 5) {

			world.weatherManager.overrideWeather(Weathers.getWeather(9), Weathers.OVERWORLD_CLEAR, 10, 1.0F, 1.0F);
		}
	}

	private static boolean isValidShrine(World world, int x, int y, int z) {

		if (world.getBlockId(x, y-1, z) != MOSSY_COBBLE_ID) {
			return false;
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
