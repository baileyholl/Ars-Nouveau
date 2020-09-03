package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedHelix;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;

public class MethodTouch extends AbstractCastMethod {

    public MethodTouch() {
        super(ModConfig.MethodTouchID, "Touch");
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity caster, World world, ArrayList<AbstractAugment> augments) { }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {
        World world = context.getWorld();
        BlockRayTraceResult res = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);
        resolver.onResolveEffect(world, context.getPlayer(), res);
        resolver.expendMana(context.getPlayer());
        Networking.sendToNearby(context.getWorld(), context.getPlayer(), new PacketANEffect(PacketANEffect.EffectType.BURST, res.getPos()));
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult res, LivingEntity caster, ArrayList<AbstractAugment> augments) {
        resolver.onResolveEffect(caster.getEntityWorld(),caster, res);
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.world, caster, new PacketANEffect(PacketANEffect.EffectType.BURST, res.getPos()));
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {
        resolver.onResolveEffect(caster.getEntityWorld(), caster, new EntityRayTraceResult(target));
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.world, caster, new PacketANEffect(PacketANEffect.EffectType.BURST, target.getPosition()));
    }

    @Override
    protected String getBookDescription() {
        return "Applies spells at the block or entity that is targeted.";
    }
}
