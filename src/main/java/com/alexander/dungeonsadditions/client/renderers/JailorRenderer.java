package com.alexander.dungeonsadditions.client.renderers;

import com.alexander.dungeonsadditions.entities.JailorEntity;
import com.alexander.dungeonsadditions.client.models.JailorModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JailorRenderer extends GeoEntityRenderer<JailorEntity> {
	@SuppressWarnings("unchecked")
	public JailorRenderer(EntityRendererManager renderManager) {
		super(renderManager, new JailorModel());
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
	
	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
	    if (bone.getName().equals("hand")) { // rArmRuff is the name of the bone you to set the item to attach too. Please see Note
	        stack.pushPose();
	        //You'll need to play around with these to get item to render in the correct orientation
	        stack.mulPose(Vector3f.XP.rotationDegrees(-75)); 
	        stack.mulPose(Vector3f.YP.rotationDegrees(0)); 
	        stack.mulPose(Vector3f.ZP.rotationDegrees(90));
	        //You'll need to play around with this to render the item in the correct spot.
	        stack.translate(0.2D, -0.3D, 0.6D); 
	        //Sets the scaling of the item.
	        stack.scale(1.0f, 1.0f, 1.0f); 
	        // Change mainHand to predefined Itemstack and TransformType to what transform you would want to use.
	        Minecraft.getInstance().getItemRenderer().renderStatic(mainHand, TransformType.THIRD_PERSON_RIGHT_HAND, packedLightIn, packedOverlayIn, stack, this.rtb);
	        stack.popPose();
	        bufferIn = rtb.getBuffer(RenderType.entityTranslucent(whTexture));
	    }
	    
	    if (bone.getName().equals("nose")) { // rArmRuff is the name of the bone you to set the item to attach too. Please see Note
	        stack.pushPose();
	        //You'll need to play around with these to get item to render in the correct orientation
	        stack.mulPose(Vector3f.XP.rotationDegrees(0)); 
	        stack.mulPose(Vector3f.YP.rotationDegrees(0)); 
	        stack.mulPose(Vector3f.ZP.rotationDegrees(0));
	        //You'll need to play around with this to render the item in the correct spot.
	        stack.translate(0.0D, 1.5D, -0.4D); 
	        //Sets the scaling of the item.
	        stack.scale(0.6F, 0.6F, 0.6F); 
	        // Change mainHand to predefined Itemstack and TransformType to what transform you would want to use.
	        Minecraft.getInstance().getItemRenderer().renderStatic(helmet, TransformType.HEAD, packedLightIn, packedOverlayIn, stack, this.rtb);
	        stack.popPose();
	        bufferIn = rtb.getBuffer(RenderType.entityTranslucent(whTexture));
	    }
	    super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
