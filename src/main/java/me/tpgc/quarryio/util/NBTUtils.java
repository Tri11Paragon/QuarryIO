package me.tpgc.quarryio.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;

public class NBTUtils {
	
	public static CompoundTag blockPosToTag(BlockPos pos) {
		if (pos == null)
			return null;
		CompoundTag tag = new CompoundTag();
		tag.putDouble("X", pos.getX());
		tag.putDouble("Y", pos.getY());
		tag.putDouble("Z", pos.getZ());
		return tag;
	}
	
	public static BlockPos tagToBlockPos(CompoundTag tag) {
		return new BlockPos(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"));
	}
	
	public static CompoundTag writeChunkList(List<ChunkPos> list) {
		if (list == null)
			throw new IllegalStateException("Supplied List cannot be NULL");
		
		CompoundTag tag = new CompoundTag();
		tag.putDouble("Size", list.size());
		for (int i = 0; i < list.size(); i++) {
			tag.putDouble("IX" + i, list.get(i).x);
			tag.putDouble("IZ" + i, list.get(i).z);
		}
		return tag;
	}
	
	public static ArrayList<ChunkPos> readChunkList(CompoundTag tag) {
		ArrayList<ChunkPos> list = new ArrayList<ChunkPos>();
		int size = (int) tag.getDouble("Size");
		for (int i = 0; i < size; i++) {
			int x = (int) tag.getDouble("IX" + i);
			int z = (int) tag.getDouble("IZ" + i);
			list.add(new ChunkPos(x, z));
		}
		return list;
	}
	
}
