package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MethodSelf extends AbstractCastMethod {
    public static MethodSelf INSTANCE = new MethodSelf();

    private MethodSelf() {
        super(GlyphLib.MethodSelfID, "Self");
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity caster, Level world, List<AbstractAugment> augments, SpellContext context, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new EntityHitResult(caster));
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.blockPosition()));
    }

    @Override
    public void onCastOnBlock(UseOnContext context, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        resolver.onResolveEffect(world, context.getPlayer(),  new EntityHitResult(context.getPlayer()));
        resolver.expendMana(context.getPlayer());
        Networking.sendToNearby(context.getLevel(), context.getPlayer(), new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, context.getPlayer().blockPosition()));
    }

    @Override
    public void onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        Level world = caster.level;
        resolver.onResolveEffect(world, caster,  new EntityHitResult(caster));
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.blockPosition()));
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity playerIn, Entity target, InteractionHand hand, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        Level world = playerIn.level;
        resolver.onResolveEffect(world, playerIn,  new EntityHitResult(playerIn));
        resolver.expendMana(playerIn);
        Networking.sendToNearby(playerIn.level, playerIn, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, playerIn.blockPosition()));
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(UseOnContext context, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockHitResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Applies spells on the caster.";
    }


    @Override
    public Item getCraftingReagent() {
        return Items.GLASS_BOTTLE;
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }
}
