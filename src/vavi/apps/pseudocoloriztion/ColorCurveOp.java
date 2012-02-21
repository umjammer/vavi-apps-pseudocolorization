/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *  
 * Original
 *  http://www.gimp.org/
 *  http://avisynth.org.ru/docs/english/externalfilters/gicocu.htm
 */

package vavi.apps.pseudocoloriztion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.graphics.Bitmap;


/**
 * ColorCurveOp. 
 *
 * @author E-Male 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/02/07 umjammer initial version <br>
 */
public class ColorCurveOp {

    /** */
    public static class Curves {

        /** */
        private class CRMatrix {
            /** */
            float[][] data;

            /** */
            public CRMatrix() {
                this.data = new float[4][4];
            }

            /** */
            public CRMatrix(float[][] data) {
                this.data = data;
            }

            /** */
            public CRMatrix compose(CRMatrix b) {
                CRMatrix result = new CRMatrix();
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        result.data[i][j] = this.data[i][0] * b.data[0][j] +
                                            this.data[i][1] * b.data[1][j] +
                                            this.data[i][2] * b.data[2][j] +
                                            this.data[i][3] * b.data[3][j];
                    }
                }
                return result;
            }
        }

        /** */
        int[][][] points = new int[5][17][2];
        /** */
        int[][] curve = new int[5][256];

        /** */
        void calculateCurve(int channel) {
            
            int[] points = new int[17];

            int num_pts = 0;
            for (int i = 0; i < 17; i++) {
                if (this.points[channel][i][0] != -1) {
                    points[num_pts++] = i;
                }
            }

            if (num_pts != 0) {
                for (int i = 0; i < this.points[channel][points[0]][0]; i++) {
                    this.curve[channel][i] = this.points[channel][points[0]][1];
                }
                for (int i = this.points[channel][points[num_pts - 1]][0]; i < 256; i++) {
                    this.curve[channel][i] = this.points[channel][points[num_pts - 1]][1];
                }
            }

            for (int i = 0; i < num_pts - 1; i++) {
                int p1 = (i == 0) ? points[i] : points[(i - 1)];
                int p2 = points[i];
                int p3 = points[(i + 1)];
                int p4 = (i == (num_pts - 2)) ? points[(num_pts - 1)] : points[(i + 2)];

                plotCurve(channel, p1, p2, p3, p4);
            }

            for (int i = 0; i < num_pts; i++) {
                int x = this.points[channel][points[i]][0];
                int y = this.points[channel][points[i]][1];
                this.curve[channel][x] = y;
            }
        }

        /** */
        private static final int round(float x) {
            return (int) (x + 0.5f);
        }

        /** */
        private static final int clamp(float x, float l, float u) {
            return (int) ((x < l) ? l : (x > u) ? u : x);
        }

        /** */
        private static final int clamp0255(float a) {
            return clamp(a, 0, 255);
        }

        /** */
        private void plotCurve(int channel, int p1, int p2, int p3, int p4) {

            // construct the geometry matrix from the segment
            CRMatrix geometry = new CRMatrix();
            for (int i = 0; i < 4; i++) {
                geometry.data[i][2] = 0;
                geometry.data[i][3] = 0;
            }

            for (int i = 0; i < 2; i++) {
                geometry.data[0][i] = this.points[channel][p1][i];
                geometry.data[1][i] = this.points[channel][p2][i];
                geometry.data[2][i] = this.points[channel][p3][i];
                geometry.data[3][i] = this.points[channel][p4][i];
            }

            // subdivide the curve 1000 times
            // n can be adjusted to give a finer or coarser curve
            float d = 1.0f / 1000;
            float d2 = d * d;
            float d3 = d * d * d;

            // construct a temporary matrix for determining the forward differencing
            // deltas
            CRMatrix tmp2 = new CRMatrix();
            tmp2.data[0][0] = 0;
            tmp2.data[0][1] = 0;
            tmp2.data[0][2] = 0;
            tmp2.data[0][3] = 1;
            tmp2.data[1][0] = d3;
            tmp2.data[1][1] = d2;
            tmp2.data[1][2] = d;
            tmp2.data[1][3] = 0;
            tmp2.data[2][0] = 6 * d3;
            tmp2.data[2][1] = 2 * d2;
            tmp2.data[2][2] = 0;
            tmp2.data[2][3] = 0;
            tmp2.data[3][0] = 6 * d3;
            tmp2.data[3][1] = 0;
            tmp2.data[3][2] = 0;
            tmp2.data[3][3] = 0;

            final CRMatrix basis = new CRMatrix(new float[][] {
                { -0.5f, 1.5f, -1.5f, 0.5f },
                { 1.0f, -2.5f, 2.0f, -0.5f },
                { -0.5f, 0.0f, 0.5f, 0.0f },
                { 0.0f, 1.0f, 0.0f, 0.0f }
            });

            // compose the basis and geometry matrices
            CRMatrix tmp1 = basis.compose(geometry);

            // compose the above results to get the deltas matrix
            CRMatrix deltas = tmp2.compose(tmp1);

            // extract the x deltas
            float x = deltas.data[0][0];
            float dx = deltas.data[1][0];
            float dx2 = deltas.data[2][0];
            float dx3 = deltas.data[3][0];

            // extract the y deltas
            float y = deltas.data[0][1];
            float dy = deltas.data[1][1];
            float dy2 = deltas.data[2][1];
            float dy3 = deltas.data[3][1];

            int lastx = clamp(x, 0, 255);
            int lasty = clamp(y, 0, 255);

            this.curve[channel][lastx] = lasty;

            // loop over the curve
            for (int i = 0; i < 1000; i++) {
                // increment the x values
                x += dx;
                dx += dx2;
                dx2 += dx3;

                // increment the y values
                y += dy;
                dy += dy2;
                dy2 += dy3;

                int newx = clamp0255(round(x));
                int newy = clamp0255(round(y));

                // if this point is different than the last one...then draw it
                if ((lastx != newx) || (lasty != newy)) {
                    this.curve[channel][newx] = newy;
                }

                lastx = newx;
                lasty = newy;
            }
        }

        void resetChannel(int channel) {
            for (int j = 0; j < 256; j++) {
                this.curve[channel][j] = j;
            }

            for (int j = 0; j < 17; j++) {
                this.points[channel][j][0] = -1;
                this.points[channel][j][1] = -1;
            }

            this.points[channel][0][0] = 0;
            this.points[channel][0][1] = 0;
            this.points[channel][16][0] = 255;
            this.points[channel][16][1] = 255;
        }

        void applyCurveHsv(int[] r, int[] g, int[] b) {
            int x, y;
            // RGB to HSV (x=H y=S z=V)
            int cmin = Math.min(r[0], g[0]);
            cmin = Math.min(b[0], cmin);
            int z = Math.max(r[0], g[0]);
            z = Math.max(b[0], z);
            int cdelta = z - cmin;
            if (cdelta != 0) {
                y = (cdelta << 8) / z;
                if (y > 255)
                    y = 255;
                if (r[0] == z) {
                    x = ((g[0] - b[0]) << 8) / cdelta;
                } else if (g[0] == z) {
                    x = 512 + (((b[0] - r[0]) << 8) / cdelta);
                } else {
                    x = 1024 + (((r[0] - g[0]) << 8) / cdelta);
                }
                if (x < 0) {
                    x = x + 1536;
                }
                x = x / 6;
            } else {
                y = 0;
                x = 0;
            }

            // Applying the curves
            x = this.curve[1][x];
            y = this.curve[2][y];
            z = this.curve[3][z];

            // HSV to RGB
            if (y == 0) {
                r[0] = z;
                g[0] = z;
                b[0] = z;
            } else {
                int chi = (x * 6) >> 8;
                int ch = (x * 6 - (chi << 8));
                int rd = (z * (256 - y)) >> 8;
                int gd = (z * (256 - ((y * ch) >> 8))) >> 8;
                int bd = (z * (256 - (y * (256 - ch) >> 8))) >> 8;
                if (chi == 0) {
                    r[0] = z;
                    g[0] = bd;
                    b[0] = rd;
                } else if (chi == 1) {
                    r[0] = gd;
                    g[0] = z;
                    b[0] = rd;
                } else if (chi == 2) {
                    r[0] = rd;
                    g[0] = z;
                    b[0] = bd;
                } else if (chi == 3) {
                    r[0] = rd;
                    g[0] = gd;
                    b[0] = z;
                } else if (chi == 4) {
                    r[0] = bd;
                    g[0] = rd;
                    b[0] = z;
                } else {
                    r[0] = z;
                    g[0] = rd;
                    b[0] = gd;
                }
            }
        }
    }

    /** */
    public static interface CurvesFactory {
        Curves getCurves(InputStream is) throws IOException;
    }

    /** */
    public static class PhotoShopCurvesFactory implements CurvesFactory {
        @Override
        public Curves getCurves(InputStream is) throws IOException {
            Curves curves = new Curves();
            for (int a = 0; a < 5; a++) {
                for (int b = 0; b < 256; b++) {
                    curves.curve[a][b] = is.read();
                }
            }
            return curves;
        }
    }

    /** */
    public static class GimpCurvesFactory implements CurvesFactory {
        @Override
        public Curves getCurves(InputStream is) throws IOException {
            Curves curves = new Curves();

            int[][] index = new int[5][17];
            int[][] value = new int[5][17];

            Scanner scanner = new Scanner(is);

            String header = scanner.nextLine();

//System.err.println(header);
            if (!"# GIMP Curves File".equals(header)) {
                throw new IOException("not gimp curves file");
            }

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 17; j++) {
                    index[i][j] = scanner.nextInt();
                    value[i][j] = scanner.nextInt();
//System.err.printf("index: %d, value: %d\n", index[i][j], value[i][j]);
                }
            }

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 17; j++) {
                    curves.points[i][j][0] = index[i][j];
                    curves.points[i][j][1] = value[i][j];
                }
            }

            // make LUTs
            for (int i = 0; i < 5; i++) {
                curves.calculateCurve(i);
            }

            return curves;
        }
    }

    /** */
    private Curves curves;

    /** */ 
    public ColorCurveOp(Curves curves) {
        this.curves = curves;
    }

    /**
     * 
     * @param src RGB24 & RGB32 only! 
     * @param dst 
     */
    public Bitmap filter(Bitmap src) {
        Bitmap dst = src.copy(src.getConfig(), true);
        src.recycle();

        int width = dst.getWidth();
        int height = dst.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = dst.getPixel(x, y);
                int a = curves.curve[4][(rgb & 0xff000000) >>> 24];
                int r = curves.curve[0][curves.curve[1][(rgb & 0x00ff0000) >> 16]];
                int g = curves.curve[0][curves.curve[2][(rgb & 0x0000ff00) >> 8]];
                int b = curves.curve[0][curves.curve[3][rgb & 0x000000ff]];
                dst.setPixel(x, y, a << 24 | r << 16 | g << 8 | b);
            }
        }

        return dst;
    }
}

/* */