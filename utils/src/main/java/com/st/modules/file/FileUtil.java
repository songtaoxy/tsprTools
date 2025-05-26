package com.st.modules.file;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.st.modules.json.jackson.JacksonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author: st
 * @date: 2023/2/6 11:39
 * @version: 1.0
 * @description:
 *
 *
 * <pre>
 * 关于new File(path):
 * String path = "x/y/z.txt" 文件; 或目录:  path ="x/y/z"
 * 1, new File(path) 构造方法同时支持文件和目录。
 * 2, 该方法只是内存中创建一个 File 对象，代表指定的路径;
 * 3, 不会判断: 物理磁盘上, 是否真的存在文件或目录
 * 4, 不会判断这个路径是文件、目录; 如果磁盘上存在, 也不会判断; 如果存储上不存在, 也不会判断
 *
 * 比如:
 *         String filePath =property+ "/tmp/x/yhello.txt";
 *         System.out.println(filePath);
 *         File file = new File(property);
 *         file.mkdirs();
 *  - 如果果”/tmp/x/yhello.txt"  磁盘上不存在这个文件或目录, 则:
 *  - File file = new File(property); 仅仅是在内存中构建了一个file对象
 *  - file.mkdirs(); 执行之后, 磁盘上并没有对应的目录.
 *
 *  - 如果想解决:
 *  File file = new File(property) 之后先判断物理磁盘是否存在: `file.exists()`
 *  然后再判断是文件还是目录: file.isFile(), file.isDirectory()
 * </pre>
 */
@Slf4j
public class FileUtil {

	public static void main(String[] args) {
		String filePath = "/Users/songtao/personaldriveMac/Projects/tsprTools/stbptools008/src/main/java/com/st/modules/file/test.txt";
		String filePath2 = "/Users/songtao/downloads/";
		//String dir3 = "/Users/songtao/downloads/a/b/c/d";
		String dir3 = "/Users/songtao/downloads/a/";
		File file = new File(filePath2);
		//File file = new File("/Users/songtao/personaldriveMac/");
		double dirSize = getFileSize(file);


		String s = readTxt(filePath);
		System.out.println(s);

		String s1 = readFile(new File(filePath));
		System.out.println(s1);

		List<File> files = listFilesForFolder(new File(filePath2));
		for (File f : files) {
			System.out.println(f.getAbsolutePath());
		}

		deleteDir(new File(dir3));
	}

	/**
	 * 判断文件是否存在
	 */
	public static boolean exists(String path) {
		return new File(path).exists();
	}

	/**
	 * 文件或目录递归创建工具
	 * <pre>
	 *
	 * 入参有三个: 1, 全路径, 2文件类型, 3覆盖类型,
	 * - 1, 全路径:文件或目录的全路径
	 * - 2, 文件类型: 0,指文件; 1, 指目录
	 * - 3, 覆盖类型: 如果不存在, 递归新建文件或目录; 如果已经存在, 0,不再创建, 提示已经存在;1,删除文件或删除目录,再递归新建文件或目录;
	 *
	 * 基于方法, 衍生出四个方法: 分别对应type
	 * - 文件类型：0-文件，1-目录
	 * - overwrite 覆盖类型：0-已存在则不创建（提示），1-已存在则先删再建
	 * - 1. 创建文件，不覆盖已存在 createFileIfNotExists 如果存在, 则直接使用
	 * - 2. 创建文件，已存在则覆盖 createFileOverwrite 如果存在, 则先删除, 再创建新的
	 * - 3. 创建目录，不覆盖已存在 createDirIfNotExists 如果存在, 则直接使用
	 * - 4. 创建目录，已存在则覆盖 createDirOverwrite 如果存在, 则先删除, 再创建新的
	 *
	 * </pre>
	 * @param fullPath 全路径（文件或目录）
	 * @param type 文件类型：0-文件，1-目录
	 * @param overwrite 覆盖类型：0-已存在则不创建（提示），1-已存在则先删再建
	 * @return 创建后的 File 对象
	 * @throws IOException
	 */
	public static File buildFile(String fullPath, int type, int overwrite) throws IOException {
		File target = new File(fullPath);

		if (target.exists()) {
			if (overwrite == 0) {
				System.out.println("[提示] 路径已存在: " + fullPath);
				return target;
			} else {
				// 先删除文件或整个目录
				deleteRecursively(target);
			}
		}

		if (type == 0) { // 文件
			File parent = target.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			if (!target.exists()) {
				target.createNewFile();
			}
		} else { // 目录
			target.mkdirs();
		}
		return target;
	}

	/**
	 * 递归删除文件或目录
	 */
	private static void deleteRecursively(File file) {
		if (!file.exists()) return;
		if (file.isFile()) {
			file.delete();
		} else {
			File[] sub = file.listFiles();
			if (sub != null) {
				for (File f : sub) {
					deleteRecursively(f);
				}
			}
			file.delete();
		}
	}




	// 1. 创建文件，不覆盖已存在
	public static File createFileIfNotExists(String filePath) throws IOException {
		return buildFile(filePath, 0, 0);
	}

	// 2. 创建文件，已存在则覆盖
	public static File createFileOverwrite(String filePath) throws IOException {
		return buildFile(filePath, 0, 1);
	}

	// 3. 创建目录，不覆盖已存在
	public static File createDirIfNotExists(String dirPath) throws IOException {
		return buildFile(dirPath, 1, 0);
	}

	// 4. 创建目录，已存在则覆盖
	public static File createDirOverwrite(String dirPath) throws IOException {
		return buildFile(dirPath, 1, 1);
	}


	/**
	 * 删除单个文件或整个目录（递归）
	 * <pre>
	 * 关键边界说明:
	 * - 路径为空或null：直接返回false并输出警告
	 * - File对象为null：直接返回false并输出警告
	 * - 文件/目录不存在：返回true（视为已“删除”）
	 * - 普通文件：直接删除
	 * - 目录：递归删除全部内容后再删自身
	 * - 其他类型（如符号链接等）：输出警告，返回false
	 * - 删除失败自动输出详细警告
	 * </pre>
	 *
	 * @param path 文件或目录路径
	 * @return true: 删除成功或本就不存在；false: 删除失败
	 */
	public static boolean delete(String path) {
		if (path == null || path.trim().isEmpty()) {
			System.out.println("[警告] 路径为空！");
			return false;
		}
		File target = new File(path);
		return delete(target);
	}

	/**
	 * <pre>
	 * 具体规范, 同{@code com.st.modules.file.FileUtil#delete(java.lang.String)}
	 * </pre>
	 * 删除单个文件或整个目录（递归）
	 * @param target File 对象
	 * @return true: 删除成功或本就不存在；false: 删除失败
	 */
	public static boolean delete(File target) {
		if (target == null) {
			System.out.println("[警告] File对象为null！");
			return false;
		}
		if (!target.exists()) {
			// 不存在视为已删除
			return true;
		}
		if (target.isFile()) {
			boolean ok = target.delete();
			if (!ok) System.out.println("[警告] 文件删除失败: " + target.getAbsolutePath());
			return ok;
		} else if (target.isDirectory()) {
			// 先递归删除子文件/目录
			File[] subs = target.listFiles();
			if (subs != null) {
				for (File sub : subs) {
					if (!delete(sub)) return false;
				}
			}
			boolean ok = target.delete();
			if (!ok) System.out.println("[警告] 目录删除失败: " + target.getAbsolutePath());
			return ok;
		} else {
			System.out.println("[警告] 未知类型: " + target.getAbsolutePath());
			return false;
		}
	}

	/**
	 * 获取指定目录下所有文件（递归）
	 */
	public static List<File> listAllFiles(String dirPath) {
		List<File> files = new ArrayList<>();
		File root = new File(dirPath);
		if (!root.exists()) return files;
		if (root.isFile()) {
			files.add(root);
		} else {
			File[] subFiles = root.listFiles();
			if (subFiles != null) {
				for (File file : subFiles) {
					files.addAll(listAllFiles(file.getPath()));
				}
			}
		}
		return files;
	}

	/**
	 * 获取指定目录下的所有一级子文件（非递归）
	 */
	public static List<File> listFiles(String dirPath) {
		List<File> files = new ArrayList<>();
		File dir = new File(dirPath);
		if (dir.exists() && dir.isDirectory()) {
			File[] subFiles = dir.listFiles();
			if (subFiles != null) {
				for (File file : subFiles) {
					if (file.isFile()) files.add(file);
				}
			}
		}
		return files;
	}

	/**
	 * 读取文件
	 *
	 * @param file
	 * @return 返回字符串
	 */
	/**
	 * <li>读取文本文件, 如txt, md....</li>
	 * @param file
	 * @return
	 */
	public static String readFile(File file) {
		String jsonStr;
		log.info("————开始读取" + file.getPath() + "文件————");
		try (FileReader fileReader = new FileReader(file); Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			int ch;
			StringBuilder sb = new StringBuilder();
			while ((ch = reader.read()) != -1) {
				sb.append((char) ch);
			}

			jsonStr = sb.toString();
			log.info("————读取" + file.getPath() + "文件结束!————");
			return jsonStr;
		} catch (Exception e) {
			log.error("————读取" + file.getPath() + "文件出现异常，读取失败!————", e);
			return null;
		}
	}

	/**
	 * 根据名称读取数据
	 *
	 * @param file
	 */
	public static Map<String, String> configMap = new LinkedHashMap<>();

	public void readProperties(String file) {
		//String path = Thread.currentThread().getContextClassLoader().getResource(file).getPath();
		//String path = RESOURCES_URL + file;
		//log.info("路径:" + path);
		try {
			Properties properties = new Properties();
			// 使用InPutStream流读取properties文件
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
			Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(reader);
			properties.load(bufferedReader);
			//获取key对应的value值
			properties.forEach((key, val) -> configMap.put(key.toString().trim(), val.toString().trim()));
		} catch (IOException e) {
		}
	}


	/**
	 * 返回某目录下所有文件的路径
	 *
	 * @param folder
	 * @return
	 */
	public static List<File> listFilesForFolder(File folder) {
		List<File> fileUrlList = new ArrayList<>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				fileUrlList.add(fileEntry);
			}
		}
		return fileUrlList;
	}


	/**
	 * 返回某目录下所有文件的路径
	 *
	 * @param folder
	 * @return
	 */
	public static List<String> listFilesNameForFolder(File folder) {
		List<String> fileUrlList = new ArrayList<>();

		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesNameForFolder(fileEntry);
			} else {
				fileUrlList.add(fileEntry.getName());
			}
		}
		return fileUrlList;
	}

	/**
	 * 返回某目录下所有文件的路径
	 *
	 * @param files
	 * @param folder
	 * @return
	 */
	public static void listFilesForFolder(List<File> files, File folder) {
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(files, fileEntry);
			} else {
				files.add(fileEntry);
			}
		}
	}


	/**
	 * <li>判断目录是否存在, 不存在, 则创建</li>
	 * <li>存在, 则不动, 提醒已经存在, 无需创建. </li>
	 *
	 * @param dir 目录(而非文件)
	 */
	public static void mkdir(String dir) {
		File folder = new File(dir);
		if (!folder.exists() && !folder.isDirectory()) {
			folder.mkdirs();
			log.info("目录已被创建:[" + dir + "]");
		} else {
			log.info("目录已存在:[" + dir + "]");
		}
	}


	/**
	 * 文件若存在, 则不做操作; <p></p>
	 * 文件若存在, 不会新建; 否则新建
	 *
	 * @param fileFullName
	 */
	/**
	 * <ul>创建文件
	 *     <li>文件已经存在, 不再创建</li>
	 *     <li>文件不存在, 创建</li>
	 * </ul>
	 * @param fileFullName
	 * @return
	 */
	@SneakyThrows
	public static File createFile(String fileFullName) {
		File file = new File(fileFullName);

		mkdir(file.getParent());

		if (!file.exists()) {
			file.createNewFile();
			log.info("文件已被创建:[" + fileFullName + "]");
		} else {
			log.info("文件已存在:[" + fileFullName + "]");
		}
		return file;
	}

	/**
	 * <ul>创建文件
	 *     <li>文件已经存在, 先删除, 再创建</li>
	 *     <li>文件不存在, 创建</li>
	 * </ul>
	 * @param fileFullName
	 */
	@SneakyThrows
	public static File updateFile(String fileFullName) {
		File file = new File(fileFullName);

		mkdir(file.getParent());

		if (file.exists()) {
			log.info("文件已存在, 正在删除旧文件.... ");
			file.delete();
		}
		file.createNewFile();
		log.info("文件已被创建:[" + fileFullName + "]");

		return file;
	}


	/**
	 * 删除目录
	 *
	 * @param dir
	 * @return
	 */
	/**
	 * <li>递归删除目录</li>
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
//递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	//建立一个读文件的方法

	/**
	 * <li>读取文本文件, 如txt, md....</li>
	 * @param filePath
	 * @return
	 */
	@SneakyThrows
	public static String readTxt(String filePath)  {

		StringBuffer sb = new StringBuffer();
		File file = new File(filePath);

		//创建一个读取文件的流对象
		FileReader fileReader = new FileReader(file);
		//bufferreader知识进行了一下封装，是读取的效率更高
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		//定义一个空字符串
		String str;
		//此处判断切记不能用if
		//bufferedReader.readLine()是读取文件的一行，如果有多行，会逐行读取。
		//当每一行不为空时则把内容打印到控制台中，也可以存到写入的流中，把内容写到文本里
		while ((str = bufferedReader.readLine()) != null) {
			sb.append(str).append(System.lineSeparator());
		}
		//最后不要忘记关流
		fileReader.close();

		return new String(sb);

	}

	/**
	 * <li>移动文件到指定位置</li>
	 *
	 * @param fileFullNameCurrent 必须是文件(不能是目录), 要移动的文件全路径
	 * @param fileFullNameTarget  必须是文件(不能是目录),移动到目标位置的文件全路径
	 * @return 是否移动成功， true：成功；否则失败
	 */
	public static Boolean moveFileToTarget(String fileFullNameCurrent, String fileFullNameTarget) {
		boolean ismove = false;

		File oldName = new File(fileFullNameCurrent);

		if (!oldName.exists()) {
			log.warn("{}", "要移动的文件{}不存在！", fileFullNameCurrent);
			return ismove;
		}

		if (oldName.isDirectory()) {
			log.warn("{}", "要移动的文件是目录，不移动！");
			return false;
		}

		File newName = new File(fileFullNameTarget);

		if (newName.isDirectory()) {
			log.warn("{}", "移动到目标位置的文件是目录，不能移动！");
			return false;
		}

		String pfile = newName.getParent();
		File pdir = new File(pfile);

		if (!pdir.exists()) {
			pdir.mkdirs();
			log.warn("{}", "要移动到目标位置文件的父目录不存在，创建：" + pfile);
		}

		ismove = oldName.renameTo(newName);
		return ismove;
	}
/*
	@SneakyThrows
	public static File transform(MultipartFile file, String path) {
		File destFile = new File(path);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		// 将上传的文件从内存放到硬盘
		file.transferTo(destFile);
		return destFile;
	}*/

	/**
	 * 将文本文件写入文件
	 *
	 * @param filepath
	 * @param content
	 */
	@SneakyThrows
	public static void bufferedWriter(String filepath, String content) {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath));
		bufferedWriter.write(content);
		bufferedWriter.flush();
		bufferedWriter.close();
	}


	/**
	 * <li>获取文件或目录(整个目录)的大小</li>
	 *
	 * @param file
	 * @return 文件大小:单位 bytes
	 */
	public static Long getFileSize(File file) {

		Long fileSizeRecursive = getFileSizeRecursive(file);
		fileSizeHandler(file, fileSizeRecursive);

		return fileSizeRecursive;


	}

	//@SneakyThrows
	public static Long getFileSizeRecursive(File file) {

		//判断文件是否存在
		if (file.exists()) {
			if (file.isFile())
				return file.length();
			final File[] children = file.listFiles();
			long total = 0;
			if (children != null)
				for (final File child : children)
					total += getFileSizeRecursive(child);
			return total;
		} else {
			Preconditions.checkArgument(false, "file[" + file.getAbsolutePath() + "]不存在，请检查路径是否正确！");
			//return 0L;
		}
		return null;
	}

	/**
	 * <li>单位转换: 人容易识别的单位 如: M,G, ...</li>
	 *
	 * @param file
	 * @param size
	 * @param <T>
	 * @return
	 */
	public static <T> T fileSizeHandler(File file, Long size) {
		T t = null;


		String file_type = "file";
		boolean directory = file.isDirectory();
		if (directory) {
			file_type = "dir";
		}

		JsonObject js = new JsonObject();
		js.addProperty("fie_path", file.getAbsolutePath());
		js.addProperty("file_type", file_type);


		if (size <= 1000) {
			js.addProperty("file_size", size + " bytes");
		} else {
			if (size / 1000 <= 1000) {
				js.addProperty("file_size", size / 1024 + " KB");

			} else {
				if (size / 1000 / 1000 <= 1000) {
					js.addProperty("file_size", size / 1024 / 1024 + " M");

				} else {
					if (size / 1000 / 1000 / 1000 <= 1000) {
						js.addProperty("file_size", size / 1024 / 1024 / 1024 + " G");
					} else {

						js.addProperty("file_size", size / 1024 / 1024 / 1024 / 1024 + " T");
					}
				}
			}
		}
		log.info(js.toString());
		System.out.println(JacksonUtils.toPrettyJson(js));
		return t;
	}


}
