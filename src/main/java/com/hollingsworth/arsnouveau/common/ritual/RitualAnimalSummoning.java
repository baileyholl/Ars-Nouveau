package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.Optional;
import java.util.stream.Collectors;

public class RitualAnimalSummoning extends AbstractRitual {
    private MobCategory category = MobCategory.CREATURE;
    private WeightedRandomList<MobSpawnSettings.SpawnerData> mobs;

    private WeightedRandomList<MobSpawnSettings.SpawnerData> getMobs() {
        return WeightedRandomList.create(getWorld().getBiome(getPos()).get().getMobSettings().getMobs(category).unwrap().stream().filter(mob -> mob.type.is(EntityTags.ANIMAL_SUMMON_BLACKLIST)).collect(Collectors.toList()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (tile == null || getWorld() == null || getPos() == null)
            return;
        mobs = getMobs();
    }

    @Override
    protected void tick() {
        if (mobs == null) mobs = getMobs();

        if (getWorld().getGameTime() % 20 == 0)
            incrementProgress();

        if (getWorld().getGameTime() % 60 == 0 && !getWorld().isClientSide) {
            Optional<MobSpawnSettings.SpawnerData> opt = mobs.getRandom(rand);
            opt.ifPresent(animal -> {
                BlockPos summonPos = getPos().above().east(rand.nextInt(3) - rand.nextInt(6)).north(rand.nextInt(3) - rand.nextInt(6));
                Mob mob = (Mob) animal.type.create(getWorld());
                if (mob == null) return;
                summon(mob, summonPos);
            });
            if (getProgress() >= 15) {
                setFinished();
            }
        }
    }

    public void summon(Mob mob, BlockPos pos) {
        mob.setPos(pos.getX(), pos.getY(), pos.getZ());
        mob.level.addFreshEntity(mob);
        if (mob.level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                serverLevel.sendParticles(player, ParticleTypes.END_ROD, false, pos.getX(), pos.getY() + 0.1, pos.getZ(), 10, 0.1, 0.1, 0.1, 0.05);
            }
        }
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
