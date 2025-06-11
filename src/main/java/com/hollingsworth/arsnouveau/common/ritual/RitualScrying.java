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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;


import java.util.List;
import java.util.Optional;

public class RitualScrying extends AbstractRitual {
    public ModConfigSpec.IntValue BASE_DURATION;
    public ModConfigSpec.DoubleValue MANIPULATION_MODIFIER;
    public ModConfigSpec.DoubleValue EFFECT_RANGE;

    @Override
    protected void tick() {
        ParticleUtil.spawnFallingSkyEffect(this, tile, rand, getCenterColor().toWrapper());
        if (getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide)
            incrementProgress();

        if (!getWorld().isClientSide && getProgress() >= 15) {
            double range = getEffectRange();
            double modifier = getManipulationModifier();
            int baseDuration = getBaseDurationTicks();
            
            List<ServerPlayer> players = getWorld().getEntitiesOfClass(ServerPlayer.class, new AABB(getPos()).inflate(range));
            if (!players.isEmpty()) {
                ItemStack item = getConsumedItems().stream()
                        .filter(i -> i.getItem() != ItemsRegistry.MANIPULATION_ESSENCE.get())
                        .findFirst()
                        .orElse(ItemStack.EMPTY);
                        
                for (ServerPlayer playerEntity : players) {
                    Optional<ScryRitualRecipe> hasRecipe = ScryRitualRegistry.getRecipes().stream().filter(recipe -> recipe.matches(new SingleRecipeInput(item), getWorld())).findFirst();
                    IScryer scryer = null;
                    if (hasRecipe.isPresent()) scryer = new TagScryer(hasRecipe.get().highlight());
                    else if (item.getItem() instanceof BlockItem blockItem) scryer = new SingleBlockScryer(blockItem.getBlock());
                    if (scryer != null) {
                        RitualScrying.grantScrying(playerEntity, (int)(baseDuration * modifier), scryer);
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
        Networking.sendToPlayerClient(new PacketGetPersistentData(tag), playerEntity);
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
            return ScryRitualRegistry.getRecipes().stream().anyMatch(recipe -> recipe.matches(new SingleRecipeInput(stack), getWorld()));
        }

        return false;
    }

    private int getBaseDurationTicks() {
        return BASE_DURATION.get() * 20; // Convert from seconds to ticks
    }
    
    private double getManipulationModifier() {
        if (!didConsumeItem(ItemsRegistry.MANIPULATION_ESSENCE.get())) {
            return 1.0;
        }
        return MANIPULATION_MODIFIER.get();
    }
    
    private double getEffectRange() {
        return EFFECT_RANGE.get();
    }
    
    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        BASE_DURATION = builder
                .comment("Base duration of the scrying effect in seconds")
                .defineInRange("base_duration", 300, 1, 1800); // 5 minutes default, max 30 minutes
        MANIPULATION_MODIFIER = builder
                .comment("Multiplier for duration when using a Manipulation Essence")
                .defineInRange("manipulation_modifier", 3.0, 1.0, 10.0);
        EFFECT_RANGE = builder
                .comment("Range in blocks around the ritual where players will receive the scrying effect")
                .defineInRange("effect_range", 5.0, 1.0, 20.0);
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
