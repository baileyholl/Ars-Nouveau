package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class ModItem extends Item {
    public List<ITextComponent> tooltip;
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

    public ModItem withTooltip(ITextComponent tip){
        tooltip = new ArrayList<>();
        tooltip.add(tip);
        return this;
    }

    public ItemStack getStack(){
        return new ItemStack(this);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        if(tooltip != null && !tooltip.isEmpty()){
            tooltip2.addAll(tooltip);
        }
    }
}
