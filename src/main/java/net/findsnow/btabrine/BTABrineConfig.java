package net.findsnow.btabrine;

import net.findsnow.btabrine.common.registry.BTABEntities;
import net.minecraft.core.entity.Entity;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BTABrineConfig {
	private static final Toml PROPERTIES = new Toml("BTABrine Config");
	public static final TomlConfigHandler cfg;

	static {
		PROPERTIES.addCategory("Entity IDs")
			.addEntry("herobrine", 300);

		cfg = new TomlConfigHandler("btabrine", PROPERTIES);
	}

}
