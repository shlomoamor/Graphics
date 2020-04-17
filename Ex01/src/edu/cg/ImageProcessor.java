package edu.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.cg.menu.components.ScaleSelector;

public class ImageProcessor extends FunctioalForEachLoops {
	// MARK: fields
	public final Logger logger;
	public final BufferedImage workingImage;
	public final RGBWeights rgbWeights;
	public final int inWidth;
	public final int inHeight;
	public final int workingImageType;
	public final int outWidth;
	public final int outHeight;

	// MARK: constructors
	public ImageProcessor(Logger logger, BufferedImage workingImage, RGBWeights rgbWeights, int outWidth,
			int outHeight) {
		super(); // initializing for each loops...

		this.logger = logger;
		this.workingImage = workingImage;
		this.rgbWeights = rgbWeights;
		inWidth = workingImage.getWidth();
		inHeight = workingImage.getHeight();
		workingImageType = workingImage.getType();
		this.outWidth = outWidth;
		this.outHeight = outHeight;
		setForEachInputParameters();
	}

	public ImageProcessor(Logger logger, BufferedImage workingImage, RGBWeights rgbWeights) {
		this(logger, workingImage, rgbWeights, workingImage.getWidth(), workingImage.getHeight());
	}

	// Changes the picture's hue - example
	public BufferedImage changeHue() {
		logger.log("Prepareing for hue changing...");

		int r = rgbWeights.redWeight;
		int g = rgbWeights.greenWeight;
		int b = rgbWeights.blueWeight;
		int max = rgbWeights.maxWeight;

		BufferedImage ans = newEmptyInputSizedImage();

		forEach((y, x) -> {
			Color c = new Color(workingImage.getRGB(x, y));
			int red = r * c.getRed() / max;
			int green = g * c.getGreen() / max;
			int blue = b * c.getBlue() / max;
			Color color = new Color(red, green, blue);
			ans.setRGB(x, y, color.getRGB());
		});

		logger.log("Changing hue done!");

		return ans;
	}

	// Sets the ForEach parameters with the input dimensions
	public final void setForEachInputParameters() {
		setForEachParameters(inWidth, inHeight);
	}

	// Sets the ForEach parameters with the output dimensions
	public final void setForEachOutputParameters() {
		setForEachParameters(outWidth, outHeight);
	}

	// A helper method that creates an empty image with the specified input dimensions.
	public final BufferedImage newEmptyInputSizedImage() {
		return newEmptyImage(inWidth, inHeight);
	}

	// A helper method that creates an empty image with the specified output dimensions.
	public final BufferedImage newEmptyOutputSizedImage() {
		return newEmptyImage(outWidth, outHeight);
	}

	// A helper method that creates an empty image with the specified dimensions.
	public final BufferedImage newEmptyImage(int width, int height) {
		return new BufferedImage(width, height, workingImageType);
	}

	// A helper method that deep copies the current working image.
	public final BufferedImage duplicateWorkingImage() {
		BufferedImage output = newEmptyInputSizedImage();

		forEach((y, x) -> output.setRGB(x, y, workingImage.getRGB(x, y)));

		return output;
	}
	
	public BufferedImage greyscale() {
		// Updating the log
		this.logger.log("Prepareing for greyscale changing...");
		
		// Get the colour weightings set by the user
		final int r = this.rgbWeights.redWeight;
		final int g = this.rgbWeights.greenWeight;
		final int b = this.rgbWeights.blueWeight;
		
		// Check that the input is valid, if not throw an exception
		if((r < 1 && r <101) && (g < 1 && g <101) && (b < 1 && b <101) ) {
			throw new UnimplementedMethodException("greyscale");
		}
		
		// Creating a new empty image
		final BufferedImage ans = this.newEmptyInputSizedImage();
		// Iterating through each pixel
		forEach((y, x) -> {
			// Getting the pixels colour
			Color c = new Color(this.workingImage.getRGB(x, y));
			
			// Applying the formula
			final int red = r * c.getRed();
			final int green = g * c.getGreen();
			final int blue = b * c.getBlue();
			final int greyCol = (red + green + blue) / (r + g + b);
			// Setting the colour
			ans.setRGB(x, y, (new Color(greyCol, greyCol, greyCol)).getRGB());
			return;
		});

		this.logger.log("Changing greyscale done!");
		return ans;
	}

	public BufferedImage nearestNeighbor() {
		// Updating the log
		this.logger.log("\"Prepareing for resizing by applying nearest neighbor interpolation.");
        // Creating a new empty image
		final BufferedImage ans = this.newEmptyOutputSizedImage();
		// Initialising the forEach width and height
        this.pushForEachParameters();
        this.setForEachOutputParameters();
        
        // Iterating through each pixel
        this.forEach((y, x) -> {
        	// As seen in recitation, getting transforming ratio
        	final float ratioX = this.inWidth / (float)this.outWidth;
        	final float ratioY = this.inHeight / (float)this.outHeight;
        	
        	// Using the Math.Round to get the closest integer value
            final int posX1 = Math.round(x * ratioX);
            final int posY1 = Math.round(y * ratioY);
            
            // Choosing the min between posX1 and original size to ensure we don't exceed image pixel
            final int posX2 = Math.min(posX1, this.inWidth - 1);
            final int posY2 = Math.min(posY1, this.inHeight - 1);
            ans.setRGB(x, y, this.workingImage.getRGB(posX2, posY2));
            return;
        });
        // Make sure we restore all parameters
        this.popForEachParameters();
        this.logger.log("Resizing done!");
        return ans;
	}

}
