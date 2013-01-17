package org.zcore.maven;

import java.io.File;
import java.util.Arrays;

/**
 * PoJo holding information for text file merging
 * @author Robert Heine <heine@zcore.org>
 *
 */
public class Merger {

    /**
     * The target file, where all other files are copied to
     * @parameter
     * @required
     */
    private File target;

    /**
     * Array of possible source files
     * @parameter
     * @required
     */
    private File[] sources;

    /**
     * Returns the target filename
     * @return {@linkplain File}
     */
    public File getTarget() {
        return target;
    }

    /**
     * Returns the array of source filenames
     * @return array of {@linkplain File}
     */
    public File[] getSources() {
        return sources;
    }

    /**
     * Overriding toString() here for debugging
     * @return {@linkplain String}
     */
    @Override
    public String toString() {
	// buffer to return
	final StringBuffer buffer = new StringBuffer();
	// append stuff
	buffer.append("Merger [toString()=" + super.toString() + "]");
	buffer.append("[target: " + getTarget().toString() + "]");
	buffer.append("[source: " + Arrays.asList(getSources().toString()) + "]");
	// return
	return buffer.toString();
    }
}
