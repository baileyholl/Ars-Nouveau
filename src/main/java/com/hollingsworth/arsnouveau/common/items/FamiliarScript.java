package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.FamiliarCap;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliarCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class FamiliarScript extends ModItem{
    public AbstractFamiliarHolder familiar;


    public FamiliarScript(AbstractFamiliarHolder familiar){
        super("familiar_" + familiar.id);
        this.familiar = familiar;
    }
    public FamiliarScript(Properties properties) {
        super(properties);
    }

    public FamiliarScript(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public FamiliarScript(String registryName) {
        super(registryName);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(worldIn.isClientSide)
            return super.use(worldIn, playerIn, handIn);

        IFamiliarCap familiarCap = FamiliarCap.getFamiliarCap(playerIn).orElse(null);
        if(familiarCap != null){
            if(familiarCap.ownsFamiliar(familiar.getId())){
                playerIn.sendMessage(new TranslationTextComponent("ars_nouveau.familiar.owned"), Util.NIL_UUID);
                return super.use(worldIn, playerIn, handIn);
            }
            familiarCap.unlockFamiliar(familiar.getId());
            playerIn.sendMessage(new TranslationTextComponent("ars_nouveau.familiar.unlocked"), Util.NIL_UUID);

        }
        return super.use(worldIn, playerIn, handIn);
    }

}
