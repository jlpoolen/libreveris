//----------------------------------------------------------------------------//
//                                                                            //
//                             J a i L o a d e r                              //
//                                                                            //
//  Copyright (C) Brenton Partridge 2000-2007. All rights reserved.           //
//  This software is released under the GNU General Public License.           //
//  Please contact users@audiveris.dev.java.net to report bugs & suggestions. //
//----------------------------------------------------------------------------//
package omr.util;

import omr.log.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Class <code>JaiLoader</code> is designed to speed up the load time of the
 * first <code>Picture</code> by allowing the <code>JAI</code> class to be
 * preloaded. Because, in some implementations, the <code>JAI</code> class must
 * load a number of renderers, its static initialization can take a long time.
 * On the first call to any static method in this class, this initialization
 * will begin in a low-priority thread, and any call to
 * <code>ensureLoaded</code> is guaranteed to block until the initialization is
 * complete.
 *
 * @author Brenton Partridge
 * @version $Id$
 */
public class JaiLoader
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = Logger.getLogger(JaiLoader.class);

    /** A future which reflects whether JAI has been loaded */
    private static Future<Void> loaded = OmrExecutors.getCachedLowExecutor()
                                                     .submit(new LoadTask());

    //~ Constructors -----------------------------------------------------------

    private JaiLoader ()
    {
    }

    //~ Methods ----------------------------------------------------------------

    //--------------//
    // ensureLoaded //
    //--------------//
    /**
     * Blocks until the JAI class has been initialized.
     * If initialization has not yet begun, begins initialization.
     *
     */
    public static void ensureLoaded ()
    {
        try {
            loaded.get();
        } catch (Exception e) {
            logger.severe("JAI loading failed", e);
        }
    }

    //---------//
    // preload //
    //---------//
    /**
     * On the first call, starts the initialization.
     */
    public static void preload ()
    {
    }

    //~ Inner Classes ----------------------------------------------------------

    //----------//
    // LoadTask //
    //----------//
    public static class LoadTask
        implements Callable<Void>
    {
        //~ Methods ------------------------------------------------------------

        public Void call ()
            throws Exception
        {
            if (logger.isFineEnabled()) {
                logger.fine("Pre-loading JAI ...");
            }

            long startTime = System.currentTimeMillis();
            javax.media.jai.JAI.getBuildVersion();

            if (logger.isFineEnabled()) {
                logger.fine(
                    "JAI Loaded in " +
                    (System.currentTimeMillis() - startTime) + "ms");
            }

            return null;
        }
    }
}
