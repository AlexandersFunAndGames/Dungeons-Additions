package com.alexander.dungeonsadditions.client.renderers;

import java.util.Random;

import com.alexander.dungeonsadditions.entities.RovingMarketerItemEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RovingMarketerItemRenderer extends EntityRenderer<RovingMarketerItemEntity> {
	   private final ItemRenderer itemRenderer;
	   private final Random random = new Random();

	   public RovingMarketerItemRenderer(EntityRendererManager p_i46167_1_, ItemRenderer p_i46167_2_) {
	      super(p_i46167_1_);
	      this.itemRenderer = p_i46167_2_;
	      this.shadowRadius = 0.15F;
	      this.shadowStrength = 0.75F;
	   }

   protected int getRenderAmount(ItemStack p_177078_1_) {
      int i = 1;
      if (p_177078_1_.getCount() > 48) {
         i = 5;
      } else if (p_177078_1_.getCount() > 32) {
         i = 4;
      } else if (p_177078_1_.getCount() > 16) {
         i = 3;
      } else if (p_177078_1_.getCount() > 1) {
         i = 2;
      }

      return i;
   }

   public void render(RovingMarketerItemEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.pushPose();
      ItemStack itemstack = p_225623_1_.getItem();
      int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
      this.random.setSeed((long)i);
      IBakedModel ibakedmodel = this.itemRenderer.getModel(itemstack, p_225623_1_.level, (LivingEntity)null);
      boolean flag = ibakedmodel.isGui3d();
      int j = this.getRenderAmount(itemstack);
      float f = 0.25F;
      float f1 = MathHelper.sin(((float)p_225623_1_.tickCount + p_225623_3_) / 10.0F + p_225623_1_.bobOffs) * 0.1F + 0.1F;
      float f2 = shouldBob() ? ibakedmodel.getTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y() : 0;
      p_225623_4_.translate(0.0D, (double)(f1 + 0.25F * f2), 0.0D);
      float f3 = p_225623_1_.getSpin(p_225623_3_);
      p_225623_4_.mulPose(Vector3f.YP.rotation(f3));
      if (!flag) {
         float f7 = -0.0F * (float)(j - 1) * 0.5F;
         float f8 = -0.0F * (float)(j - 1) * 0.5F;
         float f9 = -0.09375F * (float)(j - 1) * 0.5F;
         p_225623_4_.translate((double)f7, (double)f8, (double)f9);
      }

      for(int k = 0; k < j; ++k) {
         p_225623_4_.pushPose();
         if (k > 0) {
            if (flag) {
               float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               p_225623_4_.translate(shouldSpreadItems() ? f11 : 0, shouldSpreadItems() ? f13 : 0, shouldSpreadItems() ? f10 : 0);
            } else {
               float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               p_225623_4_.translate(shouldSpreadItems() ? f12 : 0, shouldSpreadItems() ? f14 : 0, 0.0D);
            }
         }

         this.itemRenderer.render(itemstack, ItemCameraTransforms.TransformType.GROUND, false, p_225623_4_, p_225623_5_, p_225623_6_, OverlayTexture.NO_OVERLAY, ibakedmodel);
         p_225623_4_.popPose();
         if (!flag) {
            p_225623_4_.translate(0.0, 0.0, 0.09375F);
         }
      }

      p_225623_4_.popPose();

      	//this.renderBuyItem(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      
      if (this.shouldShowName(p_225623_1_)) {
         this.renderNameTag(p_225623_1_, p_225623_1_.getCustomName(), p_225623_4_, p_225623_5_, p_225623_6_);
         this.renderNameTag2(p_225623_1_, p_225623_1_.getCustomName2(), p_225623_4_, p_225623_5_, p_225623_6_);
      }
      
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }
   
   public void renderBuyItem(RovingMarketerItemEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
	      p_225623_4_.pushPose();
	      ItemStack itemstack = p_225623_1_.getBuyItem();
	      int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
	      this.random.setSeed((long)i);
	      IBakedModel ibakedmodel = this.itemRenderer.getModel(itemstack, p_225623_1_.level, (LivingEntity)null);
	      boolean flag = ibakedmodel.isGui3d();
	      int j = this.getRenderAmount(itemstack);
	     // float f = 0.25F;
	     // float f1 = MathHelper.sin(((float)p_225623_1_.tickCount + p_225623_3_) / 10.0F + p_225623_1_.bobOffs) * 0.1F + 0.1F;
	     // float f2 = shouldBob() ? ibakedmodel.getTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y() : 0;
	      //p_225623_4_.translate(0.0D, (double)(f1 + 0.25F * f2), 0.0D);
	    //  float f3 = p_225623_1_.getSpin(p_225623_3_);
	    //  p_225623_4_.mulPose(Vector3f.YP.rotation(f3));
	      if (!flag) {
	         float f7 = -0.0F * (float)(j - 1) * 0.5F;
	         float f8 = -0.0F * (float)(j - 1) * 0.5F;
	         float f9 = -0.09375F * (float)(j - 1) * 0.5F;
	         p_225623_4_.translate((double)f7, (double)f8, (double)f9);
	      }
	      
	      p_225623_4_.scale(0.5F, 0.5F, 0.5F);
	      p_225623_4_.translate(0.25D, 0, 0);

	      for(int k = 0; k < j; ++k) {
	         p_225623_4_.pushPose();
	         if (k > 0) {
	            if (flag) {
	               float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
	               float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
	               float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
	               p_225623_4_.translate(shouldSpreadItems() ? f11 : 0, shouldSpreadItems() ? f13 : 0, shouldSpreadItems() ? f10 : 0);
	            } else {
	               float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
	               float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
	               p_225623_4_.translate(shouldSpreadItems() ? f12 : 0, shouldSpreadItems() ? f14 : 0, 0.0D);
	            }
	         }

	         this.itemRenderer.render(itemstack, ItemCameraTransforms.TransformType.GROUND, false, p_225623_4_, p_225623_5_, p_225623_6_, OverlayTexture.NO_OVERLAY, ibakedmodel);
	         p_225623_4_.popPose();
	         if (!flag) {
	            p_225623_4_.translate(0.0, 0.0, 0.09375F);
	         }
	      }

	      p_225623_4_.popPose();
   }
   protected void renderNameTag(RovingMarketerItemEntity p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
	      double d0 = this.entityRenderDispatcher.distanceToSqr(p_225629_1_);
	      if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(p_225629_1_, d0)) {
	         boolean flag = !p_225629_1_.isDiscrete();
	         float f = p_225629_1_.getBbHeight() + 0.5F;
	         int i = "deadmau5".equals(p_225629_2_.getString()) ? -10 : 0;
	         p_225629_3_.pushPose();
	         p_225629_3_.translate(0.0D, (double)f, 0.0D);
	         p_225629_3_.mulPose(this.entityRenderDispatcher.cameraOrientation());
	         p_225629_3_.scale(-0.0125F, -0.0125F, 0.0125F);
	         Matrix4f matrix4f = p_225629_3_.last().pose();
	         float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
	         int j = (int)(f1 * 255.0F) << 24;
	         FontRenderer fontrenderer = this.getFont();
	         float f2 = (float)(-fontrenderer.width(p_225629_2_) / 2);
	         fontrenderer.drawInBatch(p_225629_2_, f2, (float)i, 553648127, false, matrix4f, p_225629_4_, flag, j, p_225629_5_);
	         if (flag) {
	            fontrenderer.drawInBatch(p_225629_2_, f2, (float)i, -1, false, matrix4f, p_225629_4_, false, 0, p_225629_5_);
	         }

	         p_225629_3_.popPose();
	      }
	   }
   
   protected void renderNameTag2(RovingMarketerItemEntity p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
	      double d0 = this.entityRenderDispatcher.distanceToSqr(p_225629_1_);
	      if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(p_225629_1_, d0)) {
	         boolean flag = !p_225629_1_.isDiscrete();
	         float f = p_225629_1_.getBbHeight() - 0.1F;
	         int i = "deadmau5".equals(p_225629_2_.getString()) ? -10 : 0;
	         p_225629_3_.pushPose();
	         p_225629_3_.translate(0.0D, (double)f, 0.0D);
	         p_225629_3_.mulPose(this.entityRenderDispatcher.cameraOrientation());
	         p_225629_3_.scale(-0.0125F, -0.0125F, 0.0125F);
	         Matrix4f matrix4f = p_225629_3_.last().pose();
	         float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
	         int j = (int)(f1 * 255.0F) << 24;
	         FontRenderer fontrenderer = this.getFont();
	         float f2 = (float)(-fontrenderer.width(p_225629_2_) / 2);
	         fontrenderer.drawInBatch(p_225629_2_, f2, (float)i, 553648127, false, matrix4f, p_225629_4_, flag, j, p_225629_5_);
	         if (flag) {
	            fontrenderer.drawInBatch(p_225629_2_, f2, (float)i, -1, false, matrix4f, p_225629_4_, false, 0, p_225629_5_);
	         }

	         p_225629_3_.popPose();
	      }
	   }

   @Override
   protected boolean shouldShowName(RovingMarketerItemEntity p_177070_1_) {
	      return super.shouldShowName(p_177070_1_) && (p_177070_1_.shouldShowName() || p_177070_1_.hasCustomName() && p_177070_1_ == this.entityRenderDispatcher.crosshairPickEntity);
	   }
   
   public ResourceLocation getTextureLocation(RovingMarketerItemEntity p_110775_1_) {
      return AtlasTexture.LOCATION_BLOCKS;
   }
   
   /*==================================== FORGE START ===========================================*/

   /**
    * @return If items should spread out when rendered in 3D
    */
   public boolean shouldSpreadItems() {
      return true;
   }

   /**
    * @return If items should have a bob effect
    */
   public boolean shouldBob() {
      return true;
   }
   /*==================================== FORGE END =============================================*/
}
