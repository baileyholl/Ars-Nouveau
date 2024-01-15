package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectRune extends AbstractEffect {

    public static EffectRune INSTANCE = new EffectRune();

    public EffectRune() {
        super(GlyphLib.EffectRuneID, "Rune");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        pos = rayTraceResult.isInside() ? pos : pos.relative((rayTraceResult).getDirection());
        SpellContext newContext = spellContext.makeChildContext();
        spellContext.setCanceled(true);
        if (world.getBlockState(pos).canBeReplaced()) {
            if(!world.isInWorldBounds(pos))
                return;
            BlockState placementState = BlockRegistry.RUNE_BLOCK.get().getStateForPlacement(new BlockPlaceContext(getPlayer(shooter, (ServerLevel) world), InteractionHand.MAIN_HAND, ItemStack.EMPTY, rayTraceResult));
            world.setBlockAndUpdate(pos, placementState);
            if (world.getBlockEntity(pos) instanceof RuneTile runeTile) {
                if (shooter instanceof Player) {
                    runeTile.uuid = shooter.getUUID();
                }
                runeTile.isTemporary = true;
                Spell newSpell = newContext.getSpell().clone();
                newSpell.recipe.add(0, MethodTouch.INSTANCE);
                runeTile.spell = newSpell;
                runeTile.isSensitive = spellStats.isSensitive();
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Override
    public String getBookDescription() {
        return "Places a rune on the ground that will cast the spell on targets that touch the rune. Unlike runes placed by Runic Chalk, these runes are temporary " +
                "and cannot be recharged. When using Item Pickup, items are deposited into adjacent inventories. Sensitive will cause the rune to use the Owner's inventory for pickup and usage instead. Players with Magic Find will be able to read spells inscribed on runes.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentSensitive.INSTANCE);
    }
}
