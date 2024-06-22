package com.hollingsworth.arsnouveau.common.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.IVariantColorProvider;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.perk.*;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.ArmorRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class AnimatedMagicArmor extends ArmorItem implements IManaEquipment, IDyeable, GeoItem, IVariantColorProvider<ItemStack> {
    public GeoModel<AnimatedMagicArmor> model;

    public AnimatedMagicArmor(Holder<ArmorMaterial> materialIn, ArmorItem.Type slot, Properties builder, GeoModel<AnimatedMagicArmor> model) {
        super(materialIn, slot, builder);
        this.model = model;
    }

    public AnimatedMagicArmor(Holder<ArmorMaterial> materialIn, ArmorItem.Type slot, GeoModel<AnimatedMagicArmor> model) {
        this(materialIn, slot, ItemsRegistry.defaultItemProperties().stacksTo(1), model);
    }

    public static AnimatedMagicArmor light(ArmorItem.Type slot) {
        return new AnimatedMagicArmor(Materials.LIGHT, slot, new GenericModel<AnimatedMagicArmor>("light_armor", "item/light_armor").withEmptyAnim());
    }

    public static AnimatedMagicArmor medium(ArmorItem.Type slot) {
        return new AnimatedMagicArmor(Materials.MEDIUM, slot, new GenericModel<AnimatedMagicArmor>("medium_armor", "item/medium_armor").withEmptyAnim());
    }

    public static AnimatedMagicArmor heavy(ArmorItem.Type slot) {
        return new AnimatedMagicArmor(Materials.HEAVY, slot, new GenericModel<AnimatedMagicArmor>("heavy_armor", "item/heavy_armor").withEmptyAnim());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (world.isClientSide())
            return;
        RepairingPerk.attemptRepair(stack, player);
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
        if (perkHolder == null)
            return;
        for (PerkInstance instance : perkHolder.getPerkInstances()) {
            if (instance.getPerk() instanceof ITickablePerk tickablePerk) {
                tickablePerk.tick(stack, world, player, instance);
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();
        attributes.putAll(super.getDefaultAttributeModifiers(pEquipmentSlot));
        if (this.type.getSlot() == pEquipmentSlot) {
            UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(type);
            IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
            if (perkHolder != null) {
                attributes.put(PerkAttributes.MAX_MANA.get(), new AttributeModifier(uuid, "max_mana_armor", 30 * (perkHolder.getTier() + 1), AttributeModifier.Operation.ADDITION));
                attributes.put(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(uuid, "mana_regen_armor", perkHolder.getTier() + 1, AttributeModifier.Operation.ADDITION));
                for (PerkInstance perkInstance : perkHolder.getPerkInstances()) {
                    IPerk perk = perkInstance.getPerk();
                    attributes.putAll(perk.getModifiers(this.type.getSlot(), stack, perkInstance.getSlot().value));
                }

            }
        }
        return attributes.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        IPerkProvider<ItemStack> perkProvider = PerkRegistry.getPerkProvider(stack.getItem());
        if (perkProvider != null) {
            if (perkProvider.getPerkHolder(stack) instanceof ArmorPerkHolder armorPerkHolder) {
                tooltip.add(Component.translatable("ars_nouveau.tier", armorPerkHolder.getTier() + 1).withStyle(ChatFormatting.GOLD));
            }
            perkProvider.getPerkHolder(stack).appendPerkTooltip(tooltip, stack);
        }
    }

    @Override
    public void onDye(ItemStack stack, DyeColor dyeColor) {
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
        if (perkHolder instanceof ArmorPerkHolder armorPerkHolder) {
            armorPerkHolder.setColor(dyeColor.getName());
        }
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if(renderer == null){
                    renderer = new ArmorRenderer(getArmorModel());
                }
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
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
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        GenericModel<AnimatedMagicArmor> genericModel = (GenericModel<AnimatedMagicArmor>) model;
        return ArsNouveau.prefix( "textures/" + genericModel.textPathRoot + "/" + genericModel.name + "_" + this.getColor(stack) + ".png").toString();
    }

    @Override
    public void setColor(String color, ItemStack armor) {

    }

    @Override
    public String getColor(ItemStack object) {
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(object);
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
