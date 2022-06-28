package me.tpgc.quarryio.blocks;

import java.util.Random;

import me.tpgc.quarryio.blocks.tile.QuarryEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class QuarryBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public QuarryBlock() {
		super(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(5f, 16f).isRedstoneConductor((e, r, t) -> true));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING);
	}
	
	@Override
	public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
		super.destroy(pLevel, pPos, pState);
		if (!pLevel.isClientSide()) {
			BlockEntity entity = pLevel.getBlockEntity(pPos);
			if (entity instanceof QuarryEntity qentity) {
				qentity.remove();
			}
		}
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,BlockHitResult pHit) {
		
		if (!pLevel.isClientSide) {
			BlockEntity entity = pLevel.getBlockEntity(pPos);
			if (entity instanceof QuarryEntity qentity) {
				qentity.use(pPlayer, pState.getValue(HorizontalDirectionalBlock.FACING));
			} else
				throw new IllegalStateException("Unable to find quarry entity!");
		}
		
		return InteractionResult.sidedSuccess(pLevel.isClientSide);
	}
	
	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, level, pos, neighbor);
		if (!level.isClientSide()) {
			BlockEntity entity = level.getBlockEntity(pos);
			if (entity instanceof QuarryEntity qentity) {
				qentity.blockUpdate(state.getValue(HorizontalDirectionalBlock.FACING));
			}
		}
	}
	
	@Override
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
		super.animateTick(pState, pLevel, pPos, pRandom);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pLevel.isClientSide)
			return (lvl, pos, blockstate, t) -> {
				if (t instanceof QuarryEntity tile)
					tile.animateTick();
			};
		else 
			return (lvl, pos, blockstate, t) -> {
				if (t instanceof QuarryEntity tile)
					tile.tickServer();
			};
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new QuarryEntity(pPos, pState);
	}
	
	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 * 
	 * @deprecated call via
	 *             {@link net.minecraft.world.level.block.state.BlockBehavior.BlockStateBase#rotate}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 * 
	 * @deprecated call via
	 *             {@link net.minecraft.world.level.block.state.BlockBehavior.BlockStateBase#mirror}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}
	
}
