package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.loot.DungeonLootEnhancerModifier;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.data.PresentData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class Present extends ModItem{

    public Present(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if(pLevel.isClientSide)
            return;
        if(pEntity instanceof Player player && !pStack.has(DataComponentRegistry.PRESENT)){
            pStack.set(DataComponentRegistry.PRESENT, new PresentData(player.getName().getString(), Optional.of(player.getUUID())));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if(pLevel.isClientSide)
            return super.use(pLevel, pPlayer, pUsedHand);
        PresentData presentData = pPlayer.getItemInHand(pUsedHand).get(DataComponentRegistry.PRESENT);
        if(presentData == null)
            return super.use(pLevel, pPlayer, pUsedHand);
        int bonusRolls = presentData.uuid().isPresent() && !presentData.uuid().get().equals(pPlayer.getUUID()) ? 2 : 0;
        DungeonLootEnhancerModifier modifier = new DungeonLootEnhancerModifier(new LootItemCondition[]{},
                0.5, 0.2, 0.1,3 + bonusRolls, 1 + bonusRolls, 1 + bonusRolls);
        List<ItemStack> stacks = DungeonLootTables.getRandomRoll(modifier);
        if(stacks.isEmpty()){
            Starbuncle giftStarby = new Starbuncle(pLevel, true);
            giftStarby.setPos(pPlayer.getX(), pPlayer.getY(), pPlayer.getZ());
            pLevel.addFreshEntity(giftStarby);
        }
        for(ItemStack stack : stacks){
            ItemEntity entity = new ItemEntity(pLevel, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), stack);
            pLevel.addFreshEntity(entity);
        }
        if (!pPlayer.hasInfiniteMaterials()) {
            pPlayer.getItemInHand(pUsedHand).shrink(1);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        PresentData data = stack.get(DataComponentRegistry.PRESENT);
        stack.addToTooltip(DataComponentRegistry.PRESENT, context, tooltip2::add, flagIn);
    }
}
