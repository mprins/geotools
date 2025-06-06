/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */ package org.geotools.graph.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.geotools.util.Utilities;

// unused class
@Deprecated
public class ZipUtil {

    public static void zip(String zipFilename, String[] filenames) throws IOException {
        zip(zipFilename, filenames, filenames);
    }

    public static void zip(String zipFilename, String[] filenames, String[] archFilenames) throws IOException {
        try (ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilename)))) {

            byte[] data = new byte[512];
            int bc;
            for (int i = 0; i < filenames.length; i++) {
                try (InputStream fin = new BufferedInputStream(new FileInputStream(filenames[i]))) {

                    ZipEntry entry = new ZipEntry(new File(archFilenames[i]).getName());
                    zout.putNextEntry(entry);

                    while ((bc = fin.read(data, 0, 512)) != -1) {
                        zout.write(data, 0, bc);
                    }
                }
                zout.flush();
            }
        }
    }

    @Deprecated
    public static void unzip(String zipFilename, Collection<String> filenames, String outdir) throws IOException {
        unzip(zipFilename, filenames.toArray(new String[filenames.size()]), outdir);
    }

    /** @deprecated this used to only work on windows */
    @Deprecated
    public static void unzip(String zipFilename, String[] filenames, String outdir) throws IOException {

        try (ZipFile zipFile = new ZipFile(zipFilename)) {
            Enumeration entries = zipFile.entries();

            L1:
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                for (String filename : filenames) {
                    if (entry.getName().equals(filename)) {
                        Utilities.assertNotZipSlipVulnarable(new File(outdir, filename), Paths.get(outdir));
                        byte[] buffer = new byte[1024];
                        int len;

                        try (InputStream zipin = zipFile.getInputStream(entry);
                                BufferedOutputStream fileout =
                                        new BufferedOutputStream(new FileOutputStream(new File(outdir, filename)))) {

                            while ((len = zipin.read(buffer)) >= 0) fileout.write(buffer, 0, len);

                            zipin.close();
                            fileout.flush();
                            fileout.close();
                        }

                        continue L1;
                    }
                }
            }
        }
    }
    /** @deprecated this used to only work on windows */
    @Deprecated
    public static void unzip(String zipFilename, String outdir) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFilename)) {
            Enumeration entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                Utilities.assertNotZipSlipVulnarable(new File(outdir, entry.getName()), Paths.get(outdir));
                byte[] buffer = new byte[1024];
                int len;

                try (InputStream zipin = zipFile.getInputStream(entry);
                        BufferedOutputStream fileout =
                                new BufferedOutputStream(new FileOutputStream(new File(outdir, entry.getName())))) {
                    while ((len = zipin.read(buffer)) >= 0) fileout.write(buffer, 0, len);
                }
            }
        }
    }
}
