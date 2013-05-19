/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.uni_leipzig.simba.cache;
import de.uni_leipzig.simba.data.*;
import java.util.ArrayList;
/**
 *
 * @author ngonga
 */
public interface Cache {
    public Instance getNextInstance();
    public ArrayList<Instance> getAllInstances();
    public ArrayList<String> getAllUris();
    public void addInstance(Instance i);
    public void addTriple(String s, String p, String o);
    public boolean containsInstance(Instance i);
    public boolean containsUri(String uri);
    public Instance getInstance(String uri);
    public void resetIterator();
    public int size();
}
