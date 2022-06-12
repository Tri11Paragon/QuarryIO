package me.tpgc.quarryio.blocks;

import java.util.function.Supplier;

import me.tpgc.quarryio.QuarryIO;
import me.tpgc.quarryio.items.QuarryIOItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class QuarryIOBlocks {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, QuarryIO.MOD_ID);
	
	public static final RegistryObject<Block> DEBUG_QUARRY = registerBlock("debug_quarry", () -> new BlockDebugQuarry());
	public static final RegistryObject<Block> MARKER = registerBlock("marker", () -> new BlockMarker());
	
	private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
		RegistryObject<T> blk = BLOCKS.register(name, block);
		registerBlockItem(name, blk);
		return blk;
	}
	
	private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
		return QuarryIOItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(QuarryIO.MOD_TAB)));
	}
	
	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
	}
	
}
