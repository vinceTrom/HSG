package com.oqs.opengl.lib;

import android.os.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;

public class MMXMLParser extends DefaultHandler implements Serializable {

	public interface MMXMLParserDelegate {
		public void MMXMLParserDidFail(MMXMLParser parser,Exception e);
		public void MMXMLParserDidFinish(MMXMLParser parser);
	}
	
	public class ParsingTask extends AsyncTask<Void, Void, Exception>{

		@Override
		protected Exception doInBackground(Void... params) {
			try {
				XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
				reader.setContentHandler(MMXMLParser.this);
				InputSource is = new InputSource(_is);
				//is.setEncoding("UTF-8");
				reader.parse(is); 
				return null;
			} 
			catch (IOException e) {//MMLog.e("", e.toString());
			return e;}
			catch (SAXException e) {//MMLog.e("", e.toString());
			return e;}
			catch (ParserConfigurationException e) {//MMLog.e("", e.toString());
			return e;}
			
		}

		protected void onPostExecute(Exception result) {
			if(result!=null){
				if (_delegate != null)
					_delegate.MMXMLParserDidFail(MMXMLParser.this,result);
			}else{
				if (_delegate != null)
					_delegate.MMXMLParserDidFinish(MMXMLParser.this);
			}
		}

	}

	private static final long serialVersionUID = 3376869417941979924L;

	protected boolean _betweenTags = false;
	protected MMXMLElement _current = null;
	protected MMXMLParserDelegate _delegate = null;
	protected InputStream _is;
	protected MMXMLElement _root = null;

	public static MMXMLParser createMMXMLParser(String str,MMXMLParserDelegate delegate) {
		return MMXMLParser.createMMXMLParser(new ByteArrayInputStream(str.replaceAll("&bo", "&#38;bo").getBytes()),delegate);
	}

	public static MMXMLParser createMMXMLParser(InputStream is,MMXMLParserDelegate delegate) { 
		MMXMLParser parser = new MMXMLParser(is,delegate);
		return parser;
	}

	private MMXMLParser(InputStream is,MMXMLParserDelegate delegate) {
		_delegate = delegate;
		_is = is;
	}

	public void parse(){
		Void[] v = {};
		new ParsingTask().execute(v);
	}
	
	public MMXMLParser parseSynchronously(){
		try {
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setContentHandler(MMXMLParser.this);
			InputSource is = new InputSource(_is);
			//is.setEncoding("UTF-8");
			reader.parse(is); 
			return this;
		}catch(Exception e){return null;}
		
	}

	public void startDocument() throws SAXException {_betweenTags = false; _root = new MMXMLElement("Document",null,null);_current = _root;}
	public void endDocument() throws SAXException {_betweenTags = false; _current = null;}

	public void startElement(String namespaceURI,String localName,String qName,Attributes atts) throws SAXException {
		_betweenTags = true;
		MMXMLElement element = new MMXMLElement(localName,atts,_current);
		_current.addElement(element);
		_current = element;
	}

	public void endElement(String namespaceURI,String localName,String qName) throws SAXException {
		_current.endFoundCharacters();
		_current = _current.getParent(); 
		_betweenTags = false;
	}

	public void characters(char[] ch,int start,int length) throws SAXException {
		if (_betweenTags) _current.addFoundCharacters(new String(ch,start,length));
		}
/*
	public void error(SAXParseException e) throws SAXException {Log.d("", "MERDE1");}//if (_delegate != null) parserFailed(e);}
	public void fatalError(SAXParseException e) throws SAXException {Log.d("", "MERDE2");}//if (_delegate != null) parserFailed(e);}
	public void warning(SAXParseException e) throws SAXException {Log.d("", "MERDE3");}//if (_delegate != null) parserFailed(e);}
*/
	public MMXMLElement getRootElement() {return _root;}
	public MMXMLElement getElementForKey(String key) {MMXMLElement root = getRootElement(); return (root != null)?root.getElementForKey(key):null;}
	public MMXMLElement getElementForKeyPath(String keypath) {MMXMLElement root = getRootElement(); return (root != null)?root.getElementForKeyPath(keypath):null;}
	public MMXMLElements getElementsForKey(String key) {MMXMLElement root = getRootElement(); return (root != null)?root.getElementsForKey(key):null;}
	public int getElementsCountForKey(String key) {MMXMLElement root = getRootElement(); return (root != null)?root.getElementsCountForKey(key):0;}
	public String toString() {MMXMLElement root = getRootElement(); return (root != null)?root.toString():null;}

	private void readObject(java.io.ObjectInputStream in) throws IOException,ClassNotFoundException {_root = (MMXMLElement) in.readObject();}
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {out.writeObject(_root);}
}
