package com.alexander.dungeonsadditions.world;

import java.util.HashMap;
import java.util.Map;

import com.alexander.dungeonsadditions.init.EntityTypeInit;
import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;

public class SensorMapModifier {


    public static void replaceSensorMaps(){
        ImmutableMap<EntityType<?>, Float> oldImmutableVillagerHostiles = VillagerHostilesSensor.ACCEPTABLE_DISTANCE_FROM_HOSTILES;
        Map<EntityType<?>, Float> villagerHostiles = new HashMap<>(oldImmutableVillagerHostiles);

        // only use for Zombies and Raiders
        villagerHostiles.put(EntityTypeInit.JAILOR.get(), 5.0F);

        VillagerHostilesSensor.ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.copyOf(villagerHostiles);
    }
}
