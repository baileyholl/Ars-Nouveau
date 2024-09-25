package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MethodSelf extends AbstractCastMethod {
    public static MethodSelf INSTANCE = new MethodSelf();

    private MethodSelf() {
        super(GlyphLib.MethodSelfID, "Self");
    }

    @Override
    public CastResolveType onCast(ItemStack stack, LivingEntity caster, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new EntityHitResult(caster));
        Networking.sendToNearbyClient(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.blockPosition(), context.getColors()));
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        resolver.onResolveEffect(world, new EntityHitResult(context.getPlayer()));
        Networking.sendToNearbyClient(context.getLevel(), context.getPlayer(), new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, context.getPlayer().blockPosition(), spellContext.getColors()));
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = caster.level;
        resolver.onResolveEffect(world, new EntityHitResult(caster));
        Networking.sendToNearbyClient(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.blockPosition(), spellContext.getColors()));
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnEntity(ItemStack stack, LivingEntity playerIn, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = playerIn.level;
        resolver.onResolveEffect(world, new EntityHitResult(playerIn));
        Networking.sendToNearbyClient(playerIn.level, playerIn, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, playerIn.blockPosition(), spellContext.getColors()));
        return CastResolveType.SUCCESS;
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Applies spells on the caster.";
    }


    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }
}
