package me.tpgc.quarryio.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class BlockDebugQuarry extends Block {

	public BlockDebugQuarry() {
		super(
				BlockBehaviour.Properties.of(Material.CLAY)
					.requiresCorrectToolForDrops()
					.strength(5f, 16f));
		
	}

}
