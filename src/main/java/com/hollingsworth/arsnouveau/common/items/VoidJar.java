package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class VoidJar extends ItemScroll{

    public VoidJar() {
        super(ItemsRegistry.defaultItemProperties().maxStackSize(1), LibItemNames.VOID_JAR);
    }

    public void toggleStatus(PlayerEntity playerEntity, ItemStack stack){
        CompoundNBT tag = stack.getTag();
        if(tag.getBoolean("on")){
            tag.putBoolean("on", false);
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.off"));
        }else{
            tag.putBoolean("on", true);
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.on"));
        }
    }

    public static boolean tryVoiding(PlayerEntity player, ItemStack pickingUp) {
        NonNullList<ItemStack> list =  player.inventory.mainInventory;
        boolean voided = false;
        for(int i = 0; i < 9; i++){
            ItemStack jar = list.get(i);
            if(jar.getItem() == ItemsRegistry.VOID_JAR){
                if(isActive(jar) && containsItem(pickingUp, jar.getTag())){
                    ManaCapability.getMana(player).ifPresent(iMana -> iMana.addMana(5.0 * pickingUp.getCount()));
                    pickingUp.setCount(0);
                    voided = true;
                    break;
                }
            }
        }
        return voided;
    }

    public static boolean isActive(ItemStack stack){
        return stack.hasTag() && stack.getTag().getBoolean("on");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
        if(worldIn.isRemote)
            return super.onItemRightClick(worldIn, player, handIn);
        ItemStack stack = player.getHeldItem(handIn);
        CompoundNBT tag = stack.getTag();
        ItemScroll itemScroll = (ItemScroll) stack.getItem();


        if(handIn == Hand.MAIN_HAND){
            ItemStack stackToWrite = player.getHeldItemOffhand();
            if(player.isSneaking()){
                toggleStatus(player, stack);
                return ActionResult.resultConsume(stack);
            }

            if(!stackToWrite.isEmpty()){
                if(itemScroll.containsItem(stackToWrite, tag)) {
                    PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.scribe.item_removed"));
                    removeItem(stackToWrite, tag);
                    player.setActiveHand(handIn);
                    return ActionResult.resultFail(stack);
                }
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.scribe.item_added"));
                itemScroll.addItem(stackToWrite, tag);
                player.setActiveHand(handIn);
                return ActionResult.resultFail(stack);
            }

        }

        return ActionResult.resultSuccess(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        if(stack.hasTag()){
            if(stack.getTag().getBoolean("on")){
                tooltip2.add(new TranslationTextComponent("ars_nouveau.on"));
            }else{
                tooltip2.add(new TranslationTextComponent("ars_nouveau.off"));
            }
        }

        super.addInformation(stack, worldIn, tooltip2, flagIn);
    }
}
