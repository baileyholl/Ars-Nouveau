package com.hollingsworth.arsnouveau.common.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.IVariantColorProvider;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.perk.*;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class AnimatedMagicArmor extends ArmorItem implements IManaEquipment, IDyeable, IAnimatable, IVariantColorProvider<ItemStack> {

    public AnimatedMagicArmor(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void registerControllers(AnimationData data) {
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

    protected UUID getModifierForSlot(EquipmentSlot pEquipmentSlot) {
        return ARMOR_MODIFIER_UUID_PER_SLOT[pEquipmentSlot.getIndex()];
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();
        attributes.putAll(super.getDefaultAttributeModifiers(pEquipmentSlot));
        if (this.slot == pEquipmentSlot) {
            UUID uuid = getModifierForSlot(this.slot);
            IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
            if (perkHolder != null) {
                attributes.put(PerkAttributes.FLAT_MANA_BONUS.get(), new AttributeModifier(uuid, "max_mana_armor", 30 * (perkHolder.getTier() + 1), AttributeModifier.Operation.ADDITION));
                attributes.put(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(uuid, "mana_regen_armor", perkHolder.getTier() + 1, AttributeModifier.Operation.ADDITION));
                for (PerkInstance perkInstance : perkHolder.getPerkInstances()) {
                    IPerk perk = perkInstance.getPerk();
                    attributes.putAll(perk.getModifiers(this.slot, stack, perkInstance.getSlot().value));
                }

            }
        }
        return attributes.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        IPerkProvider<ItemStack> perkProvider = ArsNouveauAPI.getInstance().getPerkProvider(stack.getItem());
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

    AnimationFactory factory = GeckoLibUtil.createFactory(this);

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
