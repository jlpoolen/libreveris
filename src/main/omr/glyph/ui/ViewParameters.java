//----------------------------------------------------------------------------//
//                                                                            //
//                        V i e w P a r a m e t e r s                         //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright (C) Hervé Bitteur 2000-2011. All rights reserved.               //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.glyph.ui;

import omr.constant.Constant;
import omr.constant.ConstantSet;

import omr.log.Logger;

import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Action;

import java.awt.event.ActionEvent;

/**
 * Class {@code ViewParameters} handles parameters for SceneView,
 * using properties referenced through their programmatic name to avoid typos.
 *
 * @author Hervé Bitteur
 */
public class ViewParameters
    extends AbstractBean
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = Logger.getLogger(ViewParameters.class);

    /** Specific application parameters */
    private static final Constants constants = new Constants();

    /** Should the letter boxes be painted */
    public static final String LETTER_BOX_PAINTING = "letterBoxPainting";

    /** Should the stick lines be painted */
    public static final String LINE_PAINTING = "linePainting";

    /** Should the Sections selection be enabled  */
    public static final String SECTION_SELECTION_ENABLED = "sectionSelectionEnabled";

    /** Should the stick attachments be painted */
    public static final String ATTACHMENT_PAINTING = "attachmentPainting";

    //~ Instance fields --------------------------------------------------------

    /** Dynamic flag to remember if section selection is enabled */
    private boolean sectionSelectionEnabled = false;

    //~ Methods ----------------------------------------------------------------

    //-------------//
    // getInstance //
    //-------------//
    public static ViewParameters getInstance ()
    {
        return Holder.INSTANCE;
    }

    //----------------------//
    // isAttachmentPainting //
    //----------------------//
    public boolean isAttachmentPainting ()
    {
        return constants.attachmentPainting.getValue();
    }

    //---------------------//
    // isLetterBoxPainting //
    //---------------------//
    public boolean isLetterBoxPainting ()
    {
        return constants.letterBoxPainting.getValue();
    }

    //----------------//
    // isLinePainting //
    //----------------//
    public boolean isLinePainting ()
    {
        return constants.linePainting.getValue();
    }

    //---------------------------//
    // isSectionSelectionEnabled //
    //---------------------------//
    public boolean isSectionSelectionEnabled ()
    {
        return sectionSelectionEnabled;
    }

    //-----------------------//
    // setAttachmentPainting //
    //-----------------------//
    public void setAttachmentPainting (boolean value)
    {
        boolean oldValue = constants.attachmentPainting.getValue();
        constants.attachmentPainting.setValue(value);
        firePropertyChange(ATTACHMENT_PAINTING, oldValue, value);
    }

    //----------------------//
    // setLetterBoxPainting //
    //----------------------//
    public void setLetterBoxPainting (boolean value)
    {
        boolean oldValue = constants.letterBoxPainting.getValue();
        constants.letterBoxPainting.setValue(value);
        firePropertyChange(LETTER_BOX_PAINTING, oldValue, value);
    }

    //-----------------//
    // setLinePainting //
    //-----------------//
    public void setLinePainting (boolean value)
    {
        boolean oldValue = constants.linePainting.getValue();
        constants.linePainting.setValue(value);
        firePropertyChange(LINE_PAINTING, oldValue, value);
    }

    //----------------------------//
    // setSectionSelectionEnabled //
    //----------------------------//
    public void setSectionSelectionEnabled (boolean value)
    {
        boolean oldValue = sectionSelectionEnabled;
        sectionSelectionEnabled = value;
        firePropertyChange(SECTION_SELECTION_ENABLED, oldValue, value);
    }

    //-------------------//
    // toggleAttachments //
    //-------------------//
    /**
     * Action that toggles the display of attachments in selected sticks
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = ATTACHMENT_PAINTING)
    public void toggleAttachments (ActionEvent e)
    {
    }

    //---------------//
    // toggleLetters //
    //---------------//
    /**
     * Action that toggles the display of letter boxes in selected glyphs
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = LETTER_BOX_PAINTING)
    public void toggleLetters (ActionEvent e)
    {
    }

    //-------------//
    // toggleLines //
    //-------------//
    /**
     * Action that toggles the display of mean line in selected sticks
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = LINE_PAINTING)
    public void toggleLines (ActionEvent e)
    {
    }

    //----------------//
    // toggleSections //
    //----------------//
    /**
     * Action that toggles the ability to select Sections (rather than Glyphs)
     * @param e the event that triggered this action
     */
    @Action(selectedProperty = SECTION_SELECTION_ENABLED)
    public void toggleSections (ActionEvent e)
    {
        //        logger.info(
        //            "Section mode is " + (isSectionSelectionEnabled() ? "on" : "off"));
    }

    //~ Inner Interfaces -------------------------------------------------------

    //--------//
    // Holder //
    //--------//
    private static interface Holder
    {
        //~ Static fields/initializers -----------------------------------------

        public static final ViewParameters INSTANCE = new ViewParameters();
    }

    //~ Inner Classes ----------------------------------------------------------

    //-----------//
    // Constants //
    //-----------//
    private static final class Constants
        extends ConstantSet
    {
        //~ Instance fields ----------------------------------------------------

        final Constant.Boolean letterBoxPainting = new Constant.Boolean(
            true,
            "Should the letter boxes be painted");

        //
        final Constant.Boolean linePainting = new Constant.Boolean(
            false,
            "Should the stick lines be painted");

        //
        final Constant.Boolean attachmentPainting = new Constant.Boolean(
            false,
            "Should the staff & glyph attachments be painted");
    }
}
