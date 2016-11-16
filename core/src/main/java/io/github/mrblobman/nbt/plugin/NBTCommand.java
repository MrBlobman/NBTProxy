/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 MrBlobman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.mrblobman.nbt.plugin;

import io.github.mrblobman.nbt.NBTCompoundTag;
import io.github.mrblobman.nbt.NBTException;
import io.github.mrblobman.nbt.NBTIODelegate;
import io.github.mrblobman.nbt.TagFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Provide command access to all of the {@link NBTIODelegate}s. Each command must
 * be enabled in the {@code config.yml} before it can be used and form there a
 * user needs the permissions described in {@link NBTProxy.Permissions}.
 * <br>
 * Argument grammar:
 * <pre>
 *     nbt_query := query_type query_target
 *     query_type := 'get' | 'set' nbt_data | 'add' nbt_data.
 *     query_target := 'item' | 'block' | 'entity' | 'player' name | 'file' path.
 * </pre>
 */
public class NBTCommand implements CommandExecutor {
    private TagFactory tagFactory;
    private String errorMessage;

    private final int enabledCommands;

    public NBTCommand(int enabledCommands) {
        this.enabledCommands = enabledCommands;
        try {
            this.tagFactory = TagFactory.get();
        } catch (UnsupportedOperationException e) {
            this.tagFactory = null;
            this.errorMessage = e.getMessage();
        }
    }

    private static void printError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + "NBTProxy Error: " + message);
    }

    private static void printSuccess(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + "NBTProxy Success: " + message);
    }

    private boolean checkPermission(CommandSender sender, NBTProxy.Permissions permission) {
        if (permission == null) {
            printError(sender, "Internal error unknown permission.");
            return false;
        }
        if ((this.enabledCommands & permission.PERM_ENABLED_FLAG) == 0) {
            printError(sender, "Command disabled");
            return false;
        }

        if (!sender.hasPermission(permission.PERMISSION)) {
            printError(sender, "You don't have the " + permission.PERMISSION + " permission to execute this command.");
            return false;
        }

        return true;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (tagFactory == null) {
            printError(commandSender, this.errorMessage);
        } else {
            handleQuery(commandSender, args);
        }

        return true;
    }

    private void handleQuery(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "get":
                case "set":
                case "add":
                    handleQueryType(args[0].toLowerCase(), sender, args);
                default:
                    printError(sender, "Invalid query type \"" + args[0] + "\". Expected ('get' | 'set' | 'add')");
            }
        } else {
            printError(sender, "Missing query type. ('get' | 'set' | 'add')");
        }
    }

    private void handleQueryType(String queryType, CommandSender sender, String[] args) {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "item":
                    if (!checkPermission(sender, NBTProxy.Permissions.get(queryType, "item"))) return;
                    if (sender instanceof Player) {
                        ItemStack item = ((Player) sender).getItemInHand();
                        if (item == null) {
                            printError(sender, "You are not holding an item");
                        } else {
                            try {
                                executeQuery(queryType, this.tagFactory.getItemIODelegate(), item, 2, sender, args);
                            } catch (NBTException e) {
                                printError(sender, e.getMessage());
                            }
                        }
                    } else {
                        printError(sender, "Cannot get the item in the hand of a " + sender.getClass().getSimpleName());
                    }
                    break;
                case "block":
                    if (!checkPermission(sender, NBTProxy.Permissions.get(queryType, "block"))) return;
                    if (sender instanceof Player) {
                        Block block = ((Player) sender).getTargetBlock((Set<Material>) null, 10);
                        if (block == null) {
                            printError(sender, "You are not looking at a block");
                        } else {
                            try {
                                executeQuery(queryType, this.tagFactory.getBlockIODelegate(), block.getState(), 2, sender, args);
                            } catch (NBTException e) {
                                printError(sender, e.getMessage());
                            }
                        }
                    } else {
                        printError(sender, "Cannot get the target block of a " + sender.getClass().getSimpleName());
                    }
                    break;
                case "entity":
                    if (!checkPermission(sender, NBTProxy.Permissions.get(queryType, "entity"))) return;
                    if (sender instanceof Player) {
                        List<Entity> nearby = ((Player) sender).getNearbyEntities(5, 5, 5);
                        if (nearby.isEmpty()) {
                            printError(sender, "No entities within 5 blocks");
                        } else {
                            for (Entity entity : nearby) {
                                try {
                                    executeQuery(queryType, this.tagFactory.getEntityIODelegate(), entity, 2, sender, args);
                                } catch (NBTException e) {
                                    printError(sender, e.getMessage());
                                }
                            }
                        }
                    } else {
                        printError(sender, "Cannot get nearby entities of a " + sender.getClass().getSimpleName());
                    }
                    break;
                case "player":
                    if (!checkPermission(sender, NBTProxy.Permissions.get(queryType, "entity"))) return;
                    if (args.length >= 3) {
                        String playerName = args[2];
                        Player player = Bukkit.getPlayer(playerName);
                        if (player == null) {
                            printError(sender, "Cannot find online player with the name " + playerName);
                        } else {
                            try {
                                executeQuery(queryType, this.tagFactory.getEntityIODelegate(), player, 3, sender, args);
                            } catch (NBTException e) {
                                printError(sender, e.getMessage());
                            }
                        }
                    } else {
                        printError(sender, "Missing argument: player name");
                    }
                    break;
                case "file":
                    if (!checkPermission(sender, NBTProxy.Permissions.get(queryType, "file"))) return;
                    if (!(sender instanceof ConsoleCommandSender)) {
                        printError(sender, "Only the console can use the 'file' query target.");
                        return;
                    }
                    if (args.length >= 3) {
                        String path = args[2];
                        File file = new File(path);
                        if (!file.exists()) {
                            printError(sender, "File at path \"" + path + "\" doesn't exist.");
                        } else if (!file.isFile()) {
                            printError(sender, "File at path \"" + path + "\" is not a file.");
                        } else {
                            try {
                                executeQuery(queryType, this.tagFactory.getFileIODelegate(), file, 3, sender, args);
                            } catch (NBTException e) {
                                printError(sender, e.getMessage());
                            }
                        }
                    } else {
                        printError(sender, "Missing argument: file path");
                    }
                    break;
                default:
                    printError(sender, "Unknown query target \"" + args[1] + "\". Expected: ('item' | 'block' | 'entity' | 'player' name | 'file' path)");
                    break;
            }
        } else {
            printError(sender, "Missing query target. ('item' | 'block' | 'entity' | 'player' name | 'file' path)");
        }
    }

    private NBTCompoundTag getData(int index, CommandSender sender, String[] args) {
        if (args.length > index) {
            StringBuilder rawData = new StringBuilder();
            for (int i = index; i < args.length; i++)
                rawData.append(args[i]).append(' ');

            String data = rawData.toString();
            try {
                return this.tagFactory.parse(data);
            } catch (NBTException e) {
                printError(sender, "Parse error. " + e.getCause().getMessage());
                return null;
            }
        } else {
            printError(sender, "Missing nbt data.");
            return null;
        }
    }

    private <T> void executeQuery(String queryType, NBTIODelegate<T> nbtioDelegate, T target, int nbtDataIndex, CommandSender sender, String[] args) {
        switch (queryType) {
            case "get":
                String tag = nbtioDelegate.read(target).prettyPrint();
                printSuccess(sender, "Read:\n" + tag);
                break;
            case "set":
                NBTCompoundTag data = getData(nbtDataIndex, sender, args);
                if (data == null) return;
                nbtioDelegate.write(target, data);
                printSuccess(sender, "Wrote:\n" + data.prettyPrint());
            case "add":
                data = getData(nbtDataIndex, sender, args);
                if (data == null) return;
                nbtioDelegate.append(target, data);
                printSuccess(sender, "Appended:\n" + data.prettyPrint());
        }
    }
}
