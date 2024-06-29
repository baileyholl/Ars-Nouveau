package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.item.inv.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.common.items.data.PotionData;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class EffectInfuse extends AbstractEffect {
    public static EffectInfuse INSTANCE = new EffectInfuse();

    public EffectInfuse() {
        super(GlyphLib.EffectInfuseID, "Infuse");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(spellStats.getAoeMultiplier() > 0 || spellStats.getDurationMultiplier() > 0){
            this.spawnPotionEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        } else {
            super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        }
    }

    public void spawnPotionEntity(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver){
        PotionData potionData = getPotionData(world, shooter, spellContext);
        if(potionData == null){
            return;
        }
        Item potionItem = spellStats.getAoeMultiplier() > 0 ? Items.SPLASH_POTION : Items.LINGERING_POTION;
        ThrownPotion potion = new ThrownPotion(world, shooter);
        potion.setItem(potionData.asPotionStack(potionItem));
        potion.setPos(rayTraceResult.getLocation());
        world.addFreshEntity(potion);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(!(rayTraceResult.getEntity() instanceof LivingEntity livingEntity)){
            return;
        }
        PotionData potionData = getPotionData(world, shooter, spellContext);
        if(potionData == null){
            return;
        }
        potionData.applyEffects(shooter, shooter, livingEntity);

    }

    public @Nullable PotionData getPotionData(Level world, @NotNull LivingEntity shooter, SpellContext spellContext){
        PotionData potionData = null;
        InventoryManager manager = spellContext.getCaster().getInvManager();
        ExtractedStack extractedFlask = manager.extractItem(i -> {
            PotionFlask.FlaskData data = new PotionFlask.FlaskData(i);
            return !data.getPotion().isEmpty() && data.getCount() > 0;
        }, 1);
        if (!extractedFlask.isEmpty()) {
            PotionFlask.FlaskData data = new PotionFlask.FlaskData(extractedFlask.getStack());
            potionData = data.getPotion().clone();
            data.setCount(data.getCount() - 1);
            extractedFlask.returnOrDrop(world, shooter.getOnPos());
        } else {
            ExtractedStack potion = manager.extractItem(i -> i.getItem() instanceof PotionItem, 1);
            if (!potion.isEmpty()) {
                ItemStack stack = potion.getStack();
                potionData = new PotionData(stack.copy());
                stack.shrink(1);
                potion.replaceAndReturnOrDrop(new ItemStack(Items.GLASS_BOTTLE), world, shooter.getOnPos());
            }
        }

        if(potionData == null){
            BlockEntity jarEntity = spellContext.getCaster().getNearbyBlockEntity(i -> i instanceof PotionJarTile jar && jar.getAmount() > 100);
            if(jarEntity instanceof PotionJarTile jar){
                potionData = jar.getData().clone();
                jar.remove(100);
            }
        }
        return potionData;
    }


    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @NotNull
    @Override
    protected Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentAOE.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAOE.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentExtendTime.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Infuses a target with a potion or flask from your inventory. If augmented with AOE, a splash potion will spawn at the location. If augmented with Extend Time, a lingering potion will spawn at the location. Caster blocks may pull from adjacent Potion Jars to cast infuse.";
    }
}
