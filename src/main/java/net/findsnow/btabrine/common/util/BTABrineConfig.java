package net.findsnow.btabrine.common.util;

import net.fabricmc.loader.api.FabricLoader;
import net.findsnow.btabrine.BTABrine;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class BTABrineConfig {
	public static boolean achievementsEnabled = true;
	public static boolean shrineAppearance = true;

	private static final String CONFIG_FILE = "btabrine.properties";

	public static boolean areAchievementsEnabled() {
		return achievementsEnabled;
	}

	public static void setAchievementsEnabled(boolean enabled) {
		achievementsEnabled = enabled;
		saveConfig();
	}

	public static boolean isShrineAppearanceEnabled() {
        return shrineAppearance;
    }

	public static void setShrineAppearanceEnabled(boolean enabled) {
        shrineAppearance = enabled;
		saveConfig();
    }

	public static void saveConfig() {
		try {
			File configDir = FabricLoader.getInstance().getConfigDir().toFile();
			if (!configDir.exists()) {
				configDir.mkdirs();
			}

			File configFile = new File(configDir, CONFIG_FILE);
			Properties props = new Properties();

			props.setProperty("achievements_enabled", String.valueOf(achievementsEnabled));
			props.setProperty("summon_enabled", String.valueOf(shrineAppearance));
			FileWriter writer = new FileWriter(configFile);
			props.store(writer, "BTABRINE Config");
			writer.close();
		} catch (IOException e) {
			BTABrine.LOGGER.error("Failed to save configuration", e);
		}
	}

	public static void loadConfig() {
		try {
			File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE);
			if (!configFile.exists()) {
				saveConfig();
				return;
			}

			Properties props = new Properties();
			FileReader reader = new FileReader(configFile);
			props.load(reader);
			reader.close();

			achievementsEnabled = Boolean.parseBoolean(props.getProperty("achievements_enabled", "true"));
			shrineAppearance = Boolean.parseBoolean(props.getProperty("summon_enabled", "true"));
		} catch (IOException e) {
			BTABrine.LOGGER.error("Failed to load configuration", e);
		}
	}
}
