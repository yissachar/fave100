package com.fave100.server.domain;

/**
 * An empty result object to satisfy GWTP Rest Result
 * 
 * @author yissachar.radcliffe
 * 
 */
public class VoidResult {

	/* Must define at least one getter, otherwise Google Cloud Endpoints will refuse to serialize the object */
	public boolean isVoidResult() {
		return true;
	}

}
