package com.hollingsworth.arsnouveau.common.spell.method;


import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MethodPantomime extends AbstractCastMethod {
    public static MethodPantomime INSTANCE = new MethodPantomime();

    public MethodPantomime() {
        super(GlyphLib.MethodPantomimeID, "Pantomime");
    }

    public BlockHitResult findPosition(LivingEntity shooter) {
        Vec3 eyes = shooter.getEyePosition(1.0f);
        float viewXRot = shooter.getViewXRot(1.0f);
        double dist = viewXRot < 45.0f ? 1 : 2;
        Vec3 to = eyes.add(shooter.getViewVector(1.0f).scale(dist));
        BlockPos toPos = BlockPos.containing(to);
        return new BlockHitResult(eyes, Direction.getNearest(to.x, to.y, to.z).getOpposite(), toPos, true);
    }

    public CastResolveType getTarget(Level world, LivingEntity shooter, SpellResolver resolver) {
        BlockHitResult res = findPosition(shooter);

        if (res == null) return CastResolveType.FAILURE;

        resolver.onResolveEffect(world, res);
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        return getTarget(world, playerEntity, resolver);
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return getTarget(context.getLevel(), context.getPlayer(), resolver);
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return getTarget(caster.getCommandSenderWorld(), caster, resolver);
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return getTarget(caster.level(), caster, resolver);
    }

    @Override
    protected int getDefaultManaCost() {
        return 5;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Applies spells to the nearest block in your line of sight.";
    }
}
