package org.geotools.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.IndexColorModel;
import org.geotools.image.util.ColorUtilities;
import org.junit.Test;

public class ColorUtilitiesTest {

    @Test
    public void mergeSolidBackgroundSolidPalette() {
        byte[] r = {-1, 0, 0};
        byte[] g = {0, -1, 0};
        byte[] b = {0, 0, -1};
        IndexColorModel icm = new IndexColorModel(2, 3, r, g, b);
        IndexColorModel merged = ColorUtilities.applyBackgroundColor(icm, Color.WHITE);
        assertFalse(merged.hasAlpha());
        assertEquals(merged.getTransparency(), Transparency.OPAQUE);
        byte[] mr = new byte[3];
        byte[] mb = new byte[3];
        byte[] mg = new byte[3];
        merged.getReds(mr);
        merged.getGreens(mg);
        merged.getBlues(mb);
        for (int i = 0; i < r.length; i++) {
            assertEquals(r[i], mr[i]);
            assertEquals(g[i], mg[i]);
            assertEquals(b[i], mb[i]);
        }
    }

    @Test
    public void mergeSolidBackgroundTranslucentPalette() {
        byte[] r = {-1, 0, 0};
        byte[] g = {0, -1, 0};
        byte[] b = {0, 0, -1};
        byte[] a = {-128, -128, -128};
        IndexColorModel icm = new IndexColorModel(2, 3, r, g, b, a);
        IndexColorModel merged = ColorUtilities.applyBackgroundColor(icm, Color.WHITE);
        assertFalse(merged.hasAlpha());
        assertEquals(merged.getTransparency(), Transparency.OPAQUE);
        byte[] mr = new byte[3];
        byte[] mb = new byte[3];
        byte[] mg = new byte[3];
        merged.getReds(mr);
        merged.getGreens(mg);
        merged.getBlues(mb);
        for (int i = 0; i < r.length; i++) {
            assertEquals((r[i] & 0xFF) == 255 ? 255 : 126, mr[i] & 0xFF);
            assertEquals((g[i] & 0xFF) == 255 ? 255 : 126, mg[i] & 0xFF);
            assertEquals((b[i] & 0xFF) == 255 ? 255 : 126, mb[i] & 0xFF);
        }
    }

    @Test
    public void mergeTranslucentBackgroundTranslucentPalette() {
        byte[] r = {-1, 0, 0};
        byte[] g = {0, -1, 0};
        byte[] b = {0, 0, -1};
        byte[] a = {-128, -128, -128};
        IndexColorModel icm = new IndexColorModel(2, 3, r, g, b, a);
        IndexColorModel merged = ColorUtilities.applyBackgroundColor(icm, new Color(255, 255, 255, 128));
        assertTrue(merged.hasAlpha());
        assertEquals(merged.getTransparency(), Transparency.TRANSLUCENT);
        byte[] mr = new byte[3];
        byte[] mb = new byte[3];
        byte[] mg = new byte[3];
        byte[] ma = new byte[3];
        merged.getReds(mr);
        merged.getGreens(mg);
        merged.getBlues(mb);
        merged.getAlphas(ma);
        for (int i = 0; i < r.length; i++) {
            assertEquals((r[i] & 0xFF) == 255 ? 255 : 84, mr[i] & 0xFF);
            assertEquals((g[i] & 0xFF) == 255 ? 255 : 84, mg[i] & 0xFF);
            assertEquals((b[i] & 0xFF) == 255 ? 255 : 84, mb[i] & 0xFF);
            assertEquals(191, ma[i] & 0xFF);
        }
    }
}
