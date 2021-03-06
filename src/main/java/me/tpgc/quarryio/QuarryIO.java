package me.tpgc.quarryio;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import me.tpgc.quarryio.blocks.QuarryIOBlocks;
import me.tpgc.quarryio.blocks.tile.QuarryIOBlockEntities;
import me.tpgc.quarryio.items.QuarryIOItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(QuarryIO.MOD_ID)
public class QuarryIO {
	
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MOD_ID = "quarryio";
	public static final int MAX_MARKERS_LENGTH = 128;
	public static final int MIN_MARKERS_LENGTH = 2;
	
	public static final CreativeModeTab MOD_TAB = new CreativeModeTab("quarryio") {
		@Override
		public ItemStack makeIcon() {
			// TODO Auto-generated method stub
			return new ItemStack(QuarryIOItems.STONE_GEAR.get());
		}
	};
	
	public QuarryIO() {
		// Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // register mod based registries
        QuarryIOItems.register(FMLJavaModLoadingContext.get().getModEventBus());
        QuarryIOBlocks.register(FMLJavaModLoadingContext.get().getModEventBus());
        
        QuarryIOBlockEntities.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
	
    @SubscribeEvent
    public void registerBlock(RegistryEvent.Register<Block> event) {
    	event.getRegistry().register(new Block(BlockBehaviour.Properties.of(Material.STONE)).setRegistryName("rocky"));
    }
    
}
