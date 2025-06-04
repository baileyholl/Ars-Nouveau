package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.entity.WildenChimera;
import com.hollingsworth.arsnouveau.common.entity.WildenGuardian;
import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nonnull;

import static com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider.WILDEN_DROP_TAG;

public class RitualWildenSummoning extends AbstractRitual {
    @Override
    protected void tick() {
        WildenChimera.spawnPhaseParticles(getPos().above(), getWorld(), 1);

        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (getWorld().getGameTime() % 20 != 0) {
            return;
        }

        if (isBossSpawn()) {
            if (getProgress() < BOSS_SPAWN_DELAY.get()) {
                incrementProgress();
            }
            handleBossSpawn(serverLevel);
        } else {
            handleWildenSpawn(serverLevel);
        }
    }

    private void handleWildenSpawn(ServerLevel serverLevel) {
        if (serverLevel.getGameTime() % (20 * WILDEN_SPAWN_INTERVAL.get()) != 0) {
            return;
        }
        
        BlockPos spawnPos = getRandomSpawnPos();
        Mob wilden = createRandomWilden(serverLevel);
        summon(wilden, spawnPos);
        incrementProgress();
        
        if (getProgress() >= WILDEN_SPAWN_COUNT.get()) {
            setFinished();
        }
    }

    private void handleBossSpawn(ServerLevel serverLevel) {
        if (getProgress() >= BOSS_SPAWN_DELAY.get()) {
            WildenChimera chimera = new WildenChimera(serverLevel);
            summon(chimera, getPos().above());
            
            // Destroy blocks around the ritual for boss spawn
            FakePlayer fakePlayer = ANFakePlayer.getPlayer(serverLevel, this.playerUUID);
            for (BlockPos pos : BlockPos.betweenClosed(
                    getPos().east(SPAWN_RADIUS.get()).north(SPAWN_RADIUS.get()).above(), 
                    getPos().west(SPAWN_RADIUS.get()).south(SPAWN_RADIUS.get()).above(SPAWN_RADIUS.get()))) {
                
                if (!EventHooks.canEntityGrief(serverLevel, chimera)) {
                    continue;
                }
                if (serverLevel.getBlockState(pos).getDestroySpeed(serverLevel, pos) < 0) continue;
                if (SpellUtil.isCorrectHarvestLevel(4, serverLevel.getBlockState(pos))) {
                    BlockUtil.destroyBlockSafelyWithoutSound(serverLevel, pos, true, fakePlayer);
                }
            }
            setFinished();
        }
    }

    private BlockPos getRandomSpawnPos() {
        BlockPos center = getPos().above();
        int radius = SPAWN_RADIUS.get();
        int x = center.getX() + rand.nextInt(radius * 2) - radius;
        int z = center.getZ() + rand.nextInt(radius * 2) - radius;
        return new BlockPos(x, center.getY(), z);
    }

    @Nonnull
    private Mob createRandomWilden(ServerLevel level) {
        int wildenType = rand.nextInt(3);
        return switch (wildenType) {
            case 0 -> new WildenStalker(level);
            case 1 -> new WildenGuardian(level);
            default -> new WildenHunter(level);
        };
    }

    public boolean isBossSpawn() {
        return didConsumeItem(ItemsRegistry.WILDEN_HORN) && 
               didConsumeItem(ItemsRegistry.WILDEN_WING) && 
               didConsumeItem(ItemsRegistry.WILDEN_SPIKE);
    }

    public void summon(Mob mob, BlockPos pos) {
        mob.setPos(pos.getX(), pos.getY(), pos.getZ());
        mob.level.addFreshEntity(mob);
    }

    @Override
    public String getLangName() {
        return "Summon Wilden";
    }

    @Override
    public String getLangDescription() {
        return "Without augments, this ritual will summon a random variety of Wilden monsters for a short duration. When augmented with a Wilden Spike, Wilden Horn, and a Wilden Wing, this ritual will summon the Wilden Chimera, a challenging and destructive monster. Note: If summoning the chimera, this ritual will destroy blocks around the brazier.";
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.is(WILDEN_DROP_TAG);
    }

    public ModConfigSpec.IntValue WILDEN_SPAWN_COUNT;
    public ModConfigSpec.LongValue WILDEN_SPAWN_INTERVAL;
    public ModConfigSpec.IntValue BOSS_SPAWN_DELAY;
    public ModConfigSpec.IntValue SPAWN_RADIUS;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        WILDEN_SPAWN_COUNT = builder
                .comment("Number of Wilden to spawn before the ritual ends")
                .defineInRange("spawn_count", 5, 1, 100);
                
        WILDEN_SPAWN_INTERVAL = builder
                .comment("Interval in seconds between Wilden spawns")
                .defineInRange("spawn_interval", 3L, 1, 60);
                
        BOSS_SPAWN_DELAY = builder
                .comment("Delay in ticks before the boss spawns")
                .defineInRange("boss_delay", 8, 1, 200);
                
        SPAWN_RADIUS = builder
                .comment("Radius around the ritual where Wilden can spawn")
                .defineInRange("spawn_radius", 5, 1, 20);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix(RitualLib.WILDEN_SUMMON);
    }
}
