package com.alexander.dungeonsadditions.renderers;

import com.alexander.dungeonsadditions.entities.JailorEntity;
import com.alexander.dungeonsadditions.models.entity.JailorModel;
import com.alexander.dungeonsadditions.renderers.layers.GeoHeadLayer;
import com.alexander.dungeonsadditions.renderers.layers.GeoHeldItemLayer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JailorRenderer extends GeoEntityRenderer<JailorEntity> {
	@SuppressWarnings("unchecked")
	public JailorRenderer(EntityRendererManager renderManager) {
		super(renderManager, new JailorModel());
		//this.addLayer(new GeoEyeLayer<>(this, new ResourceLocation(DungeonsMobs.MODID, "textures/entity/enchanter/enchanter_eyes.png")));
	    this.addLayer(new GeoHeadLayer<>(this, 0.0, 4.75, -0.5, 0.0F, 0.0F, 180F));
		this.addLayer(new GeoHeldItemLayer(this, 0.15, -2.6, -1.5, 90F, 180F, -90F));
	}
	
	protected void applyRotations(JailorEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks,
			float rotationYaw, float partialTicks) {
        float scaleFactor = 0.9375F;
        matrixStackIn.scale(scaleFactor, scaleFactor, scaleFactor);
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}

	@Override
	public RenderType getRenderType(JailorEntity animatable, float partialTicks, MatrixStack stack,
			IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
