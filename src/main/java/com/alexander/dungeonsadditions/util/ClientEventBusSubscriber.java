package com.alexander.dungeonsadditions.util;

import com.alexander.dungeonsadditions.DungeonsAdditions;
import com.alexander.dungeonsadditions.models.SamuraiArmorModel;
import com.alexander.dungeonsadditions.models.SamuraiClothesModel;
import com.alexander.dungeonsadditions.client.renderers.DefaultExtendedGeoEntityRenderer;
import com.alexander.dungeonsadditions.client.renderers.armor.BaseDungeonsGeoArmorRenderer;
import com.alexander.dungeonsadditions.init.EntityTypeInit;
import com.alexander.dungeonsadditions.items.SamuraiArmorItem;
import com.alexander.dungeonsadditions.items.SamuraiClothesItem;
import com.alexander.dungeonsadditions.renderers.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod.EventBusSubscriber(modid = DungeonsAdditions.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

		@SubscribeEvent
		public static void clientSetup(final FMLClientSetupEvent event) {

			GeoArmorRenderer.registerArmorRenderer(SamuraiArmorItem.class, () ->
					new BaseDungeonsGeoArmorRenderer<SamuraiArmorItem>(new SamuraiArmorModel()));

			GeoArmorRenderer.registerArmorRenderer(SamuraiClothesItem.class, () ->
					new BaseDungeonsGeoArmorRenderer<SamuraiClothesItem>(new SamuraiClothesModel()));

		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.JAILOR.get(), JailorRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.SAMURAI.get(), manager -> new DefaultExtendedGeoEntityRenderer<>(manager, "geo/samurai.geo.json", "animations/samurai.animation.json", "textures/entities/samurai.png"));
			RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.ILLAGER_WARDEN.get(), IllagerWardenRenderer::new);
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.ELITE_EVOKER.get(), EliteEvokerRenderer::new);
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.TRIBAGER.get(), TribagerRenderer::new);
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.TRIBAGER_CHIEF.get(), TribagerChiefRenderer::new);
		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.ROVING_MARKETER.get(), RovingMarketerRenderer::new);

		    RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.ROVING_MARKETER_ITEM.get(), manager -> new RovingMarketerItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));

	    }
}
