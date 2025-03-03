package net.findsnow.btabrine.common.world.features;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class BTABTunnelFeature extends WorldFeature {
	private static final int MIN_LENGTH = 6;
	private static final int MAX_LENGTH = 20;
	private static final int TORCH_CHANCE = 8;

	@Override
	public boolean place(World world, Random random, int x, int y, int z) {
		if (!isValidLocation(world, x, y, z)) {
			return false;
		}

		int direction = random.nextInt(4); // 4 directions it can go in 0-3
		int dx = 0, dz = 0;

		switch (direction) {
			case 0:
				dz = -1;
				break;
			case 1:
				dx = 1;
				break;
			case 2:
				dz = 1;
				break;
			case 3:
				dx = -1;
				break;
		}

		int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH + 1);
		boolean torchPlaced = false;
		boolean success = false;

		for (int i = 0; i < length; i++) {
			int currentX = x + (dx * i);
			int currentZ = z + (dz * i);

			for (int offsetX = 0; offsetX < 2; offsetX++) {
				for (int offsetY = 0; offsetY < 2; offsetY++) {
					world.setBlockWithNotify(currentX + offsetX, y + offsetY, currentZ, 0);
					success = true;
				}
			}
			if (random.nextInt(TORCH_CHANCE) == 0 && !torchPlaced) {
				int torchX = currentX + random.nextInt(2);
				int torchZ = currentZ;
				world.setBlockWithNotify(torchX, y, torchZ, Blocks.TORCH_REDSTONE_ACTIVE.id());
				torchPlaced = true;
			} else {
				torchPlaced = false;
			}
			if (random.nextInt(6) == 0) {
				y += random.nextBoolean() ? 1 : -1;
			}
			if (isOpenSpace(world, currentX + dx, y, currentZ + dz) || isOpenSpace(world, currentX + dx, y + 1, currentZ + dz)) {
				break;
			}
		}
		return success;
	}

	private boolean isValidLocation(World world, int x, int y, int z) {
		boolean hasSolidBlocksAround = true;
		for (int offsetX = 0; offsetX < 2; offsetX++) {
			for (int offsetY = 0; offsetY < 2; offsetY++) {
				for (int offsetZ = 0; offsetZ < 2; offsetZ++) {
					if (isOpenSpace(world, x + offsetX, y + offsetY, z + offsetZ)) {
						hasSolidBlocksAround = false;
					}
				}
			}
		}
		return hasSolidBlocksAround;
	}

	private boolean isOpenSpace(World world, int i, int i1, int i2) {
		int blockID = world.getBlockId(i, i1, i2);
		return blockID == 0 ||
			blockID == Blocks.FLUID_WATER_STILL.id() ||
			blockID == Blocks.FLUID_WATER_FLOWING.id() ||
			blockID == Blocks.FLUID_LAVA_STILL.id() ||
			blockID == Blocks.FLUID_LAVA_FLOWING.id();
	}
}
