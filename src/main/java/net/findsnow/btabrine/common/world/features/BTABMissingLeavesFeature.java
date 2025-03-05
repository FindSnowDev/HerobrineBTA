package net.findsnow.btabrine.common.world.features;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class BTABMissingLeavesFeature extends WorldFeature {
	private static final int CHUNK_SIZE = 16;
	private static final int MAX_HEIGHT = 240;

	@Override
	public boolean place(World world, Random random, int chunkX, int y, int chunkZ) {
		int startX = chunkX * CHUNK_SIZE;
		int startZ = chunkZ * CHUNK_SIZE;
		boolean removedAny = false;

		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				for (int currentY = MAX_HEIGHT; currentY > 0; currentY--) {
					int blockID = world.getBlockId(startX + x, currentY, startZ + z);

					if (isLeafBlock(blockID)) {

						random.nextInt(100);
						world.setBlock(startX + x, currentY, startZ + z, 0);
						removedAny = true;

						if (random.nextInt(10) == 0) {
							world.spawnParticle(
								"leaf",
								(double)(startX + x) + 0.5D,
								currentY,
								(double)(startZ + z) + 0.5D,
								0.0D, 0.0D, 0.0D, 0, 0.0D
							);
						}
					}
				}
			}
		}
		return removedAny;
	}

	private boolean isLeafBlock(int blockID) {
		return  blockID == Blocks.LEAVES_BIRCH.id() ||
				blockID == Blocks.LEAVES_OAK.id() ||
				blockID == Blocks.LAYER_LEAVES_OAK.id() ||
				blockID == Blocks.LEAVES_OAK_RETRO.id() ||
				blockID == Blocks.LEAVES_CACAO.id() ||
				blockID == Blocks.LEAVES_PINE.id() ||
				blockID == Blocks.LEAVES_CHERRY.id() ||
				blockID == Blocks.LEAVES_CHERRY_FLOWERING.id() ||
				blockID == Blocks.LEAVES_PALM.id() ||
				blockID == Blocks.LEAVES_SHRUB.id() ||
				blockID == Blocks.LEAVES_EUCALYPTUS.id();
	}
}
