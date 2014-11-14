package io.github.iTitus.GimmeNBT.common.network.message;

import io.github.iTitus.GimmeNBT.common.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageDumpInv implements IMessage,
		IMessageHandler<MessageDumpInv, IMessage> {

	public boolean hasCustomName;
	public String inventoryOwner;
	public boolean isBlockInv;
	public ItemStack[] items;
	public String name, originName;
	public int slotNumber, stackLimit;

	public MessageDumpInv() {
	}

	public MessageDumpInv(IInventory inv, boolean isBlockInv,
			String inventoryOwner) {
		if (inv != null) {
			name = inv.getInventoryName();
			originName = inv.getClass().getName();
			hasCustomName = inv.hasCustomInventoryName();
			slotNumber = inv.getSizeInventory();
			stackLimit = inv.getInventoryStackLimit();
			items = new ItemStack[slotNumber];
			for (int i = 0; i < slotNumber; i++) {
				items[i] = inv.getStackInSlot(i);
			}
		} else {
			name = "null";
			originName = "null";
			hasCustomName = true;
			slotNumber = 0;
			stackLimit = 0;
			items = new ItemStack[0];
		}
		this.isBlockInv = isBlockInv;
		this.inventoryOwner = inventoryOwner != null ? inventoryOwner : "";
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
	public IMessage onMessage(MessageDumpInv msg, MessageContext ctx) {
		FMLClientHandler
				.instance()
				.getClientPlayerEntity()
				.addChatComponentMessage(
						Utils.makeInvDump(msg.name, msg.originName,
								msg.hasCustomName, msg.slotNumber,
								msg.stackLimit, msg.items, msg.isBlockInv,
								msg.inventoryOwner));

		return null;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, originName);
		buf.writeBoolean(hasCustomName);
		buf.writeInt(slotNumber);
		buf.writeInt(stackLimit);
		for (ItemStack item : items) {
			ByteBufUtils.writeItemStack(buf, item);
		}
		buf.writeBoolean(isBlockInv);
		ByteBufUtils.writeUTF8String(buf, inventoryOwner);
	}

}
