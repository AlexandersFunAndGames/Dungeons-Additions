package com.alexander.dungeonsadditions.world;

import com.alexander.dungeonsadditions.config.DungeonsAdditionsConfig;
import com.alexander.dungeonsadditions.init.EntityTypeInit;

import net.minecraft.world.raid.Raid;

public class RaidEntries {

    public static void initWaveMemberEntries(){
        /*
      VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
      EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
      PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
      WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
      RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});
         */

        // WARRIOR         
    		if (DungeonsAdditionsConfig.jailor_raid_spawning.get()) {
    			Raid.WaveMember.create("jailor", EntityTypeInit.JAILOR.get(), new int[]{0, 0, 0, 0, 0, 1, 0, 2});
    		}
    }
}