package net.findsnow.btabrine.common.util;

import net.findsnow.btabrine.common.entity.interfaces.IStalkerEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.phys.Vec3;

public class HStalker implements IStalkerEntity {
	private Vec3 lastKnownPos;
	private long lastPosUpdate = 0;
	private static final long TELEPORT_DELAY = 30 * 20; // 30 seconds


	@Override
	public void trackPlayerPos(Player player) {
		lastKnownPos = Vec3.getTempVec3(player.x, player.y, player.z);
		lastPosUpdate = player.world.getWorldTime();
	}

	@Override
	public Vec3 getLastKnownPlayerPos() {
		return lastKnownPos;
	}

	@Override
	public boolean canTeleportToPlayer() {
		if (lastKnownPos == null || lastPosUpdate == 0) {
			return false;
		}

		long currentTime = System.currentTimeMillis();
		return (currentTime - lastPosUpdate) >= TELEPORT_DELAY;
	}

	@Override
	public void resetTeleportCooldown() {
		lastPosUpdate = System.currentTimeMillis();
	}

	@Override
	public void teleportToLastKnownPos() {

	}
}
