package net.findsnow.btabrine.common.util;

import net.findsnow.btabrine.common.entity.HerobrineNightmareEntity;
import net.findsnow.btabrine.common.entity.HerobrineWanderingEntity;
import net.findsnow.btabrine.common.entity.prototype.HWatcherEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static net.findsnow.btabrine.common.entity.HerobrineStalkingEntity.isPlayerUnderground;

public class HerobrineManager {
	private static HerobrineManager instance;

	// tracking cooldown
	private long lastStalkingSpawn = 0;
	private long lastWanderingSpawn = 0;
	private long lastNightmareSpawn = 0;

	private static final int STALKING_COOLDOWN = 2400;
	private static final int WANDERING_COOLDOWN = 2400;
	private static final int ENTITY_LIFESPAN = 3600;

	private static final float STALKING_CHANCE = 1F;
	private static final float WANDERING_CHANCE = 0.05F;

	// personalized cooldowns, testing for MP
	private Map<String, Long> playerCooldowns = new HashMap<>();

	private Random random = new Random();

	// singleton
	private HerobrineManager() {

	}

	public static HerobrineManager getInstance() {
		if (instance == null) {
            instance = new HerobrineManager();
        }
        return instance;
	}

	// mixin tick soon
	public void onWorldTick(World world) {
		if (hasExistingHerobrineEntity(world)) {
			return;
		}

		long currentTime = world.getWorldTime();

		if (currentTime - lastStalkingSpawn > STALKING_COOLDOWN) {
			trySpawnStalkingHerobrine(world);
		}

		if (currentTime - lastWanderingSpawn > WANDERING_COOLDOWN) {
			trySpawnWanderingHerobrine(world);
        }
	}

	private void trySpawnStalkingHerobrine(World world) {
		if (random.nextFloat() * 100 < STALKING_CHANCE) {
			for (Player player : world.players) {
				if (isPlayerEligbleForStalking(player, world)) {
					if (HWatcherEntity.trySpawn(world, player)) {
						lastStalkingSpawn = world.getWorldTime();
						return;
					}
				}
			}
		}
	}

	private void trySpawnWanderingHerobrine(World world) {
		if (random.nextFloat() * 100 < WANDERING_CHANCE) {
			for (Player player : world.players) {
				if (world.isDaytime() && !isPlayerUnderground(player, world)) {
					if (spawnWanderingHerobrine(world, player)) {
						lastWanderingSpawn = world.getWorldTime();
						return;
					}
				}
			}
		}
	}

	private boolean spawnWanderingHerobrine(World world, Player player) {
		int tryCount = 0;
		Random random = new Random();

		while (tryCount < 10) {
			double angle = random.nextDouble() * Math.PI * 2;
			double distance = 30.0 + random.nextDouble() * 20.0;

			int spawnX = (int) (player.x + Math.sin(angle) * distance);
			int spawnZ = (int) (player.z + Math.cos(angle) * distance);

			// Find ground level
			int spawnY = -1;
			for (int y = (int)player.y + 5; y > (int)player.y - 10; y--) {
				if (world.getBlockId(spawnX, y, spawnZ) != 0 &&
					world.getBlockId(spawnX, y + 1, spawnZ) == 0 &&
					world.getBlockId(spawnX, y + 2, spawnZ) == 0) {
					spawnY = y + 1;
					break;
				}
			}

			if (spawnY != -1) {
				HerobrineWanderingEntity herobrine = new HerobrineWanderingEntity(world);
				herobrine.setPos(spawnX + 0.5, spawnY, spawnZ + 0.5);
				herobrine.setLifespan(ENTITY_LIFESPAN);
				world.entityJoinedWorld(herobrine);
				System.out.println("Wandering Herobrine spawned at " + spawnX + ", " + spawnY + ", " + spawnZ);
				return true;
			}

			tryCount++;
		}

		return false;
	}

	private boolean isPlayerEligbleForStalking(Player player, World world) {
		boolean isNight = world.getWorldTime() % 24000 > 13000 && world.getWorldTime() % 24000 < 23000;
		boolean playerInCave = isPlayerUnderground(player, world);
		boolean playerInWild = !isPlayerUnderground(player, world);

		return (isNight && (playerInCave || playerInWild) && world.canBlockSeeTheSky((int)player.x, (int)player.y, (int)player.z));
	}



	private boolean hasExistingHerobrineEntity(World world) {
		return HWatcherEntity.hasExistingHerobrineStalker(world) || hasExistingWanderingHerobrine(world) || hasExistingNightmareHerobrine(world);
	}

	private boolean hasExistingNightmareHerobrine(World world) {
		return world.getLoadedEntityList().stream().anyMatch(entity -> entity instanceof HerobrineWanderingEntity && !entity.removed);
	}

	private boolean hasExistingWanderingHerobrine(World world) {
		return world.getLoadedEntityList().stream().anyMatch(entity -> entity instanceof HerobrineNightmareEntity && !entity.removed);
	}
}
