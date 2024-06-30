package com.hollingsworth.arsnouveau.client.container;

import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.function.Function;

public class StoredItemStack {
	private ItemStack stack;
	private long count;
	private static final String ITEM_COUNT_NAME = "c", ITEMSTACK_NAME = "s";
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
			reversed  = rev;
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
		NAME(ComparatorName::new)
		;
		public static final SortingTypes[] VALUES = values();
		private final Function<Boolean, IStoredItemStackComparator> factory;
		SortingTypes(Function<Boolean, IStoredItemStackComparator> factory) {
			this.factory = factory;
		}

		public IStoredItemStackComparator create(boolean rev) {
			return factory.apply(rev);
		}
	}

	@Override
	public int hashCode() {
		if(hash == 0) {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((stack == null) ? 0 : stack.getItem().hashCode());
			result = prime * result + ((stack == null || !stack.hasTag()) ? 0 : stack.getTag().hashCode());
			hash = result;
			return result;
		}
		return hash;
	}

	public String getDisplayName() {
		return stack.getHoverName().getString();
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

	public boolean equals(StoredItemStack other) {
		if (this == other) return true;
		if (other == null) return false;
		if (count != other.count) return false;
		if (stack == null) {
			return other.stack == null;
		} else return ItemStack.isSameItem(stack, other.stack) && ItemStack.matches(stack, other.stack);
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
