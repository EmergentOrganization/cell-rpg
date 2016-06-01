package io.github.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class FileStructure {
    public static final String RESOURCE_DIR = "resources/";
    private final Logger logger = LogManager.getLogger(getClass());
    private boolean isJar = false;

    public FileStructure() {
        if (!Gdx.files.internal(RESOURCE_DIR + "property.settings").file().exists()) { // Must be in a JAR
            logger.info("JAR detected");
            isJar = true;
        }
    }

    /**
     * Unpacks assets into root directory. Does not overwrite files if they are already there.
     */
    public void unpackAssets() {
        URL url = PixelonTransmission.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println("copying assets...");
        System.out.println("URL:" + url);
        System.out.println("ext:" + Gdx.files.getExternalStoragePath());
        System.out.println("loc:" + Gdx.files.getLocalStoragePath());
        try {
            String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
            jarPath = jarPath.replace("/", File.separator);  // b/c URLDecoder is platform-independent
            System.out.println("pth:" + jarPath);
            System.out.println("sep:" + File.separator);
            JarFile jar = new JarFile(jarPath);
            System.out.println("found " + jar.size() + " entries...");
            Enumeration<JarEntry> iter = jar.entries();

            while (iter.hasMoreElements()) {
                JarEntry entry = iter.nextElement();
                String rootDir = jarPath.substring(0, jarPath.lastIndexOf(File.separator)) + File.separator;
                File file = new File(rootDir + entry.getName());

                String s1 = rootDir + RESOURCE_DIR.substring(0, RESOURCE_DIR.length() - 1);
                boolean t1 = (File.separator + file.getAbsolutePath()).contains(s1);
                System.out.println(file.getAbsolutePath());
                //System.out.println(s1);
                System.out.println("tst:" + t1 );

                boolean isNotPrepackedFile = !file.getAbsolutePath().contains("unpacked");
                if (t1 && isNotPrepackedFile) {
                    if (file.exists()) {
                        System.out.println("skip existing file");
                        continue;
                    }

                    if (entry.isDirectory()) {
                        file.mkdir();
                        continue;
                    }

                    System.out.println("cp " + file.getAbsolutePath());

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

    public boolean isJar() {
        return isJar;
    }
}
