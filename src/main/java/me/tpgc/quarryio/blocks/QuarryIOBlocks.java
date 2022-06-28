package me.tpgc.quarryio.blocks;

import java.util.function.Supplier;

import me.tpgc.quarryio.QuarryIO;
import me.tpgc.quarryio.items.HoverableBlockItem;
import me.tpgc.quarryio.items.QuarryIOItems;
import me.tpgc.quarryio.util.UpgradeID;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class QuarryIOBlocks {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, QuarryIO.MOD_ID);
	
	public static final RegistryObject<Block> DEBUG_QUARRY = registerBlock("debug_quarry", () -> new DebugQuarryBlock(), "tooltip.quarryio.debug_quarry.danger", null);
	public static final RegistryObject<Block> QUARRY = registerBlock("quarry", () -> new QuarryBlock());
	public static final RegistryObject<Block> MARKER = registerBlock("marker", () -> new BlockMarker());
	// Upgrades
	public static final RegistryObject<Block> EFFICIENCY_1_UPGRADE = registerBlock("efficiency_1", () -> new UpgradeBaseBlock() {
			@Override public UpgradeID getUpgradeID() {return UpgradeID.EFFICIENCY1;} 
		}, "tooltip.quarryio.quarry.eff1", null);
	public static final RegistryObject<Block> EFFICIENCY_2_UPGRADE = registerBlock("efficiency_2", () -> new UpgradeBaseBlock() {
			@Override public UpgradeID getUpgradeID() {return UpgradeID.EFFICIENCY2;} 
		}, "tooltip.quarryio.quarry.eff2", null);
	public static final RegistryObject<Block> EFFICIENCY_3_UPGRADE = registerBlock("efficiency_3", () -> new UpgradeBaseBlock() {
			@Override public UpgradeID getUpgradeID() {return UpgradeID.EFFICIENCY3;} 
		}, "tooltip.quarryio.quarry.eff3", null);
	public static final RegistryObject<Block> EFFICIENCY_4_UPGRADE = registerBlock("efficiency_4", () -> new UpgradeBaseBlock() {
			@Override public UpgradeID getUpgradeID() {return UpgradeID.EFFICIENCY4;} 
		}, "tooltip.quarryio.quarry.eff4", null);
	public static final RegistryObject<Block> EFFICIENCY_5_UPGRADE = registerBlock("efficiency_5", () -> new UpgradeBaseBlock() {
			@Override public UpgradeID getUpgradeID() {return UpgradeID.EFFICIENCY5;} 
		}, "tooltip.quarryio.quarry.eff5", null);
	public static final RegistryObject<Block> SILK_TOUCH_UPGRADE = registerBlock("silktouch", () -> new UpgradeBaseBlock() {
		@Override public UpgradeID getUpgradeID() {return UpgradeID.SILKTOUCH;} 
	}, "tooltip.quarryio.quarry.silk", null);
	public static final RegistryObject<Block> FORTUNE_1_UPGRADE = registerBlock("fortune_1", () -> new UpgradeBaseBlock() {
		@Override public UpgradeID getUpgradeID() {return UpgradeID.FORTUNE1;} 
	}, "tooltip.quarryio.quarry.fortune1", null);
	public static final RegistryObject<Block> FORTUNE_2_UPGRADE = registerBlock("fortune_2", () -> new UpgradeBaseBlock() {
		@Override public UpgradeID getUpgradeID() {return UpgradeID.FORTUNE2;} 
	}, "tooltip.quarryio.quarry.fortune2", null);
	public static final RegistryObject<Block> FORTUNE_3_UPGRADE = registerBlock("fortune_3", () -> new UpgradeBaseBlock() {
		@Override public UpgradeID getUpgradeID() {return UpgradeID.FORTUNE3;} 
	}, "tooltip.quarryio.quarry.fortune3", null);
	
	private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
		RegistryObject<T> blk = BLOCKS.register(name, block);
		registerBlockItem(name, blk);
		return blk;
	}
	
	private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block, String tooltip, String hovertip){
		RegistryObject<T> blk = BLOCKS.register(name, block);
		registerBlockItem(name, tooltip, hovertip, blk);
		return blk;
	}
	
	private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, String tooltip, String hovertip, RegistryObject<T> block){
		return QuarryIOItems.ITEMS.register(name, () -> new HoverableBlockItem(block.get(), new Item.Properties().tab(QuarryIO.MOD_TAB), tooltip, hovertip));
	}
	
	private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
		return QuarryIOItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(QuarryIO.MOD_TAB)));
	}
	
	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
	}
	
}
