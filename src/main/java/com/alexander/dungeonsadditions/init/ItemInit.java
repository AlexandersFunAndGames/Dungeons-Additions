package com.alexander.dungeonsadditions.init;

import com.alexander.dungeonsadditions.DungeonsAdditions;
import com.alexander.dungeonsadditions.items.CustomSpawnEggItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			DungeonsAdditions.MOD_ID);

	//spawn eggs
	
	public static final RegistryObject<Item> JAILOR_SPAWN_EGG = ITEMS.register("jailor_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.JAILOR, 0x0c1e2a, 0x848989,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));
	
	public static final RegistryObject<Item> ROVING_MARKETER_SPAWN_EGG = ITEMS.register("roving_marketer_spawn_egg",
			() -> new CustomSpawnEggItem(EntityTypeInit.ROVING_MARKETER, 0x880c85, 0xfdab0b,
					new Item.Properties().tab(ItemGroup.TAB_MISC)));
}
