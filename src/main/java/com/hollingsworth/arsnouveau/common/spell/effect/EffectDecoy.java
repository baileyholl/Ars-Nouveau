package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectDecoy extends AbstractEffect {
    public EffectDecoy() {
        super(GlyphLib.EffectDecoyID, "Decoy");
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, augments, spellContext);
        if(shooter != null){
            BlockPos pos = rayTraceResult.getBlockPos();
            EntityDummy dummy = new EntityDummy(world);
            dummy.ticksLeft = 30 * 20 + getDurationModifier(augments) * 20 * 15;
//            dummy.setUUID(shooter.getUUID());
            dummy.setPos(pos.getX(), pos.getY() +1, pos.getZ());
            dummy.setOwnerID(shooter.getUUID());
            summonLivingEntity(rayTraceResult, world, shooter, augments, spellContext, dummy);
            world.getEntitiesOfClass(MobEntity.class, dummy.getBoundingBox().inflate(20, 10, 20)).forEach(l ->{
                l.setTarget(dummy);
            });
        }
    }

    @Override
    public int getManaCost() {
        return 50;
    }
}
