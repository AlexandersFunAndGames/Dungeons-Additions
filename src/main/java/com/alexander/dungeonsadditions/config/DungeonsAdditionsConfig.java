package com.alexander.dungeonsadditions.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class DungeonsAdditionsConfig {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	
	public static final ForgeConfigSpec.ConfigValue<Boolean> jailor_raid_spawning;
	
	public static final ForgeConfigSpec.ConfigValue<Boolean> HP_retextured_marketer;
	
	static {
		BUILDER.push("Configure Your Dungeons Additions Mod!");
		
		BUILDER.comment("\r\n" + "Spawn Configurations" + "\r\n");
		jailor_raid_spawning = BUILDER.comment("\r\n" + "Controlls whether the Jailor spawns in raids, usually set to true").define("Jailor Raid Spawning", true);
		
		BUILDER.comment("\r\n" + "Texture Tweaks" + "\r\n");
		HP_retextured_marketer = BUILDER.comment("\r\n" + "Controlls whether Roving Marketer has its retexture by the one and only 8HP, usually set to true").define("8HP Retextured Marketer", true);
		
		BUILDER.pop();
		SPEC = BUILDER.build();
	}
}
