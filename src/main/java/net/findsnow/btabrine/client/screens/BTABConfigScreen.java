package net.findsnow.btabrine.client.screens;

import net.findsnow.btabrine.common.compat.BTABModMenu;
import net.findsnow.btabrine.common.util.BTABrineConfig;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.lang.I18n;

public class BTABConfigScreen extends Screen {
	private final Screen parent;
	private static final int TOGGLE_ACHIEVEMENTS_BUTTON_ID = 0;
	private static final int SUMMON_BUTTON_ID = 1;
	private static final int DONE_BUTTON_ID = 2;

	public BTABConfigScreen(Screen parent) {
		this.parent = parent;
	}

	@Override
	public void init() {
		super.init();
		I18n i18n = I18n.getInstance();
		String achievementButtonText = i18n.translateKey("btabrine.config.achievements") + ": " +
			(BTABrineConfig.areAchievementsEnabled() ? i18n.translateKey("btabrine.config.enabled") : i18n.translateKey("btabrine.config.disabled"));
		String summonButtonText = i18n.translateKey("btabrine.config.summon") + ": " +
			(BTABrineConfig.areAchievementsEnabled() ? i18n.translateKey("btabrine.config.enabled") : i18n.translateKey("btabrine.config.disabled"));
		this.buttons.add(new ButtonElement(TOGGLE_ACHIEVEMENTS_BUTTON_ID, this.width / 2 - 100, this.height / 4 + 24, 200, 20, achievementButtonText));
		this.buttons.add(new ButtonElement(SUMMON_BUTTON_ID, this.width / 2 - 100, this.height / 4 + 64, 200, 20, summonButtonText));

		this.buttons.add(new ButtonElement(DONE_BUTTON_ID, this.width / 2 - 100, this.height / 4 + 104, 200, 20, i18n.translateKey("btabrine.gui.done")));
	}

	@Override
	protected void buttonClicked(ButtonElement button) {
		super.buttonClicked(button);
		I18n i18n = I18n.getInstance();
		switch (button.id) {
			case TOGGLE_ACHIEVEMENTS_BUTTON_ID:
				boolean newValue = !BTABrineConfig.areAchievementsEnabled();
				BTABrineConfig.setAchievementsEnabled(newValue);

				String achievementButtonText = i18n.translateKey("btabrine.config.achievements") + ": " +
					(newValue ? i18n.translateKey("btabrine.config.enabled") : i18n.translateKey("btabrine.config.disabled"));
				button.displayString = achievementButtonText;
				break;
			case SUMMON_BUTTON_ID:
				boolean newValue2 = !BTABrineConfig.isShrineAppearanceEnabled();
				BTABrineConfig.setShrineAppearanceEnabled(newValue2);

				String summonButtonText = i18n.translateKey("btabrine.config.summon") + ": " +
					(newValue2 ? i18n.translateKey("btabrine.config.enabled") : i18n.translateKey("btabrine.config.disabled"));
				button.displayString = summonButtonText;
				break;

			case DONE_BUTTON_ID:
				mc.displayScreen(parent);
				break;
		}
	}

	@Override
	public void render(int mx, int my, float partialTick) {
		this.renderBackground();

		I18n i18n = I18n.getInstance();
		this.drawStringCentered(this.font, i18n.translateKey("btabrine.config.title"), this.width / 2, 20, 0xFFFFFF);

		super.render(mx, my, partialTick);
	}
}
