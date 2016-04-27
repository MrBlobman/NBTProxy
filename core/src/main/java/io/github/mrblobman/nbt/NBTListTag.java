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
package io.github.mrblobman.nbt;

public abstract class NBTListTag extends NBTBaseTag<NBTListTag> {
    private int valueType = 0;

    protected NBTListTag(Object handle) {
        super(handle);
    }

    public int valueType() {
        return valueType;
    }

    @Override
    public NBTType<NBTListTag> type() {
        return NBTType.LIST;
    }

    private void checkAndSetType(NBTType type) {
        if (valueType == 0) {
            valueType = type.ID;
        } else if (valueType != type.ID) {
            throw new ClassCastException("Cannot add "+NBTType.getName(type.ID)+" to a list of "+NBTType.getName(valueType));
        }
    }

    public abstract int size();

    protected abstract void addInternal(NBTBaseTag tag);

    public void add(NBTBaseTag tag) {
        checkAndSetType(tag.type());
        addInternal(tag);
    }

    protected abstract void setInternal(int pos, NBTBaseTag tag);

    public void set(int pos, NBTBaseTag tag) {
        if (pos < 0 || pos >= size())
            throw new IndexOutOfBoundsException("Index: "+pos+", Size: "+size());

        checkAndSetType(tag.type());
        setInternal(pos, tag);
    }

    public abstract NBTBaseTag remove(int pos);

    public abstract boolean isEmpty();

    public abstract NBTBaseTag get(int pos);
}
