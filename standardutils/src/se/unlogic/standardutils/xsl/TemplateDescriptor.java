package se.unlogic.standardutils.xsl;

import java.net.URI;

import javax.xml.transform.URIResolver;

public class TemplateDescriptor {

	private final URI uri;
	private final URIResolver uriResolver;

	public TemplateDescriptor(URI uri, URIResolver uriResolver) {

		super();

		if(uri == null) {

			throw new NullPointerException("uri cannot be null");
		}

		this.uri = uri;
		this.uriResolver = uriResolver;
	}

	public URI getUri() {

		return uri;
	}

	public URIResolver getUriResolver() {

		return uriResolver;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((uriResolver == null) ? 0 : uriResolver.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		TemplateDescriptor other = (TemplateDescriptor) obj;
		if(uri == null) {
			if(other.uri != null) {
				return false;
			}
		} else if(!uri.equals(other.uri)) {
			return false;
		}
		if(uriResolver == null) {
			if(other.uriResolver != null) {
				return false;
			}
		} else if(!uriResolver.equals(other.uriResolver)) {
			return false;
		}
		return true;
	}
}
