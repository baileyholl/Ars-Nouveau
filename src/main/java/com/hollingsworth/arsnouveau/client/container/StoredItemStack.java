package com.hollingsworth.arsnouveau.client.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.function.Function;

public class StoredItemStack {

    public static final Codec<StoredItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(StoredItemStack::getStack),
            Codec.LONG.fieldOf("count").forGetter(StoredItemStack::getQuantity)
    ).apply(instance, StoredItemStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StoredItemStack> STREAM = StreamCodec.composite(ItemStack.STREAM_CODEC, StoredItemStack::getStack, ByteBufCodecs.VAR_LONG, StoredItemStack::getQuantity, StoredItemStack::new);

    private ItemStack stack;
    private long count;
    private int hash;

    public StoredItemStack(ItemStack stack, long count) {
        this.stack = stack;
        this.count = count;
    }

    public StoredItemStack(ItemStack stack) {
        this.stack = stack.copy();
        this.stack.setCount(1);
        this.count = stack.getCount();
    }

    public ItemStack getStack() {
        return stack;
    }

    public long getQuantity() {
        return count;
    }

    public ItemStack getActualStack() {
        ItemStack s = stack.copy();
        s.setCount((int) count);
        return s;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        StoredItemStack other = (StoredItemStack) obj;
        if (stack == null) {
            return other.stack == null;
        } else return ItemStack.isSameItem(stack, other.stack) && ItemStack.matches(stack, other.stack);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = ItemStack.hashItemAndComponents(stack);
        }
        return hash;
    }
//
//	public CompoundTag writeToNBT(CompoundTag tag) {
//		tag.putLong(ITEM_COUNT_NAME, getQuantity());
//		tag.put(ITEMSTACK_NAME, stack.save(new CompoundTag()));
//		tag.getCompound(ITEMSTACK_NAME).remove("Count");
//		return tag;
//	}
//
//	public CompoundTag writeToNBT(CompoundTag tag, long q) {
//		tag.putLong(ITEM_COUNT_NAME, q);
//		tag.put(ITEMSTACK_NAME, stack.save(new CompoundTag()));
//		tag.getCompound(ITEMSTACK_NAME).remove("Count");
//		return tag;
//	}
//
//	public static StoredItemStack readFromNBT(CompoundTag tag) {
//		ItemStack cheat = ItemStack.of(tag);
//		tag.getCompound(ITEMSTACK_NAME).putByte("Count", (byte) 1);
//		StoredItemStack stack = new StoredItemStack(!cheat.isEmpty() ? cheat : ItemStack.of(tag.getCompound(ITEMSTACK_NAME)), !cheat.isEmpty() ? cheat.getCount() : tag.getLong(ITEM_COUNT_NAME));
//		return !stack.stack.isEmpty() ? stack : null;
//	}

    public static class ComparatorAmount implements IStoredItemStackComparator {
        public boolean reversed;

        public ComparatorAmount(boolean reversed) {
            this.reversed = reversed;
        }

        @Override
        public int compare(StoredItemStack in1, StoredItemStack in2) {
            int c = in2.getQuantity() > in1.getQuantity() ? 1 : (in1.getQuantity() == in2.getQuantity() ? in1.getStack().getHoverName().getString().compareTo(in2.getStack().getHoverName().getString()) : -1);
            return this.reversed ? -c : c;
        }

        @Override
        public boolean isReversed() {
            return reversed;
        }

        @Override
        public int type() {
            return 0;
        }

        @Override
        public void setReversed(boolean rev) {
            reversed = rev;
        }
    }

    public static class ComparatorName implements IStoredItemStackComparator {
        public boolean reversed;

        public ComparatorName(boolean reversed) {
            this.reversed = reversed;
        }

        @Override
        public int compare(StoredItemStack in1, StoredItemStack in2) {
            int c = in1.getDisplayName().compareTo(in2.getDisplayName());
            return this.reversed ? -c : c;
        }

        @Override
        public boolean isReversed() {
            return reversed;
        }

        @Override
        public int type() {
            return 1;
        }

        @Override
        public void setReversed(boolean rev) {
            reversed = rev;
        }
    }

    public interface IStoredItemStackComparator extends Comparator<StoredItemStack> {
        boolean isReversed();

        void setReversed(boolean rev);

        int type();
    }

    public enum SortingTypes {
        AMOUNT(ComparatorAmount::new),
        NAME(ComparatorName::new);
        public static final SortingTypes[] VALUES = values();
        private final Function<Boolean, IStoredItemStackComparator> factory;

        SortingTypes(Function<Boolean, IStoredItemStackComparator> factory) {
            this.factory = factory;
        }

        public IStoredItemStackComparator create(boolean rev) {
            return factory.apply(rev);
        }
    }

    public String getDisplayName() {
        return stack.getHoverName().getString();
    }

    public void grow(long c) {
        count += c;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getMaxStackSize() {
        return stack.getMaxStackSize();
    }
}
