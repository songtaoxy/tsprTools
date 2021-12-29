package com.st.utils.io;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.*;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
/**
 * @author: st
 * @date: 2021/12/29 03:11
 * @version: 1.0
 * @description:
 */

/**
 * 复制目录(多线程). java-copy-dir-in-parallel<p></p>
 *
 * <a href="https://www.logicbig.com/how-to/java-io/copy-dir-in-parallel.html">See: Java IO -
 * Copy * Directories In Parallel</a><p>
 *
 * We have seen {@link CopyDirSingle} how to copy directories recursively. To take advantage of
 * multiple cores we can create independent child directories in different threads. Following
 * example shows how to do that<p>
 *
 *
 * <pre>
 *     Output:
 *     {@code
 *     Directory creation time taken: 1.668 seconds
 *     Files copy time taken: 26.43 seconds
 *     Setting directories date attributes time taken: 967.7 milliseconds
 *     }
 *     The size of above directory is 839MB.
 *     When I don't use parallel(), the output is:
 *     {@code
 *     Directory creation time taken: 3.077 seconds
 *     Files copy time taken: 49.71 seconds
 *     Setting directories date attributes time taken: 1.606 seconds
 *     }
 * </pre>
 */
public class CopyDirParallel {

	/**
	 * Creating main class for a test.
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		copyDirInParallel("/Users/songtao/personaldriveMac/baklist", "/Users/songtao/downloads/");
	}

	public static void copyDirInParallel(String srcDir, String destinationParentDir) throws IOException {
		Path source = Paths.get(srcDir);

		Path destination = Paths.get(destinationParentDir).resolve(source.getFileName().toString());
		if (!Files.exists(destination)) {
			Files.createDirectories(destination);
		}
		//first create all Directories to avoid many
		// threads trying to create same child/parent directories
		TimerUtil.runTask("Directory creation", () -> {
			try {
				createDirectories(source, destination);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		TimerUtil.runTask("Files copy", () -> {
			try {
				copyFiles(source, destination);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		TimerUtil.runTask("Setting directories date attributes", () -> {
			try {//it has to be at end because copying files under
				// directories (above process) itself modifies the dates
				copyDirectoriesDateAttributes(source, destination);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static void copyFiles(Path source, Path destination) throws IOException {
		Files.walk(source, FileVisitOption.FOLLOW_LINKS)
				.parallel()
				.filter(Files::isRegularFile)
				.forEach(srcFile -> {
					try {
						CmdProgress.animate();
						Path destFile = destination
								.resolve(source.relativize(srcFile));
						if (Files.exists(destFile) &&
								Files.getLastModifiedTime(destFile)
										.compareTo(Files.getLastModifiedTime(srcFile)) >= 0) {
							return;
						}

						Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING,
								StandardCopyOption.COPY_ATTRIBUTES);
						copyDateAttributes(srcFile, destFile);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
		CmdProgress.clearLine();
	}

	private static void createDirectories(Path source, Path destination) throws IOException {
		Files.walk(source,
						FileVisitOption.FOLLOW_LINKS).parallel()
				.filter(Files::isDirectory).filter(CopyDirParallel::haveNoChildFolder).distinct()
				.forEach(srcDir -> {
					try {
						Path relativePath = source.relativize(srcDir);
						Path destDir = destination.resolve(relativePath);
						CmdProgress.animate();
						Files.createDirectories(destDir);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
		CmdProgress.clearLine();
	}

	//Helps to find longest dir path which avoids creating same child/parent
	//directories more than one time

	private static boolean haveNoChildFolder(Path d) {
		File[] files = d.toFile().listFiles();
		if (files == null) {//empty folder
			return true;
		}
		return Arrays.stream(files)
				.noneMatch(File::isDirectory);
	}
	private static void copyDirectoriesDateAttributes(Path source, Path destination)
			throws IOException {
		copyDateAttributes(source, destination);
		Files.walk(source, FileVisitOption.FOLLOW_LINKS)
				.parallel()
				.filter(Files::isDirectory)
				.forEach(srcDir -> {
					try {
						Path relativePath = source.relativize(srcDir);
						Path destDir = destination.resolve(relativePath);
						CmdProgress.animate();
						copyDateAttributes(srcDir, destDir);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
		CmdProgress.clearLine();
	}

	private static void copyDateAttributes(Path sourceFile, Path destFile) throws IOException {
		BasicFileAttributes bfa = Files.readAttributes(sourceFile, BasicFileAttributes.class);
		Files.setAttribute(destFile, "creationTime", bfa.creationTime());
		Files.setAttribute(destFile, "lastModifiedTime", bfa.lastModifiedTime());
		Files.setAttribute(destFile, "lastAccessTime", bfa.lastAccessTime());
	}
}


class CmdProgress {
	private static byte anim = 0;

	public static void animate() {
		switch (anim) {
			case 1:
				System.out.print("\r \\");
				break;
			case 2:
				System.out.print("\r |");
				break;
			case 3:
				System.out.print("\r /");
				break;
			default:
				anim = 0;
				System.out.print("\r -");
		}
		anim++;
	}

	public static void clearLine() {
		System.out.print("\r    \r");
	}
}

class TimerUtil {
	public static void runTask(String msg, Runnable task){
		long startTime = getTimeElapsed(0);
		task.run();
		System.out.printf("%s time taken: %s%n", msg, timeToString(getTimeElapsed(startTime)));
	}

	public static String timeToString(long nanos) {

		Optional<TimeUnit> first = Stream.of(DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS,
						MICROSECONDS).filter(u -> u.convert(nanos, NANOSECONDS) > 0)
				.findFirst();
		TimeUnit unit = first.isPresent() ? first.get() : NANOSECONDS;

		double value = (double) nanos / NANOSECONDS.convert(1, unit);
		return String.format("%.4g %s", value, unit.name().toLowerCase());
	}

	private static long getTimeElapsed(long startTime) {
		return System.nanoTime() - startTime;
	}

}

