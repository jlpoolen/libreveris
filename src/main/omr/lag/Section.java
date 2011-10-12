//----------------------------------------------------------------------------//
//                                                                            //
//                               S e c t i o n                                //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright (C) Hervé Bitteur 2000-2011. All rights reserved.               //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.lag;

import omr.glyph.facets.Glyph;

import omr.graph.Vertex;

import omr.lag.ui.SectionView;

import omr.math.Barycenter;
import omr.math.Line;
import omr.math.PointsCollector;

import omr.run.Oriented;
import omr.run.Run;

import omr.score.common.PixelPoint;
import omr.score.common.PixelRectangle;

import omr.sheet.SystemInfo;
import omr.sheet.picture.Picture;

import omr.stick.StickRelation;

import omr.util.Vip;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Interface {@code Section} handles a section of contiguous and compatible
 * {@link Run} instances.
 *
 * <p> A section does not carry orientation information, only the containing
 * {@link Lag} has this information.  Thus all runs of a given lag (and
 * consequently all sections made of these runs) share the same orientation.
 *
 * <ol> <li> Positions increase in parallel with run numbers, so the thickness
 * of a section is defined as the delta between last and first positions, in
 * other words its number of runs. </li>
 *
 * <li> Coordinates increase along any section run, so the section start is the
 * minimum of all run starting coordinates, and the section stop is the maximum
 * of all run stopping coordinates. We define section length as the value: stop
 * - start +1 </li> </ol>
 *
 * @author Hervé Bitteur
 */
public interface Section
    extends Vertex<Lag, Section>, Comparable<Section>, Oriented, SectionView, Vip
{
    //~ Static fields/initializers ---------------------------------------------

    /** A section comparator, using section id */
    public static final Comparator<Section> idComparator = new Comparator<Section>() {
        public int compare (Section s1,
                            Section s2)
        {
            return Integer.signum(s1.getId() - s2.getId());
        }
    };

    /** For comparing Section instances on their decreasing length */
    public static final Comparator<Section> reverseLengthComparator = new Comparator<Section>() {
        public int compare (Section s1,
                            Section s2)
        {
            return Integer.signum(s2.getLength() - s1.getLength());
        }
    };

    /** For comparing Section instances on their decreasing weight */
    public static final Comparator<Section> reverseWeightComparator = new Comparator<Section>() {
        public int compare (Section s1,
                            Section s2)
        {
            return Integer.signum(s2.getWeight() - s1.getWeight());
        }
    };

    /** For comparing Section instances on their start value */
    public static final Comparator<Section> startComparator = new Comparator<Section>() {
        public int compare (Section s1,
                            Section s2)
        {
            return s1.getStartCoord() - s2.getStartCoord();
        }
    };

    /** For comparing Section instances on their pos value */
    public static final Comparator<Section> posComparator = new Comparator<Section>() {
        public int compare (Section s1,
                            Section s2)
        {
            return s1.getFirstPos() - s2.getFirstPos();
        }
    };


    //~ Methods ----------------------------------------------------------------

    /**
     * Return the <b>absolute</b> line which best approximates the section
     * @return the absolute fitted line
     * @see #getOrientedLine()
     */
    public Line getAbsoluteLine ();

    /**
     * Check that the section at hand is a candidate section not yet aggregated
     * to a recognized stick.
     * @return true if aggregable (but not yet aggregated)
     */
    public boolean isAggregable ();

    /**
     * Report the section area absolute center.
     *
     * @return the area absolute center
     */
    public PixelPoint getAreaCenter ();

    /**
     * Report the ratio of length over thickness
     *
     * @return the "slimness" of the section
     */
    public double getAspect ();

    /**
     * Return the absolute point which is at the mass center of the section,
     * with all pixels considered of equal weight.
     *
     * @return the mass center of the section, as a absolute point
     */
    public PixelPoint getCentroid ();

    /**
     * Define a color, according to the data at hand, that is according to the
     * role of this section in the enclosing stick.
     * @return the related color
     */
    public Color getColor ();

    /**
     * Return a COPY of the absolute bounding box.
     * Useful to quickly check if the section needs to be repainted.
     * @return the absolute bounding box
     */
    public PixelRectangle getContourBox ();

    /**
     * @param fat the fat flag
     */
    public void setFat (boolean fat);

    /**
     * @return the fat flag, if any
     */
    public Boolean isFat ();

    /**
     * Return the adjacency ratio on the incoming junctions of the section.
     * This is computed as the ratio to the length of the first run, of the
     * sum of run overlapping lengths of the incoming junctions. In other
     * words, this is a measure of how much the section at hand is
     * overlapped with runs.
     *
     * <ul> <li> An isolated section/vertex, such as the one related to a
     * barline, will exhibit a very low adjacency ratio. </li>
     *
     * <li> On the contrary, a section which is just a piece of a larger glyph,
     * such as a treble clef or a brace, will have a higher adjacency. </li>
     * </ul>
     *
     * @return the percentage of overlapped run length
     * @see #getLastAdjacency
     */
    public double getFirstAdjacency ();

    /**
     * Set the position of the first run in the section.
     * @param firstPos position of the first run, abscissa for a vertical run,
     *                 ordinate for a horizontal run.
     */
    public void setFirstPos (int firstPos);

    /**
     * Return the position (x for vertical runs, y for horizontal runs) of the
     * first run in the section
     * @return the position
     */
    public int getFirstPos ();

    /**
     * Return the first run within the section
     * @return the run, which always exists
     */
    public Run getFirstRun ();

    /**
     * Return the contribution of the section to the foreground
     * @return the section foreground weight
     */
    public int getForeWeight ();

    /**
     * Assign the containing glyph
     * @param glyph the containing glyph
     */
    public void setGlyph (Glyph glyph);

    /**
     * Report the glyph the section belongs to, if any
     * @return the glyph, which may be null
     */
    public Glyph getGlyph ();

    /**
     * Checks whether the section is already a member of a glyph
     * @return the result of the test
     */
    public boolean isGlyphMember ();

    /**
     * Check that the section at hand is a member section, aggregated to a known
     * glyph.
     * @return true if member of a known glyph
     */
    public boolean isKnown ();

    /**
     * Return the adjacency ratio at the end of the section/vertex at hand.  See
     * getFirstAdjacency for explanation of the role of adjacency.
     * @return the percentage of overlapped run length
     * @see #getFirstAdjacency
     */
    public double getLastAdjacency ();

    /**
     * Return the position of the last run of the section
     * @return the position of last run
     */
    public int getLastPos ();

    /**
     * Return the last run of the section
     * @return this last run (rightmost run for vertical section)
     */
    public Run getLastRun ();

    /**
     * Return the length of the section, along the runs direction
     * @return stop - start +1
     */
    public int getLength ();

    /**
     * Return the mean gray level of the section
     * @return the section foreground level (0 -> 255)
     */
    public int getLevel ();

    /**
     * Return the size of the longest run in the section
     * @return the maximum run length
     */
    public int getMaxRunLength ();

    /**
     * Report the ratio of length over mean thickness
     * @return the "slimness" of the section
     */
    public double getMeanAspect ();

    /**
     * Return the average value for all run lengths in the section.
     * @return the mean run length
     */
    public int getMeanRunLength ();

    /**
     * Report the average thickness of the section
     * @return the average thickness of the section
     */
    public double getMeanThickness ();

    /**
     * A read-only access to adjacent sections from opposite orientation
     * @return the set of adjacent sections of the opposite orientation
     */
    public Set<Section> getOppositeSections ();

    /**
     * Return the section oriented bounding rectangle, so please clone it if you
     * want to modify it afterwards
     * @return the section bounding rectangle
     */
    public Rectangle getOrientedBounds ();

    /**
     * Report the (oriented) line which best approximates the section
     * @return the oriented fitted line
     * @see #getAbsoluteLine()
     */
    public Line getOrientedLine ();

    /**
     * Create an iterator along the absolute polygon that represents the section
     * contour
     * @return an iterator on the underlying polygon
     */
    public PathIterator getPathIterator ();

    /**
     * This method allows to write a specific pixel at given oriented
     * coordinates in the given picture.
     *
     * @param picture the picture to be updated
     * @param cp      the (coord,pos) coordinates of the specified point
     * @param val     the color value for the pixel
     */
    public void setPixel (Picture picture,
                          Point   cp,
                          int     val);

    /**
     * Return the absolute polygon that defines the display contour.
     *
     * @return the absolute perimeter contour
     */
    public Polygon getPolygon ();

    /**
     * @param processed the processed to set
     */
    public void setProcessed (boolean processed);

    /**
     * @return the processed
     */
    public boolean isProcessed ();

    /**
     * Report the absolute centroid of the section pixels found in the provided
     * absolute region of interest
     * @param absRoi the absolute rectangle that defines the region of interest
     * @return the absolute centroid
     */
    public PixelPoint getRectangleCentroid (PixelRectangle absRoi);

    //TODO:  REMOVE getRelation ASAP
    public StickRelation getRelation ();

    /**
     * Report the number of runs this sections contains
     * @return the nb of runs in the section
     */
    public int getRunCount ();

    /**
     * Return the list of all runs in this section
     * @return the section runs
     */
    public List<Run> getRuns ();

    /**
     * Return the smallest run starting coordinate, which means the smallest y
     * value (ordinate) for a section of vertical runs.
     * @return the starting coordinate of the section
     */
    public int getStartCoord ();

    /**
     * Return the approximate absolute point which starts the section
     * (left point for horizontal section, top point for vertical section)
     * @return the approximate absolute starting point
     */
    public PixelPoint getStartPoint ();

    /**
     * Return the largest run stopping coordinate, which is the largest y value
     * (ordinate) for a section of vertical runs.
     * @return the stopping coordinate of the section
     */
    public int getStopCoord ();

    /**
     * Return the approximate absolute point which stops the section
     * (right point for horizontal section, bottom point for vertical section)
     * @return the approximate absolute stopping point
     */
    public PixelPoint getStopPoint ();

    /**
     * Assign a containing system
     * @param system the system to set
     */
    public void setSystem (SystemInfo system);

    /**
     * Report the containing system
     * @return the system (may be null)
     */
    public SystemInfo getSystem ();

    /**
     * Return the thickness of the section, which is just the number of runs.
     * @return the nb of runs in this section
     */
    public int getThickness ();

    /**
     * Return the thickness of the section at the provided coord value
     * @param coord the coordinate (x for horizontal, y for vertical) around
     * which the thickness is to be measured
     * @param probeWidth the width of the probe to use
     * @return the thickness around this location
     */
    public int getThicknessAt (int coord,
                               int probeWidth);

    /**
     * Reports whether this section is organized in vertical runs
     * @return true if vertical, false otherwise
     */
    public boolean isVertical ();

    /**
     * Return the total weight of the section, which is the sum of the weight
     * (length) of all runs.
     * @return the section weight
     */
    public int getWeight ();

    /**
     * Register the adjacency of a section from the other orientation
     * @param otherSection the other section to remember
     */
    public void addOppositeSection (Section otherSection);

    /**
     * Extend a section with the given run. This new run is assumed to be
     * contiguous to the current last run of the section, no check is performed.
     * @param run the new last run
     */
    public void append (Run run);

    /**
     * Compute the various cached parameters from scratch
     */
    public void computeParameters ();

    /**
     * Predicate to check whether the given point falls within one of the
     * section runs.
     *
     * @param coord coordinate along section length
     * @param pos   run position
     * @return true if point(coord,pos) is contained in the section
     */
    public boolean contains (int coord,
                             int pos);

    /**
     * Cumulate in the provided Barycenter the section pixels that are contained
     * in the provided roi Rectangle. If the roi is null, all pixels are
     * cumulated into the barycenter.
     * @param barycenter the absolute point to populate
     * @param absRoi the absolute rectangle of interest
     */
    public void cumulate (Barycenter     barycenter,
                          PixelRectangle absRoi);

    /**
     * Cumulate all points that compose the runs of the section, into the
     * provided <b>absolute</b> collector.
     * @param collector the absolute points collector to populate
     */
    public void cumulate (PointsCollector collector);

    /**
     * Draws a basic representation of the section, using ascii characters
     */
    public void drawAscii ();

    /**
     * Build an image with the pixels of this section
     * @param im the image to populate with this section
     * @param box absolute bounding box (used as image coordinates reference)
     */
    public void fillImage (BufferedImage im,
                           Rectangle     box);

    /**
     * Draws the section, into the provided table
     */
    public void fillTable (char[][]  table,
                           Rectangle box);

    /**
     * Return the next sibling section, both linked by source of last incoming
     * edge
     * @return the next sibling or null
     */
    public Section inNextSibling ();

    /**
     * Return the previous sibling section, both linked by source of first
     * incoming edge
     * @return the previous sibling or null
     */
    public Section inPreviousSibling ();

    /**
     * Merge this section with the other provided section, which is not
     * affected, and must generally be destroyed.
     * It is assumed (and not checked) that the two sections are
     * contiguous.
     * @param other the other section to include into this one
     */
    public void merge (Section other);

    /**
     * Return the next sibling section, both linked by target of the last
     * outgoing edge
     * @return the next sibling or null
     */
    public Section outNextSibling ();

    /**
     * Return the previous sibling section, both linked by target of the first
     * outgoing edge
     * @return the previous sibling or null
     */
    public Section outPreviousSibling ();

    /**
     * Add a run at the beginning rather than at the end of the section
     * @param run the new first run
     */
    public void prepend (Run run);

    /**
     * Nullify the fat sticky attribute
     */
    public void resetFat ();

    /**
     * Apply a translation vector to this section
     * @param vector the translation vector
     */
    public void translate (PixelPoint vector);

    /**
     * Write the pixels of the section in the given picture
     * @param picture the picture to update
     * @param pixel   the gray level to be used for the pixels
     */
    public void write (Picture picture,
                       int     pixel);
}
