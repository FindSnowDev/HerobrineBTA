package net.findsnow.btabrine.common.entity.prototype;

import net.findsnow.btabrine.common.entity.base.HerobrineBase;
import net.findsnow.btabrine.common.entity.interfaces.IStalkerEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class HStalkerEntity extends HerobrineBase implements IStalkerEntity {

	private static final float SPAWN_CHANCE = 0.05F;
	private static final float IMMEDIATE_DESPAWN_DISTANCE = 1.0F;
	private static final float STALK_DISTANCE = 15.0F;

	private boolean isFleeing = false;
	private int lifespan = 1200;
	private Player targetPlayer;
	private int ticksExisted = 0;
	private Random random = new Random();

	private Vec3 lastPos;

	public HStalkerEntity(@Nullable World world) {
		super(world);
		this.isJumping = false;
		this.moveSpeed = 0;
		this.lastPos = Vec3.getTempVec3(this.x, this.y, this.z);
	}

	@Override
	public void trackPlayerPos(Player player) {

	}

	@Override
	public Vec3 getLastKnownPlayerPos() {
		return null;
	}

	@Override
	public boolean canTeleportToPlayer() {
		return false;
	}

	@Override
	public void resetTeleportCooldown() {

	}

	@Override
	public void teleportToLastKnownPos() {

	}
}
