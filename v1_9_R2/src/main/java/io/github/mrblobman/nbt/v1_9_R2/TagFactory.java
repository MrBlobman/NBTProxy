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
package io.github.mrblobman.nbt.v1_9_R2;

import io.github.mrblobman.nbt.NBTBaseTag;
import io.github.mrblobman.nbt.NBTCompoundTag;
import io.github.mrblobman.nbt.NBTException;
import io.github.mrblobman.nbt.NBTIODelegate;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_9_R2.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;

public class TagFactory extends io.github.mrblobman.nbt.TagFactory {
    private final NBTIODelegate<ItemStack> itemStackNBTIODelegate = new NBTIODelegate<ItemStack>() {
        @Override
        public NBTCompoundTag read(ItemStack item) {
            net.minecraft.server.v1_9_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            if (nmsItem.hasTag())
                return new io.github.mrblobman.nbt.v1_9_R2.NBTCompoundTag(nmsItem.getTag());
            else
                return new io.github.mrblobman.nbt.v1_9_R2.NBTCompoundTag();
        }

        @Override
        public void write(ItemStack item, NBTCompoundTag tag) {
            //Grab the nms item
            net.minecraft.server.v1_9_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            nmsItem.setTag((NBTTagCompound) tag.getHandle());
            //By setting the item meta we can modify the item directly without copying it
            //and requiring a new reference returned
            ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
            item.setItemMeta(meta);
        }

        @Override
        public void append(ItemStack item, NBTCompoundTag tag) {
            //Grab the nms item
            net.minecraft.server.v1_9_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            if (nmsItem.hasTag()) {
                NBTTagCompound nmsTag = nmsItem.getTag();
                nmsTag.a((NBTTagCompound) tag.getHandle());
            } else {
                nmsItem.setTag((NBTTagCompound) tag.getHandle());
            }
            //By setting the item meta we can modify the item directly without copying it
            //and requiring a new reference returned
            ItemMeta meta = CraftItemStack.getItemMeta(nmsItem);
            item.setItemMeta(meta);
        }
    };

    private final NBTIODelegate<Entity> entityNBTIODelegate = new NBTIODelegate<Entity>() {
        @Override
        public NBTCompoundTag read(Entity entity) {
            net.minecraft.server.v1_9_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();

            NBTCompoundTag tag = new io.github.mrblobman.nbt.v1_9_R2.NBTCompoundTag(new NBTTagCompound());
            if (!nmsEntity.c((NBTTagCompound) tag.getHandle())) {
                //Skip the id tag and try to read the data anyways
                nmsEntity.e((NBTTagCompound) tag.getHandle());
            }

            return tag;
        }

        @Override
        public void write(Entity entity, NBTCompoundTag tag) {
            net.minecraft.server.v1_9_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();

            //Also set each of the default values for a standard entity
            nmsEntity.f((NBTTagCompound) tag.getHandle());
        }

        @Override
        public void append(Entity entity, NBTCompoundTag tag) {
            net.minecraft.server.v1_9_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();

            NBTTagCompound oldTag = new NBTTagCompound();
            if (!nmsEntity.c(oldTag)) {
                //Skip the id tag and try to read the data anyways
                nmsEntity.e(oldTag);
            }
            oldTag.a((NBTTagCompound) tag.getHandle());

            //Also set each of the default values for a standard entity
            nmsEntity.f((NBTTagCompound) tag.getHandle());
        }
    };

    private final NBTIODelegate<BlockState> blockNBTIODelegate = new NBTIODelegate<BlockState>() {
        @Override
        public NBTCompoundTag read(BlockState blockState) {
            CraftBlockState craftState = (CraftBlockState) blockState;
            TileEntity nmsBlock = craftState.getTileEntity();
            if (nmsBlock == null)
                throw new NBTException("Given block (" + blockState + ") is not a tile entity and does not have an NBT tag.");

            NBTCompoundTag tag =  new io.github.mrblobman.nbt.v1_9_R2.NBTCompoundTag();
            nmsBlock.save((NBTTagCompound) tag.getHandle());
            return tag;
        }

        @Override
        public void write(BlockState blockState, NBTCompoundTag tag) {
            CraftBlockState craftState = (CraftBlockState) blockState;
            TileEntity nmsBlock = craftState.getTileEntity();
            if (nmsBlock == null)
                throw new NBTException("Given block (" + blockState + ") is not a tile entity and does not have an NBT tag.");

            nmsBlock.a((NBTTagCompound) tag.getHandle());
        }

        @Override
        public void append(BlockState blockState, NBTCompoundTag tag) {
            CraftBlockState craftState = (CraftBlockState) blockState;
            TileEntity nmsBlock = craftState.getTileEntity();
            if (nmsBlock == null)
                throw new NBTException("Given block (" + blockState + ") is not a tile entity and does not have an NBT tag.");

            NBTCompoundTag oldTag =  new io.github.mrblobman.nbt.v1_9_R2.NBTCompoundTag();
            nmsBlock.save((NBTTagCompound) tag.getHandle());
            oldTag.putAll(tag);
            nmsBlock.a((NBTTagCompound) oldTag.getHandle());
        }
    };

    private final NBTIODelegate<File> fileNBTIODelegate = new NBTIODelegate<File>() {
        @Override
        public NBTCompoundTag read(File item) {
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(item);
            } catch (FileNotFoundException e) {
                throw new NBTException("Error opening file input stream for "+item.getName()+".", e);
            }

            try {
                NBTTagCompound nmsTag = NBTCompressedStreamTools.a(inputStream);
                return new io.github.mrblobman.nbt.v1_9_R2.NBTCompoundTag(nmsTag);
            } catch (IOException e) {
                throw new NBTException("Error reading from file "+item.getName()+".", e);
            }
        }

        @Override
        public void write(File item, NBTCompoundTag tag) {
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(item);
            } catch (FileNotFoundException e) {
                throw new NBTException("Error opening file output stream for "+item.getName()+".", e);
            }

            try {
                NBTCompressedStreamTools.a((NBTTagCompound) tag.getHandle(), outputStream);
            } catch (IOException e) {
                throw new NBTException("Error writing to file "+item.getName()+".", e);
            }
        }

        @Override
        public void append(File item, NBTCompoundTag tag) {
            NBTCompoundTag existing = read(item);
            existing.putAll(tag);
            write(item, existing);
        }
    };

    @Override
    public NBTIODelegate<ItemStack> getItemIODelegate() {
        return itemStackNBTIODelegate;
    }

    @Override
    public NBTIODelegate<Entity> getEntityIODelegate() {
        return entityNBTIODelegate;
    }

    @Override
    public NBTIODelegate<BlockState> getBlockIODelegate() {
        return blockNBTIODelegate;
    }

    @Override
    public NBTIODelegate<File> getFileIODelegate() {
        return fileNBTIODelegate;
    }

    @Override
    public NBTBaseTag<Byte> newByteTag(byte value) {
        return new NBTByteTag(value);
    }

    @Override
    protected NBTBaseTag<Byte> wrapByteTag(Object handle) {
        return new NBTByteTag((NBTTagByte) handle);
    }

    @Override
    public NBTBaseTag<Short> newShortTag(short value) {
        return new NBTShortTag(value);
    }

    @Override
    protected NBTBaseTag<Short> wrapShortTag(Object handle) {
        return new NBTShortTag((NBTTagShort) handle);
    }

    @Override
    public NBTBaseTag<Integer> newIntTag(int value) {
        return new NBTIntTag(value);
    }

    @Override
    protected NBTBaseTag<Integer> wrapIntTag(Object handle) {
        return new NBTIntTag((NBTTagInt) handle);
    }

    @Override
    public NBTBaseTag<Long> newLongTag(long value) {
        return new NBTLongTag(value);
    }

    @Override
    protected NBTBaseTag<Long> wrapLongTag(Object handle) {
        return new NBTLongTag((NBTTagLong) handle);
    }

    @Override
    public NBTBaseTag<Float> newFloatTag(float value) {
        return new NBTFloatTag(value);
    }

    @Override
    protected NBTBaseTag<Float> wrapFloatTag(Object handle) {
        return new NBTFloatTag((NBTTagFloat) handle);
    }

    @Override
    public NBTBaseTag<Double> newDoubleTag(double value) {
        return new NBTDoubleTag(value);
    }

    @Override
    protected NBTBaseTag<Double> wrapDoubleTag(Object handle) {
        return new NBTDoubleTag((NBTTagDouble) handle);
    }

    @Override
    public NBTBaseTag<byte[]> newByteArrayTag(byte[] value) {
        return new NBTByteArrayTag(value);
    }

    @Override
    protected NBTBaseTag<byte[]> wrapByteArrayTag(Object handle) {
        return new NBTByteArrayTag((NBTTagByteArray) handle);
    }

    @Override
    public NBTBaseTag<String> newStringTag(String value) {
        return new NBTStringTag(value);
    }

    @Override
    protected NBTBaseTag<String> wrapStringTag(Object handle) {
        return new NBTStringTag((NBTTagString) handle);
    }

    @Override
    public NBTListTag newListTag() {
        return new NBTListTag();
    }

    @Override
    protected NBTListTag wrapListTag(Object handle) {
        return new NBTListTag((NBTTagList) handle);
    }

    @Override
    public NBTCompoundTag newCompoundTag() {
        return new io.github.mrblobman.nbt.v1_9_R2.NBTCompoundTag();
    }

    @Override
    protected NBTCompoundTag wrapCompoundTag(Object handle) {
        return new io.github.mrblobman.nbt.v1_9_R2.NBTCompoundTag((NBTTagCompound) handle);
    }

    @Override
    public NBTBaseTag<int[]> newIntArrayTag(int[] value) {
        return new NBTIntArrayTag(value);
    }

    @Override
    public NBTBaseTag<int[]> wrapIntArrayTag(Object handle) {
        return new NBTIntArrayTag((NBTTagIntArray) handle);
    }
}
