package com.alexander.dungeonsadditions.init;

import com.alexander.dungeonsadditions.DungeonsAdditions;
import com.alexander.dungeonsadditions.items.CustomSpawnEggItem;

import com.alexander.dungeonsadditions.items.SamuraiArmorItem;
import com.alexander.dungeonsadditions.items.SamuraiClothesItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			DungeonsAdditions.MOD_ID);
	
	//melee weapons

	public static final RegistryObject<Item> TRIBAL_SPEAR = ITEMS.register("tribal_spear",
			() -> new SwordItem(ItemTier.IRON, 2, -1.8F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT)));

	//armor

	public static final RegistryObject<Item> SAMURAI_HELMET = ITEMS.register("samurai_helmet",
			() -> new SamuraiArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD,
					new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

	public static final RegistryObject<Item> SAMURAI_CHESTPLATE = ITEMS.register("samurai_chestplate",
			() -> new SamuraiArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST,
					new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

	public static final RegistryObject<Item> SAMURAI_LEGGINGS = ITEMS.register("samurai_leggings",
			() -> new SamuraiArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.LEGS,
					new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

	public static final RegistryObject<Item> SAMURAI_BOOTS = ITEMS.register("samurai_boots",
			() -> new SamuraiArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.FEET,
					new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

	// Clothes

	public static final RegistryObject<Item> SAMURAI_ROBES = ITEMS.register("samurai_robes",
			() -> new SamuraiClothesItem(ArmorMaterial.LEATHER, EquipmentSlotType.CHEST,
					new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

	public static final RegistryObject<Item> SAMURAI_PANTS = ITEMS.register("samurai_pants",
			() -> new SamuraiClothesItem(ArmorMaterial.LEATHER, EquipmentSlotType.LEGS,
					new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

	public static final RegistryObject<Item> SAMURAI_SHOES = ITEMS.register("samurai_shoes",
			() -> new SamuraiClothesItem(ArmorMaterial.LEATHER, EquipmentSlotType.FEET,
					new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

	//spawn eggs
	
	public static final RegistryObject<Item> JAILOR_SPAWN_EGG = ITEMS.register("jailor_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.JAILOR, 0x0c1e2a, 0x848989,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));
	
	public static final RegistryObject<Item> ILLAGER_WARDEN_SPAWN_EGG = ITEMS.register("illager_warden_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.ILLAGER_WARDEN, 0x394c5d, 0x7993ab,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));
	
	public static final RegistryObject<Item> ELITE_EVOKER_SPAWN_EGG = ITEMS.register("elite_evoker_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.ELITE_EVOKER, 0x131313, 0x7e00ff,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));
	
	public static final RegistryObject<Item> TRIBAGER_SPAWN_EGG = ITEMS.register("tribager_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.TRIBAGER, 0x959b9b, 0x67a124,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));
	
	public static final RegistryObject<Item> TRIBAGER_CHIEF_SPAWN_EGG = ITEMS.register("tribager_chief_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.TRIBAGER_CHIEF, 0x959b9b, 0xd8471a,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));
	
	public static final RegistryObject<Item> ROVING_MARKETER_SPAWN_EGG = ITEMS.register("roving_marketer_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.ROVING_MARKETER, 0x880c85, 0xfdab0b,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));

	public static final RegistryObject<Item> SAMURAI_SPAWN_EGG = ITEMS.register("samurai_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.ROVING_MARKETER, 0x722525, 0x7993ab,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));
}
