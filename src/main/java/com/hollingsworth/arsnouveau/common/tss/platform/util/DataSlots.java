package com.hollingsworth.arsnouveau.common.tss.platform.util;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class DataSlots extends DataSlot {
	private IntConsumer c;
	private IntSupplier s;

	@Override
	public int get() {
		return s.getAsInt();
	}

	@Override
	public void set(int p_39402_) {
		c.accept(p_39402_);
	}

	private DataSlots(IntConsumer c, IntSupplier s) {
		this.c = c;
		this.s = s;
	}

	public static DataSlot set(IntConsumer c) {
		return new DataSlots(c, () -> 0);
	}

	public static DataSlot get(IntSupplier s) {
		return new DataSlots(a -> {}, s);
	}

	public static DataSlot create(IntConsumer c, IntSupplier s) {
		return new DataSlots(c, s);
	}
}
