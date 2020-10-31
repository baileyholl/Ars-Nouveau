package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectBlink extends AbstractEffect {

    public EffectBlink() {
        super(ModConfig.EffectBlinkID, "Blink");
    }
    
    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(shooter == null)
            return;

        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity().equals(shooter)) {
            double distance = 8.0f + 3.0f *getAmplificationBonus(augments);
            Vector3d lookVec = new Vector3d(shooter.getLookVec().getX(), 0, shooter.getLookVec().getZ());
            Vector3d vec = shooter.getPositionVec().add(lookVec.scale(distance));

            BlockPos pos = new BlockPos(vec);
            if (!isValidTeleport(world, pos)){
                for(double i = distance; i >= 0; i--){
                    vec = shooter.getPositionVec().add(lookVec.scale(i));
                    pos = new BlockPos(vec);

                    if(i <= 0){
                        return;
                    }
                    if (isValidTeleport(world, pos)){
                        break;
                    }

                }

            }
            shooter.setPositionAndUpdate(vec.getX(), vec.getY(), vec.getZ());
        }else if(rayTraceResult instanceof EntityRayTraceResult) {
            Vector3d vec = safelyGetHitPos(rayTraceResult);
            shooter.setPositionAndUpdate(vec.getX(), vec.getY(), vec.getZ());
        }else if(rayTraceResult instanceof BlockRayTraceResult){
            Vector3d vec = rayTraceResult.getHitVec();
            if(isValidTeleport(world, new BlockPos(vec))){
                shooter.setPositionAndUpdate(vec.getX(), vec.getY(), vec.getZ());
            }
        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    /**
     * Checks is a player can be placed at a given position without suffocating.
     */
    public static boolean isValidTeleport(World world, BlockPos pos){
        return !world.getBlockState(pos).isSolid() &&  !world.getBlockState(pos.up()).isSolid() && !world.getBlockState(pos.up(2)).isSolid();
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected String getBookDescription() {
        return "If the spell hits a block, the caster will be teleported to that location. If using Self, the caster will teleport a short distance in that direction.";
    }
}
