package com.alexander.dungeonsadditions.entities;

import java.util.*;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class IllagerWardenEntity extends AbstractIllagerEntity implements IAnimatable {

	public int commandRoyalGuardCooldown;
	public int seeTimes;
	public int powerfulAttackCooldown;
	public int DamageReduceInt;
	public float DamageReduceMath;
	public int ReduceDamageReduceCooldown;
	public int injuryDamageCooldown;
	public int ArmorHitTime;
	public int sprintCooldown;
	public boolean canHurt;
	public static int MAX_DAMAGE = 15;
	public static int DAMAGE_RESISTANCE = 35;

	public static final DataParameter<Boolean> CAN_MELEE = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> IS_MELEE = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> IS_THUMP = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> IS_DEFENDING = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> IS_ATTACKING = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> C_DEFENDING = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> C_ATTACKING = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Integer> ATTACK_TICKS = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.INT);
	public static final DataParameter<Integer> TIMER = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.INT);
	public static final DataParameter<Integer> ATTACK_TYPE = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.INT);
	public static final DataParameter<Boolean> IS_COMMAND_GUARD = EntityDataManager.defineId(IllagerWardenEntity.class, DataSerializers.BOOLEAN);
	AnimationFactory factory = new AnimationFactory(this);

	public IllagerWardenEntity(EntityType<? extends IllagerWardenEntity> p_i50189_1_, World p_i50189_2_) {
		super(p_i50189_1_, p_i50189_2_);
	}

	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(0, new IllagerWardenEntity.MeleeGoal());
		this.goalSelector.addGoal(0, new IllagerWardenEntity.Melee2Goal());
		this.goalSelector.addGoal(0, new IllagerWardenEntity.Melee3Goal());
		this.goalSelector.addGoal(0, new IllagerWardenEntity.MeleeEndGoal());
		this.goalSelector.addGoal(1, new IllagerWardenEntity.ThumpGoal());
		this.goalSelector.addGoal(2, new AbstractIllagerEntity.RaidOpenDoorGoal(this));
		this.goalSelector.addGoal(3, new AbstractRaiderEntity.FindTargetGoal(this, 10.0F));
		this.goalSelector.addGoal(2, new IllagerWardenEntity.AttackGoal(this, 1.45));
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
		this.entityData.define(IS_COMMAND_GUARD, false);
		this.entityData.define(IS_THUMP, false);
		this.entityData.define(IS_MELEE, false);
		this.entityData.define(CAN_MELEE, false);
		this.entityData.define(IS_ATTACKING, false);
		this.entityData.define(IS_DEFENDING, false);
		this.entityData.define(C_ATTACKING, false);
		this.entityData.define(C_DEFENDING, false);
		this.entityData.define(ATTACK_TICKS, 0);
		this.entityData.define(TIMER, 0);
		this.entityData.define(ATTACK_TYPE, 0);
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController(this, "controller", 5, this::predicate));
	}
	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		//String assemble = "illager_warden_royal_guard_assemble";
		//String command_attack = "illager_warden_royal_guard_attack";
		//String command_defend = "illager_warden_royal_guard_defend";
		event.getController().animationSpeed = 1;
		if (this.entityData.get(IS_THUMP)) {
			event.getController().animationSpeed = 1.575;
			event.getController().setAnimation(new AnimationBuilder().addAnimation("illager_warden_thump", true));
		} else if (this.entityData.get(IS_MELEE)) {
			if (this.entityData.get(ATTACK_TYPE) == 0) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("illager_warden_attack", true));
			}else
			if (this.entityData.get(ATTACK_TYPE) == 1) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("illager_warden_attack_2", true));
			}else
			if (this.entityData.get(ATTACK_TYPE) == 2) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("illager_warden_attack_3", true));
			}else
			if (this.entityData.get(ATTACK_TYPE) == 3) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("illager_warden_attack_end", true));
			}
		} else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
			if (!this.isAggressive()) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("illager_warden_walk", true));
			}else {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("illager_warden_run", true));
			}
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("illager_warden_idle", true));
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

	@Override
	public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
		boolean e;
		if (
				p_70097_1_.isProjectile() ||
				p_70097_1_.isFire() ||
				p_70097_1_.isExplosion() ||
				(p_70097_2_ <= 2 + this.DamageReduceInt && !p_70097_1_.isBypassArmor() &&
						!this.canHurt || this.getRandom().nextFloat() < 0.35) ||
				this.injuryDamageCooldown <= 0 && !p_70097_1_.isBypassArmor()) {
			if (!(this.ArmorHitTime > 0)) {
				if (this.injuryDamageCooldown <= 0) {
					this.injuryDamageCooldown = 30;
				}
			}
			if (this.ReduceDamageReduceCooldown <= 0) {
				this.DamageReduceInt = (int) Math.max(DamageReduceInt - 3, -2);
				this.ReduceDamageReduceCooldown = 15;
			}

			Entity l = p_70097_1_.getDirectEntity();

			this.ArmorHitTime = 8;
			if (l != null) {
				l.setDeltaMovement(l.getDeltaMovement().add(
						(l.getX() - this.getX()) / 4.14,
						0.15,
						(l.getZ() - this.getZ()) / 4.14
				));
			}

			if (!p_70097_1_.isFire()) {
				this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.8f, 0.65F + this.getRandom().nextFloat() * 0.1F);
			}else {
				this.setSecondsOnFire(0);
			}
			e = false;
		}else {
			e = super.hurt(p_70097_1_, p_70097_2_);
		}
		this.canHurt = false;
		return e;
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

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.getTarget() != null && this.getTarget().isAlive()) {
			this.seeTimes ++;
			if (this.seeTimes > 50000) {
				this.seeTimes = 49999;
			}
		}else {
			this.seeTimes = 0;
		}
		if (this.hurtTime > 0 || this.ArmorHitTime > 0) {
			this.setDeltaMovement(this.getDeltaMovement().add(0,-3.5,0));
		}
		if (this.ArmorHitTime > 0) {
			this.ArmorHitTime --;
		}
		if (this.sprintCooldown > 0) {
			this.sprintCooldown --;
		}
		if (this.powerfulAttackCooldown > 0) {
			this.powerfulAttackCooldown --;
		}
		if (this.commandRoyalGuardCooldown > 0) {
			this.commandRoyalGuardCooldown --;
		}
		if (this.ReduceDamageReduceCooldown > 0){
			this.ReduceDamageReduceCooldown--;
		}else {
			this.DamageReduceMath += 0.333333F;
			if (this.DamageReduceMath > 10){
				this.DamageReduceMath = 10F;
			}
			this.DamageReduceInt = (int) Math.max(DamageReduceMath, 3);
		}
		if (this.seeTimes > 400 && this.entityData.get(IS_DEFENDING) || this.distanceToSqr(this.getTarget()) >= 800 ) {
			// royal guard defend
		}
		if (this.entityData.get(IS_DEFENDING)) {
			this.getNavigation().stop();
		}

	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MonsterEntity.createMonsterAttributes()
				.add(Attributes.ATTACK_KNOCKBACK, 3.25D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.85D)
				.add(Attributes.MOVEMENT_SPEED, (double)0.225F)
				.add(Attributes.FOLLOW_RANGE, 26.0D)
				.add(Attributes.MAX_HEALTH, 100.0D)
				.add(Attributes.ARMOR, 12.0D)
				.add(Attributes.ATTACK_DAMAGE, 10.0D);
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
		return SoundEvents.VILLAGER_CELEBRATE;
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
			Item MACE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dungeons_gear", "truthseeker"));

			ItemStack mace = new ItemStack(MACE);
			if (this.getCurrentRaid() == null) {
				this.setItemSlot(EquipmentSlotType.MAINHAND, mace);
			}
		}
		else{
			if (this.getCurrentRaid() == null) {
				this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
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
		return SoundEvents.VINDICATOR_AMBIENT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.VINDICATOR_DEATH;
	}

	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundEvents.VINDICATOR_HURT;
	}

	@Override
	protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
		this.playSound(SoundEvents.ARMOR_EQUIP_IRON, 0.75f, 1);
	}

	@Override
	public void applyRaidBuffs(int waveAmount, boolean b) {
		ItemStack mainhandWeapon = new ItemStack(Items.DIAMOND_SWORD);
		if(ModList.get().isLoaded("dungeons_gear")){
			Item MACE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dungeons_gear", "truthseeker"));

			mainhandWeapon = new ItemStack(MACE);
		}
		Raid raid = this.getCurrentRaid();
		int enchantmentLevel = 3;
		if (raid != null && waveAmount > raid.getNumGroups(Difficulty.NORMAL)) {
			enchantmentLevel = 5;
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
		private int maxAttackTimer = 15;
		private final double moveSpeed;
		private int delayCounter;
		private int sr;
		private int attackTimer;

		public IllagerWardenEntity v = IllagerWardenEntity.this;

		public AttackGoal(CreatureEntity creatureEntity, double moveSpeed) {
			super(creatureEntity, moveSpeed, true);
			this.moveSpeed = moveSpeed;
		}

		@Override
		public boolean canUse() {
			return v.getTarget() != null && v.getTarget().isAlive() && !v.entityData.get(IS_MELEE);
		}

		@Override
		public void start() {
			v.setAggressive(true);
			this.delayCounter = 0;
			//this.attackTimer = 0;
		}

		public void tick() {
			LivingEntity livingentity = v.getTarget();
			v.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
			double d0 = v.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());

			if (--this.delayCounter <= 0) {
				this.delayCounter = 4 + v.getRandom().nextInt(7);
				v.getNavigation().moveTo(livingentity, (double) this.moveSpeed);
			}

			this.attackTimer = Math.max(this.attackTimer - 1, 0);
			this.checkAndPerformAttack(livingentity, v.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ()));
		}

		@Override
		protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
			if ((distToEnemySqr + 21 + enemy.getBbWidth() <= this.getAttackReachSqr(enemy) || v.getBoundingBox().intersects(enemy.getBoundingBox()) || v.getBoundingBox().intersects(enemy.getBoundingBox().inflate(3,1.3,3))) && this.attackTimer <= 0) {
				this.attackTimer = maxAttackTimer;
				v.entityData.set(ATTACK_TYPE, 0);
				v.entityData.set(CAN_MELEE, true);
			}
		}

		@Override
		public void stop() {
			v.getNavigation().stop();
			if (v.getTarget() == null) {
				v.setAggressive(false);
			}
		}

		public IllagerWardenEntity.AttackGoal setMaxAttackTick(int max) {
			this.maxAttackTimer = max;
			return this;
		}
	}
	class MeleeGoal extends Goal {
		public IllagerWardenEntity v = IllagerWardenEntity.this;

		public MeleeGoal() {
			this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return v.getTarget() != null && v.entityData.get(CAN_MELEE) && v.entityData.get(ATTACK_TYPE) == 0;
		}

		@Override
		public boolean canContinueToUse() {
			//animation tick
			return v.entityData.get(TIMER) <= 30;
		}

		@Override
		public void start() {
			v.entityData.set(ATTACK_TYPE, 0);
			v.entityData.set(IS_MELEE, true);
			v.entityData.set(TIMER, 0);
		}

		@Override
		public void stop() {
			v.entityData.set(TIMER, 0);
			if (v.getTarget() != null && v.getTarget().isAlive()) {
				if (v.distanceToSqr(v.getTarget()) <= 30) {
					v.entityData.set(ATTACK_TYPE, 1);
				}else {
					v.entityData.set(ATTACK_TYPE, 0);
					v.entityData.set(IS_MELEE, false);
					v.entityData.set(CAN_MELEE, false);
				}
			}else {
				v.setAggressive(false);
				v.entityData.set(CAN_MELEE, false);
				v.entityData.set(IS_MELEE, false);
				v.entityData.set(ATTACK_TYPE, 0);
			}
		}

		@Override
		public void tick() {
			v.entityData.set(TIMER, v.entityData.get(TIMER) + 1);
			if (v.getTarget() != null && v.getTarget().isAlive()) {
				v.getLookControl().setLookAt(v.getTarget(), 30.0F, 30.0F);
				if (v.entityData.get(TIMER) == 22) {
					v.playSound(SoundEvents.PLAYER_ATTACK_CRIT,1,1);
					if (v.distanceToSqr(v.getTarget()) <= 12 + v.getTarget().getBbWidth()) {
						v.doHurtTarget(v.getTarget());
						if (v.getTarget() instanceof PlayerEntity) {
							if (v.getTarget().isBlocking()) {
								((PlayerEntity) v.getTarget()).disableShield(true);
							}
						}
					}
				}
				if (v.entityData.get(TIMER) == 17) {
					v.setDeltaMovement(v.getDeltaMovement().add(-Math.max(Math.min(v.getX() - v.getTarget().getX(), 3), -3)  ,0,-Math.max(Math.min(v.getZ() -v.getTarget().getZ(), 3), -3)));
				}
			}

		}

	}
	class Melee3Goal extends Goal {
		public IllagerWardenEntity v = IllagerWardenEntity.this;

		public Melee3Goal() {
			this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return v.entityData.get(ATTACK_TYPE) == 2;
		}

		@Override
		public boolean canContinueToUse() {
			//animation tick
			return v.entityData.get(TIMER) <= 30;
		}

		@Override
		public void start() {
			v.entityData.set(ATTACK_TYPE, 2);
			v.entityData.set(IS_MELEE, true);
			v.entityData.set(TIMER, 0);
		}

		@Override
		public void stop() {
			v.entityData.set(TIMER, 0);
			if (v.getTarget() != null && v.getTarget().isAlive()) {
				if (v.distanceToSqr(v.getTarget()) <= 30) {
					v.entityData.set(ATTACK_TYPE, 3);
				}else {
					v.entityData.set(ATTACK_TYPE, 0);
					v.entityData.set(IS_MELEE, false);
					v.entityData.set(CAN_MELEE, false);
				}
			}else {
				v.setAggressive(false);
				v.entityData.set(CAN_MELEE, false);
				v.entityData.set(IS_MELEE, false);
				v.entityData.set(ATTACK_TYPE, 0);
			}
		}

		@Override
		public void tick() {
			v.entityData.set(TIMER, v.entityData.get(TIMER) + 1);
			if (v.getTarget() != null && v.getTarget().isAlive()) {
				v.getLookControl().setLookAt(v.getTarget(), 30.0F, 30.0F);
				if (v.entityData.get(TIMER) == 8) {
					v.playSound(SoundEvents.PLAYER_ATTACK_SWEEP,1,1);
					if (v.distanceToSqr(v.getTarget()) <= 12 + v.getTarget().getBbWidth()) {
						v.doHurtTarget(v.getTarget());
					}
				}
				if (v.entityData.get(TIMER) == 2) {
					v.setDeltaMovement(v.getDeltaMovement().add(-Math.max(Math.min(v.getX() - v.getTarget().getX(), 3), -3)  ,0,-Math.max(Math.min(v.getZ() -v.getTarget().getZ(), 3), -3)));
				}

			}

		}

	}
	class MeleeEndGoal extends Goal {
		public IllagerWardenEntity v = IllagerWardenEntity.this;

		public MeleeEndGoal() {
			this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return v.entityData.get(ATTACK_TYPE) == 3;
		}

		@Override
		public boolean canContinueToUse() {
			//animation tick
			return v.entityData.get(TIMER) <= 30;
		}

		@Override
		public void start() {
			v.entityData.set(ATTACK_TYPE, 3);
			v.entityData.set(IS_MELEE, true);
			v.entityData.set(TIMER, 0);
		}

		@Override
		public void stop() {
			v.entityData.set(ATTACK_TYPE, 0);
			v.entityData.set(TIMER, 0);
			v.entityData.set(IS_MELEE, false);
			v.entityData.set(CAN_MELEE, false);
			if (v.getTarget() == null) {
				v.setAggressive(false);
			}
		}

		@Override
		public void tick() {
			v.entityData.set(TIMER, v.entityData.get(TIMER) + 1);
			if (v.getTarget() != null && v.getTarget().isAlive()) {
				v.getLookControl().setLookAt(v.getTarget(), 30.0F, 30.0F);
				if (v.entityData.get(TIMER) == 8) {
					v.playSound(SoundEvents.PLAYER_ATTACK_SWEEP,1,1);
					if (v.distanceToSqr(v.getTarget()) <= 12 + v.getTarget().getBbWidth()) {
						v.doHurtTarget(v.getTarget());
					}
				}
				if (v.entityData.get(TIMER) == 2) {
					v.setDeltaMovement(v.getDeltaMovement().add(-Math.max(Math.min(v.getX() - v.getTarget().getX(), 3), -3)  ,0,-Math.max(Math.min(v.getZ() -v.getTarget().getZ(), 3), -3)));
				}
			}

		}

	}
	class Melee2Goal extends Goal {
		public IllagerWardenEntity v = IllagerWardenEntity.this;

		public Melee2Goal() {
			this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return v.entityData.get(ATTACK_TYPE) == 1;
		}

		@Override
		public boolean canContinueToUse() {
			//animation tick
			return v.entityData.get(TIMER) <= 30;
		}

		@Override
		public void start() {
			v.entityData.set(ATTACK_TYPE, 1);
			v.entityData.set(IS_MELEE, true);
			v.entityData.set(TIMER, 0);
		}

		@Override
		public void stop() {
			v.entityData.set(TIMER, 0);
			if (v.getTarget() != null && v.getTarget().isAlive()) {
				if (v.distanceToSqr(v.getTarget()) <= 30) {
					v.entityData.set(ATTACK_TYPE, 2);
				}else {
					v.entityData.set(ATTACK_TYPE, 0);
					v.entityData.set(IS_MELEE, false);
					v.entityData.set(CAN_MELEE, false);
				}
			}else {
				v.setAggressive(false);
				v.entityData.set(ATTACK_TYPE, 0);
				v.entityData.set(CAN_MELEE, false);
				v.entityData.set(IS_MELEE, false);
			}
		}

		@Override
		public void tick() {
			v.entityData.set(TIMER, v.entityData.get(TIMER) + 1);
			if (v.getTarget() != null && v.getTarget().isAlive()) {
				v.getLookControl().setLookAt(v.getTarget(), 30.0F, 30.0F);
				if (v.entityData.get(TIMER) == 18) {
					v.playSound(SoundEvents.PLAYER_ATTACK_SWEEP,1,1);
					if (v.distanceToSqr(v.getTarget()) <= 12 + v.getTarget().getBbWidth()) {
						v.doHurtTarget(v.getTarget());
					}
				}
				if (v.entityData.get(TIMER) == 13) {
					v.setDeltaMovement(v.getDeltaMovement().add(-Math.max(Math.min(v.getX() - v.getTarget().getX(), 3), -3)  ,0,-Math.max(Math.min(v.getZ() -v.getTarget().getZ(), 3), -3)));
				}
			}else {
				v.entityData.set(IS_MELEE, false);
			}

		}

	}
	class ThumpGoal extends Goal {

		private int r;
		public IllagerWardenEntity v = IllagerWardenEntity.this;

		public ThumpGoal() {
			this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
		}

		@Override
		public boolean isInterruptable() {
			return false;
		}

		@Override
		public boolean canUse() {
			return v.getTarget() != null &&
					v.powerfulAttackCooldown <= 0 &&
					v.distanceToSqr(v.getTarget()) <= 60;
		}

		@Override
		public boolean canContinueToUse() {
			//animation tick
			return v.entityData.get(TIMER) <= (int) (140 / 1.5);
		}

		@Override
		public void start() {
			v.powerfulAttackCooldown = 280;
			v.entityData.set(IS_THUMP, true);
			v.entityData.set(TIMER, 0);
		}

		@Override
		public void stop() {
			v.entityData.set(IS_THUMP, false);
			v.entityData.set(TIMER, 0);
		}

		private void Attackparticle(int paticle,float circle, float vec, float math) {
			if (v.level.isClientSide) {
				for (int i1 = 0; i1 < paticle; i1++) {
					double DeltaMovementX = v.getRandom().nextGaussian() * 0.07D;
					double DeltaMovementY = v.getRandom().nextGaussian() * 0.07D;
					double DeltaMovementZ = v.getRandom().nextGaussian() * 0.07D;
					float angle = (0.01745329251F * v.yBodyRot) + i1;
					float f = MathHelper.cos(v.yRot * ((float)Math.PI / 180F)) ;
					float f1 = MathHelper.sin(v.yRot * ((float)Math.PI / 180F)) ;
					double extraX = circle * MathHelper.sin((float) (Math.PI + angle));
					double extraY = 0.3F;
					double extraZ = circle * MathHelper.cos(angle);
					double theta = (v.yBodyRot) * (Math.PI / 180);
					theta += Math.PI / 2;
					double vecX = Math.cos(theta);
					double vecZ = Math.sin(theta);
					int hitX = MathHelper.floor(v.getX() + vec * vecX+ extraX);
					int hitY = MathHelper.floor(v.getY());
					int hitZ = MathHelper.floor(v.getZ() + vec * vecZ + extraZ);
					BlockPos hit = new BlockPos(hitX, hitY, hitZ);
					BlockState block = v.level.getBlockState(hit.below());
					v.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, block), getX() + vec * vecX + extraX + f * math, v.getY() + extraY, getZ() + vec * vecZ + extraZ + f1 * math, DeltaMovementX, DeltaMovementY, DeltaMovementZ);

				}
			}
		}

		public void ExplosionCloud(){
			AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(v.level, v.getX(), v.getY(0.5), v.getZ());
			areaeffectcloudentity.setParticle(ParticleTypes.EXPLOSION);
			areaeffectcloudentity.setRadius(4.75F);
			areaeffectcloudentity.setDuration(0);
			v.level.addFreshEntity(areaeffectcloudentity);
			v.playSound(SoundEvents.GENERIC_EXPLODE, 1, 1);
		}

		@Override
		public void tick() {
			v.entityData.set(TIMER, v.entityData.get(TIMER) + 1);
			v.setDeltaMovement(Vector3d.ZERO);
			if (v.getTarget() != null) {
				float f = (float) MathHelper.atan2(v.getTarget().getZ() - v.getZ(), v.getTarget().getX() - v.getX());
				if (v.entityData.get(TIMER) == (int) (45 / 1.5)) {
					this.Attackparticle(30, 0.2F, 1.15F, 0);
					this.ExplosionCloud();

					List<Entity> list = Lists.newArrayList(v.level.getEntities(v, v.getBoundingBox().inflate(9, 9 + v.getTarget().getBbHeight(), 9)));
					for(Entity entity : list) {
						if(entity instanceof LivingEntity && (!v.isAlliedTo(entity) && entity != v.getTarget())){
							LivingEntity livingEntity = (LivingEntity)entity;
							livingEntity.setDeltaMovement(
									livingEntity.getDeltaMovement().add((v.getX() - livingEntity.getX()) / -3.14,0.6888,(v.getZ() - livingEntity.getZ()) / -3.14)
							);
							v.doHurtTarget(livingEntity);
						}
					}
					if(v.distanceToSqr(v.getTarget()) <= 60){
						LivingEntity livingEntity = (LivingEntity)v.getTarget();
						livingEntity.setDeltaMovement(
								livingEntity.getDeltaMovement().add((v.getX() - livingEntity.getX()) / -2.14,0.6888,(v.getZ() - livingEntity.getZ()) / -2.14)
						);
						v.doHurtTarget(livingEntity);
					}
				}
			}

		}

	}
}