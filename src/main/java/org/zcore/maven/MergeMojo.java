package org.zcore.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Mojo for merging supplied text files
 *
 * @author Robert Heine <robert.heine@zcore.org>
 * @goal merge
 * @requiresProject
 */
public class MergeMojo extends AbstractMojo {

    /**
     * Configuration from file
     *
     * <pre>
     * &lt;mergers&gt;
     * &nbsp;&nbsp;&lt;merge&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;target&gt;${build.outputDirectory}/target.txt&lt;/target&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;sources&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;source&gt;src/main/config/${property}/application.txt&lt;/source&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;source&gt;src/main/config/extended/application.txt&lt;/source&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;source&gt;src/main/config/default/application.txt&lt;/source&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/sources&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;rewriteNewlines&gt;${newline.character}&lt;/rewriteNewlines&gt;
     * &nbsp;&nbsp;&lt;/merge&gt;
     * &lt;/mergers&gt;
     * </pre>
     *
     * @required
     * @parameter
     */
    private transient Merger[] mergers;

    /**
     * Opens an OutputStream, based on the supplied file
     * @param target {@linkplain File}
     * @return {@linkplain OutputStream}
     * @throws MojoExecutionException
     */
    protected OutputStream initOutput(final File file)
        throws MojoExecutionException {
        // stream to return
        final OutputStream stream;
        // plenty of things can go wrong...
        try {
            // directory?
            if (file.isDirectory()) {
            throw new MojoExecutionException("File "
                + file.getAbsolutePath() + " is directory!");
            }
            // already exists && can't remove it?
            if (file.exists() && !file.delete()) {
            throw new MojoExecutionException("Could not remove file: "
                + file.getAbsolutePath());
            }
            // get directory above file file
            final File fileDirectory = file.getParentFile();
            // does not exist && create it?
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
            throw new MojoExecutionException(
                "Could not create directory: "
                    + fileDirectory.getAbsolutePath());
            }
            // moar wtf: parent directory is no directory?
            if (!fileDirectory.isDirectory()) {
            throw new MojoExecutionException("Not a directory: "
                + fileDirectory.getAbsolutePath());
            }
            // file file is for any reason not creatable?
            if (!file.createNewFile()) {
            throw new MojoExecutionException("Could not create file: "
                + file.getAbsolutePath());
            }
            // finally create some file
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Could not find file: "
                + file.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write to file: "
                + file.getAbsolutePath(), e);
        }
        // return
        return stream;
    }

    /**
     * Opens an InputStream, based on the supplied file
     * @param target {@linkplain File}
     * @return {@linkplain InputStream}
     * @throws MojoExecutionException
     */
    protected InputStream initInput(final File file)
        throws MojoExecutionException {
        InputStream stream = null;
        try {
            if (file.isDirectory()) {
                throw new MojoExecutionException("File "
                    + file.getAbsolutePath()
                    + " is directory!");
            }
            if (!file.exists()) {
                throw new MojoExecutionException("File "
                    + file.getAbsolutePath()
                    + " does not exist!");
            }
            stream = new FileInputStream(file);
            //append to outfile here
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Could not find file: "
                + file.getAbsolutePath(), e);
        }
        return stream;
    }

    /**
     * Factory Execute
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        // iterate through all <merge />
        for (Merger merger : mergers) {
            // get target file name...
            final File target = merger.getTarget();
            // get list of source files
            final List<File> sources = Arrays.asList(merger.getSources());
            // ...and use a stream
            final OutputStream targetStream = initOutput(target);
            // iterate source files
            Iterator<File> itr = sources.iterator();
            while (itr.hasNext()) {
                File source = itr.next();
                final InputStream sourceStream = initInput(source);
                // append
                appendStream(sourceStream, targetStream, !itr.hasNext());
                // close source
                if (null != sourceStream) {
                    try {
                        sourceStream.close();
                    } catch (IOException e) {
                        throw new MojoExecutionException(
                            "Could not close file: "
                                + source.getAbsolutePath(), e);
                    }
                }
            }
            // close target
            if (null != targetStream) {
                try {
                    targetStream.close();
                } catch (IOException e) {
                    throw new MojoExecutionException("Could not close file: "
                        + target.getAbsolutePath(), e);
                }
            }
        }
    }

    /**
     * Appends inputstream to outputstream
     *
     * @param input  {@linkplain InputStream}
     * @param output {@linkplain OutputStream}
     * @throws MojoExecutionException
     */
    protected void appendStream(final InputStream input,
                                final OutputStream output, boolean theLast) throws MojoExecutionException {
        // prebuffer
        int character;
        try {
            // read & write
            while ((character = input.read()) != -1) {
                output.write(character);
            }
            // append newline
            if (!theLast) {
                // get line seperator, based on system
                final String newLine = System.getProperty("line.separator");
                output.write(newLine.getBytes());
            }
            // flush
            output.flush();
        } catch (IOException e) {
            throw new MojoExecutionException("Error in buffering/writing "
                    + e.getMessage(), e);
        }
    }
}
