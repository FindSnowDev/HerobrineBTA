package net.findsnow.btabrine.common.event;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.entity.Mob;
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
		if (world != null) {


			if (!hasWarned) {
				hasWarned = true;
				world.sendGlobalMessage("Stop.");
			}

			if (world.rand.nextInt(100) < 20) {
				Mob herobrine = (Mob)EntityDispatcher.createEntityInWorld(this.herobrineID, world);
				if (herobrine != null) {

					EntityLightning lightning = new EntityLightning(world, x, y, z);
					world.entityJoinedWorld(lightning);

					herobrine.moveTo(x + 0.5, y + 1.0, z + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
					world.entityJoinedWorld(herobrine);

					if (world.weatherManager != null) {
						world.weatherManager.overrideWeather(Weathers.OVERWORLD_FOG, 200, 100);
					}

					world.playSoundAtEntity(null, herobrine, "btabrine:mob.herobrine.vanish", 1.0F, 1.0F);

					String randomPhrase = spawnPhrases[world.rand.nextInt(spawnPhrases.length)];
					world.sendGlobalMessage(randomPhrase);
				}
			}
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
