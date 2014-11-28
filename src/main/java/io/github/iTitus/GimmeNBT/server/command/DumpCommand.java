package io.github.iTitus.GimmeNBT.server.command;

import io.github.iTitus.GimmeNBT.common.network.MessageHandler;
import io.github.iTitus.GimmeNBT.common.network.message.MessageDumpInv;
import io.github.iTitus.GimmeNBT.common.util.Utils;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class DumpCommand extends CommandBase {

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length != 1 ? null : getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    @Override
    public String getCommandName() {
        return "dump_inv";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.gimmenbt.dump_inv.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length == 1 && index == 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length > 3 || args.length == 2) {
            throw new WrongUsageException("commands.gimmenbt.dump_inv.usage");
        } else {

            if (args.length == 0 || args.length == 1) {

                EntityPlayerMP player;

                if (args.length == 0) {
                    player = getCommandSenderAsPlayer(sender);
                } else {
                    player = getPlayer(sender, args[0]);
                }

                if (player == null) {
                    throw new PlayerNotFoundException();
                }

                if (sender instanceof EntityPlayerMP) {
                    MessageHandler.INSTANCE.sendTo(new MessageDumpInv(player.inventory, false, player.getCommandSenderName()), getCommandSenderAsPlayer(sender));
                } else {
                    sender.addChatMessage(Utils.makeInvDump(player.inventory, false, player.getCommandSenderName()));
                }

            } else {

                int x = MathHelper.floor_double(func_110666_a(sender, sender.getPlayerCoordinates().posX, args[0]));
                int y = MathHelper.floor_double(func_110666_a(sender, sender.getPlayerCoordinates().posY, args[1]));
                int z = MathHelper.floor_double(func_110666_a(sender, sender.getPlayerCoordinates().posZ, args[2]));

                World w = sender.getEntityWorld();
                TileEntity tile = w.getTileEntity(x, y, z);

                if (tile != null && tile instanceof IInventory) {
                    if (sender instanceof EntityPlayerMP) {
                        MessageHandler.INSTANCE.sendTo(new MessageDumpInv((IInventory) tile, true, String.format("%d,  %d, %d", x, y, z)), getCommandSenderAsPlayer(sender));
                    } else {
                        sender.addChatMessage(Utils.makeInvDump((IInventory) tile, true, String.format("%d, %d, %d", x, y, z)));
                    }
                } else {
                    func_152373_a(sender, this, "commands.gimmenbt.dump_inv.te.noInv", String.format("%d,  %d, %d", x, y, z));
                }

            }
        }
    }

}
