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

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class NBTProxy extends JavaPlugin {
    /**
     * A list of the permissions for the {@code /nbt} command.
     */
    public enum Permissions {
        /**
         * Required for executing:
         * <ul>
         *     <li>{@code /nbt get item}</li>
         * </ul>
         */
        READ_ITEM    (1     , "nbtproxy.read.item",    "read.item"),
        /**
         * Required for executing:
         * <ul>
         *     <li>{@code /nbt get block}</li>
         * </ul>
         */
        READ_BLOCK   (1 << 1, "nbtproxy.read.block",   "read.block"),
        /**
         * Required for executing:
         * <ul>
         *     <li>{@code /nbt get entity}</li>
         *     <li>{@code /nbt get player} &lt;name&gt;</li>
         * </ul>
         */
        READ_ENTITY  (1 << 2, "nbtproxy.read.entity",  "read.entity"),
        /**
         * Required for executing:
         * <ul>
         *     <li>{@code /nbt get file} &lt;path&gt;</li>
         * </ul>
         */
        READ_FILE    (1 << 3, "nbtproxy.read.file",    "read.file"),
        /**
         * Required for executing:
         * <ul>
         *     <li>{@code /nbt set item} &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt write item} &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt add item} &lt;nbt-data&gt;</li>
         * </ul>
         */
        WRITE_ITEM   (1 << 4, "nbtproxy.write.item",   "write.item"),
        /**
         * Required for executing:
         * <ul>
         *     <li>{@code /nbt set block} &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt write block} &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt add block} &lt;nbt-data&gt;</li>
         * </ul>
         */
        WRITE_BLOCK  (1 << 5, "nbtproxy.write.block",  "write.block"),
        /**
         * Required for executing:
         * <ul>
         *     <li>{@code /nbt set entity} &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt write entity} &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt add entity} &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt set player} &lt;name&gt; &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt write player} &lt;name&gt; &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt add player} &lt;name&gt; &lt;nbt-data&gt;</li>
         * </ul>
         */
        WRITE_ENTITY (1 << 6, "nbtproxy.write.entity", "write.entity"),
        /**
         * Required for executing:
         * <ul>
         *     <li>{@code /nbt set file} &lt;path&gt; &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt write file} &lt;path&gt; &lt;nbt-data&gt;</li>
         *     <li>{@code /nbt add file} &lt;path&gt; &lt;nbt-data&gt;</li>
         * </ul>
         */
        WRITE_FILE   (1 << 7, "nbtproxy.write.file",   "write.file");

        /**
         * A unique key for use in an {@code 'enabled'} bit set.
         */
        public final int PERM_ENABLED_FLAG;
        /**
         * The full name of the permission that a {@link org.bukkit.permissions.Permissible}
         * must have to execute the query this permission guards.
         */
        public final String PERMISSION;
        /**
         * The relative path from 'enabled-commands' to the config key for setting the
         * enabled state of the query this permission guards.
         */
        public final String CONFIG_KEY;

        Permissions(int permEnabledFlag, String permission, String configKey) {
            this.PERM_ENABLED_FLAG = permEnabledFlag;
            this.PERMISSION = permission;
            this.CONFIG_KEY = configKey;
        }

        /**
         * Lookup a permission based on the {@code queryType} and {@code queryTarget}
         * @param queryType the type of the query. One of {@code get}, {@code read},
         *                  {@code set}, {@code write}, and {@code add}
         * @param queryTarget the target of the query. One of {@code item}, {@code block},
         *                    {@code entity}, and {@code file}
         * @return the associated {@link Permissions permission} or {@code null} if and
         *         unknown {@code queryType} or {@code queryTarget}
         */
        public static Permissions get(String queryType, String queryTarget) {
            boolean isRead;
            switch (queryType) {
                case "get":
                case "read":
                    isRead = true;
                    break;
                case "set":
                case "write":
                case "add":
                    isRead = false;
                    break;
                default:
                    return null;
            }

            switch (queryTarget) {
                case "item":
                    return isRead ? READ_ITEM : WRITE_ITEM;
                case "block":
                    return isRead ? READ_BLOCK : WRITE_BLOCK;
                case "entity":
                    return isRead ? READ_ENTITY : WRITE_ENTITY;
                case "file":
                    return isRead ? READ_FILE : WRITE_FILE;
                default:
                    return null;
            }
        }
    }

    @Override
    public void onEnable() {
        this.getCommand("nbtproxy").setExecutor(new InfoCommand(this.getDescription()));

        this.saveDefaultConfig();

        Configuration config = getConfig();
        int enabledCommands = 0;
        for (Permissions perm : Permissions.values()) {
            if (config.getBoolean("enabled-commands." + perm.CONFIG_KEY))
                enabledCommands |= perm.PERM_ENABLED_FLAG;
        }

        this.getCommand("nbt").setExecutor(new NBTCommand(enabledCommands));
    }
}
