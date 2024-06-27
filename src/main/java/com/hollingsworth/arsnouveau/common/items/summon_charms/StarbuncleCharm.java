package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.api.entity.ChangeableBehavior;
import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.api.registry.BehaviorRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class StarbuncleCharm extends AbstractSummonCharm {
    public StarbuncleCharm() {
        super();
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        Starbuncle carbuncle = new Starbuncle(world, true);
        Starbuncle.StarbuncleData data = new Starbuncle.StarbuncleData(context.getItemInHand().getOrCreateTag());
        pos = pos.relative(context.getClickedFace());
        carbuncle.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        carbuncle.data = data;
        world.addFreshEntity(carbuncle);
        carbuncle.restoreFromTag();
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        return useOnBlock(context, world, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        Starbuncle.StarbuncleData data = new Starbuncle.StarbuncleData(stack.getOrCreateTag());
        if (data.name != null) {
            tooltip2.add(data.name);
        }
        if(data.adopter != null){
            tooltip2.add(Component.translatable("ars_nouveau.adopter", data.adopter).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
        if(data.bio != null){
            tooltip2.add(Component.literal(data.bio).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)));
        }
        if(data.behaviorTag != null && context != null){
            // danger zone
            try{
                ChangeableBehavior behavior = BehaviorRegistry.create(new Starbuncle(context, true), data.behaviorTag);
                if(behavior != null){
                    behavior.getTooltip(tooltip2);
                }
            }catch (Exception e){
                // :-)
            }
        }
    }
}
