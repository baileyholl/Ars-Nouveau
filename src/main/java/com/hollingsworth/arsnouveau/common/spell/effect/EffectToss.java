package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectToss extends AbstractEffect {
    public EffectToss() {
        super(GlyphLib.EffectTossID, "Toss");
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext);
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        if(world.getBlockEntity(rayTraceResult.getBlockPos()) != null && world.getBlockEntity(rayTraceResult.getBlockPos()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()){
            TileEntity tileEntity = world.getBlockEntity(rayTraceResult.getBlockPos());
            IItemHandler iItemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

            if(iItemHandler != null){
               getItemFromCaster(shooter, spellContext, (i) ->{
                   if(i.isEmpty())
                       return false;
                   if(shooter instanceof PlayerEntity){
                       if(ItemStack.matches(shooter.getMainHandItem(), i))
                           return false;
                   }
                    ItemStack orig = i.copy();
                    ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, orig, false);
                    if(left.sameItem(orig)){
                        return false;
                    }
                    i.setCount(left.getCount());
                    return true;
                });

            }
        } else{
            ItemStack stack =  getItemFromCaster(shooter, spellContext, (i) -> {
                if (!i.isEmpty() && shooter instanceof PlayerEntity) {
                    return !ItemStack.matches(shooter.getMainHandItem(), i);
                }
                return true;
            });
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copy()));
            stack.setCount(0);
        }
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Override
    public String getBookDescription() {
        return "Causes the caster to place an item from their inventory to a location. If this glyph is used on an inventory, the item will attempt to be inserted into it.";
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf();
    }
}
