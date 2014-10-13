package io.github.iTitus.GimmeNBT.network.message;

import io.github.iTitus.GimmeNBT.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageDumpInv implements IMessage,
		IMessageHandler<MessageDumpInv, IMessage> {

	public String name, originName;
	public boolean hasCustomName;
	public int slotNumber, stackLimit;
	public ItemStack[] items;
	public boolean isBlockInv;
	public String inventoryOwner;

	public MessageDumpInv() {
	}

	public MessageDumpInv(IInventory inv, boolean isBlockInv,
			String inventoryOwner) {
		name = inv.getInventoryName();
		originName = inv.getClass().getName();
		hasCustomName = inv.hasCustomInventoryName();
		slotNumber = inv.getSizeInventory();
		stackLimit = inv.getInventoryStackLimit();
		items = new ItemStack[slotNumber];
		for (int i = 0; i < slotNumber; i++) {
			items[i] = inv.getStackInSlot(i);
		}
		this.isBlockInv = isBlockInv;
		this.inventoryOwner = inventoryOwner;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		originName = ByteBufUtils.readUTF8String(buf);
		hasCustomName = buf.readBoolean();
		slotNumber = buf.readInt();
		stackLimit = buf.readInt();
		items = new ItemStack[slotNumber];
		for (int i = 0; i < items.length; i++) {
			items[i] = ByteBufUtils.readItemStack(buf);
		}
		isBlockInv = buf.readBoolean();
		inventoryOwner = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, originName);
		buf.writeBoolean(hasCustomName);
		buf.writeInt(slotNumber);
		buf.writeInt(stackLimit);
		for (int i = 0; i < items.length; i++) {
			ByteBufUtils.writeItemStack(buf, items[i]);
		}
		buf.writeBoolean(isBlockInv);
		ByteBufUtils.writeUTF8String(buf, inventoryOwner);
	}

	@Override
	public IMessage onMessage(MessageDumpInv msg, MessageContext ctx) {

		ChatComponentText text = null;

		if (Utils.makeInvDump(msg.name, msg.originName, msg.hasCustomName,
				msg.slotNumber, msg.stackLimit, msg.items, msg.isBlockInv, msg.inventoryOwner)) {
			if (msg.isBlockInv)
				text = new ChatComponentText(
						StatCollector.translateToLocalFormatted(
								"commands.gimmenbt.dump_inv.te.success",
								msg.inventoryOwner));
			else
				text = new ChatComponentText(
						StatCollector.translateToLocalFormatted(
								"commands.gimmenbt.dump_inv.player.success",
								msg.inventoryOwner));
		} else {
			if (msg.isBlockInv)
				text = new ChatComponentText(
						StatCollector.translateToLocalFormatted(
								"commands.gimmenbt.dump_inv.te.failed",
								msg.inventoryOwner));
			else
				text = new ChatComponentText(
						StatCollector.translateToLocalFormatted(
								"commands.gimmenbt.dump_inv.player.failed",
								msg.inventoryOwner));
		}

		FMLClientHandler.instance().getClientPlayerEntity()
				.addChatComponentMessage(text);

		return null;
	}

}
