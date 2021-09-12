package com.alexander.dungeonsadditions.init;

import com.alexander.dungeonsadditions.DungeonsAdditions;
import com.alexander.dungeonsadditions.entities.JailorEntity;
import com.alexander.dungeonsadditions.entities.RovingMarketerEntity;
import com.alexander.dungeonsadditions.entities.RovingMarketerItemEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeInit {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			DungeonsAdditions.MOD_ID);

	public static final RegistryObject<EntityType<JailorEntity>> JAILOR = ENTITY_TYPES.register("jailor",
			() -> EntityType.Builder.of(JailorEntity::new, EntityClassification.MONSTER).sized(0.8F, 1.8F)
					.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "jailor").toString()));

	public static final RegistryObject<EntityType<RovingMarketerEntity>> ROVING_MARKETER = ENTITY_TYPES
			.register("roving_marketer",
					() -> EntityType.Builder.of(RovingMarketerEntity::new, EntityClassification.CREATURE)
							.sized(1.4F, 4.2F)
							.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "roving_marketer").toString()));

	public static final RegistryObject<EntityType<RovingMarketerItemEntity>> ROVING_MARKETER_ITEM = ENTITY_TYPES
			.register("roving_marketer_item",
					() -> EntityType.Builder.of(RovingMarketerItemEntity::new, EntityClassification.MISC)
							.sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20)
							.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "roving_marketer_item").toString()));

}
