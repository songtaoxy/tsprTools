package com.mask;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: st
 * @date: 2022/7/6 18:48
 * @version: 1.0
 * @description:
 */
public class CreateMask {

	private static final int WHITE = -1;
	private static final int BLACK = -16777216;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		String filePath = "/Users/songtao/downloads/1111.bmp";
		// 生成对应的文件
		File file = new File(filePath);

		// 判断文件是否存在
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			ArrayList<Point> points = new ArrayList<Point>();
			points.add(new Point(334, 362));
			points.add(new Point(906, 300));
			points.add(new Point(934, 309));
		/*	points.add(new Point(875, 1075));
			points.add(new Point(696, 1075));
			points.add(new Point(686, 921));
			points.add(new Point(582, 877));
			points.add(new Point(600, 786));
			points.add(new Point(481, 766));
			points.add(new Point(475, 594));
			points.add(new Point(471, 585));
			points.add(new Point(467, 572));
			points.add(new Point(535, 577));*/
			points.add(new Point(555, 569));
			points.add(new Point(566, 548));
			points.add(new Point(551, 528));
			points.add(new Point(358, 526));
			points.add(new Point(354, 382));

			// 生成图像
			create(1920, 1080, filePath, "bmp", points);

			System.out.println("ours spend: " + (System.currentTimeMillis() - start) + "ms");
		} catch (IOException e) {
			System.out.println("File Create failed: " + e.getMessage());
		}
	}


	/**
	 * 功能: 判断像素点是否在一个图像的内部
	 *
	 * @param points: 像素点集
	 * @param x: 待测试点的横坐标
	 * @param y: 待测试点的纵坐标
	 * @return
	 */
	private static boolean judgePointInGraphical(List<Point> points, int x, int y) {
		// 得到横纵坐标的最大和最小值
		int xMax = -1, yMax = -1, xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE;
		// 横纵坐标数组
		int size = points.size();
		double[] xArr = new double[size];
		double[] yArr = new double[size];

		// 遍历点集
		for (int i = 0; i < size; ++i) {
			int x_ = points.get(i).getxAxis();
			int y_ = points.get(i).getyAxis();

			xArr[i] = x_;
			yArr[i] = y_;

			if (x_ > xMax) xMax = x_;
			if (y_ > yMax) yMax = y_;
			if (x_ < xMin) xMin = x_;
			if (y_ < yMin) yMin = y_;
		}

		// 首先判断该元素是否在外围的四边形之外
		if (x < xMin || x > xMax || y < yMin || y > yMax) {
			return false;
		}

		// pnploy算法
		boolean in = false;
		for (int i = 0, j = size - 1; i < size; j = i++) {
			if (((yArr[i] > y) != (yArr[j] > y)) && (x < (xArr[j] - xArr[i]) * (y - yArr[i]) / (yArr[j] - yArr[i]) + xArr[i]))
				in = !in;
		}

		return in;
	}



	/**
	 * 功能: 绘制轮廓
	 *
	 * @param image
	 * @param type
	 * @param file
	 * @param points
	 * @return
	 * @throws IOException
	 */
	private static void drawOutline(BufferedImage image, String type, File file, java.util.List<Point> points) throws IOException {
		Graphics2D g = image.createGraphics();

		// 设置画线的颜色和粗细
		g.setColor(new Color(255, 255, 255));
		g.setStroke(new BasicStroke(2));

		int size = points.size();
		for (int i = 0; i < size; ++i) {
			Point begin = points.get(i);
			Point end = points.get((i + 1) % size);
			g.drawLine(begin.getxAxis(), begin.getyAxis(), end.getxAxis(), end.getyAxis());
		}
		g.dispose();
	}

	/**
	 * 功能: 根据区域像素点的值生成掩膜
	 *
	 * @param width: 图像的宽度
	 * @param height: 图像的高度
	 * @param filePath: 文件要保存的路径
	 * @param type: 文件的格式
	 * @param points: 像素点集
	 * @throws IOException
	 */
	public static void create(int width, int height, String filePath, String type, java.util.List<Point> points) throws IOException {
		// 预处理
		File file = new File(filePath);
		if (file.exists()) file.delete();
		file.createNewFile();

		// 生成绘制原始轮廓的图像
		BufferedImage rawImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		drawOutline(rawImage, type, file, points);

		// 对原式轮廓进行填充
		FloodFillAlgorithm fillAlgorithm = new FloodFillAlgorithm(rawImage);

		// 找到种子节点
		// 找到第一个节点周围的矩形范围内找到满足条件的点
		Point pivot = points.get(0);
		int centerX = pivot.getxAxis(), centerY = pivot.getyAxis();
		Point seed = null;

		for (int j = centerY - 5; j <= centerY + 5; ++j) {
			for (int i = centerX - 5; i <= centerX + 5; ++i) {
				if (i != centerX && j != centerY && judgePointInGraphical(points, i, j)
						&& rawImage.getRGB(i, j) == BLACK) {
					seed = new Point(i, j);
					break;
				}
			}
			// 判断seed是否为空
			if (seed != null) break;
		}

		fillAlgorithm.seedFillScanLineWithStack(seed.getxAxis(), seed.getyAxis(), WHITE, BLACK);
		fillAlgorithm.updateResult();

		// 生成新的图
		ImageIO.write(rawImage, type, file);
	}
}
