package me.tpgc.quarryio.items;

import me.tpgc.quarryio.QuarryIO;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class QuarryIOItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, QuarryIO.MOD_ID);
	
	public static final RegistryObject<Item> WOODEN_GEAR = ITEMS.register("woodgear", () -> new Item(new Item.Properties().tab(QuarryIO.MOD_TAB)));
	public static final RegistryObject<Item> STONE_GEAR = ITEMS.register("stonegear", () -> new Item(new Item.Properties().tab(QuarryIO.MOD_TAB)));
	public static final RegistryObject<Item> IRON_GEAR = ITEMS.register("irongear", () -> new Item(new Item.Properties().tab(QuarryIO.MOD_TAB)));
	public static final RegistryObject<Item> GOLD_GEAR = ITEMS.register("goldgear", () -> new Item(new Item.Properties().tab(QuarryIO.MOD_TAB)));
	public static final RegistryObject<Item> DIAMOND_GEAR = ITEMS.register("diamondgear", () -> new Item(new Item.Properties().tab(QuarryIO.MOD_TAB)));
	
	public static void register(IEventBus bus) {
		ITEMS.register(bus);
	}
	
}
