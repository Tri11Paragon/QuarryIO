package me.tpgc.quarryio.items;

import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class HoverableBlockItem extends BlockItem {

	// §
	
	private String normalToolTip = null;
	private String shiftToolTip = null;
	
	public HoverableBlockItem(Block pBlock, Properties pProperties, String normalToolTip, String shiftToolTip) {
		super(pBlock, pProperties);
		this.normalToolTip = normalToolTip;
		this.shiftToolTip = shiftToolTip;
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
		if (Screen.hasShiftDown()) {
			if (shiftToolTip != null)
				pTooltip.add(new TranslatableComponent(shiftToolTip));
		} else {
			if (normalToolTip != null)
				pTooltip.add(new TranslatableComponent(normalToolTip));
		}
	}

}
