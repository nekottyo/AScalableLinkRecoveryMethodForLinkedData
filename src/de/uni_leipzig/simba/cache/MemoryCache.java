/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.uni_leipzig.simba.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.uni_leipzig.simba.data.Instance;
/**
 *
 * @author ngonga
 */
public class MemoryCache implements Cache, Serializable
{
	private static final long serialVersionUID = 1L;

	// maps uris to instance. A bit redundant as instance contain their URI
    protected HashMap<String, Instance> instanceMap;
    
    //Iterator for getting next instance
    Iterator<Instance> instanceIterator;
    
    

    public MemoryCache()
    {    	
        instanceMap = new HashMap<String, Instance>();
    }

    /**
     * Returns the next instance in the list of instances
     * @return null if no next instance, else the next instance
     */
    public Instance getNextInstance() {
        if(instanceIterator.hasNext())
            return instanceIterator.next();
        else return null;
    }

    /**
     * Returns all the instance contained in the cache
     * @return ArrayList containing all instances
     */
    public ArrayList<Instance> getAllInstances() {
        return new ArrayList<Instance>(instanceMap.values());
    }

    public void addInstance(Instance i) {
        if(instanceMap.containsKey(i.getUri()))
        {
            Instance m = instanceMap.get(i.getUri());

        }
        else
            instanceMap.put(i.getUri(), i);
    }
    
    /**
     * 
     * @param uri URI to look for
     * @return The instance with the URI uri if it is in the cache, else null
     */
    public Instance getInstance(String uri) {
        if(instanceMap.containsKey(uri))
            return instanceMap.get(uri);
        else return null;
    }

    /**
     *
     * @return The total number of subjects in all triples in the cache.
     */
    public int size() {
    	// does not really calculate the size but only the number of subjects
    	// possible fix: loop over the instanceMap values and add the sizes of those values values. However that would be more slow. 
        return instanceMap.size();
    }

// needs Instance.properties to be public or there to be a size method
//    public int realSize() {
//    	int count = 0;
//    	for(Instance instance: instanceMap.values())
//    	{
//    		count += instance.properties.values().size();
//    	}
//        return count;
//    }

    /**
     * Adds a new spo statement to the cache
     * @param s The URI of the instance linked to o via p
     * @param p The property which links s and o
     * @param o The value of the property of p for the entity s
     */
    public void addTriple(String s, String p, String o) {
        if(instanceMap.containsKey(s))
        {
            Instance m = instanceMap.get(s);
            m.addProperty(p, o);
        }
        else
        {
            Instance m = new Instance(s);
            m.addProperty(p, o);
            instanceMap.put(s, m);
        }
    }

    /**
     *
     * @param i The instance to look for
     * @return true if the URI of the instance is found in the cache
     */
    public boolean containsInstance(Instance i) {
        return instanceMap.containsKey(i.getUri());
    }

    /**
     *
     * @param uri The URI to looks for
     * @return True if an instance with the URI uri is found in the cache, else false
     */
    public boolean containsUri(String uri) {
        return instanceMap.containsKey(uri);
    }

    public void resetIterator()
    {
        instanceIterator = instanceMap.values().iterator();
    }

    @Override
    public String toString()
    {
        return instanceMap.toString();
    }

    public ArrayList<String> getAllUris() {
        return new ArrayList<String>(instanceMap.keySet());
    }
}
