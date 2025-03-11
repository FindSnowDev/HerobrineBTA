package net.findsnow.btabrine.common.util;

import net.findsnow.btabrine.common.entity.interfaces.IDetectorEntity;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldTypes;

public class HDetector implements IDetectorEntity {
	@Override
	public boolean isPlayerUnderground(Player player, World world) {
		boolean canSeeSky = world.canBlockSeeTheSky((int)player.x, (int)player.y, (int)player.z);

		if (world.getWorldType().equals(WorldTypes.OVERWORLD_EXTENDED) && !canSeeSky && player.y < 130) {
			return true;
		} else if (world.getWorldType().equals(WorldTypes.OVERWORLD_DEFAULT) && !canSeeSky && player.y < 50) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isPlayerNearHome(Player player, World world) {
		int homeRadius = 32;
		int homeBlockCount = 0;

		for (int x = -homeRadius; x <= homeRadius; x++) {
			for (int y = -2; y <= 4; y++) {
				for (int z = -homeRadius; z <= homeRadius; z++) {
					int blockId = world.getBlockId(
						(int) player.x + x,
						(int) player.y + y,
						(int) player.z + z
					);
					if (blockId != 0 && blockId != Blocks.GRASS.id() && blockId != Blocks.DIRT.id()) {
						homeBlockCount++;
					}
				}
			}
		}
		return homeBlockCount > 50;
	}

	@Override
	public boolean isPLayerInWild(Player player, World world) {
		return !isPlayerUnderground(player, world) &&
			!isPlayerNearHome(player, world) &&
			world.canBlockSeeTheSky((int)player.x, (int)player.y, (int)player.z);
	}

	@Override
	public boolean isNightTime(World world) {
		return world.getWorldTime() % 24000 > 13000 && world.getWorldTime() % 24000 < 23000;
	}

	@Override
	public boolean checkForWindowsNearPlayer(Player player, World world) {
		int blockCount = 0;
		int totalChecks = 0;

		for (int x = -3; x <= 3; x++) {
			for (int y = -1; y <= 3; y++){
				for (int z = -3; z <= 3; z++) {
					int blockId = world.getBlockId(
						(int) player.x + x,
						(int) player.y + y,
						(int) player.z + z
					);
					if (blockId != 0) {
						blockCount++;
					}
					totalChecks++;
				}
			}
		}
		return (double) blockCount / totalChecks > 0.5;
	}

	@Override
	public boolean findWindowPos(Player player, World world, float range, Entity entityToPos) {
		for (int checkRange = 5; checkRange <= range; checkRange += 5) {
			for (int i = 0; i < 10; i++) {
				double angle = Math.random() * Math.PI * 2;
				int checkX = (int) (player.x + Math.sin(angle) * checkRange);
				int checkZ = (int) (player.z + Math.cos(angle) * checkRange);

				int groundY = findGroundLevel(player, world, checkX, checkZ);
				if (groundY > 0) {
					for (int y = groundY; y < groundY + 8; y++) {
						if (isWindowBlock(world, checkX, y, checkZ)) {
							if (entityToPos != null) {
								entityToPos.setPos(checkX + 0.5, y + 0.5, checkZ + 0.5);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean isWindowBlock(World world, int x, int y, int z) {
		int blockId = world.getBlockId(x, y, z);
		return blockId == Blocks.GLASS.id() || blockId == Blocks.GLASS_TINTED.id() || blockId == Blocks.TRAPDOOR_GLASS.id();
	}

	private int findGroundLevel(Player player, World world, int checkX, int checkZ) {
		for (int y = (int) player.y + 5; y > (int) player.y - 10; y--) {
			if (world.getBlockId(checkX, y, checkZ) != 0 &&
				world.getBlockId(checkX, y + 1, checkZ) == 0) {
				return y + 1;
			}
		}
		return -1;
	}
}
