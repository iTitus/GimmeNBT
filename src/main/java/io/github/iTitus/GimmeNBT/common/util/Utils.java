package io.github.iTitus.GimmeNBT.common.util;

import io.github.iTitus.GimmeNBT.common.lib.LibMod;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;

public final class Utils {

	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd_HH.mm.ss");

	private static final String NEW_LINE = UUID.randomUUID().toString();

	private static File getDumpFile(String name, boolean isBlockInv,
			String inventoryOwner) {

		String fileName = String.format("inv_dump_%s_%s_%s_%s", name,
				(isBlockInv ? "@" : "") + inventoryOwner, FMLCommonHandler
						.instance().getEffectiveSide(),
				dateFormat.format(new Date()));

		int i = 1;
		while (true) {
			File saveFile = new File(getSaveFolder(), fileName
					+ (i == 1 ? "" : "_" + i) + ".txt");

			if (!saveFile.exists()) {
				return saveFile;
			}

			i++;
		}
	}

	public static List<String> getFormattedNBT(NBTTagCompound nbt) {
		String nbtString = nbt.toString();
		nbtString = nbtString.replace("{", "{" + NEW_LINE)
				.replace("[", "[" + NEW_LINE).replace(",", "," + NEW_LINE);
		return Arrays.asList(nbtString.split(NEW_LINE));
	}

	public static int getMaxDamage(ItemStack stack) {

		if (stack == null || stack.getItem() == null || stack.stackSize == 0)
			return 0;

		ArrayList<ItemStack> items = new ArrayList<ItemStack>(0);
		int types = 0;

		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			stack.getItem().getSubItems(
					stack.getItem(),
					(stack.getItem().getCreativeTab() != null ? stack.getItem()
							.getCreativeTab() : CreativeTabs.tabAllSearch),
					items);
			for (ItemStack possibleStack : items) {
				types = Math.max(types, possibleStack.getMaxDamage());
			}
		}

		types = Math.max(Math.max(types, items.size() - 1),
				stack.getMaxDamage());

		return types;
	}

	public static String getMaxDamageString(ItemStack stack) {

		if (stack == null || stack.getItem() == null || stack.stackSize == 0)
			return "";

		int types = getMaxDamage(stack);

		if (FMLCommonHandler.instance().getEffectiveSide().isServer()
				&& stack.getHasSubtypes()) {
			return "";
		}

		return " / " + types;
	}

	private static File getSaveFolder() {

		File saveFolder;

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			saveFolder = new File(Minecraft.getMinecraft().mcDataDir,
					LibMod.MOD_ID);
		else
			saveFolder = MinecraftServer.getServer().getFile(LibMod.MOD_ID);

		if (saveFolder != null && !saveFolder.exists())
			saveFolder.mkdir();

		return saveFolder;
	}

	public static IChatComponent makeInvDump(IInventory inv,
			boolean isBlockInv, String inventoryOwner) {

		if (inv == null)
			return makeInvDump("null", "null", true, 0, 0, new ItemStack[0],
					isBlockInv, inventoryOwner != null ? inventoryOwner
							: "null");
		ItemStack[] items = new ItemStack[inv.getSizeInventory()];
		for (int i = 0; i < items.length; i++) {
			items[i] = inv.getStackInSlot(i);
		}
		return makeInvDump(inv.getInventoryName(), inv.getClass().getName(),
				inv.hasCustomInventoryName(), inv.getSizeInventory(),
				inv.getInventoryStackLimit(), items, isBlockInv, inventoryOwner);

	}

	public static IChatComponent makeInvDump(String name, String originName,
			boolean hasCustomName, int slotNumber, int stackLimit,
			ItemStack[] items, boolean isBlockInv, String inventoryOwner) {

		boolean success = true;

		if (StringUtils.isNullOrEmpty(name)
				|| StringUtils.isNullOrEmpty(originName) || items == null
				|| items.length != slotNumber
				|| StringUtils.isNullOrEmpty(inventoryOwner))
			success = false;

		PrintWriter writer = null;
		File saveFile = null, tmpFile = null;

		if (success) {

			try {
				saveFile = getDumpFile(name, isBlockInv, inventoryOwner);
				tmpFile = new File(saveFile.getAbsolutePath() + "_tmp");

				if (saveFile.exists())
					saveFile.delete();
				if (tmpFile.exists())
					tmpFile.delete();

				tmpFile.createNewFile();
				writer = new PrintWriter(tmpFile);

				String locName = hasCustomName ? name : StatCollector
						.translateToLocal(name) + " (" + name + ")";
				writer.println("### Name: " + locName + " - From class: "
						+ originName + " - Slots: " + slotNumber
						+ " - Stacklimit: " + stackLimit + " ###");
				writer.println();

				for (int i = 0; i < slotNumber; i++) {
					ItemStack stack = items[i];
					writer.println("Slot " + i + ":  "
							+ (stack == null ? "Empty" : "Full"));
					if (stack != null) {

						// Display Name
						writer.println(String.format("Name: %s",
								stack.getDisplayName()));

						// StackSize
						writer.println(String.format("StackSize: %d / %d",
								stack.stackSize, stack.getMaxStackSize()));

						// Damage
						writer.println(String.format("Damage: %d%s",
								stack.getItemDamage(),
								getMaxDamageString(stack)));

						// ID
						writer.println(String.format("ID: %s (%d)",
								Item.itemRegistry.getNameForObject(stack
										.getItem()), Item.getIdFromItem(stack
										.getItem())));

						// Unl. Name
						writer.println(String.format("Unlocalized Name: %s",
								stack.getUnlocalizedName()));

						// NBT
						NBTTagCompound nbt = stack.getTagCompound();
						if (nbt != null) {
							writer.println(StatCollector
									.translateToLocal("tooltip.gimmenbt.nbt"));
							for (String str : getFormattedNBT(nbt))
								writer.println(str);
						}
					}

					writer.println();

				}

				writer.print("############################");

			} catch (Throwable t) {
				FMLLog.warning("Could not save dump file: %s", t);
				t.printStackTrace();
				success = false;
			} finally {
				try {
					if (writer != null)
						writer.close();
					if (tmpFile != null && saveFile != null) {
						if (success)
							tmpFile.renameTo(saveFile);
						tmpFile.delete();
					}
				} catch (Throwable t) {
					FMLLog.warning(
							"Could not finish to save the dump file: %s", t);
					t.printStackTrace();
					success = false;
				}
			}
		}

		if (success) {
			ChatComponentText fileLink = new ChatComponentText(
					saveFile.getName());
			fileLink.getChatStyle()
					.setChatClickEvent(
							new ClickEvent(Action.OPEN_FILE, saveFile
									.getAbsolutePath()));
			fileLink.getChatStyle().setUnderlined(Boolean.TRUE);
			if (isBlockInv)
				return new ChatComponentTranslation(
						"commands.gimmenbt.dump_inv.te.success",
						inventoryOwner, fileLink);
			return new ChatComponentTranslation(
					"commands.gimmenbt.dump_inv.player.success",
					inventoryOwner, fileLink);
		} else {
			if (isBlockInv)
				return new ChatComponentTranslation(
						"commands.gimmenbt.dump_inv.te.failed", inventoryOwner);
			return new ChatComponentTranslation(
					"commands.gimmenbt.dump_inv.player.failed", inventoryOwner);
		}

	}

	private Utils() {
	}

}
