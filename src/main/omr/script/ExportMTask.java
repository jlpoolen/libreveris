//----------------------------------------------------------------------------//
//                                                                            //
//        E x p o r t M e a s u r e C o o r d i n a t e s T a s k             //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright © John L. Poole and others 2000-2013. All rights reserved.      //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.script;


import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import omr.score.Score;
import omr.sheet.Sheet;
import omr.util.ExportMeasureCoordinates;

/**
 * Class {@code ExportTask} exports measure coordinates to an XML file
 *    based on ExportTask
 *
 * @author John L. Poole
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ExportMTask
        extends ScriptTask
{
    //~ Instance fields --------------------------------------------------------

    /** The file used for export */
    @XmlAttribute
    private String path;

    /** Should we add our signature? */
    @XmlAttribute(name = "inject-signature")
    private Boolean injectSignature;

    //~ Constructors -----------------------------------------------------------
    //------------------------------//
    // ExportMeasureCoordinatesTask //
    //------------------------------//
    /**
     * Create a task to export the coordinates of measures of a 
     * score entities of a sheet
     *
     * @param path override the full path of the export file
     * which normally goes to the same directory as the image
     * and has the same name as the image, but for the ".xml" suffix
     */
    public ExportMTask (String path)
    {
        this.path = path;
    }

    //------------------------------//
    // ExportMeasureCoordinatesTask //
    //------------------------------//
    /** No-arg constructor needed by JAXB */
    private ExportMTask ()
    {
    }

    //~ Methods ----------------------------------------------------------------
    //------//trunk/src/main/omr/script/ExportMeasureCoordinatesTask.java
    // core //
    //------//
    @Override
    public void core (Sheet sheet)
    {
        logger.info("Invoking ExportMTask.core()");
        Score s = sheet.getScore();
    	  ExportMeasureCoordinates emc = new ExportMeasureCoordinates(s);
    	  emc.export();
        //ScoresManager.getInstance()
                //.export(
                //sheet.getScore(),
                //(path != null) ? new File(path) : null,
                //injectSignature);
    }

    //-----------------//
    // internalsString //
    //-----------------//
    @Override
    protected String internalsString ()
    {
        return " export " + path + super.internalsString();
    }
}
