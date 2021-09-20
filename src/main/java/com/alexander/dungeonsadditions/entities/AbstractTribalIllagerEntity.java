package com.alexander.dungeonsadditions.entities;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class AbstractTribalIllagerEntity extends MonsterEntity {
	
	   public static final DataParameter<Integer> ATTACK_TICKS = EntityDataManager.defineId(AbstractTribalIllagerEntity.class, DataSerializers.INT);
	
	   protected AbstractTribalIllagerEntity(EntityType<? extends AbstractTribalIllagerEntity> p_i48556_1_, World p_i48556_2_) {
		      super(p_i48556_1_, p_i48556_2_);
		   }

		   protected void registerGoals() {
		      super.registerGoals();
		   }
		   
		    protected void defineSynchedData() {
		        super.defineSynchedData();
			    this.entityData.define(ATTACK_TICKS, 0);
		    }
		    
			public void baseTick() {
				super.baseTick();
				
				if (this.getAttackTicks() > 0) {
					this.setAttackTicks(this.getAttackTicks() - 1);
				}
			}
			
			   public int getAttackTicks() {
				      return this.entityData.get(ATTACK_TICKS);
				   }

				   public void setAttackTicks(int p_189794_1_) {	   
				      this.entityData.set(ATTACK_TICKS, p_189794_1_);
				   }
				   
		   public CreatureAttribute getMobType() {
		      return CreatureAttribute.ILLAGER;
		   }
}
