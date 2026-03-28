package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextArg;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
    public static final MapCodec<CopyNameFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_460608_ -> commonFields(p_460608_)
            .and(LootContextArg.ENTITY_OR_BLOCK.fieldOf("source").forGetter(p_460609_ -> p_460609_.source))
            .apply(p_460608_, CopyNameFunction::new)
    );
    private final LootContextArg<Object> source;

    private CopyNameFunction(List<LootItemCondition> p_298700_, LootContextArg<?> p_461033_) {
        super(p_298700_);
        this.source = LootContextArg.cast((LootContextArg<? extends Object>)p_461033_);
    }

    @Override
    public LootItemFunctionType<CopyNameFunction> getType() {
        return LootItemFunctions.COPY_NAME;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(this.source.contextParam());
    }

    @Override
    public ItemStack run(ItemStack p_80185_, LootContext p_80186_) {
        if (this.source.get(p_80186_) instanceof Nameable nameable) {
            p_80185_.set(DataComponents.CUSTOM_NAME, nameable.getCustomName());
        }

        return p_80185_;
    }

    public static LootItemConditionalFunction.Builder<?> copyName(LootContextArg<?> p_460773_) {
        return simpleBuilder(p_460607_ -> new CopyNameFunction(p_460607_, p_460773_));
    }
}
