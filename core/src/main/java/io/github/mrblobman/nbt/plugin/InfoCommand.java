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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * A command that display information about the plugin to the sender.
 */
public class InfoCommand implements CommandExecutor {
    private NBTProxy plugin;
    private HoverEvent clickToCopyToolTip;

    public InfoCommand(NBTProxy plugin) {
        this.plugin = plugin;
        this.clickToCopyToolTip = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.AQUA + "Click to paste the version into your chatbox."));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        PluginDescriptionFile pdf = plugin.getDescription();
        if (commandSender instanceof Player) {
            //We will build a nice message instead of the plain text
            ComponentBuilder msg = new ComponentBuilder("\nNBTProxy Info:\n").color(ChatColor.AQUA);
            appendInfoString(msg, "Description", pdf.getDescription());
            appendInfoString(msg, "Version", pdf.getVersion());
            appendInfoString(msg, "Authors", pdf.getAuthors().toString());
            appendInfoString(msg, "Website", pdf.getWebsite());
            ((Player) commandSender).spigot().sendMessage(msg.create());
        } else {
            commandSender.sendMessage(ChatColor.AQUA + "NBTProxy Info:");
            commandSender.sendMessage(ChatColor.AQUA + "  Description: " + ChatColor.YELLOW + pdf.getDescription());
            commandSender.sendMessage(ChatColor.AQUA + "  Version: " + ChatColor.YELLOW + pdf.getVersion());
            commandSender.sendMessage(ChatColor.AQUA + "  Authors: " + ChatColor.YELLOW + pdf.getAuthors());
            commandSender.sendMessage(ChatColor.AQUA + "  Website: " + ChatColor.YELLOW + pdf.getWebsite());
        }
        return true;
    }

    private void appendInfoString(ComponentBuilder msg, String label, String value) {
        msg.append("  " + label + ": ").color(ChatColor.AQUA);
        msg.append(value + "\n").color(ChatColor.YELLOW);
        msg.event(clickToCopyToolTip);
        msg.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, value));
    }
}
