
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uni_leipzig.simba.io;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import de.uni_leipzig.simba.controller.LimesLogger;

/**
 *
 * @author ngonga
 */
public class DtdChecker implements org.xml.sax.ErrorHandler {

    LimesLogger Logger = LimesLogger.getInstance();
    boolean valid = true;

    public void fatalError(SAXParseException e) throws SAXException {
        Logger.fatal("Error at " + e.getLineNumber() + " line.");
        Logger.fatal(e.getMessage());
        valid = false;
    }
    //Validation errors

    public void error(SAXParseException e) throws SAXParseException {
        Logger.warn("Error at " + e.getLineNumber() + " line.");
        Logger.warn(e.getMessage());
        valid = false;
    }

    //Show warnings
    public void warning(SAXParseException err) throws SAXParseException {
        Logger.warn(err.getMessage());
        valid = false;
    }
}

