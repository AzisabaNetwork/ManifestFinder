package net.azisaba.manifestFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public class Main {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: ManifestFinder.jar [directory to find jars that contains MANIFEST.MF]");
            return;
        }
        Files.walk(new File(args[0]).toPath(), 1).forEach(path -> {
            File file = path.toFile();
            if (!file.getName().endsWith(".jar")) return;
            try {
                ZipFile zip = new ZipFile(file);
                JarFile jar = new JarFile(file);
                JarEntry entry = jar.getJarEntry("META-INF/MANIFEST.MF");
                if (entry == null) return;
                jar.getManifest();
                System.out.println(ANSI_GREEN + file.getAbsolutePath() + " contains MANIFEST.MF:" + ANSI_RESET);
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)));
                } catch (SecurityException e) {
                    System.out.println(ANSI_YELLOW + "[!] SecurityException: " + e.getMessage() + ANSI_RESET);
                    reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
                }
                if (entry.getCertificates() != null) System.out.println(ANSI_YELLOW + "[!] Contains certificates" + ANSI_RESET);
                if (entry.getCodeSigners() != null) System.out.println(ANSI_YELLOW + "[!] Contains code signers" + ANSI_RESET);
                System.out.println("----------");
                reader.lines().forEach(s -> {
                    if (s.length() != 0) {
                        System.out.println(ANSI_CYAN + s + ANSI_RESET);
                    }
                });
                System.out.println("--------------------------------------------------");
            } catch (Exception e) {
                System.err.println(ANSI_RED + file.getAbsolutePath() + " contains invalid zip file" + ANSI_RESET);
                e.printStackTrace();
            }
        });
    }
}
