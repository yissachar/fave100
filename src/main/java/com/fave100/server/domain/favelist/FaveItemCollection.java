package com.fave100.server.domain.favelist;

import java.util.List;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "FaveItemCollection")
public class FaveItemCollection {

	@ApiModelProperty(required = true, value = "A list of FaveItems") private List<FaveItem> items;

	public FaveItemCollection(List<FaveItem> items) {
		this.items = items;
	}

	public List<FaveItem> getItems() {
		return items;
	}

	public void setItems(List<FaveItem> items) {
		this.items = items;
	}

}
