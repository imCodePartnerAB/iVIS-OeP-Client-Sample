package se.unlogic.hierarchy.core.enums;


public enum EventSource {

	LOCAL(true, false),
	REMOTE(false, true);

	private final boolean local;
	private final boolean remote;

	private EventSource(boolean local, boolean remote) {

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
