package com.alexander.dungeonsadditions.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EliteEvokerEntity extends SpellcastingIllagerEntity implements IAnimatable {

	   AnimationFactory factory = new AnimationFactory(this);
	   
	   public EliteEvokerEntity(EntityType<? extends EliteEvokerEntity> p_i50207_1_, World p_i50207_2_) {
	      super(p_i50207_1_, p_i50207_2_);
	      this.xpReward = 15;
	   }
	   
	   protected void registerGoals() {
	      super.registerGoals();
	      this.goalSelector.addGoal(0, new SwimGoal(this));
	      this.goalSelector.addGoal(1, new EliteEvokerEntity.CastingSpellGoal());
	      this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 1.0D, 1.25D));
	      this.goalSelector.addGoal(4, new EliteEvokerEntity.SummonSpellGoal());
	      this.goalSelector.addGoal(5, new EliteEvokerEntity.AttackSpellGoal());
	      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 1.0D));
	      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
	      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
	      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
	      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
	      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
	      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
	   }
	   
		@Override
		public void registerControllers(AnimationData data) {
			data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
		}
	   
		private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
				if (this.getArmPose() == ArmPose.SPELLCASTING && this.getCurrentSpell() == SpellType.SUMMON_VEX) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("elite_evoker_summon_vexes", true));
				} else if (this.getArmPose() == ArmPose.SPELLCASTING && this.getCurrentSpell() == SpellType.FANGS) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("elite_evoker_summon_fangs", true));
				} else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
						event.getController().setAnimation(new AnimationBuilder().addAnimation("elite_evoker_walk", true));
				} else {
					    event.getController().setAnimation(new AnimationBuilder().addAnimation("elite_evoker_idle", true));
				}
			return PlayState.CONTINUE;
		}
		
		@Override
		public AnimationFactory getFactory() {
			return factory;
		}

	   public static AttributeModifierMap.MutableAttribute createAttributes() {
	      return MonsterEntity.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 48.0D);
	   }

	   protected void defineSynchedData() {
	      super.defineSynchedData();
	   }

	   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
	      super.readAdditionalSaveData(p_70037_1_);
	   }

	   public SoundEvent getCelebrateSound() {
	      return SoundEvents.EVOKER_CELEBRATE;
	   }

	   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
	      super.addAdditionalSaveData(p_213281_1_);
	   }

	   protected void customServerAiStep() {
	      super.customServerAiStep();
	   }

	   public boolean isAlliedTo(Entity p_184191_1_) {
	      if (p_184191_1_ == null) {
	         return false;
	      } else if (p_184191_1_ == this) {
	         return true;
	      } else if (super.isAlliedTo(p_184191_1_)) {
	         return true;
	      } else if (p_184191_1_ instanceof VexEntity) {
	         return this.isAlliedTo(((VexEntity)p_184191_1_).getOwner());
	      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getMobType() == CreatureAttribute.ILLAGER) {
	         return this.getTeam() == null && p_184191_1_.getTeam() == null;
	      } else {
	         return false;
	      }
	   }

	   protected SoundEvent getAmbientSound() {
	      return SoundEvents.EVOKER_CELEBRATE;
	   }

	   protected SoundEvent getDeathSound() {
	      return SoundEvents.EVOKER_DEATH;
	   }

	   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
	      return SoundEvents.EVOKER_HURT;
	   }

	   protected SoundEvent getCastingSoundEvent() {
	      return SoundEvents.EVOKER_CAST_SPELL;
	   }

	   public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
	   }

	   class AttackSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
	      private AttackSpellGoal() {
	      }

	      protected int getCastingTime() {
	         return 25;
	      }

	      protected int getCastingInterval() {
	         return 100;
	      }

	      protected void performSpellCasting() {
	         LivingEntity livingentity = EliteEvokerEntity.this.getTarget();
	         double d0 = Math.min(livingentity.getY(), EliteEvokerEntity.this.getY());
	         double d1 = Math.max(livingentity.getY(), EliteEvokerEntity.this.getY()) + 1.0D;
	         float f = (float)MathHelper.atan2(livingentity.getZ() - EliteEvokerEntity.this.getZ(), livingentity.getX() - EliteEvokerEntity.this.getX());
	         if (EliteEvokerEntity.this.distanceToSqr(livingentity) < 9.0D) {
	            for(int i = 0; i < 10; ++i) {
	               float f1 = f + (float)i * (float)Math.PI * 0.4F;
	               this.createSpellEntity(EliteEvokerEntity.this.getX() + (double)MathHelper.cos(f1) * 1.5D, EliteEvokerEntity.this.getZ() + (double)MathHelper.sin(f1) * 1.5D, d0, d1, f1, 0);
	            }

	            for(int k = 0; k < 16; ++k) {
	               float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
	               this.createSpellEntity(EliteEvokerEntity.this.getX() + (double)MathHelper.cos(f2) * 2.5D, EliteEvokerEntity.this.getZ() + (double)MathHelper.sin(f2) * 2.5D, d0, d1, f2, 3);
	            }
	         } else {
	            for(int l = 0; l < 25; ++l) {
	               double d2 = 1.25D * (double)(l + 1);
	               int j = 1 * l;
	               this.createSpellEntity(EliteEvokerEntity.this.getX() + (double)MathHelper.cos(f) * d2, EliteEvokerEntity.this.getZ() + (double)MathHelper.sin(f) * d2, d0, d1, f, j);
	               this.createSpellEntity(EliteEvokerEntity.this.getX() +  3 + (double)MathHelper.cos(f) * d2, EliteEvokerEntity.this.getZ() + 3 + (double)MathHelper.sin(f) * d2, d0, d1, f, j);
	               this.createSpellEntity(EliteEvokerEntity.this.getX() -  3 + (double)MathHelper.cos(f) * d2, EliteEvokerEntity.this.getZ() - 3 +  (double)MathHelper.sin(f) * d2, d0, d1, f, j);
	            }
	         }

	      }

	      private void createSpellEntity(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
	         BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
	         boolean flag = false;
	         double d0 = 0.0D;

	         do {
	            BlockPos blockpos1 = blockpos.below();
	            BlockState blockstate = EliteEvokerEntity.this.level.getBlockState(blockpos1);
	            if (blockstate.isFaceSturdy(EliteEvokerEntity.this.level, blockpos1, Direction.UP)) {
	               if (!EliteEvokerEntity.this.level.isEmptyBlock(blockpos)) {
	                  BlockState blockstate1 = EliteEvokerEntity.this.level.getBlockState(blockpos);
	                  VoxelShape voxelshape = blockstate1.getCollisionShape(EliteEvokerEntity.this.level, blockpos);
	                  if (!voxelshape.isEmpty()) {
	                     d0 = voxelshape.max(Direction.Axis.Y);
	                  }
	               }

	               flag = true;
	               break;
	            }

	            blockpos = blockpos.below();
	         } while(blockpos.getY() >= MathHelper.floor(p_190876_5_) - 1);

	         if (flag) {
	        	 EliteEvokerEntity.this.level.addFreshEntity(new EvokerFangsEntity(EliteEvokerEntity.this.level, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, EliteEvokerEntity.this));
	         }

	      }

	      protected SoundEvent getSpellPrepareSound() {
	         return SoundEvents.EVOKER_PREPARE_ATTACK;
	      }

	      protected SpellcastingIllagerEntity.SpellType getSpell() {
	         return SpellcastingIllagerEntity.SpellType.FANGS;
	      }
	   }

	   class CastingSpellGoal extends SpellcastingIllagerEntity.CastingASpellGoal {
	      private CastingSpellGoal() {
	      }

	      public void tick() {
	         if (EliteEvokerEntity.this.getTarget() != null) {
	        	 EliteEvokerEntity.this.getLookControl().setLookAt(EliteEvokerEntity.this.getTarget(), (float)EliteEvokerEntity.this.getMaxHeadYRot(), (float)EliteEvokerEntity.this.getMaxHeadXRot());
	         }
	      }
	   }

	   class SummonSpellGoal extends SpellcastingIllagerEntity.UseSpellGoal {
	      private final EntityPredicate vexCountTargeting = (new EntityPredicate()).range(16.0D).allowUnseeable().ignoreInvisibilityTesting().allowInvulnerable().allowSameTeam();

	      private SummonSpellGoal() {
	      }

	      public boolean canUse() {
	         if (!super.canUse()) {
	            return false;
	         } else {
	            int i = EliteEvokerEntity.this.level.getNearbyEntities(VexEntity.class, this.vexCountTargeting, EliteEvokerEntity.this, EliteEvokerEntity.this.getBoundingBox().inflate(16.0D)).size();
	            return i < 12;
	         }
	      }

	      protected int getCastingTime() {
	         return 35;
	      }

	      protected int getCastingInterval() {
	         return 340;
	      }

	      protected void performSpellCasting() {
	         ServerWorld serverworld = (ServerWorld)EliteEvokerEntity.this.level;

	         for(int i = 0; i < 6; ++i) {
	            BlockPos blockpos = EliteEvokerEntity.this.blockPosition().offset(-2 + EliteEvokerEntity.this.random.nextInt(5), 1, -2 + EliteEvokerEntity.this.random.nextInt(5));
	            VexEntity vexentity = EntityType.VEX.create(EliteEvokerEntity.this.level);
	            vexentity.moveTo(blockpos, 0.0F, 0.0F);
	            vexentity.finalizeSpawn(serverworld, EliteEvokerEntity.this.level.getCurrentDifficultyAt(blockpos), SpawnReason.MOB_SUMMONED, (ILivingEntityData)null, (CompoundNBT)null);
	            vexentity.setOwner(EliteEvokerEntity.this);
	            vexentity.setBoundOrigin(blockpos);
	            vexentity.setLimitedLife(20 * (30 + EliteEvokerEntity.this.random.nextInt(90)));
	            serverworld.addFreshEntityWithPassengers(vexentity);
	         }

	      }

	      protected SoundEvent getSpellPrepareSound() {
	         return SoundEvents.EVOKER_PREPARE_SUMMON;
	      }

	      protected SpellcastingIllagerEntity.SpellType getSpell() {
	         return SpellcastingIllagerEntity.SpellType.SUMMON_VEX;
	      }
	   }
	
	}