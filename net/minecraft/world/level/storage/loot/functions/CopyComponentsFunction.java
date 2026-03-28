package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextArg;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyComponentsFunction extends LootItemConditionalFunction {
    private static final Codec<LootContextArg<DataComponentGetter>> GETTER_CODEC = LootContextArg.createArgCodec(
        p_460603_ -> p_460603_.anyEntity(CopyComponentsFunction.DirectSource::new)
            .anyBlockEntity(CopyComponentsFunction.BlockEntitySource::new)
            .anyItemStack(CopyComponentsFunction.DirectSource::new)
    );
    public static final MapCodec<CopyComponentsFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_460601_ -> commonFields(p_460601_)
            .and(
                p_460601_.group(
                    GETTER_CODEC.fieldOf("source").forGetter(p_460602_ -> p_460602_.source),
                    DataComponentType.CODEC.listOf().optionalFieldOf("include").forGetter(p_338132_ -> p_338132_.include),
                    DataComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter(p_338126_ -> p_338126_.exclude)
                )
            )
            .apply(p_460601_, CopyComponentsFunction::new)
    );
    private final LootContextArg<DataComponentGetter> source;
    private final Optional<List<DataComponentType<?>>> include;
    private final Optional<List<DataComponentType<?>>> exclude;
    private final Predicate<DataComponentType<?>> bakedPredicate;

    CopyComponentsFunction(
        List<LootItemCondition> p_330806_,
        LootContextArg<DataComponentGetter> p_461204_,
        Optional<List<DataComponentType<?>>> p_338636_,
        Optional<List<DataComponentType<?>>> p_338680_
    ) {
        super(p_330806_);
        this.source = p_461204_;
        this.include = p_338636_.map(List::copyOf);
        this.exclude = p_338680_.map(List::copyOf);
        List<Predicate<DataComponentType<?>>> list = new ArrayList<>(2);
        p_338680_.ifPresent(p_338129_ -> list.add(p_338134_ -> !p_338129_.contains(p_338134_)));
        p_338636_.ifPresent(p_338131_ -> list.add(p_338131_::contains));
        this.bakedPredicate = Util.allOf(list);
    }

    @Override
    public LootItemFunctionType<CopyComponentsFunction> getType() {
        return LootItemFunctions.COPY_COMPONENTS;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(this.source.contextParam());
    }

    @Override
    public ItemStack run(ItemStack p_330563_, LootContext p_331220_) {
        DataComponentGetter datacomponentgetter = this.source.get(p_331220_);
        if (datacomponentgetter != null) {
            if (datacomponentgetter instanceof DataComponentMap datacomponentmap) {
                p_330563_.applyComponents(datacomponentmap.filter(this.bakedPredicate));
            } else {
                Collection<DataComponentType<?>> collection = this.exclude.orElse(List.of());
                this.include.map(Collection::stream).orElse(BuiltInRegistries.DATA_COMPONENT_TYPE.listElements().map(Holder::value)).forEach(p_450906_ -> {
                    if (!collection.contains(p_450906_)) {
                        TypedDataComponent<?> typeddatacomponent = datacomponentgetter.getTyped(p_450906_);
                        if (typeddatacomponent != null) {
                            p_330563_.set(typeddatacomponent);
                        }
                    }
                });
            }
        }

        return p_330563_;
    }

    public static CopyComponentsFunction.Builder copyComponentsFromEntity(ContextKey<? extends Entity> p_451493_) {
        return new CopyComponentsFunction.Builder(new CopyComponentsFunction.DirectSource<>(p_451493_));
    }

    public static CopyComponentsFunction.Builder copyComponentsFromBlockEntity(ContextKey<? extends BlockEntity> p_451522_) {
        return new CopyComponentsFunction.Builder(new CopyComponentsFunction.BlockEntitySource(p_451522_));
    }

    record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements LootContextArg.Getter<BlockEntity, DataComponentGetter> {
        public DataComponentGetter get(BlockEntity p_451571_) {
            return p_451571_.collectComponents();
        }
    }

    public static class Builder extends LootItemConditionalFunction.Builder<CopyComponentsFunction.Builder> {
        private final LootContextArg<DataComponentGetter> source;
        private Optional<ImmutableList.Builder<DataComponentType<?>>> include = Optional.empty();
        private Optional<ImmutableList.Builder<DataComponentType<?>>> exclude = Optional.empty();

        Builder(LootContextArg<DataComponentGetter> p_461177_) {
            this.source = p_461177_;
        }

        public CopyComponentsFunction.Builder include(DataComponentType<?> p_338267_) {
            if (this.include.isEmpty()) {
                this.include = Optional.of(ImmutableList.builder());
            }

            this.include.get().add(p_338267_);
            return this;
        }

        public CopyComponentsFunction.Builder exclude(DataComponentType<?> p_338302_) {
            if (this.exclude.isEmpty()) {
                this.exclude = Optional.of(ImmutableList.builder());
            }

            this.exclude.get().add(p_338302_);
            return this;
        }

        protected CopyComponentsFunction.Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyComponentsFunction(
                this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build)
            );
        }
    }

    record DirectSource<T extends DataComponentGetter>(ContextKey<? extends T> contextParam) implements LootContextArg.Getter<T, DataComponentGetter> {
        public DataComponentGetter get(T p_461198_) {
            return p_461198_;
        }
    }
}
