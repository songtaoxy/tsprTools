package com.st.utils.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * @author: st
 * @date: 2021/12/29 09:54
 * @version: 1.0
 * @description:
 */

/**
 * 复制目录(递归),单线程.<p></p>
 *
 * The visitor pattern introduced in Java 1.7 is based on {@link java.nio.file.FileVisitor} interface. Instead of implementing this interface, this example extends {@link java.nio.file.SimpleFileVisitor} which has default behavior to visit all files.
 */
public class CopyDirSingle extends SimpleFileVisitor<Path>{
  private Path fromPath;
  private Path toPath;
  private StandardCopyOption copyOption;


  /**
   * Creating main class for a test.
   * @param args
   * @throws IOException
   */
  public static void main (String[] args) throws IOException {
    CopyUtil.copyDirectoryContent(new File("/Users/songtao/personaldriveMac/baklist/"), new File("/Users/songtao/downloads/"));
  }

  public CopyDirSingle(Path fromPath, Path toPath, StandardCopyOption copyOption) {
    this.fromPath = fromPath;
    this.toPath = toPath;
    this.copyOption = copyOption;
  }

  public CopyDirSingle(Path fromPath, Path toPath) {
    this(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

    Path targetPath = toPath.resolve(fromPath.relativize(dir));
    if (!Files.exists(targetPath)) {
      Files.createDirectory(targetPath);
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

    Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);
    return FileVisitResult.CONTINUE;
  }
}


/**
 * 工具类
 */
class CopyUtil {
  public static void copyDirectoryContent (File sourceFolder,
                                           File destinationFolder) throws IOException {
    if (sourceFolder.isDirectory()) {

      if (destinationFolder.exists() && destinationFolder.isFile()) {
        throw new IllegalArgumentException(
                "Destination exists but is not a folder: "
                        + destinationFolder
                        .getAbsolutePath());
      }

      if (!destinationFolder.exists()) {
        Files.createDirectory(destinationFolder.toPath());
      }

      for (File file : sourceFolder.listFiles()) {
        if (file.isDirectory()) {
          copyDirectory(file, destinationFolder);
        } else {
          copyFile(file, destinationFolder);
        }
      }
    }
  }

  public static void copyDirectory (File fromFile, File toParentFile)
          throws IOException {
    Path from = fromFile.toPath();
    Path to = Paths.get(toParentFile.getAbsolutePath() + File.separatorChar + fromFile
            .getName());

    Files.walkFileTree(from, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
            Integer.MAX_VALUE, new CopyDirSingle(from, to));
  }

  public static void copyFile (File toCopy, File mainDestination)
          throws IOException {
    if (!mainDestination.exists()) {
      mainDestination.mkdirs();
    }
    Path to = Paths.get(mainDestination.getAbsolutePath() +
            File.separatorChar + toCopy.getName());

    Files.copy(toCopy.toPath(), to, StandardCopyOption.REPLACE_EXISTING);
  }
}