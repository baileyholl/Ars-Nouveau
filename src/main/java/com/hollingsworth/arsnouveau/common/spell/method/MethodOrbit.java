package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityOrbitProjectile;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class MethodOrbit extends AbstractCastMethod {

    public static MethodOrbit INSTANCE = new MethodOrbit();

    private MethodOrbit() {
        super(GlyphLib.MethodOrbitID, "Orbit");
    }


    public void summonProjectiles(Level world, LivingEntity shooter, SpellResolver resolver, SpellStats stats){
        int total = 3 + stats.getBuffCount(AugmentSplit.INSTANCE);
        for(int i = 0; i < total; i++){
            EntityOrbitProjectile wardProjectile = new EntityOrbitProjectile(world, resolver);
            wardProjectile.wardedEntity = shooter;
            wardProjectile.setOwnerID(shooter.getId());
            wardProjectile.setOffset(i);
            wardProjectile.setAccelerates((int) stats.getAccMultiplier());
            wardProjectile.setAoe((float)stats.getAoeMultiplier());
            wardProjectile.extendTimes = (int) stats.getDurationMultiplier();
            wardProjectile.setTotal(total);
            wardProjectile.setColor(resolver.spellContext.colors);
            world.addFreshEntity(wardProjectile);
        }
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        summonProjectiles(world, playerEntity, resolver, spellStats);
        resolver.expendMana(playerEntity);
    }

    @Override
    public void onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        summonProjectiles(context.getLevel(), context.getPlayer(), resolver, spellStats);
        resolver.expendMana(context.getPlayer());
    }

    @Override
    public void onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        summonProjectiles(caster.level, caster, resolver, spellStats);
        resolver.expendMana(caster);
    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        summonProjectiles(caster.level, caster, resolver, spellStats);
        resolver.expendMana(caster);
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }

    @Override
    public String getBookDescription() {
        return "Summons three orbiting projectiles around the caster that will cast a spell on any entities it may hit. Additional projectiles, their speed, radius, and duration may be augmented. Sensitive will cause Orbit to hit blocks.";
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAccelerate.INSTANCE, AugmentDecelerate.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentExtendTime.INSTANCE,
                AugmentDurationDown.INSTANCE, AugmentSensitive.INSTANCE);
    }
}
