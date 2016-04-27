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

import java.io.PrintStream;

/**
 * Created on 2016-04-26.
 */
public class NBTDeserializationException extends NBTException {
    private NBTBaseTag tag;

    public NBTDeserializationException(NBTBaseTag tag) {
        super();
        this.tag = tag;
    }

    public NBTDeserializationException(NBTBaseTag tag, String message) {
        super(message);
        this.tag = tag;
    }

    public NBTDeserializationException(NBTBaseTag tag, String message, Throwable cause) {
        super(message, cause);
        this.tag = tag;
    }

    public NBTDeserializationException(NBTBaseTag tag, Throwable cause) {
        super(cause);
        this.tag = tag;
    }

    public NBTBaseTag getTag() {
        return this.tag;
    }

    /**
     * Print out the tag.
     * @return itself for chaining a call to {@link #printStackTrace()}
     * afterwards if desired.<br>
     * Ex: {@code e.printTag().printStackTrace();}
     */
    public NBTException printTag() {
        System.err.print("Tag: ");
        System.err.println(this.tag);
        return this;
    }

    /**
     * Print out the tag.
     * @param stream the output stream to print to
     * @return itself for chaining a call to {@link #printStackTrace()}
     * afterwards if desired.<br>
     * Ex: {@code e.printTag(stream).printStackTrace(stream);}
     */
    public NBTException printTag(PrintStream stream) {
        stream.print("Tag: ");
        stream.println(this.tag);
        return this;
    }
}
