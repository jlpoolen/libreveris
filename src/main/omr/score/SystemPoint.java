//----------------------------------------------------------------------------//
//                                                                            //
//                           S y s t e m P o i n t                            //
//                                                                            //
//  Copyright (C) Herve Bitteur 2000-2006. All rights reserved.               //
//  This software is released under the terms of the GNU General Public       //
//  License. Please contact the author at herve.bitteur@laposte.net           //
//  to report bugs & suggestions.                                             //
//----------------------------------------------------------------------------//
//
package omr.score;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Class <code>SystemPoint</code> is a simple Point that is meant to represent a
 * point inside a system, and where coordinates are expressed in units, the
 * origin being the upper-left corner of the system.
 *
 * <p>This specialization is used to take benefit of compiler checks, to prevent
 * the use of points with incorrect meaning or units.
 *
 * @author Herv&eacute; Bitteur
 * @version $Id$
 */
public class SystemPoint
    extends Point
{
    //~ Constructors -----------------------------------------------------------

    //-------------//
    // SystemPoint //
    //-------------//
    /**
     * Creates a new SystemPoint object.
     */
    public SystemPoint ()
    {
    }

    //-------------//
    // SystemPoint //
    //-------------//
    /**
     * Creates a new SystemPoint object, by cloning another system point
     *
     * @param point the system point to clone
     */
    public SystemPoint (SystemPoint point)
    {
        super(point);
    }

    //-------------//
    // SystemPoint //
    //-------------//
    /**
     * Creates a new SystemPoint object, by cloning an untyped point
     *
     * @param x abscissa
     * @param y ordinate
     */
    public SystemPoint (int x,
                      int y)
    {
        super(x, y);
    }

    //~ Methods ----------------------------------------------------------------

    //----------//
    // distance //
    //----------//
    @Override
    public double distance (Point2D pt)
    {
        if (!(pt instanceof SystemPoint)) {
            throw new RuntimeException(
                "Trying to compute distance between heterogeneous points");
        }

        return super.distance(pt);
    }

    //----------//
    // toString //
    //----------//
    @Override
    public String toString ()
    {
        return "SystemPoint[x=" + x + ",y=" + y + "]";
    }
}
