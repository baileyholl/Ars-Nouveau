package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;

import net.minecraft.world.World;

import java.util.List;

public class MethodSelf extends AbstractCastMethod {
    public MethodSelf() {
        super(ModConfig.MethodSelfID, "Self");
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity caster, World world, List<AbstractAugment> augments) {
        resolver.onResolveEffect(caster.getEntityWorld(), caster, new EntityRayTraceResult(caster));
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.world, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.getPosition()));
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments) {
        World world = context.getWorld();
        resolver.onResolveEffect(world, context.getPlayer(),  new EntityRayTraceResult(context.getPlayer()));
        resolver.expendMana(context.getPlayer());
        Networking.sendToNearby(context.getWorld(), context.getPlayer(), new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, context.getPlayer().getPosition()));
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments) {
        World world = caster.world;
        resolver.onResolveEffect(world, caster,  new EntityRayTraceResult(caster));
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.world, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.getPosition()));
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity playerIn, LivingEntity target, Hand hand, List<AbstractAugment> augments) {
        World world = playerIn.world;
        resolver.onResolveEffect(world, playerIn,  new EntityRayTraceResult(playerIn));
        resolver.expendMana(playerIn);
        Networking.sendToNearby(playerIn.world, playerIn, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, playerIn.getPosition()));
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Override
    protected String getBookDescription() {
        return "A spell you start with. Applies spells on the caster.";
    }
}
