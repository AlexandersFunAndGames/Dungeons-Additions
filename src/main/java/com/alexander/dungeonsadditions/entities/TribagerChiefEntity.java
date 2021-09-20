package com.alexander.dungeonsadditions.entities;

import javax.annotation.Nullable;

import com.alexander.dungeonsadditions.init.ItemInit;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TribagerChiefEntity extends AbstractTribalIllagerEntity implements IAnimatable {

	   AnimationFactory factory = new AnimationFactory(this);
	   
	   public TribagerChiefEntity(EntityType<? extends TribagerChiefEntity> p_i50189_1_, World p_i50189_2_) {
		      super(p_i50189_1_, p_i50189_2_);
		   }
	   
	   protected void registerGoals() {
		      super.registerGoals();
		      this.goalSelector.addGoal(0, new SwimGoal(this));
		      this.goalSelector.addGoal(2, new TribagerChiefEntity.AttackGoal(this));
		      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractTribalIllagerEntity.class)).setAlertOthers());
		      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TribagerChiefEntity.class, true).setUnseenMemoryTicks(600));
		      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true).setUnseenMemoryTicks(600));
		     // this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true).setUnseenMemoryTicks(600));
		      //this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true).setUnseenMemoryTicks(600));
		      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 1.0D));
		      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
		   }
	   
	   @Nullable
	   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
	      ILivingEntityData ilivingentitydata = super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
	      this.populateDefaultEquipmentSlots(p_213386_2_);
	      this.populateDefaultEquipmentEnchantments(p_213386_2_);
	      return ilivingentitydata;
	   }

	    @Override
	    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {

	                //this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemInit.TRIBAL_SPEAR.get()));
	    }
	   
	   public static AttributeModifierMap.MutableAttribute createAttributes() {
		      return MonsterEntity.createMonsterAttributes().add(Attributes.ATTACK_KNOCKBACK, 1.5D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9D).add(Attributes.MOVEMENT_SPEED, (double)0.225F).add(Attributes.FOLLOW_RANGE, 30.0D).add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.ATTACK_DAMAGE, 7.5D);
		   }

	   protected SoundEvent getAmbientSound() {
		      return SoundEvents.PILLAGER_AMBIENT;
		   }

		   protected SoundEvent getDeathSound() {
		      return SoundEvents.PILLAGER_DEATH;
		   }

		   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		      return SoundEvents.PILLAGER_HURT;
		   }
	   
		@Override
		public void registerControllers(AnimationData data) {
			data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
		}
	   
		private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
				if (this.getAttackTicks() > 0) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("tribal_illager_chief_attack", true));
				} else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
						event.getController().setAnimation(new AnimationBuilder().addAnimation("tribal_illager_chief_walk", true));
				} else {
					    event.getController().setAnimation(new AnimationBuilder().addAnimation("tribal_illager_chief_idle", true));
				}
			return PlayState.CONTINUE;
		}
		
		@Override
		public AnimationFactory getFactory() {
			return factory;
		}
	   
	   class AttackGoal extends MeleeAttackGoal {
		      public AttackGoal(TribagerChiefEntity p_i50577_2_) {
		         super(p_i50577_2_, 1.0D, false);
		      }
		      
		    public boolean canUse() {
		    	return super.canUse();
		    }
		    
		    public boolean canContinueToUse() {
		    	return super.canContinueToUse();
		    }

		      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
		          return (double)(this.mob.getBbWidth() * 3.75F * this.mob.getBbWidth() * 3.75F + p_179512_1_.getBbWidth());
		       }
		      
		      protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
			         double d0 = this.getAttackReachSqr(p_190102_1_);
			         if (p_190102_2_ <= d0 && this.isTimeToAttack()) {
			            this.resetAttackCooldown();
			            this.mob.doHurtTarget(p_190102_1_);
			         } else if (p_190102_2_ <= d0 * 1.5D) {
			            if (this.isTimeToAttack()) {
			               this.resetAttackCooldown();
			            }

			            if (this.getTicksUntilNextAttack() <= 30) {
			            	if (TribagerChiefEntity.this.getAttackTicks() <= 0) {
			            		TribagerChiefEntity.this.setAttackTicks(40);
			            		//TribagerEntity.this.playSound(SoundEventInit.JAILOR_ATTACK.get(), 1.0F, TribagerEntity.this.getVoicePitch());
			            	}
			            }
			         } else {
			            this.resetAttackCooldown();
			         }
			      }
		   }
}
