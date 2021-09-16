package com.hollingsworth.arsnouveau.common.dimension.dungeon;

import com.google.common.collect.Sets;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DungeonEvent {
    public DungeonEvent.State state;
    private final ServerWorld world;
    private int ticks;
    private float totalHealth;
    public Set<LivingEntity> attackers = new HashSet<>();
    int combatOverTicks;
    private final ServerBossInfo raidEvent = new ServerBossInfo(new StringTextComponent("Test"), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
    public SpawnManager spawnManager;
    public BuildManager buildManager;


    public DungeonEvent(ServerWorld world) {
        this.world = world;
        this.state = State.BUILDING;
        this.raidEvent.setPercent(0.0F);
        spawnManager = new SpawnManager(world, this);
        buildManager = new BuildManager(world, this);
    }


    public void tick() {
        if (!world.hasChunkAt(DungeonManager.HOME_POS))
            return;

        if (state == State.BUILDING) {
            build();
        }

        if (state == State.COMBAT) {
            combat();
        }
        if (state == State.REWARD) {
            reward();
        }
        this.raidEvent.setVisible(true);
        if (world.getGameTime() % 20 == 0) {
            updatePlayers();
        }
        this.setDirty();
        this.raidEvent.setName(new StringTextComponent(getHealthOfLivingRaiders() + " " + totalHealth + " " + this.state.getName()));
        ticks++;
    }

    public void setDirty() {
        DungeonManager.from(world).setDirty(true);
    }

    public void reward() {
        for(BlockPos p : buildManager.rewardLoc){
            world.setBlock(p, Blocks.CHEST.defaultBlockState(), 2);
        }
        switchState(State.BUILDING);
    }


    public void combat() {
        spawnManager.tick();
        updateBossbar();
        if (getHealthOfLivingRaiders() == 0) {
            combatOverTicks++;
            if (combatOverTicks >= 100) {
                combatOverTicks = 0;
                switchState(State.REWARD);
                spawnManager.waveComplete();
            }
        }
    }

    public void build() {
        // Let buildManager switch the state when building is finished.
        buildManager.tick();
    }

    public void switchState(State state) {
        this.state = state;
        this.ticks = 0;
    }

    private void updatePlayers() {
        Set<ServerPlayerEntity> set = Sets.newHashSet(this.raidEvent.getPlayers());
        List<ServerPlayerEntity> list = world.getPlayers(p -> true);

        for (ServerPlayerEntity serverplayerentity : list) {
            if (!set.contains(serverplayerentity)) {
                this.raidEvent.addPlayer(serverplayerentity);
            }
        }

        for (ServerPlayerEntity serverplayerentity1 : set) {
            if (!list.contains(serverplayerentity1)) {
                this.raidEvent.removePlayer(serverplayerentity1);
            }
        }
    }

    public void updateBossbar() {
        if (totalHealth == 0)
            return;
        this.raidEvent.setPercent(MathHelper.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0F, 1.0F));
    }

    public float getHealthOfLivingRaiders() {
        float f = 0.0F;
        Set<LivingEntity> stale = new HashSet<>();
        for (LivingEntity attacker : attackers) {

            if (attacker.removed || attacker.isDeadOrDying()) {
                stale.add(attacker);
            }
        }
        this.attackers.removeAll(stale);
        for (LivingEntity attacker : attackers) {
            f += attacker.getHealth();
        }
        return f;
    }

    public boolean addAttacker(LivingEntity entity, boolean addHealth) {
        if (entity == null)
            return false;
        boolean didAdd = this.attackers.add(entity);
        if (addHealth && didAdd)
            this.totalHealth += entity.getHealth();
        return didAdd;
    }

    public DungeonEvent(ServerWorld world, CompoundNBT tag) {
        this.world = world;
        this.state = State.getByName(tag.getString("State"));
        this.ticks = tag.getInt("ticks");
        this.totalHealth = tag.getFloat("health");
        this.spawnManager = new SpawnManager(tag.getCompound("spawnManager"), world, this);
        this.buildManager = new BuildManager(tag.getCompound("buildManager"), world, this);
    }

    public CompoundNBT save(CompoundNBT tag) {
        tag.putString("State", state.getName());
        tag.putInt("ticks", ticks);
        tag.putFloat("health", totalHealth);
        tag.putInt("numAttacker", attackers.size());
        tag.put("spawnManager", spawnManager.serialize());
        tag.put("buildManager", buildManager.serialize());
        return tag;
    }

    public enum State {
        BUILDING,
        SPAWNING,
        COMBAT,
        REWARD;

        private static final State[] VALUES = values();

        private static State getByName(String p_221275_0_) {
            for (State state : VALUES) {
                if (p_221275_0_.equalsIgnoreCase(state.name())) {
                    return state;
                }
            }

            return BUILDING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
