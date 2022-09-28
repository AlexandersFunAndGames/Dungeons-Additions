package com.alexander.dungeonsadditions.entities;

import com.alexander.dungeonsadditions.CombatEvent;
import com.alexander.dungeonsadditions.entities.ai.AvoidAndApproachTargetGoal;
import com.alexander.dungeonsadditions.init.ItemInit;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

public class SamuraiEntity extends AbstractIllagerEntity implements IAnimatable {

    protected static final UUID SPEED_MODIFIER_RUNNING_UUID = UUID.fromString("05cd373b-0ff1-4ded-8630-b380232ed7b1");
    protected static final AttributeModifier SPEED_MODIFIER_RUN = new AttributeModifier(SPEED_MODIFIER_RUNNING_UUID,
            "Running speed increase", + 0.1D, AttributeModifier.Operation.ADDITION);

    protected static final DataParameter<Boolean> IS_PULLED_OUT_WEAPON = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_PULLING_OUT_WEAPON = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_WITHDRAW_WEAPON = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> CAN_MELEE = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_MELEE = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_MELEE_FAST = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_DESTROY_PROJECTILES = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_DODGE = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_PARRY = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_BLOCK = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_DASH = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_RUN = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.BOOLEAN);

    protected static final DataParameter<Integer> PARRY_TICK = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> DODGE_TICK = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> DASH_TICK = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> BLOCK_TICK = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);

    protected static final DataParameter<Integer> PARRY_COOLDOWN = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> DODGE_COOLDOWN = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> MELEE_COOLDOWN = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> DESTROY_PROJECTILES_COOLDOWN = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> DASH_COOLDOWN = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);
    protected static final DataParameter<Integer> BLOCK_COOLDOWN = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);

    protected static final DataParameter<Integer> MELEE_TYPE = EntityDataManager.defineId(SamuraiEntity.class, DataSerializers.INT);

    protected int timer;
    protected int MemoryTimeAfterTargetDisappears;

    AnimationFactory factory = new AnimationFactory(this);

    public SamuraiEntity(EntityType<? extends AbstractIllagerEntity> p_i48556_1_, World p_i48556_2_) {
        super(p_i48556_1_, p_i48556_2_);
        this.timer = 0;
        this.MemoryTimeAfterTargetDisappears = 0;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(@Nonnull IServerWorld p_213386_1_, @Nonnull DifficultyInstance p_213386_2_, @Nonnull SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {

        populateDefaultEquipmentSlots(p_213386_2_);
        populateDefaultEquipmentEnchantments(p_213386_2_);

        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    @Override
    protected void populateDefaultEquipmentSlots(@Nonnull DifficultyInstance p_180481_1_) {
        if (ModList.get().isLoaded("dungeons_gear")) {
            Item KATANA = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dungeons_gear", "katana"));
            ItemStack katana = new ItemStack(KATANA);

            if (this.getCurrentRaid() == null) this.setItemSlot(EquipmentSlotType.MAINHAND, katana);
        }else if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
        }
        if (this.getRandom().nextInt(88 - (28 * p_180481_1_.getDifficulty().getId())) == 0 && this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(ItemInit.SAMURAI_HELMET.get()));
            this.setItemSlot(EquipmentSlotType.CHEST, new ItemStack(ItemInit.SAMURAI_CHESTPLATE.get()));
            this.setItemSlot(EquipmentSlotType.LEGS, new ItemStack(ItemInit.SAMURAI_LEGGINGS.get()));
            this.setItemSlot(EquipmentSlotType.FEET, new ItemStack(ItemInit.SAMURAI_BOOTS.get()));

            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttributeBaseValue(Attributes.MAX_HEALTH) * 1.5);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) * 1.5);
            if (ModList.get().isLoaded("dungeons_gear")) {
                Item KATANA = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dungeons_gear", "masters_katana"));
                ItemStack katana = new ItemStack(KATANA);

                if (this.getCurrentRaid() == null) this.setItemSlot(EquipmentSlotType.MAINHAND, katana);
            }else if (this.getCurrentRaid() == null) this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));

        } else if (this.getCurrentRaid() == null) {
            this.setItemSlot(EquipmentSlotType.CHEST, new ItemStack(ItemInit.SAMURAI_ROBES.get()));
            this.setItemSlot(EquipmentSlotType.LEGS, new ItemStack(ItemInit.SAMURAI_PANTS.get()));
            this.setItemSlot(EquipmentSlotType.FEET, new ItemStack(ItemInit.SAMURAI_SHOES.get()));
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));

        this.registerAttackGoal();
    }

    protected void registerAttackGoal() {
        this.goalSelector.addGoal(0, new DoHurtTargetGoal(this));
        this.goalSelector.addGoal(0, new DestroyProjectileGoal(this));
        this.goalSelector.addGoal(0, new PullOutWeaponGoal(this));
        this.goalSelector.addGoal(0, new BlockGoal(this));
        this.goalSelector.addGoal(1, new WithdrawWeaponGoal(this));
        this.goalSelector.addGoal(1, new DodgeGoal(this));
        this.goalSelector.addGoal(2, new DashGoal(this));
        this.goalSelector.addGoal(4, new SamuraiAvoidEntityGoal(this, 1.1f, 90, 45, 7.5));
        this.goalSelector.addGoal(5, new SamuraiAttackGoal(this, 1.2, true));
    }

    @Override
    public void aiStep() {

        ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (this.entityData.get(PARRY_TICK) > 0 ) this.getNavigation().stop();

        if (this.getTarget() != null) {
            this.MemoryTimeAfterTargetDisappears = this.tickCount + 60;
            if (this.distanceTo(this.getTarget()) >= 9) {
                if (!modifiableattributeinstance.hasModifier(SPEED_MODIFIER_RUN)) {
                    modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_RUN);
                }
                this.entityData.set(IS_RUN, true);
            } else {
                this.entityData.set(IS_RUN, false);
                modifiableattributeinstance.removeModifier(SPEED_MODIFIER_RUNNING_UUID);
            }
        } else if (this.getCurrentRaid() != null){
            this.entityData.set(IS_RUN, true);
            if (!modifiableattributeinstance.hasModifier(SPEED_MODIFIER_RUN)) {
                modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_RUN);
            }
        }else {
            this.entityData.set(IS_RUN, false);
            modifiableattributeinstance.removeModifier(SPEED_MODIFIER_RUNNING_UUID);
        }


        super.aiStep();

        this.CooldownReduction();
        this.TickReduction();
    }

    @Override
    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        if (!p_70097_1_.isProjectile() && !p_70097_1_.isBypassArmor()) {
            if (this.entityData.get(IS_DASH)) return false;
            if (this.entityData.get(IS_DODGE)) return false;

            boolean b = false;

            for (LivingEntity entityHit : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.5, 5.5, 5.5))) {
                float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
                float entityAttackingAngle = this.yBodyRot % 360;
                if (entityHitAngle < 0) {
                    entityHitAngle += 360;
                }
                if (entityAttackingAngle < 0) {
                    entityAttackingAngle += 360;
                }
                float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
                if (entityHitDistance <= 5.5f && (entityRelativeAngle <= 135f / 2 && entityRelativeAngle >= -135f / 2) || (entityRelativeAngle >= 360 - 135f / 2 || entityRelativeAngle <= -360 + 135f / 2)) {
                    if (entityHit == p_70097_1_.getEntity()) {
                        b = !this.entityData.get(IS_MELEE);
                        break;
                    }
                }
            }

            if (this.entityData.get(PARRY_COOLDOWN) <= 0 && b) {
                this.entityData.set(PARRY_TICK, 15);
                this.entityData.set(PARRY_COOLDOWN, 5);
                this.getNavigation().stop();
                this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1, 1.5f);
                return false;
            }

            if (this.entityData.get(IS_BLOCK) && b) {
                this.entityData.set(CAN_MELEE, true);
                this.entityData.set(PARRY_TICK, 15);
                this.playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1, 1.5f);
                return false;
            }
        }
        return super.hurt(p_70097_1_, p_70097_2_);
    }

    @Override
    public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
        Item KATANA = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dungeons_gear", "masters_katana"));
        ItemStack katana = new ItemStack(KATANA);
        if (!ModList.get().isLoaded("dungeons_gear")) {
            katana = new ItemStack(Items.IRON_SWORD);
        }
        ItemStack helmet = new ItemStack(ItemInit.SAMURAI_HELMET.get());
        ItemStack chestplate = new ItemStack(ItemInit.SAMURAI_CHESTPLATE.get());
        ItemStack leggings = new ItemStack(ItemInit.SAMURAI_LEGGINGS.get());
        ItemStack boots = new ItemStack(ItemInit.SAMURAI_BOOTS.get());

        Raid raid = this.getCurrentRaid();
        int enchantmentLevel = 2;
        if (raid != null && p_213660_1_ > raid.getNumGroups(Difficulty.NORMAL)) {
            enchantmentLevel = 3;
        }

        boolean applyEnchant;
        if (raid == null) {
            this.setItemSlot(EquipmentSlotType.MAINHAND, katana);

            this.setItemSlot(EquipmentSlotType.HEAD, helmet);
            this.setItemSlot(EquipmentSlotType.CHEST, chestplate);
            this.setItemSlot(EquipmentSlotType.LEGS, leggings);
            this.setItemSlot(EquipmentSlotType.FEET, boots);

            return;
        }

        applyEnchant = this.random.nextFloat() <= raid.getEnchantOdds();

        if (applyEnchant) {
            Map<Enchantment, Integer> enchantmentIntegerMap = Maps.newHashMap();
            enchantmentIntegerMap.put(Enchantments.SHARPNESS, enchantmentLevel);
            EnchantmentHelper.setEnchantments(enchantmentIntegerMap, katana);
        }


        applyEnchant = this.random.nextFloat() <= raid.getEnchantOdds();

        if (applyEnchant) {
            Map<Enchantment, Integer> enchantmentIntegerMap = Maps.newHashMap();
            enchantmentIntegerMap.put(Enchantments.ALL_DAMAGE_PROTECTION, enchantmentLevel);
            EnchantmentHelper.setEnchantments(enchantmentIntegerMap, helmet);
        }

        applyEnchant = this.random.nextFloat() <= raid.getEnchantOdds();

        if (applyEnchant) {
            Map<Enchantment, Integer> enchantmentIntegerMap = Maps.newHashMap();
            enchantmentIntegerMap.put(Enchantments.ALL_DAMAGE_PROTECTION, enchantmentLevel);
            EnchantmentHelper.setEnchantments(enchantmentIntegerMap, chestplate);
        }

        applyEnchant = this.random.nextFloat() <= raid.getEnchantOdds();

        if (applyEnchant) {
            Map<Enchantment, Integer> enchantmentIntegerMap = Maps.newHashMap();
            enchantmentIntegerMap.put(Enchantments.ALL_DAMAGE_PROTECTION, enchantmentLevel);
            EnchantmentHelper.setEnchantments(enchantmentIntegerMap, leggings);
        }

        applyEnchant = this.random.nextFloat() <= raid.getEnchantOdds();

        if (applyEnchant) {
            Map<Enchantment, Integer> enchantmentIntegerMap = Maps.newHashMap();
            enchantmentIntegerMap.put(Enchantments.ALL_DAMAGE_PROTECTION, enchantmentLevel);
            EnchantmentHelper.setEnchantments(enchantmentIntegerMap, boots);
        }

        this.setItemSlot(EquipmentSlotType.MAINHAND, katana);

        this.setItemSlot(EquipmentSlotType.HEAD, helmet);
        this.setItemSlot(EquipmentSlotType.CHEST, chestplate);
        this.setItemSlot(EquipmentSlotType.LEGS, leggings);
        this.setItemSlot(EquipmentSlotType.FEET, boots);
    }

    public static void DestroyProjectiles(@Nonnull LivingEntity v, float range, float X, float Y, float Z, float arc) {
        for (ProjectileEntity entityHit : v.level.getEntitiesOfClass(ProjectileEntity.class, v.getBoundingBox().inflate(X, Y, Z))) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - v.getZ(), entityHit.getX() - v.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = v.yBodyRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - v.getZ()) * (entityHit.getZ() - v.getZ()) + (entityHit.getX() - v.getX()) * (entityHit.getX() - v.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                entityHit.remove();
            }
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, 22.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Nonnull
    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    protected SoundEvent getHurtSound(@Nonnull DamageSource p_184601_1_) {
        return SoundEvents.VINDICATOR_HURT;
    }

    @Override
    protected void playStepSound(@Nonnull BlockPos p_180429_1_, @Nonnull BlockState p_180429_2_) {
        if (this.getItemBySlot(EquipmentSlotType.HEAD).getItem().equals(ItemInit.SAMURAI_HELMET.get()) &&
                this.getItemBySlot(EquipmentSlotType.CHEST).getItem().equals(ItemInit.SAMURAI_CHESTPLATE.get()) &&
                this.getItemBySlot(EquipmentSlotType.LEGS).getItem().equals(ItemInit.SAMURAI_LEGGINGS.get()) &&
                this.getItemBySlot(EquipmentSlotType.FEET).getItem().equals(ItemInit.SAMURAI_BOOTS.get())) {
            this.playSound(SoundEvents.ARMOR_EQUIP_GOLD, 0.75f, 1);
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
        data.addAnimationController(new AnimationController<>(this, "left_handed", 0, this::leftHanded));
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        event.getController().animationSpeed = 1;

        if (this.entityData.get(IS_PULLING_OUT_WEAPON)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("pull_out_weapon", true));
        } else
            if (this.entityData.get(IS_WITHDRAW_WEAPON)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("withdraw_weapon", true));
        } else
            if (this.entityData.get(IS_MELEE)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation(
                        this.entityData.get(MELEE_TYPE) == 1 || this.entityData.get(MELEE_TYPE) == 5 ?
                                "melee_attack_type_1" :
                                this.entityData.get(MELEE_TYPE) == 2 ?
                                        "melee_attack_type_2" :
                                        this.entityData.get(MELEE_TYPE) == 3 ?
                                                "melee_attack_type_3" :
                                                "melee_attack_type_4" ,
                        true)
                );
        } else
            if (this.entityData.get(IS_DODGE)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("dodge", true));
        } else
            if (this.entityData.get(PARRY_TICK) > 0) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("parry", true));
        } else
            if (this.entityData.get(IS_DASH)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("dash", true));
        } else
            if (this.entityData.get(IS_BLOCK)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("block", true));
        } else
            if (this.entityData.get(IS_DESTROY_PROJECTILES)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("destroy_projectile", true));
        } else
            if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation(
                        this.entityData.get(IS_RUN) ?
                                "run" :
                                (this.entityData.get(IS_PULLED_OUT_WEAPON)) ?
                                        "walk_pulled_out_weapon" :
                                        "walk",
                        true)
                );
        } else
            if (this.isCelebrating()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("celebrate", true));
        }else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(
                    (this.entityData.get(IS_PULLED_OUT_WEAPON)) ?
                            "idle_pulled_out_weapon" :
                            "idle",
                    true)
            );
        }

        return PlayState.CONTINUE;
    }

    private <P extends IAnimatable> PlayState leftHanded(AnimationEvent<P> event) {
        if (this.isLeftHanded()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("left_handed", true));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(CAN_MELEE, false);

        this.entityData.define(IS_MELEE, false);
        this.entityData.define(IS_MELEE_FAST, false);
        this.entityData.define(IS_DASH, false);
        this.entityData.define(IS_BLOCK, false);
        this.entityData.define(IS_DODGE, false);
        this.entityData.define(IS_PARRY, false);
        this.entityData.define(IS_RUN, false);
        this.entityData.define(IS_DESTROY_PROJECTILES, false);
        this.entityData.define(IS_PULLED_OUT_WEAPON, false);
        this.entityData.define(IS_PULLING_OUT_WEAPON, false);
        this.entityData.define(IS_WITHDRAW_WEAPON, false);

        this.entityData.define(DASH_TICK, 0);
        this.entityData.define(DODGE_TICK, 0);
        this.entityData.define(PARRY_TICK, 0);
        this.entityData.define(BLOCK_TICK, 0);

        this.entityData.define(DASH_COOLDOWN, 0);
        this.entityData.define(PARRY_COOLDOWN, 0);
        this.entityData.define(DODGE_COOLDOWN, 0);
        this.entityData.define(MELEE_COOLDOWN, 0);
        this.entityData.define(BLOCK_COOLDOWN, 0);
        this.entityData.define(DESTROY_PROJECTILES_COOLDOWN, 0);

        this.entityData.define(MELEE_TYPE, 0);
    }

    protected void CooldownReduction() {
        if (this.entityData.get(DASH_COOLDOWN) > 0) this.entityData.set(DASH_COOLDOWN, this.entityData.get(DASH_COOLDOWN) - 1);
        if (this.entityData.get(PARRY_COOLDOWN) > 0) this.entityData.set(PARRY_COOLDOWN, this.entityData.get(PARRY_COOLDOWN) - 1);
        if (this.entityData.get(DODGE_COOLDOWN) > 0) this.entityData.set(DODGE_COOLDOWN, this.entityData.get(DODGE_COOLDOWN) - 1);
        if (this.entityData.get(MELEE_COOLDOWN) > 0) this.entityData.set(MELEE_COOLDOWN, this.entityData.get(MELEE_COOLDOWN) - 1);
        if (this.entityData.get(BLOCK_COOLDOWN) > 0) this.entityData.set(BLOCK_COOLDOWN, this.entityData.get(BLOCK_COOLDOWN) - 1);
        if (this.entityData.get(DESTROY_PROJECTILES_COOLDOWN) > 0) this.entityData.set(DESTROY_PROJECTILES_COOLDOWN, this.entityData.get(DESTROY_PROJECTILES_COOLDOWN) - 1);
    }

    protected void TickReduction() {
        if (this.entityData.get(DASH_TICK) > 0) this.entityData.set(DASH_TICK, this.entityData.get(DASH_TICK) - 1);
        if (this.entityData.get(PARRY_TICK) > 0) this.entityData.set(PARRY_TICK, this.entityData.get(PARRY_TICK) - 1);
        if (this.entityData.get(DODGE_TICK) > 0) this.entityData.set(DODGE_TICK, this.entityData.get(DODGE_TICK) - 1);
        if (this.entityData.get(BLOCK_TICK) > 0) this.entityData.set(BLOCK_TICK, this.entityData.get(BLOCK_TICK) - 1);
    }

    class SamuraiAvoidEntityGoal extends AvoidAndApproachTargetGoal {

        public SamuraiAvoidEntityGoal(CreatureEntity creatureEntity, double moveSpeed, double maxDistanceToTarget, double minDistanceToTarget, double AvoidToTargetDist) {
            super(creatureEntity, moveSpeed, maxDistanceToTarget, minDistanceToTarget, AvoidToTargetDist);
        }

        @Override
        public boolean isInterruptable() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && SamuraiEntity.this.entityData.get(MELEE_COOLDOWN) > 0;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && SamuraiEntity.this.entityData.get(MELEE_COOLDOWN) > 0;
        }
    }
    static class SamuraiAttackGoal extends MeleeAttackGoal {

        protected final SamuraiEntity mobEntity;

        public SamuraiAttackGoal(SamuraiEntity p_i1636_1_, double p_i1636_2_, boolean p_i1636_4_) {
            super(p_i1636_1_, p_i1636_2_, p_i1636_4_);
            this.mobEntity = p_i1636_1_;
        }

        @Override
        public void tick() {
            if (mobEntity.getTarget() == null) return;

            super.tick();

            if (mobEntity.entityData.get(MELEE_COOLDOWN) <= 0) {
                if (mobEntity.distanceTo(mobEntity.getTarget()) <= 12 && mobEntity.entityData.get(DASH_COOLDOWN) <= 0) {
                    mobEntity.entityData.set(IS_DASH, true);
                }

                if (mobEntity.distanceTo(mobEntity.getTarget()) <= 20 && mobEntity.entityData.get(BLOCK_COOLDOWN) <= 0 && mobEntity.getRandom().nextInt(50) == 0) {
                    mobEntity.entityData.set(IS_BLOCK, true);
                }
            }
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if ((distToEnemySqr + 18 + enemy.getBbWidth() <= this.getAttackReachSqr(enemy) || mobEntity.getBoundingBox().intersects(enemy.getBoundingBox().inflate(2)) || mobEntity.getBoundingBox().intersects(enemy.getBoundingBox().inflate(2,2,2)))) {
                mobEntity.entityData.set(CAN_MELEE, true);
            }
        }

    }

    static class DoHurtTargetGoal extends Goal {
        protected final SamuraiEntity mob;

        protected int type;
        protected static final int horizontalChop = 1;
        protected static final int chopFromBottomUp = 2;
        protected static final int obliqueChop = 3;
        protected static final int verticalChop = 4;
        protected static final int blockAndChop = 5;

        protected boolean canMoving;
        protected boolean hasNext;

        public DoHurtTargetGoal(SamuraiEntity entity) {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
            this.mob = entity;
        }

        @Override
        public boolean canUse() {
            return mob.entityData.get(CAN_MELEE) && mob.getTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (!this.hasNext) {
                return isAttacking();
            } else if (!isAttacking()) {
                mob.timer = 0;
                this.type = this.type + 1;

                return mob.getRandom().nextBoolean();
            }else {
                return true;
            }
        }

        private boolean isAttacking() {
            return mob.timer <
                    (this.type == horizontalChop ?
                            3 :
                            this.type == chopFromBottomUp ?
                                    8 :
                                    this.type == obliqueChop ?
                                            3 :
                                            this.type == verticalChop ?
                                                    4 :
                                                    this.type == blockAndChop ?
                                                            3 :
                                                            0) + 2;
        }

        @Override
        public boolean isInterruptable() {
            return mob.hurtTime > 0;
        }

        @Override
        public void start() {
            mob.timer = 0;
            this.type = horizontalChop;
            mob.entityData.set(IS_MELEE, true);
            mob.entityData.set(CAN_MELEE, false);
            mob.entityData.set(MELEE_TYPE, horizontalChop);
        }

        @Override
        public void tick() {
            mob.timer ++;

            if (this.type == horizontalChop) {
                attack(2, true);
                this.hasNext = true;
            }else if (this.type == chopFromBottomUp) {
                attack(3, true);
                this.hasNext = true;
            }else if (this.type == obliqueChop) {
                attack(2, true);
                this.hasNext = true;
            }else if (this.type == verticalChop) {
                attack(2, true);
                this.hasNext = false;
            }else if (this.type == blockAndChop) {
                attack(2, false);
                this.hasNext = false;
            }

            if (mob.getTarget() == null) this.hasNext = false;

            if (!this.canMoving) mob.getNavigation().stop();

            if (mob.getTarget() != null) {
                mob.getLookControl().setLookAt(mob.getTarget(), 30, 30);

                if (this.canMoving) mob.getNavigation().moveTo(mob.getTarget(), 1.0);
            }
            mob.entityData.set(MELEE_TYPE, this.type);
        }

        private void attack(int i, boolean CanMoving) {
            if (mob.timer == i) {
                mob.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1);
                CombatEvent.AreaAttack(mob, 5, 5, 5, 5, 170, 1, 0, 0.5);
                SamuraiEntity.DestroyProjectiles(mob, 5, 5, 5, 5, 170);
                this.canMoving = CanMoving;
            }else {
                this.canMoving = false;
            }
        }

        @Override
        public void stop() {
            mob.timer = 0;
            mob.entityData.set(IS_MELEE, false);
            mob.entityData.set(MELEE_COOLDOWN, 15);
        }
    }

    static class DestroyProjectileGoal extends Goal {

        private static final int attackTimerTakesEffect = 2;
        private static final int attackTimerEnd = 3;
        private final SamuraiEntity mob;

        public DestroyProjectileGoal(SamuraiEntity entity) {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
            this.mob = entity;
        }

        @Override
        public boolean canUse() {
            return mob.entityData.get(IS_DESTROY_PROJECTILES) && mob.getTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.timer < attackTimerEnd;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public void start() {
            mob.timer = 0;
            mob.entityData.set(IS_DESTROY_PROJECTILES, true);
        }

        @Override
        public void tick() {
            mob.timer ++;
            mob.getNavigation().stop();
            if (mob.getTarget() != null) mob.getLookControl().setLookAt(mob.getTarget(), 30, 30);

            if (mob.timer == attackTimerTakesEffect) {
                CombatEvent.AreaAttack(mob, 6, 6, 6, 6, 180, 0.8f, 0, 0.5);
                SamuraiEntity.DestroyProjectiles(mob, 6, 6, 6, 6, 180);
            }
        }

        @Override
        public void stop() {
            mob.timer = 0;
            mob.entityData.set(IS_DESTROY_PROJECTILES, false);
            mob.entityData.set(DESTROY_PROJECTILES_COOLDOWN, 12);
        }
    }

    static class DodgeGoal extends Goal {

        private final SamuraiEntity mob;
        private static final int dodgeTimerTakesEffect = 6;

        private float dodgeYaw;

        public DodgeGoal(SamuraiEntity entity) {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
            this.mob = entity;
        }

        @Override
        public boolean canUse() {
            return (mob.isInWater() || mob.isInLava() || (mob.entityData.get(MELEE_COOLDOWN) > 0 && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= 7) || (mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= 13 && mob.hurtTime > 0)) &&
                    mob.entityData.get(DODGE_COOLDOWN) <= 0;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.timer < dodgeTimerTakesEffect + 2 || !mob.isOnGround();
        }

        @Override
        public void start() {
            mob.timer = 0;
            mob.entityData.set(IS_DODGE, true);
        }

        @Override
        public void stop() {
            mob.timer = 0;
            mob.entityData.set(IS_DODGE, false);
            mob.entityData.set(DODGE_COOLDOWN, 30);
            mob.entityData.set(DASH_COOLDOWN, 35);
            mob.entityData.set(DODGE_TICK, 8);
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.timer ++;

            if (mob.getTarget() != null) mob.getLookControl().setLookAt(mob.getTarget(), 30, 30);

            if (mob.timer == 1) {
                dodgeYaw = (float) Math.toRadians((mob.yBodyRot % 360) + 90 + mob.getRandom().nextFloat() * 150 - 75);
            }

            if (mob.timer == 6 && (mob.isOnGround() || mob.isInLava() || mob.isInWater())) {
                float speed = -1.5f;
                Vector3d m = mob.getDeltaMovement().add(speed * Math.cos(dodgeYaw), 0, speed * Math.sin(dodgeYaw));
                mob.setDeltaMovement(m.x, 0.6, m.z);
            }
        }
    }

    static class DashGoal extends Goal {

        private final SamuraiEntity mob;
        private static final int dodgeTimerTakesEffect = 6;

        public DashGoal(SamuraiEntity entity) {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
            this.mob = entity;
        }

        @Override
        public boolean canUse() {
            return mob.entityData.get(IS_DASH);
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.timer < dodgeTimerTakesEffect + 2 || !mob.isOnGround();
        }

        @Override
        public void start() {
            mob.timer = 0;
        }

        @Override
        public void stop() {
            mob.timer = 0;
            mob.entityData.set(IS_DASH, false);
            mob.entityData.set(DASH_COOLDOWN, 30);
            mob.entityData.set(DASH_TICK, 8);

            CombatEvent.AreaAttack(mob, 6, 6, 6, 6, 120, 1.1f, 0, 0.5);
            SamuraiEntity.DestroyProjectiles(mob, 6, 6, 6, 6, 120);
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.timer ++;

            if (mob.getTarget() != null) mob.getLookControl().setLookAt(mob.getTarget(), 30, 30);

            if (mob.timer == 4 && (mob.isOnGround() || mob.isInLava() || mob.isInWater())) {
                if (mob.getTarget() != null) {
                    Vector3d m = mob.getDeltaMovement().add(
                                    Math.max(Math.min(mob.getTarget().getX() - mob.getX(), 1.7), -1.7),
                                    (0),
                                    Math.max(Math.min(mob.getTarget().getZ() - mob.getZ(), 1.7), -1.7))
                            .scale(0.8);
                    mob.setDeltaMovement(m.x, 0.6, m.z);
                }
            }
        }
    }

    static class PullOutWeaponGoal extends Goal {

        private final SamuraiEntity mob;

        public PullOutWeaponGoal(SamuraiEntity entity) {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
            this.mob = entity;
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && !mob.entityData.get(IS_PULLED_OUT_WEAPON) && !mob.entityData.get(IS_RUN);
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.timer < 20;
        }

        @Override
        public void start() {
            mob.timer = 0;
            mob.entityData.set(IS_PULLING_OUT_WEAPON, true);
        }

        @Override
        public void stop() {
            mob.timer = 0;
            mob.entityData.set(IS_PULLED_OUT_WEAPON, true);
            mob.entityData.set(IS_PULLING_OUT_WEAPON, false);
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.timer ++;
            if (mob.getTarget() != null) mob.getLookControl().setLookAt(mob.getTarget(), 30, 30);
        }
    }

    static class WithdrawWeaponGoal extends Goal {

        private final SamuraiEntity mob;

        public WithdrawWeaponGoal(SamuraiEntity entity) {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
            this.mob = entity;
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() == null && mob.entityData.get(IS_PULLED_OUT_WEAPON) && mob.MemoryTimeAfterTargetDisappears <= mob.tickCount;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.timer < 20;
        }

        @Override
        public void start() {
            mob.timer = 0;
            mob.entityData.set(IS_WITHDRAW_WEAPON, true);
        }

        @Override
        public void stop() {
            mob.timer = 0;
            mob.entityData.set(IS_PULLED_OUT_WEAPON, false);
            mob.entityData.set(IS_WITHDRAW_WEAPON, false);
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.timer ++;
            if (mob.getTarget() != null) mob.getLookControl().setLookAt(mob.getTarget(), 30, 30);
        }
    }

    static class BlockGoal extends Goal {

        private final SamuraiEntity mob;

        public BlockGoal(SamuraiEntity entity) {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
            this.mob = entity;
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && mob.entityData.get(IS_BLOCK) && mob.entityData.get(BLOCK_COOLDOWN) <= 0;
        }

        @Override
        public boolean isInterruptable() {
            return mob.entityData.get(CAN_MELEE);
        }

        @Override
        public boolean canContinueToUse() {
            return mob.timer < 80 && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) < 12;
        }

        @Override
        public void start() {
            mob.timer = 0;
        }

        @Override
        public void stop() {
            mob.timer = 0;
            mob.entityData.set(IS_BLOCK, false);
            mob.entityData.set(BLOCK_COOLDOWN, 80);
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.timer ++;
            if (mob.getTarget() != null) mob.getLookControl().setLookAt(mob.getTarget(), 30, 30);
        }
    }

}