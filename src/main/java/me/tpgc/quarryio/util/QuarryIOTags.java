package me.tpgc.quarryio.util;

import me.tpgc.quarryio.QuarryIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class QuarryIOTags {
	
	public static class Blocks {

		private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(QuarryIO.MOD_ID, name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Items {
    	public static final TagKey<Item> WOODGEARS = forgeTag("gears/wood");
    	public static final TagKey<Item> STONEGEARS = forgeTag("gears/stone");
    	public static final TagKey<Item> IRONGEARS = forgeTag("gears/iron");
    	public static final TagKey<Item> GOLDGEARS = forgeTag("gears/gold");
    	public static final TagKey<Item> DIAMONDGEARS = forgeTag("gears/diamond");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(QuarryIO.MOD_ID, name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }
	
}
