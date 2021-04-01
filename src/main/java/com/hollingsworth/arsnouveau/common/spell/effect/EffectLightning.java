package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.LightningEntity;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectLightning extends AbstractEffect {
    public EffectLightning() {
        super(GlyphLib.EffectLightningID, "Lightning");
    }


    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        Vector3d pos = safelyGetHitPos(rayTraceResult);
        LightningEntity lightningBoltEntity = new LightningEntity(ModEntities.LIGHTNING_ENTITY,world);
        lightningBoltEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        lightningBoltEntity.setCaster(shooter instanceof ServerPlayerEntity ? (ServerPlayerEntity) shooter : null);
        lightningBoltEntity.amps = getAmplificationBonus(augments);
        lightningBoltEntity.extendTimes = getDurationModifier(augments);
        (world).addEntity(lightningBoltEntity);
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.HEART_OF_THE_SEA;
    }

    @Override
    public String getBookDescription() {
        return "Summons a lightning bolt at the location. Entities struck will be given the Shocked effect. Shocked causes all additional lightning damage to deal bonus damage, and increases the level of Shocked up to III. Lightning also deals bonus damage to entities that are wet. " +
                "Can be augmented with Amplify, Dampen, and Extend Time.";
    }
}
