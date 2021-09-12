package com.alexander.dungeonsadditions.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.processor.IBone;

@OnlyIn(Dist.CLIENT)
public interface IHasGeoHead {
   IBone getHead();
   
	void translateToHead(MatrixStack p_225599_2_);
}
