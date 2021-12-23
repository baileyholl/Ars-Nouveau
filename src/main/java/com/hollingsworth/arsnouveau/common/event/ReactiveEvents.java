package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketReactiveSpell;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ReactiveEvents {

    @SubscribeEvent
    public static void livingHitEvent(LivingHurtEvent e){
        LivingEntity entity = e.getEntityLiving();
        if(entity.getCommandSenderWorld().isClientSide || !(entity instanceof Player))
            return;

        for(ItemStack s : entity.getArmorSlots()){
            castSpell((Player) entity, s);
        }
    }

    public static void castSpell(Player playerIn, ItemStack s){

        if(EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT, s) * .25 >= Math.random() && new ReactiveCaster(s).getSpell().isValid()){
            ReactiveCaster reactiveCaster = new ReactiveCaster(s);
            reactiveCaster.castSpell(playerIn.getCommandSenderWorld(), playerIn, InteractionHand.MAIN_HAND, null);
        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock e){
        Player entity = e.getPlayer();

        if(entity.getCommandSenderWorld().isClientSide)
            return;
        ItemStack s = e.getItemStack();
        castSpell(entity, s);
    }

    @SubscribeEvent
    public static void playerAttackEntity(AttackEntityEvent e){
        LivingEntity entity = e.getEntityLiving();

        if(entity == null || entity.getCommandSenderWorld().isClientSide || !(entity instanceof Player))
            return;
        ItemStack s = e.getEntityLiving().getMainHandItem();
        castSpell((Player) entity, s);
    }


    @SubscribeEvent
    public static void leftClickAir(PlayerInteractEvent.LeftClickEmpty e){
        if(EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT, e.getItemStack()) > 0)
            Networking.INSTANCE.sendToServer(new PacketReactiveSpell());
    }
}
