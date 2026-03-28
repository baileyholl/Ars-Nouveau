package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;

public class LootContext {
    private final LootParams params;
    private final RandomSource random;
    private final HolderGetter.Provider lootDataResolver;
    private final Set<LootContext.VisitedEntry<?>> visitedElements = Sets.newLinkedHashSet();

    LootContext(LootParams p_287722_, RandomSource p_287702_, HolderGetter.Provider p_335850_) {
        this.params = p_287722_;
        this.random = p_287702_;
        this.lootDataResolver = p_335850_;
    }

    public boolean hasParameter(ContextKey<?> p_381161_) {
        return this.params.contextMap().has(p_381161_);
    }

    public <T> T getParameter(ContextKey<T> p_381164_) {
        return this.params.contextMap().getOrThrow(p_381164_);
    }

    public <T> @Nullable T getOptionalParameter(ContextKey<T> p_380975_) {
        return this.params.contextMap().getOptional(p_380975_);
    }

    public void addDynamicDrops(Identifier p_467993_, Consumer<ItemStack> p_78944_) {
        this.params.addDynamicDrops(p_467993_, p_78944_);
    }

    public boolean hasVisitedElement(LootContext.VisitedEntry<?> p_279182_) {
        return this.visitedElements.contains(p_279182_);
    }

    public boolean pushVisitedElement(LootContext.VisitedEntry<?> p_279152_) {
        return this.visitedElements.add(p_279152_);
    }

    public void popVisitedElement(LootContext.VisitedEntry<?> p_279198_) {
        this.visitedElements.remove(p_279198_);
    }

    public HolderGetter.Provider getResolver() {
        return this.lootDataResolver;
    }

    public RandomSource getRandom() {
        return this.random;
    }

    public float getLuck() {
        return this.params.getLuck();
    }

    public ServerLevel getLevel() {
        return this.params.getLevel();
    }

    public static LootContext.VisitedEntry<LootTable> createVisitedEntry(LootTable p_279327_) {
        return new LootContext.VisitedEntry<>(LootDataType.TABLE, p_279327_);
    }

    public static LootContext.VisitedEntry<LootItemCondition> createVisitedEntry(LootItemCondition p_279250_) {
        return new LootContext.VisitedEntry<>(LootDataType.PREDICATE, p_279250_);
    }

    public static LootContext.VisitedEntry<LootItemFunction> createVisitedEntry(LootItemFunction p_279163_) {
        return new LootContext.VisitedEntry<>(LootDataType.MODIFIER, p_279163_);
    }

    public static enum BlockEntityTarget implements StringRepresentable, LootContextArg.SimpleGetter<BlockEntity> {
        BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

        private final String name;
        private final ContextKey<? extends BlockEntity> param;

        private BlockEntityTarget(String p_451328_, ContextKey<? extends BlockEntity> p_451172_) {
            this.name = p_451328_;
            this.param = p_451172_;
        }

        @Override
        public ContextKey<? extends BlockEntity> contextParam() {
            return this.param;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    // Neo: Keep track of the original loot table ID through modifications
    @org.jspecify.annotations.Nullable
    private Identifier queriedLootTableId;

    private LootContext(LootParams p_287722_, RandomSource p_287702_, HolderGetter.Provider p_287619_, Identifier queriedLootTableId) {
        this(p_287722_, p_287702_, p_287619_);
        this.queriedLootTableId = queriedLootTableId;
    }

    public void setQueriedLootTableId(@org.jspecify.annotations.Nullable Identifier queriedLootTableId) {
        if (this.queriedLootTableId == null && queriedLootTableId != null) this.queriedLootTableId = queriedLootTableId;
    }

    public Identifier getQueriedLootTableId() {
        return this.queriedLootTableId == null ? net.neoforged.neoforge.common.loot.LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.queriedLootTableId;
    }

    public static class Builder {
        private final LootParams params;
        private @Nullable RandomSource random;
        @Nullable
        private Identifier queriedLootTableId; // Forge: correctly pass around loot table ID with copy constructor

        public Builder(LootParams p_287628_) {
            this.params = p_287628_;
        }

        public Builder(LootContext context) {
            this.params = context.params;
            this.random = context.random;
            this.queriedLootTableId = context.queriedLootTableId;
        }

        public LootContext.Builder withOptionalRandomSeed(long p_78966_) {
            if (p_78966_ != 0L) {
                this.random = RandomSource.create(p_78966_);
            }

            return this;
        }

        public LootContext.Builder withOptionalRandomSource(RandomSource p_347445_) {
            this.random = p_347445_;
            return this;
        }

        public LootContext.Builder withQueriedLootTableId(Identifier queriedLootTableId) {
            this.queriedLootTableId = queriedLootTableId;
            return this;
        }

        public ServerLevel getLevel() {
            return this.params.getLevel();
        }

        public LootContext create(Optional<Identifier> p_298622_) {
            ServerLevel serverlevel = this.getLevel();
            MinecraftServer minecraftserver = serverlevel.getServer();
            RandomSource randomsource = Optional.ofNullable(this.random)
                .or(() -> p_298622_.map(serverlevel::getRandomSequence))
                .orElseGet(serverlevel::getRandom);
            return new LootContext(this.params, randomsource, minecraftserver.reloadableRegistries().lookup(), queriedLootTableId);
        }
    }

    public static enum EntityTarget implements StringRepresentable, LootContextArg.SimpleGetter<Entity> {
        THIS("this", LootContextParams.THIS_ENTITY),
        ATTACKER("attacker", LootContextParams.ATTACKING_ENTITY),
        DIRECT_ATTACKER("direct_attacker", LootContextParams.DIRECT_ATTACKING_ENTITY),
        ATTACKING_PLAYER("attacking_player", LootContextParams.LAST_DAMAGE_PLAYER),
        TARGET_ENTITY("target_entity", LootContextParams.TARGET_ENTITY),
        INTERACTING_ENTITY("interacting_entity", LootContextParams.INTERACTING_ENTITY);

        public static final StringRepresentable.EnumCodec<LootContext.EntityTarget> CODEC = StringRepresentable.fromEnum(LootContext.EntityTarget::values);
        private final String name;
        private final ContextKey<? extends Entity> param;

        private EntityTarget(String p_79001_, ContextKey<? extends Entity> p_380963_) {
            this.name = p_79001_;
            this.param = p_380963_;
        }

        @Override
        public ContextKey<? extends Entity> contextParam() {
            return this.param;
        }

        // Forge: This method is patched in to expose the same name used in getByName so that ContextNbtProvider#forEntity serializes it properly
        // TODO 1.21.11: To be consistent with getParam() being renamed to contextParam(), consider renaming to name()
        public String getName() {
            return this.name;
        }

        public static LootContext.EntityTarget getByName(String p_79007_) {
            LootContext.EntityTarget lootcontext$entitytarget = CODEC.byName(p_79007_);
            if (lootcontext$entitytarget != null) {
                return lootcontext$entitytarget;
            } else {
                throw new IllegalArgumentException("Invalid entity target " + p_79007_);
            }
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static enum ItemStackTarget implements StringRepresentable, LootContextArg.SimpleGetter<ItemStack> {
        TOOL("tool", LootContextParams.TOOL);

        private final String name;
        private final ContextKey<? extends ItemStack> param;

        private ItemStackTarget(String p_451240_, ContextKey<? extends ItemStack> p_451491_) {
            this.name = p_451240_;
            this.param = p_451491_;
        }

        @Override
        public ContextKey<? extends ItemStack> contextParam() {
            return this.param;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public record VisitedEntry<T>(LootDataType<T> type, T value) {
    }
}
