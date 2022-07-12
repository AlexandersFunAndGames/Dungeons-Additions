package com.alexander.dungeonsadditions;

import com.alexander.dungeonsadditions.entities.IllagerWardenEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DungeonsAdditions.MOD_ID)
public class DungeonsAdditionsEvent {
    @SubscribeEvent
    public static void onDamageMob(LivingHurtEvent event)  {
        if (event.getEntityLiving() instanceof IllagerWardenEntity) {
            if (event.getAmount() > 7 + ((IllagerWardenEntity) event.getEntityLiving()).DamageReduceInt)
                ((IllagerWardenEntity)event.getEntityLiving()).canHurt = true;
            event.setAmount(Math.min(
                    IllagerWardenEntity.MAX_DAMAGE,
                    event.getAmount() * (IllagerWardenEntity.DAMAGE_RESISTANCE) / 100F)
            );
        }
    }

}
