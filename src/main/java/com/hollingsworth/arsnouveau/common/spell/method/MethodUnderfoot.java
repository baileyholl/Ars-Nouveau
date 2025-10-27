package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MethodUnderfoot extends AbstractCastMethod {
    public static MethodUnderfoot INSTANCE = new MethodUnderfoot();

    private MethodUnderfoot() {
        super(GlyphLib.MethodUnderfootID, "Underfoot");
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity caster, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        // check if the caster has an entity beneath them or is riding one
        if (caster.getVehicle() != null) {
            resolver.onResolveEffect(caster.getCommandSenderWorld(), new EntityHitResult(caster.getVehicle()));
        } else {
            List<Entity> nearbyEntities = world.getEntities(caster, caster.getBoundingBox().inflate(0.1, 1.5, 0.1));
            if (!nearbyEntities.isEmpty()) {
                resolver.onResolveEffect(caster.getCommandSenderWorld(), new EntityHitResult(nearbyEntities.getFirst()));
            } else {
                resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position, Direction.DOWN, caster.blockPosition().below(), true));
            }
        }
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        LivingEntity caster = context.getPlayer();
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position, Direction.DOWN, caster.blockPosition().below(), true));
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position, Direction.DOWN, caster.blockPosition().below(), true));
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        // check if the caster has an entity beneath them or is riding one
        if (caster.getVehicle() != null) {
            resolver.onResolveEffect(caster.getCommandSenderWorld(), new EntityHitResult(caster.getVehicle()));
        } else {
            List<Entity> nearbyEntities = caster.level().getEntities(caster, caster.getBoundingBox().inflate(0.1, 1.5, 0.1));
            if (!nearbyEntities.isEmpty()) {
                resolver.onResolveEffect(caster.getCommandSenderWorld(), new EntityHitResult(nearbyEntities.getFirst()));
            } else {
                resolver.onResolveEffect(caster.getCommandSenderWorld(), new BlockHitResult(caster.position, Direction.DOWN, caster.blockPosition().below(), true));
            }
        }
        return CastResolveType.SUCCESS;
    }

    @Override
    public int getDefaultManaCost() {
        return 5;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Targets the spell on the block beneath the player.";
    }

}
