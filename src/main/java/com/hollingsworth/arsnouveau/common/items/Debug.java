package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.event.timed.EruptionEvent;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketTimedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class Debug extends ModItem {
    public Debug() {
        super(new Item.Properties());
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {

            for (BlockPos p : BlockPos.betweenClosed(context.getClickedPos().immutable().east(20).north(20), context.getClickedPos().immutable().south(20).west(20))) {
                if (context.getLevel().random.nextFloat() < 0.03) {
                    double distance = BlockUtil.distanceFrom(p, context.getClickedPos());
                    int time = (int) (40 + distance * 5 + context.getLevel().random.nextInt(10));
                    EruptionEvent event = new EruptionEvent(context.getLevel(), p.immutable(), time, (int) (distance * 2));
                    EventQueue.getServerInstance().addEvent(event);
                    Networking.sendToNearby(context.getLevel(), context.getClickedPos(), new PacketTimedEvent(event.serialize(new CompoundTag())));
                }
            }
//            EventQueue.getServerInstance().addEvent(new EruptionEvent(context.getLevel(), context.getClickedPos(), 60));
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).east(0)));
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).east(20)));
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).west(20)));
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerIn, InteractionHand handIn) {

        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }
}
