package net.findsnow.btabrine.common.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.findsnow.btabrine.client.sounds.IHerobrineCues;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.client.world.WorldClient;
import net.minecraft.client.world.WorldClientMP;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = WorldClientMP.class, remap = false)
@Environment(EnvType.CLIENT)
public class MixinWorldMP extends WorldClient implements IHerobrineCues {

	@Unique
	private int btabrine_soundCounter;

	@Unique
	public boolean isSurface(Entity entity) {
		return this.canBlockSeeTheSky((int) entity.x, (int) entity.y, (int) entity.z);
	}

	@Unique
	public boolean isNightTime() {
		return this.skyDarken > 4;
	}

	@Unique
	public boolean isInHouse(Entity entity, WorldType worldType) {
		boolean atSurfaceLevel = !isUnderground(entity, worldType);
		boolean cantSeeSky = !canBlockSeeTheSky((int)entity.x, (int)entity.y, (int)entity.z);
		return atSurfaceLevel && cantSeeSky;
	}

	@Unique
	public boolean isUnderground(Entity entity, WorldType worldTypeGroups) {
		if (worldTypeGroups.equals(WorldTypes.OVERWORLD_EXTENDED) && !isSurface(entity) && entity.y < 130) {
			return true;
		} else if (worldTypeGroups.equals(WorldTypes.OVERWORLD_DEFAULT) && !isSurface(entity) && entity.y < 50) {
			return true;
		}
		return false;
	}

	@Unique
	private final Set<ChunkCoordinate> btabrine_posToUpdate = new HashSet<>();

	@Inject(method = "<init>", at = @At("TAIL"))
	private void btabrine_initSoundCounter(PacketHandlerClient packetHandlerClient, long seed, int dimensionId, int worldTypeId, CallbackInfo callbackInfo) {
		btabrine_soundCounter = rand.nextInt(200);
    }

	@Override
	public void btabrine_startSoundCue() {
		btabrine_posToUpdate.clear();

		for (Player p : players) {
			int playerChunkX = MathHelper.floor(p.x / (double) 16.0F);
			int playerChunkZ = MathHelper.floor(p.z / (double) 16.0F);
			byte radius = 3;

			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) { // Fixed the loop condition (was z < radius)
					btabrine_posToUpdate.add(new ChunkCoordinate(x + playerChunkX, z + playerChunkZ));
				}
			}
		}

		if (btabrine_soundCounter > 0) {
			--btabrine_soundCounter;
			return;
		}

		for (ChunkCoordinate chunkCoordinate : btabrine_posToUpdate) {
			int chunkBlockX = chunkCoordinate.x * 16;
			int chunkBlockZ = chunkCoordinate.z * 16;

			if (isChunkLoaded(chunkCoordinate.x, chunkCoordinate.z)) {

				updateLCG = updateLCG * 3 + 1013904223;
				int randVal = updateLCG >> 2;
				int blockX = chunkBlockX + (randVal & 15);
				int blockZ = chunkBlockZ + (randVal / 256 & 15);
				int blockY = getHeightValue(blockX, blockZ) - 5 - rand.nextInt(10);

				if (rand.nextInt(100) < 20) {
					Player closestPlayer = getClosestPlayer(blockX + 0.5, blockY + 0.5, blockZ + 0.5, 24);
					if (closestPlayer != null) {
						double distSq = closestPlayer.distanceToSqr(blockX + 0.5, blockY + 0.5, blockZ + 0.5);

						// this only plays if player is close enough
						if (distSq > 16 && distSq < 256) {
							String soundPath = "";
							float volume = 0.6F + rand.nextFloat() * 0.2F;
							float pitch = 0.7F + rand.nextFloat() * 0.2F;
							boolean isInCave = isUnderground(closestPlayer, worldType);
							boolean isInHome = isInHouse(closestPlayer, worldType);

							if (isInCave) {
								// Only mining sound now
								soundPath = "btabrine:ambient.herobrine.mine1";
								volume = 1.0F;
								pitch = 1.0F;
								System.out.println("PLAYING MINING SOUND");
							} else if (isDaytime() || isNightTime()) {
								// Only footstep sound now
								soundPath = "btabrine:ambient.herobrine.step1";
								volume = 1.0F;
								pitch = 1.0F;
							} else if (isInHome){
								int soundChoice = rand.nextInt(3);
								switch (soundChoice) {
									case 0:
										soundPath = "btabrine:ambient.herobrine.step1";
										volume = 1.0F;
										pitch = 1.0F;
										break;
									case 1:
										soundPath = "minecraft:random.door_open";
										volume = 1.0F;
										pitch = 1.0F;
										System.out.println("PLAYING DOOR OPEN SOUND");
										break;
									case 2:
										soundPath = "minecraft:random.door_close";
										volume = 1.0F;
										pitch = 1.0F;
										System.out.println("PLAYING DOOR SHUT SOUND");
										break;
									default:
										break;
								}
							}
							playSoundEffect(null, SoundCategory.CAVE_SOUNDS, blockX + 0.5, blockY + 0.5, blockZ + 0.5, soundPath, volume, pitch);
							btabrine_soundCounter = isInCave || !isDaytime() ? 100 + rand.nextInt(200) : 300 + rand.nextInt(600);
							return;
						}
					}
				}
			}
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void btabrine_tick(CallbackInfo callbackInfo) {
		btabrine_startSoundCue();
	}
}
