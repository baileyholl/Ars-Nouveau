package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;

public class BeltOfLevitation extends ArsNouveauCurio {
    public BeltOfLevitation() {
        super();
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player && !player.abilities.flying) {
            Level world = player.getCommandSenderWorld();

            if (!player.onGround() && player.isShiftKeyDown() && !world.isClientSide()) {
                boolean isTooHigh = true;
                for (int i = 1; i < 6; i++) {
                    if (!world.getBlockState(player.blockPosition().below(i)).isAir()) {
                        isTooHigh = false;
                        break;
                    }
                }

                if (isTooHigh) {
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 2));
                } else {
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 5, 2));
                }
                player.fallDistance = 0.0f;
            }

            if (world.isClientSide()) {
                Vec3 oldMotion = player.getDeltaMovement();
                double y = oldMotion.y();
                Vec3 motion = player.getDeltaMovement().scale(1.1);
                if (Math.sqrt(motion.length()) > 0.6) {
                    return;
                }
                player.lerpMotion(motion.x, y, motion.z);
                player.hurtMarked = true;
            }
        }
    }
}
