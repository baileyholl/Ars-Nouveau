package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.network.PacketAddFadingLight;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class MethodTouch extends AbstractCastMethod {
    public static MethodTouch INSTANCE = new MethodTouch();

    private MethodTouch() {
        super(GlyphLib.MethodTouchID, "Touch");
    }

    @Override
    public int getDefaultManaCost() {
        return 5;
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity caster, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) { }

    @Override
    public void onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        BlockHitResult res = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false);
        resolver.onResolveEffect(world, res);
        resolver.expendMana(context.getPlayer());
        Networking.sendToNearby(context.getLevel(), context.getPlayer(),
                new PacketANEffect(PacketANEffect.EffectType.BURST, res.getBlockPos(), spellContext.colors));
        addFadingLight(context.getLevel(),res.getBlockPos().getX() + 0.5, res.getBlockPos().getY()+ 0.5, res.getBlockPos().getZ()+ 0.5);
    }

    @Override
    public void onCastOnBlock(BlockHitResult res, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(),caster, res);
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.BURST, res.getBlockPos(), spellContext.colors));
        addFadingLight(caster.getLevel(),res.getBlockPos().getX() + 0.5, res.getBlockPos().getY()+ 0.5, res.getBlockPos().getZ()+ 0.5);
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new EntityHitResult(target));
        if(spellContext.getType() != SpellContext.CasterType.RUNE)
            resolver.expendMana(caster);
        Networking.sendToNearby(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.BURST, target.blockPosition(), spellContext.colors));
        addFadingLight(caster.getLevel(),target.blockPosition().getX() + 0.5, target.blockPosition().getY()+ 0.5, target.blockPosition().getZ()+ 0.5);
    }

    public void addFadingLight(Level level, double x, double y, double z) {
        Networking.sendToNearby(level, new BlockPos(x,y,z), new PacketAddFadingLight(x,y,z, Config.TOUCH_LIGHT_DURATION.get(), Config.TOUCH_LIGHT_LUMINANCE.get()));
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellResolver resolver) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(UseOnContext context, SpellStats spellStats, SpellResolver resolver) {
        Level world = context.getLevel();
        BlockHitResult res = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false);
        return resolver.wouldAllEffectsDoWork(res, world, context.getPlayer(), spellStats);
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellResolver resolver) {
        return resolver.wouldAllEffectsDoWork(blockRayTraceResult, caster.getCommandSenderWorld(), caster, spellStats);
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellResolver resolver) {
        return resolver.wouldAllEffectsDoWork(new EntityHitResult(target), caster.getCommandSenderWorld(), caster, spellStats);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentSensitive.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Applies spells at the block or entity that is targeted.";
    }


    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }
}
