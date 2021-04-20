package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.RitualBlock;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenRitualBook;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class RitualBook extends ModItem{
    public RitualBook() {
        super(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof RitualBlock){
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {

        if(!worldIn.isClientSide && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenRitualBook());
        }
        return new ActionResult<>(ActionResultType.CONSUME, playerIn.getItemInHand(handIn));
    }
}
