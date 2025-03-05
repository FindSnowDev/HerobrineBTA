package net.findsnow.btabrine.common.entity;

import net.findsnow.btabrine.common.entity.base.HerobrineBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.WeatherManager;
import net.minecraft.core.world.weather.Weathers;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class HerobrineWanderingEntity extends HerobrineBase {

	private static final String[][] SIGN_PHRASES = {
		{"You are not", "alone", "", ""},
		{"I'm", "watching", "", ""},
		{"I see", "you", "", ""},
		{"Found you", "{username}", "", ""},
		{"{username}", "Behind", "You", ""},
		{"", "Hello", "", ""}
	};
	private static int currentMessageIndex = 0;

	private static double DESPAWN_DISTANCE = 15.0;
	private static final double FOG_DISTANCE = 30.0;

	private int ticksExisted = 0;
	private int wanderCooldown = 0;
	private int lifespan = 1200;
	private Vec3 lastPos;

	private Random random = new Random();

	public HerobrineWanderingEntity(@Nullable World world) {
		super(world);
		this.lastPos = Vec3.getTempVec3(this.x, this.y, this.z);
	}

	public void setLifespan(int ticks) {
		this.lifespan = ticks;
	}

	@Override
	public void tick() {
		super.tick();

		lastPos = Vec3.getTempVec3(this.x, this.y, this.z);
		Player targetPlayer = findNearestPlayer();

		if (lifespan > 0) {
			lifespan--;
			if (lifespan <= 0) {
				placeSign();
				remove();
				return;
			}

			if (targetPlayer != null && playerCausesFog(targetPlayer, world)) {
				world.playSoundAtEntity(null, this, "btabrine:mob.herobrine.vanish", 1.0F, 1.0F);
				world.weatherManager.overrideWeather(Weathers.getWeather(9), Weathers.OVERWORLD_CLEAR, lifespan, 1.0F, 1.0F);

				if (playerCausesDespawn(targetPlayer)) {

					world.weatherManager.overrideWeather(Weathers.OVERWORLD_CLEAR);
					System.out.println("Player is causing despawn");
					remove();
					placeSign();
					return;
				}
			}
		}

		ticksExisted++;

		if (ticksExisted > 2400) {
			if (random.nextInt(100) < 5) {
				placeSign();
				remove();
				System.out.println("Wandering Herobrine has despawned naturally");
			}
		}
	}

	private boolean playerCausesDespawn(Player targetPlayer) {
		if (targetPlayer == null) {
			return false;
		}
		double distance = this.distanceTo(targetPlayer);
		return distance <= DESPAWN_DISTANCE;
	}

	private boolean playerCausesFog(Player targetPlayer, World world) {
		if (targetPlayer == null) {
			return false;
		}
		double distance = this.distanceTo(targetPlayer);
		return distance <= FOG_DISTANCE;
	}

	private void placeSign() {
		if (world != null) {
			int signX = MathHelper.floor(lastPos.x);
			int signY = MathHelper.floor(lastPos.y);
			int signZ = MathHelper.floor(lastPos.z);

			while (signY > 0 && world.getBlockId(signX, signY - 1, signZ) == 0) {
				signY--;
			}

			int blockBelow = world.getBlockId(signX, signY - 1, signZ);
			if (blockBelow != 0 && world.getBlockId(signX, signY, signZ) == 0) {
				world.setBlockWithNotify(signX, signY, signZ, Blocks.SIGN_POST_PLANKS_OAK.id());
				int metadata = random.nextInt(16);
				world.setBlockMetadata(signX, signY, signZ, metadata);  // FIX: Metadata at correct position

				TileEntitySign entitySign = new TileEntitySign();
				world.setTileEntity(signX, signY, signZ, entitySign);

				if (entitySign != null) {
					String[] message = getNextMessage();
					for (int i = 0; i < 4; i++) {
						entitySign.signText[i] = message[i];
					}
				}

				world.notifyBlocksOfNeighborChange(signX, signY, signZ, Blocks.SIGN_POST_PLANKS_OAK.id());

				System.out.println("Placed sign at " + signX + ", " + signY + ", " + signZ);
			} else {
				System.out.println("Failed to place sign: invalid position");
			}
		}
	}

	private String[] getNextMessage() {
		String[] message = SIGN_PHRASES[currentMessageIndex].clone();
		currentMessageIndex = (currentMessageIndex + 1) % SIGN_PHRASES.length;

		Player player = findNearestPlayer();
		if (player != null) {
			String username = player.username;
			for (int i = 0; i < message.length; i++) {
				if (message[i].contains("{username}")) {
					message[i] = message[i].replace("{username}", username);
				}
			}
		} else {
			for (int i = 0; i < message.length; i++) {
				if (message[i].contains("{username}")) {
                    message[i] = message[i].replace("{username}", "player");
                }
			}
		}
		return message;
	}

	private Player findNearestPlayer() {
		if (world == null) return null;

		Player player = null;
		double closestDistance = Double.MAX_VALUE;

		for (Object o : world.players) {
			if (o instanceof Player) {
				Player p = (Player) o;
				double distance = p.distanceToSqr(this.x, this.y, this.z);

				if (distance < closestDistance) {
					closestDistance = distance;
					player = p;
				}
			}
		}
		return player;
	}
}
