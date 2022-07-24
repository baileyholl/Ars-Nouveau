package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
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
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (spellStats.hasBuff(AugmentSensitive.INSTANCE)){
            int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
            int maxItemCrush = 4 + (4 * aoeBuff) + (4 * spellStats.getBuffCount(AugmentPierce.INSTANCE));
            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(new BlockPos(rayTraceResult.getLocation())).inflate(aoeBuff + 1.0));
            if (!itemEntities.isEmpty()) {
                crushItems(world, itemEntities, maxItemCrush);
            }
            return;
        }
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
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (rayTraceResult.getEntity() instanceof ItemEntity){
            int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
            int maxItemCrush = 4 + (4 * aoeBuff) + (4 * spellStats.getBuffCount(AugmentPierce.INSTANCE));
            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(new BlockPos(rayTraceResult.getLocation())).inflate(aoeBuff + 1.0));
            if (!itemEntities.isEmpty()) {
                crushItems(world, itemEntities, maxItemCrush);
            }
        }else {
            dealDamage(world, shooter, (float) ((rayTraceResult.getEntity().isSwimming() ? DAMAGE.get() * 3.0 : DAMAGE.get()) + AMP_VALUE.get() * spellStats.getAmpMultiplier()), spellStats, rayTraceResult.getEntity(), DamageSource.DROWN);
        }
    }

    public static void crushItems(World world, List<ItemEntity> itemEntities, int maxItemCrush) {
        List<CrushRecipe> recipes = world.getRecipeManager().getAllRecipesFor(RecipeRegistry.CRUSH_TYPE);
        CrushRecipe lastHit = null; // Cache this for AOE hits
        int itemsCrushed = 0;
        for (ItemEntity IE : itemEntities) {
            if (itemsCrushed > maxItemCrush) {
                break;
            }

            ItemStack stack = IE.getItem();
            Item item = stack.getItem();

            if (lastHit == null || !lastHit.matches(item.getDefaultInstance(), world)) {
                lastHit = recipes.stream().filter(recipe -> recipe.matches(item.getDefaultInstance(), world)).findFirst().orElse(null);
            }

            if (lastHit == null) continue;

            while (!stack.isEmpty() && itemsCrushed <= maxItemCrush) {
                List<ItemStack> outputs = lastHit.getRolledOutputs(world.random);
                stack.shrink(1);
                itemsCrushed++;
                for (ItemStack result : outputs) {
                    world.addFreshEntity(new ItemEntity(world, IE.getX(), IE.getY(), IE.getZ(), result.copy()));
                }
            }

        }
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
                AugmentFortune.INSTANCE, AugmentSensitive.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Turns stone into gravel, and gravel into sand. Will also crush flowers into bonus dye. For full recipe support, see JEI. Will also harm entities and deals bonus damage to entities that are swimming. Use runes or sensitive to target items.";
    }

    @Override
    public Item getCraftingReagent() {
        return Items.GRINDSTONE;
    }

    @Override
    public int getManaCost() {
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
