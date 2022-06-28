package me.tpgc.quarryio.blocks;

import me.tpgc.quarryio.util.UpgradeID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class UpgradeBaseBlock extends Block {

	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
	protected static final VoxelShape AABB = Block.box(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	
	public UpgradeBaseBlock() {
		super(BlockBehaviour.Properties.of(Material.STONE).strength(2.5f));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING);
	}
	
	@Override
	public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
		updateFacingDir(pState, pLevel, pPos);
	}
	
	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, level, pos, neighbor);
		updateFacingDir(state, (Level) level, pos);
	}
	
	private void updateFacingDir(BlockState state, Level level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (level.getBlockState(pos.relative(direction)).getBlock() instanceof QuarryBlock qb) {
				level.setBlock(pos, state.setValue(FACING, direction), 1 | 2);
				return;
			}
		}
	}
	
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
	      return AABB;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	public abstract UpgradeID getUpgradeID();

}
