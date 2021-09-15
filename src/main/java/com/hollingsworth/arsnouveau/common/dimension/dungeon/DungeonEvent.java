package com.hollingsworth.arsnouveau.common.dimension.dungeon;

import com.google.common.collect.Sets;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class DungeonEvent {
    public DungeonEvent.State state;
    private final ServerWorld world;
    private int ticks;
    private float totalHealth;
    public Set<LivingEntity> attackers = new HashSet<>();
    int combatOverTicks;
    private String currentTemplate = "";
    private Template.BlockInfo centeredInfo;
    int buildIndex;
    int currentWave;
    List<BlockPos> rewardLoc = new ArrayList<>();
    List<BlockPos> currentBuild = new ArrayList<>();
    boolean setBuild;
    private final ServerBossInfo raidEvent = new ServerBossInfo(new StringTextComponent("Test"), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
    public CombatManager combatManager;
    public int spawnDelayTicks;

    public DungeonEvent(ServerWorld world) {
        this.world = world;
        this.state = State.BUILDING;
        this.raidEvent.setPercent(0.0F);
        combatManager = new CombatManager();
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
        for(BlockPos p : rewardLoc){
            world.setBlock(p, Blocks.CHEST.defaultBlockState(), 2);
        }
        switchState(State.BUILDING);
    }


    public void combat() {
        System.out.println(combatManager.budget);
        for(int i = 0; i < 5; i++) {
            if (spawnDelayTicks == 0 && combatManager.budget > 0) {
                LivingEntity entity = combatManager.getNextEntity(world);
                entity.setPos(0, 105, 0);
                world.addFreshEntity(entity);
                entity.getPersistentData().put("an_dungeon", new CompoundNBT());
                addAttacker(entity, true);
                if(i >= 4){
                    spawnDelayTicks = 100;
                }
            }else{
                break;
            }
        }
        if(spawnDelayTicks > 0)
            spawnDelayTicks--;
        updateBossbar();
        if (getHealthOfLivingRaiders() == 0) {
            combatOverTicks++;
            if (combatOverTicks >= 100) {
                combatOverTicks = 0;
                switchState(State.REWARD);
                currentWave++;
                combatManager.budget = 500 + 50 * currentWave;
                spawnDelayTicks = 0;
            }
        }
    }

    public void build() {
        TemplateManager templatemanager = world.getStructureManager();
        if(!currentBuild.isEmpty() && !setBuild){
            for(int i = 0; i < Math.max(1,currentBuild.size() / 50); i++) {
                if (buildIndex < currentBuild.size()) {
                    world.setBlock(currentBuild.get(buildIndex), Blocks.AIR.defaultBlockState(), 2);
                } else {
                    currentBuild = new ArrayList<>();
                }
                buildIndex++;
            }
            return;
        }
        if(!setBuild) {
            for(BlockPos p : rewardLoc){
                world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
            rewardLoc.clear();
            this.currentTemplate = this.currentTemplate.equals("ars_nouveau:test") ? "ars_nouveau:dirt" : "ars_nouveau:test";
            Template template = templatemanager.getOrCreate(new ResourceLocation(this.currentTemplate));
            if (template.palettes.isEmpty())
                return;
            Template.Palette palette = template.palettes.get(0);
            List<Template.BlockInfo> start = palette.blocks(Blocks.COBBLESTONE);
            this.centeredInfo = start.get(0);
            this.buildIndex = 0;
            setBuild = true;
        }
        Template template = templatemanager.getOrCreate(new ResourceLocation(this.currentTemplate));
        List<Template.BlockInfo> pallette = template.palettes.get(0).blocks();
        rewardLoc.add(new BlockPos(0, 102, 0));
        for(int i = 0; i < Math.max(1,pallette.size() / 100); i++){
            boolean foundNonAir = false;
            while(!foundNonAir){
                if(buildIndex < pallette.size()){
                    Template.BlockInfo blockInfo = pallette.get(buildIndex);
                    if(blockInfo.state.isAir()){
                        buildIndex++;
                        continue;
                    }
                    BlockPos translatedPos = DungeonManager.HOME_POS.offset(blockInfo.pos.getX(), blockInfo.pos.getY(), blockInfo.pos.getZ())
                            .offset(-centeredInfo.pos.getX(), -centeredInfo.pos.getY(), -centeredInfo.pos.getZ());
                    world.setBlock(translatedPos, blockInfo.state, 2);
                    currentBuild.add(translatedPos);
                    buildIndex++;
                    foundNonAir = true;
                }else{
                    this.buildIndex = 0;
                    Collections.reverse(currentBuild);
                    switchState(State.COMBAT);
                    return;
                }
            }
        }
    }

    public void switchState(State state) {
        this.state = state;
        this.ticks = 0;
        setBuild = false;
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


    public DungeonEvent(ServerWorld world, CompoundNBT tag) {
        this.world = world;
        this.state = State.getByName(tag.getString("State"));
        this.ticks = tag.getInt("ticks");
        System.out.println(this.world.dimension().location());
        this.totalHealth = tag.getFloat("health");
        this.currentTemplate = tag.getString("template");
        this.setBuild = tag.getBoolean("setBuild");
        this.buildIndex = tag.getInt("buildIndex");
        int counter = 0;
        while(NBTUtil.hasBlockPos(tag, "current_b" + counter)){
            this.currentBuild.add(NBTUtil.getBlockPos(tag, "current_b" + counter));
            counter++;
        }
        counter = 0;
        while(NBTUtil.hasBlockPos(tag, "reward_loc_" + counter)){
            this.rewardLoc.add(NBTUtil.getBlockPos(tag, "reward_loc_" + counter));
            counter++;
        }
        this.currentWave = tag.getInt("wave");
        this.combatManager = new CombatManager(tag.getCompound("combatManager"));
        this.spawnDelayTicks = tag.getInt("spawnTicks");
    }

    public CompoundNBT save(CompoundNBT tag) {
        tag.putString("State", state.getName());
        tag.putInt("ticks", ticks);
        tag.putFloat("health", totalHealth);
        tag.putInt("numAttacker", attackers.size());
        tag.putString("template", currentTemplate);
        tag.putBoolean("setBuild", setBuild);
        tag.putInt("buildIndex", buildIndex);
        tag.putInt("wave", currentWave);
        for(int i = 0; i < currentBuild.size(); i++){
            NBTUtil.storeBlockPos(tag, "current_b" + i, currentBuild.get(i));
        }
        for(int i = 0; i < rewardLoc.size(); i++){
            NBTUtil.storeBlockPos(tag, "reward_loc_" + i, rewardLoc.get(i));
        }
        tag.put("combatManager", combatManager.serialize());
        tag.putInt("spawnTicks", spawnDelayTicks);
        return tag;
    }


}
