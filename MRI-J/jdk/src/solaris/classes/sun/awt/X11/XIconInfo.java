/*
 * Copyright 2006-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package sun.awt.X11;
import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import sun.awt.image.ToolkitImage;
import sun.awt.image.ImageRepresentation;
import java.util.Arrays;

class XIconInfo {
    /**
     * Representation of image as an int array
     * It's being used for _NET_WM_ICON hint
     * with 32-bit X data model
     */
    private int[] intIconData;
    /**
     * Representation of image as an int array
     * It's being used for _NET_WM_ICON hint
     * with 64-bit X data model
     */
    private long[] longIconData;
    /**
     * Icon image.
     */
    private Image image;
    /**
     * Width of icon image. Being set in constructor.
     */
    private final int width;
    /**
     * Height of icon image. Being set in constructor.
     */
    private final int height;
    /**
     * Width of scaled icon image. Can be set in setScaledDimension.
     */
    private int scaledWidth;
    /**
     * Height of scaled icon image. Can be set in setScaledDimension.
     */
    private int scaledHeight;
    /**
     * Length of raw data. Being set in constructor / setScaledDimension.
     */
    private int rawLength;

    XIconInfo(int[] intIconData) {
        this.intIconData =
            (null == intIconData) ? null : Arrays.copyOf(intIconData, intIconData.length);
        this.width = intIconData[0];
        this.height = intIconData[1];
        this.scaledWidth = width;
        this.scaledHeight = height;
        this.rawLength = width * height + 2;
    }

    XIconInfo(long[] longIconData) {
        this.longIconData =
        (null == longIconData) ? null : Arrays.copyOf(longIconData, longIconData.length);
        this.width = (int)longIconData[0];
        this.height = (int)longIconData[1];
        this.scaledWidth = width;
        this.scaledHeight = height;
        this.rawLength = width * height + 2;
    }

    XIconInfo(Image image) {
        this.image = image;
        if (image instanceof ToolkitImage) {
            ImageRepresentation ir = ((ToolkitImage)image).getImageRep();
            ir.reconstruct(ImageObserver.ALLBITS);
            this.width = ir.getWidth();
            this.height = ir.getHeight();
        } else {
            this.width = image.getWidth(null);
            this.height = image.getHeight(null);
        }
        this.scaledWidth = width;
        this.scaledHeight = height;
        this.rawLength = width * height + 2;
    }

    /*
     * It sets size of scaled icon.
     */
    void setScaledSize(int width, int height) {
        this.scaledWidth = width;
        this.scaledHeight = height;
        this.rawLength = width * height + 2;
    }

    boolean isValid() {
        return (width > 0 && height > 0);
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    public String toString() {
        return "XIconInfo[w=" + width + ",h=" + height + ",sw=" + scaledWidth + ",sh=" + scaledHeight + "]";
    }

    int getRawLength() {
        return rawLength;
    }

    int[] getIntData() {
        if (this.intIconData == null) {
            if (this.longIconData != null) {
                this.intIconData = longArrayToIntArray(longIconData);
            } else if (this.image != null) {
                this.intIconData = imageToIntArray(this.image, scaledWidth, scaledHeight);
            }
        }
        return this.intIconData;
    }

    long[] getLongData() {
        if (this.longIconData == null) {
            if (this.intIconData != null) {
                this.longIconData = intArrayToLongArray(this.intIconData);
            } else if (this.image != null) {
                int[] intIconData = imageToIntArray(this.image, scaledWidth, scaledHeight);
                this.longIconData = intArrayToLongArray(intIconData);
            }
        }
        return this.longIconData;
    }

    Image getImage() {
        if (this.image == null) {
            if (this.intIconData != null) {
                this.image = intArrayToImage(this.intIconData);
            } else if (this.longIconData != null) {
                int[] intIconData = longArrayToIntArray(this.longIconData);
                this.image = intArrayToImage(intIconData);
            }
        }
        return this.image;
    }

    private static int[] longArrayToIntArray(long[] longData) {
        int[] intData = new int[longData.length];
        for (int i = 0; i < longData.length; i++) {
            // Such a conversion is valid since the
            // original data (see
            // make/sun/xawt/ToBin.java) were ints
            intData[i] = (int)longData[i];
        }
        return intData;
    }

    private static long[] intArrayToLongArray(int[] intData) {
        long[] longData = new long[intData.length];
        for (int i = 0; i < intData.length; i++) {
            longData[i] = (int)intData[i];
        }
        return longData;
    }

    static Image intArrayToImage(int[] raw) {
        ColorModel cm =
            new DirectColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), 32,
                                 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000,
                                 false, DataBuffer.TYPE_INT);
        DataBuffer buffer = new DataBufferInt(raw, raw.length-2, 2);
        WritableRaster raster =
            Raster.createPackedRaster(buffer, raw[0], raw[1],
                                      raw[0],
                                      new int[] {0x00ff0000, 0x0000ff00,
                                                 0x000000ff, 0xff000000},
                                      null);
        BufferedImage im = new BufferedImage(cm, raster, false, null);
        return im;
    }

    /*
     * Returns array of integers which holds data for the image.
     * It scales the image if necessary.
     */
    static int[] imageToIntArray(Image image, int width, int height) {
        if (width <= 0 || height <= 0) {
            return null;
        }
        ColorModel cm =
            new DirectColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), 32,
                                 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000,
                                 false, DataBuffer.TYPE_INT);
        DataBufferInt buffer = new DataBufferInt(width * height);
        WritableRaster raster =
            Raster.createPackedRaster(buffer, width, height,
                                      width,
                                      new int[] {0x00ff0000, 0x0000ff00,
                                                 0x000000ff, 0xff000000},
                                      null);
        BufferedImage im = new BufferedImage(cm, raster, false, null);
        Graphics g = im.getGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        int[] data = buffer.getData();
        int[] raw = new int[width * height + 2];
        raw[0] = width;
        raw[1] = height;
        System.arraycopy(data, 0, raw, 2, width * height);
        return raw;
    }

}
