package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.recipe.SummonRitualRecipe;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RitualAnimalSummoning extends AbstractRitual {
    private final MobCategory category = MobCategory.CREATURE;
    private WeightedRandomList<MobSpawnSettings.SpawnerData> mobs;
    private EntityType<? extends Entity> entityType;

    private WeightedRandomList<MobSpawnSettings.SpawnerData> getMobs(Level world) {
        return WeightedRandomList.create(world.getBiome(getPos()).get().getMobSettings().getMobs(category).unwrap().stream().filter(mob -> !mob.type.is(EntityTags.ANIMAL_SUMMON_BLACKLIST)).collect(Collectors.toList()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (tile == null || getWorld() == null || getPos() == null) return;
        mobs = getMobs(getWorld());
    }

    @Override
    protected void tick() {

        Level world = getWorld();
        if (world == null || getPos() == null) return;

        //update the mob list if not initialized
        //fetch from the biome
        if (mobs == null) mobs = getMobs(world);
        //or fetch from the custom recipe type
        if (!getConsumedItems().isEmpty() && entityType == null) {
            getEntityType(world);
        }

        //every 20 ticks, increment progress
        if (world.getGameTime() % 20 == 0) incrementProgress();

        //every 60 ticks, spawn a mob. 1 tick = 1/20th of a second
        if (world.getGameTime() % 60 == 0 && !world.isClientSide) {

            //randomize the spawn position
            BlockPos summonPos = getPos().above().east(rand.nextInt(3) - rand.nextInt(6)).north(rand.nextInt(3) - rand.nextInt(6));

            //if we have a custom entity type, try to summon it
            if (entityType != null) {
                Entity mob = entityType.create(world);
                if (mob == null) return;
                summon(mob, summonPos);
            }
            //otherwise summon a random mob from the biome
            else {
                Optional<MobSpawnSettings.SpawnerData> opt = mobs.getRandom(rand);
                opt.map(animal -> animal.type.create(world)).ifPresent(mob -> summon(mob, summonPos));
            }
            //if we've summoned 5 mobs, finish the ritual
            if (getProgress() >= 15) {
                setFinished();
            }
        }
    }

    private void getEntityType(Level level) {
        Optional<SummonRitualRecipe> recipe = level.getRecipeManager().getAllRecipesFor(RecipeRegistry.SUMMON_RITUAL_TYPE.get()).stream().filter(r -> r.matches(getConsumedItems().get(0))).findFirst();
        recipe.ifPresent(summonRitualRecipe -> {
            ResourceLocation registryName = summonRitualRecipe.mob;
            entityType = ForgeRegistries.ENTITY_TYPES.getValue(registryName);
        });
    }

    public void summon(Entity mob, BlockPos pos) {
        mob.setPos(pos.getX(), pos.getY(), pos.getZ());
        mob.level.addFreshEntity(mob);
        if (mob.level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                serverLevel.sendParticles(player, ParticleTypes.END_ROD, false, pos.getX(), pos.getY() + 0.1, pos.getZ(), 10, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        if (this.getWorld() != null) {
            List<SummonRitualRecipe> summons = this.getWorld().getRecipeManager().getAllRecipesFor(RecipeRegistry.SUMMON_RITUAL_TYPE.get());
            if (summons.stream().anyMatch(recipe -> recipe.matches(stack))) {
                return true;
            }
        }
        return super.canConsumeItem(stack);
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
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.ANIMAL_SUMMON);
    }
}
