package net.findsnow.btabrine;

import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;


public class BTABrineConfig {
	private static final Toml PROPERTIES = new Toml("BTABrine Config");
	public static final TomlConfigHandler cfg;

	static {
		PROPERTIES.addCategory("Entity IDs")
			.addEntry("herobrine", 300);

		cfg = new TomlConfigHandler("btabrine", PROPERTIES);
	}

}
