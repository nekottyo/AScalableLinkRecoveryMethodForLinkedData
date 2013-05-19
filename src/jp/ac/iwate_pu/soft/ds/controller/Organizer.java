package jp.ac.iwate_pu.soft.ds.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import jp.ac.iwate_pu.soft.ds.data.CharacterVector;

import de.uni_leipzig.simba.cache.Cache;
import de.uni_leipzig.simba.data.Instance;

public class Organizer {

	public String algorithmName;
	public int characterLength;

	public Organizer(String encryptionAlgorithmName, int charaLength) {
		algorithmName = encryptionAlgorithmName;
		characterLength = charaLength;
	}

	public HashMap<String,CharacterVector> computeCharacterVector(Cache c){
		long startTime = System.currentTimeMillis();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithmName);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		byte[] bytes;
		HashMap<String, CharacterVector> hm = new HashMap<String, CharacterVector>();
		CharacterVector cv = null;
		ArrayList<String> uris = c.getAllUris();
		Instance ins = null;
		HashMap<String, TreeSet<String>> props = null;
		Set<String> keys = null;
		Iterator<String> keyIter = null, valIter = null;
		TreeSet<String> ts = null;
		for(int i = 0; i < uris.size(); i++) {
			cv = new CharacterVector(characterLength);
			ins = c.getInstance(uris.get(i));
			props = ins.getAllProperty();
			keys = props.keySet();
			for(keyIter = keys.iterator(); keyIter.hasNext();) {
				ts = props.get(keyIter.next());
				for(valIter = ts.iterator(); valIter.hasNext();) {
					bytes = md.digest(valIter.next().toString().getBytes());
					cv.addCharacter(bytes);
				}
			}
			hm.put(uris.get(i), cv);
		}
		System.out.println("Computing Character Vectors took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
	//	System.out.println(uris.size());
		//１件当たりの時間を表示
	//	System.out.println("Computing Character Vectors Avacare took " + (((System.currentTimeMillis() - startTime) / 1000.0) / uris.size()) + "seconds.");
		return hm;
	}

	public HashMap<String, TreeSet<String>> computePairstoCompare(HashMap<String, CharacterVector> hm1, HashMap<String, CharacterVector> hm2, int threshold) {
		long startTime = System.currentTimeMillis();
		HashMap<String, TreeSet<String>> returnHM = new HashMap<String, TreeSet<String>>();

		Iterator<String> keyIter1 = null, keyIter2 = null;
		CharacterVector cv1, cv2;
		String key1, key2;
		int val = 0;
//		long averageTime = 0;
		int counter = 0;

		for(keyIter1 = hm1.keySet().iterator(); keyIter1.hasNext();) {
//			long startTime1 = System.currentTimeMillis();
			key1 = keyIter1.next();
			cv1 = hm1.get(key1);
			for(keyIter2 = hm2.keySet().iterator(); keyIter2.hasNext();) {
				key2 = keyIter2.next();
				cv2 = hm2.get(key2);
				val = cv1.discrepancy(cv2);				/*  
				 * 閾値異常かどうかを計算
				 */
				if(val <= threshold) {
					/*
					 * 同じkeyのものだったらツリーの子に追加
					 * 別のkeyのものだったら新たにツリーを作成
					 */
					if(returnHM.containsKey(key1)) {
						returnHM.get(key1).add(key2);
					}else{
						TreeSet<String> pair = new TreeSet<String>();
						pair.add(key2);
						returnHM.put(key1, pair);
					}
				}
			}
//			averageTime += ((System.currentTimeMillis() - startTime1) / 1000.0);
			counter++;
		}
		System.out.println("Computing pairs to compare took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
		System.out.println("Avarage pairs to compare took " + ((System.currentTimeMillis() - startTime) / 1000.0) / counter + " seconds.");
		//		System.out.println("Avarage time " + averageTime / counter + "seconds. ");
		return returnHM;
	}

	public void OutputHowManyAnswersCorrect(HashMap<String, TreeSet<String>> pairs, String endpoint, String graph) {
		String query = null;

		String oldRes = null, newRes = null;
		TreeSet<String> ts = null;
		Query sparqlQuery = null;
		QueryExecution qexec = null;
		int hit = 0;
		for(Iterator<String> i = pairs.keySet().iterator(); i.hasNext();) {
			oldRes = i.next();
			ts = pairs.get(oldRes);
			for(Iterator<String> iter = ts.iterator(); iter.hasNext();){
				newRes = iter.next().toString();
				query = "prefix dsnotify: <http://dsnotify.org/vocab/0.1#>\r" +
						"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r" +
						"prefix dbpedia: <http://dbpedia.org/resource/>\r" +
						"ask{\r" +
						"?x rdf:type dsnotify:MoveEvent .\r" +
						"?x dsnotify:olderResource \"" + oldRes + "\"^^<http://www.w3.org/2001/XMLSchema#string> .\r" +
						"?x dsnotify:targetResource \"" + newRes + "\"^^<http://www.w3.org/2001/XMLSchema#string>  .\r" +
						"}";
				sparqlQuery = QueryFactory.create(query);
				qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
				if(qexec.execAsk()) {
					hit++;
					System.out.print("hit!");
				}
			}
		}
		System.out.println("\nhit: " + hit);
	}

	public static void main(String[] args){
	}
}
