package com.alexander.dungeonsadditions.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.processor.IBone;

@OnlyIn(Dist.CLIENT)
public interface IHasGeoArm {
	
	IBone getArm(HandSide p_191216_1_);
	
	boolean shouldRenderHeldItem(LivingEntity entity);
	
	void translateToHand(HandSide p_225599_1_, MatrixStack p_225599_2_);
}
