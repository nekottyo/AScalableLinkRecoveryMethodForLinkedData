/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uni_leipzig.simba.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/** @author ngonga
 */
public class Instance implements Comparable<Instance>, Serializable
{
	private static final long serialVersionUID = -2938424062250337829L;
	private String uri;
	public HashMap<String, TreeSet<String>> properties;
	public float distance;

	// default constructor for serialisation
	private Instance()
	{
		properties = new HashMap<String, TreeSet<String>>();
		// distance to exemplar
		distance = -1;
	}
	/** Constructor
	 *
	 * @param _uri URI of the instance. This is the key to accessing it.
	 */
	public Instance(String _uri)
	{    	
		this();
		uri = _uri;
	}

	/**
	 * Add a new (property, value) pair
	 * @param propUri URI of the property
	 * @param value value of the property for this instance
	 */
	public void addProperty(String propUri, String value) {
		if (properties.containsKey(propUri)) {
			properties.get(propUri).add(value);
		} else {
			TreeSet<String> values = new TreeSet<String>();
			values.add(value);
			properties.put(propUri, values);
		}
	}

	public void addProperty(String propUri, TreeSet<String> values) {
		if (properties.containsKey(propUri)) {
			Iterator<String> iter = values.iterator();
			while (iter.hasNext()) {
				properties.get(propUri).add(iter.next());
			}
		} else {
			properties.put(propUri, values);
		}
	}

	/**
	 * Returns the URI of this instance
	 * @return URI of this instance
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Return all the values for a given property
	 * @param propUri
	 * @return TreeSet of values associated with this URI
	 */
	public TreeSet<String> getProperty(String propUri) {
		if (properties.containsKey(propUri)) {
			return properties.get(propUri);
		} else {
			//        	LimesLogger logger = LimesLogger.getInstance();
			//            logger.warn("Failed to access property "+propUri+" on "+uri);
			//            logger.warn("possible properties are "+properties.keySet());
			return new TreeSet<String>();
		}
	}
	
	public HashMap<String, TreeSet<String>> getAllProperty() {
		return properties;
	}

	@Override
	public String toString() {
		String s = uri;
		String propUri;
		Iterator<String> iter = properties.keySet().iterator();
		while (iter.hasNext()) {
			propUri = iter.next();
			s = s + "; " + propUri + " -> " + properties.get(propUri);
		}
		return s +"; distance = "+distance;
	}

	/** Comparison with other Instances
	 *
	 * @param instance Instance for comparison
	 * @return 1 if the distance from the exemplar to the current instance is smaller
	 * than the distance from the exemplar to o. 
	 */
	public int compareTo(Instance instance)
	{
		if(!instance.getClass().equals(Instance.class)) return -1;
		float diff = distance - instance.distance;
		if (diff < 0) {
			return 1;
		} else if (diff > 0) {
			return -1;
		} else {
			return instance.uri.compareTo(uri);
		}
	}
}
