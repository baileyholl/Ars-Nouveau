package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface LootContextArg<R> {
    Codec<LootContextArg<Object>> ENTITY_OR_BLOCK = createArgCodec(
        p_461084_ -> p_461084_.anyOf(LootContext.EntityTarget.values()).anyOf(LootContext.BlockEntityTarget.values())
    );

    @Nullable R get(LootContext p_461136_);

    ContextKey<?> contextParam();

    static <U> LootContextArg<U> cast(LootContextArg<? extends U> p_461225_) {
        return (LootContextArg<U>)p_461225_;
    }

    static <R> Codec<LootContextArg<R>> createArgCodec(UnaryOperator<LootContextArg.ArgCodecBuilder<R>> p_461169_) {
        return p_461169_.apply(new LootContextArg.ArgCodecBuilder<>()).build();
    }

    public static final class ArgCodecBuilder<R> {
        private final ExtraCodecs.LateBoundIdMapper<String, LootContextArg<R>> sources = new ExtraCodecs.LateBoundIdMapper<>();

        ArgCodecBuilder() {
        }

        public <T> LootContextArg.ArgCodecBuilder<R> anyOf(T[] p_461148_, Function<T, String> p_460903_, Function<T, ? extends LootContextArg<R>> p_460900_) {
            for (T t : p_461148_) {
                this.sources.put(p_460903_.apply(t), (LootContextArg<R>)p_460900_.apply(t));
            }

            return this;
        }

        public <T extends StringRepresentable> LootContextArg.ArgCodecBuilder<R> anyOf(T[] p_461102_, Function<T, ? extends LootContextArg<R>> p_461229_) {
            return this.anyOf(p_461102_, StringRepresentable::getSerializedName, p_461229_);
        }

        public <T extends StringRepresentable & LootContextArg<? extends R>> LootContextArg.ArgCodecBuilder<R> anyOf(T[] p_460696_) {
            return this.anyOf(p_460696_, p_461014_ -> LootContextArg.cast((LootContextArg<? extends R>)p_461014_));
        }

        public LootContextArg.ArgCodecBuilder<R> anyEntity(Function<? super ContextKey<? extends Entity>, ? extends LootContextArg<R>> p_461163_) {
            return this.anyOf(LootContext.EntityTarget.values(), p_460742_ -> p_461163_.apply(p_460742_.contextParam()));
        }

        public LootContextArg.ArgCodecBuilder<R> anyBlockEntity(Function<? super ContextKey<? extends BlockEntity>, ? extends LootContextArg<R>> p_461088_) {
            return this.anyOf(LootContext.BlockEntityTarget.values(), p_460786_ -> p_461088_.apply(p_460786_.contextParam()));
        }

        public LootContextArg.ArgCodecBuilder<R> anyItemStack(Function<? super ContextKey<? extends ItemStack>, ? extends LootContextArg<R>> p_461013_) {
            return this.anyOf(LootContext.ItemStackTarget.values(), p_460876_ -> p_461013_.apply(p_460876_.contextParam()));
        }

        Codec<LootContextArg<R>> build() {
            return this.sources.codec(Codec.STRING);
        }
    }

    public interface Getter<T, R> extends LootContextArg<R> {
        @Nullable R get(T p_460721_);

        @Override
        ContextKey<? extends T> contextParam();

        @Override
        default @Nullable R get(LootContext p_461179_) {
            T t = p_461179_.getOptionalParameter((ContextKey<T>)this.contextParam());
            return t != null ? this.get(t) : null;
        }
    }

    public interface SimpleGetter<T> extends LootContextArg<T> {
        @Override
        ContextKey<? extends T> contextParam();

        @Override
        default @Nullable T get(LootContext p_461001_) {
            return p_461001_.getOptionalParameter((ContextKey<T>)this.contextParam());
        }
    }
}
