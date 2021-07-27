package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityOrbitProjectile;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MethodOrbit extends AbstractCastMethod {

    public static MethodOrbit INSTANCE = new MethodOrbit();

    private MethodOrbit() {
        super(GlyphLib.MethodOrbitID, "Orbit");
    }


    public void summonProjectiles(World world, LivingEntity shooter, SpellResolver resolver, List<AbstractAugment> augments){
        int total = 3 + getBuffCount(augments, AugmentSplit.class);
        for(int i = 0; i < total; i++){
            EntityOrbitProjectile wardProjectile = new EntityOrbitProjectile(world, shooter);
            wardProjectile.wardedEntity = shooter;
            wardProjectile.setOwnerID(shooter.getId());
            wardProjectile.spellResolver = resolver;
            wardProjectile.setOffset(i);
            wardProjectile.pierceLeft = getBuffCount(augments, AugmentPierce.class);
            wardProjectile.setAccelerates(getBuffCount(augments, AugmentAccelerate.class));
            wardProjectile.setAoe(getBuffCount(augments, AugmentAOE.class));
            wardProjectile.extendTimes = getBuffCount(augments, AugmentExtendTime.class) - getBuffCount(augments, AugmentDurationDown.class);
            wardProjectile.setTotal(total);
            wardProjectile.setColor(resolver.spellContext.colors);
            world.addFreshEntity(wardProjectile);
        }
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellContext context, SpellResolver resolver) {
        summonProjectiles(world, playerEntity, resolver, augments);
        resolver.expendMana(playerEntity);
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        summonProjectiles(context.getLevel(), context.getPlayer(), resolver, augments);
        resolver.expendMana(context.getPlayer());
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        summonProjectiles(caster.level, caster, resolver, augments);
        resolver.expendMana(caster);
    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        summonProjectiles(caster.level, caster, resolver, augments);
        resolver.expendMana(caster);
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Override
    public String getBookDescription() {
        return "Summons three orbiting projectiles around the caster that will cast a spell on any entities it may hit. Additional projectiles, their speed, radius, and duration may be augmented.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAccelerate.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentExtendTime.INSTANCE,
                AugmentDurationDown.INSTANCE);
    }
}
