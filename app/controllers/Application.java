package controllers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	private static final Properties APP_PROP = new Properties();
	private static String ORIGINAL_FILE_DIR;
	private static String TMP_FILE_DIR;

	static {
		FileInputStream in;
		try {
			in = FileUtils.openInputStream(new File("conf/annin-conf.xml"));
			APP_PROP.loadFromXML(in);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		String workDir = APP_PROP.getProperty("work.dir");
		makeDirsIfNothing(workDir);

		System.out.println("work.dir=" + workDir);
		ORIGINAL_FILE_DIR = workDir + "/ori/";
		TMP_FILE_DIR = workDir + "/tmp/";

		makeDirsIfNothing(ORIGINAL_FILE_DIR);
		makeDirsIfNothing(TMP_FILE_DIR);

		createSampleImage();
	}

	private static void makeDirsIfNothing(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result convertImage(String imageUrl) {

		System.out.println("imageUrl=" + imageUrl);

		try {
			String path = imageUrl.substring(0, imageUrl.indexOf("."));
			String[] array = path.split("_");
			System.out.println("array.length=" + array.length);
			File outImageFile;
			if (array.length == 1) {
				outImageFile = new File(ORIGINAL_FILE_DIR + imageUrl);
			} else {
				String srcFile = array[0];
				int width = Integer.parseInt(array[1]);
				int height = Integer.parseInt(array[2]);

				String outFileName = imageUrl;

				outImageFile = new File(TMP_FILE_DIR + outFileName);
				if (!outImageFile.exists()) {
					File baseImage = new File(ORIGINAL_FILE_DIR + srcFile + ".jpg");

					FileInputStream original = FileUtils.openInputStream(baseImage);
					ByteArrayOutputStream newImage = resize(original, width, height);
					FileUtils.writeByteArrayToFile(outImageFile, newImage.toByteArray());
				}
			}

			return ok(outImageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ok(index.render("Your new application is ready."));
	}

	private static ByteArrayOutputStream resize(InputStream originalImage, int width, int height) throws IOException {

		BufferedImage oriImage = ImageIO.read(originalImage);

		BufferedImage newImage = new BufferedImage(width, height, oriImage.getType());
		Graphics2D g2d = newImage.createGraphics();
		g2d.drawImage(oriImage, 0, 0, width, height, null);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(newImage, "jpeg", outputStream);
		outputStream.flush();

		return outputStream;

	}

	private static void createSampleImage() {
		int width = 100;
		int height = 100;

		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = newImage.createGraphics();
		g2.drawRect(0, 0, width, height);
		g2.drawChars("sample".toCharArray(), 0, 6, 20, 20);
		g2.drawChars("100*100".toCharArray(), 0, 7, 20, 40);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			ImageIO.write(newImage, "jpeg", outputStream);
			outputStream.flush();

			File outImageFile = new File(ORIGINAL_FILE_DIR + "sample.jpg");
			FileUtils.writeByteArrayToFile(outImageFile, outputStream.toByteArray());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}
}
