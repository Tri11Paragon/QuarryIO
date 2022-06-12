package me.tpgc.quarryio.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockMarker extends Block {

	protected static final VoxelShape AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
	
	public BlockMarker() {
		super(BlockBehaviour.Properties.of(Material.METAL)
				.requiresCorrectToolForDrops()
				.strength(10f, 16f));
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
	      return AABB;
	   }
	
}
