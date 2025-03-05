package net.findsnow.btabrine.common.mixin;

import net.findsnow.btabrine.client.sounds.IHerobrineCues;
import net.findsnow.btabrine.common.entity.HerobrineStalkingEntity;
import net.findsnow.btabrine.common.util.HerobrineManager;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mixin(value = World.class, remap = false)
public abstract class MixinWorld implements WorldSource, IHerobrineCues {
	@Shadow
	Random rand;
	@Shadow
	public abstract boolean isChunkLoaded(int x, int z);
	@Shadow
	protected int updateLCG;
	@Shadow
	public abstract Biome getBlockBiome(int x, int y, int z);
	@Shadow
	public abstract Player getClosestPlayer(double x, double y, double z, double radius);
	@Shadow
	public abstract void playSoundEffect(Entity player, SoundCategory category, double x, double y, double z, String soundPath, float volume, float pitch);
	@Shadow
	public abstract boolean canBlockSeeTheSky(int x, int y, int z);
	@Shadow
	public abstract boolean isDaytime();
	@Shadow
	public List<Player> players;

	@Shadow
	public abstract int getHeightValue(int x, int z);

	@Shadow
	public WorldType worldType;

	@Shadow
	public abstract WorldType getWorldType();

	@Shadow
	public int skyDarken;

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
	public boolean isSurface(Entity entity) {
		return this.canBlockSeeTheSky((int) entity.x, (int) entity.y, (int) entity.z);
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
	private void btabrine_herobrineManager() {
		HerobrineManager.getInstance().onWorldTick((World)(Object)this);
	}

	@Unique
	private int btabrine_soundCounter;

	@Unique
	private final Set<ChunkCoordinate> btabrine_posToUpdate = new HashSet<>();

	@Inject(method = "<init>()V", at = @At("TAIL"))
	private void btabrine_initCounter(CallbackInfo callbackInfo) {
		btabrine_soundCounter = rand.nextInt(600);
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

				if (rand.nextInt(100) < 0.5) {
					Player closestPlayer = getClosestPlayer(blockX + 0.5, blockY + 0.5, blockZ + 0.5, 24);
					if (closestPlayer != null) {
						double distSq = closestPlayer.distanceToSqr(blockX + 0.5, blockY + 0.5, blockZ + 0.5);

						if (distSq > 16 && distSq < 256) {
							String soundPath = "";
							float volume = 0.6F + rand.nextFloat() * 0.2F;
							float pitch = 0.7F + rand.nextFloat() * 0.2F;
							boolean isInCave = isUnderground(closestPlayer, worldType);
							boolean isInHome = isInHouse(closestPlayer, worldType);

							if (isInCave) {
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
							btabrine_soundCounter = isInCave || !isDaytime() ? 400 + rand.nextInt(600) : 800 + rand.nextInt(1200);
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
		btabrine_herobrineManager();
	}
}
