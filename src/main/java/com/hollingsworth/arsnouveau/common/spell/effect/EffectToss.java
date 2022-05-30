package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectToss extends AbstractEffect {

    public static EffectToss INSTANCE = new EffectToss();

    public EffectToss() {
        super(GlyphLib.EffectTossID, "Toss");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext);
        BlockPos pos = rayTraceResult.getEntity().blockPosition();
        summonStack(shooter, spellContext, world, pos);
    }

    public void summonStack(LivingEntity shooter, SpellContext context, Level world, BlockPos pos) {
        ItemStack casterStack = extractStackFromCaster(shooter, context, (i) -> {
            if (!i.isEmpty() && shooter instanceof Player) {
                return !ItemStack.matches(shooter.getMainHandItem(), i);
            }
            return false;
        }, 64);

        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, casterStack.copy()));
        casterStack.setCount(0);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext);
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        if (world.getBlockEntity(rayTraceResult.getBlockPos()) != null && world.getBlockEntity(rayTraceResult.getBlockPos()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            BlockEntity tileEntity = world.getBlockEntity(rayTraceResult.getBlockPos());
            IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

            if (iItemHandler == null) {
                return;
            }

            ItemStack casterStack = getItemFromCaster(shooter, spellContext, (i) -> {

                if (i.isEmpty())
                    return false;
                if (shooter instanceof Player) {
                    return !ItemStack.matches(shooter.getMainHandItem(), i);
                }
                return true;
            });
            ItemStack stack = extractStackFromCaster(shooter, spellContext, (stack1) ->{
                if(stack1.isEmpty())
                    return false;
                for(int i = 0; i < iItemHandler.getSlots(); i++){
                    if(iItemHandler.isItemValid(i, casterStack)){
                        return true;
                    }
                }
                return false;
            }, 64);
            ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, stack, false);
            insertStackToCaster(shooter, spellContext, left);
        } else {
            summonStack(shooter, spellContext, world, pos);
        }
    }

    @Nonnull
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

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
