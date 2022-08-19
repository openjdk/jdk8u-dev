/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


// common infrastructure for Secmod tests

import java.io.*;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Provider;

public class SecmodTest extends PKCS11Test {

    static String LIBPATH;
    static String DBDIR;
    static char[] password = "test12".toCharArray();
    static String keyAlias = "mykey";
    static boolean useSqlite = false;

    static void useSqlite(boolean b) {
        useSqlite = b;
    }

    static boolean initSecmod() throws Exception {
        useNSS();
        LIBPATH = getNSSLibDir();
        if (LIBPATH == null) {
            return false;
        }
        // load all the libraries except libnss3 into memory
        if (loadNSPR(LIBPATH) == false) {
            return false;
        }
        safeReload(LIBPATH + System.mapLibraryName("softokn3"));
        safeReload(LIBPATH + System.mapLibraryName("nssckbi"));

        DBDIR = System.getProperty("test.classes", ".") + SEP + "tmpdb";
        if (useSqlite) {
            System.setProperty("pkcs11test.nss.db", "sql:" + DBDIR);
        } else {
            System.setProperty("pkcs11test.nss.db", DBDIR);
        }
        File dbdirFile = new File(DBDIR);
        if (dbdirFile.exists()) {
            deleteDir(dbdirFile.toPath());
        }
        dbdirFile.mkdir();

        if (useSqlite) {
            copyFile("key4.db", BASE, DBDIR);
            copyFile("cert9.db", BASE, DBDIR);
            copyFile("pkcs11.txt", BASE, DBDIR);
        } else {
            copyFile("secmod.db", BASE, DBDIR);
            copyFile("key3.db", BASE, DBDIR);
            copyFile("cert8.db", BASE, DBDIR);
        }
        return true;
    }

    private static void copyFile(String name, String srcDir, String dstDir) throws IOException {
        InputStream in = new FileInputStream(new File(srcDir, name));
        OutputStream out = new FileOutputStream(new File(dstDir, name));
        byte[] buf = new byte[2048];
        while (true) {
            int n = in.read(buf);
            if (n < 0) {
                break;
            }
            out.write(buf, 0, n);
        }
        in.close();
        out.close();
    }

    private static void deleteDir(final Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void main(Provider p) throws Exception {
        // dummy
    }

}
