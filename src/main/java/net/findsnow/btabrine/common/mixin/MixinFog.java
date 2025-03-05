package net.findsnow.btabrine.common.mixin;

import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherClear;
import net.minecraft.core.world.weather.Weathers;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Weathers.class, remap = false)
public class MixinFog {

    @Shadow @Final @Mutable
    private static Weather[] WEATHERS;

    @Unique
    private static final Weather HEROBRINE_FOG = new WeatherClear(9)
        .setLanguageKey("overworld.herobrine_fog")
        .setFogDistance(0.03F);


    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addHerobrineFog(CallbackInfo callbackInfo) {
        if (WEATHERS.length > 9) {
            WEATHERS[9] = HEROBRINE_FOG;
        } else {
            System.err.println("Warning: Herobrine Fog weather slot not available. BTABrine may not function correctly.");
        }
    }
}
