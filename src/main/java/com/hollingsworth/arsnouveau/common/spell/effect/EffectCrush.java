package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectCrush extends AbstractEffect {

    public static EffectCrush INSTANCE = new EffectCrush();

    private EffectCrush() {
        super(GlyphLib.EffectCrushID, "Crush");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        List<CrushRecipe> recipes = world.getRecipeManager().getAllRecipesFor(RecipeRegistry.CRUSH_TYPE);
        CrushRecipe lastHit = null; // Cache this for AOE hits
        for(BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats.getBuffCount(AugmentAOE.INSTANCE), spellStats.getBuffCount(AugmentPierce.INSTANCE))){
            BlockState state = world.getBlockState(p);
            Item item = state.getBlock().asItem();
            if(lastHit == null || !lastHit.matches(item.getDefaultInstance(), world)){
                lastHit = null;
                for(CrushRecipe recipe : recipes){
                    if(recipe.matches(item.getDefaultInstance(), world)){
                        lastHit = recipe;
                        break;
                    }
                }
            }

            if(lastHit == null)
                continue;

            List<ItemStack> outputs = lastHit.getRolledOutputs(world.random);
            boolean placedBlock = false;
            for(ItemStack i : outputs){
                if(!placedBlock && i.getItem() instanceof BlockItem){
                    world.setBlockAndUpdate(p, ((BlockItem) i.getItem()).getBlock().defaultBlockState());
                    i.shrink(1);
                    placedBlock = true;
                }
                if(!i.isEmpty()){
                    world.addFreshEntity(new ItemEntity(world, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, i));
                }
            }
            if(!placedBlock)
                world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        dealDamage(world, shooter, (float) ((rayTraceResult.getEntity().isSwimming() ? DAMAGE.get() * 3.0 : DAMAGE.get()) + AMP_VALUE.get() * spellStats.getAmpMultiplier()), spellStats, rayTraceResult.getEntity(), DamageSource.DROWN);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 3.0);
        addAmpConfig(builder, 1.0);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE, AugmentPierce.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Turns stone into gravel, and gravel into sand. Will also crush flowers into bonus dye. For full recipe support, see JEI. Will also harm entities and deals bonus damage to entities that are swimming.";
    }

    @Override
    public Item getCraftingReagent() {
        return Items.GRINDSTONE;
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
