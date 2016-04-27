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

import java.util.Objects;

/**
 * A base wrapper class to better delegate some of the default methods.
 */
public abstract class NBTBaseTag<T> {
    private Object handle;

    protected NBTBaseTag(Object handle) {
        Objects.requireNonNull(handle, "Cannot wrap a null handle.");
        this.handle = handle;
    }

    /**
     * @return the type of this tag
     */
    public abstract NBTType<T> type();

    /**
     * @return true iff {@code type().isCompound()}, false otherwise
     */
    public boolean isCompound() {
        return type().isCompound();
    }

    /**
     * @return true iff {@code type().isNumber()}, false otherwise
     */
    public boolean isNumber() {
        return type().isNumber();
    }

    /**
     * @return the value this tag has
     */
    public abstract T get();

    /**
     * @return the nms class that this tag wraps
     */
    public Object getHandle() {
        return handle;
    }

    @Override
    public boolean equals(Object obj) {
        return handle.equals(obj);
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }

    @Override
    public String toString() {
        return handle.toString();
    }
}
