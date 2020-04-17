/**
 * Exercise Number : 	1
 * Date: 				16/04/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 * 
 * */

package edu.cg;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.LinkedList;

public class SeamsCarver extends ImageProcessor {

	//MARK: An inner interface for functional programming.
	@FunctionalInterface
	interface ResizeOperation {
		BufferedImage resize();
	}

	private int numOfSeams;
	private ResizeOperation resizeOp;
	private int[][] greyPixelsMatrix;
	private long[][] dpMatrix;
	private int[][] indicesMatrix;
	private boolean[][] imageMask;
	private LinkedList<LinkedList<Integer>> foundSeams;
	private LinkedList<LinkedList<Integer>> seamsInOriginalPlaces;
	private boolean[][] maskAfterSeamCarving;

	public SeamsCarver(Logger logger, BufferedImage workingImage, int outWidth, RGBWeights rgbWeights, boolean[][] imageMask) {
		super(logger, workingImage, rgbWeights, outWidth, workingImage.getHeight());
		numOfSeams = Math.abs(outWidth - inWidth);
		this.imageMask = imageMask;
		if (inWidth < 2 | inHeight < 2)
			throw new RuntimeException("Can not apply seam carving: workingImage is too small.");
		if (numOfSeams > inWidth / 2)
			throw new RuntimeException("Can not apply seam carving: too many seams...");

		// Setting resizeOp with the appropriate method:
		if (outWidth > inWidth)
			resizeOp = this::increaseImageWidth;
		else if (outWidth < inWidth)
			resizeOp = this::reduceImageWidth;
		else
			resizeOp = this::duplicateWorkingImage;
		
		logger.log("Begins preliminary calculations...");
		// LinkedList of seams found
		this.foundSeams = new LinkedList<>();
		this.seamsInOriginalPlaces = new LinkedList<>();
		// Initialising matrix with the grey colour values
		this.GreyScaleImage();
		// Initialising a indices matrix
		createIndicesMatrix();
		setSeamsList();
	}
	
	/**This method sets the linkedList of Seams* */
	public void setSeamsList() {
		logger.log("Finding the " + this.numOfSeams + " minimal seams...");
		int widthOfMatrix = this.greyPixelsMatrix[0].length;
		// Looping through for numOfSeams amount of times
		for (int i = 0; i < this.numOfSeams; i++) {
			logger.log("Finding seam #" + (i + 1));
			// Initialsing the dynamic programming matrix
			this.dpMatrix = new long[this.greyPixelsMatrix.length][widthOfMatrix];
			// Ensure that the dpMatrix cells where the mask was applied are set to maxInt
			applyMask();
			// Calc the cost matrix for the image
			calculateCostMatrix();
			// Get a seam from the dpMatrix
			LinkedList<Integer> seam = getMinSeamFromMatrix(widthOfMatrix);

			// Store the found seam we found
			this.foundSeams.addFirst(seam);
			// Remove the seam from the indice matrix (as seen in Recitation)
			removeFoundSeamFromIndicesMatrix();
			widthOfMatrix--;
		}
		logger.log("Preliminary calculations were ended.");
	}
	
	
	public BufferedImage resize() {
		return resizeOp.resize();
	}

	/**This method reduces the image width.* */
	private BufferedImage reduceImageWidth() {
		logger.log("Preparing the image for width reduction...");
		// Setting new empty mask
		this.maskAfterSeamCarving = new boolean[this.workingImage.getHeight()][this.workingImage.getWidth() - this.numOfSeams];
		BufferedImage ans = newEmptyOutputSizedImage();
		setForEachOutputParameters();
		forEach((y, x) -> {
					// Copy over the pixel
					ans.setRGB(x, y, this.workingImage.getRGB(this.indicesMatrix[y][x], y));
					// Edit the mask
					this.maskAfterSeamCarving[y][x] = this.imageMask[y][this.indicesMatrix[y][x]];
				}
		);
		logger.log("Image width reduction done!");
		return ans;
	}
	
	/**This method increases the image width.* */
	private BufferedImage increaseImageWidth() {
		logger.log("Preparing the image for width increase");
		int originalImageIndex, sortedLineIndex;
		// Create a new image with the addition of the seam needed to add
		BufferedImage ans = newEmptyImage(this.workingImage.getWidth() + this.numOfSeams, this.workingImage.getHeight());
		this.maskAfterSeamCarving = new boolean[this.workingImage.getHeight()][this.workingImage.getWidth() + this.numOfSeams];
		for (int i = 0; i < ans.getHeight(); i++) {
			originalImageIndex = 0;
			sortedLineIndex = 0;
			LinkedList<Integer> lineSorted = getSortedLineOfRemoveSeams(i);
			boolean endOfLine = false;
			int currentElement = lineSorted.get(sortedLineIndex);
			for (int j = 0; j < ans.getWidth(); j++) {
				// Check if we are at the index of the element in the seam and not in the end of the line
				while (!endOfLine && originalImageIndex == currentElement) {
					sortedLineIndex++;
					// Set pixel
					ans.setRGB(j, i, this.workingImage.getRGB(originalImageIndex, i));
					// Copy element to the new mask
					this.maskAfterSeamCarving[i][j] = this.imageMask[i][originalImageIndex];
					// Move cols (From the duplication)
					j++;
					// Repeat
					ans.setRGB(j, i, this.workingImage.getRGB(originalImageIndex, i));
					this.maskAfterSeamCarving[i][j] = this.imageMask[i][originalImageIndex];
					// If we have not reached the end of the seam then get the next element (pixel index)
					if (sortedLineIndex < lineSorted.size()) {
						currentElement = lineSorted.get(sortedLineIndex);
					}
					else {
						endOfLine = true;
					}
				}
				// If we were not in the seam rememebr to copy pixel
				ans.setRGB(j, i, this.workingImage.getRGB(originalImageIndex, i));
				this.maskAfterSeamCarving[i][j] = this.imageMask[i][originalImageIndex];
				originalImageIndex++;
			}
		}
		logger.log("Image width increase done!");
		return ans;
	}

	/**This method shows image seams
	 * @param Int seamColorRGB* */
	public BufferedImage showSeams(int seamColorRGB) {
		logger.log("Preparing the image for seam marking...");
		BufferedImage ans = duplicateWorkingImage();
		// Iterate through each seam changing only the pixels in the seam
		for (LinkedList<Integer> seam : this.seamsInOriginalPlaces) {
			for (int i = 0; i < seam.size(); i++) {
				// Change the pixel to the inputted colour
				ans.setRGB(seam.get(i), i, seamColorRGB);
			}
		}
		logger.log("Seams has been marked!");
		return ans;
	}
	
	/**This method return the mask of the resize image after seam carving. We will return an image with the same size as the resized image
	 * @param Int seamColorRGB* */
	public boolean[][] getMaskAfterSeamCarving() {
		// If we have no seams therefore the mask hasn't changed return the old Mask
		if(this.maskAfterSeamCarving == null) {
			return this.imageMask;
		}else {
			return this.maskAfterSeamCarving;
		}
	}

	/**This method looks at the costMatrix and returns the minimal seam
	 * @param Int realWidth* */
	private LinkedList<Integer> getMinSeamFromMatrix(int realWidth) {
		LinkedList<Integer> seamInOriginalLocation = new LinkedList<>();
		LinkedList<Integer> currentSeam = new LinkedList<>();
		int minCostIndex = findMinCostInBottomRow(this.dpMatrix[this.dpMatrix.length - 1]);
		currentSeam.addFirst(minCostIndex);
		seamInOriginalLocation.addFirst(this.indicesMatrix[this.dpMatrix.length - 1][minCostIndex]);
		int j = minCostIndex;
		for (int i = this.dpMatrix.length - 1; i > 0; i--) {
			long pixelEnergy = calculatePixelEnergy(j, i, realWidth);
			if (j == 0 && j < this.dpMatrix[0].length - 1) {
				if (this.dpMatrix[i][j] == pixelEnergy + this.dpMatrix[i - 1][j] + calculateNewEdgesCost(getCorrectPixel(i, j + 1), 0)) {
					currentSeam.addFirst(j);
					seamInOriginalLocation.addFirst(this.indicesMatrix[i][j]);
				}
				else {
					currentSeam.addFirst(j + 1);
					seamInOriginalLocation.addFirst(this.indicesMatrix[i][j + 1]);
					j = j + 1;
				}
			}
			else if (j > 0 && j == this.dpMatrix[0].length - 1) {
				if (this.dpMatrix[i][j] == pixelEnergy + this.dpMatrix[i - 1][j] + calculateNewEdgesCost(getCorrectPixel(i, j - 1), 0)) {
					currentSeam.addFirst(j);
					seamInOriginalLocation.addFirst(this.indicesMatrix[i][j]);
				}
				else {
					currentSeam.addFirst(j - 1);
					seamInOriginalLocation.addFirst(this.indicesMatrix[i][j - 1]);
					j = j - 1;
				}
			}
			else if (j == 0 && j == this.dpMatrix[0].length - 1) {
				currentSeam.addFirst(j);
				seamInOriginalLocation.addFirst(this.indicesMatrix[i][j]);
			}
			else {
				if (this.dpMatrix[i][j] == pixelEnergy + this.dpMatrix[i - 1][j] + calculateNewEdgesCost(getCorrectPixel(i, j - 1), getCorrectPixel(i, j + 1))) {
					currentSeam.addFirst(j);
					seamInOriginalLocation.addFirst(this.indicesMatrix[i][j]);
				}
				else if (this.dpMatrix[i][j] ==
						pixelEnergy + this.dpMatrix[i - 1][j - 1] + calculateNewEdgesCost(getCorrectPixel(i, j - 1), getCorrectPixel(i, j + 1)) +
								calculateNewEdgesCost(getCorrectPixel(i, j - 1), getCorrectPixel(i - 1, j))) {
					currentSeam.addFirst(j - 1);
					seamInOriginalLocation.addFirst(this.indicesMatrix[i][j - 1]);
					j = j - 1;
				}
				else {
					currentSeam.addFirst(j + 1);
					seamInOriginalLocation.addFirst(this.indicesMatrix[i][j + 1]);
					j = j + 1;
				}
			}
		}
		this.seamsInOriginalPlaces.addFirst(seamInOriginalLocation);
		return currentSeam;
	}

	/**This method calculates the cost matrix */
	private void calculateCostMatrix() {
		// Initialize the first row of the matrix
		initializeFirstRowOfMatrix(this.dpMatrix, this.dpMatrix[0].length);
		int matrixHeight = this.dpMatrix.length;
		int matrixWidth = this.dpMatrix[0].length;
		for (int i = 1; i < matrixHeight; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				long pixelEnergy = calculatePixelEnergy(j, i, matrixWidth);
				if (j == 0 && j < matrixWidth - 1) {
					this.dpMatrix[i][j] += pixelEnergy + Math.min(
							this.dpMatrix[i - 1][j] + calculateNewEdgesCost(getCorrectPixel(i, j + 1), 0),
							this.dpMatrix[i - 1][j + 1] + calculateNewEdgesCost(getCorrectPixel(i, j + 1), getCorrectPixel(i - 1, j))  + calculateNewEdgesCost(getCorrectPixel(i, j + 1), 0)
					);
				}
				else if (j > 0 && j == matrixWidth - 1) {
					this.dpMatrix[i][j] += pixelEnergy + Math.min(
							this.dpMatrix[i - 1][j] + calculateNewEdgesCost(getCorrectPixel(i, j - 1), 0),
							this.dpMatrix[i - 1][j - 1] + calculateNewEdgesCost(getCorrectPixel(i, j - 1), getCorrectPixel(i - 1, j)) + calculateNewEdgesCost(getCorrectPixel(i, j - 1), 0)
					);
				}
				else if (j == 0 && j == matrixWidth - 1) {
					this.dpMatrix[i][j] += pixelEnergy + this.dpMatrix[i - 1][j];
				}
				else {
					// general case
					this.dpMatrix[i][j] += pixelEnergy + Math.min(
							this.dpMatrix[i - 1][j] + calculateNewEdgesCost(getCorrectPixel(i, j - 1), getCorrectPixel(i, j + 1)),
							Math.min(
									this.dpMatrix[i - 1][j - 1] + calculateNewEdgesCost(getCorrectPixel(i, j - 1), getCorrectPixel(i, j + 1)) + calculateNewEdgesCost(getCorrectPixel(i, j - 1), getCorrectPixel(i - 1, j)),
									this.dpMatrix[i - 1][j + 1] + calculateNewEdgesCost(getCorrectPixel(i, j + 1), getCorrectPixel(i - 1, j)) + calculateNewEdgesCost(getCorrectPixel(i, j - 1), getCorrectPixel(i, j + 1)))
					);
				}
			}
		}
	}

	/**This method Initialize matrix with the positions of the pixels in the original image. */
	private void createIndicesMatrix() {
		this.indicesMatrix = new int[this.greyPixelsMatrix.length][this.greyPixelsMatrix[0].length];
		for (int i = 0; i < this.indicesMatrix.length; i++) {
			for (int j = 0; j < this.indicesMatrix[0].length; j++) {
				// Set to col number as seen in recitation
				this.indicesMatrix[i][j] = j;
			}
		}
	}

	// Initialise matrix with the values of the grey color of the original image
	private void GreyScaleImage() {
        final BufferedImage grey = this.greyscale();
        this.greyPixelsMatrix = new int[this.inHeight][this.inWidth];
        this.forEach((y, x) -> this.greyPixelsMatrix[y][x] = new Color(grey.getRGB(x, y)).getRed());
    }

	/**This method return sorted values of all x that was removed from the original image in line number index 
	 * @param int index*/
	private LinkedList<Integer> getSortedLineOfRemoveSeams(int index) {
		LinkedList<Integer> ans = new LinkedList<>();
		for (LinkedList<Integer> seam : this.seamsInOriginalPlaces) {
			ans.addFirst(seam.get(index));
		}
		Comparator<Integer> order = Integer::compare;
		ans.sort(order);
		return ans;
	}

	
	/**This method returns the minimum cost of the last row of the cost matrix. 
	 * @param long[] currentRow*/
	private int findMinCostInBottomRow(long[] currentRow) {
		long minValue = Long.MAX_VALUE;
		int minIndex = 0;
		for (int i = 0; i < currentRow.length; i++) {
			if (currentRow[i] < minValue) {
				minIndex = i;
				minValue = currentRow[i];
			}
		}
		return minIndex;
	}
 
	/**This method remove the last founded min seam from the indicesMatrix.*/
	private void removeFoundSeamFromIndicesMatrix() {
		LinkedList<Integer> seam = this.foundSeams.getFirst();
		for (int i = 0; i < seam.size(); i++) {
			for (int j = seam.get(i); j < this.dpMatrix[0].length - 1; j++) {
				// Shifting by overwriting
				this.indicesMatrix[i][j] = this.indicesMatrix[i][j + 1];
			}
		}
	}

	private int getCorrectPixel(int i, int j){
		return this.greyPixelsMatrix[i][this.indicesMatrix[i][j]];
	}

	/**This method initialises the first row of the cost matrix (with the pixel energy value).
	 * @param long[][] matrix
	 * @param int realWidth* */
	private void initializeFirstRowOfMatrix(long[][] matrix, int realWidth) {
		for (int i = 0; i < matrix[0].length; i++) {
			matrix[0][i] = calculatePixelEnergy(i, 0, realWidth);
		}
	}

	/**This method sets the masked cells to Integer.MAX_VALUE. */
	private void applyMask(){
		for (int i = 1; i < this.dpMatrix.length; i++) {
			for (int j = 0; j < this.dpMatrix[0].length; j++) {
				if(imageMask[i][indicesMatrix[i][j]]){ this.dpMatrix[i][j] = Integer.MIN_VALUE; }
			}
		}
	}
 
	/**This method returns the cost of new edges. */
	private long calculateNewEdgesCost(int greyColor1, int greyColor2) {
		return Math.abs(greyColor1 - greyColor2);
	}

	/**This method returns the pixel energy of the pixel (x, y)
	 * @param int x
	 * @param int y
	 * @param int realWidth */
	private long calculatePixelEnergy(int x, int y, int realWidth) {
		return x < realWidth - 1 ?
				Math.abs(this.greyPixelsMatrix[y][this.indicesMatrix[y][x]] - this.greyPixelsMatrix[y][this.indicesMatrix[y][x + 1]]) :
				Math.abs(this.greyPixelsMatrix[y][this.indicesMatrix[y][x]] - this.greyPixelsMatrix[y][this.indicesMatrix[y][x - 1]]);
	}
}