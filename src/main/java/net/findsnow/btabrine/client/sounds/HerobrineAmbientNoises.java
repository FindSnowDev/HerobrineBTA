package net.findsnow.btabrine.client.sounds;

import net.findsnow.btabrine.BTABrine;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.slf4j.Logger;
import turniplabs.halplibe.helper.SoundHelper;

import java.util.Random;

public class HerobrineAmbientNoises {

	// In Development, cant figure this out...
	private static final Logger LOGGER = BTABrine.LOGGER;
	private static final Random random = new Random();
	private static final Minecraft minecraft = Minecraft.getMinecraft();

	private static int footstepsCooldown = 0;
	private static int miningCooldown = 0;
	private static int doorSoundCooldown = 0;

	private static final int BASE_SOUND_CHANCE = 800;
	private static final int CAVE_SOUND_MULTIPLIER = 2;

	private static final String[] MINING_SOUNDS = {
		"btabrine:ambient.herobrine.mine1"
	};

	private static final String[] FOOTSTEP_SOUNDS = {
		"btabrine:ambient.herobrine.step1"
    };

	private static final String[] DOOR_SOUNDS = {
		"btabrine:ambient.herobrine.door_open",
		"btabrine:ambient.herobrine.door_shut"
    };

	public static void registerSounds() {
		LOGGER.info("Registering sounds for Herobrine");
		for (String sound : MINING_SOUNDS) {
			SoundHelper.addSound(BTABrine.MOD_ID, "ambient/herobrine/" + sound + ".ogg");
		}
		for (String sound : FOOTSTEP_SOUNDS) {
			SoundHelper.addSound(BTABrine.MOD_ID, "ambient/herobrine/" + sound + ".ogg");
		}
		for (String sound : DOOR_SOUNDS) {
			SoundHelper.addSound(BTABrine.MOD_ID, "ambient/herobrine/" + sound + ".ogg");
		}
	}

	public static void tickAmbience() {
		if (footstepsCooldown > 0) {
            footstepsCooldown--;
        }
        if (miningCooldown > 0) {
            miningCooldown--;
        }
        if (doorSoundCooldown > 0) {
            doorSoundCooldown--;
        }

		Player player = minecraft.thePlayer;
		World world = minecraft.currentWorld;

		if (player == null || world == null) return;
		if (random.nextInt(100) >= 10) return;

		boolean isInCave = isInCave(player, world);
		boolean isInside = isInside(player, world);

		int soundChance = BASE_SOUND_CHANCE;
		if (isInCave) {
			soundChance /= CAVE_SOUND_MULTIPLIER;
		}

		if (random.nextInt(soundChance) == 0) {
			if (isInCave && miningCooldown <= 0) {
				playMiningSound(player);
				miningCooldown = 160 + random.nextInt(300);
			} else if (!isInCave && !isInside && footstepsCooldown <= 0) {
				playFootstepSound(player);
                footstepsCooldown = 200 + random.nextInt(400);
			} else if (isInside && doorSoundCooldown <= 0) {
				playDoorSound(player);
                doorSoundCooldown = 240 + random.nextInt(480);
			}
		}
	}

	private static void playDoorSound(Player player) {
		String sound = "ambient.herobrine.mine1";
		minecraft.currentWorld.playSoundAtEntity(player, player, sound, 1.0F, 1.0F);
		LOGGER.debug("Playing door sound at Entity)");
	}

	private static void playFootstepSound(Player player) {
		String sound = "ambient.herobrine.step1";
		minecraft.currentWorld.playSoundAtEntity(player, player, sound, 1.0F, 1.0F);
		LOGGER.debug("Playing Footstep sounds at Entity");
	}

	private static void playMiningSound(Player player) {
		String sound = MINING_SOUNDS[random.nextInt(MINING_SOUNDS.length)];
		minecraft.currentWorld.playSoundAtEntity(player, player, sound, 1.0F, 1.0F);
		LOGGER.debug("Playing Mining sounds at Entity");
	}

	private static boolean isInside(Player player, World world) {
		int startY = (int) Math.floor(player.y) + 2;
		int endY = startY + 6;

		for (int y = startY; y < endY; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					int blockID = world.getBlockId(
						(int) Math.floor(player.x) + x,
						y,
						(int) Math.floor(player.z) + z
					);
					if (blockID != 0 && Blocks.blocksList[blockID].getMaterial().isSolid()) {
						return true;
					}
				}
			}
		}
		return false;
	}


	private static boolean isInCave(Player player, World world) {
		if (player.y > 55) return false;

		int lightLevel = world.getBlockLightValue(
			(int)Math.floor(player.x),
			(int)Math.floor(player.y),
			(int)Math.floor(player.z)
		);
		return lightLevel < 8;
	}
}
