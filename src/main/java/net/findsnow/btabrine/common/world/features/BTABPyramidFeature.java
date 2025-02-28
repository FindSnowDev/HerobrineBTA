package net.findsnow.btabrine.common.world.features;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;

import java.util.Random;

public class BTABPyramidFeature extends WorldFeature {

 @Override
 public boolean place(World world, Random random, int i, int j, int k) {
     Block sandBlock = Blocks.SAND;
     Block sandstoneBlock = Blocks.SANDSTONE;

     int baseSize = 5;
     int height = 3;

     for (int l = 0; l < height; l++) {
         int size = baseSize - (2 * l);
         int startX = i - (size / 2);
         int startZ = k - (size / 2);

         Block currentBlockType = (l == 0) ? sandstoneBlock : sandBlock;

         for (int x = 0; x < size; x++) {
             for (int z = 0; z < size; z++) {
                 world.setBlockWithNotify(startX + x, j + l, startZ + z, currentBlockType.id());
             }
         }
     }
     return true;
 }
}
