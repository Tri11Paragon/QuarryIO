package me.tpgc.quarryio.blocks.tile;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.tpgc.quarryio.QuarryIO;
import me.tpgc.quarryio.blocks.QuarryIOBlocks;
import me.tpgc.quarryio.blocks.UpgradeBaseBlock;
import me.tpgc.quarryio.util.NBTUtils;
import me.tpgc.quarryio.util.QuarryEnergyStorage;
import me.tpgc.quarryio.util.UpgradeID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class QuarryEntity extends BlockEntity {

	private static final int INTERNAL_ENERGY_STORAGE = 10000000;
	private static final int ENERGY_MAX_RECIEVE = 1000000;
	private static final int ENERGY_BASE_COST = 32;
	private static final NumberFormat format = NumberFormat.getInstance(Locale.CANADA);
	private static final ItemStack SILK_TOUCH_TOOL = new ItemStack(Items.NETHERITE_PICKAXE);
	private static final ItemStack FORTUNE_1_TOOL = new ItemStack(Items.NETHERITE_PICKAXE);
	private static final ItemStack FORTUNE_2_TOOL = new ItemStack(Items.NETHERITE_PICKAXE);
	private static final ItemStack FORTUNE_3_TOOL = new ItemStack(Items.NETHERITE_PICKAXE);
	
	private static final ItemStack[] fortunes = {ItemStack.EMPTY, FORTUNE_1_TOOL, FORTUNE_2_TOOL, FORTUNE_3_TOOL};
	
	// Never create lazy optionals in getCapability. Always place them as fields in the tile entity:
	
	private final QuarryEnergyStorage energyStorage = createEnergy();
	private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);
	
	private boolean foundMarkers = false;
	private BlockPos close = null;
	private BlockPos X = null;
	private BlockPos Z = null; 
	
	private double XDirection;
	private double ZDirection;
	
	private int distX, distZ;
	
	private double miningX;
	private double miningY;
	private double miningZ;
	
	private int status;
	
	private boolean finishedMining = false;
	
	private int efficiencyLevel = 0;
	private boolean silktouch = false;
	private int fortune = 0;
	
	private ArrayList<ChunkPos> loadedChunks = new ArrayList<ChunkPos>();
	
	public QuarryEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(QuarryIOBlockEntities.QUARRY_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
		SILK_TOUCH_TOOL.enchant(Enchantments.SILK_TOUCH, 1);
		FORTUNE_1_TOOL.enchant(Enchantments.BLOCK_FORTUNE, 1);
		FORTUNE_2_TOOL.enchant(Enchantments.BLOCK_FORTUNE, 2);
		FORTUNE_3_TOOL.enchant(Enchantments.BLOCK_FORTUNE, 3);
	}
	
	public void use(Player player, Direction block_facing) {
		player.sendMessage(new TextComponent("Energy: " + format.format(energyStorage.getEnergyStored()) + "/" 
			+ format.format(energyStorage.getMaxEnergyStored())), player.getUUID());
		checkForMarkers(block_facing);
		if (foundMarkers)
			player.sendMessage(new TextComponent("Boundry Established"), player.getUUID());
		if (efficiencyLevel > 0 || fortune > 0 || silktouch) {
			player.sendMessage(new TextComponent("Active Upgrades:"), player.getUUID());
			if (efficiencyLevel > 0)
				player.sendMessage(new TextComponent("Efficiency " + efficiencyLevel), player.getUUID());
			if (fortune > 0)
				player.sendMessage(new TextComponent("Fortune " + fortune), player.getUUID());
			if (silktouch)
				player.sendMessage(new TextComponent("Silktouch"), player.getUUID());
		}
		switch (status) {
		case 1:
			player.sendMessage(new TextComponent("Quarry Mining @ {" + miningX + ", " + miningY + ", " + miningZ + "}"), player.getUUID());
			break;
		case 2:
			player.sendMessage(new TextComponent("Out of energy!"), player.getUUID());
			break;
		case 3:
			player.sendMessage(new TextComponent("Finished Mining"), player.getUUID());
			break;
		case 4:
			if (!foundMarkers)
				player.sendMessage(new TextComponent("Unable to find markers. "
						+ "Please place at least one behind the quarry, one along the X and one along the Z direction!"),
						player.getUUID());
			break;
		case 5:
			player.sendMessage(new TextComponent("No empty storage blocks found! (Add chests around the quarry)"), player.getUUID());
			break;
		}
		setChanged();
	}
	
	public void blockUpdate(Direction block_facing) {
		checkForMarkers(block_facing);
	}
	
	private long tickTime = 0;
	private long checkForUpgrades = 0;
	
	public void tickServer() {
		if (!foundMarkers || finishedMining) {
			status = finishedMining ? 3 : 4;
			return;
		}
		tickTime++;
		checkForUpgrades++;
		if (level.hasNeighborSignal(worldPosition)) {
			switch (efficiencyLevel) {
			case 0:
				if (tickTime > 2) {
					performBlockMine();
					tickTime = 0;
					status = 1;
				}
				break;
			case 1:
				if (tickTime > 1) {
					performBlockMine();
					tickTime = 0;
					status = 1;
				}
				break;
			case 2:
				if (tickTime > 0) {
					performBlockMine();
					tickTime = 0;
					status = 1;
				}
				break;
			case 3:
				performBlockMine();
				tickTime = 0;
				status = 1;
				break;
			case 4:
				performBlockMine();
				performBlockMine();
				tickTime = 0;
				status = 1;
				break;
			case 5:
				performBlockMine();
				performBlockMine();
				performBlockMine();
				tickTime = 0;
				status = 1;
				break;
			}
		}
		
		if (checkForUpgrades > 20) {
			checkForUpgrades = 0;
			checkForUpgrades();
		}
	}
	
	private void checkForUpgrades() {
		efficiencyLevel = 0;
		silktouch = false;
		fortune = 0;
		for (Direction direction : Direction.values()) {
			if (level.getBlockState(worldPosition.relative(direction)).getBlock() instanceof UpgradeBaseBlock bb) {
				UpgradeID upgradeId = bb.getUpgradeID();
				if (upgradeId.getID() < 5)
					efficiencyLevel = upgradeId.getID() + 1;
				if (upgradeId.getID() == 5)
					silktouch = true;
				if (upgradeId.getID() > 5 && !silktouch) {
					fortune = upgradeId.getID() - 5;
				}
			}
		}
	}
	
	private void performBlockMine() {
		BlockPos pos = new BlockPos(miningX, miningY, miningZ);
		BlockState currentMiningBlockstate = level.getBlockState(pos);
		Block currentMiningBlock = currentMiningBlockstate.getBlock();
		
		int blockEnergyCost = getBlockEnergyCost(currentMiningBlock);
		if (!energyStorage.consumeEnergy(blockEnergyCost)) {
			status = 2;
			return;
		}
		// if we can mine this block, then we can go down
		int blockMineStatus = mineBlock(pos, currentMiningBlockstate);
		if (blockMineStatus < 0) {
			status = 5;
			return;
		}
		if (blockMineStatus > 0) {
			miningY--;
		} else {
			// but if we can't mine the block, we've hit bedrock.
			// So, goto the next mining position.
			miningX += XDirection;
			// if the mining is outside bounds, reset and move in the Z direction
			if (miningX > close.getX() + distX || miningX < close.getX() - distX) {
				miningX = close.getX() + XDirection;
				miningZ += ZDirection;
				if (miningZ > close.getZ() + distZ || miningZ < close.getZ() - distZ) {
					finishedMining = true;
				}
			}
			miningY = close.getY();
				//miningY = level.getHeightmapPos(Types.WORLD_SURFACE_WG, new BlockPos(miningX, 0, miningZ)).getY();
		}
	}
	
	private boolean sendItemsToNearbyContainers(List<ItemStack> stackerino) {
		for (Direction direction : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
            if (be != null) {
            	boolean ableToInsertAll = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).map(handler -> {
            		int slots = handler.getSlots();
            		itemStackSearch: 
            			for (int s = 0; s < stackerino.size(); s++) {
		            		int itemsLeft = stackerino.get(s).getCount();
		            		for (int i = 0; i < slots; i++) {
		            			ItemStack leftOvers = handler.insertItem(i, stackerino.get(s), false);
		            			// if we successfully extracted all the times, we can return
		            			if (leftOvers.isEmpty()) {
		            				// I have a feeling that using labels is a sign of bad design
		            				itemsLeft = 0;
		            				continue itemStackSearch;
		            			}
		            			// if the returned stack has the same amount of items as we have
		            			// then we know that slot couldn't be inserted into.
		            			if (leftOvers.getCount() == itemsLeft)
		            				continue;
		            			else {
		            				// if it wasn't the same amount, some was transfered.
		            				// then reduce to the amount we got left to transfer.
		            				itemsLeft = leftOvers.getCount();
		            			}
		            		}
		            		if (itemsLeft > 0)
		            			return false;
            			}
            		return true;
            	}).orElse(false);
            	return ableToInsertAll;
            }
		}
		return false;
	}
	
	public void animateTick() {
		// this is going to require some networking
		// something im not interested in doing currently
//		RenderSystem.depthMask(false);
//		
//		Minecraft mc = Minecraft.getInstance();
//		
//		RenderSystem.lineWidth(30.0f);
//		
//		// Get vertex builder
//		BufferSource irendertypebuffer1 = mc.renderBuffers().bufferSource();
//		VertexConsumer vb = irendertypebuffer1.getBuffer(RenderType.lines());
//		
//		// Color of the bound (White)
//		Color c = Color.BLACK;
//		// Split up in red, green and blue and transform it to 0.0 - 1.0
//		float red = c.getRed() / 255.0f;
//		float green = c.getGreen() / 255.0f;
//		float blue = c.getBlue() / 255.0f;
//		if (X != null && Z != null)
//			LevelRenderer.renderLineBox(vb, X.getX(), X.getY(), X.getZ(), 5, 5, 5, red, green, blue, 1.0f);
//		
//		RenderSystem.depthMask(true);
	}
	
	private int getBlockEnergyCost(Block b) {
		return (int) (ENERGY_BASE_COST * b.defaultDestroyTime());
	}
	
	private int mineBlock(BlockPos pos, BlockState b) {
		if (b.getBlock().defaultDestroyTime() < 0)
			return 0;
		
		List<ItemStack> minedBlockDrops = getDrops(level.getBlockState(pos), (ServerLevel) level, worldPosition, null);
		
		if (minedBlockDrops.size() > 0 && !sendItemsToNearbyContainers(minedBlockDrops))
			return -1;
		
		level.destroyBlock(pos, false);
		
		//itemHandler.insertItem(0, new ItemStack(b.getBlock()), false);
		return 1;
	}
	
	public List<ItemStack> getDrops(BlockState pState, ServerLevel pLevel, BlockPos pPos, @Nullable BlockEntity pBlockEntity) {
		if (silktouch) {
			LootContext.Builder lootcontext$builder = 
					(new LootContext.Builder(pLevel))
						.withRandom(pLevel.random)
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pPos))
						.withParameter(LootContextParams.TOOL, SILK_TOUCH_TOOL)
						.withOptionalParameter(LootContextParams.BLOCK_ENTITY, pBlockEntity)
						.withLuck(fortune);
			return pState.getDrops(lootcontext$builder);
		} else {
			LootContext.Builder lootcontext$builder = 
					(new LootContext.Builder(pLevel))
						.withRandom(pLevel.random)
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pPos))
						.withParameter(LootContextParams.TOOL, fortunes[fortune])
						.withOptionalParameter(LootContextParams.BLOCK_ENTITY, pBlockEntity);
			return pState.getDrops(lootcontext$builder);
		}
	}
	
	private void checkForMarkers(Direction block_facing) {
		BlockPos firstCheckPos = worldPosition.relative(block_facing.getOpposite());
		if (level.getBlockState(firstCheckPos).getBlock() == QuarryIOBlocks.MARKER.get())
			close = firstCheckPos;
		else {
			foundMarkers = false;
			status = 4;
			return;
		}
		boolean found = false;
		for (int x = QuarryIO.MIN_MARKERS_LENGTH; x < QuarryIO.MAX_MARKERS_LENGTH; x++) {
			if (level.getBlockState((X = close.relative(Axis.X, (int) (x * (XDirection = 1))))).getBlock() == QuarryIOBlocks.MARKER.get()
					|| level.getBlockState((X = close.relative(Axis.X, (int) (x * (XDirection = -1))))).getBlock() == QuarryIOBlocks.MARKER.get()) {
				found = true;
				break;
			}
		}
		if (!found) {
			foundMarkers = false;
			status = 4;
			return;
		}
		found = false;
		for (int z = QuarryIO.MIN_MARKERS_LENGTH; z < QuarryIO.MAX_MARKERS_LENGTH; z++) {
			if (level.getBlockState((Z = close.relative(Axis.Z, (int) (z * (ZDirection = 1))))).getBlock() == QuarryIOBlocks.MARKER.get()
					|| level.getBlockState((Z = close.relative(Axis.Z, (int) (z * (ZDirection = -1))))).getBlock() == QuarryIOBlocks.MARKER.get()) {
				found = true;
				break;
			}
		}
		if (!found) {
			foundMarkers = false;
			status = 4;
			return;
		}
		distX = close.distManhattan(X)-1;
		distZ = close.distManhattan(Z)-1;
		if (!foundMarkers) {
			miningX = close.getX() + XDirection;
			miningZ = close.getZ() + ZDirection;
			miningY = level.getHeightmapPos(Types.WORLD_SURFACE, new BlockPos(miningX, 0, miningZ)).getY();
			updateChunkys();
		}
		foundMarkers = true;
		finishedMining = false;
	}
	
	public void remove() {
		ServerLevel sl = ((ServerLevel) (this.level));
		for (ChunkPos p : loadedChunks) {
			sl.setChunkForced(p.x, p.z, false);
		}
	}
	
	private void updateChunkys() {
		ServerLevel sl = ((ServerLevel) (this.level));
		if (loadedChunks.size() > 0) {
			for (ChunkPos p : loadedChunks) {
				sl.setChunkForced(p.x, p.z, false);
			}
		}
		// hey if minecraft isn't going to memory efficient, why should i?
		loadedChunks = new ArrayList<ChunkPos>();
		ChunkPos base = this.level.getChunk(this.worldPosition).getPos();
		
		for (int i = 0; i < distX/16; i++) {
			for (int j = 0; j < distZ/16; j++) {
				ChunkPos currentPos = new ChunkPos((int)(base.x + (i * XDirection)), (int)(base.z + (j * ZDirection)));
				loadedChunks.add(currentPos);
				sl.setChunkForced(currentPos.x, currentPos.z, true);
			}
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		pTag.put("Energy", energyStorage.serializeNBT());
		
		if (X != null && Z != null && close != null) {
			pTag.put("CloseMarker", NBTUtils.blockPosToTag(close));
			pTag.put("XMarker", NBTUtils.blockPosToTag(X));
			pTag.put("ZMarker", NBTUtils.blockPosToTag(Z));
		}
		pTag.putBoolean("FoundMarkers", foundMarkers);
		pTag.putDouble("MiningDirectionX", XDirection);
		pTag.putDouble("MiningDirectionZ", ZDirection);
		pTag.putDouble("DistX", distX);
		pTag.putDouble("DistZ", distZ);
		pTag.putDouble("MiningX", miningX);
		pTag.putDouble("MiningY", miningY);
		pTag.putDouble("MiningZ", miningZ);
		pTag.putDouble("Status", status);
		pTag.putBoolean("FinishedMining", finishedMining);
		
		if (loadedChunks.size() > 0) {
			pTag.put("Chunks", NBTUtils.writeChunkList(loadedChunks));
		}
		
		super.saveAdditional(pTag);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		if (pTag.contains("Energy")) {
            energyStorage.deserializeNBT(pTag.get("Energy"));
        }
		if (pTag.contains("Chunks")) {
			loadedChunks = NBTUtils.readChunkList(pTag.getCompound("Chunks"));
		}
		
		if (pTag.contains("CloseMarker")) {close = NBTUtils.tagToBlockPos(pTag.getCompound("CloseMarker"));}
		if (pTag.contains("XMarker")) {X = NBTUtils.tagToBlockPos(pTag.getCompound("XMarker"));}
		if (pTag.contains("ZMarker")) {Z = NBTUtils.tagToBlockPos(pTag.getCompound("ZMarker"));}
		if (pTag.contains("FoundMarkers")) {foundMarkers = pTag.getBoolean("FoundMarkers");}
		if (pTag.contains("MiningDirectionX")) {XDirection = pTag.getDouble("MiningDirectionX");}
		if (pTag.contains("MiningDirectionZ")) {ZDirection = pTag.getDouble("MiningDirectionZ");}
		if (pTag.contains("DistX")) {distX = (int) pTag.getDouble("DistX");}
		if (pTag.contains("DistZ")) {distZ = (int) pTag.getDouble("DistZ");}
		if (pTag.contains("MiningX")) {miningX = pTag.getDouble("MiningX");}
		if (pTag.contains("MiningY")) {miningY = pTag.getDouble("MiningY");}
		if (pTag.contains("MiningZ")) {miningZ = pTag.getDouble("MiningZ");}
		if (pTag.contains("Status")) {status = (int) pTag.getDouble("Status");}
		if (pTag.contains("FinishedMining")) {finishedMining = pTag.getBoolean("FinishedMining");}
		
		super.load(pTag);
	}
	
	@Override
	public void setRemoved() {
		super.setRemoved();
		energy.invalidate();
//		if (loadedChunks.size() > 0) {
//			for (ChunkPos p : loadedChunks) {
//				((ServerLevel) level).setChunkForced(p.x, p.z, false);
//			}
//		}
	}
	
	@SuppressWarnings("unused")
	private ItemStackHandler createHandler() {
        return new ItemStackHandler(8) {

            @Override
            protected void onContentsChanged(int slot) {
                // To make sure the TE persists when the chunk is saved later we need to
                // mark it dirty every time the item handler changes
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            	return stack;
            }
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
            	return super.extractItem(slot, amount, simulate);
            }
        };
    }
	
	private QuarryEnergyStorage createEnergy() {
        return new QuarryEnergyStorage(INTERNAL_ENERGY_STORAGE, ENERGY_MAX_RECIEVE) {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
            	int rec = super.receiveEnergy(maxReceive, simulate);
                setChanged();
				return rec;
            }
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
            	int rec = super.extractEnergy(maxExtract, simulate);
            	setChanged();
            	return rec;
            }
            @Override
            public boolean consumeEnergy(int amount) {
            	boolean t = super.consumeEnergy(amount);
            	setChanged();
            	return t;
            }
        };
    }
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//            return handler.cast();
//        }
		if (cap == CapabilityEnergy.ENERGY) {
			return energy.cast();
		}
		return super.getCapability(cap, side);
	}
	
	
}
