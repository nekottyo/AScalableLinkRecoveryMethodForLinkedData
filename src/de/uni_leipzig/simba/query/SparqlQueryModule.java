/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uni_leipzig.simba.query;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.uni_leipzig.simba.cache.Cache;
import de.uni_leipzig.simba.controller.LimesLogger;
import de.uni_leipzig.simba.data.Restriction;
import de.uni_leipzig.simba.io.KBInfo;

/**
 *
 * @author ngonga
 */
public class SparqlQueryModule implements QueryModule {

	protected KBInfo kb;

	public SparqlQueryModule(KBInfo kbinfo) {
		kb = kbinfo;
	}

	/** Reads from a SPARQL endpoint and writes the results in a cache
	 *
	 * @param cache The cache in which the content on the SPARQL endpoint is
	 * to be written
	 * @throws Exception 
	 */
	public void fillCache(Cache cache) throws Exception {
		LimesLogger logger = LimesLogger.getInstance();
		long startTime = System.currentTimeMillis();
		//write prefixes		
		String query = "";
		for (String key : kb.prefixes.keySet())
		{
			query = query + "PREFIX " + key + ": <" + kb.prefixes.get(key) + ">\n";
		}

		// fill in variable for the different properties to be retrieved
		query = query + "SELECT DISTINCT " + kb.var;
		for (int i = 0; i < kb.properties.size(); i++) {
			query = query + " ?v" + i;
		}
		if(kb.properties.size() == 0) {
			query = query + " ?prop ?v0";
		}
		query = query + "\n";

		// graph
		/*
        if (!kb.graph.equals(" ")) {
        query = query + "FROM <" + kb.graph + ">\n";
        } */

		//restriction
		if ((kb.restrictions.size()>0)||(kb.properties.size()>0))
		{
			query = query + "WHERE {\n";
			if (kb.restrictions.size()>  0)
			{
				String where;				
				for (Restriction restriction: kb.restrictions)
				{
					where = restriction.toString(kb.var);
					query = query + where + " .\n";
				}
			}
			//properties
			if (kb.properties.size()>  0)
			{
				query = query + "OPTIONAL {\n";
				int i=0;
				for (String property : kb.properties)
				{
					query = query + kb.var + " " + property + " ?v" + i + " .\n";
					i++;
				}
				//close optional
				query = query + "}\n";
			} else {
				query = query + "OPTIONAL {\n";
				query = query + kb.var + " ?prop ?v0 .\n";
				query = query + "}\n";
			}
			// close where
			query = query + "}\n";
		} 

		//query = query + " LIMIT 1000";

		logger.info("Querying the endpoint.");
		//run query

		int offset = 0;
		boolean moreResults = false;
		int counter = 0;
		String basicQuery = query;
		do {

			if (kb.pageSize > 0) {
				query = basicQuery + " LIMIT " + kb.pageSize + " OFFSET " + offset;
			}
			//logger.info("Following query was sent to endpoint <" + kb.endpoint + ">\n\n" + query);
			Query sparqlQuery = QueryFactory.create(query);
			QueryExecution qexec;
			// take care of graph issues. Only takes one graph. Seems like some sparql endpoint do
			// not like the FROM option.
			// it is important to
			if (kb.graph != null) {
				qexec = QueryExecutionFactory.sparqlService(kb.endpoint, sparqlQuery, kb.graph);
			} //
			else {
				qexec = QueryExecutionFactory.sparqlService(kb.endpoint, sparqlQuery);
			}
			ResultSet results = qexec.execSelect();


			//write
			String uri, property, value;
			try {
				if (results.hasNext()) {
					moreResults = true;
				} else {
					moreResults = false;
				}
				while (results.hasNext()) {

					QuerySolution soln = results.nextSolution();
					// process query here
					{
						try {
							//first get uri
							uri = soln.get(kb.var.substring(1)).toString();

							//now get (p,o) pairs for this s
							for (int i = 0; i < kb.properties.size(); i++) {
								property = kb.properties.get(i);
								if (soln.contains("v"+i)) {
									value = soln.get("v" + i).toString();
									//remove localization information, e.g. @en
									if (value.contains("@")) {
										value = value.substring(0, value.indexOf("@"));
									}
									cache.addTriple(uri, property, value);
									//logger.info("Adding (" + uri + ", " + property + ", " + value + ")");
								}
								//else logger.warn(soln.toString()+" does not contain "+property);
							}
							//else
							//    cache.addTriple(uri, property, "");
							if(kb.properties.size() == 0) {
								if(soln.contains("v0") && soln.contains("prop")) {
									property = soln.get("prop").toString();
									value = soln.get("v0").toString();
									if (value.contains("@")) {
										value = value.substring(0, value.indexOf("@"));
									}
									cache.addTriple(uri, property, value);
								}
							}
						}
						catch (Exception e) {
							//logger.warn("Error while processing: " + soln.toString());
							//logger.warn("Following exception occured: " + e.getMessage());
							//logger.info("Processing further ...");
						}

					}
					counter++;

					//logger.info(soln.get("v0").toString());       // Get a result variable by name.
				}

			} catch (Exception e) {
				//logger.warn("Exception while handling query");
				//logger.warn(e.toString());
			} finally {
				qexec.close();
			}
			offset = offset + kb.pageSize;
		} while (moreResults && kb.pageSize > 0);
		logger.info("Retrieved " + counter + " instances.");
		logger.info(cache.size() + " of these instances contained valid data.");
		logger.info("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
		System.out.println("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
	}
}
