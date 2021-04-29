package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.IRitualCaster;
import com.hollingsworth.arsnouveau.api.ritual.RitualCaster;
import com.hollingsworth.arsnouveau.common.block.RitualBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenRitualBook;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class RitualBook extends ModItem{
    public RitualBook() {
        super(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof RitualBlock){
            IRitualCaster caster = getRitualCaster(context.getItemInHand());
            World world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            RitualTile tile = (RitualTile) world.getBlockEntity(pos);
            tile.setRitual(caster.getSelectedRitual());
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isClientSide && playerIn instanceof ServerPlayerEntity) {
            RitualCaster caster = RitualCaster.deserialize(playerIn.getItemInHand(handIn));
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketOpenRitualBook(player.getItemInHand(handIn).getOrCreateTag(), caster.getUnlockedRitualIDs(), handIn == Hand.MAIN_HAND));
        }
        return new ActionResult<>(ActionResultType.CONSUME, playerIn.getItemInHand(handIn));
    }

    public static void setRitualID(ItemStack stack, String ID){
        getRitualCaster(stack).setRitual(ID);
    }

    public static IRitualCaster getRitualCaster(ItemStack stack){
        return RitualCaster.deserialize(stack);
    }

    public static @Nullable AbstractRitual getSelectedRitual(ItemStack stack){
        return ArsNouveauAPI.getInstance().getRitual(RitualCaster.deserialize(stack).getSelectedRitual());
    }

    public static void unlockRitual(ItemStack stack, String ritualID){
        getRitualCaster(stack).unlockRitual(ritualID);
    }

    public static boolean containsRitual(ItemStack stack, String ritualID){
        return getRitualCaster(stack).getUnlockedRitualIDs().contains(ritualID);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if(stack != null && stack.hasTag()) {
            tooltip.add(new StringTextComponent(RitualBook.getSelectedRitual(stack).getName()));
        }
    }
}
