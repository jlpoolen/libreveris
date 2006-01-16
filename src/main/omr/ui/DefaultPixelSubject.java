//-----------------------------------------------------------------------//
//                                                                       //
//               D e f a u l t S e c t i o n S u b j e c t               //
//                                                                       //
//  Copyright (C) Herve Bitteur 2000-2006. All rights reserved.          //
//  This software is released under the terms of the GNU General Public  //
//  License. Please contact the author at herve.bitteur@laposte.net      //
//  to report bugs & suggestions.                                        //
//-----------------------------------------------------------------------//

package omr.ui;

import omr.util.DefaultSubject;
import omr.util.Subject;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Class <code>DefaultPixelSubject</code> is an implementation of the
 * specific {@link Subject} meant for {@link PixelObserver} observers
 *
 * @author Herv&eacute; Bitteur
 * @version $Id$
 */
public class DefaultPixelSubject
    extends DefaultSubject<PixelSubject, PixelObserver, Point>
{
    //----------------//
    // notifyObservers //
    //----------------//
    public void notifyObservers (Point ul,
                                 int level)
    {
        for (PixelObserver observer : observers) {
            observer.update(ul, level);
        }
    }

    //----------------//
    // notifyObservers //
    //----------------//
    public void notifyObservers (Rectangle rect)
    {
        for (PixelObserver observer : observers) {
            observer.update(rect);
        }
    }
}
