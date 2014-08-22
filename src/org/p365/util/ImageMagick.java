package org.p365.util;

import java.io.IOException;
import java.util.ArrayList;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

/**
 * @author zqs
 * @version 
 */
public class ImageMagick {

	/** * ImageMagick��·�� */
	public static String imageMagickPath = null;
	static {
		/** ��ȡImageMagick��·�� */
		// Properties prop = new PropertiesFile().getPropertiesFile();
		// linux�²�Ҫ���ô�ֵ����Ȼ�ᱨ��
		// imageMagickPath = prop.getProperty("imageMagickPath");
	}

	/**
	 * * ��������ü�ͼƬ
	 * 
	 * @param srcPath
	 *            Ҫ�ü�ͼƬ��·��
	 * @param newPath
	 *            �ü�ͼƬ���·��
	 * @param x
	 *            ��ʼ������
	 * @param y
	 *            ��ʼ������
	 * @param x1
	 *            ����������
	 * @param y1
	 *            ����������
	 */
	public static void cutImage(String srcPath, String newPath, int x, int y,
			int x1, int y1) throws Exception {
		int width = x1 - x;
		int height = y1 - y;
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		/**
		 * width���ü��Ŀ�� height���ü��ĸ߶� x���ü��ĺ����� y���ü��Ĵ�����
		 */
		op.crop(width, height, x, y);
		op.addImage(newPath);
		ConvertCmd convert = new ConvertCmd();
		// linux�²�Ҫ���ô�ֵ����Ȼ�ᱨ��
		// convert.setSearchPath(imageMagickPath);
		convert.run(op);
	}

	/**
	 * ���ݳߴ�����ͼƬ
	 * 
	 * @param width
	 *            ���ź��ͼƬ���
	 * @param height
	 *            ���ź��ͼƬ�߶�
	 * @param srcPath
	 *            ԴͼƬ·��
	 * @param newPath
	 *            ���ź�ͼƬ��·��
	 * @param type
	 *            1Ϊ��������2Ϊ��С�����磨������1024x1024,��С��50%x50%��
	 */
	public static String cutImage(int width, int height, String srcPath,
			String newPath, int type, String quality) throws Exception {
		IMOperation op = new IMOperation();
		ConvertCmd cmd = new ConvertCmd(true);
		op.addImage();
		String raw = "";
		if (type == 1) {
			// ������
			raw = width + "x" + height + "^";
		} else {
			// �����ذٷֱ�
			raw = width + "%x" + height + "%";
		}
		op.addRawArgs("-sample", raw);
		if ((quality != null && quality.equals(""))) {
			op.addRawArgs("-quality", quality);
		}
		op.addImage();

		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("win") != -1) {
			// linux�²�Ҫ���ô�ֵ����Ȼ�ᱨ��
			cmd.setSearchPath("C://Program Files//GraphicsMagick-1.3.14-Q16");
		}

		try {
			cmd.run(op, srcPath, newPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newPath;
	}

	/**
	 * ��ͼƬ��ˮӡ
	 * 
	 * @param srcPath
	 *            ԴͼƬ·��
	 */
	public static void addImgText(String srcPath) throws Exception {
		IMOperation op = new IMOperation();
		op.font("����").gravity("southeast").pointsize(18).fill("#BCBFC8")
				.draw("text 100,100 co188.com");
		op.addImage();
		op.addImage();

		String osName = System.getProperty("os.name").toLowerCase();
		ConvertCmd cmd = new ConvertCmd(true);
		if (osName.indexOf("win") != -1) {
			// linux�²�Ҫ���ô�ֵ����Ȼ�ᱨ��
			cmd.setSearchPath("C://Program Files//GraphicsMagick-1.3.14-Q16");
		}

		try {
			cmd.run(op, srcPath, srcPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// cutImage("D:\\apple870.jpg", "D:\\apple870eee.jpg",98, 48, 370, 320);
		Long start = System.currentTimeMillis();
		// cutImage(100,100,
		// "e:\\37AF7D10F2D8448A9A5.jpg","e:\\37AF7D10F2D8448A9A5_bak2.jpg",2,"100");
		addImgText("e:\\37AF7D10F2D8448A9A5_bak2.jpg");
		Long end = System.currentTimeMillis();
		System.out.println("time:" + (end - start) / 3600);
	}
}
