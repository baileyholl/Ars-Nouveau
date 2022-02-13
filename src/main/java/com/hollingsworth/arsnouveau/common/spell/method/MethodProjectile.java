package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class MethodProjectile extends AbstractCastMethod {
    public static MethodProjectile INSTANCE = new MethodProjectile();

    private MethodProjectile() {
        super(GlyphLib.MethodProjectileID, "Projectile");
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    public void summonProjectiles(Level world, LivingEntity shooter, SpellStats stats, SpellResolver resolver){
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList<>();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, resolver);
        projectiles.add(projectileSpell);
        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

        for(int i =1; i < numSplits + 1; i++){
            Direction offset =shooter.getDirection().getClockWise();
            if(i%2==0) offset = offset.getOpposite();
             // Alternate sides
            BlockPos projPos = shooter.blockPosition().relative(offset, i);
            projPos = projPos.offset(0, 1.5, 0);
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
            spell.setPos(projPos.getX(), projPos.getY(), projPos.getZ());
            projectiles.add(spell);
        }

        float velocity = 1.0f + stats.getBuffCount(AugmentAccelerate.INSTANCE);

        for(EntityProjectileSpell proj : projectiles) {
            proj.shoot(shooter, shooter.xRot, shooter.yRot, 0.0F, velocity, 0.8f);
            world.addFreshEntity(proj);
        }
    }

    // Summons the projectiles directly above the block, facing downwards.
    public void summonProjectiles(Level world, BlockPos pos, LivingEntity shooter, SpellStats stats, SpellResolver resolver){
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList<>();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, resolver);
        projectileSpell.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
        projectiles.add(projectileSpell);

        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

        for(int i =1; i < numSplits + 1; i++){
            Direction offset = shooter.getDirection().getClockWise();
            if(i%2==0) offset = offset.getOpposite();
            // Alternate sides
            BlockPos projPos = pos.relative(offset, i); // TODO: Fix split
            projPos = projPos.offset(0, 1.5, 0);
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
            spell.setPos(projPos.getX(), projPos.getY(), projPos.getZ());
            projectiles.add(spell);
        }
        for(EntityProjectileSpell proj : projectiles) {
            proj.setDeltaMovement(new Vec3(0, -0.1, 0));
            world.addFreshEntity(proj);
        }
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity shooter, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        summonProjectiles(world, shooter, spellStats, resolver);
        resolver.expendMana(shooter);
    }

    @Override
    public void onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        Player shooter = context.getPlayer();
        summonProjectiles(world, shooter, spellStats, resolver);
        resolver.expendMana(shooter);
    }

    /**
     * Cast by entities.
     */
    @Override
    public void onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        caster.lookAt(EntityAnchorArgument.Anchor.EYES, blockRayTraceResult.getLocation().add(0, 0, 0));
        summonProjectiles(caster.getCommandSenderWorld(), blockRayTraceResult.getBlockPos(), caster, spellStats, resolver);
        resolver.expendMana(caster);
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        summonProjectiles(caster.getCommandSenderWorld(), caster, spellStats, resolver);
        resolver.expendMana(caster);
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(UseOnContext context, SpellStats spellStats, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellResolver resolver) {
        return true;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Summons a projectile that applies spell effects when this projectile hits a target or block. Sensitive will allow Projectiles to break plants or other materials that do not block motion.";
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }
}
