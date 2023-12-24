package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.api.scrying.SingleBlockScryer;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketGetPersistentData;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class RitualScrying extends AbstractRitual {
    @Override
    protected void tick() {

        ParticleUtil.spawnFallingSkyEffect(this, tile, rand, getCenterColor().toWrapper());
        if (getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide)
            incrementProgress();

        if (!getWorld().isClientSide && getProgress() >= 15) {
            List<ServerPlayer> players = getWorld().getEntitiesOfClass(ServerPlayer.class, new AABB(getPos()).inflate(5.0));
            if (!players.isEmpty()) {
                ItemStack item = getConsumedItems().stream().filter(i -> i.getItem() instanceof BlockItem).findFirst().orElse(ItemStack.EMPTY);
                int modifier = didConsumeItem(ItemsRegistry.MANIPULATION_ESSENCE) ? 3 : 1;
                for (ServerPlayer playerEntity : players) {
                    if (item.getItem() instanceof BlockItem blockItem)
                        RitualScrying.grantScrying(playerEntity, 60 * 20 * 5 * modifier, new SingleBlockScryer(blockItem.getBlock()));
                }
            }
            setFinished();
        }
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.SCRYING);
    }

    public static void grantScrying(ServerPlayer playerEntity, int ticks, IScryer scryer) {
        playerEntity.addEffect(new MobEffectInstance(ModPotions.SCRYING_EFFECT.get(), ticks));
        CompoundTag tag = playerEntity.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        tag.put("an_scryer", scryer.toTag(new CompoundTag()));
        playerEntity.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
        Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> playerEntity), new PacketGetPersistentData(tag));
    }

    @Override
    public boolean canStart() {
        return !getConsumedItems().isEmpty();
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        Item extendTime = ItemsRegistry.MANIPULATION_ESSENCE.get();
        if (didConsumeItem(extendTime) && getConsumedItems().size() == 1 && stack.getItem() instanceof BlockItem)
            return true;

        if (!getConsumedItems().isEmpty() && stack.getItem() instanceof BlockItem)
            return false;

        if (didConsumeItem(stack.getItem()))
            return false;

        if (getConsumedItems().isEmpty() && stack.getItem() instanceof BlockItem)
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
        return "Grants vision of a given block through any other block for a given time. White particles signify you are very close, green is semi-far, and blue particles are blocks very far from you.  To complete the ritual, throw any block of your choice before starting. You may also add a Manipulation Essence to increase the duration to 15 minutes.";
    }
}
