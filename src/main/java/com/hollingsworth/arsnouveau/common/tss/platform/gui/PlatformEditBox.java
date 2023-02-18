package com.hollingsworth.arsnouveau.common.tss.platform.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class PlatformEditBox extends EditBox {

	public PlatformEditBox(Font p_94114_, int p_94115_, int p_94116_, int p_94117_, int p_94118_, Component p_94119_) {
		super(p_94114_, p_94115_, p_94116_, p_94117_, p_94118_, p_94119_);
	}

	public void setY(int i) {
		y = i;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
