package com.alexander.dungeonsadditions.init;

import com.alexander.dungeonsadditions.DungeonsAdditions;
import com.alexander.dungeonsadditions.entities.*;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeInit {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
			.create(ForgeRegistries.ENTITIES, DungeonsAdditions.MOD_ID);

	public static final RegistryObject<EntityType<JailorEntity>> JAILOR = ENTITY_TYPES
			.register("jailor",
					() -> EntityType.Builder.of(JailorEntity::new, EntityClassification.MONSTER)
							.sized(0.8F, 1.8F)
							.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "jailor").toString()));


	public static final RegistryObject<EntityType<SamuraiEntity>> SAMURAI = ENTITY_TYPES
			.register("samurai",
					() -> EntityType.Builder.of(SamuraiEntity::new, EntityClassification.MONSTER)
							.sized(0.8F, 1.8F)
							.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "samurai").toString()));

	public static final RegistryObject<EntityType<IllagerWardenEntity>> ILLAGER_WARDEN = ENTITY_TYPES
			.register("illager_warden",
					() -> EntityType.Builder.of(IllagerWardenEntity::new, EntityClassification.MONSTER)
							.sized(0.68F, 1.95F)
							.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "illager_warden").toString()));
	
	public static final RegistryObject<EntityType<EliteEvokerEntity>> ELITE_EVOKER = ENTITY_TYPES
			.register("elite_evoker",
					() -> EntityType.Builder.of(EliteEvokerEntity::new, EntityClassification.MONSTER)
							.sized(0.6F, 1.95F)
							.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "elite_evoker").toString()));
	
	public static final RegistryObject<EntityType<TribagerEntity>> TRIBAGER = ENTITY_TYPES
			.register("tribager",
					() -> EntityType.Builder.of(TribagerEntity::new, EntityClassification.MONSTER)
							.sized(0.6F, 1.95F)
							.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "tribager").toString()));
	
	public static final RegistryObject<EntityType<TribagerChiefEntity>> TRIBAGER_CHIEF = ENTITY_TYPES
			.register("tribager_chief",
					() -> EntityType.Builder.of(TribagerChiefEntity::new, EntityClassification.MONSTER)
							.sized(0.6F, 2.75F)
							.build(new ResourceLocation(DungeonsAdditions.MOD_ID, "tribager_chief").toString()));

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
