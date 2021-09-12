package com.alexander.dungeonsadditions.renderers.layers;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alexander.dungeonsadditions.interfaces.IGeoTranslateRotate;
import com.alexander.dungeonsadditions.interfaces.IHasGeoHead;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@OnlyIn(Dist.CLIENT)
public class GeoHeadLayer<T extends LivingEntity & IAnimatable> extends GeoLayerRenderer<T> {

	public float xRot;
	public float yRot;
	public float zRot;
	
	public double xPos;
	public double yPos;
	public double zPos;

	public GeoHeadLayer(IGeoRenderer<T> renderer, double x, double y, double z, float xR, float yR, float zR) {
		super(renderer);
		this.xPos = x;
		this.yPos = y;
		this.zPos = z;
		this.xRot = xR;
		this.yRot = yR;
		this.zRot = zR;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
			T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {

	      ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlotType.HEAD);
	      if (!itemstack.isEmpty()) {
	         Item item = itemstack.getItem();
	         matrixStackIn.pushPose();
	         matrixStackIn.translate(xPos, yPos, zPos);	 
	         matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(xRot));	        	 
	         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(yRot));
		     matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(zRot));
	         boolean flag = entitylivingbaseIn instanceof VillagerEntity || entitylivingbaseIn instanceof ZombieVillagerEntity;
	         if (entitylivingbaseIn.isBaby() && !(entitylivingbaseIn instanceof VillagerEntity)) {
	            float f = 2.0F;
	            float f1 = 1.4F;
	            matrixStackIn.translate(0.0D, 0.03125D, 0.0D);
	            matrixStackIn.scale(0.7F, 0.7F, 0.7F);
	            matrixStackIn.translate(0.0D, 1.0D, 0.0D);
	         }

	         ((IHasGeoHead)this.getEntityModel()).translateToHead(matrixStackIn);
	         if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
	            float f3 = 1.1875F;
	            matrixStackIn.scale(1.1875F, -1.1875F, -1.1875F);
	            if (flag) {
	            	matrixStackIn.translate(0.0D, 0.0625D, 0.0D);
	            }

	            GameProfile gameprofile = null;
	            if (itemstack.hasTag()) {
	               CompoundNBT compoundnbt = itemstack.getTag();
	               if (compoundnbt.contains("SkullOwner", 10)) {
	                  gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
	               } else if (compoundnbt.contains("SkullOwner", 8)) {
	                  String s = compoundnbt.getString("SkullOwner");
	                  if (!StringUtils.isBlank(s)) {
	                     gameprofile = SkullTileEntity.updateGameprofile(new GameProfile((UUID)null, s));
	                     compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
	                  }
	               }
	            }

	            matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
	            SkullTileEntityRenderer.renderSkull((Direction)null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getType(), gameprofile, limbSwing, matrixStackIn, bufferIn, packedLightIn);
	         } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlot() != EquipmentSlotType.HEAD) {
	            float f2 = 0.625F;
	            matrixStackIn.translate(0.0D, -0.25D, 0.0D);
	            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
	            matrixStackIn.scale(0.625F, -0.625F, -0.625F);
	            if (flag) {
	            	matrixStackIn.translate(0.0D, 0.1875D, 0.0D);
	            }

	            Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD, false, matrixStackIn, bufferIn, packedLightIn);
	         }

	         matrixStackIn.popPose();
	      }
		
	}
	}