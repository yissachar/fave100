package com.fave100;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FileBuilder {
	
	public final static int INDENT_SIZE = 4;
	
	private StringBuilder sb = new StringBuilder();
	private int indentDepth = 0;
	
	public void append(String str) {
		sb.append(str);
	}
	
	public void append(Object obj) {
		sb.append(obj);
	}
	
	public void indent() {
		indentDepth++;			
		applyIndent();
	}
	
	public void outdent() {
		indentDepth--;
	}
	
	public void applyIndent() {
		for(int i = 0; i < indentDepth * INDENT_SIZE; i++) {
			sb.append(" ");
		}
	}
	
	public void save(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer;
		writer = new PrintWriter(new File(fileName), "UTF-8");
    	writer.print(sb.toString());
    	writer.close();
	}

}
