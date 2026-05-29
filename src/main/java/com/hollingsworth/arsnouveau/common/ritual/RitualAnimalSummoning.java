package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SummonRitualRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SummonRitualRecipe.WeightedMobType;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Collectors;

public class RitualAnimalSummoning extends AbstractRitual {
    private final MobCategory category = MobCategory.CREATURE;
    private WeightedRandomList<WeightedMobType> mobs;

    private Optional<RecipeHolder<SummonRitualRecipe>> recipe;

    private Optional<RecipeHolder<SummonRitualRecipe>> getRecipe() {
        return getWorld().getRecipeManager().getAllRecipesFor(RecipeRegistry.SUMMON_RITUAL_TYPE.get()).stream().filter(r -> r.value().matches(getConsumedItems())).findFirst();
    }

    private WeightedRandomList<WeightedMobType> getMobs(Level world) {
        if (recipe.isPresent()) {
            SummonRitualRecipe summonRitualRecipe = recipe.get().value();
            return summonRitualRecipe.mobs;
        }
        return WeightedRandomList.create(world.getBiome(getPos()).value().getMobSettings().getMobs(category).unwrap().stream().filter(mob -> !mob.type.is(EntityTags.ANIMAL_SUMMON_BLACKLIST)).map(WeightedMobType::fromSpawnerData).collect(Collectors.toList()));
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        if (tile == null || getWorld() == null || getPos() == null) return;
        if (recipe == null) recipe = getRecipe();
        mobs = getMobs(getWorld());
    }

    @Override
    protected void tick() {

        Level world = getWorld();
        if (world == null || getPos() == null) return;

        if (recipe == null) recipe = getRecipe();

        //update the mob list if not initialized
        if (mobs == null) mobs = getMobs(world);

        //every 60 ticks, spawn a mob. 1 tick = 1/20th of a second
        if (world.getGameTime() % 60 == 0 && !world.isClientSide) {

            //randomize the spawn position
            BlockPos summonPos = getPos().above().east(rand.nextInt(3) - rand.nextInt(6)).north(rand.nextInt(3) - rand.nextInt(6));

            Optional<WeightedMobType> opt = mobs.getRandom(rand);
            opt.ifPresent(entry -> {
                Entity mob = BuiltInRegistries.ENTITY_TYPE.get(entry.mob()).create(world);
                if (mob == null) return;
                summon(mob, summonPos);
                incrementProgress();
            });

            recipe.ifPresentOrElse(recipe -> {
                if (getProgress() >= recipe.value().count) {
                    setFinished();
                }
            }, () -> {
                if (getProgress() >= 5) {
                    setFinished();
                }
            });
        }
    }

    public void summon(Entity mob, BlockPos pos) {
        mob.setPos(pos.getX(), pos.getY(), pos.getZ());
        mob.level().addFreshEntity(mob);
        if (mob.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                serverLevel.sendParticles(player, ParticleTypes.END_ROD, false, pos.getX(), pos.getY() + 0.1, pos.getZ(), 10, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }

    @Override
    public boolean canStart(@Nullable Player player) {
        if (getConsumedItems().size() == 0) {
            return true;
        }
        if (recipe == null) recipe = getRecipe();
        return recipe.isPresent();
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return getWorld().getRecipeManager().getAllRecipesFor(RecipeRegistry.SUMMON_RITUAL_TYPE.get()).stream().anyMatch(r -> r.value().augment.test(stack));
    }

    @Override
    public String getLangName() {
        return "Summon Animals";
    }

    @Override
    public String getLangDescription() {
        return "Summon a random variety of wild animals native to the biome it's in.";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix(RitualLib.ANIMAL_SUMMON);
    }
}
