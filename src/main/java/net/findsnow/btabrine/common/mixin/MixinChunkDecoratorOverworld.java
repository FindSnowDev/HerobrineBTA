package net.findsnow.btabrine.common.mixin;


import net.findsnow.btabrine.common.world.features.BTABMissingLeavesFeature;
import net.findsnow.btabrine.common.world.features.BTABPyramidFeature;
import net.findsnow.btabrine.common.world.features.BTABSignFeature;
import net.findsnow.btabrine.common.world.features.BTABTunnelFeature;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = ChunkDecoratorOverworld.class, remap = false)
public class MixinChunkDecoratorOverworld {
	@Shadow
	@Final
	private World world;

	@Inject(
		method = "decorate",
		at = @At("TAIL")
	)
	private void addPyramidGeneration(Chunk chunk, CallbackInfo ci) {
		int chunkX = chunk.xPosition;
		int chunkZ = chunk.zPosition;
		Random rand = new Random(this.world.getRandomSeed());
		long l1 = rand.nextLong() / 2L * 2L + 1L;
		long l2 = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed((long) chunkX * l1 + (long) chunkZ * l2 ^ this.world.getRandomSeed());
		int x = chunkX * 16 + rand.nextInt(16) + 8;
		int z = chunkZ * 16 + rand.nextInt(16) + 8;
		int y = this.world.getHeightValue(x, z);

		if (rand.nextInt(310) == 0 && y > 50 && isFlatLand(x, y - 1, z)) {
			new BTABPyramidFeature().place(this.world, rand, x, y, z);
		}

		if (rand.nextInt(15) == 0) {
			for (int attempts = 0; attempts < 3; attempts++) {
				int tunnelX = chunkX * 16 + rand.nextInt(16);
				int tunnelZ = chunkZ * 16 + rand.nextInt(16);
				int surfaceY = this.world.getHeightValue(tunnelX, tunnelZ);
				int tunnelY;

				if (surfaceY > 80) {
					tunnelY = 10 + rand.nextInt(surfaceY - 25);
				} else {
					tunnelY = 20 + rand.nextInt(50);
				}

				if (this.world.getBlockId(tunnelX, tunnelY, tunnelZ) != 0 ) {
					if (new BTABTunnelFeature().place(this.world, rand, tunnelX, tunnelY, tunnelZ)) {
						break;
					}
				}
			}
		}

		if (rand.nextInt(20) == 0) {
			new BTABMissingLeavesFeature().place(this.world, rand, chunkX, 0, chunkZ);
		}
	}

	private boolean isFlatLand(int x, int y, int z) {
		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				int blockId = this.world.getBlockId(x + dx, y, z + dz);
				int heightHere = this.world.getHeightValue(x + dx, z + dz);
				if (blockId == 0 || heightHere != y + 1) {
					return false;
				}
			}
		}
		return true;
	}
}
