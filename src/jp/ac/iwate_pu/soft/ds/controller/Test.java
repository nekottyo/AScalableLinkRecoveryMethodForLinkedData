/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.iwate_pu.soft.ds.controller;

import de.uni_leipzig.simba.cache.*;
import de.uni_leipzig.simba.controller.LimesLogger;
import de.uni_leipzig.simba.query.*;
import de.uni_leipzig.simba.io.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import jp.ac.iwate_pu.soft.ds.data.CharacterVector;

public class Test {

	public static void main(String args[]) throws Exception {

		long startTime = System.currentTimeMillis();
		LimesLogger logger = LimesLogger.getInstance();
		// get config
		ConfigReader cr = new ConfigReader();
		String root = "C:/Users/taro/Documents/4th.1stHalf/pleiades-e4.2-java-jre_20120812/workspace/AScalableLinkRecoveryMethodForLinkedData/";
		cr.validateAndRead(root + "test.xml");

		//fill cache using the query module
		// first source
		SparqlQueryModule qm = new SparqlQueryModule(cr.getSourceInfo());
		MemoryCache source = new MemoryCache();
		qm.fillCache(source);

		//then target
		SparqlQueryModule targetQm = new SparqlQueryModule(cr.getTargetInfo());
		MemoryCache target = new MemoryCache();
		targetQm.fillCache(target);


		Organizer organizer = new Organizer(cr.algorithm, cr.characterLength);
		HashMap<String, CharacterVector> sourceHM = organizer.computeCharacterVector(source);
		HashMap<String, CharacterVector> targetHM = organizer.computeCharacterVector(target);

		System.out.println(sourceHM.size() + " * " + targetHM.size() + " = " + sourceHM.size() * targetHM.size());

		HashMap<String, TreeSet<String>> pairs = organizer.computePairstoCompare(sourceHM, targetHM, cr.blockingThreshold);

		organizer.OutputHowManyAnswersCorrect(pairs, "http://localhost:8890/sparql", "http://localhost:8890/DAV/eventset");
		
		//write
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(root + cr.blockedFile)));
		String str = null;
		for(Iterator<String> i = pairs.keySet().iterator();i.hasNext();) {
			str = i.next();
			pw.println(str + ", " + pairs.get(str));
		}
		pw.close();

		/*
        //set metrics
        SimpleMetricFactory mf = new SimpleMetricFactory();
        mf.setExpression(cr.metricExpression);

        // this does the job but is still buggy
        SimpleMetricFactory organizerMf = new SimpleMetricFactory();
        String var1 = cr.source.var.replaceAll("\\?", "");
        String var2 = cr.target.var.replaceAll("\\?", "");
        organizerMf.setExpression(mf.foldExpression(cr.metricExpression, var1, var2));

        //Sample
        LimesOrganizer organizer = new LimesOrganizer();
        organizer.computeExemplars(source, organizerMf);

        ArrayList<String> uris = target.getAllUris();

        //get Writer ready
        Serializer accepted = new SimpleN3Serializer();
        Serializer toReview = new SimpleN3Serializer();

        accepted.open(cr.acceptanceFile);
        accepted.printPrefixes(cr.prefixes);
        toReview.open(cr.verificationFile);
        toReview.printPrefixes(cr.prefixes);

        //now write results
        HashMap<String, Float> results;
        Iterator<String> resultIterator;
        String s;
        for (int i = 0; i < uris.size(); i++) {
            results = organizer.getSimilarInstances(target.getInstance(uris.get(i)),cr.verificationThreshold, mf);
            resultIterator = results.keySet().iterator();
            while(resultIterator.hasNext())
            {
                s = resultIterator.next();
                if(results.get(s) > cr.acceptanceThreshold)
                    accepted.printStatement(uris.get(i), cr.acceptanceRelation, s);
                else if(results.get(s) > cr.verificationThreshold)
                    toReview.printStatement(uris.get(i), cr.acceptanceRelation, s);
            }
        }

        //close writers
        accepted.close();
        toReview.close();
		 */

		//logger.info("Required " + organizer.getComparisons() + " comparisons overall.\n");
		//logger.info("Comparisons were carried out in " + organizer.getComparisonTime() + " seconds overall.\n");
		logger.info("Required " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds overall.");
		System.out.println("Algorithm: " + cr.algorithm + " CharactorLength: "+ cr.characterLength + " Theta: " + cr.blockingThreshold);
	}
}
