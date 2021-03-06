//----------------------------------------------------------------------------//
//                                                                            //
//                           O c r C o m p a n i o n                          //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright © Hervé Bitteur and others 2000-2013. All rights reserved.      //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package com.audiveris.installer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

/**
 * Class {@code OcrCompanion} handles the installation of Tesseract
 * library and the support for a selected set of languages.
 *
 * @author Hervé Bitteur
 */
public class OcrCompanion
        extends AbstractCompanion
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = LoggerFactory.getLogger(
            OcrCompanion.class);

    /** Companion description. */
    private static final String DESC = "<html>You can select which precise languages"
                                       + "<br/>the <b>OCR</b> should support."
                                       + "<br/>For this, please use the language selector below.</html>";

    /** Environment descriptor. */
    private static final Descriptor descriptor = DescriptorFactory.getDescriptor();

    /** Internet address to retrieve Tesseract trained data. */
    private static final String TESS_RADIX = "http://tesseract-ocr.googlecode.com/files";

    /** Precise Tesseract version. */
    private static final String TESS_VERSION = "tesseract-ocr-3.02";

    /**
     * Collection of all Tesseract supported languages.
     * Update this list according to Tesseract web site on
     * http://code.google.com/p/tesseract-ocr/downloads/list
     */
    public static final String[] ALL_LANGUAGES = new String[]{
        "afr", "ara", "aze", "bel",
        "ben", "bul", "cat", "ces",
        "chi_sim", "chi_tra", "chr",
        "dan", "deu", "ell", "eng",
        "enm", "epo", "epo_alt",
        "equ", "est", "eus", "fin",
        "fra", "frk", "frm", "glg",
        "grc", "heb", "hin", "hrv",
        "hun", "ind", "isl", "ita",
        "ita_old", "jpn", "kan",
        "kor", "lav", "lit", "mal",
        "mkd", "mlt", "msa", "nld",
        "nor", "pol", "por", "ron",
        "rus", "slk", "slv", "spa",
        "spa_old", "sqi", "srp",
        "swa", "swe", "tam", "tel",
        "tgl", "tha", "tur", "ukr",
        "vie"
    };

    /** Collection of pre-desired languages. */
    public static final String[] PREDESIRED_LANGUAGES = new String[]{
        "deu", "eng", "fra",
        "ita"
    };

    /** Name of local temporary folder for OCR languages. */
    public static final String LOCAL_OCR_FOLDER = "local-ocr-folder";

    /** Name of local temporary folder for binary files. */
    public static final String LOCAL_LIB_FOLDER = "local-lib-folder";

    //~ Instance fields --------------------------------------------------------
    /** Handling of tessdata directory. */
    private final Tessdata tessdata = new Tessdata();

    /** The languages to be added (if not present). */
    private final Set<String> desired = buildDesiredLanguages();

    /** The languages to be removed (if present). */
    private final Set<String> nonDesired = new TreeSet<String>();

    /** User selector, if any. */
    private LangSelector selector;

    //~ Constructors -----------------------------------------------------------
    //--------------//
    // OcrCompanion //
    //--------------//
    /**
     * Creates a new OcrCompanion object for OCR library and a list of
     * supported languages.
     */
    public OcrCompanion ()
    {
        super("OCR", DESC);

        if (Installer.hasUI()) {
            view = new BasicCompanionView(this, 60);
            selector = new LangSelector(this);
        }
    }

    //~ Methods ----------------------------------------------------------------
    //----------------//
    // checkInstalled //
    //----------------//
    @Override
    public boolean checkInstalled ()
    {
        try {
            boolean installed = true;

            // Check for Tesseract library
            if (!descriptor.isTesseractInstalled()) {
                installed = false;
            } else {
                if (selector != null) {
                    selector.update(null);
                }

                // We check if each of the desired language actually exists
                for (String language : desired) {
                    if (!isLangInstalled(language)) {
                        installed = false;

                        break;
                    }
                }

                if (installed) {
                    // We check if each of the non-desired language still exists
                    for (String language : nonDesired) {
                        if (isLangInstalled(language)) {
                            installed = false;

                            break;
                        }
                    }
                }
            }

            status = installed ? Status.INSTALLED : Status.NOT_INSTALLED;
        } catch (Throwable ex) {
            logger.warn("Tesseract could not be checked", ex);
            status = Status.NOT_INSTALLED;
        }

        return status == Status.INSTALLED;
    }

    //------------//
    // getDesired //
    //------------//
    /**
     * Report the set of desired languages.
     *
     * @return the current set of desired languages
     */
    public Set<String> getDesired ()
    {
        return desired;
    }

    //------------------//
    // getInstallWeight //
    //------------------//
    @Override
    public int getInstallWeight ()
    {
        return isNeeded() ? 1 : 0;
    }

    //---------------//
    // getNonDesired //
    //---------------//
    /**
     * Report the set of non-desired languages.
     *
     * @return the current set of non-desired languages
     */
    public Set<String> getNonDesired ()
    {
        return nonDesired;
    }

    //-------------//
    // getSelector //
    //-------------//
    public LangSelector getSelector ()
    {
        return selector;
    }

    //-----------------//
    // isLangInstalled //
    //-----------------//
    public boolean isLangInstalled (String language)
    {
        try {
            // We check if the main language file actually exists
            final File lanFile = new File(
                    tessdata.get(),
                    language + ".traineddata");

            return lanFile.exists();
        } catch (Exception ex) {
            logger.warn("No tessdata found", ex);

            return false;
        }
    }

    //-----------//
    // doInstall //
    //-----------//
    @Override
    protected void doInstall ()
            throws Exception
    {
        // First, install library if so needed
        if (!descriptor.isTesseractInstalled()) {
            descriptor.installTesseract();
        }

        // Then, we process languages in alphabetical order
        // To keep in sync with selector display
        final Set<String> relevant = new TreeSet<String>();
        relevant.addAll(desired);
        relevant.addAll(nonDesired);

        for (String lang : relevant) {
            if (desired.contains(lang) && !isLangInstalled(lang)) {
                if (selector != null) {
                    selector.update(lang);
                }

                installLanguage(lang);
            } else if (nonDesired.contains(lang)) {
                if (selector != null) {
                    selector.update(lang);
                }

                uninstallLanguage(lang);
                nonDesired.remove(lang);
            }

            if (selector != null) {
                selector.update(null);
            }
        }

        // If some languages have been installed, copy them to tessdata
        final File local = getLocalOcrFolder();

        if (local.exists()
            && local.isDirectory()
            && (local.listFiles().length > 0)) {
            // Try immediate copy, in user mode
            final Path[] sources = new Path[]{
                new File(
                local,
                Descriptor.TESSERACT_OCR).toPath()
            };
            final Path target = tessdata.get()
                    .getParentFile()
                    .getParentFile()
                    .toPath();

            try {
                FileCopier fc = new FileCopier(sources, target, true);
                fc.copy();
            } catch (IOException ex) {
                logger.debug(
                        "Could not directly copy language files"
                        + ", will post a copy command at system level");

                // Fallback to a posted copy command
                // Dirty hack since XCOPY (Windows) and cp (Unix) don't treat 
                // source and target similarly when these are directories
                Path source = null;

                if (DescriptorFactory.WINDOWS) {
                    source = local.toPath();
                } else if (DescriptorFactory.LINUX) {
                    source = new File(local, Descriptor.TESSERACT_OCR).toPath();
                }

                // Here, source and target are folders (not regular files)
                appendCommand(descriptor.getCopyCommand(source, target));
            }
        }
    }

    //-----------------------//
    // buildDesiredLanguages //
    //-----------------------//
    private Set<String> buildDesiredLanguages ()
    {
        Set<String> set = new TreeSet<String>();

        // Initialize selected languages with the pre-selected ones
        set.addAll(Arrays.asList(PREDESIRED_LANGUAGES));

        // Include all the already installed languages as well
        for (String lang : ALL_LANGUAGES) {
            if (isLangInstalled(lang)) {
                set.add(lang);
            }
        }

        return set;
    }

    //-------------------//
    // getLocalOcrFolder //
    //-------------------//
    /**
     * Report the local (temporary) folder where OCR language data are
     * expanded before being copied to final target location.
     *
     * @return the local OCR folder
     */
    private File getLocalOcrFolder ()
    {
        return new File(descriptor.getTempFolder(), LOCAL_OCR_FOLDER);
    }

    //-----------------//
    // installLanguage //
    //-----------------//
    private void installLanguage (final String lang)
            throws Exception
    {
        try {
            final String tarName = TESS_VERSION + "." + lang + ".tar";
            final String archiveName = tarName + ".gz";
            final String archiveHttp = TESS_RADIX + "/" + archiveName;

            // Download
            final File temp = descriptor.getTempFolder();
            final File targz = new File(temp, archiveName);
            Utilities.download(archiveHttp, targz);

            // Decompress the .tar.gz
            Expander.unGzip(targz, temp);

            // Expand the .tar to LOCAL_OCR_FOLDER
            final File tar = new File(temp, tarName);
            logger.debug("tar: {}", tar);

            final File local = getLocalOcrFolder();
            final File data = new File(
                    new File(local, Descriptor.TESSERACT_OCR),
                    Descriptor.TESSDATA);
            data.mkdirs();
            logger.debug("local tessdata folder: {}", data.getAbsolutePath());
            Expander.unTar(tar, local);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    Installer.getFrame(),
                    ex.getMessage());
            throw ex;
        }
    }

    //-------------------//
    // uninstallLanguage //
    //-------------------//
    private void uninstallLanguage (final String lang)
            throws Exception
    {
        if (!tessdata.get()
                .exists()) {
            return;
        }

        // Clean up relevant files in the folder
        Files.walkFileTree(
                tessdata.get().toPath(),
                EnumSet.noneOf(FileVisitOption.class),
                1,
                new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile (Path file,
                                              BasicFileAttributes attrs)
                    throws IOException
            {
                Path name = file.getName(file.getNameCount() - 1);
                logger.debug("Visiting {}, name {}", file, name);

                if (name.toString()
                        .startsWith(lang + ".")) {
                    logger.info("Removing file {}", file);

                    try {
                        // Try immediate delete
                        Files.delete(file);
                    } catch (AccessDeniedException ex) {
                        logger.info(
                                "Cannot delete {}, will post a delete command",
                                file);

                        // Post a delete command
                        appendCommand(
                                descriptor.getDeleteCommand(file));
                    }
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }

    //~ Inner Classes ----------------------------------------------------------
    //----------//
    // Tessdata //
    //----------//
    /**
     * Handles the precise location of tessdata.
     */
    private static class Tessdata
    {
        //~ Instance fields ----------------------------------------------------

        private File prefixDir;

        private File tessdataDir;

        //~ Methods ------------------------------------------------------------
        /**
         * Report tessdata directory specification (there is no
         * guarantee that the directory actually exists).
         * If environment variable TESSDATA_PREFIX exists, it is used as the
         * path to tessdata parent directory.
         * Otherwise, we use the OS-dependent default prefix.
         *
         * @return the tessdata directory
         */
        public File get ()
        {
            if (tessdataDir == null) {
                tessdataDir = new File(getPrefix(), Descriptor.TESSDATA);
            }

            return tessdataDir;
        }

        /**
         * Report the parent folder of tessdata, usually the one
         * pointed to by TESSDATA_PREFIX (there is no guarantee that
         * the directory exists or is writable).
         *
         * @return the tessdata PARENT directory
         */
        public File getPrefix ()
        {
            if (prefixDir == null) {
                final String prefix = System.getenv(Descriptor.TESSDATA_PREFIX);

                if (prefix == null) {
                    prefixDir = descriptor.getDefaultTessdataPrefix();
                } else {
                    prefixDir = new File(prefix);
                }
            }

            return prefixDir;
        }
    }
}
