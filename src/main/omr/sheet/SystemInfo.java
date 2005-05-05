//-----------------------------------------------------------------------//
//                                                                       //
//                          S y s t e m I n f o                          //
//                                                                       //
//  Copyright (C) Herve Bitteur 2000-2005. All rights reserved.          //
//  This software is released under the terms of the GNU General Public  //
//  License. Please contact the author at herve.bitteur@laposte.net      //
//  to report bugs & suggestions.                                        //
//-----------------------------------------------------------------------//
//      $Id$
package omr.sheet;

import omr.glyph.Glyph;
import omr.stick.Stick;
import omr.stick.StickSection;

import java.util.ArrayList;
import java.util.List;
import omr.glyph.GlyphSection;
import java.util.Comparator;

/**
 * Class <code>SystemInfo</code> gathers information from the original
 * picture about a retrieved system.
 *
 * <p>Nota: All measurements are assumed in pixels.
 */
public class SystemInfo
        implements java.io.Serializable
{
    //~ Instance variables ------------------------------------------------

    // Unique Id for this system (in the sheet)
    private int id;

    // NOTA: Following items are listed in the chronological order of their
    // filling/computation step
    //
    // BARS step
    // =========
    //

    // Index of first stave (from 0).
    private int startIdx;

    // Index of last stave.
    private int stopIdx;

    // Ordinate of top of first stave.
    private int top;

    // Abscissa of beginning of system.
    private int left = -1;

    // Width of the system.
    private int width = -1;

    // Ordinate of bottom of last stave.
    private int bottom;

    // Delta ordinate between first line of first stave & first line of
    // last stave.
    private int deltaY;

    // Staves of this system
    private List<StaveInfo> staves;

    // SYSTEMS step
    // ============
    //
    // Bottom of system related area
    private int areaBottom = -1;

    // Top of system related area
    private int areaTop = -1;

    // Retrieved bar lines in this system
    private List<BarInfo> bars = new ArrayList<BarInfo>();

    // Retrieved stems in this system
    private List<Stick> stems = new ArrayList<Stick>();
    private int maxStemWidth = -1;

    // Retrieved ledgers in this system
    private List<Ledger> ledgers = new ArrayList<Ledger>();
    private int maxLedgerWidth = -1;

    // Retrieved endings in this system
    private List<Ending> endings = new ArrayList<Ending>();

    // Sections
    private transient List<GlyphSection> hSections = new ArrayList<GlyphSection>();
    private List<GlyphSection> vSections = new ArrayList<GlyphSection>();

    // Glyphs & Sticks
    private List<Glyph> glyphs  = new ArrayList<Glyph>();
    private transient List<Stick> hSticks = new ArrayList<Stick>();
    private List<Stick> vSticks = new ArrayList<Stick>();

    //~ Constructors ------------------------------------------------------

    //------------//
    // SystemInfo //
    //------------//
    /**
     * Create a SystemInfo entity, to register the provided parameters
     *
     * @param sheet    the containing sheet
     * @param startIdx the index of the starting stave
     * @param stopIdx  the index of the terminating stave
     *
     * @throws omr.ProcessingException
     */
    public SystemInfo (int id,
                       Sheet sheet,
                       int startIdx,
                       int stopIdx)
    {
        this.id = id;
        this.startIdx = startIdx;
        this.stopIdx = stopIdx;

        // Compute size
        staves = new ArrayList<StaveInfo>();
        staves.addAll(sheet.getStaves().subList(startIdx, stopIdx + 1));

        for (StaveInfo set : staves) {
            if (left == -1) {
                left = set.getLeft();
            } else {
                left = Math.min(left, set.getLeft());
            }

            if (width == -1) {
                width = set.getRight() - left + 1;
            } else {
                width = Math.max(width, set.getRight() - left + 1);
            }
        }

        // First stave
        StaveInfo set = staves.get(0);
        LineInfo line = set.getFirstLine();
        top = line.getLine().yAt(line.getLeft());

        // Last stave
        set = staves.get(staves.size() - 1);
        line = set.getFirstLine();
        deltaY = line.getLine().yAt(line.getLeft()) - top;
        line = set.getLastLine();
        bottom = line.getLine().yAt(line.getLeft());
    }

    //~ Methods -----------------------------------------------------------

    //-------//
    // getId //
    //-------//
    /**
     * Report the id (debugging info) of the system info
     *
     * @return the id
     */
    public int getId ()
    {
        return id;
    }

    //---------------//
    // setAreaBottom //
    //---------------//
    /**
     * Set the ordinate of bottom of system area
     *
     * @param areaBottom ordinate of bottom of system area in pixels
     */
    public void setAreaBottom (int areaBottom)
    {
        this.areaBottom = areaBottom;
    }

    //---------------//
    // getAreaBottom //
    //---------------//
    /**
     * Report the ordinate of the bottom of the picture area whose all
     * items are assumed to belong to this system (the system related area)
     *
     * @return the related area bottom ordinate (in pixels)
     */
    public int getAreaBottom ()
    {
        return areaBottom;
    }

    //------------//
    // setAreaTop //
    //------------//
    /**
     * Set the ordinate of top of systemp area
     *
     * @param areaTop ordinate of top of system area in pixels
     */
    public void setAreaTop (int areaTop)
    {
        this.areaTop = areaTop;
    }

    //------------//
    // getAreaTop //
    //------------//
    /**
     * Report the ordinate of the top of the related picture area
     *
     * @return the related area top ordinate (in pixels)
     */
    public int getAreaTop ()
    {
        return areaTop;
    }

    //---------//
    // getBars //
    //---------//
    /**
     * Report the list of bar lines in this system
     *
     * @return the (abscissa ordered) collection of bar lines
     */
    public List<BarInfo> getBars ()
    {
        return bars;
    }

    //---------//
    // setBars //
    //---------//
    /**
     * For Castor
     */
    public void setBars (List<BarInfo> bars)
    {
        this.bars = bars;
    }

    //-----------//
    // getBottom //
    //-----------//
    /**
     * Report the ordinate of the bottom of the system, which is the
     * ordinate of the last line of the last stave of this system
     *
     * @return the system bottom, in pixels
     */
    public int getBottom ()
    {
        return bottom;
    }

    //-----------//
    // getDeltaY //
    //-----------//
    /**
     * Report the deltaY of the system, that is the difference in ordinate
     * between first and last staves of the system. This deltaY is of
     * course 0 for a one-stave system.
     *
     * @return the deltaY value, expressed in pixels
     */
    public int getDeltaY ()
    {
        return deltaY;
    }

    //------------//
    // getEndings //
    //------------//
    /**
     * Report the collection of endings found
     *
     * @return the endings collection
     */
    public List<Ending> getEndings ()
    {
        return endings;
    }

    //-----------//
    // setGlyphs // For Castor
    //-----------//
    public void setGlyphs (List<Glyph> glyphs)
    {
        this.glyphs = glyphs;
    }

    //-----------//
    // getGlyphs //
    //-----------//
    public List<Glyph> getGlyphs ()
    {
        return glyphs;
    }

    //-----------------------//
    // getHorizontalSections //
    //-----------------------//
    /**
     * Report the collection of horizontal sections in the system related
     * area
     *
     * @return the area horizontal sections
     */
    public List<GlyphSection> getHorizontalSections ()
    {
        return hSections;
    }

    //---------------------//
    // getHorizontalSticks //
    //---------------------//
    /**
     * Report the collection of horizontal sticks left over in the system
     * related area
     *
     * @return the area horizontal sticks
     */
    public List<Stick> getHorizontalSticks ()
    {
        return hSticks;
    }

    //------------//
    // getLedgers //
    //------------//
    /**
     * Report the collection of ledgers found
     *
     * @return the ledger collection
     */
    public List<Ledger> getLedgers ()
    {
        return ledgers;
    }

    //-------------------//
    // getMaxLedgerWidth //
    //-------------------//
    public int getMaxLedgerWidth()
    {
        if (maxLedgerWidth == -1) {
            for (Ledger ledger : ledgers) {
                maxLedgerWidth = Math.max(maxLedgerWidth,
                                          ledger.getContourBox().width);
            }
        }

        return maxLedgerWidth;
    }

    //---------//
    // getLeft //
    //---------//
    /**
     * Report the left abscissa
     *
     * @return the left abscissa value, expressed in pixels
     */
    public int getLeft ()
    {
        return left;
    }

    //-----------//
    // getStaves //
    //-----------//
    /**
     * Report the list of staves that compose this system
     *
     * @return the staves
     */
    public List<StaveInfo> getStaves ()
    {
        return staves;
    }

    //-----------//
    // setStaves //
    //-----------//
    /**
     * Assign list of staves that compose this system
     *
     * @param staves the list of staves
     */
    public void setStaves (List<StaveInfo> staves)
    {
        this.staves = staves;
    }

    //----------//
    // getStems //
    //----------//
    /**
     * Report the collection of stems found
     *
     * @return the stem collection
     */
    public List<Stick> getStems ()
    {
        return stems;
    }

    //-----------------//
    // getMaxStemWidth //
    //-----------------//
    public int getMaxStemWidth()
    {
        if (maxStemWidth == -1) {
            for (Stick stem : stems) {
                maxStemWidth = Math.max(maxStemWidth,
                                          stem.getContourBox().width);
            }
        }

        return maxStemWidth;
    }

    //--------//
    // getTop //
    //--------//
    /**
     * Report the ordinate of the top of this system
     *
     * @return the top ordinate, expressed in pixels
     */
    public int getTop ()
    {
        return top;
    }

    //---------------------//
    // getVerticalSections //
    //---------------------//
    /**
     * Report the collection of vertical sections in the system related
     * area
     *
     * @return the area vertical sections
     */
    public List<GlyphSection> getVerticalSections ()
    {
        return vSections;
    }

    //-------------------//
    // getVerticalSticks //
    //-------------------//
    /**
     * Report the collection of vertical sticks left over in the system
     * related area
     *
     * @return the area vertical sticks clutter
     */
    public List<Stick> getVerticalSticks ()
    {
        return vSticks;
    }

    //----------//
    // getRight //
    //----------//

    /**
     * Report the abscissa of the end of the system
     *
     * @return the right abscissa, expressed in pixels
     */
    public int getRight ()
    {
        return left + width;
    }

    //----------//
    // getWidth //
    //----------//
    /**
     * Report the width of the system
     *
     * @return the width value, expressed in pixels
     */
    public int getWidth ()
    {
        return width;
    }

    //----------//
    // toString //
    //----------//
    /**
     * Report a readable description
     *
     * @return a description based on stave indices
     */
    public String toString ()
    {
        StringBuffer sb = new StringBuffer(80);
        sb.append("{SystemInfo#").append(id);
        sb.append(" ").append(startIdx);
        if (startIdx != stopIdx) {
            sb.append("..").append(stopIdx);
        }
        sb.append("}");

        return sb.toString();
    }

    //-------------//
    // getStartIdx //
    //-------------//
    /**
     * Report the index of the starting staff of this system
     *
     * @return the staff index, counted from 0
     */
    public int getStartIdx ()
    {
        return startIdx;
    }

    //-------------//
    // setStartIdx //
    //-------------//
    public void setStartIdx (int startIdx)
    {
        this.startIdx = startIdx;
    }

    //-------------//
    // getStaveAtY //
    //-------------//
    public StaveInfo getStaveAtY (int y)
    {
        for (StaveInfo stave : staves) {
            if (y <= stave.getAreaBottom()) {
                return stave;
            }
        }
        System.out.println("getStaveAtY. y=" + y);
        for (StaveInfo stave : staves) {
            System.out.println("areaTop=" + stave.getAreaTop() +
                               " areaBottom=" + stave.getAreaBottom());
        }
        return null;
    }

    //------------//
    // getStopIdx //
    //------------//
    /**
     * Report the index of the terminating staff of this system
     *
     * @return the stopping staff index, counted from 0
     */
    public int getStopIdx ()
    {
        return stopIdx;
    }

    //------------//
    // setStopIdx //
    //------------//
    public void setStopIdx (int stopIdx)
    {
        this.stopIdx = stopIdx;
    }
}
