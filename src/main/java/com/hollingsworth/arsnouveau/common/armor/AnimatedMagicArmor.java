package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.api.client.IVariantColorProvider;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AnimatedMagicArmor extends MagicArmor implements IManaEquipment, IAnimatable, IVariantColorProvider<ItemStack> {

    public AnimatedMagicArmor(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return GeoArmorRenderer.getRenderer(AnimatedMagicArmor.this.getClass(), livingEntity)
                        .applyEntityStats(original).applySlot(equipmentSlot)
                        .setCurrentItem(livingEntity, itemStack, equipmentSlot);
            }
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Nullable
    @Override
    public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        Class<? extends ArmorItem> clazz = this.getClass();
        GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz, entity);
        return renderer.getTextureLocation((ArmorItem) stack.getItem()).toString();
    }

    @Override
    public void setColor(String color) {

    }


    @Override
    public String getColor(ItemStack object) {
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(object);
        if(!(perkHolder instanceof ArmorPerkHolder data)){
            return "purple";
        }
        return data.getColor() == null || data.getColor().isEmpty() ? "purple" : data.getColor();
    }
}
