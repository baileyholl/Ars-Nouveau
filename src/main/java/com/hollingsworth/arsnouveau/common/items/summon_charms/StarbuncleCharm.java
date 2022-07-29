package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.BehaviorRegistry;
import com.hollingsworth.arsnouveau.common.entity.ChangeableBehavior;
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

public class StarbuncleCharm extends AbstractSummonCharm {
    public StarbuncleCharm() {
        super();
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        Starbuncle carbuncle = new Starbuncle(world, true);
        Starbuncle.StarbuncleData data = new Starbuncle.StarbuncleData(context.getItemInHand().getOrCreateTag());
        carbuncle.setPos(pos.getX(), pos.above().getY(), pos.getZ());
        carbuncle.data = data;
        carbuncle.restoreFromTag();
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
        if (data.name != null) {
            tooltip2.add(data.name);
        }
        if(data.behaviorTag != null && worldIn != null){
            // danger zone
            try{
                ChangeableBehavior behavior = BehaviorRegistry.create(new Starbuncle(worldIn, true), data.behaviorTag);
                if(behavior != null){
                    behavior.getTooltip(tooltip2);
                }
            }catch (Exception e){
                // :-)
            }
        }
    }
}
