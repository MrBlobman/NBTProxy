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
package io.github.mrblobman.nbt.v1_9_R1;

import io.github.mrblobman.nbt.*;
import net.minecraft.server.v1_9_R1.NBTBase;
import net.minecraft.server.v1_9_R1.NBTTagCompound;

import java.util.Set;

public class NBTCompoundTag extends io.github.mrblobman.nbt.NBTCompoundTag {
    private NBTTagCompound nmsTag;

    public NBTCompoundTag() {
        super(new NBTTagCompound());
        this.nmsTag = (NBTTagCompound) super.getHandle();
    }

    public NBTCompoundTag(NBTTagCompound nmsTag) {
        super(nmsTag);
        this.nmsTag = (NBTTagCompound) super.getHandle();
    }

    @Override
    public void put(String key, NBTBaseTag value) {
        this.nmsTag.set(key, (NBTBase) value.getHandle());
    }

    @Override
    public void putAll(io.github.mrblobman.nbt.NBTCompoundTag tag) {
        this.nmsTag.a((NBTTagCompound) tag.getHandle());
    }

    @Override
    public  <T> T get(String key, NBTType<T> type) {
        NBTBase nbtBase = nmsTag.get(key);
        try {
            NBTBaseTag<T> tag = nbtBase == null ? type.newTag() : type.wrapHandle(nbtBase);
            return tag.get();
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public NBTBaseTag getTag(String key) {
        NBTBase nbtBase = nmsTag.get(key);
        return nbtBase == null ? null : NBTType.getType(nbtBase.getTypeId()).wrapHandle(nbtBase);
    }

    @Override
    public boolean hasKey(String key) {
        return this.nmsTag.hasKey(key);
    }

    @Override
    public boolean hasKeyOfType(String key, NBTType type) {
        return this.nmsTag.hasKeyOfType(key, type.ID);
    }

    @Override
    public int size() {
        return nmsTag.d();
    }

    @Override
    public Set<String> keys() {
        return nmsTag.c();
    }

    @Override
    public void putBoolean(String key, boolean value) {
        nmsTag.setBoolean(key, value);
    }

    @Override
    public boolean getBoolean(String key) {
        return nmsTag.getBoolean(key);
    }
}
