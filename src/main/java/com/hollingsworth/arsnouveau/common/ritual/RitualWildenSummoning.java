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
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider.WILDEN_DROP_TAG;

public class RitualWildenSummoning extends AbstractRitual {

    private UUID uuid;

    @Override
    public void onStart(@Nullable Player player) {
        if (player != null) this.uuid = player.getUUID();
    }

    @Override
    protected void tick() {
        WildenChimera.spawnPhaseParticles(getPos().above(), getWorld(), 1);
        if (getWorld().getGameTime() % 20 == 0)
            incrementProgress();
        if (getWorld().getGameTime() % 60 == 0 && (getWorld() instanceof  ServerLevel serverLevel)) {
            FakePlayer fakePlayer = ANFakePlayer.getPlayer(serverLevel, uuid);
            if (!isBossSpawn()) {
                int wild = rand.nextInt(3);
                BlockPos summonPos = getPos().above().east(rand.nextInt(3) - rand.nextInt(6)).north(rand.nextInt(3) - rand.nextInt(6));
                Mob mobEntity = switch (wild) {
                    case 0 -> new WildenStalker(serverLevel);
                    case 1 -> new WildenGuardian(serverLevel);
                    default -> new WildenHunter(serverLevel);
                };
                summon(mobEntity, summonPos);
                if (getProgress() >= 15) {
                    setFinished();
                }
            } else {
                if (getProgress() >= 8) {
                    WildenChimera chimera = new WildenChimera(serverLevel);
                    summon(chimera, getPos().above());
                    for(BlockPos b : BlockPos.betweenClosed(getPos().east(5).north(5).above(), getPos().west(5).south(5).above(5))){
                        if (!net.neoforged.neoforge.event.EventHooks.canEntityGrief(serverLevel, chimera)) {
                            continue;
                        }
                        if (serverLevel.getBlockState(b).getDestroySpeed(serverLevel, b) < 0) continue;
                        if (SpellUtil.isCorrectHarvestLevel(4, serverLevel.getBlockState(b))) {
                            BlockUtil.destroyBlockSafelyWithoutSound(serverLevel, b, true, fakePlayer);
                        }
                    }
                    setFinished();
                }
            }
        }
    }

    public boolean isBossSpawn() {
        return didConsumeItem(ItemsRegistry.WILDEN_HORN) && didConsumeItem(ItemsRegistry.WILDEN_WING) && didConsumeItem(ItemsRegistry.WILDEN_SPIKE);
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

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.WILDEN_SUMMON);
    }
}
