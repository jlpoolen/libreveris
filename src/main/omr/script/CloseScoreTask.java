//----------------------------------------------------------------------------//
//                                                                            //
//                           C l o s e S c o r e T a s k                      //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright © John L. Poole and others 2000-2013. All rights reserved.      //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.script;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import omr.score.Score;
import omr.sheet.Sheet;

/**
 * Class {@code CloseScore} closes the score and frees up memory
 *
 * @author John L. Poole
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CloseScoreTask
        extends ScriptTask
{
    //~ Instance fields --------------------------------------------------------

    /** Should we add our signature? */
    @XmlAttribute(name = "inject-signature")
    private Boolean injectSignature;

    //~ Constructors -----------------------------------------------------------
    //------------------------------//
    //       CloseScoreTask         //
    //------------------------------//
    /**
     * Create a task to close a score
     */
    

    //------------------------------//
    // ExportMeasureCoordinatesTask //
    //------------------------------//
    /** No-arg constructor needed by JAXB */
    private CloseScoreTask ()
    {
    }

    //~ Methods --------------------------------------------------------------//
    @Override
    public void core (Sheet sheet)
    {
        logger.info("Invoking .core()");
        Score s = sheet.getScore();
    	s.close();
        System.gc();  // throw out the garbage, it seems 8 files takes up 2g
    }

    //-----------------//
    // internalsString //
    //-----------------//
    @Override
    protected String internalsString ()
    {
        return " close score " ;
    }
}
