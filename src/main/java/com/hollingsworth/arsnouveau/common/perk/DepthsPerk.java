package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class DepthsPerk extends Perk {

    public static DepthsPerk INSTANCE = new DepthsPerk(new ResourceLocation(ArsNouveau.MODID, "thread_depths"));
    public static final UUID PERK_UUID = UUID.fromString("ce320c42-9d63-4b83-9e69-ef144790d667");

    public DepthsPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public int getCountCap() {
        return 3;
    }

    @Override
    public String getLangDescription() {
        return "Greatly increases the amount of time you may breathe underwater by reducing the chance your air will decrease. If this perk is in slot 3 or higher, you will no longer lose air. Stacks with Respiration Enchantments.";
    }

    @Override
    public String getLangName() {
        return "Depths";
    }
}
