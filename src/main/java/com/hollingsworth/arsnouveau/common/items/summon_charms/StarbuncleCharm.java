package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hollingsworth.arsnouveau.common.entity.Starbuncle.COLOR;

public class StarbuncleCharm extends AbstractSummonCharm {
    public StarbuncleCharm() {
        super();
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        Starbuncle carbuncle = new Starbuncle(world, true);
        Starbuncle.StarbuncleData data = new Starbuncle.StarbuncleData(context.getItemInHand().getOrCreateTag());

        carbuncle.setPos(pos.getX(), pos.above().getY(), pos.getZ());
        carbuncle.FROM_LIST = data.FROM_LIST;
        carbuncle.TO_LIST = data.TO_LIST;
        carbuncle.whitelist = data.whitelist;
        carbuncle.blacklist = data.blacklist;
        carbuncle.allowedItems = data.allowedItems;
        carbuncle.ignoreItems = data.ignoreItems;
        carbuncle.pathBlock = data.pathBlock;
        carbuncle.bedPos = data.bedPos;
        carbuncle.getEntityData().set(Starbuncle.TO_POS_SIZE, data.TO_LIST.size());
        carbuncle.getEntityData().set(Starbuncle.FROM_POS_SIZE, data.FROM_LIST.size());
        carbuncle.setCustomName(data.name);
        if(data.color != null)
            carbuncle.getEntityData().set(COLOR, data.color);
        world.addFreshEntity(carbuncle);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        return useOnBlock(context, world, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        Starbuncle.StarbuncleData data = new Starbuncle.StarbuncleData(stack.getOrCreateTag());
        if(data.name != null){
            tooltip2.add(data.name);
        }
        tooltip2.add(Component.translatable("ars_nouveau.starbuncle.storing", data.TO_LIST.size()));
        tooltip2.add(Component.translatable("ars_nouveau.starbuncle.taking", data.FROM_LIST.size()));
    }
}
