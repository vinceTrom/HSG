package com.oqs.opengl.lib;



import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oqs.opengl.lib.MMXMLElement.MMXMLElements;
import com.oqs.opengl.lib.MMXMLParser.MMXMLParserDelegate;

import java.io.IOException;
import java.io.Serializable;

public class MMXMLDocument extends Object implements Serializable,MMXMLParserDelegate {
	
	public interface MMXMLDocumentDelegate {
		public void MMXMLDocumentDidFail(MMXMLDocument document,Throwable e);
		public void MMXMLDocumentDidFinish(MMXMLDocument document);
	}
	
	private static final long serialVersionUID = -4589758180807700503L;
	
	protected MMXMLDocumentDelegate _delegate = null;
	protected MMXMLParser _parser = null;
	
	protected AsyncHttpResponseHandler _handler = new AsyncHttpResponseHandler() {
		public void onFailure(Throwable e) {ASIHTTPRequestDidFail(e);}
		public void onSuccess() {ASIHTTPRequestDidFinish(this.getResponseString());}
	};
	
	public static MMXMLDocument createMMXMLDocument(String url,MMXMLDocumentDelegate delegate) {return new MMXMLDocument(url,delegate);}
	public static MMXMLDocument createMMXMLDocument(String url,RequestParams params,MMXMLDocumentDelegate delegate) {return new MMXMLDocument(url,params,delegate);}
	public static MMXMLDocument createMMXMLDocument(Context context,String url,MMXMLDocumentDelegate delegate) {return new MMXMLDocument(context,url,delegate);}
	public static MMXMLDocument createMMXMLDocument(Context context,String url,RequestParams params,MMXMLDocumentDelegate delegate) {return new MMXMLDocument(context,url,params,delegate);}
	
	public MMXMLDocument(String url,MMXMLDocumentDelegate delegate) {initialize(delegate);MMASIHTTPManager.get(url,_handler);}
	public MMXMLDocument(String url,RequestParams params,MMXMLDocumentDelegate delegate) {initialize(delegate);MMASIHTTPManager.get(url,params,_handler);}
	public MMXMLDocument(Context context,String url,MMXMLDocumentDelegate delegate) {initialize(delegate);MMASIHTTPManager.get(context,url,_handler);}
	public MMXMLDocument(Context context,String url,RequestParams params,MMXMLDocumentDelegate delegate) {initialize(delegate);MMASIHTTPManager.get(context,url,params,_handler);}
	
	protected void initialize(MMXMLDocumentDelegate delegate) {_delegate = delegate;}
	
	protected void ASIHTTPRequestDidFail(Throwable e) {if (_delegate != null) _delegate.MMXMLDocumentDidFail(this,e);}
	protected void ASIHTTPRequestDidFinish(String response) {_parser = MMXMLParser.createMMXMLParser(response,this);_parser.parse();}
	
	public void MMXMLParserDidFail(MMXMLParser parser,Exception e) {if (_delegate != null) _delegate.MMXMLDocumentDidFail(this,e);}
	public void MMXMLParserDidFinish(MMXMLParser parser) {if (_delegate != null) _delegate.MMXMLDocumentDidFinish(this);}
	
	public MMXMLElement getRootElement() {return (_parser != null)?_parser.getRootElement():null;}
	public MMXMLElement getElementForKey(String key) {MMXMLElement root = getRootElement(); return (root != null)?root.getElementForKey(key):null;}
	public MMXMLElement getElementForKeyPath(String keypath) {MMXMLElement root = getRootElement(); return (root != null)?root.getElementForKeyPath(keypath):null;}
	public MMXMLElements getElementsForKey(String key) {MMXMLElement root = getRootElement(); return (root != null)?root.getElementsForKey(key):null;}
	public int getElementsCountForKey(String key) {MMXMLElement root = getRootElement(); return (root != null)?root.getElementsCountForKey(key):0;}
	public String toString() {MMXMLElement root = getRootElement(); return (root != null)?root.toString():null;}
	public String toStringHTMLFormat() {MMXMLElement root = getRootElement(); return (root != null)?root.toStringHTMLFormat():null;}
	public String getResponseString(){return _handler.getResponseString();}
	public byte[] getResponseData(){return _handler.getResponseData();}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException,ClassNotFoundException {_parser = (MMXMLParser) in.readObject();}
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {out.writeObject(_parser);}
}
