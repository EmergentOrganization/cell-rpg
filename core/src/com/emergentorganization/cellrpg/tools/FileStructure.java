package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import com.emergentorganization.cellrpg.PixelonTransmission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by BrianErikson on 10/21/15.
 */
public class FileStructure {
    private final Logger logger = LogManager.getLogger(getClass());
    private boolean isJar = false;
    public static final String RESOURCE_DIR = "resources" + File.separator;

    private static class FileStructureHolder {
        private static final FileStructure INSTANCE = new FileStructure();
    }

    public static FileStructure fetch() {
        return FileStructureHolder.INSTANCE;
    }

    public void initialize() {
        if (!Gdx.files.internal(RESOURCE_DIR + "property.settings").file().exists()) { // Must be in a JAR
            logger.info("JAR detected; unpacking assets");
            isJar = true;
            unpackAssets();
        }
    }

    /**
     * Unpacks assets into root directory. Does not overwrite files if they are already there.
     */
    private void unpackAssets() {
        URL url = PixelonTransmission.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
            JarFile jar = new JarFile(jarPath);
            Enumeration<JarEntry> iter = jar.entries();

            while (iter.hasMoreElements()) {
                JarEntry entry = iter.nextElement();
                String rootDir = jarPath.substring(0, jarPath.lastIndexOf(File.separator)) + File.separator;
                File file = new File(rootDir + entry.getName());

                if (file.getAbsolutePath().contains(rootDir + RESOURCE_DIR.substring(0, RESOURCE_DIR.length() - 1)) &&
                        !file.getAbsolutePath().contains("unpacked")) {
                    if (file.exists()) {
                        continue;
                    }

                    if (entry.isDirectory()) {
                        file.mkdir();
                        continue;
                    }
                    InputStream is = jar.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] buf = new byte[4096];
                    int r;
                    while ((r = is.read(buf)) != -1) {
                        fos.write(buf, 0, r);
                    }
                    fos.close();
                    is.close();
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
