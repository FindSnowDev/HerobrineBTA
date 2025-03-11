package net.findsnow.btabrine.common.registry;

import net.minecraft.client.gui.achievements.ScreenAchievements;
import net.minecraft.client.gui.achievements.data.AchievementPage;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BTABAchievementPage extends AchievementPage {
	protected final ItemStack icon;
	@NotNull
	protected final String key;

	public BTABAchievementPage(ItemStack icon, @NotNull String key) {
		this.icon = icon;
		this.key = key;
	}

	@Override
	public @NotNull String getName() {
		return I18n.getInstance().translateNameKey(this.key);
	}

	@Override
	public @NotNull String getDescription() {
		return I18n.getInstance().translateDescKey(this.key);
	}

	@NotNull
	@Override
	public AchievementEntry onOpenAchievement() {
		return getAchievementEntries().isEmpty() ? null : getAchievementEntries().get(0);
	}

	@Override
	public @Nullable IconCoordinate getBackgroundTile(ScreenAchievements screenAchievements, int i, Random random, int j, int k) {
		random.setSeed(random.nextLong() + (long)this.key.hashCode());

		int rand = random.nextInt(100);
		if (rand < 40) {
			return getTextureFromBlock(Blocks.STONE);
		} else if (rand < 70) {
			return getTextureFromBlock(Blocks.COBBLE_STONE);
		} else {
			return getTextureFromBlock(Blocks.BRICK_STONE_POLISHED_MOSSY);
		}
	}

	@Override
	public void postProcessBackground(ScreenAchievements screenAchievements, Random random, ScreenAchievements.BGLayer bGLayer, int i, int j) {
		if (bGLayer.id == 0) {
			for (int x = -100; x <= 100; x++) {
				if (random.nextInt(50) == 0) {
					int glitchSize = 1 + random.nextInt(2);
					this.carveCircle(screenAchievements, bGLayer, x - i, 25 + random.nextInt(10) - j, glitchSize);
				}
			}
		}
	}

	private void carveCircle(ScreenAchievements screen, ScreenAchievements.BGLayer layer, int centerX, int centerY, int size) {
		for (int i = centerX - size; i <= centerX + size; i++) {
			for (int j = centerY - size; j < centerY + size; j++) {
				double diffX = (double)i + 0.5 - (double)centerX;
				double diffY = (double)j + 0.5 - (double)centerY;
				if (diffX * diffX + diffY * diffY < (double)(size * size)) {
					layer.put(null, i, j);
				}
			}
		}
	}

	@Override
	public @NotNull ItemStack getIcon() {
		return this.icon;
	}

	@Override
	public int backgroundLayers() {
		return 2;
	}

	@Override
	public int backgroundColor() {
		return 0xFF1A1A1A;
	}

	@Override
	public IconCoordinate getAchievementIcon(Achievement achievement) {
		return TextureRegistry.getTexture(achievement.getType().texture);
	}

	@Override
	public int lineColorLocked(boolean bl) {
		return 0;
	}

	@Override
	public int lineColorUnlocked(boolean bl) {
		return 7368816;
	}

	@Override
	public int lineColorCanUnlock(boolean bl) {
		return 65280;
	}
}
