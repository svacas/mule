/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.http.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils
{
    private static final int  BUFFER_SIZE = 4096;

    private static void extractFile(ZipInputStream in, File outdir, String name) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir,name)));
        int count = -1;
        while ((count = in.read(buffer)) != -1)
            out.write(buffer, 0, count);
        out.close();
    }

    private static void mkdirs(File outdir,String path)
    {
        File d = new File(outdir, path);
        if( !d.exists() )
            d.mkdirs();
    }

    private static String dirpart(String name)
    {
        int s = name.lastIndexOf( File.separatorChar );
        return s == -1 ? null : name.substring( 0, s );
    }

    /***
     * Extract zipfile to outdir with complete directory structure
     * @param zipInputStream Input .zip file
     * @param outdir Output directory
     */
    public static void extract(InputStream zipInputStream, File outdir)
    {
        try
        {
            ZipInputStream zin = new ZipInputStream(zipInputStream);
            ZipEntry entry;
            String name, dir;
            while ((entry = zin.getNextEntry()) != null)
            {
                name = entry.getName();
                if( entry.isDirectory() )
                {
                    mkdirs(outdir,name);
                    continue;
                }
        /* this part is necessary because file entry can come before
         * directory entry where is file located
         * i.e.:
         *   /foo/foo.txt
         *   /foo/
         */
                dir = dirpart(name);
                if( dir != null )
                    mkdirs(outdir,dir);

                extractFile(zin, outdir, name);
            }
            zin.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
