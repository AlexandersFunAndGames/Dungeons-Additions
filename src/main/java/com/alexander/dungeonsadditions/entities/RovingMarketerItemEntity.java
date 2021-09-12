package com.alexander.dungeonsadditions.entities;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class RovingMarketerItemEntity extends Entity {
	   private static final DataParameter<ItemStack> DATA_ITEM = EntityDataManager.defineId(RovingMarketerItemEntity.class, DataSerializers.ITEM_STACK);
	   private static final DataParameter<ItemStack> DATA_BUY_ITEM = EntityDataManager.defineId(RovingMarketerItemEntity.class, DataSerializers.ITEM_STACK);
	   private static final DataParameter<Optional<ITextComponent>> DATA_CUSTOM_NAME2 = EntityDataManager.defineId(RovingMarketerEntity.class, DataSerializers.OPTIONAL_COMPONENT);
	   public final float bobOffs;
	   
	   public RovingMarketerItemEntity(EntityType<? extends RovingMarketerItemEntity> p_i50217_1_, World p_i50217_2_) {
	      super(p_i50217_1_, p_i50217_2_);	    	 
	    	 
	      this.bobOffs = (float)(Math.random() * Math.PI * 2.0D);
	   }
	   
	public void tick() {
		super.tick();
     
		 //ITEM NAME
	     IFormattableTextComponent name2 = new TranslationTextComponent(this.getItem().getDisplayName().getString() + this.getItem().getCount());

	     name2.setStyle(name2.getStyle().withColor(Color.fromRgb(0x962a94)));

	     this.setCustomName(name2);
   	 
   	 
	     //COST NAME
	     IFormattableTextComponent name = new TranslationTextComponent(this.getBuyItem().getDisplayName().getString() + this.getBuyItem().getCount());

	     name.setStyle(name.getStyle().setUnderlined(true).withColor(Color.fromRgb(0x962a94)));

	     this.setCustomName2(name);
   	 
	}
	
	   protected boolean isMovementNoisy() {
		      return false;
		   }
	   
	   public boolean isPickable() {
		      return !this.removed;
		   }
		   
		   
	
	@Override
	public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
		System.out.print("\r\n" + "interacted!");
		if (p_184230_1_.getItemInHand(p_184230_2_).getItem() == this.getBuyItem().getItem() && p_184230_1_.getItemInHand(p_184230_2_).getCount() >= this.getBuyItem().getCount()) {
		this.playSound(SoundEvents.END_PORTAL_FRAME_FILL, 1.0F, 1.0F);
		p_184230_1_.getItemInHand(p_184230_2_).shrink(this.getBuyItem().getCount());
		this.throwItemsTowardPlayer(this, p_184230_1_, this.getItem());
		this.remove();
		return ActionResultType.sidedSuccess(this.level.isClientSide);
		} else {
		return ActionResultType.FAIL;
		}
	}
	   
	   private void throwItemsTowardPlayer(RovingMarketerItemEntity p_234472_0_, PlayerEntity p_234472_1_, ItemStack p_234472_2_) {
		      throwItemsTowardPos(p_234472_0_, p_234472_2_, p_234472_1_.position());
		   }

		   private void throwItemsTowardPos(RovingMarketerItemEntity p_234476_0_, ItemStack p_234476_1_, Vector3d p_234476_2_) {
		      if (!p_234476_1_.isEmpty()) {		         
		         double d0 = p_234476_0_.getEyeY() - (double)0.3F;
		         ItemEntity itementity = new ItemEntity(p_234476_0_.level, p_234476_0_.getX(), d0, p_234476_0_.getZ(), p_234476_1_);
		         float f = 0.3F;
		         Vector3d vector3d = p_234476_2_.subtract(p_234476_0_.position());
		         vector3d = vector3d.normalize().scale((double)0.3F);
		         itementity.setDeltaMovement(vector3d);
		         itementity.setDefaultPickUpDelay();
		         p_234476_0_.level.addFreshEntity(itementity);
		      }

		   }

	   protected void defineSynchedData() {
		      this.getEntityData().define(DATA_CUSTOM_NAME2, Optional.empty());
		      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
		      this.getEntityData().define(DATA_BUY_ITEM, ItemStack.EMPTY);
		   }

	   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
		   
	         ITextComponent itextcomponent = this.getCustomName2();
	         if (itextcomponent != null) {
	        	 p_213281_1_.putString("CustomName2", ITextComponent.Serializer.toJson(itextcomponent));
	         }
	         
		      if (!this.getItem().isEmpty()) {
		         p_213281_1_.put("Item", this.getItem().save(new CompoundNBT()));
		      }
		      
		      if (!this.getBuyItem().isEmpty()) {
			         p_213281_1_.put("BuyItem", this.getBuyItem().save(new CompoundNBT()));
			      }

		   }

		   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
			   
               if (p_70037_1_.contains("CustomName2", 8)) {
                   String s = p_70037_1_.getString("CustomName2");

                   try {
                      this.setCustomName2(ITextComponent.Serializer.fromJson(s));
                   } catch (Exception exception) {
                      LOGGER.warn("Failed to parse entity custom name {}", s, exception);
                   }
                }
               
		      CompoundNBT compoundnbt = p_70037_1_.getCompound("Item");
		      this.setItem(ItemStack.of(compoundnbt));
		      if (this.getItem().isEmpty()) {
		         this.remove();
		      }
		      
		      CompoundNBT compoundnbtBuy = p_70037_1_.getCompound("BuyItem");
		      this.setBuyItem(ItemStack.of(compoundnbtBuy));
		      if (this.getBuyItem().isEmpty()) {
		         this.remove();
		      }

		   }
		   
		   public ItemStack getItem() {
			      return this.getEntityData().get(DATA_ITEM);
			   }

			   public void setItem(ItemStack p_92058_1_) {				  
		    	 
			      this.getEntityData().set(DATA_ITEM, p_92058_1_);
			   }
			   
			   public ItemStack getBuyItem() {
				      return this.getEntityData().get(DATA_BUY_ITEM);
				   }

				   public void setBuyItem(ItemStack p_92058_1_) {
					   
				    	 IFormattableTextComponent name = new TranslationTextComponent(this.getItem().getDisplayName().getString() + " " + this.getItem().getCount() + ". Cost: " + 
									this.getBuyItem().getDisplayName().getString() + " " + this.getBuyItem().getCount());

			    	 name.setStyle(name.getStyle().setUnderlined(true).withColor(Color.fromRgb(0xfdab0b)));

			    	 //System.out.print(this.getCustomName());

			    	 this.setCustomName(name);
			    	 
				      this.getEntityData().set(DATA_BUY_ITEM, p_92058_1_);
				   }
			   
			   @OnlyIn(Dist.CLIENT)
			   public float getSpin(float p_234272_1_) {
			      return ((float)this.tickCount + p_234272_1_) / 20.0F + this.bobOffs;
			   }
			   
			   public void setCustomName2(@Nullable ITextComponent p_200203_1_) {
				      this.entityData.set(DATA_CUSTOM_NAME2, Optional.ofNullable(p_200203_1_));
				   }

				   @Nullable
				   public ITextComponent getCustomName2() {
				      return this.entityData.get(DATA_CUSTOM_NAME2).orElse((ITextComponent)null);
				   }

				   public boolean hasCustomName2() {
				      return this.entityData.get(DATA_CUSTOM_NAME2).isPresent();
				   }

	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	}
