package com.gentics.mesh.core.data.search;

public enum SearchQueueEntryAction {

	CREATE_ACTION("create"), DELETE_ACTION("delete"), UPDATE_ACTION("update");

	private String name;

	private SearchQueueEntryAction(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static SearchQueueEntryAction valueOfName(String actionName) {
		for (SearchQueueEntryAction action : SearchQueueEntryAction.values()) {
			if (actionName.equals(action.getName())) {
				return action;
			}
		}
		return null;
	}

}
