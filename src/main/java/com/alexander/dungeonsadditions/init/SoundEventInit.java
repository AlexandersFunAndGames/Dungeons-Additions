package com.alexander.dungeonsadditions.init;

import com.alexander.dungeonsadditions.DungeonsAdditions;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEventInit {
	
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
			DungeonsAdditions.MOD_ID);
	
	public static final RegistryObject<SoundEvent> JAILOR_IDLE = SOUNDS.register("entity.jailor.idle", () -> new SoundEvent(new ResourceLocation(DungeonsAdditions.MOD_ID, "entity.jailor.idle")));
	public static final RegistryObject<SoundEvent> JAILOR_HURT = SOUNDS.register("entity.jailor.hurt", () -> new SoundEvent(new ResourceLocation(DungeonsAdditions.MOD_ID, "entity.jailor.hurt")));
	public static final RegistryObject<SoundEvent> JAILOR_DEATH = SOUNDS.register("entity.jailor.death", () -> new SoundEvent(new ResourceLocation(DungeonsAdditions.MOD_ID, "entity.jailor.death")));
	public static final RegistryObject<SoundEvent> JAILOR_ATTACK = SOUNDS.register("entity.jailor.attack", () -> new SoundEvent(new ResourceLocation(DungeonsAdditions.MOD_ID, "entity.jailor.attack")));
	
}
