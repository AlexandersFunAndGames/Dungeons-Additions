package com.alexander.dungeonsadditions.models.entity;

import com.alexander.dungeonsadditions.DungeonsAdditions;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class TribagerModel extends AnimatedGeoModel {
	   
		@Override
		public ResourceLocation getAnimationFileLocation(Object entity) {
			return new ResourceLocation(DungeonsAdditions.MOD_ID, "animations/tribal_illager.animation.json");
		}

		@Override
		public ResourceLocation getModelLocation(Object entity) {
			return new ResourceLocation(DungeonsAdditions.MOD_ID, "geo/tribal_illager.geo.json");
		}

		@Override
		public ResourceLocation getTextureLocation(Object entity) {
				return new ResourceLocation(DungeonsAdditions.MOD_ID, "textures/entities/tribal_illager.png");
		}

		@Override
		public void setLivingAnimations(IAnimatable entity, Integer uniqueID, AnimationEvent customPredicate) {
			super.setLivingAnimations(entity, uniqueID, customPredicate);
			IBone head = this.getAnimationProcessor().getBone("head");
			
			IBone spear = this.getAnimationProcessor().getBone("sword");
			IBone dartBlower = this.getAnimationProcessor().getBone("dartthrower");
			
			spear.setHidden(true);
			dartBlower.setHidden(true);
			
			LivingEntity entityIn = (LivingEntity) entity;
			EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
			if (extraData.headPitch != 0 || extraData.netHeadYaw != 0) {
				head.setRotationX(head.getRotationX() + (extraData.headPitch * ((float) Math.PI / 180F)));
				head.setRotationY(head.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 180F)));
			}
		}
	}

