//-----------------------------------------------------------------------//
//                                                                       //
//                               B o a r d                               //
//                                                                       //
//  Copyright (C) Herve Bitteur 2000-2005. All rights reserved.          //
//  This software is released under the terms of the GNU General Public  //
//  License. Please contact the author at herve.bitteur@laposte.net      //
//  to report bugs & suggestions.                                        //
//-----------------------------------------------------------------------//
//      $Id$
package omr.ui;

import omr.util.Logger;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Class <code>Board</code> defines the common properties of any user board
 * such as PixelBoard, SectionBoard, and the like
 */
public class Board
    extends Panel
{
    //~ Static variables/initializers -------------------------------------

    private static final Logger logger = Logger.getLogger(Board.class);

    /** To indicate that value is invalid */
    public static final int NO_VALUE = 0;

    //~ Instance variables ------------------------------------------------

    // Board Tag
    private Tag tag;

    //~ Constructors ------------------------------------------------------

    //-------//
    // Board //
    //-------//
    /**
     * Create a board
     *
     * @param tag the tag to wrap the board
     */
    public Board (Tag tag)
    {
        this.tag = tag;
    }

    //--------//
    // getTag //
    //--------//
    /**
     * Report the tag of the board
     *
     * @return the board tag
     */
    public Tag getTag ()
    {
        return tag;
    }

    //-------------//
    // emptyFields //
    //-------------//
    /**
     * Empty all the text fields of the given panel
     *
     * @param panel the panel to "blank".
     */
    protected void emptyFields(JPanel panel)
    {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JTextField){
                ((JTextField) comp).setText("");
            }
        }
    }

    //~ Classes -----------------------------------------------------------

    //-----//
    // Tag //
    //-----//
    /**
     * Enum <code>Tag</code> is used to refer to the various user boards.
     */
    public static enum Tag
    {
            /** Board for pixel info (coordinates, pixel grey level) */
            PIXEL   ("Pixel"),

            /** Board for section info */
            SECTION ("Section"),

            /** Board for glyph info */
            GLYPH   ("Glyph"),

            /** Board for filter results */
            FILTER  ("Filter"),

            /** Custom board */
            CUSTOM  ("Custom");

        //~ Instance variables --------------------------------------------

        private String label;

        //~ Constructors --------------------------------------------------

        //-----//
        // Tag //
        //-----//
        /**
         * Create a tag enum item, with its provided description
         *
         * @param label the tag description
         */
        Tag (String label)
        {
            this.label = label;
        }

        //----------//
        // toString //
        //----------//
        /**
         * Report the tag description
         *
         * @return the tag description
         */
        @Override
            public String toString()
        {
            return label;
        }
    }
}
