package net.findsnow.btabrine.common.mixin;

import net.findsnow.btabrine.common.event.ShrineEvent;
import net.minecraft.core.block.BlockLogicFire;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLogicFire.class, remap = false)
public class MixinBlockFire {
	private static final ShrineEvent shrineEvent = new ShrineEvent();

 @Inject(method = "onBlockPlacedByWorld", at = @At("TAIL"))
 private void onBlockFireAdded(World world, int x, int y, int z, CallbackInfo callbackInfo) {
	 shrineEvent.checkShrineActivation(world, x, y, z, Blocks.FIRE.id());
 }
}
