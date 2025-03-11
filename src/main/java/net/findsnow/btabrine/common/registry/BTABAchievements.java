package net.findsnow.btabrine.common.registry;

import net.findsnow.btabrine.BTABrine;
import net.findsnow.btabrine.common.util.BTABrineConfig;
import net.minecraft.client.gui.achievements.data.AchievementPage;
import net.minecraft.client.gui.achievements.data.AchievementPages;
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;

import java.util.ArrayList;
import java.util.List;

public class BTABAchievements {
	public static List<Achievement> herobrineAchievements = new ArrayList<>();
	private static AchievementPage herobrineAchievementPage = null;

	public static void registerAchievements() {
		boolean enabled = BTABrineConfig.areAchievementsEnabled();
		BTABrine.LOGGER.info("Achievements enabled " + enabled);

		if (enabled) {
			Achievement HELLO = new Achievement(NamespaceID.getPermanent("btabrine", "hello"), "hello", Blocks.TORCH_REDSTONE_ACTIVE.id(), null)
				.setType(Achievement.TYPE_NORMAL)
				.setClientsideAchievement();
			Achievement SWEETER_DREAMS = new Achievement(NamespaceID.getPermanent("btabrine", "sweeter_dreams"), "sweeterDreams", Items.BED, HELLO)
				.setType(Achievement.TYPE_NORMAL)
				.setClientsideAchievement();

			herobrineAchievements.add(HELLO);
			herobrineAchievements.add(SWEETER_DREAMS);

			herobrineAchievementPage = AchievementPages.register(
				new BTABAchievementPage(Blocks.NETHERRACK.getDefaultStack(), "gui.achievements.page.herobrine"));
			herobrineAchievementPage.addAchievement(herobrineAchievements.get(0), 0, 0);
			herobrineAchievementPage.addAchievement(herobrineAchievements.get(1), 2, 1);
		}
	}
}
