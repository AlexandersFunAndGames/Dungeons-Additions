package com.alexander.dungeonsadditions.entities;

import java.util.EnumSet;

import com.alexander.dungeonsadditions.init.EntityTypeInit;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class RovingMarketerEntity extends CreatureEntity implements IAnimatable {
	
	   public static final DataParameter<Integer> TRADE_TICKS = EntityDataManager.defineId(RovingMarketerEntity.class, DataSerializers.INT);
	   public static final DataParameter<Integer> RUN_TICKS = EntityDataManager.defineId(RovingMarketerEntity.class, DataSerializers.INT);
	   public static final DataParameter<Integer> ATTACK_TICKS = EntityDataManager.defineId(RovingMarketerEntity.class, DataSerializers.INT);
	   
	   AnimationFactory factory = new AnimationFactory(this);
	   
	   public RovingMarketerEntity(EntityType<? extends RovingMarketerEntity> p_i50189_1_, World p_i50189_2_) {
		      super(p_i50189_1_, p_i50189_2_);
		      this.maxUpStep = 1.0F;
		   }

	protected void registerGoals() {
	      this.goalSelector.addGoal(0, new SwimGoal(this));
	      this.goalSelector.addGoal(1, new RovingMarketerEntity.TradeGoal());
	      this.goalSelector.addGoal(2, new RovingMarketerEntity.AttackGoal(1.5D));
	      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
	      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
	      this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	   }
	
    protected void defineSynchedData() {
        super.defineSynchedData();
	    this.entityData.define(TRADE_TICKS, 0);
	    this.entityData.define(RUN_TICKS, 0);
	    this.entityData.define(ATTACK_TICKS, 0);
    }
	
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
	}
   
	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
			if (this.getTradeTicks() < 0) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_traded", true));
			} else if (this.getTradeTicks() > 1135) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_ready_trade", true));
			} else if (this.getTradeTicks() > 0 && this.getTradeTicks() <= 40) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_timed_out", true));
			} else if (this.getTradeTicks() > 40) {
				if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_trading_walk", true));
				} else {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_trading", true));					
				}
			} else if (this.getAttackTicks() > 0) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_attack", true));	
			} else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
				if (this.getRunTicks() > 0) {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_run", true));	
				} else {
					event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_walk", true));		
				}
			} else {
				    event.getController().setAnimation(new AnimationBuilder().addAnimation("roving_marketer_idle", true));
			}
		return PlayState.CONTINUE;
	}

	   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
		      super.addAdditionalSaveData(p_213281_1_);
		      p_213281_1_.putInt("TradeTicks", this.getTradeTicks());
		      p_213281_1_.putInt("AngerTicks", this.getRunTicks());
		   }

		   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
		      super.readAdditionalSaveData(p_70037_1_);
		      this.setTradeTicks(p_70037_1_.getInt("TradeTicks"));
		      this.setRunTicks(p_70037_1_.getInt("AngerTicks"));
		   }
		   
	   public static enum RovingMarketerTrades {
		      ENDER_PEARL(0, Items.ENDER_PEARL, 6, Items.LAPIS_LAZULI, 12),
		      EMERALD(1, Items.EMERALD, 8, Items.GOLD_INGOT, 6),
		      GOLD(2, Items.GOLD_INGOT, 8, Items.ENDER_EYE, 1),
		      PURPUR_BLOCK(3, Items.PURPUR_BLOCK, 46, Items.ENDER_EYE, 16),
		      PURPUR_PILLAR(4, Items.PURPUR_PILLAR, 42, Items.ENDER_EYE, 18),
		      END_ROD(5, Items.END_ROD, 12, Items.ENDER_EYE, 6),
		      CHORUS_FLOWER(6, Items.CHORUS_FLOWER, 8, Items.ENDER_EYE, 7),
		      NETHER_BRICKS(7, Items.NETHER_BRICKS, 16, Items.GOLD_INGOT, 5),
		      DIAMOND_AXE(8, Items.DIAMOND_AXE, 1, Items.ENDER_EYE, 3),
		      NAUTILUS_SHELL(9, Items.NAUTILUS_SHELL, 4, Items.EMERALD, 64),
		      LAVA_BUCKET(10, Items.LAVA_BUCKET, 1, Items.LAPIS_LAZULI, 6),
		      SHULKER_BOX(11, Items.SHULKER_BOX, 1, Items.ENDER_EYE, 23),
		      NETHERITE_HELMET(12, Items.NETHERITE_HELMET, 1, Items.GOLD_INGOT, 63),
		      LAPIS_LAZULI(13, Items.LAPIS_LAZULI, 28, Items.EMERALD, 32),
		      DIAMOND(14, Items.DIAMOND, 12, Items.ENDER_EYE, 9);

		      private final int tradeId;
		      private final Item soldItem;
		      private final int soldItemNumber;
		      private final Item boughtItem;
		      private final int boughtItemNumber;

		      private RovingMarketerTrades(int tradeId, Item soldItem, int soldItemNumber, Item boughtItem, int boughtItemNumber) {
		    	 this.tradeId = tradeId;
		         this.soldItem = soldItem;
		         this.soldItemNumber = soldItemNumber;
		         this.boughtItem = boughtItem;
		         this.boughtItemNumber = boughtItemNumber;
		      }

		      public static RovingMarketerEntity.RovingMarketerTrades byId(int p_193337_0_) {
		         for(RovingMarketerEntity.RovingMarketerTrades trade : values()) {
		            if (p_193337_0_ == trade.tradeId) {
		               return trade;
		            }
		         }

		         return ENDER_PEARL;
		      }
		   }
	
	protected ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
			if (this.getTradeTicks() > 0) {
				return ActionResultType.FAIL;		
			} else {
				this.setTradeTicks(1200);
				return ActionResultType.SUCCESS;
			}
	}
	
	public void remove() {
		for (Entity rider : this.getPassengers()) {
			rider.remove();
		}
		super.remove();
	}
			
			public void baseTick() {
				super.baseTick();
				
				if (this.getPassengers().size() > 0 && this.getPassengers().size() < 4) {
					this.setTradeTicks(-40);
					for (Entity rider : this.getPassengers()) {
						rider.remove();
					}
				}
				
				if (this.getTradeTicks() == 1) {
			    	this.setRunTicks(1200);
				}
				
				if (this.getRunTicks() > 0 && this.level.getNearestPlayer(this, 100) != null) {
			    	this.setTarget(this.level.getNearestPlayer(this, 100));
				}
				
				if (this.getTradeTicks() == 1140 && !this.level.isClientSide) {
					for (int i = 0; i < 4; i++) {
			        ServerWorld serverworld = (ServerWorld)this.level;
		            RovingMarketerItemEntity item = EntityTypeInit.ROVING_MARKETER_ITEM.get().create(this.level);
		            if (this.canAddPassenger(item)) {
		            item.moveTo(this.blockPosition(), 0.0F, 0.0F);
		            int randomTrade = this.random.nextInt(RovingMarketerTrades.values().length);
		            item.setItem(new ItemStack(RovingMarketerTrades.byId(randomTrade).soldItem, RovingMarketerTrades.byId(randomTrade).soldItemNumber));
		            item.setBuyItem(new ItemStack(RovingMarketerTrades.byId(randomTrade).boughtItem, RovingMarketerTrades.byId(randomTrade).boughtItemNumber));
		            item.startRiding(this);
		            serverworld.addFreshEntityWithPassengers(item);
					}
					}
				}
				
				if (!(this.getTradeTicks() > 40 && this.getTradeTicks() <= 1140) || !this.isAlive()) {
				for (Entity rider : this.getPassengers()) {
					rider.remove();
				}
				}
				
				if (this.getTradeTicks() > 0) {
					this.setTradeTicks(this.getTradeTicks() - 1);
				}
				
				if (this.getTradeTicks() < 0) {
					this.setTradeTicks(this.getTradeTicks() + 1);
				}
				
				if (this.getRunTicks() > 0) {
					this.setRunTicks(this.getRunTicks() - 1);
				}
				
				if (this.getAttackTicks() > 0) {
					this.setAttackTicks(this.getAttackTicks() - 1);
				}
			}
			
			   public int getTradeTicks() {
				      return this.entityData.get(TRADE_TICKS);
				   }

				   public void setTradeTicks(int p_189794_1_) {	   
				      this.entityData.set(TRADE_TICKS, p_189794_1_);
				   }
				   
				   public int getRunTicks() {
					      return this.entityData.get(RUN_TICKS);
					   }

					   public void setRunTicks(int p_189794_1_) {	   
					      this.entityData.set(RUN_TICKS, p_189794_1_);
					   }
					   
					   public int getAttackTicks() {
						      return this.entityData.get(ATTACK_TICKS);
						   }

						   public void setAttackTicks(int p_189794_1_) {	   
						      this.entityData.set(ATTACK_TICKS, p_189794_1_);
						   }
				   
				   public void positionRider(Entity p_184232_1_) {
					      if (this.hasPassenger(p_184232_1_)) {
					         float f = 0.0F;
					         float f2 = 0.0F;
					         float f3 = 0.0F;
					         float f1 = (float)((this.removed ? (double)0.01F : this.getPassengersRidingOffset()) + p_184232_1_.getMyRidingOffset());
					        // if (this.getPassengers().size() > 1) {
					            int i = this.getPassengers().indexOf(p_184232_1_);
					           // if (i == 0) {
					                f = 1.75F;
					                
					                if (i == 0) {
						                f2 = 0.75F;
						            //    f3 = 0.25F;
					                } else if (i == 1) {
						                f2 = 0.25F;
						            //    f3 = 0.1F;
					                } else if (i == 2) {
						                f2 = -0.25F;
						             //   f3 = 0.3F;
					                } else if (i == 3) {
						                f2 = -0.75F;
						              //  f3 = 0.4F;
					                }
					             //} else {
					            //    f = -1.6F;
					            // }

					           // if (p_184232_1_ instanceof AnimalEntity) {
					           //    f = (float)((double)f + 1.6D);
					        // //   }
					        // }

					         Vector3d vector3d = (new Vector3d((double)f, 0.0D, (double)f2)).yRot(-this.yRot * ((float)Math.PI / 180F) - ((float)Math.PI / 2F));
					         p_184232_1_.setPos(this.getX() + vector3d.x, this.getY() + (double)f1 + f3, this.getZ() + vector3d.z);
					         this.clampRotation(p_184232_1_);
					         //if (p_184232_1_ instanceof AnimalEntity && this.getPassengers().size() > 1) {
					         //   int j = p_184232_1_.getId() % 2 == 0 ? 90 : 270;
					         //   p_184232_1_.setYBodyRot(((AnimalEntity)p_184232_1_).yBodyRot + (float)j);
					         //   p_184232_1_.setYHeadRot(p_184232_1_.getYHeadRot() + (float)j);
					         //}

					      }
					   }
				   
				public double getPassengersRidingOffset() {
					return 1.85D;
				}
				   
				   protected void clampRotation(Entity p_184454_1_) {
					      p_184454_1_.setYBodyRot(this.yRot);
					      float f = MathHelper.wrapDegrees(p_184454_1_.yRot - this.yRot);
					      float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
					      p_184454_1_.yRotO += f1 - f;
					      p_184454_1_.yRot += f1 - f;
					      p_184454_1_.setYHeadRot(p_184454_1_.yRot);
					   }
				   
				   protected boolean canAddPassenger(Entity p_184219_1_) {
					      return this.getPassengers().size() < 4;
					   }
	
	@Override
	public AnimationFactory getFactory() {
		return factory;
	}

	   public static AttributeModifierMap.MutableAttribute createAttributes() {
	      return MobEntity.createMobAttributes().add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ATTACK_DAMAGE, 14.0D).add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, (double)0.25F).add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
	   }

	   protected SoundEvent getAmbientSound() {
	      return SoundEvents.ENDERMAN_AMBIENT;
	   }

	   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
	      return SoundEvents.ENDERMAN_HURT;
	   }

	   protected SoundEvent getDeathSound() {
	      return SoundEvents.ENDERMAN_DEATH;
	   }
	   
	   class TradeGoal extends Goal {
		      public TradeGoal() {
		         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
		      }

		    public void tick() {
		    	PlayerEntity player = RovingMarketerEntity.this.level.getNearestPlayer(RovingMarketerEntity.this, 100);
		    if (player != null && RovingMarketerEntity.this.canSee(player)) {
		    	RovingMarketerEntity.this.lookControl.setLookAt(player.getX(), player.getEyeY(), player.getZ());
		    }
		    
		    if (player != null && RovingMarketerEntity.this.distanceTo(player) > 10) {
		    	if (RovingMarketerEntity.this.distanceTo(player) < 7 && RovingMarketerEntity.this.canSee(player)) {
		    		RovingMarketerEntity.this.navigation.stop();
		    	} else {
		    		if (RovingMarketerEntity.this.getTradeTicks() > 40 && RovingMarketerEntity.this.getTradeTicks() <= 1140) {
		    		RovingMarketerEntity.this.navigation.moveTo(player, 1.0D);   		
		    		} else {
			    		RovingMarketerEntity.this.navigation.stop();
		    		}
		    	}
		    }
		    super.tick();
		    }
		      
		      public boolean canUse() {
		         return RovingMarketerEntity.this.getTradeTicks() != 0;
		      }
		   }
	   
	   class AttackGoal extends MeleeAttackGoal {
		   
		   public final EntityPredicate slimePredicate = (new EntityPredicate()).range(20.0D).allowUnseeable().ignoreInvisibilityTesting().allowInvulnerable().allowSameTeam();
		   
		      public AttackGoal(double speed) {
		         super(RovingMarketerEntity.this, speed, true);
		      }
		      
		      	public boolean canUse() {
		      		if (RovingMarketerEntity.this.getRunTicks() <= 0) {
		      			return false;
		      		} else {
		      			return super.canUse();
		      		}
		      	}
			      
			    public boolean canContinueToUse() {
		      		if (RovingMarketerEntity.this.getRunTicks() <= 0) {
		      			return false;
		      		} else {
					    return super.canContinueToUse();
		      		}
			    	}
		      
		      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
		          return (double)(this.mob.getBbWidth() * 3.0F * this.mob.getBbWidth() * 3.0F + p_179512_1_.getBbWidth());
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
		            	RovingMarketerEntity.this.setAttackTicks(40);
		            }
		         } else {
		            this.resetAttackCooldown();
		         }
		      }
	   }
	}
