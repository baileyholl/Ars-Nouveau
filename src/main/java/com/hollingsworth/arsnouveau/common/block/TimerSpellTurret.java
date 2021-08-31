package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.TimerSpellTurretTile;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class TimerSpellTurret extends BasicSpellTurret{

    public TimerSpellTurret(Properties properties, String registry) {
        super(properties, registry);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.FALSE));
    }

    public TimerSpellTurret() {
        this(defaultProperties().noOcclusion(), LibBlockNames.TIMER_SPELL_TURRET);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TimerSpellTurretTile();
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getItemInHand(handIn);
        if(handIn == Hand.MAIN_HAND) {
            if ((stack.getItem() instanceof SpellParchment) || worldIn.isClientSide)
                return super.use(state, worldIn, pos, player, handIn, hit);
            TimerSpellTurretTile timerSpellTurretTile = (TimerSpellTurretTile) worldIn.getBlockEntity(pos);
            if(timerSpellTurretTile.isLocked)
                return ActionResultType.SUCCESS;
            timerSpellTurretTile.addTime(20 * (player.isShiftKeyDown() ? 10 : 1));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void attack(BlockState state, World level, BlockPos pos, PlayerEntity player) {
        if(!level.isClientSide){
            TimerSpellTurretTile timerSpellTurretTile = (TimerSpellTurretTile) level.getBlockEntity(pos);
            if(!timerSpellTurretTile.isLocked){
                timerSpellTurretTile.addTime(-20 * (player.isShiftKeyDown() ? 10 : 1));
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if(!world.isClientSide() && world.getBlockEntity(pos) instanceof TimerSpellTurretTile){
            ((TimerSpellTurretTile) world.getBlockEntity(pos)).isOff = world.hasNeighborSignal(pos);
            ((TimerSpellTurretTile) world.getBlockEntity(pos)).update();
        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock e){
    }
}
