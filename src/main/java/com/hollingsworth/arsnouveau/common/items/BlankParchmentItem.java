package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.items.data.ScryPosData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BlankParchmentItem extends ModItem implements IScribeable {
    public BlankParchmentItem(Properties properties) {
        super(properties);
    }


    public BlankParchmentItem() {
        super();
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide)
            return super.useOn(pContext);
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof ICameraMountable) {
            ItemStack stack = new ItemStack(ItemsRegistry.SCRYER_SCROLL.get());
            stack.set(DataComponentRegistry.SCRY_DATA, new ScryPosData(pContext.getClickedPos()));
            if (!pContext.getPlayer().addItem(stack)) {
                pContext.getLevel().addFreshEntity(new ItemEntity(pContext.getLevel(), pContext.getPlayer().getX(), pContext.getPlayer().getY(), pContext.getPlayer().getZ(), stack));
            }
            if (!pContext.getPlayer().hasInfiniteMaterials()) {
                pContext.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        ItemStack spellParchment = new ItemStack(ItemsRegistry.SPELL_PARCHMENT.get());
        if (spellParchment.getItem() instanceof IScribeable scribeable) {
            boolean success = scribeable.onScribe(world, pos, player, handIn, spellParchment);
            if (world.getBlockEntity(pos) instanceof ScribesTile scribesTile && success) {
                scribesTile.setStack(spellParchment);
            }
            return success;
        }
        return false;
    }
}
