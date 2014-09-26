package io.github.iTitus.GimmeNBT;

import java.util.ArrayList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = GimmeNBT.MOD_ID, version = GimmeNBT.VERSION, acceptableRemoteVersions = "*")
public class GimmeNBT {

	public static final String MOD_ID = "GimmeNBT";
	public static final String VERSION = "${version}";

	@Instance
	public static GimmeNBT INSTANCE;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(INSTANCE);
			FMLLog.info("%s v%s successfully loaded!", MOD_ID, VERSION);
		}
	}

	@SubscribeEvent
	public void onItemToolTipAdded(ItemTooltipEvent event) {
		if (event.showAdvancedItemTooltips
				|| Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
				|| Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {

			event.toolTip.add("");

			// ID
			event.toolTip.add("ID: "
					+ Item.itemRegistry.getNameForObject(event.itemStack
							.getItem()) + " ("
					+ Item.getIdFromItem(event.itemStack.getItem()) + ")");

			// Name
			event.toolTip.add("Name: " + event.itemStack.getUnlocalizedName());

			// Damage
			ArrayList<ItemStack> items = new ArrayList<ItemStack>(0);
			event.itemStack.getItem().getSubItems(event.itemStack.getItem(),
					CreativeTabs.tabAllSearch, items);
			int types = items.size() - 1;
			for (ItemStack stack : items)
				types = Math.max(types,
						Math.max(stack.getItemDamage(), stack.getMaxDamage()));
			if (types > 0) {
				event.toolTip.add("Damage: " + event.itemStack.getItemDamage()
						+ "/" + types);
			}

			// Max stack size
			event.toolTip.add("Max. StackSize: "
					+ event.itemStack.getMaxStackSize());

			// NBT
			NBTTagCompound nbt = event.itemStack.getTagCompound();
			if (nbt != null) {
				event.toolTip.add("NBT: " + nbt.toString());
			}
		}
	}
}
