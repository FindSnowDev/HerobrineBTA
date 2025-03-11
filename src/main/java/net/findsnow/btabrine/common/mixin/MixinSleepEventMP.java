package net.findsnow.btabrine.common.mixin;

import net.findsnow.btabrine.common.event.SleepEvent;
import net.findsnow.btabrine.common.registry.BTABAchievements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.ScreenSleepSP;
import net.minecraft.client.gui.chat.ScreenSleepMP;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScreenSleepMP.class, remap = false)
public class MixinSleepEventMP {
	@Unique
	private static final SleepEvent sleepEvent = new SleepEvent();
	@Unique
	private boolean isNightmare = false;


	@Inject(method = "init", at = @At("HEAD"))
	private void onInit(CallbackInfo callbackInfo) {
		Player player = Minecraft.getMinecraft().thePlayer;
		if (player != null && sleepEvent.shouldTriggerNightmare(player, player.world)) {
			isNightmare = true;
			sleepEvent.startNightmare(player);
		}
	}

	@Inject(method = "buttonClicked", at = @At("RETURN"), cancellable = true)
	private void onButtonClicked(ButtonElement button, CallbackInfo ci) {
		if (isNightmare && sleepEvent.isNightmareRunning()) {
			ci.cancel();
		}
	}

	@Inject(method = "leaveBed", at = @At("HEAD"))
	private void onWakeUp(CallbackInfo ci) {
        if (isNightmare && sleepEvent.isNightmareRunning()) {
            sleepEvent.endNightmare(true);
			isNightmare = false;
        }
    }
}
