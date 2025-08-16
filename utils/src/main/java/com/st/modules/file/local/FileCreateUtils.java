package com.st.modules.file.local;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

import static com.st.modules.file.local.FileDeleteUitls.delete;

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
public class FileCreateUtils {



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
	public static File createFile(String fullPath, int type, int overwrite) throws IOException {
		File target = new File(fullPath);

		if (target.exists()) {
			if (overwrite == 0) {
				System.out.println("[提示] 路径已存在: " + fullPath);
				return target;
			} else {
				// 先删除文件或整个目录
				delete(target);
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


	// 1. 创建文件，不覆盖已存在
	public static File createFileIfNotExists(String filePath) throws IOException {
		return createFile(filePath, 0, 0);
	}

	// 2. 创建文件，已存在则覆盖
	public static File createFileOverwrite(String filePath) throws IOException {
		return createFile(filePath, 0, 1);
	}

	// 3. 创建目录，不覆盖已存在
	public static File createDirIfNotExists(String dirPath) throws IOException {
		return createFile(dirPath, 1, 0);
	}

	// 4. 创建目录，已存在则覆盖
	public static File createDirOverwrite(String dirPath) throws IOException {
		return createFile(dirPath, 1, 1);
	}

}
