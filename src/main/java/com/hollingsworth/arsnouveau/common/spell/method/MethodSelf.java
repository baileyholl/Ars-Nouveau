package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

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
    public void onCast(ItemStack stack, LivingEntity caster, World world, List<AbstractAugment> augments, SpellContext context, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new EntityRayTraceResult(caster));
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.blockPosition()));
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        World world = context.getLevel();
        resolver.onResolveEffect(world, context.getPlayer(),  new EntityRayTraceResult(context.getPlayer()));
        resolver.expendMana(context.getPlayer());
        Networking.sendToNearby(context.getLevel(), context.getPlayer(), new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, context.getPlayer().blockPosition()));
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        World world = caster.level;
        resolver.onResolveEffect(world, caster,  new EntityRayTraceResult(caster));
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, caster.blockPosition()));
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity playerIn, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        World world = playerIn.level;
        resolver.onResolveEffect(world, playerIn,  new EntityRayTraceResult(playerIn));
        resolver.expendMana(playerIn);
        Networking.sendToNearby(playerIn.level, playerIn, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, playerIn.blockPosition()));
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellResolver resolver) {
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
