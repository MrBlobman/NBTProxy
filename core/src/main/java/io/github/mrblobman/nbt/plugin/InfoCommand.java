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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * A command that display information about the plugin to the sender.
 */
public class InfoCommand implements CommandExecutor {
    private final String[] infoMsg;
    private final BaseComponent[] infoMsgPretty;

    public InfoCommand(PluginDescriptionFile pdf) {
        this.infoMsg = new String[] {
                ChatColor.AQUA + "NBTProxy Info:",
                ChatColor.AQUA + "  Description: " + ChatColor.YELLOW + pdf.getDescription(),
                ChatColor.AQUA + "  Version: " + ChatColor.YELLOW + pdf.getVersion(),
                ChatColor.AQUA + "  Authors: " + ChatColor.YELLOW + pdf.getAuthors(),
                ChatColor.AQUA + "  Website: " + ChatColor.YELLOW + pdf.getWebsite()
        };
        HoverEvent clickToCopyToolTip = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.AQUA + "Click to paste the version into your chatbox."));
        ComponentBuilder msg = new ComponentBuilder("\nNBTProxy Info:\n").color(ChatColor.AQUA);
        appendInfoString(msg, clickToCopyToolTip, "Description", pdf.getDescription());
        appendInfoString(msg, clickToCopyToolTip, "Version", pdf.getVersion());
        appendInfoString(msg, clickToCopyToolTip, "Authors", pdf.getAuthors().toString());
        appendInfoString(msg, clickToCopyToolTip, "Website", pdf.getWebsite());
        this.infoMsgPretty = msg.create();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            //We will use a nice message instead of the plain text
            ((Player) commandSender).spigot().sendMessage(infoMsgPretty);
        } else {
            commandSender.sendMessage(infoMsg);
        }
        return true;
    }

    private static void appendInfoString(ComponentBuilder msg, HoverEvent clickToCopyToolTip, String label, String value) {
        msg.append("  " + label + ": ").color(ChatColor.AQUA);
        msg.append(value + "\n").color(ChatColor.YELLOW);
        msg.event(clickToCopyToolTip);
        msg.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, value));
    }
}
