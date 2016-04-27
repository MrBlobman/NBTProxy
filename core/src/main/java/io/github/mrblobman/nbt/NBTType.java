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

import java.util.BitSet;

public final class NBTType<T> {
    private static final String[] TAG_NAMES = new String[] {
            "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"
    };
    private static final NBTType<EndTagValue>       END         = new NBTType<>(0, EndTagValue.VALUE);
    public static final NBTType<Byte>               BYTE        = new NBTType<>(1, (byte)   0);
    public static final NBTType<Short>              SHORT       = new NBTType<>(2, (short)  0);
    public static final NBTType<Integer>            INT         = new NBTType<>(3, (int)    0);
    public static final NBTType<Long>               LONG        = new NBTType<>(4, (long)   0);
    public static final NBTType<Float>              FLOAT       = new NBTType<>(5, (float)  0);
    public static final NBTType<Double>             DOUBLE      = new NBTType<>(6, (double) 0);
    public static final NBTType<byte[]>             BYTE_ARRAY  = new NBTType<>(7, new byte[0]);
    public static final NBTType<String>             STRING      = new NBTType<>(8, "");
    public static final NBTType<NBTListTag>         LIST        = new NBTType<>(9, null);
    public static final NBTType<NBTCompoundTag>     COMPOUND    = new NBTType<>(10, null);
    public static final NBTType<int[]>              INT_ARRAY   = new NBTType<>(11, new int[0]);
    
    private static final BitSet NUMBER_IDS = new BitSet(6);
    static {
        NUMBER_IDS.set(1, 6);
    }
    
    public final int ID;
    private final T defaultVal;

    private NBTType(int id, T defaultVal) {
        ID = id;
        this.defaultVal = defaultVal;
    }

    /**
     * Create a fresh tag of this type. For list and compound tags
     * the tag can be safely cast to an {@link NBTListTag} or {@link NBTCompoundTag}
     * or equivalently with a {@link NBTBaseTag#get()} call.<br>
     * Ex:
     * <pre>
     *     NBTCompoundTag tag = NBTType.COMPOUND.newTag().get();
     * </pre>
     * @return the newly created tag
     */
    @SuppressWarnings("unchecked")
    public NBTBaseTag<T> newTag() {
        switch (ID) {
            /*case 0:
                return (NBTBaseTag<T>) TagFactory.get().newEndTag();*/
            case 1:
                return (NBTBaseTag<T>) TagFactory.get().newByteTag((Byte) defaultVal);
            case 2:
                return (NBTBaseTag<T>) TagFactory.get().newShortTag((Short) defaultVal);
            case 3:
                return (NBTBaseTag<T>) TagFactory.get().newIntTag((Integer) defaultVal);
            case 4:
                return (NBTBaseTag<T>) TagFactory.get().newLongTag((Long) defaultVal);
            case 5:
                return (NBTBaseTag<T>) TagFactory.get().newFloatTag((Float) defaultVal);
            case 6:
                return (NBTBaseTag<T>) TagFactory.get().newDoubleTag((Double) defaultVal);
            case 7:
                return (NBTBaseTag<T>) TagFactory.get().newByteArrayTag((byte[]) defaultVal);
            case 8:
                return (NBTBaseTag<T>) TagFactory.get().newStringTag((String) defaultVal);
            case 9:
                return (NBTBaseTag<T>) TagFactory.get().newListTag();
            case 10:
                return (NBTBaseTag<T>) TagFactory.get().newCompoundTag();
            case 11:
                return (NBTBaseTag<T>) TagFactory.get().newIntArrayTag((int[]) defaultVal);
        }
        throw new Error("NBTType has been instantiated illegally (via reflection?) with an invalid type id of "+ID);
    }

    /**
     * Wrap the nms handle in a wrapper of the correct type
     * @param handle the handle to wrap
     * @return the wrapper for the handle
     * @throws ClassCastException if the handle is not a tag of this tag type
     */
    @SuppressWarnings("unchecked")
    public NBTBaseTag<T> wrapHandle(Object handle) throws ClassCastException {
        switch (ID) {
            /*case 0:
                return (NBTBaseTag<T>) TagFactory.get().wrapEndTag(handle);*/
            case 1:
                return (NBTBaseTag<T>) TagFactory.get().wrapByteTag(handle);
            case 2:
                return (NBTBaseTag<T>) TagFactory.get().wrapShortTag(handle);
            case 3:
                return (NBTBaseTag<T>) TagFactory.get().wrapIntTag(handle);
            case 4:
                return (NBTBaseTag<T>) TagFactory.get().wrapLongTag(handle);
            case 5:
                return (NBTBaseTag<T>) TagFactory.get().wrapFloatTag(handle);
            case 6:
                return (NBTBaseTag<T>) TagFactory.get().wrapDoubleTag(handle);
            case 7:
                return (NBTBaseTag<T>) TagFactory.get().wrapByteArrayTag(handle);
            case 8:
                return (NBTBaseTag<T>) TagFactory.get().wrapStringTag(handle);
            case 9:
                return (NBTBaseTag<T>) TagFactory.get().wrapListTag(handle);
            case 10:
                return (NBTBaseTag<T>) TagFactory.get().wrapCompoundTag(handle);
            case 11:
                return (NBTBaseTag<T>) TagFactory.get().wrapIntArrayTag(handle);
        }
        throw new Error("NBTType has been instantiated illegally (via reflection?) with an invalid type id of "+ID);
    }

    public static String getName(int type) {
        return type > 0 && type < TAG_NAMES.length ? TAG_NAMES[type] : "UNKNOWN";
    }

    /**
     * Lookup a type based on it's id.
     * @param type the type id
     * @return the type represented by the id
     * @throws IllegalArgumentException if the type id does not correspond to
     * any of the known types
     */
    public static NBTType getType(int type) {
        switch (type) {
            case 1:
                return NBTType.BYTE;
            case 2:
                return NBTType.SHORT;
            case 3:
                return NBTType.INT;
            case 4:
                return NBTType.LONG;
            case 5:
                return NBTType.FLOAT;
            case 6:
                return NBTType.DOUBLE;
            case 7:
                return NBTType.BYTE_ARRAY;
            case 8:
                return NBTType.STRING;
            case 9:
                return NBTType.LIST;
            case 10:
                return NBTType.COMPOUND;
            case 11:
                return NBTType.INT_ARRAY;
            case 0:
            default:
                throw new IllegalArgumentException("Unknown tag type id "+type);
        }
    }

    /**
     * @return true if this tag wraps a number value, false otherwise
     */
    public boolean isNumber() {
        return NUMBER_IDS.get(ID);
    }

    /**
     * @return true if this tag wraps a byte or int array, false otherwise
     */
    public boolean isArray() {
        return ID == 7 || ID == 11;
    }

    /**
     * @return true if this tag wraps a byte array, false otherwise
     */
    public boolean isByteArray() {
        return ID == 7;
    }

    /**
     * @return true if this tag wraps an int array, false otherwise
     */
    public boolean isIntArray() {
        return ID == 11;
    }

    /**
     * @return true if this tag wraps a String, false otherwise
     */
    public boolean isString() {
        return ID == 8;
    }

    /**
     * @return true if this tag is a list of NBT tags, false otherwise
     */
    public boolean isList() {
        return ID == 9;
    }

    /**
     * @return true if this tag is a compound tag, false otherwise
     */
    public boolean isCompound() {
        return ID == 10;
    }
}
