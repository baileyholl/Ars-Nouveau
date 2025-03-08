package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BookwyrmCharm extends ModItem {
    public BookwyrmCharm() {
        super(defaultProps().component(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData().setColor("blue")));
    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        if (world.isClientSide) return InteractionResult.SUCCESS;
        if (world.getBlockEntity(pos) instanceof StorageLecternTile tile) {
            EntityBookwyrm bookwyrm = tile.addBookwyrm();
            if(bookwyrm != null){
                bookwyrm.fromCharmData(pContext.getItemInHand().getOrDefault(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData()));
                Player player = pContext.getPlayer();
                if (player == null || !pContext.getPlayer().hasInfiniteMaterials()) {
                    pContext.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        stack.addToTooltip(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, context, tooltip2::add, flagIn);
        tooltip2.add(Component.translatable("ars_nouveau.tooltip.bookwyrm"));
    }
}
