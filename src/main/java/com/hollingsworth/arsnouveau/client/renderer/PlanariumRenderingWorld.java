package com.hollingsworth.arsnouveau.client.renderer;

import com.hollingsworth.arsnouveau.client.renderer.world.CulledStatePos;
import com.hollingsworth.arsnouveau.common.world.dimension.PlanariumChunkGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class PlanariumRenderingWorld extends Level implements LevelAccessor {

    ClientChunkCache clientChunkCache;

    public PlanariumRenderingWorld(Level rWorld, List<CulledStatePos> coordinates, BlockPos lookingAt) {
        this(rWorld);
        this.lookingAt = lookingAt;
        for (CulledStatePos statePos : coordinates) {
            this.setBlock(statePos.pos, statePos.state, 0);
        }
        for (CulledStatePos statePos : coordinates) {
            try {
                BlockState adjustedState = Block.updateFromNeighbourShapes(statePos.state, this, statePos.pos);
                this.setBlock(statePos.pos, adjustedState, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public PlanariumRenderingWorld(Level world) {
        super((WritableLevelData) world.getLevelData(), world.dimension(), world.registryAccess(), world.dimensionTypeRegistration(),
                world::getProfiler, world.isClientSide, world.isDebug(), 0, 0);
        this.realWorld = world;
        this.clientChunkCache = new ClientChunkCache(Minecraft.getInstance().level, 0);
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos blockPos) {
        return 15;
    }

    public final HashMap<BlockPos, BlockState> positions = new HashMap<>();
    private Level realWorld;
    private BlockPos lookingAt;


    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        BlockState blockState = getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = ((EntityBlock) blockState.getBlock()).newBlockEntity(pos.offset(lookingAt), blockState);
            if (blockEntity == null) {
                return null;
            }
            blockEntity.setLevel(this.realWorld);
            return blockEntity;
        }
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        // Check if our template has the pos, otherwise return air if we are inside the jar, void otherwise.
        boolean contains = positions.containsKey(pos);
        if (contains) {
            return positions.get(pos);
        } else {
            if (!PlanariumChunkGenerator.innerBox.contains(pos.getX(), pos.getY(), pos.getZ())) {
                return Blocks.VOID_AIR.defaultBlockState();
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
    }

    @Override
    public void scheduleTick(BlockPos p_186461_, Block p_186462_, int p_186463_) {
        //noOp
    }

    @Override
    public boolean setBlock(BlockPos p_46944_, BlockState p_46945_, int p_46946_) {
        return this.setBlock(p_46944_, p_46945_, p_46946_, 512);
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int p_46949_, int p_46950_) {
        positions.put(pos, state);
        return true;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.positions.containsKey(pos) ? this.positions.get(pos).getFluidState() : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getHeight() {
        return 31;
    }

    @Override
    public RegistryAccess registryAccess() {
        return null;
    }

    @Override
    public PotionBrewing potionBrewing() {
        return null;
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ChunkAccess getChunk(int pX, int pZ, ChunkStatus pChunkStatus, boolean pRequireChunk) {
        return null;
    }

    @Override
    public int getHeight(Heightmap.Types p_46827_, int p_46828_, int p_46829_) {
        return 0;
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return realWorld.getBiomeManager();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return null;
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int p_204159_, int p_204160_, int p_204161_) {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return true;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public DimensionType dimensionType() {
        return realWorld.dimensionType();
    }

    @Override
    public int getMinBuildHeight() {
        return realWorld.getMinBuildHeight();
    }

    @Override
    public int getRawBrightness(BlockPos blockPos, int amount) {
        return realWorld.getRawBrightness(blockPos.offset(lookingAt), amount);
    }

    @Override
    public boolean canSeeSky(BlockPos blockPos) {
        return realWorld.canSeeSky(blockPos.offset(lookingAt));
    }

    @Override
    public long nextSubTickCount() {
        return 0;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return BlackholeTickAccess.emptyLevelList();
    }

    @Override
    public LevelData getLevelData() {
        return this.realWorld.getLevelData();
    }

    @Override
    public TickRateManager tickRateManager() {
        return null;
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_46800_) {
        return null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public MinecraftServer getServer() {
        return null;
    }

    @Override
    public ChunkSource getChunkSource() {
        return this.clientChunkCache;
    }

    @Override
    public RandomSource getRandom() {
        return Minecraft.getInstance().level.random;
    }

    @Override
    public void playSound(@org.jetbrains.annotations.Nullable Player p_46775_, BlockPos p_46776_, SoundEvent p_46777_, SoundSource p_46778_, float p_46779_, float p_46780_) {

    }

    @Override
    public void addParticle(ParticleOptions p_46783_, double p_46784_, double p_46785_, double p_46786_, double p_46787_, double p_46788_, double p_46789_) {

    }

    @Override
    public void levelEvent(@org.jetbrains.annotations.Nullable Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {

    }

    @Override
    public void gameEvent(Holder<GameEvent> holder, Vec3 vec3, GameEvent.Context context) {

    }

    @Override
    public float getShade(Direction pDirection, boolean pShade) {
        ClientLevel clientLevel = (ClientLevel) realWorld;
        boolean flag = clientLevel.effects().constantAmbientLight();
        if (!pShade) {
            return flag ? 0.9F : 1.0F;
        } else {
            switch (pDirection) {
                case DOWN:
                    return flag ? 0.9F : 0.5F;
                case UP:
                    return flag ? 0.9F : 1.0F;
                case NORTH:
                case SOUTH:
                    return 0.8F;
                case WEST:
                case EAST:
                    return 0.6F;
                default:
                    return 1.0F;
            }
        }
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return Minecraft.getInstance().level.getLightEngine();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public List<Entity> getEntities(@org.jetbrains.annotations.Nullable Entity p_45936_, AABB p_45937_, Predicate<? super Entity> p_45938_) {
        return null;
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> p_151464_, AABB p_151465_, Predicate<? super T> p_151466_) {
        return null;
    }

    @Override
    public List<? extends Player> players() {
        return List.of();
    }

    @Override
    public boolean isStateAtPosition(BlockPos p_46938_, Predicate<BlockState> p_46939_) {
        return p_46939_.test(getBlockState(p_46938_));
    }

    @Override
    public boolean isFluidAtPosition(BlockPos p_151584_, Predicate<FluidState> p_151585_) {
        return false;
    }

    @Override
    public boolean removeBlock(BlockPos p_46951_, boolean p_46952_) {
        return false;
    }

    @Override
    public boolean destroyBlock(BlockPos p_46957_, boolean p_46958_, @org.jetbrains.annotations.Nullable Entity p_46959_, int p_46960_) {
        return false;
    }

    @Override
    public int getMaxLocalRawBrightness(BlockPos pos) {
        return 15;
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
    }

    @Override
    public void playSeededSound(Player p_220363_, double p_220364_, double p_220365_, double p_220366_,
                                SoundEvent p_220367_, SoundSource p_220368_, float p_220369_, float p_220370_, long p_220371_) {
    }

    @Override
    public void playSeededSound(Player pPlayer, double pX, double pY, double pZ, Holder<SoundEvent> pSound,
                                SoundSource pSource, float pVolume, float pPitch, long pSeed) {
    }

    @Override
    public void playSeededSound(Player pPlayer, Entity pEntity, Holder<SoundEvent> pSound, SoundSource pCategory,
                                float pVolume, float pPitch, long pSeed) {
    }

    @Override
    public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent soundIn,
                          SoundSource category, float volume, float pitch) {
    }

    @Override
    public void playSound(@Nullable Player p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_,
                          SoundSource p_217384_4_, float p_217384_5_, float p_217384_6_) {
    }

    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public MapItemSavedData getMapData(MapId mapId) {
        return null;
    }

    @Override
    public boolean addFreshEntity(Entity entityIn) {
        return false;
    }

    @Override
    public void setMapData(MapId mapId, MapItemSavedData mapItemSavedData) {

    }

    @Override
    public MapId getFreeMapId() {
        return realWorld.getFreeMapId();
    }


    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
    }

    @Override
    public Scoreboard getScoreboard() {
        return realWorld.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return realWorld.getRecipeManager();
    }

    @Override
    public void updateNeighbourForOutputSignal(BlockPos p_175666_1_, Block p_175666_2_) {
    }

    @Override
    public void gameEvent(@org.jetbrains.annotations.Nullable Entity pEntity, Holder<GameEvent> pGameEvent, Vec3 pPos) {

    }

    @Override
    public String gatherChunkSourceStats() {
        return realWorld.gatherChunkSourceStats();
    }


    // Intentionally copied from LevelHeightAccessor. Workaround for issues caused
    // when other mods (such as Lithium)
    // override the vanilla implementations in ways which cause WrappedWorlds to
    // return incorrect, default height info.
    // WrappedWorld subclasses should implement their own getMinBuildHeight and
    // getHeight overrides where they deviate
    // from the defaults for their dimension.

    @Override
    public int getMaxBuildHeight() {
        return this.getMinBuildHeight() + this.getHeight();
    }

    @Override
    public int getSectionsCount() {
        return this.getMaxSection() - this.getMinSection();
    }

    @Override
    public int getMinSection() {
        return SectionPos.blockToSectionCoord(this.getMinBuildHeight());
    }

    @Override
    public int getMaxSection() {
        return SectionPos.blockToSectionCoord(this.getMaxBuildHeight() - 1) + 1;
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos) {
        return this.isOutsideBuildHeight(pos.getY());
    }

    @Override
    public boolean isOutsideBuildHeight(int y) {
        return y < this.getMinBuildHeight() || y >= this.getMaxBuildHeight();
    }

    @Override
    public int getSectionIndex(int y) {
        return this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(y));
    }

    @Override
    public int getSectionIndexFromSectionY(int sectionY) {
        return sectionY - this.getMinSection();
    }

    @Override
    public int getSectionYFromSectionIndex(int sectionIndex) {
        return sectionIndex + this.getMinSection();
    }

    // Invisible overrides for neoforge compatibility
    public void setDayTimeFraction(float dayTimePerTick) {

    }

    public void setDayTimePerTick(float dayTimePerTick) {

    }

    public float getDayTimePerTick() {
        return 0;
    }

    public float getDayTimeFraction() {
        return 0;
    }

}
