package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class EffectRune extends AbstractEffect {

    public static EffectRune INSTANCE = new EffectRune();

    public EffectRune() {
        super(GlyphLib.EffectRuneID, "Rune");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext);
        BlockPos pos = rayTraceResult.getBlockPos();
        pos = rayTraceResult.isInside() ? pos : pos.relative(( rayTraceResult).getDirection());
        spellContext.setCanceled(true);
        if(spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;
        Spell newSpell = new Spell(new ArrayList<>(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
        if(world.getBlockState(pos).getMaterial().isReplaceable()){
            world.setBlockAndUpdate(pos, BlockRegistry.RUNE_BLOCK.defaultBlockState());
            if(world.getBlockEntity(pos) instanceof RuneTile){
                RuneTile runeTile = (RuneTile) world.getBlockEntity(pos);
                if(shooter instanceof Player){
                    runeTile.uuid = shooter.getUUID();
                }
                runeTile.isTemporary = true;
                newSpell.recipe.add(0, MethodTouch.INSTANCE);
                runeTile.spell = newSpell;
                runeTile.color = spellContext.colors.toParticleColor();
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
                "and cannot be recharged. When using Item Pickup, items are deposited into adjacent inventories. Players with Magic Find will be able to read spells inscribed on runes.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf();
    }
}
