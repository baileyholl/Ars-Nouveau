package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EffectLightning extends AbstractEffect {
    public EffectLightning() {
        super(ModConfig.EffectLightningID, "Lightning");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        Vec3d pos = rayTraceResult.getHitVec();
        LightningBoltEntity lightningBoltEntity = new LightningBoltEntity(world, pos.getX(), pos.getY(), pos.getZ(), false);
        lightningBoltEntity.setCaster(shooter instanceof ServerPlayerEntity ? (ServerPlayerEntity) shooter : null);
        ((ServerWorld) world).addLightningBolt(lightningBoltEntity);
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
