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
 * An NBTNumberTag wraps a number. It is one level higher than the primitive number tags
 * but it is not an actual tag type. The type of this tag depends on the tag it is wrapping.
 * If the underlying tag is a byte tag this tags type will also be a byte tag but provides
 * simple cast avoiding methods to the other primitive number types.
 * @param <T>
 */
public abstract class NBTNumberTag<T extends Number> extends NBTBaseTag<T> {

    protected NBTNumberTag(Object handle) {
        super(handle);
    }

    public byte getByte() {
        return get().byteValue();
    }

    public short getShort() {
        return get().shortValue();
    }

    public int getInt() {
        return get().intValue();
    }

    public long getLong() {
        return get().longValue();
    }

    public float getFloat() {
        return get().floatValue();
    }

    public double getDouble() {
        return get().doubleValue();
    }
}
