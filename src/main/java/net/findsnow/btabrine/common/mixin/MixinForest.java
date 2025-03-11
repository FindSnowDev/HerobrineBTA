package net.findsnow.btabrine.common.mixin;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.BiomeForest;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = BiomeForest.class, remap = false)
public class MixinForest {
	@Unique
	private static int consecutiveLeafless = 0;

	@Unique
	private void tryPlaceTorch(World world, int x, int y, int z, int side, Random random, float chance) {
		if (random.nextFloat() < chance && world.isAirBlock(x, y, z)) {
			world.setBlockAndMetadataWithNotify(x, y, z, Blocks.TORCH_REDSTONE_ACTIVE.id(), side);
		}
	}

	@Inject(method = "getRandomWorldGenForTrees", at = @At("HEAD"), cancellable = true)
	private void onGetRandomWorldGenForTrees(Random random, CallbackInfoReturnable<WorldFeature> callbackInfo) {
		boolean makeLeafless;

		if (consecutiveLeafless > 0) {
			makeLeafless = random.nextInt(100) < 26;
			if (makeLeafless) {
				consecutiveLeafless++;
				if (consecutiveLeafless > 20) {
					consecutiveLeafless = 0;
				}
			} else {
				consecutiveLeafless = 0;
			}
		} else {
			makeLeafless = random.nextInt(100) < 14;
			if (makeLeafless) {
				consecutiveLeafless = 1;
			}
		}

		if (makeLeafless) {
			int logType;
			int leavesType = Blocks.LEAVES_OAK.id();

			if (random.nextInt(5) == 0) {
				logType = Blocks.LOG_BIRCH.id();
				leavesType = Blocks.LEAVES_BIRCH.id();
			} else if (random.nextInt(25) == 0) {
				logType = Blocks.LOG_OAK_MOSSY.id();
			} else {
				logType = Blocks.LOG_OAK.id();
			}

			final int finalLogType = logType;
			final int finalLeavesType = leavesType;

			callbackInfo.setReturnValue(new WorldFeatureTree(0, finalLogType, 8) {
				@Override
				public boolean place(World world, Random random, int x, int y, int z) {
					boolean result = super.place(world, random, x, y, z);

					if (result) {
						// Remove leaves
						for (int xOffset = -4; xOffset <= 4; xOffset++) {
							for (int yOffset = 0; yOffset <= 12; yOffset++) {
								for (int zOffset = -4; zOffset <= 4; zOffset++) {
									int blockX = x + xOffset;
									int blockY = y + yOffset;
									int blockZ = z + zOffset;

									if (world.getBlockId(blockX, blockY, blockZ) == finalLeavesType) {
										world.setBlockWithNotify(blockX, blockY, blockZ, 0);
									}
								}
							}
						}

						int treeHeight = 0;
						while (treeHeight < 20 && world.getBlockId(x, y + treeHeight, z) == finalLogType) {
							treeHeight++;
						}

						if (treeHeight >= 2) {
							int torchY = y + 1;

							int side = random.nextInt(4);
							if (side == 0) tryPlaceTorch(world, x + 1, torchY, z, 1, random, 0.5f); // east
							if (side == 1) tryPlaceTorch(world, x - 1, torchY, z, 3, random, 0.5f); // west
							if (side == 2) tryPlaceTorch(world, x, torchY, z + 1, 2, random, 0.5f); // south
							if (side == 3) tryPlaceTorch(world, x, torchY, z - 1, 0, random, 0.5f); // north

							if (treeHeight >= 6 && random.nextFloat() < 0.3f) {
								int secondTorchY = y + 3 + random.nextInt(treeHeight - 4);
								int secondSide = random.nextInt(4);
								if (secondSide == 0) tryPlaceTorch(world, x + 1, secondTorchY, z, 1, random, 0.6f);
								if (secondSide == 1) tryPlaceTorch(world, x - 1, secondTorchY, z, 3, random, 0.6f);
								if (secondSide == 2) tryPlaceTorch(world, x, secondTorchY, z + 1, 2, random, 0.6f);
								if (secondSide == 3) tryPlaceTorch(world, x, secondTorchY, z - 1, 0, random, 0.6f);
							}
						}
					}
					return result;
				}
			});
			callbackInfo.cancel();
		}
	}
}
