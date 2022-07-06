package com.mask;

/**
 * @author: st
 * @date: 2022/7/6 18:49
 * @version: 1.0
 * @description:
 */

import java.awt.image.BufferedImage;

/**
 * 泛洪算法
 */
public class FloodFillAlgorithm {

		// 相关属性
		private BufferedImage inputImage;
		private int[] matrix;
		private int width;
		private int height;

		private int maxStackSize = 500;
		private int[] xstack = new int[maxStackSize];
		private int[] ystack = new int[maxStackSize];
		private int stackSize;

		// 初始化函数
		public FloodFillAlgorithm(BufferedImage rawImage) {
			this.inputImage = rawImage;
			width = rawImage.getWidth();      // 得到图像的宽度
			height = rawImage.getHeight();    // 得到图像的高度
			matrix = new int[width * height]; // 得到图像像素矩阵
			getRGB(rawImage, 0, 0, width, height, matrix);
		}

		public int getColor(int x, int y) {
			int index = y * width + x;
			return matrix[index];
		}

		public void setColor(int x, int y, int newColor) {
			int index = y * width + x;
			matrix[index] = newColor;
		}

		public void updateResult() {
			setRGB(inputImage, 0, 0, width, height, matrix);
		}

		//算法核心部分
		public void seedFillScanLineWithStack(int x, int y, int newColor, int oldColor) {
			if(oldColor == newColor) {
				return;
			}

			emptyStack();

			int y1;
			boolean spanLeft, spanRight;
			push(x, y);

			while(true) {
				// 取出栈顶的元素
				x = popx();
				if(x == -1) return;
				y = popy();

				y1 = y;
				// 向上扫描
				while (y1 >= 0 && getColor(x, y1) == oldColor) y1--;
				y1++;

				spanLeft = spanRight = false;

				// 向下扫描
				while(y1 < height && getColor(x, y1) == oldColor) {
					// 画垂直线
					setColor(x, y1, newColor);

					// 将扫描线左边的扫描线满足条件的点压入堆栈
					if (!spanLeft && x > 0 && getColor(x - 1, y1) == oldColor) {
						push(x - 1, y1);
						spanLeft = true;
					} else if (spanLeft && x > 0 && getColor(x - 1, y1) != oldColor) {
						spanLeft = false;
					}

					if(!spanRight && x < width - 1 && getColor(x + 1, y1) == oldColor) {
						push(x + 1, y1);
						spanRight = true;
					} else if(spanRight && x < width - 1 && getColor(x + 1, y1) != oldColor) {
						spanRight = false;
					}

					y1++;
				}
			}

		}

		// 清空堆栈
		private void emptyStack() {
			while(popx() != - 1) {
				popy();
			}
			stackSize = 0;
		}

		// 将像素点的坐标压入堆栈
		public void push(int x, int y) {
			stackSize++;
			// 栈扩容为原来的两倍
			if (stackSize == maxStackSize) {
				int[] newXStack = new int[maxStackSize * 2];
				int[] newYStack = new int[maxStackSize * 2];
				System.arraycopy(xstack, 0, newXStack, 0, maxStackSize);
				System.arraycopy(ystack, 0, newYStack, 0, maxStackSize);
				xstack = newXStack;
				ystack = newYStack;
				maxStackSize *= 2;
			}
			xstack[stackSize - 1] = x;
			ystack[stackSize - 1] = y;
		}

		// 取出像素点的横坐标
		public int popx() {
			if (stackSize == 0)
				return -1;
			else
				return xstack[stackSize - 1];
		}

		// 取出像素点的纵坐标
		public int popy() {
			int value = ystack[stackSize - 1];
			stackSize--;
			return value;
		}

		//去掉了BufferedImage.getRGB()方法中的惩罚措施
		public int[] getRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
			int type = image.getType();
			if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
				return (int [])image.getRaster().getDataElements(x, y, width, height, pixels);
			return image.getRGB(x, y, width, height, pixels, 0, width);
		}

		//去掉了BufferedImage.setRGB()方法中的惩罚措施
		public void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
			int type = image.getType();
			if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
				image.getRaster().setDataElements( x, y, width, height, pixels );
			else
				image.setRGB(x, y, width, height, pixels, 0, width);
		}

}
