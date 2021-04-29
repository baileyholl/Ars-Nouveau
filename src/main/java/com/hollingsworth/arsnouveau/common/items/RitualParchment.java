package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RitualParchment extends ModItem{
    public AbstractRitual ritual;

    public RitualParchment(Properties properties) {
        super(properties);
    }

    public RitualParchment(String registryName, AbstractRitual ritual){
        super(ItemsRegistry.defaultItemProperties(), registryName);
        this.ritual = ritual;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(worldIn.isClientSide || ritual == null)
            return super.use(worldIn, playerIn, handIn);


        playerIn.inventory.items.forEach(itemStack -> {
            if(itemStack.getItem() instanceof RitualBook){
                if(RitualBook.containsRitual(itemStack, ritual.getID())){
                    playerIn.sendMessage(new TranslationTextComponent("ars_nouveau.ritual.known"),  Util.NIL_UUID);
                    return;
                }
                RitualBook.unlockRitual(itemStack, ritual.getID());
                playerIn.getItemInHand(handIn).shrink(1);
                playerIn.sendMessage(new TranslationTextComponent("ars_nouveau.ritual.unlocked"),  Util.NIL_UUID);
            }
        });
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
//        if(spellPart != null){
//            if(!Config.isSpellEnabled(this.spellPart.tag)){
//                tooltip2.add(new StringTextComponent("Disabled. Cannot be used."));
//            }
//        }
    }

}
