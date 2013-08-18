package com.fave100.server.domain.favelist;


class FaveRankerWrapper {

	private FaveItem faveItem;

	public FaveRankerWrapper(final FaveItem faveItem) {
		this.faveItem = faveItem;
	}

	public FaveItem getFaveItem() {
		return faveItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((faveItem == null) ? 0 : faveItem.getSongID().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FaveRankerWrapper other = (FaveRankerWrapper)obj;
		if (faveItem == null) {
			if (other.faveItem != null)
				return false;
		}
		else if (!faveItem.getSongID().equals(other.faveItem.getSongID()))
			return false;
		return true;
	}
}