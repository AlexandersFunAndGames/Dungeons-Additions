package com.alexander.dungeonsadditions.models.entity;

import com.alexander.dungeonsadditions.DungeonsAdditions;
import com.alexander.dungeonsadditions.entities.JailorEntity;
import com.alexander.dungeonsadditions.interfaces.IGeoTranslateRotate;
import com.alexander.dungeonsadditions.interfaces.IHasGeoArm;
import com.alexander.dungeonsadditions.interfaces.IHasGeoHead;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class JailorModel extends AnimatedGeoModel implements IHasGeoArm, IHasGeoHead, IGeoTranslateRotate {
	   
		@Override
		public ResourceLocation getAnimationFileLocation(Object entity) {
			return new ResourceLocation(DungeonsAdditions.MOD_ID, "animations/jailor.animation.json");
		}

		@Override
		public ResourceLocation getModelLocation(Object entity) {
			return new ResourceLocation(DungeonsAdditions.MOD_ID, "geo/jailor.geo.json");
		}

		@Override
		public ResourceLocation getTextureLocation(Object entity) {
				return new ResourceLocation(DungeonsAdditions.MOD_ID, "textures/entities/jailor.png");
		}

		@Override
		public void setLivingAnimations(IAnimatable entity, Integer uniqueID, AnimationEvent customPredicate) {
			super.setLivingAnimations(entity, uniqueID, customPredicate);
			IBone head = this.getAnimationProcessor().getBone("head");
			
			IBone scythe = this.getAnimationProcessor().getBone("rapier");

			scythe.setHidden(true);
			
			LivingEntity entityIn = (LivingEntity) entity;
			EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
			if (extraData.headPitch != 0 || extraData.netHeadYaw != 0) {
				head.setRotationX(head.getRotationX() + (extraData.headPitch * ((float) Math.PI / 180F)));
				head.setRotationY(head.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 180F)));
			}
		}
		
		   public IBone getArm(HandSide p_191216_1_) {
			      return this.getAnimationProcessor().getBone("leftarm");
			   }

		   
		   

			   public void translateToHand(HandSide p_225599_1_, MatrixStack p_225599_2_) {
					  this.translateAndRotate(this.getAnimationProcessor().getBone("body"), p_225599_2_, false);
					  this.translateAndRotate(this.getAnimationProcessor().getBone("torax"), p_225599_2_, false);
					  this.translateAndRotate(this.getAnimationProcessor().getBone("arms2"), p_225599_2_, false);
					  this.translateAndRotate(this.getArm(p_225599_1_), p_225599_2_, false);
			   }
			   
			   public void translateAndRotate(IBone bone, MatrixStack p_228307_1_, boolean reverserotations) {
		      
				      p_228307_1_.translate((double)(bone.getPivotX() / 16.0F), (double)(bone.getPivotY() / 16.0F), (double)(bone.getPivotZ() / 16.0F));
				      
					      if (bone.getRotationZ() != 0.0F) {
				         p_228307_1_.mulPose(Vector3f.ZP.rotation(reverserotations ? -bone.getRotationZ() : bone.getRotationZ()));
				      }

				      if (bone.getRotationY() != 0.0F) {
				         p_228307_1_.mulPose(Vector3f.YP.rotation(reverserotations ? -bone.getRotationY() : bone.getRotationY()));
				      }

				      if (bone.getRotationX() != 0.0F) {
				         p_228307_1_.mulPose(Vector3f.XP.rotation(reverserotations ? -bone.getRotationX() : bone.getRotationX()));
				      }
				      
				   }

			@Override
			public IBone getHead() {
				return this.getAnimationProcessor().getBone("head");
			}
			
			   public void translateToHead(MatrixStack p_225599_2_) {
					  this.translateAndRotate(this.getAnimationProcessor().getBone("body"), p_225599_2_, true);
					  this.translateAndRotate(this.getAnimationProcessor().getBone("torax"), p_225599_2_, true);
					  this.translateAndRotate(this.getHead(), p_225599_2_, true);
			   }

			@Override
			public boolean shouldRenderHeldItem(LivingEntity entity) {
				JailorEntity jailor = ((JailorEntity)entity);
				return jailor.getAttackTicks() > 0;
			}
	}

