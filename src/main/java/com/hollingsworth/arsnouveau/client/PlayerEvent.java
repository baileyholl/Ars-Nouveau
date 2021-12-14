package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketGetPersistentData;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.DropDistribution.rand;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class PlayerEvent {

    @SubscribeEvent
    public static void onBlock(final PlayerInteractEvent.RightClickBlock event) {
        Player entity = event.getPlayer();
        if(!event.getWorld().isClientSide || event.getHand() != InteractionHand.MAIN_HAND || event.getWorld().getBlockState(event.getPos()).getBlock() instanceof ScribesBlock)
            return;
        if(entity.getItemInHand(event.getHand()).getItem() instanceof SpellBook){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onTooltip(final ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        int level = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT, stack);
        if(level > 0 && stack.hasTag() && stack.getTag().contains("spell")){
            Spell spell = Spell.deserialize(stack.getTag().getString("spell"));
            event.getToolTip().add(new TextComponent(spell.getDisplayString()));
        }
    }

    @SubscribeEvent
    public static void onItem(final PlayerInteractEvent.RightClickItem event) {
        Player entity = event.getPlayer();
        if(!event.getWorld().isClientSide || event.getHand() != InteractionHand.MAIN_HAND)
            return;
        if(entity.getItemInHand(event.getHand()).getItem() instanceof SpellBook){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void playerLoginEvent(final net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event){
        if(!event.getPlayer().level.isClientSide && event.getPlayer().hasEffect(ModPotions.SCRYING_EFFECT)){
            CompoundTag tag = event.getPlayer().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()-> (ServerPlayer) event.getPlayer()), new PacketGetPersistentData(tag));
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(final TickEvent.PlayerTickEvent event){
        if(event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END && event.player.getEffect(ModPotions.SCRYING_EFFECT) != null && ClientInfo.ticksInGame % 30 == 0){

            List<BlockPos> scryingPos = new ArrayList<>();
            CompoundTag tag = ClientInfo.persistentData;
            if(!tag.contains("an_scrying"))
                return;
            Player playerEntity = event.player;
            Level world = playerEntity.level;
            for(BlockPos p : BlockPos.withinManhattan(playerEntity.blockPosition(), 20, 120, 20)){
                if(p.getY() >= world.getMaxBuildHeight() || world.getBlockState(p).isAir())
                    continue;
                if(scryingPos.size() >= 50)
                    break;

                if(world.getBlockState(p).getBlock().getRegistryName().toString().equals(tag.getString("an_scrying"))) {
                    scryingPos.add(new BlockPos(p));
                }
            }
            ClientInfo.scryingPositions = scryingPos;
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(final RenderLevelLastEvent event)
    {
        final Player playerEntity = Minecraft.getInstance().player;

        if (playerEntity == null || playerEntity.getEffect(ModPotions.SCRYING_EFFECT) == null)
            return;
        Vec3 vector3d = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        ClientLevel world = Minecraft.getInstance().level;

        double yView = vector3d.y();
        if(Minecraft.getInstance().isPaused())
            return;
        for(BlockPos p : ClientInfo.scryingPositions){
            ParticleColor color = new ParticleColor(
                    rand.nextInt(255),
                    rand.nextInt(255),
                    rand.nextInt(255));
            BlockPos renderPos = new BlockPos(p);
            if(Math.abs(yView - p.getY()) >= 30){
                renderPos = new BlockPos(p.getX(), p.getY() > yView ? yView + 20 : yView - 20, p.getZ());
                color = new ParticleColor(
                        rand.nextInt(30),
                        rand.nextInt(255),
                        rand.nextInt(50));
            }

            if(Math.abs(yView - p.getY()) >= 60){
                renderPos = new BlockPos(p.getX(), p.getY() > yView ? yView + 20 : yView - 20, p.getZ());
               color =  new ParticleColor(
                        rand.nextInt(50),
                        rand.nextInt(50),
                        rand.nextInt(255));
            }

            world.addParticle(
                    GlowParticleData.createData(color, true),
                    renderPos.getX() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), renderPos.getY() + 0.2 + ParticleUtil.inRange(-0.1, 0.1), renderPos.getZ() + 0.5 + ParticleUtil.inRange(-0.1, 0.1),
                    0, 0.03f, 0);

        }
    }

}
