package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.item.inv.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import com.hollingsworth.arsnouveau.common.util.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class EffectToss extends AbstractEffect {

    public static EffectToss INSTANCE = new EffectToss();

    public EffectToss() {
        super(GlyphLib.EffectTossID, "Toss");
    }

    public int getStackSize(SpellStats spellStats) {
        if (spellStats.hasBuff(AugmentExtract.INSTANCE)) return 1;

        double amp = spellStats.getAmpMultiplier();

        return (int) (64 * Math.pow(2, amp));
    }

    private ExtractedStack extractItem(InventoryManager inventory, Predicate<ItemStack> predicate, int count, boolean randomized) {
        if (randomized) {
            return inventory.extractRandomItem(predicate, count);
        }
        return inventory.extractItem(predicate, count);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getEntity().blockPosition();
        summonStack(shooter, spellContext, spellStats, world, pos, new InventoryManager(spellContext.getCaster()));
    }

    public void processStacks(LivingEntity shooter, SpellContext context, SpellStats spellStats, InventoryManager inventoryManager, Consumer<ExtractedStack> consumer) {
        int amount = getStackSize(spellStats);
        while (amount > 0) {
            int size = Math.min(amount, 64);
            ExtractedStack casterStack = this.extractItem(inventoryManager, (i) -> {
                if (i.isEmpty()) return false;
                if (context.getCaster() instanceof TileCaster) {
                    return true;
                }
                return shooter instanceof Player && !ItemStack.matches(shooter.getMainHandItem(), i);
            }, size, spellStats.isRandomized());
            consumer.accept(casterStack);
            amount -= size;
        }
    }

    public void summonStack(LivingEntity shooter, SpellContext context, SpellStats spellStats, Level world, BlockPos pos, InventoryManager inventoryManager) {
        this.processStacks(shooter, context, spellStats, inventoryManager, stack -> {
                world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack.stack.copy()));
                stack.stack.setCount(0);
        });
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        InventoryManager manager = new InventoryManager(spellContext.getCaster());
        if (world.getBlockEntity(rayTraceResult.getBlockPos()) == null) {
            summonStack(shooter, spellContext, spellStats, world, pos, manager);
            return;
        }
        IItemHandler targetInv = world.getCapability(Capabilities.ItemHandler.BLOCK, rayTraceResult.getBlockPos(), null);

        if (targetInv == null) {
            return;
        }

        this.processStacks(shooter, spellContext, spellStats, manager, (stack) -> {
            stack.stack = ItemHandlerHelper.insertItemStacked(targetInv, stack.getStack(), false);
            stack.returnOrDrop(world, pos);
        });
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @Override
    public String getBookDescription() {
        return "Causes the caster to place an item from their inventory to a location. If this glyph is used on an inventory, the item will attempt to be inserted into it. Toss throws 64 items by default. Dampen will halve the amount each time. Amplify will double the amount each time. Randomize will select a random stack.";
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtract.INSTANCE, AugmentRandomize.INSTANCE, AugmentDampen.INSTANCE, AugmentAmplify.INSTANCE);
    }
}
