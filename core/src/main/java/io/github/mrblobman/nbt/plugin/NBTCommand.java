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

import io.github.mrblobman.nbt.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Provide command access to all of the {@link NBTIODelegate}s. Each command must
 * be enabled in the {@code config.yml} before it can be used and form there a
 * user needs the permissions described in {@link NBTProxy.Permissions}.
 * <br>
 * Argument grammar:
 * <pre>
 *     nbt_query := query_type query_target;
 *     query_type := ('get' | 'read') path? | ('set' | 'write') nbt_data | 'add' nbt_data;
 *     query_target := 'item' | 'block' | 'entity' | 'player' name | 'file' path;
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
                case "read":
                case "set":
                case "write":
                case "add":
                    handleQueryType(args[0].toLowerCase(), sender, args);
                    break;
                default:
                    printError(sender, "Invalid query type \"" + args[0] + "\". Expected ('get' | 'read' | 'set' | 'write' | 'add')");
            }
        } else {
            printError(sender, "Missing query type. ('get' | 'read' | 'set' | 'write' | 'add')");
        }
    }

    private void handleQueryType(String queryType, CommandSender sender, String[] args) {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "item":
                    if (!checkPermission(sender, NBTProxy.Permissions.get(queryType, "item"))) return;
                    if (sender instanceof Player) {
                        ItemStack item = ((Player) sender).getItemInHand();
                        if (item == null || item.getType() == Material.AIR) {
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
                        BlockState state;
                        if (block == null) {
                            printError(sender, "You are not looking at a block");
                        } else if ((state = block.getState()) == null) {
                            printError(sender, "Cannot get block state of the block you are looking at");
                        } else {
                            try {
                                executeQuery(queryType, this.tagFactory.getBlockIODelegate(), state, 2, sender, args);
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

            String data = rawData.toString().trim();
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

    private static final String[] EMPTY_PATH = new String[0];
    private static final Pattern DOT_SPLITTER = Pattern.compile("\\.");
    private String[] getReadPath(int index, String[] args) {
        if (args.length > index) {
            StringBuilder rawData = new StringBuilder();
            for (int i = index; i < args.length; i++)
                rawData.append(args[i]).append(' ');

            return DOT_SPLITTER.split(rawData.toString().trim());
        } else {
            return EMPTY_PATH;
        }
    }

    private <T> void executeQuery(String queryType, NBTIODelegate<T> nbtioDelegate, T target, int typeDataIndex, CommandSender sender, String[] args) {
        switch (queryType) {
            case "get":
            case "read":
                String[] readPath = getReadPath(typeDataIndex, args);
                NBTBaseTag tag = nbtioDelegate.read(target);
                for (String readPathSegment : readPath) {
                    if (tag == null) {
                        printError(sender, "Path " + Arrays.toString(readPath) + " encounters the end of a path and can't read '" + readPathSegment + "'.");
                        return;
                    } else if (tag.isCompound()) {
                        tag = ((NBTCompoundTag) tag).getTag(readPathSegment);
                    } else if (tag.type().isList()) {
                        int index;
                        try {
                            index = Integer.parseInt(readPathSegment);
                        } catch (NumberFormatException e) {
                            printError(sender, "Path " + Arrays.toString(readPath) + " encounters a list but '" + readPathSegment + "' is not a number.");
                            return;
                        }
                        try {
                            tag = ((NBTListTag) tag).get(index);
                        } catch (IndexOutOfBoundsException e) {
                            printError(sender, "Path " + Arrays.toString(readPath) + " encounters a list but '" + readPathSegment + "' is not an element in that list.");
                            return;
                        }
                    } else {
                        printError(sender, "Path " + Arrays.toString(readPath) + " encounters a " + NBTType.getName(tag.type().ID) + " and can't read '" + readPathSegment + "' from it.");
                        return;
                    }
                }
                if (tag == null) {
                    printError(sender, "Path " + Arrays.toString(readPath) + " does not lead anywhere.");
                    return;
                }
                printSuccess(sender, "Read\n" + (tag.isCompound() ? ((NBTCompoundTag) tag).prettyPrint() : tag.toString()));
                break;
            case "set":
            case "write":
                NBTCompoundTag data = getData(typeDataIndex, sender, args);
                if (data == null) return;
                nbtioDelegate.write(target, data);
                printSuccess(sender, "Wrote\n" + data.prettyPrint());
                break;
            case "add":
                data = getData(typeDataIndex, sender, args);
                if (data == null) return;
                nbtioDelegate.append(target, data);
                printSuccess(sender, "Appended\n" + data.prettyPrint());
                break;
        }
    }
}
