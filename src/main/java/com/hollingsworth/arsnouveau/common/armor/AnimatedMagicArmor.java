package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.ITickablePerk;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.ArmorRenderer;
import com.hollingsworth.arsnouveau.client.renderer.item.DyeableGeoModel;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.MaterialRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * Animated magic armor item for Ars Nouveau.
 * In 1.21.11, ArmorItem is gone; we extend Item and set DataComponents.EQUIPPABLE.
 * ArmorMaterial is now a plain record (not a registry) in net.minecraft.world.item.equipment.
 */
public class AnimatedMagicArmor extends Item implements IDyeable, GeoItem {
    public final ArmorType type;
    public final ArmorMaterial material;
    public GeoModel<AnimatedMagicArmor> model;

    public AnimatedMagicArmor(ArmorMaterial material, ArmorType type, Properties builder, GeoModel<AnimatedMagicArmor> model) {
        super(builder.component(DataComponents.EQUIPPABLE,
                Equippable.builder(type.getSlot())
                        .setEquipSound(material.equipSound())
                        .setAsset(material.assetId())
                        .build())
                .durability(type.getDurability(material.durability()))
                .attributes(material.createAttributes(type)));
        this.type = type;
        this.material = material;
        this.model = model;
    }

    public AnimatedMagicArmor(ArmorMaterial material, ArmorType type, GeoModel<AnimatedMagicArmor> model) {
        this(material, type, ItemsRegistry.defaultItemProperties().stacksTo(1).component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder()).component(DataComponents.BASE_COLOR, DyeColor.PURPLE), model);
    }

    public static AnimatedMagicArmor light(ArmorType type) {
        return new AnimatedMagicArmor(MaterialRegistry.LIGHT, type,
                ItemsRegistry.defaultItemProperties()
                        .stacksTo(1)
                        .component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder())
                        .component(DataComponents.BASE_COLOR, DyeColor.PURPLE),
                new DyeableGeoModel<AnimatedMagicArmor>("light_armor", "item/light_armor").withEmptyAnim());
    }

    public static AnimatedMagicArmor medium(ArmorType type) {
        return new AnimatedMagicArmor(MaterialRegistry.MEDIUM, type, ItemsRegistry.defaultItemProperties()
                .stacksTo(1)
                .component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder())
                .component(DataComponents.BASE_COLOR, DyeColor.PURPLE),
                new DyeableGeoModel<AnimatedMagicArmor>("medium_armor", "item/medium_armor").withEmptyAnim());
    }

    public static AnimatedMagicArmor heavy(ArmorType type) {
        return new AnimatedMagicArmor(MaterialRegistry.HEAVY, type, ItemsRegistry.defaultItemProperties()
                .stacksTo(1)
                .component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder())
                .component(DataComponents.BASE_COLOR, DyeColor.PURPLE),
                new DyeableGeoModel<AnimatedMagicArmor>("heavy_armor", "item/heavy_armor").withEmptyAnim());
    }

    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel world, @NotNull Entity player, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, player, slot);
        // Only tick when equipped in an armor slot
        if (slot != null && slot.isArmor()) {
            if (player instanceof LivingEntity livingEntity) {
                RepairingPerk.attemptRepair(stack, livingEntity);
                var perkHolder = PerkUtil.getPerkHolder(stack);
                if (perkHolder == null)
                    return;
                for (PerkInstance instance : perkHolder.getPerkInstances(stack)) {
                    if (instance.getPerk() instanceof ITickablePerk tickablePerk) {
                        tickablePerk.tick(stack, world, livingEntity, instance);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        var modifiers = super.getDefaultAttributeModifiers(stack);
        var perkHolder = PerkUtil.getPerkHolder(stack);
        if (perkHolder != null) {
            for (PerkInstance instance : perkHolder.getPerkInstances(stack)) {
                modifiers = instance.getPerk().applyAttributeModifiers(modifiers, stack, instance.getSlot().value(), EquipmentSlotGroup.bySlot(this.type.getSlot()));
            }
            modifiers = modifiers.withModifierAdded(PerkAttributes.MAX_MANA, new AttributeModifier(ArsNouveau.prefix("max_mana_armor_" + this.type.getName()), 30 * (perkHolder.getTier() + 1), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(this.type.getSlot()));
            modifiers = modifiers.withModifierAdded(PerkAttributes.MANA_REGEN_BONUS, new AttributeModifier(ArsNouveau.prefix("mana_regen_armor_" + this.type.getName()), perkHolder.getTier() + 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(this.type.getSlot()));
        }
        return modifiers;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext world, @NotNull TooltipDisplay pTooltipDisplay, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, pTooltipDisplay, tooltip, flag);
        var data = stack.get(DataComponentRegistry.ARMOR_PERKS);
        if (data != null) {
            tooltip.accept(Component.translatable("ars_nouveau.tier", data.getTier() + 1).withStyle(ChatFormatting.GOLD));
            data.appendPerkTooltip(tooltip, stack);
        }
    }

    @Override
    public boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return true;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        GeoItem.super.createGeoRenderer(consumer);
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?, ?> renderer;

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                if (renderer == null) {
                    renderer = new ArmorRenderer(getArmorModel());
                }
                return this.renderer;
            }
        });
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return !stack.has(DataComponentRegistry.UNBREAKING);
    }

    public GeoModel<AnimatedMagicArmor> getArmorModel() {
        return model;
    }

    @Deprecated(forRemoval = true) // Use BASE_COLOR data component instead
    public String getColor(ItemStack object) {
        return object.getOrDefault(DataComponents.BASE_COLOR, DyeColor.PURPLE).getName();
    }

    //needed to exclude armors that can't be upgraded via recipe from jei info
    public int getMinTier() {
        return 0;
    }
}
