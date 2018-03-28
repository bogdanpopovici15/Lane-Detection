package OpenCvUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageTransformations {
	public static void loadOpenCVDLL() {
                File f=new File("Resources\\opencv_java2413.dll");
		System.load(f.getAbsolutePath());
	}
        public static BufferedImage matToBufferedImage (Mat matrix) {		
		if (matrix.channels() == 1) {
			int cols = matrix.cols();
			int rows = matrix.rows();
			int elemSize = (int)matrix.elemSize();
			byte[] data = new byte[cols * rows * elemSize];
			int type;
			matrix.get(0, 0, data);
			switch (matrix.channels()) {
			case 1:
				type = BufferedImage.TYPE_BYTE_GRAY;
				break;
			case 3:
				type = BufferedImage.TYPE_3BYTE_BGR;
				// bgr to rgb
				byte b;
				for (int i = 0; i < data.length; i = i + 3) {
					b = data[i];
					data[i] = data[i + 2];
					data[i + 2] = b;
				}
				break;
			default:
				return null;
			}

			BufferedImage image2 = new BufferedImage(cols, rows, type);
			image2.getRaster().setDataElements(0, 0, cols, rows, data);
			return image2;
		}

		if (matrix.channels() == 3) {
			int width = matrix.width(), height = matrix.height(), channels = matrix.channels();
			byte[] sourcePixels = new byte[width * height * channels];
			matrix.get(0, 0, sourcePixels);
			// create new image and get reference to backing data
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			final byte[] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
			System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
			return image;
		}

		return null;
	}
        public static Mat GrayTransofrmation(Mat src){
            Mat dest = new Mat();
	    Imgproc.cvtColor(src, dest, Imgproc.COLOR_BGR2GRAY);
	    Imgproc.blur(dest, dest, new Size(4, 4));
            Imgproc.GaussianBlur(dest, dest, new Size(5, 5), 0);
            return dest;
        }
        public static Mat CannyTransformation(Mat src,int lowThreshold,int maxThreshold){
            Mat dest = new Mat();
            Imgproc.Canny(src, dest, lowThreshold, maxThreshold);
            return dest;
        }
        public static Mat DetectLines(Mat initialImage,Mat canny,int houghThreshold,int minLineLenght,int maxLineGap){
          Mat Initial=initialImage;
            for(int x = 0; x< canny.width();x++) {
              for(int y = 0; y<canny.height();y++) {
                  int black = -16777216;
                  if(y<(canny.height()/2)) {
	            canny.put(y, x,black );
	          }
              }
          }
          Mat lines = new Mat();
          Imgproc.HoughLinesP(canny, lines, 1, Math.PI / 180, houghThreshold, minLineLenght, maxLineGap);
          for (int i = 0; i < lines.cols(); i++) {
              double[] val = lines.get(0, i);
              Core.line(Initial, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 3);
	  }
          return Initial;
        }
}