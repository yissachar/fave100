package com.fave100.shared.domain;

import java.util.List;

import com.gwtplatform.dispatch.shared.Result;

@SuppressWarnings("serial")
public class FaveItemCollection implements Result {
	List<FaveItemDto> items;

    protected FaveItemCollection() {
    }

    public FaveItemCollection(List<FaveItemDto> items) {
        this.items = items;
    }

    public List<FaveItemDto> getItems() {
        return items;
    }
}

