package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketGetPersistentData;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

public class ScryingRitual extends AbstractRitual {
    @Override
    protected void tick() {
        if(!getWorld().isClientSide){
            List<ServerPlayerEntity> players =  getWorld().getEntitiesOfClass(ServerPlayerEntity.class, new AxisAlignedBB(getPos()).inflate(5.0));
            if(players.size() > 0){
                for(ServerPlayerEntity playerEntity : players){
                    playerEntity.addEffect(new EffectInstance(ModPotions.SCRYING_EFFECT, 60 * 20 * 5, 1));
                    CompoundNBT tag = playerEntity.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
                    tag.putString("an_scrying", getConsumedItems().get(0).getItem().getRegistryName().toString());
                    playerEntity.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, tag);
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->playerEntity), new PacketGetPersistentData(tag));
                    setFinished();
                }
            }
        }
    }

    @Override
    public boolean canStart() {
        return !getConsumedItems().isEmpty();
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return getConsumedItems().isEmpty() && stack.getItem() instanceof BlockItem;
    }

    @Override
    public String getID() {
        return RitualLib.SCRYING;
    }
}
