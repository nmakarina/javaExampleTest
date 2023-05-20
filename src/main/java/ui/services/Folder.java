package ui.services;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class Folder {
    private static String path;
    protected static String unzipPath = System.getProperty("user.dir") + "\\unzip\\unzip";

    private void createDir(String fullPath) {
        try {
            Path path = Paths.get(fullPath);
            if (Files.exists(path)) return;
            Files.createDirectory(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cant create folder " + fullPath);
        }
    }

    public String path() {
        return Folder.path == null ? "" : Folder.path;
    }


    public static boolean findFile(final String filename) {
        final File file = new File(filename);
        return file.exists();
    }

    public static boolean createFolder(final String folder) {
        final File file = new File(folder);
        return file.mkdir();
    }

    public static boolean renameAll(final String olddir, final String newdir) {
        File dir = new File(olddir);
        boolean res = true;
        if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles()) {
                try {
                    if (f.isFile()){
                        if (f.getName().length()>45)
                        {
                            res &= f.renameTo(new File(newdir, f.getName().subSequence(0,40).toString() + f.getName().subSequence(f.getName().length()-5,f.getName().length()).toString()));
                            //System.out.println("Rename file "+f.getAbsolutePath());
                            //final File ff = new File(newdir).listFiles()[0];
                            //System.out.println("to "+ ff.getAbsolutePath());
                        }
                        else {
                            res &= f.renameTo(new File(newdir, f.getName()));
                        }}
                } catch (Exception e) {
                    res = false;
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    public static void recursiveDelete(File file) {
        // до конца рекурсивного цикла
        if (!file.exists())
            return;
        //если это папка, то идем внутрь этой папки и вызываем рекурсивное удаление всего, что там есть
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                // рекурсивный вызов
                recursiveDelete(f);
            }
        }
        // вызываем метод delete() для удаления файлов и пустых(!) папок
        file.delete();
        System.out.println("Удаленный файл или папка: " + file.getAbsolutePath());
    }
}
