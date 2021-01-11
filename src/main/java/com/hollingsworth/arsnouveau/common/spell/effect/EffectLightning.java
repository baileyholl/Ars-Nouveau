package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
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
        super(ModConfig.EffectLightningID, "Lightning");
    }


    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        Vector3d pos = safelyGetHitPos(rayTraceResult);
        LightningBoltEntity lightningBoltEntity = new LightningBoltEntity(EntityType.LIGHTNING_BOLT,world);
        lightningBoltEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        lightningBoltEntity.setCaster(shooter instanceof ServerPlayerEntity ? (ServerPlayerEntity) shooter : null);
        (world).addEntity(lightningBoltEntity);
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
        return Items.CONDUIT;
    }

    @Override
    protected String getBookDescription() {
        return "Summons a lightning bolt at the location";
    }
}
