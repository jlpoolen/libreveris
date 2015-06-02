/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package omr.util;

import java.awt.Rectangle;
import java.io.File;
import java.io.PrintWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Collection;
import java.util.List;
import java.util.regex.*;

import omr.score.Score;
import omr.score.entity.Measure;
import omr.score.entity.Page;
import omr.score.entity.PageNode;
import omr.score.entity.ScorePart;
import omr.score.entity.ScoreSystem;
import omr.score.entity.SystemPart;
import omr.score.ui.ScoreController;

import omr.sheet.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given the current page, creates an XML dump of the system, parts, and 
 * measures providing their coordinates and dimensions based upon just as they
 * appear on the scanned image, not as they might appear in a rendering
 * of a Score.  This is for create overlays on the scans themselves.
 * 
 * @author jlpoole
 */
public class ExportMeasureCoordinates {
    private Score score;
     
         /** Usual logger utility. */
    private static final Logger logger = 
            LoggerFactory.getLogger(ExportMeasureCoordinates.class);
    private String indent = "";
    private int defaultIndentSpace = 2;
    private Pattern p = Pattern.compile("^(.*)\\.(tiff?|png)$",
            Pattern.CASE_INSENSITIVE);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
    public ExportMeasureCoordinates() {
      
    }
    public void export(){
        String xmlFile = null;
        PrintWriter xmlWriter = null;
        Calendar calobj = Calendar.getInstance();
        score  = ScoreController.getCurrentScore();

        //
        // Create an output file with the same name, but ".xml" suffix
        //
        String imageFile = score.getImageFile().getName();
        String imageDir  = score.getImageFile().getParentFile().getAbsolutePath();
        logger.info("imageFile = " + imageFile);
        logger.info("imageDir = " + imageDir);
        Matcher m = p.matcher(imageFile);
        boolean outToFile = m.find();
        if (outToFile){
            xmlFile = m.group(1);
            xmlFile = imageDir  + File.separator + xmlFile;
            try {  
                xmlWriter = new PrintWriter(xmlFile + ".xml", "UTF-8");
            }  catch (Exception e)  {
                logger.info("Could not open output file: " + xmlFile);
            }
        } else {
            logger.info("Could not derive file from " + imageFile);
        }      
        int systemId, partId, measureId = 0;
        String xmlOut = "";
        logger.info("score.getImagePath = {}",score.getImagePath());
        // what is the current page ?
        // TODO: Danger need to identify current Page!
        Page currentPage = score.getFirstPage();  
        
        List currentSystems = currentPage.getSystems();
        xmlOut += indent + "<page id=\"" + score.getImagePath() + "\" ";
        xmlOut += indent + "\nomrDate=\"" + sdf.format(calobj.getTime()) + "\" ";
        xmlOut += indent + "\nimageLastModified=\"" 
                + sdf.format(score.getImageFile().lastModified()) + "\" ";
        //
        // TODO: add version of Libreveris, in case we want to rerun OMR
        //       later on with a revised version.
        //
        xmlOut = closeElement(xmlOut);
        increaseIndent();
        for (systemId = 0; systemId < currentSystems.size();systemId++) {
            ScoreSystem curSystem = (ScoreSystem) currentSystems.get(systemId);
            xmlOut += indent + "<system id=\"" + curSystem.getId() + "\"";
            
            Rectangle systemRect = curSystem.getBox();
            xmlOut += getRectangleAttributes(systemRect);
            xmlOut = closeElement(xmlOut);
            
            List currentParts = curSystem.getParts();
            increaseIndent();
            for (partId = 0; partId < currentParts.size(); partId++){              
                SystemPart curPart = (SystemPart) currentParts.get(partId); 
                xmlOut += indent + "<part id=\"" + curPart.getId() + "\"";
                
                Rectangle partRect = curPart.getBox();
                xmlOut += getRectangleAttributes(partRect);
                xmlOut = closeElement(xmlOut);
                
                List myMeasures = curPart.getMeasures();
                increaseIndent();
                for (measureId = 0; measureId < myMeasures.size(); measureId++){
                    Measure curMeasure = (Measure) myMeasures.get(measureId);
                    xmlOut += "      <measure id=\"" 
                            + curMeasure.getIdValue() + "\"";
                    
                    logger.info("id = {}",curMeasure.getIdValue()); 
                    Rectangle measureRect = curMeasure.getBox();
                    logger.info(" rect.x = {}", measureRect.x);
                    logger.info(" rect.y = {}", measureRect.y);
                    xmlOut += getRectangleAttributes(measureRect);
                    xmlOut = closeElementSingle(xmlOut);
                } 
                 xmlOut += endElement("part");
            }
            xmlOut += endElement("system");
        }
        xmlOut += endElement("page");
        logger.info(xmlOut);
        if (outToFile){
            xmlWriter.print(xmlOut);
            xmlWriter.close();
        }
    }
    private String getRectangleAttributes (Rectangle r){
        String attrs = "";
        attrs+= " x=\"" + r.x + "\" y=\"" +  r.y + "\""
                        + " width=\"" + r.width 
                        + "\" height=\"" + r.height + "\"";
        return attrs;
    }
    /**
     * For elements which will have closing tags afterwards
     * @param e Element name.
     * @return  Element name + ">" and new line
     */
    private String closeElement(String e){
        return e + ">\n";
    }
    
    /**
     *  For elements which will have no children.
     * @param e Element name.
     * @return  Element name + "/>" and new line
     */
    private String closeElementSingle(String e){
        return e + "/>\n";
    }
    private String endElement (String e){
        decreaseIndent();
        String s = indent + "</" + e + ">\n";
        return s;
    }
    
    private void increaseIndent(){
        increaseIndent(defaultIndentSpace);
    }
    
    /**
     * 
     * @param x Augment number of spaces desired for indentation.
     */
    private void increaseIndent(int x){
        if (indent.endsWith("")){
            for (int i = 0; i < x; i++){
                indent += " ";
            }
        } else {
            for (int i = 0; i < x; i++){
                    indent = indent.replaceFirst(".?$"," ");
            }
        }
    }
    private void decreaseIndent(){
        decreaseIndent(defaultIndentSpace);
    }
     /**
     * 
     * @param x Number of spaces to be removed from indentation string.
     */
    private void decreaseIndent(int x){
        for (int i = 0; i < x; i++){
            indent = indent.replaceFirst(".?$","");
        }
    }
}
