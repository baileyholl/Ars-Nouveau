package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.ITickablePerk;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.ArmorRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.MaterialRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class AnimatedMagicArmor extends ArmorItem implements IDyeable, GeoItem {
    public GeoModel<AnimatedMagicArmor> model;

    public AnimatedMagicArmor(Holder<ArmorMaterial> materialIn, ArmorItem.Type slot, Properties builder, GeoModel<AnimatedMagicArmor> model) {
        super(materialIn, slot, builder);
        this.model = model;
    }

    public AnimatedMagicArmor(Holder<ArmorMaterial> materialIn, ArmorItem.Type slot, GeoModel<AnimatedMagicArmor> model) {
        this(materialIn, slot, ItemsRegistry.defaultItemProperties().stacksTo(1).component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder()), model);
    }

    public static AnimatedMagicArmor light(ArmorItem.Type slot) {
        return new AnimatedMagicArmor(MaterialRegistry.LIGHT, slot,
                ItemsRegistry.defaultItemProperties()
                        .stacksTo(1)
                        .component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder())
                        .durability(slot.getDurability(20)), new GenericModel<AnimatedMagicArmor>("light_armor", "item/light_armor").withEmptyAnim());
    }

    public static AnimatedMagicArmor medium(ArmorItem.Type slot) {
        return new AnimatedMagicArmor(MaterialRegistry.MEDIUM, slot,ItemsRegistry.defaultItemProperties()
                .stacksTo(1)
                .component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder())
                .durability(slot.getDurability(25)), new GenericModel<AnimatedMagicArmor>("medium_armor", "item/medium_armor").withEmptyAnim());
    }

    public static AnimatedMagicArmor heavy(ArmorItem.Type slot) {
        return new AnimatedMagicArmor(MaterialRegistry.HEAVY, slot,ItemsRegistry.defaultItemProperties()
                .stacksTo(1)
                .component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder())
                .durability(slot.getDurability(35)), new GenericModel<AnimatedMagicArmor>("heavy_armor", "item/heavy_armor").withEmptyAnim());
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity player, int slotId, boolean pIsSelected) {
        super.inventoryTick(stack, world, player, slotId, pIsSelected);
        if(slotId >= Inventory.INVENTORY_SIZE && slotId < Inventory.INVENTORY_SIZE + 4){
            if (world.isClientSide())
                return;
            if(player instanceof LivingEntity livingEntity) {
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
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        var data = stack.get(DataComponentRegistry.ARMOR_PERKS);
        if (data != null) {
            tooltip.add(Component.translatable("ars_nouveau.tier", data.getTier() + 1).withStyle(ChatFormatting.GOLD));
            data.appendPerkTooltip(tooltip, stack);
        }
    }

    @Override
    public void onDye(ItemStack stack, DyeColor dyeColor) {
        var data = stack.get(DataComponentRegistry.ARMOR_PERKS);
        if(data == null){
            return;
        }
        stack.set(DataComponentRegistry.ARMOR_PERKS, data.setColor(dyeColor.getName()));
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
            private GeoArmorRenderer<?> renderer;

            @Override
            public @Nullable <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if(renderer == null){
                    renderer = new ArmorRenderer(getArmorModel());
                }
                return this.renderer;
            }
        });
    }

    public GeoModel<AnimatedMagicArmor> getArmorModel() {
        return model;
    }

    /*
     * Needed to avoid file not found errors since Geckolib doesn't redirect to the correct texture
     */
    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        GenericModel<AnimatedMagicArmor> genericModel = (GenericModel<AnimatedMagicArmor>) model;
        return ArsNouveau.prefix( "textures/" + genericModel.textPathRoot + "/" + genericModel.name + "_" + this.getColor(stack) + ".png");
    }


    public String getColor(ItemStack object) {
        var perkHolder = PerkUtil.getPerkHolder(object);
        if(!(perkHolder instanceof ArmorPerkHolder data)){
            return "purple";
        }
        return data.getColor() == null || data.getColor().isEmpty() ? "purple" : data.getColor();
    }

    //needed to exclude armors that can't be upgraded via recipe from jei info
    public int getMinTier() {
        return 0;
    }
}
