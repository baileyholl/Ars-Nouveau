package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketGetPersistentData;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

public class ScryingRitual extends AbstractRitual {
    @Override
    protected void tick() {

        ParticleUtil.spawnFallingSkyEffect(tile.ritual, tile, rand, getCenterColor().toWrapper());
        if(getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide)
            incrementProgress();


        if(!getWorld().isClientSide && getProgress() >= 15){
            List<ServerPlayerEntity> players =  getWorld().getEntitiesOfClass(ServerPlayerEntity.class, new AxisAlignedBB(getPos()).inflate(5.0));
            if(players.size() > 0){
                for(ServerPlayerEntity playerEntity : players){
                    playerEntity.addEffect(new EffectInstance(ModPotions.SCRYING_EFFECT, 60 * 20 * 5 * (didConsumeItem(ArsNouveauAPI.getInstance().getGlyphItem(AugmentExtendTime.INSTANCE)) ? 3 : 1), 0));
                    CompoundNBT tag = playerEntity.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
                    ItemStack item = getConsumedItems().stream().filter(i -> i.getItem() instanceof BlockItem).findFirst().orElse(ItemStack.EMPTY);
                    tag.putString("an_scrying", item.getItem().getRegistryName().toString());
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
        Item extendTime = ArsNouveauAPI.getInstance().getGlyphItem(AugmentExtendTime.INSTANCE);
        if(didConsumeItem(extendTime) && getConsumedItems().size() == 1 && stack.getItem() instanceof BlockItem)
            return true;

        if(!getConsumedItems().isEmpty() && stack.getItem() instanceof BlockItem)
            return false;

        if(didConsumeItem(stack.getItem()))
            return false;

        if(getConsumedItems().isEmpty() && stack.getItem() instanceof BlockItem)
            return true;

        return stack.getItem() == extendTime;
    }

    @Override
    public ParticleColor getCenterColor() {
        return ParticleColor.makeRandomColor(50, 155, 80, rand);
    }

    @Override
    public String getLangName() {
        return "Scrying";
    }

    @Override
    public String getLangDescription() {
        return "Grants vision of a given block through any other block for a given time. White particles signify you are very close, green is semi-far, and blue particles are blocks very far from you.  To complete the ritual, throw any block of your choice before starting. You may also add a Glyph of Extend Time to increase the duration to 15 minutes.";
    }

    @Override
    public String getID() {
        return RitualLib.SCRYING;
    }
}
