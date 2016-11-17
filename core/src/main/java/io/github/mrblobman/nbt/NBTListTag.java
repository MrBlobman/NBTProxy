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
 * An NBTListTag is a collection of {@link NBTBaseTag}s of the same type. It is a special
 * {@link NBTBaseTag} in the sense that its value (returned by {@link NBTBaseTag#get()})
 * is itself. The value is determined
 */
public abstract class NBTListTag extends NBTBaseTag<NBTListTag> {
    protected int valueType = 0;

    protected NBTListTag(Object handle) {
        super(handle);
    }

    /**
     * Get the type id of the tags that go inside this list.
     * @return the type id or 0 if the list {@link #isEmpty()}
     */
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
            throw new ClassCastException("Cannot add " + NBTType.getName(type.ID) + " to a list of " + NBTType.getName(valueType));
        }
    }

    /**
     * Get the number of tags in this list
     * @return the size of the list
     */
    public abstract int size();

    protected abstract void addInternal(NBTBaseTag tag);

    /**
     * Append an element to the end of this list.
     * @param tag the tag to add to this list
     * @throws ClassCastException if the list contains one or more tags and
     *                            the tag to add does not share the same type ID.
     */
    public void add(NBTBaseTag tag) {
        checkAndSetType(tag.type());
        addInternal(tag);
    }

    protected abstract void setInternal(int pos, NBTBaseTag tag);

    /**
     * Replace the element at index {@code pos} with {@code tag}.
     * @param pos the index in the list to set
     * @param tag the tag to put into the list
     * @throws IndexOutOfBoundsException if the {@code pos} is not an index in
     *                                   the list.
     * @throws ClassCastException if the list contains one or more tags and
     *                            the tag to add does not share the same type ID.
     */
    public void set(int pos, NBTBaseTag tag) {
        if (pos < 0 || pos >= size())
            throw new IndexOutOfBoundsException("Index: " + pos + ", Size: " + size());

        checkAndSetType(tag.type());
        setInternal(pos, tag);
    }

    /**
     * Removes the tag at {@code pos} from the list and returns it. All of the
     * following elements are shifted 1 position down to fill the gap.
     * @param pos the index of the tag to remove
     * @return the removed tag
     * @throws IndexOutOfBoundsException if the {@code pos} is not an index in
     *                                   the list.
     */
    public abstract NBTBaseTag remove(int pos);

    /**
     * A shorthand for {@code size() == 0}. It checks if this
     * list contains any elements.
     * @return true if there are no elements in this list ({@link #size()} == 0)
     *         and false otherwise
     */
    public abstract boolean isEmpty();

    /**
     * Gets the tag at {@code pos} from the list.
     * @param pos the index of the tag to get
     * @return the tag at index {@code pos}
     * @throws IndexOutOfBoundsException if the {@code pos} is not an index in
     *                                   the list.
     */
    public abstract NBTBaseTag get(int pos);
}
