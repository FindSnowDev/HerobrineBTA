package net.findsnow.btabrine.common.entity.interfaces;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

/**
 * Interface for entities that can detect various player and world conditions.
 */
public interface IDetectorEntity {
    /**
     * Checks if the player is currently underground.
     *
     * @param player The player to check
     * @param world The world the player is in
     * @return true if the player is underground, false otherwise
     */
    boolean isPlayerUnderground(Player player, World world);

    /**
     * Checks if the player is near their home or a structure considered a home.
     *
     * @param player The player to check
     * @param world The world the player is in
     * @return true if the player is near home, false otherwise
     */
    boolean isPlayerNearHome(Player player, World world);

    /**
     * Checks if the player is in a wild/wilderness area.
     *
     * @param player The player to check
     * @param world The world the player is in
     * @return true if the player is in the wild, false otherwise
     */
    boolean isPLayerInWild(Player player, World world);

    /**
     * Checks if it's currently night time in the world.
     *
     * @param world The world to check the time in
     * @return true if it's night time, false otherwise
     */
    boolean isNightTime(World world);

    /**
     * Scans the area around a player for windows that the entity could peek through.
     *
     * @param player The player to check around
     * @param world The world to check in
     * @return true if windows are found near the player, false otherwise
     */
    boolean checkForWindowsNearPlayer(Player player, World world);


	boolean findWindowPos(Player player, World world, float range, Entity entityToPos);
}
