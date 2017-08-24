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
package io.github.mrblobman.nbt.v1_12_R1.bridge;

import io.github.mrblobman.nbt.NBTCompoundTag;
import io.github.mrblobman.nbt.NBTException;
import io.github.mrblobman.nbt.NBTIODelegate;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.TileEntity;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlockEntityState;

public class BlockNBTIODelegate implements NBTIODelegate<BlockState> {
    @Override
    public NBTCompoundTag read(BlockState blockState) {
        if (!(blockState instanceof CraftBlockEntityState))
            throw new NBTException("Given block is not a tile entity and does not have an NBT tag.");

        CraftBlockEntityState<?> craftState = (CraftBlockEntityState<?>) blockState;
        return new io.github.mrblobman.nbt.v1_12_R1.NBTCompoundTag(craftState.getSnapshotNBT());
    }

    @Override
    public void write(BlockState blockState, NBTCompoundTag tag) {
        if (!(blockState instanceof CraftBlockEntityState))
            throw new NBTException("Given block is not a tile entity and does not have an NBT tag.");

        CraftBlockEntityState<?> craftState = (CraftBlockEntityState<?>) blockState;
        TileEntity nmsBlock = ((CraftWorld) craftState.getWorld())
                .getTileEntityAt(craftState.getX(), craftState.getY(), craftState.getZ());

        nmsBlock.load((NBTTagCompound) tag.getHandle());
    }

    @Override
    public void append(BlockState blockState, NBTCompoundTag tag) {
        if (!(blockState instanceof CraftBlockEntityState))
            throw new NBTException("Given block is not a tile entity and does not have an NBT tag.");

        CraftBlockEntityState<?> craftState = (CraftBlockEntityState<?>) blockState;
        NBTCompoundTag oldTag = new io.github.mrblobman.nbt.v1_12_R1.NBTCompoundTag(craftState.getSnapshotNBT());

        oldTag.putAll(tag);

        TileEntity nmsBlock = ((CraftWorld) craftState.getWorld())
                .getTileEntityAt(craftState.getX(), craftState.getY(), craftState.getZ());

        nmsBlock.load((NBTTagCompound) tag.getHandle());
    }
}
