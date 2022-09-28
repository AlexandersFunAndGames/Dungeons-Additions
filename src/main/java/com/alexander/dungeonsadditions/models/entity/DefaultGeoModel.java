package com.alexander.dungeonsadditions.client.models;

import com.alexander.dungeonsadditions.DungeonsAdditions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

import javax.annotation.Nullable;

public class DefaultGeoModel extends AnimatedGeoModel {

    protected final String geoName;
    protected final String animationName;
    protected final String texturesName;

    public DefaultGeoModel(String geoNames, String animationNames, String texturesNames) {
        this.animationName = animationNames;
        this.geoName = geoNames;
        this.texturesName = texturesNames;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Object entity) {
        return new ResourceLocation(DungeonsAdditions.MOD_ID, this.animationName);
    }

    @Override
    public ResourceLocation getModelLocation(Object entity) {
        return new ResourceLocation(DungeonsAdditions.MOD_ID, this.geoName);
    }

    @Override
    public ResourceLocation getTextureLocation(Object entity) {
        return new ResourceLocation(DungeonsAdditions.MOD_ID, this.texturesName);
    }

    @Override
    public void setLivingAnimations(IAnimatable entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        IBone head = this.getAnimationProcessor().getBone("bipedHead");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null)
            if (extraData.headPitch != 0 || extraData.netHeadYaw != 0) {
                head.setRotationX(head.getRotationX() + (extraData.headPitch * ((float) Math.PI / 180F)));
                head.setRotationY(head.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 180F)));
            }
    }

    @Override
    public void setMolangQueries(IAnimatable animatable, double currentTick) {
        super.setMolangQueries(animatable, currentTick);

        MolangParser parser = GeckoLibCache.getInstance().parser;
        LivingEntity livingEntity = (LivingEntity) animatable;
        Vector3d velocity = livingEntity.getDeltaMovement();
        float groundSpeed = MathHelper.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
        parser.setValue("query.ground_speed", groundSpeed * 20 - 1);
    }
}