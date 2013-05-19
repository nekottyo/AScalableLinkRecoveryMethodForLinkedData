/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.uni_leipzig.simba.io;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.data.Restriction;



/**
 * Contains the infos necessary to access a knowledge base
 * @author ngonga
 */
public class KBInfo implements Serializable
{    	  
	private static final long serialVersionUID = 1l;
	public String id;
	public String endpoint;
	public String graph;
	public String var;
	// properties contain the list of properties whose values will be used during
	// the mapping process
	public List<String> properties;
	// restrictions specify the type of instances to be taken into consideration
	// while mapping
	public List<Restriction> restrictions;
	public Map<String, String> prefixes;
	public int pageSize;
	/**
	 * Constructor
	 */
	KBInfo()
	{
		id=null;
		endpoint=null;
		graph=null;
		restrictions = new ArrayList<Restriction>();
		properties = new ArrayList<String>();
		prefixes = new HashMap<String, String>();
		//-1 means query all at once
		pageSize=-1;
	}

	public static String expandPrefix(String url, Map<String,String> prefixes)
	{
		if(url.startsWith("http")||url.startsWith("<")) return url;	
		for(String prefix: prefixes.keySet())
		{
			if(url.startsWith(prefix+':'))
			{
				return '<'+url.replace(prefix+':', prefixes.get(prefix))+'>';
			}							
		}
		return url;
	}

	public void expandPrefixes(Map<String,String> prefixes)
	{
		List<String> newProperties = new ArrayList<String>();
		for(String property: properties)
		{							
			newProperties.add(expandPrefix(property,prefixes));
		}
		this.properties = newProperties;
		List<Restriction> newRestrictions = new ArrayList<Restriction>();
		for(Restriction restriction: restrictions)
		{
			String newProperty = expandPrefix(restriction.getProperty(),prefixes);
			String newObject = expandPrefix(restriction.getObject(),prefixes);
			newRestrictions.add(new Restriction(newProperty, newObject));
		}
		this.restrictions = newRestrictions;
	}

		public KBInfo(String id, String endpoint, String graph, String var,
				List<String> properties, List<Restriction> restrictions,
				Map<String, String> prefixes, int pageSize) {
			super();
			this.id = id;
			this.endpoint = endpoint;
			this.graph = graph;
			this.var = var;
			this.properties = properties;
			this.restrictions = restrictions;
			this.prefixes = prefixes;
			this.pageSize = pageSize;
		}

		/**
		 * 
		 * @return String representation of knowledge base info
		 */
		@Override
		public String toString()
		{
			String s = "ID: "+id+"\n";
			s = s+"Prefixes: "+prefixes+"\n";
			s = s+"Endpoint: "+endpoint+"\n";
			s = s+"Graph: "+graph+"\n";
			s = s+"Restrictions: "+restrictions+"\n";
			s = s+"Properties: "+properties+"\n";
			s = s+"Page size: "+pageSize+"\n";
			return s;
		}

		public String getRestrictionQuerySubstring()
		{				
			StringBuffer where = new StringBuffer();		
			for (int i = 0; i < this.restrictions.size(); i++)
			{	
				where.append(this.restrictions.get(i)+".\n");
			}
			return where.toString().substring(0, where.length()-1);
		}


		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
			+ ((endpoint == null) ? 0 : endpoint.hashCode());
			result = prime * result + ((graph == null) ? 0 : graph.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + pageSize;
			result = prime * result
			+ ((prefixes == null) ? 0 : prefixes.hashCode());
			result = prime * result
			+ ((properties == null) ? 0 : properties.hashCode());
			result = prime * result
			+ ((restrictions == null) ? 0 : restrictions.hashCode());
			result = prime * result + ((var == null) ? 0 : var.hashCode());
			return result;
		}


		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			KBInfo other = (KBInfo) obj;
			if (endpoint == null) {
				if (other.endpoint != null)
					return false;
			} else if (!endpoint.equals(other.endpoint))
				return false;
			if (graph == null) {
				if (other.graph != null)
					return false;
			} else if (!graph.equals(other.graph))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (pageSize != other.pageSize)
				return false;
			if (prefixes == null) {
				if (other.prefixes != null)
					return false;
			} else if (!prefixes.equals(other.prefixes))
				return false;
			if (properties == null) {
				if (other.properties != null)
					return false;
			} else if (!properties.equals(other.properties))
				return false;
			if (restrictions == null) {
				if (other.restrictions != null)
					return false;
			} else if (!restrictions.equals(other.restrictions))
				return false;
			if (var == null) {
				if (other.var != null)
					return false;
			} else if (!var.equals(other.var))
				return false;
			return true;
		}


	}
