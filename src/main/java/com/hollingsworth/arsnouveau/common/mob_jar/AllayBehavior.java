package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.item.inv.FilterSet;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.MultiInsertReference;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AllayBehavior extends JarBehavior<Allay> {

    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile) {
        super.use(state, world, pos, player, handIn, hit, tile);
    }

    @Override
    public void tick(MobJarTile tile) {
        super.tick(tile);
        if(tile.getLevel().isClientSide){
            Allay allay = entityFromJar(tile);
            allay.tickCount++;
            allay.holdingItemAnimationTicks0 = allay.holdingItemAnimationTicks;
            if (allay.hasItemInHand()) {
                allay.holdingItemAnimationTicks = Mth.clamp(allay.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
            } else {
                allay.holdingItemAnimationTicks = Mth.clamp(allay.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
            }

            if (allay.isDancing()) {
                ++allay.dancingAnimationTicks;
                allay.spinningAnimationTicks0 = allay.spinningAnimationTicks;
                if (allay.isSpinning()) {
                    ++allay.spinningAnimationTicks;
                } else {
                    --allay.spinningAnimationTicks;
                }

                allay.spinningAnimationTicks = Mth.clamp(allay.spinningAnimationTicks, 0.0F, 15.0F);
            } else {
                allay.dancingAnimationTicks = 0.0F;
                allay.spinningAnimationTicks = 0.0F;
                allay.spinningAnimationTicks0 = 0.0F;
            }
        }else{
            Level level = tile.getLevel();
            if(level.getGameTime() % 40 == 0){
                Allay allay = entityFromJar(tile);
                ItemStack heldStack = allay.getItemInHand(InteractionHand.MAIN_HAND);
                List<FilterableItemHandler> inventories = InvUtil.adjacentInventories(level, tile.getBlockPos());
                if(inventories.isEmpty()){
                    return;
                }

                if(heldStack.getItem() instanceof ItemScroll){
                    for(FilterableItemHandler filterableItemHandler : inventories) {
                        if(filterableItemHandler.filters instanceof FilterSet.ListSet listSet){
                            listSet.addFilterScroll(heldStack, filterableItemHandler.getHandler());
                        }
                    }
                }
                InventoryManager manager = new InventoryManager(inventories);
                for(ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(tile.getBlockPos()).inflate(5.0D))){
                    if(entity.isAlive() && !entity.getItem().isEmpty()){
                        if (heldStack.isEmpty() || heldStack.getItem() instanceof ItemScroll || ItemStack.isSameItem(entity.getItem(), heldStack)) {
                            MultiInsertReference reference = manager.insertStackWithReference(entity.getItem());
                            if(!reference.isEmpty()) {
                                ItemStack remainder = reference.getRemainder();
                                entity.setItem(remainder);
                                level.playSound(null, tile.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 1.0F);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldUsePartialTicks(MobJarTile pBlockEntity) {
        return true;
    }

    @Override
    public Vec3 translate(MobJarTile pBlockEntity) {
        return new Vec3(0, 0.2, 0);
    }

    @Override
    public void getTooltip(MobJarTile tile, List<Component> tooltips) {
        super.getTooltip(tile, tooltips);
        Allay allay = entityFromJar(tile);
        if(allay.getMainHandItem().getItem() instanceof ItemScroll scroll){
            scroll.appendHoverText(allay.getMainHandItem(), Item.TooltipContext.of(tile.getLevel()), tooltips, TooltipFlag.Default.NORMAL);
        }
    }
}
