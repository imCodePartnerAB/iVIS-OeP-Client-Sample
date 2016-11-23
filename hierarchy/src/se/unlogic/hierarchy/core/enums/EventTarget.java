package se.unlogic.hierarchy.core.enums;

public enum EventTarget {

	LOCAL(true, false),
	REMOTE(false, true),
	ALL(true, true);

	private final boolean local;
	private final boolean remote;

	private EventTarget(boolean local, boolean remote) {

		this.local = local;
		this.remote = remote;
	}

	public boolean isLocal() {

		return local;
	}

	public boolean isRemote() {

		return remote;
	}
}
