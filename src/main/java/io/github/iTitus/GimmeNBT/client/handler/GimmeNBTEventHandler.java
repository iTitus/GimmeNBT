package io.github.iTitus.GimmeNBT.client.handler;

import io.github.iTitus.GimmeNBT.common.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GimmeNBTEventHandler {

    @SubscribeEvent
    public void onItemToolTipAdded(ItemTooltipEvent event) {
        if (event.showAdvancedItemTooltips || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {

            event.toolTip.add("");

            // ID
            event.toolTip.add(StatCollector.translateToLocalFormatted("tooltip.gimmenbt.id", Item.itemRegistry.getNameForObject(event.itemStack.getItem()), Item.getIdFromItem(event.itemStack.getItem())));

            // Name
            event.toolTip.add(StatCollector.translateToLocalFormatted("tooltip.gimmenbt.unlocalizedName", event.itemStack.getUnlocalizedName()));

            // Damage
            int types = Utils.getMaxDamage(event.itemStack);
            if (types > 0) {
                event.toolTip.add(StatCollector.translateToLocalFormatted("tooltip.gimmenbt.damage", event.itemStack.getItemDamage(), types));
            }

            // Max stack size
            event.toolTip.add(StatCollector.translateToLocalFormatted("tooltip.gimmenbt.maxStackSize", event.itemStack.getMaxStackSize()));

            // NBT
            NBTTagCompound nbt = event.itemStack.getTagCompound();
            if (nbt != null) {
                event.toolTip.add(StatCollector.translateToLocal("tooltip.gimmenbt.nbt"));
                event.toolTip.addAll(Utils.getFormattedNBT(nbt));
            }
        }
    }

}
