package com.alexander.dungeonsadditions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alexander.dungeonsadditions.entities.JailorEntity;
import com.alexander.dungeonsadditions.entities.RovingMarketerEntity;
import com.alexander.dungeonsadditions.entities.RovingMarketerItemEntity;
import com.alexander.dungeonsadditions.init.EntityTypeInit;
import com.alexander.dungeonsadditions.init.ItemInit;
import com.alexander.dungeonsadditions.init.SoundEventInit;
import com.alexander.dungeonsadditions.items.CustomSpawnEggItem;
import com.alexander.dungeonsadditions.world.RaidEntries;
import com.alexander.dungeonsadditions.world.SensorMapModifier;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

@Mod("dungeonsadditions")
@Mod.EventBusSubscriber(modid = DungeonsAdditions.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class DungeonsAdditions
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "dungeonsadditions";

    public DungeonsAdditions() {

    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	
    	bus.addListener(this::commonSetup);
    	GeckoLib.initialize();
    	SoundEventInit.SOUNDS.register(bus);
    	EntityTypeInit.ENTITY_TYPES.register(bus);
    	ItemInit.ITEMS.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
    	CustomSpawnEggItem.initSpawnEggs();
    }
    
    @SuppressWarnings("deprecation")
    public void commonSetup(final FMLCommonSetupEvent event) {
    	
        event.enqueueWork(RaidEntries::initWaveMemberEntries);
        event.enqueueWork(SensorMapModifier::replaceSensorMaps);
        
    	DeferredWorkQueue.runLater(() -> {
    		GlobalEntityTypeAttributes.put(EntityTypeInit.JAILOR.get(), JailorEntity.createAttributes().build());
    		GlobalEntityTypeAttributes.put(EntityTypeInit.ROVING_MARKETER.get(), RovingMarketerEntity.createAttributes().build());
    	});
    	
    }
}
