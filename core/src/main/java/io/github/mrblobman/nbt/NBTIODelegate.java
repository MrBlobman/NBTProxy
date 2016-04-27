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

/**
 * This class provides {@link #read(Object) read}, {@link #write(Object, NBTCompoundTag) write}
 * and {@link #append(Object, NBTCompoundTag) append} operations for the NBT data of an object
 * of the type {@link T}.
 * @param <T> The type of the data this delegate can forward the operations to.
 */
public interface NBTIODelegate<T> {

    /**
     * Read the NBT data from the {@code item}.
     * @param item the item who's data is being read
     * @return an {@link NBTCompoundTag} with the item's NBT data
     */
    NBTCompoundTag read(T item);

    /**
     * Write the given tag to the item. This completely overwrites the items
     * old data with this tag. See {@link #append(Object, NBTCompoundTag)} if you
     * would only like to add new tags or overwrite just the tags in the compound.
     * @param item the item that the data is being written to
     * @param tag the data to write to the item
     */
    void write(T item, NBTCompoundTag tag);

    /**
     * Add all of the mappings in the tag to the item. This will only overwrite things
     * that appear in the given tag leaving the other elements in the existing tag alone.
     * @param item the item that the data is being written to
     * @param tag the data to append to the item
     */
    void append(T item, NBTCompoundTag tag);
}
