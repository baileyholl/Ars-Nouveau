package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.MultiInsertReference;
import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.api.util.LevelPosMap;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class AllayBehavior extends JarBehavior<Allay> {
    public static LevelPosMap ALLAY_MAP = new LevelPosMap(
            (level, pos) -> !(level.getBlockEntity(pos) instanceof MobJarTile mobJarTile) || !(mobJarTile.getEntity() instanceof Allay)
    );

    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile) {
        super.use(state, world, pos, player, handIn, hit, tile);
    }

    @Override
    public void tick(MobJarTile tile) {
        super.tick(tile);

        Level level = tile.getLevel();
        if (level == null) return;
        if (level.getGameTime() % 20 == 0) {
            ALLAY_MAP.addPosition(level, tile.getBlockPos());
        }

        if (level.isClientSide) {
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
        } else {
            if (level.getGameTime() % 40 == 0){
                Function<ItemStack, ItemStack> processor = pickupItems(tile);
                if (processor == null) return;

                for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(tile.getBlockPos()).inflate(5.0D))) {
                    if (!entity.isAlive()) continue;
                    ItemStack remainder = processor.apply(entity.getItem());
                    if (remainder != null) {
                        entity.setItem(remainder);
                    }
                }
            }
        }
    }

    public static Function<ItemStack, ItemStack> pickupItems(MobJarTile tile) {
        Level level = tile.getLevel();
        if (level == null) return null;

        if (tile.getBlockState().getValue(BlockStateProperties.POWERED)) return null;

        Entity entity = tile.getEntity();
        if (!(entity instanceof Allay allay)) return null;
        ItemStack heldStack = allay.getItemInHand(InteractionHand.MAIN_HAND);
        List<FilterableItemHandler> inventories = InvUtil.adjacentInventories(level, tile.getBlockPos());
        if(inventories.isEmpty()){
            return null;
        }

        if(heldStack.getItem() instanceof ItemScroll){
            for(FilterableItemHandler filterableItemHandler : inventories){
                filterableItemHandler.addFilterScroll(heldStack);
            }
        }

        InventoryManager manager = new InventoryManager(inventories);

        return (stack) -> {
            if (heldStack.isEmpty() || heldStack.getItem() instanceof ItemScroll || ItemStack.isSameItem(stack, heldStack)) {
                MultiInsertReference reference = manager.insertStackWithReference(stack);

                if (!reference.isEmpty()) {
                    ItemStack remainder = reference.getRemainder();
                    level.playSound(null, tile.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 1.0F);
                    return remainder;
                }
            }
            return null;
        };
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

    @SubscribeEvent
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
        if (event.isCanceled()) return;
        Level level = event.getLevel();
        if (event.getEntity() instanceof ItemEntity entity) {
            ALLAY_MAP.applyForRange(level, entity.position(), 5, (pos) -> {
                if (level.getBlockEntity(pos) instanceof MobJarTile tile && tile.getEntity() instanceof Allay) {
                    Function<ItemStack, ItemStack> processor = pickupItems(tile);
                    if (processor == null) {
                        return false;
                    }

                    ItemStack remainder = processor.apply(entity.getItem());
                    if (remainder != null) {
                        entity.setItem(remainder);
                        if (remainder.isEmpty()) {
                            event.setCanceled(true);
                        }
                        return true;
                    }
                }
                return false;
            });
        }
    }
}
