package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.block.tile.ManaJarTile;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ManaUtil {

    public static int getPlayerDiscounts(LivingEntity e){
        AtomicInteger discounts = new AtomicInteger();
        CuriosUtil.getAllWornItems(e).ifPresent(items ->{

            for(int i = 0; i < items.getSlots(); i++){
                Item item = items.getStackInSlot(i).getItem();
                if(item instanceof IManaEquipment)
                    discounts.addAndGet(((IManaEquipment) item).getManaDiscount());
            }
        });
        return discounts.get();
    }


    public static int getMaxMana(PlayerEntity e){
        IMana mana = ManaCapability.getMana(e).orElse(null);
        if(mana == null)
            return 0;
        int max = Config.INIT_MAX_MANA.get();
        for(ItemStack i : e.getEquipmentAndArmor()){
            if(i.getItem() instanceof IManaEquipment){
                max += (((IManaEquipment) i.getItem()).getMaxManaBoost());
            }
            max += ( Config.MANA_BOOST_BONUS.get() * EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, i));
        }

        IItemHandlerModifiable items = CuriosUtil.getAllWornItems(e).orElse(null);
        if(items != null){
            for(int i = 0; i < items.getSlots(); i++){
                Item item = items.getStackInSlot(i).getItem();
                if(item instanceof IManaEquipment)
                    max += (((IManaEquipment) item).getMaxManaBoost());
            }
        }

        int tier = mana.getBookTier();
        int numGlyphs = mana.getGlyphBonus() > 5 ? mana.getGlyphBonus() - 5 : 0;
        max += numGlyphs * Config.GLYPH_MAX_BONUS.get();
        max += tier * Config.TIER_MAX_BONUS.get();

        MaxManaCalcEvent event = new MaxManaCalcEvent(e, max);
        MinecraftForge.EVENT_BUS.post(event);
        max = event.getMax();
        return max;
    }

    public static double getManaRegen(PlayerEntity e) {
        IMana mana = ManaCapability.getMana(e).orElse(null);
        if(mana == null)
            return 0;
        double regen = Config.INIT_MANA_REGEN.get();
        for(ItemStack i : e.getEquipmentAndArmor()){
            if(i.getItem() instanceof MagicArmor){
                MagicArmor armor = ((MagicArmor) i.getItem());
                regen += armor.getManaRegenBonus();
            }
            regen += Config.MANA_REGEN_ENCHANT_BONUS.get() * EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, i);
        }
        IItemHandlerModifiable items = CuriosUtil.getAllWornItems(e).orElse(null);
        if(items != null){
            for(int i = 0; i < items.getSlots(); i++){
                Item item = items.getStackInSlot(i).getItem();
                if(item instanceof IManaEquipment)
                    regen += ((IManaEquipment) item).getManaRegenBonus();
            }
        }

        int tier = mana.getBookTier();
        double numGlyphs = mana.getGlyphBonus() > 5 ? mana.getGlyphBonus() - 5 : 0;
        regen += numGlyphs * Config.GLYPH_REGEN_BONUS.get();
        regen += tier;
        if(e.getActivePotionEffect(ModPotions.MANA_REGEN_EFFECT) != null)
            regen += Config.MANA_REGEN_POTION.get() * (1 + e.getActivePotionEffect(ModPotions.MANA_REGEN_EFFECT).getAmplifier());
        ManaRegenCalcEvent event = new ManaRegenCalcEvent(e, regen);
        MinecraftForge.EVENT_BUS.post(event);
        regen = event.getRegen();
        return regen;
    }

    /**
     * Searches for nearby mana jars that have enough mana.
     * Returns the position where the mana was taken, or null if none were found.
     */
    @Nullable
    public static BlockPos takeManaNearby(BlockPos pos, World world, int range, int mana){
        final BlockPos[] pos1 = {null};
        BlockPos.getAllInBox(pos.add(range, range, range), pos.add(-range, -range, -range)).forEach(blockPos -> {
            blockPos = blockPos.toImmutable();
            if(pos1[0] == null && world.getTileEntity(blockPos) instanceof ManaJarTile && ((ManaJarTile) world.getTileEntity(blockPos)).getCurrentMana() >= mana) {
                ((ManaJarTile) world.getTileEntity(blockPos)).removeMana(mana);
                pos1[0] = blockPos;
            }
        });
        return pos1[0];
    }

    public static BlockPos takeManaNearbyWithParticles(BlockPos pos, World world, int range, int mana){
        BlockPos result = takeManaNearby(pos,world,range,mana);
        if(result != null){
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, result, pos);
            world.addEntity(aoeProjectile);
        }
        return result;
    }

    /**
     * Searches for nearby mana jars that have enough mana.
     * Returns the position where the mana was taken, or null if none were found.
     */
    @Nullable
    public static boolean hasManaNearby(BlockPos pos, World world, int range, int mana){
        final boolean[] hasMana = {false};
        BlockPos.getAllInBox(pos.add(range, range, range), pos.add(-range, -range, -range)).forEach(blockPos -> {
            blockPos = blockPos.toImmutable();
            if(!hasMana[0] && world.getTileEntity(blockPos) instanceof ManaJarTile && ((ManaJarTile) world.getTileEntity(blockPos)).getCurrentMana() >= mana) {
                hasMana[0] = true;
            }
        });
        return hasMana[0];
    }

    @Nullable
    public static BlockPos canGiveMana(BlockPos pos, World world, int range){
        final boolean[] hasMana = {false};
        final BlockPos[] loc = {null};
        BlockPos.getAllInBox(pos.add(range, range, range), pos.add(-range, -range, -range)).forEach(blockPos -> {
            blockPos = blockPos.toImmutable();
            if(!hasMana[0] && world.getTileEntity(blockPos) instanceof ManaJarTile && ((ManaJarTile) world.getTileEntity(blockPos)).canAcceptMana()) {
                hasMana[0] = true;
                loc[0] = blockPos;
            }
        });
        return  loc[0];
    }

    @Nullable
    public static BlockPos canGiveManaClosest(BlockPos pos, World world, int range){
        Optional<BlockPos> loc = BlockPos.getClosestMatchingPosition(pos, range, range, (b) ->  world.getTileEntity(b) instanceof ManaJarTile && ((ManaJarTile) world.getTileEntity(b)).canAcceptMana());
        return loc.orElse(null);
    }
}
