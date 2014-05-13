package controllers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	private static final String ORIGINAL_FILE_DIR = "../../temp/annin-tofu/ori/";
	private static final String TMP_FILE_DIR = "../../temp/annin-tofu/tmp/";

	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result convertImage(String imageUrl) {

		System.out.println("imageUrl=" + imageUrl);

		try {
			String path = imageUrl.substring(0, imageUrl.indexOf("."));
			String[] array = path.split("_");
			String srcFile = array[0];
			int width = Integer.parseInt(array[1]);
			int height = Integer.parseInt(array[2]);

			String outFileName = imageUrl + ".jpg";

			File outImageFile = new File(TMP_FILE_DIR + outFileName);
			if (!outImageFile.exists()) {
				File baseImage = new File(ORIGINAL_FILE_DIR + srcFile + ".jpg");

				FileInputStream original = FileUtils.openInputStream(baseImage);
				ByteArrayOutputStream newImage = resize(original, width, height);
				FileUtils.writeByteArrayToFile(outImageFile, newImage.toByteArray());

			}
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
}
