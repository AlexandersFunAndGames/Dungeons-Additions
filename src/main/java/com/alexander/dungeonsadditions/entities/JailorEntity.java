package com.alexander.dungeonsadditions.entities;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.alexander.dungeonsadditions.init.SoundEventInit;
import com.google.common.collect.Maps;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class JailorEntity extends AbstractIllagerEntity implements IAnimatable {

	   public static final DataParameter<Integer> ATTACK_TICKS = EntityDataManager.defineId(JailorEntity.class, DataSerializers.INT);
	   AnimationFactory factory = new AnimationFactory(this);

	   public JailorEntity(EntityType<? extends JailorEntity> p_i50189_1_, World p_i50189_2_) {
	      super(p_i50189_1_, p_i50189_2_);
	   }

	   protected void registerGoals() {
	      super.registerGoals();
	      this.goalSelector.addGoal(0, new SwimGoal(this));
	      this.goalSelector.addGoal(1, new JailorEntity.LeashedTargetAvoidEntityGoal<>(this, IronGolemEntity.class, 10.0F, 1.0D, 1.0D));
	      this.goalSelector.addGoal(1, new JailorEntity.LeashedTargetAvoidEntityGoal<>(this, PlayerEntity.class, 7.5F, 1.0D, 1.0D));
	      this.goalSelector.addGoal(2, new JailorEntity.LeashedTargetAvoidEntityGoal<>(this, AbstractVillagerEntity.class, 5.0F, 1.0D, 1.0D));
	      this.goalSelector.addGoal(2, new AbstractIllagerEntity.RaidOpenDoorGoal(this));
	      this.goalSelector.addGoal(3, new AbstractRaiderEntity.FindTargetGoal(this, 10.0F));
	      this.goalSelector.addGoal(2, new JailorEntity.AttackGoal(this));
	      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
	      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true).setUnseenMemoryTicks(600));
	      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true).setUnseenMemoryTicks(600));
	      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true).setUnseenMemoryTicks(600));
	      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 1.0D));
	      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
	      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
	   }
	   
	    protected void defineSynchedData() {
	        super.defineSynchedData();
		    this.entityData.define(ATTACK_TICKS, 0);
	    }
	    
	    public boolean doHurtTarget(Entity p_70652_1_) {
	        if (p_70652_1_ instanceof MobEntity && this.getTarget() == p_70652_1_) {
	        	((MobEntity)p_70652_1_).setLeashedTo(this, true);
	        }
	    	return super.doHurtTarget(p_70652_1_);
	    }
	   
		@Override
		public void registerControllers(AnimationData data) {
			data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
		}
	   
		private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
				if (this.getAttackTicks() > 0) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("jailor_attack", true));
				} else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
						event.getController().setAnimation(new AnimationBuilder().addAnimation("jailor_walk", true));
				} else {
					    event.getController().setAnimation(new AnimationBuilder().addAnimation("jailor_idle", true));
				}
			return PlayState.CONTINUE;
		}
		
		@Override
		public AnimationFactory getFactory() {
			return factory;
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

	   protected void customServerAiStep() {
	      if (!this.isNoAi() && GroundPathHelper.hasGroundPathNavigation(this)) {
	         boolean flag = ((ServerWorld)this.level).isRaided(this.blockPosition());
	         ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(flag);
	      }

	      super.customServerAiStep();
	   }

	   public static AttributeModifierMap.MutableAttribute createAttributes() {
	      return MonsterEntity.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.275F).add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MAX_HEALTH, 24.0D).add(Attributes.ATTACK_DAMAGE, 8.0D);
	   }

	   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
	      super.addAdditionalSaveData(p_213281_1_);

	   }

	   @OnlyIn(Dist.CLIENT)
	   public AbstractIllagerEntity.ArmPose getArmPose() {
	      if (this.isAggressive()) {
	         return AbstractIllagerEntity.ArmPose.ATTACKING;
	      } else {
	         return this.isCelebrating() ? AbstractIllagerEntity.ArmPose.CELEBRATING : AbstractIllagerEntity.ArmPose.CROSSED;
	      }
	   }

	   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
	      super.readAdditionalSaveData(p_70037_1_);

	   }

	   public SoundEvent getCelebrateSound() {
	      return SoundEventInit.JAILOR_IDLE.get();
	   }

	   @Nullable
	   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
	      ILivingEntityData ilivingentitydata = super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
	      ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(true);
	      this.populateDefaultEquipmentSlots(p_213386_2_);
	      this.populateDefaultEquipmentEnchantments(p_213386_2_);
	      return ilivingentitydata;
	   }

	    @Override
	    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {

	        if(ModList.get().isLoaded("dungeons_gear")){
	            Item MACE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dungeons_gear", "jailors_scythe"));

	            ItemStack mace = new ItemStack(MACE);
	            if (this.getCurrentRaid() == null) {
	                this.setItemSlot(EquipmentSlotType.MAINHAND, mace);
	            }
	        }
	        else{
	            if (this.getCurrentRaid() == null) {
	                this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_AXE));
	            }
	        }
	    }

	   public boolean isAlliedTo(Entity p_184191_1_) {
	      if (super.isAlliedTo(p_184191_1_)) {
	         return true;
	      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getMobType() == CreatureAttribute.ILLAGER) {
	         return this.getTeam() == null && p_184191_1_.getTeam() == null;
	      } else {
	         return false;
	      }
	   }

	   protected SoundEvent getAmbientSound() {
		   return SoundEventInit.JAILOR_IDLE.get();
	   }

	   protected SoundEvent getDeathSound() {
		   return SoundEventInit.JAILOR_DEATH.get();
	   }

	   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		   return SoundEventInit.JAILOR_HURT.get();
	   }

	    @Override
	    public void applyRaidBuffs(int waveAmount, boolean b) {
	        ItemStack mainhandWeapon = new ItemStack(Items.IRON_AXE);
	        if(ModList.get().isLoaded("dungeons_gear")){
	            Item MACE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dungeons_gear", "jailors_scythe"));

	            mainhandWeapon = new ItemStack(MACE);
	        }
	        Raid raid = this.getCurrentRaid();
	        int enchantmentLevel = 1;
	        if (raid != null && waveAmount > raid.getNumGroups(Difficulty.NORMAL)) {
	            enchantmentLevel = 2;
	        }

	        boolean applyEnchant = false;
	        if (raid != null) {
	            applyEnchant = this.random.nextFloat() <= raid.getEnchantOdds();
	        }
	        if (applyEnchant) {
	            Map<Enchantment, Integer> enchantmentIntegerMap = Maps.newHashMap();
	            enchantmentIntegerMap.put(Enchantments.SHARPNESS, enchantmentLevel);
	            EnchantmentHelper.setEnchantments(enchantmentIntegerMap, mainhandWeapon);
	        }

	        this.setItemSlot(EquipmentSlotType.MAINHAND, mainhandWeapon);
	    }

	   class AttackGoal extends MeleeAttackGoal {
	      public AttackGoal(JailorEntity p_i50577_2_) {
	         super(p_i50577_2_, 1.0D, false);
	      }
	      
	    public boolean canUse() {
	    	if (JailorEntity.this.getTarget() != null && JailorEntity.this.getTarget() instanceof MobEntity && ((MobEntity)JailorEntity.this.getTarget()).isLeashed()) {
	    		return false;
	    	} else {
	    	return super.canUse();
	    	}
	    }
	    
	    public boolean canContinueToUse() {
	    	if (JailorEntity.this.getTarget() != null && JailorEntity.this.getTarget() instanceof MobEntity && ((MobEntity)JailorEntity.this.getTarget()).isLeashed()) {
	    		return false;
	    	} else {
	    	return super.canContinueToUse();
	    	}
	    }

	      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
	          return (double)(this.mob.getBbWidth() * 3.25F * this.mob.getBbWidth() * 3.25F + p_179512_1_.getBbWidth());
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
		            	if (JailorEntity.this.getAttackTicks() <= 0) {
		            	JailorEntity.this.setAttackTicks(30);
		            	JailorEntity.this.playSound(SoundEventInit.JAILOR_ATTACK.get(), 1.0F, JailorEntity.this.getVoicePitch());
		            	}
		            }
		         } else {
		            this.resetAttackCooldown();
		         }
		      }
	   }
	   
	   public class LeashedTargetAvoidEntityGoal<T extends LivingEntity> extends Goal {
		   protected final CreatureEntity mob;
		   private final double walkSpeedModifier;
		   private final double sprintSpeedModifier;
		   protected T toAvoid;
		   protected final float maxDist;
		   protected Path path;
		   protected final PathNavigator pathNav;
		   protected final Class<T> avoidClass;
		   protected final Predicate<LivingEntity> avoidPredicate;
		   protected final Predicate<LivingEntity> predicateOnAvoidEntity;
		   private final EntityPredicate avoidEntityTargeting;

		   public LeashedTargetAvoidEntityGoal(CreatureEntity p_i46404_1_, Class<T> p_i46404_2_, float p_i46404_3_, double p_i46404_4_, double p_i46404_6_) {
		      this(p_i46404_1_, p_i46404_2_, (p_200828_0_) -> {
		         return true;
		      }, p_i46404_3_, p_i46404_4_, p_i46404_6_, EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test);
		   }

		   public LeashedTargetAvoidEntityGoal(CreatureEntity p_i48859_1_, Class<T> p_i48859_2_, Predicate<LivingEntity> p_i48859_3_, float p_i48859_4_, double p_i48859_5_, double p_i48859_7_, Predicate<LivingEntity> p_i48859_9_) {
		      this.mob = p_i48859_1_;
		      this.avoidClass = p_i48859_2_;
		      this.avoidPredicate = p_i48859_3_;
		      this.maxDist = p_i48859_4_;
		      this.walkSpeedModifier = p_i48859_5_;
		      this.sprintSpeedModifier = p_i48859_7_;
		      this.predicateOnAvoidEntity = p_i48859_9_;
		      this.pathNav = p_i48859_1_.getNavigation();
		      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		      this.avoidEntityTargeting = (new EntityPredicate()).range((double)p_i48859_4_).selector(p_i48859_9_.and(p_i48859_3_));
		   }

		   public LeashedTargetAvoidEntityGoal(CreatureEntity p_i48860_1_, Class<T> p_i48860_2_, float p_i48860_3_, double p_i48860_4_, double p_i48860_6_, Predicate<LivingEntity> p_i48860_8_) {
		      this(p_i48860_1_, p_i48860_2_, (p_203782_0_) -> {
		         return true;
		      }, p_i48860_3_, p_i48860_4_, p_i48860_6_, p_i48860_8_);
		   }

		   public boolean canUse() {
		      this.toAvoid = this.mob.level.getNearestLoadedEntity(this.avoidClass, this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0D, (double)this.maxDist));
		      if (JailorEntity.this.getTarget() != null && JailorEntity.this.getTarget() instanceof MobEntity && ((MobEntity)JailorEntity.this.getTarget()).isLeashed()) {
		      if (this.toAvoid == null) {
		         return false;
		      } else {
		         Vector3d vector3d = RandomPositionGenerator.getPosAvoid(this.mob, 16, 7, this.toAvoid.position());
		         if (vector3d == null) {
		            return false;
		         } else if (this.toAvoid.distanceToSqr(vector3d.x, vector3d.y, vector3d.z) < this.toAvoid.distanceToSqr(this.mob)) {
		            return false;
		         } else {
		            this.path = this.pathNav.createPath(vector3d.x, vector3d.y, vector3d.z, 0);
		            return this.path != null;
		         }
		      }
		      } else {
		    	  return false;
		      }
		   }

		   public boolean canContinueToUse() {
			   if (JailorEntity.this.getTarget() != null && JailorEntity.this.getTarget() instanceof MobEntity && ((MobEntity)JailorEntity.this.getTarget()).isLeashed()) {
		      return !this.pathNav.isDone();
			   } else {
				   return false;
			   }
		   }

		   public void start() {
		      this.pathNav.moveTo(this.path, this.walkSpeedModifier);
		   }

		   public void stop() {
		      this.toAvoid = null;
		   }

		   public void tick() {
		      if (this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
		         this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
		      } else {
		         this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
		      }

		   }
		}
	}