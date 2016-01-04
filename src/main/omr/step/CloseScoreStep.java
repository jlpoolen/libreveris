//----------------------------------------------------------------------------//
//                                                                            //
//                    C l o s e S c o r e S t e p                             //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright © Hervé Bitteur and others 2000-2013. All rights reserved.      //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.step;


import omr.sheet.Sheet;
import omr.sheet.SystemInfo;
import omr.score.Score;
import omr.util.ExportCoordinates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


/**
 * Class {@code CloseScoreStep} closes the score
 *
 * @author John L. Poole
 */
public class CloseScoreStep
        extends AbstractStep
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = 
            LoggerFactory.getLogger(CloseScoreStep.class);

    //~ Constructors -----------------------------------------------------------
    //----------------//
    // CloseScoreStep //
    //----------------//
    /**
     * Creates a new ExportStep object.
     */
    public CloseScoreStep ()
    {
        super(Steps.CLOSESCORE,
                Level.SHEET_LEVEL,
                Mandatory.OPTIONAL,
                DATA_TAB,
                "Closes the score");
    }

    //~ Methods ----------------------------------------------------------------
    //------//
    // doit //
    //------//
    @Override
    public void doit (Collection<SystemInfo> systems,
                      Sheet sheet)
            throws StepException
    {
        Score s = sheet.getScore();
        logger.info("Closing Score of " + s.getImagePath());
        s.close();
    }
}
