package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.ScryRitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.scrying.IScryer;
import com.hollingsworth.arsnouveau.api.scrying.SingleBlockScryer;
import com.hollingsworth.arsnouveau.api.scrying.TagScryer;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ScryRitualRecipe;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketGetPersistentData;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RitualScrying extends AbstractRitual {

    @Override
    protected void tick() {

        ParticleUtil.spawnFallingSkyEffect(this, tile, rand, getCenterColor().toWrapper());
        if (getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide)
            incrementProgress();

        if (!getWorld().isClientSide && getProgress() >= 15) {
            List<ServerPlayer> players = getWorld().getEntitiesOfClass(ServerPlayer.class, new AABB(getPos()).inflate(5.0));
            if (!players.isEmpty()) {
                ItemStack item = getConsumedItems().stream().filter(i -> i.getItem() != ItemsRegistry.MANIPULATION_ESSENCE.get()).findFirst().orElse(ItemStack.EMPTY);
                int modifier = didConsumeItem(ItemsRegistry.MANIPULATION_ESSENCE.get()) ? 3 : 1;
                for (ServerPlayer playerEntity : players) {
                    Optional<ScryRitualRecipe> hasRecipe = ScryRitualRegistry.getRecipes().stream().filter(recipe -> recipe.matches(item)).findFirst();
                    IScryer scryer = null;
                    if (hasRecipe.isPresent()) scryer = new TagScryer(hasRecipe.get().highlight());
                    else if (item.getItem() instanceof BlockItem blockItem) scryer = new SingleBlockScryer(blockItem.getBlock());
                    if (scryer != null) {
                        RitualScrying.grantScrying(playerEntity, 60 * 20 * 5 * modifier, scryer);
                    }
                }
            }
            setFinished();
        }
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.SCRYING);
    }

    public static void grantScrying(ServerPlayer playerEntity, int ticks, IScryer scryer) {
        playerEntity.addEffect(new MobEffectInstance(ModPotions.SCRYING_EFFECT, ticks));
        CompoundTag tag = playerEntity.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        tag.put("an_scryer", scryer.toTag(new CompoundTag()));
        playerEntity.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
        Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> playerEntity), new PacketGetPersistentData(tag));
    }

    @Override
    public boolean canStart(@Nullable Player player) {
        return !getConsumedItems().isEmpty();
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        boolean hasExtended = didConsumeItem(ItemsRegistry.MANIPULATION_ESSENCE.get());
        if (!hasExtended && stack.getItem() == ItemsRegistry.MANIPULATION_ESSENCE.get()) return true;

        boolean hasConsumedAugment = getConsumedItems().size() - (hasExtended ? 1 : 0) > 0;
        if (!hasConsumedAugment) {
            if (stack.getItem() instanceof BlockItem) {
                return true;
            }
            return ScryRitualRegistry.getRecipes().stream().anyMatch(recipe -> recipe.matches(stack));
        }

        return false;
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
