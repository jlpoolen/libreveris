//----------------------------------------------------------------------------//
//                                                                            //
//                                  C l e f                                   //
//                                                                            //
//  Copyright (C) Herve Bitteur 2000-2006. All rights reserved.               //
//  This software is released under the terms of the GNU General Public       //
//  License. Please contact the author at herve.bitteur@laposte.net           //
//  to report bugs & suggestions.                                             //
//----------------------------------------------------------------------------//
//
package omr.score;

import omr.glyph.Glyph;
import omr.glyph.Shape;

import omr.score.visitor.ScoreVisitor;

import omr.util.Logger;

/**
 * Class <code>Clef</code> encapsulates a clef.
 *
 * @author Herv&eacute Bitteur
 * @version $Id$
 */
public class Clef
    extends MeasureNode
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = Logger.getLogger(Clef.class);

    //~ Instance fields --------------------------------------------------------

    /** Precise clef shape, from Clefs range in Shape class */
    private Shape shape;

    /**
     * Step line of the clef : -4 for top line (Baritone), -2 for Bass, 0 for
     * Alto, +2 for Treble and Mezzo-Soprano, +4 for bottom line (Soprano).
     */
    private int pitchPosition;

    //~ Constructors -----------------------------------------------------------

    //------//
    // Clef //
    //------//
    /**
     * Create a Clef instance
     *
     * @param measure the containing measure
     * @param staff the assigned staff
     * @param shape precise clef shape
     * @param center center wrt system (in units)
     * @param pitchPosition pitch position
     */
    public Clef (Measure     measure,
                 Staff       staff,
                 Shape       shape,
                 SystemPoint center,
                 int         pitchPosition)
    {
        super(measure);

        setStaff(staff);
        this.shape = shape;
        setCenter(center);
        this.pitchPosition = pitchPosition;
    }

    //~ Methods ----------------------------------------------------------------

    //------------//
    // noteStepOf //
    //------------//
    public static Note.Step noteStepOf (int   pitchPosition,
                                        Shape clefShape)
    {
        switch (clefShape) {
        case G_CLEF :
        case G_CLEF_OTTAVA_ALTA :
        case G_CLEF_OTTAVA_BASSA :
            return Note.Step.values()[(71 - pitchPosition) % 7];

        case F_CLEF :
        case F_CLEF_OTTAVA_ALTA :
        case F_CLEF_OTTAVA_BASSA :
            return Note.Step.values()[(73 - pitchPosition) % 7];

        default :
            logger.severe("No note step defined for clef shape " + clefShape);

            return Note.Step.A; // To keep compiler happy
        }
    }

    //----------//
    // octaveOf //
    //----------//
    public static int octaveOf (int   pitchPosition,
                                Shape clefShape)
    {
        switch (clefShape) {
        case G_CLEF :
            return (34 - pitchPosition) / 7;

        case G_CLEF_OTTAVA_ALTA :
            return ((34 - pitchPosition) / 7) + 1;

        case G_CLEF_OTTAVA_BASSA :
            return ((34 - pitchPosition) / 7) - 1;

        case F_CLEF :
            return (22 - pitchPosition) / 7;

        case F_CLEF_OTTAVA_ALTA :
            return ((22 - pitchPosition) / 7) + 1;

        case F_CLEF_OTTAVA_BASSA :
            return ((22 - pitchPosition) / 7) - 1;

        default :
            logger.severe("No note octave defined for clef shape " + clefShape);

            return 0; // To keep compiler happy
        }
    }

    //------------------//
    // getPitchPosition //
    //------------------//
    /**
     * Report the vertical position within the staff
     *
     * @return the pitch position
     */
    public int getPitchPosition ()
    {
        return pitchPosition;
    }

    //----------//
    // getShape //
    //----------//
    /**
     * Report the precise shape of this clef
     *
     * @return the clef shape
     */
    public Shape getShape ()
    {
        return shape;
    }

    //--------//
    // accept //
    //--------//
    @Override
    public boolean accept (ScoreVisitor visitor)
    {
        return visitor.visit(this);
    }

    //----------//
    // toString //
    //----------//
    @Override
    public String toString ()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{Clef");
        sb.append(" ")
          .append(shape);

        sb.append(" pp=")
          .append((int) Math.rint(pitchPosition));

        sb.append("}");

        return sb.toString();
    }

    //----------//
    // populate //
    //----------//
    static boolean populate (Glyph       glyph,
                             Measure     measure,
                             Staff       staff,
                             SystemPoint center)
    {
        Shape shape = glyph.getShape();

        switch (shape) {
        case G_CLEF :
        case G_CLEF_OTTAVA_ALTA :
        case G_CLEF_OTTAVA_BASSA :
            new Clef(measure, staff, shape, center, 2);

            return true;

        case F_CLEF :
        case F_CLEF_OTTAVA_ALTA :
        case F_CLEF_OTTAVA_BASSA :
            new Clef(measure, staff, shape, center, -2);

            return true;

        default :
            logger.warning("No implementation yet for " + shape);

            return false;
        }
    }
}
