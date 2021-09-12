package com.alexander.dungeonsadditions.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.processor.IBone;

@OnlyIn(Dist.CLIENT)
public interface IGeoTranslateRotate {
	void translateAndRotate(IBone bone, MatrixStack p_228307_1_, boolean reverserotations);
}
