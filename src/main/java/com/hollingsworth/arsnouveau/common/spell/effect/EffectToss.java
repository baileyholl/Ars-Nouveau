package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.item.inv.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectToss extends AbstractEffect {

    public static EffectToss INSTANCE = new EffectToss();

    public EffectToss() {
        super(GlyphLib.EffectTossID, "Toss");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getEntity().blockPosition();
        summonStack(shooter, spellContext, world, pos, new InventoryManager(spellContext.getCaster()));
    }

    public void summonStack(LivingEntity shooter, SpellContext context, Level world, BlockPos pos, InventoryManager inventoryManager) {
        ExtractedStack casterStack = inventoryManager.extractItem((i) ->{
            // Let tiles export items regardless of the shooter. Runes mimic the player as the shooter.
            if(!i.isEmpty() && context.getCaster() instanceof TileCaster){
                return true;
            }
            if (!i.isEmpty() && shooter instanceof Player) {
                return !ItemStack.matches(shooter.getMainHandItem(), i);
            }
            return false;
        }, 64);
        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, casterStack.stack.copy()));
        casterStack.stack.setCount(0);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        InventoryManager manager = new InventoryManager(spellContext.getCaster());
        if (world.getBlockEntity(rayTraceResult.getBlockPos()) == null) {
            summonStack(shooter, spellContext, world, pos, manager);
            return;
        }
        BlockEntity tileEntity = world.getBlockEntity(rayTraceResult.getBlockPos());
        IItemHandler targetInv = tileEntity.getCapability(Capabilities.ITEM_HANDLER);

        if (targetInv == null) {
            return;
        }
        // Extracts a stack from the caster that can be inserted into the target inventory
        ExtractedStack casterStack = manager.extractByAmount(stackToExtract ->{
            if(stackToExtract.isEmpty())
                return 0;
            if (shooter instanceof Player && ItemStack.matches(shooter.getMainHandItem(), stackToExtract)) {
                return 0;
            }
            for (int i = 0; i < targetInv.getSlots(); i++) {
                ItemStack stackInTarget = targetInv.getStackInSlot(i);
                if(stackInTarget.isEmpty()){
                    return targetInv.getSlotLimit(i);
                }else if (ItemHandlerHelper.canItemStacksStack(stackInTarget, stackToExtract)) {
                    int origSize = stackToExtract.getCount();
                    ItemStack simReturn = targetInv.insertItem(i, stackToExtract, true);
                    int maxRoom = origSize - simReturn.getCount();
                    int adjustedMax = Math.min(maxRoom, targetInv.getSlotLimit(i));
                    if(adjustedMax > 0) {
                        return adjustedMax;
                    }
                }
            }
            return 0;
        });
        casterStack.stack = ItemHandlerHelper.insertItemStacked(targetInv, casterStack.getStack(), false);
        casterStack.returnOrDrop(world, pos);
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
        return "Causes the caster to place an item from their inventory to a location. If this glyph is used on an inventory, the item will attempt to be inserted into it.";
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
