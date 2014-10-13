package io.github.iTitus.GimmeNBT.util;

import io.github.iTitus.GimmeNBT.GimmeNBT;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Utils {

	/**
	 * Totally not random ;)
	 */
	private static final String NEW_LINE = "_aSdTrGhUzFxLINEBREAKgHgZuGhGz_";
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd_HH.mm.ss");

	public static List<String> getFormattedNBT(NBTTagCompound nbt) {
		String nbtString = nbt.toString();
		nbtString = nbtString.replace("{", "{" + NEW_LINE)
				.replace("[", "[" + NEW_LINE).replace(",", "," + NEW_LINE);
		return Arrays.asList(nbtString.split(NEW_LINE));
	}

	public static int getMaxDamage(ItemStack stack) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>(0);
		stack.getItem().getSubItems(stack.getItem(), CreativeTabs.tabAllSearch,
				items);

		int types = 0;
		for (ItemStack possibleStack : items) {
			types = Math.max(
					types,
					Math.max(possibleStack.getItemDamage(),
							possibleStack.getMaxDamage()));
		}
		types = Math.max(types, items.size() - 1);

		return types;
	}

	public static boolean makeInvDump(String name, String originName,
			boolean hasCustomName, int slotNumber, int stackLimit,
			ItemStack[] items, boolean isBlockInv, String inventoryOwner) {

		if (StringUtils.isNullOrEmpty(name)
				|| StringUtils.isNullOrEmpty(originName) || items == null
				|| items.length != slotNumber
				|| StringUtils.isNullOrEmpty(inventoryOwner))
			return false;

		boolean success = true;

		PrintWriter writer = null;
		File saveFile = null, tmpFile = null;

		try {
			saveFile = getDumpFile(name, isBlockInv, inventoryOwner);
			tmpFile = new File(saveFile.getAbsolutePath() + "_tmp");

			System.out.println(saveFile);

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
					writer.println(String.format("StackSize: %d/%d",
							stack.stackSize, stack.getMaxStackSize()));

					// Damage
					writer.println(String.format("Damage: %d/%d",
							stack.getItemDamage(), getMaxDamage(stack)));

					// ID
					writer.println(String
							.format("ID: %s (%d)", Item.itemRegistry
									.getNameForObject(stack.getItem()), Item
									.getIdFromItem(stack.getItem())));

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
			t.printStackTrace();
			success = false;
		} finally {
			if (writer != null)
				writer.close();
			if (tmpFile != null && saveFile != null) {
				if (success)
					tmpFile.renameTo(saveFile);
				tmpFile.delete();
			}
		}

		return success;

	}

	private static File getDumpFile(String name, boolean isBlockInv,
			String inventoryOwner) {

		String fileName = String.format("inv_dump_%s_%s_%s", name,
				(isBlockInv ? "@" + inventoryOwner : inventoryOwner),
				dateFormat.format(new Date()).toString());
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

	private static File getSaveFolder() {

		File saveFolder = null;

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			saveFolder = new File(Minecraft.getMinecraft().mcDataDir,
					GimmeNBT.MOD_ID);
		else
			saveFolder = MinecraftServer.getServer().getFile(GimmeNBT.MOD_ID);

		if (saveFolder != null && !saveFolder.exists())
			saveFolder.mkdir();

		return saveFolder;
	}

	public static boolean makeInvDump(IInventory inv, boolean isBlockInv,
			String inventoryOwner) {
		if (inv == null)
			return false;
		ItemStack[] items = new ItemStack[inv.getSizeInventory()];
		for (int i = 0; i < items.length; i++) {
			items[i] = inv.getStackInSlot(i);
		}
		return makeInvDump(inv.getInventoryName(), inv.getClass().getName(),
				inv.hasCustomInventoryName(), inv.getSizeInventory(),
				inv.getInventoryStackLimit(), items, isBlockInv, inventoryOwner);
	}

}
