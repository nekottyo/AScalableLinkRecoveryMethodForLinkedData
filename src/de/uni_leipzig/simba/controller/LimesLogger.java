/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uni_leipzig.simba.controller;

//import org.apache.log4j.*;

/**
 * Implements a logger for the LIMES project
 * @author ngonga
 */
public class LimesLogger
{	
	  static class Logger
	    {
	    	   public void warn(Object o)
	    	    {
	    	        //logger.warn(o);
	    	    }

	    	    public void fatal(Object o)
	    	    {
	    	        System.out.println(o);
	    	    }

	    	    public void debug(Object o)
	    	    {
	    	        //System.out.println(o);
	    	    }

	    	    public void info(Object o)
	    	    {
	    	        //System.out.println(o);
	    	    }
	    }
	  
    private static LimesLogger ll = null;
    private static final Logger logger =  new Logger();//Logger.getRootLogger();

    private LimesLogger(String logFile) {
        //PatternLayout layout = new PatternLayout("%d{dd.MM.yyyy HH:mm:ss,SSS} %-5p [%t] %c: %m%nFilename: %F  Linenumber: %L  Methodname: %M%n%n");
//        PatternLayout layout = new PatternLayout("%d{dd.MM.yyyy HH:mm:ss} %-5p [%t] %c: %m%n");
//        try {
//            FileAppender fileAppender = new FileAppender(layout, logFile, false);
//            logger.addAppender(fileAppender);
//        } catch (Exception e) {
//            logger.warn("Exception creating file appender.");
//        }
//        logger.setLevel(Level.DEBUG);
    }
    

    /** Logger is a singleton     
     */
    public static LimesLogger getInstance(String file)
    {
        if(ll==null)
            ll = new LimesLogger(file);
        return ll;
    }

    public static LimesLogger getInstance()
    {
        if(ll==null)
            ll = new LimesLogger("limes.log");
        return ll;
    }
    
    public void warn(Object o)
    {
        //logger.warn(o);
    }

    public void fatal(Object o)
    {
        logger.fatal(o);
    }

    public void debug(Object o)
    {
        logger.debug(o);
    }

    public void info(Object o)
    {
        logger.info(o);
    }
}
