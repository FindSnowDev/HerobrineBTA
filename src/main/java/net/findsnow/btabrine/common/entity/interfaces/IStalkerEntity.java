package net.findsnow.btabrine.common.entity.interfaces;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.phys.Vec3;

/**
 * Interface for entities that can stalk and teleport to players.
 */
public interface IStalkerEntity {
	/**
     * Stores the player's current position for tracking.
     *
     * @param player The player whose position should be tracked
     */
    void trackPlayerPos(Player player);

    /**
     * Gets the last known position of the tracked player.
     *
     * @return The vector position where the player was last seen
     */
    Vec3 getLastKnownPlayerPos();

    /**
     * Checks if enough time has passed since the last teleport.
     *
     * @return true if the entity can teleport to the player, false otherwise
     */
    boolean canTeleportToPlayer();

    /**
     * Resets the cooldown timer after a teleportation occurs.
     */
    void resetTeleportCooldown();

    /**
     * Handles the logic for teleporting to the player's last known position.
     */
    void teleportToLastKnownPos();
}
