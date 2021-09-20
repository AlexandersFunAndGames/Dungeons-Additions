package com.alexander.dungeonsadditions.util;

import com.alexander.dungeonsadditions.DungeonsAdditions;
import com.alexander.dungeonsadditions.init.EntityTypeInit;
import com.alexander.dungeonsadditions.renderers.EliteEvokerRenderer;
import com.alexander.dungeonsadditions.renderers.IllagerWardenRenderer;
import com.alexander.dungeonsadditions.renderers.JailorRenderer;
import com.alexander.dungeonsadditions.renderers.RovingMarketerItemRenderer;
import com.alexander.dungeonsadditions.renderers.RovingMarketerRenderer;
import com.alexander.dungeonsadditions.renderers.TribagerChiefRenderer;
import com.alexander.dungeonsadditions.renderers.TribagerRenderer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DungeonsAdditions.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.JAILOR.get(),
		    	    manager -> new JailorRenderer(manager));
		    
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.ILLAGER_WARDEN.get(),
		    	    manager -> new IllagerWardenRenderer(manager));
		    
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.ELITE_EVOKER.get(),
		    	    manager -> new EliteEvokerRenderer(manager));
		    
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.TRIBAGER.get(),
		    	    manager -> new TribagerRenderer(manager));
		    
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.TRIBAGER_CHIEF.get(),
		    	    manager -> new TribagerChiefRenderer(manager));

		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.ROVING_MARKETER.get(),
		    	    manager -> new RovingMarketerRenderer(manager));
		    
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.ROVING_MARKETER_ITEM.get(),
		    	    manager -> new RovingMarketerItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));

	    }
}
