package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.network.PacketAddFadingLight;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
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
    public CastResolveType onCast(ItemStack stack, LivingEntity caster, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        return CastResolveType.FAILURE;
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        BlockHitResult res = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false);
        resolver.onResolveEffect(world, res);
        Networking.sendToNearbyClient(context.getLevel(), context.getPlayer(),
                new PacketANEffect(PacketANEffect.EffectType.BURST, res.getBlockPos(), spellContext.getColors()));
        addFadingLight(context.getLevel(), res.getBlockPos().getX() + 0.5, res.getBlockPos().getY() + 0.5, res.getBlockPos().getZ() + 0.5);
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult res, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), res);
        Networking.sendToNearbyClient(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.BURST, res.getBlockPos(), spellContext.getColors()));
        addFadingLight(caster.level(), res.getBlockPos().getX() + 0.5, res.getBlockPos().getY() + 0.5, res.getBlockPos().getZ() + 0.5);
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnEntity(ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new EntityHitResult(target));
        Networking.sendToNearbyClient(caster.level, caster, new PacketANEffect(PacketANEffect.EffectType.BURST, target.blockPosition(), spellContext.getColors()));
        addFadingLight(caster.level(), target.blockPosition().getX() + 0.5, target.blockPosition().getY() + 0.5, target.blockPosition().getZ() + 0.5);
        return spellContext.getCaster().getCasterType() != SpellContext.CasterType.RUNE ? CastResolveType.SUCCESS : CastResolveType.SUCCESS_NO_EXPEND;
    }

    public void addFadingLight(Level level, double x, double y, double z) {
        Networking.sendToNearbyClient(level, BlockPos.containing(x, y, z), new PacketAddFadingLight(x, y, z));
    }

   @NotNull
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
