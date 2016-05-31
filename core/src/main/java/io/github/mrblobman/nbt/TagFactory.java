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

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.File;

/**
 * A TagFactory is the class that links the version independent classes to the version
 * dependent ones. It creates proxies wrapping the version dependent nms classes. These
 * proxies delegate actions to their nms handle. See {@link NBTBaseTag} for this basic
 * interaction.
 */
public abstract class TagFactory {
    private static final String IMPL_PACKAGE = "io.github.mrblobman.nbt.";
    private static final String CLASS_NAME = ".TagFactory";
    private static TagFactory factory;

    /**
     * Get the instance of a {@link TagFactory} that supports the current
     * server version.
     * @return the appropriate instance for the current server version
     * @throws UnsupportedOperationException if there is no support for the current version.
     */
    public static TagFactory get() {
        if (factory != null) return factory;
        String implVersion;
        try {
            implVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnsupportedOperationException("A TagFactory can only be instantiated on a Spigot server.");
        }
        try {
            Class<?> implClass = Class.forName(IMPL_PACKAGE + implVersion + CLASS_NAME);
            return factory = (TagFactory) implClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new UnsupportedOperationException("A TagFactory implementation is not defined for your version "+implVersion);
        }
    }

    /**
     * @return the {@link NBTIODelegate} for {@link ItemStack} IO tasks.
     */
    public abstract NBTIODelegate<ItemStack> getItemIODelegate();

    /**
     * @return the {@link NBTIODelegate} for {@link Entity} IO tasks.
     */
    public abstract NBTIODelegate<Entity> getEntityIODelegate();

    /**
     * NOTE: This delegate must assume that any {@link BlockState}s given
     * to it are tile entities! There is no exposed interface in Spigot for
     * the sub set of BlockStates that are tile entities so it is left to the caller
     * to ensure this safety.
     * @return the {@link NBTIODelegate} for {@link BlockState} IO tasks.
     */
    public abstract NBTIODelegate<BlockState> getBlockIODelegate();

    /**
     * @return the {@link NBTIODelegate} for {@link File} IO tasks.
     */
    public abstract NBTIODelegate<File> getFileIODelegate();

    /**
     * Wrap the value in a byte tag.
     * @param value the value to wrap
     * @return a new byte tag with the given value
     */
    public abstract NBTBaseTag<Byte> newByteTag(byte value);

    protected abstract NBTBaseTag<Byte> wrapByteTag(Object handle);

    /**
     * Wrap the value in a short tag.
     * @param value the value to wrap
     * @return a new short tag with the given value
     */
    public abstract NBTBaseTag<Short> newShortTag(short value);

    protected abstract NBTBaseTag<Short> wrapShortTag(Object handle);

    /**
     * Wrap the value in a int tag.
     * @param value the value to wrap
     * @return a new int tag with the given value
     */
    public abstract NBTBaseTag<Integer> newIntTag(int value);

    protected abstract NBTBaseTag<Integer> wrapIntTag(Object handle);

    /**
     * Wrap the value in a long tag.
     * @param value the value to wrap
     * @return a new long tag with the given value
     */
    public abstract NBTBaseTag<Long> newLongTag(long value);

    protected abstract NBTBaseTag<Long> wrapLongTag(Object handle);

    /**
     * Wrap the value in a float tag.
     * @param value the value to wrap
     * @return a new float tag with the given value
     */
    public abstract NBTBaseTag<Float> newFloatTag(float value);

    protected abstract NBTBaseTag<Float> wrapFloatTag(Object handle);

    /**
     * Wrap the value in a double tag.
     * @param value the value to wrap
     * @return a new double tag with the given value
     */
    public abstract NBTBaseTag<Double> newDoubleTag(double value);

    protected abstract NBTBaseTag<Double> wrapDoubleTag(Object handle);

    /**
     * Wrap the value in a byte[] tag.
     * @param value the value to wrap
     * @return a new byte[] tag with the given value
     */
    public abstract NBTBaseTag<byte[]> newByteArrayTag(byte[] value);

    protected abstract NBTBaseTag<byte[]> wrapByteArrayTag(Object handle);

    /**
     * Wrap the value in a String tag.
     * @param value the value to wrap
     * @return a new String tag with the given value
     */
    public abstract NBTBaseTag<String> newStringTag(String value);

    protected abstract NBTBaseTag<String> wrapStringTag(Object handle);

    /**
     * Create an empty list tag.
     * @return the newly created list tag.
     */
    public abstract NBTListTag newListTag();

    protected abstract NBTListTag wrapListTag(Object handle);

    /**
     * Create an empty compound tag.
     * @return the newly created compound tag.
     */
    public abstract NBTCompoundTag newCompoundTag();

    protected abstract NBTCompoundTag wrapCompoundTag(Object handle);

    /**
     * Wrap the value in a int[] tag.
     * @param value the value to wrap
     * @return a new int[] tag with the given value
     */
    public abstract NBTBaseTag<int[]> newIntArrayTag(int[] value);

    protected abstract NBTBaseTag<int[]> wrapIntArrayTag(Object handle);
}
