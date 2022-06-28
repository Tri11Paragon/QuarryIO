package me.tpgc.quarryio.blocks.tile;

import me.tpgc.quarryio.QuarryIO;
import me.tpgc.quarryio.blocks.QuarryIOBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class QuarryIOBlockEntities {
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, QuarryIO.MOD_ID);
	
	public static final RegistryObject<BlockEntityType<DebugQuarryEntity>> DEBUG_QUARRY_BLOCK_ENTITY = BLOCK_ENTITIES.register("debug_quarry_block_entity", 
			() -> BlockEntityType.Builder.of(DebugQuarryEntity::new, QuarryIOBlocks.DEBUG_QUARRY.get()).build(null));
	public static final RegistryObject<BlockEntityType<QuarryEntity>> QUARRY_BLOCK_ENTITY = BLOCK_ENTITIES.register("quarry_block_entity", 
			() -> BlockEntityType.Builder.of(QuarryEntity::new, QuarryIOBlocks.QUARRY.get()).build(null));
	
	public static void register(IEventBus bus) {
		BLOCK_ENTITIES.register(bus);
	}
	
}
