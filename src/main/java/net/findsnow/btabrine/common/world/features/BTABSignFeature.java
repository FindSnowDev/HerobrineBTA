package net.findsnow.btabrine.common.world.features;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class BTABSignFeature extends WorldFeature {

	private static final String[][] SIGN_PHRASES = {
		{"You are not", "alone", ""},
		{"I'm", "watching", ""},
		{"I see", "you", ""},
		{"Hello", "", ""}
	};

	private static int currentMessageIndex = 0;

	@Override
	public boolean place(World world, Random random, int x, int y, int z) {
		if (!isSurface(world, x, y, z)) {
			return false;
		}

		world.setBlockWithNotify(x, y + 1, z, Blocks.SIGN_POST_PLANKS_OAK.id());
		int metadata = random.nextInt(16);
		world.setBlockMetadataWithNotify(x, y + 1, z, metadata);

		TileEntitySign entitySign = (TileEntitySign)world.getTileEntity(x, y + 1, z);
		if (entitySign != null) {
			String[] message = getNextMessage();
			for (int i = 0; i < 4; i++) {
				entitySign.signText[i] = message[i];
			}
		}
		return true;
	}

	private boolean isSurface(World world, int x, int y, int z) {
		if (world.getBlockId(x, y + 1, z) != 0) {
			return false;
		}

		int blockID = world.getBlockId(x, y, z);
		Block block = Blocks.blocksList[blockID];

		return block != null && block.isCubeShaped() &&
			(blockID == Blocks.GRASS.id() ||
				blockID == Blocks.DIRT.id() ||
				blockID == Blocks.STONE.id() ||
				blockID == Blocks.SAND.id() ||
				blockID == Blocks.GRAVEL.id() ||
				blockID == Blocks.LIMESTONE.id());
	}

	private String[] getNextMessage() {
		String[] message = SIGN_PHRASES[currentMessageIndex];
		currentMessageIndex = (currentMessageIndex + 1) % SIGN_PHRASES.length;
		return message;
	}
}
