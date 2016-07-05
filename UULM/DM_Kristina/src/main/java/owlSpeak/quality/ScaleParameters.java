package owlSpeak.quality;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Formatter;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

public class ScaleParameters {
	private double lower = -1.0;
	private double upper = 1.0;
	@SuppressWarnings("unused")
	private double y_lower;
	@SuppressWarnings("unused")
	private double y_upper;
	@SuppressWarnings("unused")
	private boolean y_scaling = false;
	private double[] feature_max;
	private double[] feature_min;
	@SuppressWarnings("unused")
	private double y_max = -Double.MAX_VALUE;
	@SuppressWarnings("unused")
	private double y_min = Double.MAX_VALUE;
	private int max_index;
	// private long num_nonzeros = 0;
	// private long new_num_nonzeros = 0;
	private boolean[] indexHasToBeScaled;
	private int numFeatures;
	
	public ScaleParameters() {
		
	}

	public ScaleParameters(File file) throws IOException {
		loadFromFile(file);
	}

	public ScaleParameters(String filename) throws IOException {
		loadFromFile(filename);
	}

	public ScaleParameters(URI fileURI) throws IOException {
		loadFromFile(fileURI);
	}

	void loadFromFile(BufferedReader fp_restore, String filename)
			throws IOException {
		int idx;

		setMax_index(0);
		if ((fp_restore.read()) == 'y') {
			fp_restore.readLine();
			fp_restore.readLine();
			fp_restore.readLine();
		}
		fp_restore.readLine();
		fp_restore.readLine();

		String restore_line = null;
		while ((restore_line = fp_restore.readLine()) != null) {
			StringTokenizer st2 = new StringTokenizer(restore_line);
			idx = Integer.parseInt(st2.nextToken());
			setMax_index(Math.max(getMax_index(), idx));
		}
		fp_restore = rewind(fp_restore, filename);

		setNumFeatures(0);

		try {
			feature_max = new double[(getMax_index() + 1)];
			feature_min = new double[(getMax_index() + 1)];
			setIndexHasToBeScaled(new boolean[(getMax_index() + 1)]);
		} catch (OutOfMemoryError e) {
			System.err.println("can't allocate enough memory");
			System.exit(1);
		}

		for (int i = 0; i <= getMax_index(); i++) {
			feature_max[i] = -Double.MAX_VALUE;
			feature_min[i] = Double.MAX_VALUE;
			getIndexHasToBeScaled()[i] = false;
		}

		// fp_restore rewinded in finding max_index
		double fmin, fmax;

		fp_restore.mark(2); // for reset
		if ((fp_restore.read()) == 'y') {
			fp_restore.readLine(); // pass the '\n' after 'y'
			StringTokenizer st = new StringTokenizer(fp_restore.readLine());
			y_lower = Double.parseDouble(st.nextToken());
			y_upper = Double.parseDouble(st.nextToken());
			st = new StringTokenizer(fp_restore.readLine());
			y_min = Double.parseDouble(st.nextToken());
			y_max = Double.parseDouble(st.nextToken());
			y_scaling = true;
		} else {
			fp_restore.reset();
		}

		if (fp_restore.read() == 'x') {
			fp_restore.readLine(); // pass the '\n' after 'x'
			StringTokenizer st = new StringTokenizer(fp_restore.readLine());
			lower = Double.parseDouble(st.nextToken());
			upper = Double.parseDouble(st.nextToken());
			restore_line = null;
			while ((restore_line = fp_restore.readLine()) != null) {
				StringTokenizer st2 = new StringTokenizer(restore_line);
				idx = Integer.parseInt(st2.nextToken());
				fmin = Double.parseDouble(st2.nextToken());
				fmax = Double.parseDouble(st2.nextToken());
				if (idx <= getMax_index()) {
					getIndexHasToBeScaled()[idx] = true;
					setNumFeatures(getNumFeatures() + 1);
					feature_min[idx] = fmin;
					feature_max[idx] = fmax;
				}
			}
		}
		fp_restore.close();
	}

	void loadFromFile(File file) throws IOException {
		loadFromFile(new BufferedReader(new FileReader(file)),
				file.getAbsolutePath());
	}

	void loadFromFile(String filename) throws IOException {
		loadFromFile(new BufferedReader(new FileReader(filename)), filename);
	}

	void loadFromFile(URI fileURI) throws IOException {
		loadFromFile(new BufferedReader(new FileReader(new File(fileURI))),
				fileURI.getPath());
	}

	private BufferedReader rewind(BufferedReader fp, String filename)
			throws IOException {
		fp.close();
		return new BufferedReader(new FileReader(filename));
	}
	
	/**
	 * copy and pasted code from svm_scale.java from libsvm
	 */

	public double[] scale(double[] _values) {

		double[] values = _values;//.clone();

		for (int index = 0; index < getMax_index(); index++) {
			if (getIndexHasToBeScaled()[index + 1]) {
				if (feature_max[index + 1] != feature_min[index + 1]) {
					if (values[index] == feature_min[index + 1]) {
						values[index] = lower;
					} else if (values[index] == feature_max[index + 1]) {
						values[index] = upper;
					} else {
						// String message = "param " + index +
						// " value before scaling: " + values[index];
						values[index] = lower
								+ (upper - lower)
								* (values[index] - feature_min[index + 1])
								/ (feature_max[index + 1] - feature_min[index + 1]);
						// logger.debug(message + " value after scaling: " +
						// values[index]);
					}
				}
			}
		}
		return values;
	}
	
	public void createFromFeatures(Vector<double[]> values) {
		max_index = values.firstElement().length;
		setIndexHasToBeScaled(new boolean[max_index+1]);
		
		try {
			feature_max = new double[(max_index + 1)];
			feature_min = new double[(max_index + 1)];
		} catch (OutOfMemoryError e) {
			System.err.println("can't allocate enough memory");
			System.exit(1);
		}

		for (int i = 0; i <= max_index; i++) {
			feature_max[i] = -Double.MAX_VALUE;
			feature_min[i] = Double.MAX_VALUE;
		}

		/* pass 2: find out min/max value */
		for (double[] vector : values) {

			for (int index = 0; index < vector.length; index++) {
							
				double value = vector[index];
				
				if (Double.isNaN(value))
					System.out.println("NaN");

				feature_max[index+1] = Math.max(feature_max[index+1], value);
				feature_min[index+1] = Math.min(feature_min[index+1], value);
				
				getIndexHasToBeScaled()[index+1] = true;
			}
		}
	}
	
	void writeToFile(String save_filename) throws IOException {
		/* pass 2.5: save/restore feature_min/feature_max */
		if (save_filename != null) {
			Formatter formatter = new Formatter(new StringBuilder());
			BufferedWriter fp_save = null;

			try {
				fp_save = new BufferedWriter(new FileWriter(save_filename));
			} catch (IOException e) {
				System.err.println("can't open file " + save_filename);
				System.exit(1);
			}

//			if (y_scaling) {
//				formatter.format("y\n");
//				formatter.format("%.16g %.16g\n", y_lower, y_upper);
//				formatter.format("%.16g %.16g\n", y_min, y_max);
//			}
			formatter.format("x\n");
			formatter.format(Locale.ENGLISH,"%.16g %.16g\n", lower, upper);
			
			for (int i = 1; i <= max_index; i++) {
				if (feature_min[i] != feature_max[i]) {
					formatter.format(Locale.ENGLISH,"%d %.16g %.16g\n", i, feature_min[i],
							feature_max[i]);
				}
			}
			fp_save.write(formatter.toString());
			formatter.close();
			fp_save.close();
		}	
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}

	public int getMax_index() {
		return max_index;
	}

	public void setMax_index(int max_index) {
		this.max_index = max_index;
	}

	public boolean[] getIndexHasToBeScaled() {
		return indexHasToBeScaled;
	}

	public void setIndexHasToBeScaled(boolean[] indexHasToBeScaled) {
		this.indexHasToBeScaled = indexHasToBeScaled;
	}
}
