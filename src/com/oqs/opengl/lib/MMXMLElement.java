package com.oqs.opengl.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.xml.sax.Attributes;

public class MMXMLElement implements Serializable {

	private static final long serialVersionUID = 7319405290544893137L;

	public class MMXMLAttributes extends HashMap<String,String> {private static final long serialVersionUID = -33654921235947123L;};
	public class MMXMLElements extends ArrayList<MMXMLElement> {private static final long serialVersionUID = -1547810583659667554L;};

	protected String _foundCharacters = "";
	protected String _name = null;
	protected MMXMLElement _parent = null;

	protected MMXMLAttributes _attributes = new MMXMLAttributes();
	protected MMXMLElements _elements = new MMXMLElements();

	protected String toString(int indentation) {
		String fc = getFoundCharacters();
		String tabs = ""; for (int tt=0; tt<indentation; tt++) tabs += "\t";
		String desc = String.format("%s%s%s\n",tabs,getName(),(fc != null)?String.format(" = %s",fc):"");
		Set<Entry<String,String>> entries = getAttributes().entrySet();
		for (Entry<String,String> ee:entries) {desc += String.format("%s\t%s = %s\n",tabs,ee.getKey(),ee.getValue());}
		for (MMXMLElement ee:getElements()) desc += ee.toString();
		return desc;
	}
	protected String toStringHTMLFormat() {
		Set<Entry<String,String>> entries = getAttributes().entrySet();
		String desc = "";
		if(!entries.isEmpty() || getFoundCharacters().length()>0 || hasElements()){
			 desc= String.format("<%s",getName());
			 for (Entry<String,String> ee:entries) {desc += String.format("\t%s = %s",ee.getKey(),ee.getValue());}
				desc+=">";
				desc+=getFoundCharacters();
				for (MMXMLElement ee:getElements()) desc += ee.toStringHTMLFormat();
				desc+= String.format("</%s>",getName());
		}else{
			desc = String.format("<%s/>",getName());
			for (MMXMLElement ee:getElements()) desc += ee.toStringHTMLFormat();
		}

			

		return desc;
	}

	public MMXMLElement(String name,Attributes attributes,MMXMLElement parent) {
		_name = name;
		_parent = parent;
		if (attributes != null) for (int aa=0; aa<attributes.getLength(); aa++) addAttribute(attributes.getLocalName(aa),attributes.getValue(aa));
	}

	public void addAttribute(String key,String value) {_attributes.put(key,value);}

	public void addElement(MMXMLElement element) {_elements.add(element);}
	public void addElement(String name,Attributes attributes,MMXMLElement parent) {addElement(new MMXMLElement(name,attributes,parent));}

	public void addFoundCharacters(String foundCharacters) {_foundCharacters = _foundCharacters+foundCharacters;}
	public void endFoundCharacters() {_foundCharacters.trim();}

	public MMXMLAttributes getAttributes() {return _attributes;}
	public int getAttributesCount() {return getAttributes().size();}
	public boolean hasAttributes() {return !_attributes.isEmpty();}

	public MMXMLElement getElement(int index) {return (index < getElementsCount())?_elements.get(index):null;}

	public MMXMLElement getElementForKey(String key) {
		for (MMXMLElement ee:getElements()) if ((ee != null) && (ee.getName().equals(key))) return ee;
		return null;
	}

	public MMXMLElement getElementForKeyPath(String keypath) {
		MMXMLElement element = this;
		String[] keys = keypath.split(".");
		int kk = 0; int cb = keys.length;
		while ((kk < cb) && (element != null)) {element = element.getElementForKey(keys[kk]); kk++;}
		return element;
	}

	public MMXMLElements getElementsForKey(String key) {
		MMXMLElements elements = new MMXMLElements();
		for (MMXMLElement ee:getElements()) if ((ee != null) && (ee.getName().equals(key))) elements.add(ee);
		return elements;
	}

	public int getElementsCountForKey(String key) {MMXMLElements elements = getElementsForKey(key); return (elements != null)?elements.size():0;}

	public MMXMLElements getElements() {return _elements;}
	public int getElementsCount() {return getElements().size();}
	public boolean hasElements() {return getElementsCount() != 0;}

	public String getFoundCharacters() {return _foundCharacters;}
	public String getName() {return _name;}
	public MMXMLElement getParent() {return _parent;}

	public String toString() {return toString(0);}

}
