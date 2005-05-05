//-----------------------------------------------------------------------//
//                                                                       //
//                      J u n c t i o n P o l i c y                      //
//                                                                       //
//  Copyright (C) Herve Bitteur 2000-2005. All rights reserved.          //
//  This software is released under the terms of the GNU General Public  //
//  License. Please contact the author at herve.bitteur@laposte.net      //
//  to report bugs & suggestions.                                        //
//-----------------------------------------------------------------------//
//      $Id$
package omr.lag;

/**
 * Class <code>JunctionPolicy</code> encapsulates the policy that decides if a
 * run can extend a given section. If not, the run is part of a new section,
 * linked to the previous one by a junction.
 */
public abstract class JunctionPolicy
{
    //~ Methods --------------------------------------------------------------

    //---------------//
    // consistentRun //
    //---------------//

    /**
     * Check if provided run is consistent with the section as defined so far
     *
     * @param run     the candidate run for section extension
     * @param section the to-be extended section
     *
     * @return true is extension is compatible with the defined junction
     *         policy
     */
    public abstract boolean consistentRun (Run run,
                                           Section section);
}
