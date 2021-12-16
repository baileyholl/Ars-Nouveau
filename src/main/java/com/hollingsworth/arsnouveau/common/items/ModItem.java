package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModItem extends Item {
    public List<Component> tooltip;
    public Rarity rarity;

    public ModItem(Properties properties) {
        super(properties);
    }

    public ModItem(Properties properties, String registryName){
        this(properties);
        setRegistryName(ArsNouveau.MODID, registryName);
    }

    public ModItem(String registryName){
        this(ItemsRegistry.defaultItemProperties(), registryName);
    }

    public ModItem withTooltip(Component tip){
        tooltip = new ArrayList<>();
        tooltip.add(tip);
        return this;
    }

    public ModItem withRarity(Rarity rarity){
        this.rarity = rarity;
        return this;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return rarity != null ? rarity : super.getRarity(stack);
    }

    public ItemStack getStack(){
        return new ItemStack(this);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if(tooltip != null && !tooltip.isEmpty()){
            tooltip2.addAll(tooltip);
        }
    }
}
