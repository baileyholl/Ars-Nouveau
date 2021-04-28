package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectSummonDecoy extends AbstractEffect {
    public EffectSummonDecoy() {
        super(GlyphLib.EffectDecoyID, "Summon Decoy");
    }


    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolve(rayTraceResult, world, shooter, augments, spellContext);
        if(shooter != null){
            Vector3d pos = safelyGetHitPos(rayTraceResult);
            EntityDummy dummy = new EntityDummy(world);
            dummy.ticksLeft = 30 * 20 + getDurationModifier(augments) * 20 * 15;
//            dummy.setUUID(shooter.getUUID());
            dummy.setPos(pos.x, pos.y +1, pos.z);
            dummy.setOwnerID(shooter.getUUID());
            summonLivingEntity(rayTraceResult, world, shooter, augments, spellContext, dummy);
            world.getEntitiesOfClass(MobEntity.class, dummy.getBoundingBox().inflate(20, 10, 20)).forEach(l ->{
                l.setTarget(dummy);
            });
        }
    }


    @Override
    public int getManaCost() {
        return 200;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Override
    public String getBookDescription() {
        return "Summons a decoy of yourself. Upon summoning, the decoy will attract any nearby mobs to attack it. Does not apply summoning sickness.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.ARMOR_STAND;
    }
}
