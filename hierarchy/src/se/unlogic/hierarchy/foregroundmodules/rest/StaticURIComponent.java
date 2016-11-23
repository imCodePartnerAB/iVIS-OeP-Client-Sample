package se.unlogic.hierarchy.foregroundmodules.rest;


public class StaticURIComponent implements URIComponent {

	private final String name;

	public StaticURIComponent(String name) {

		super();
		this.name = name;
	}

	@Override
	public boolean matches(String value, Object[] paramArray) {

		return name.equals(value);
	}
}
