package se.unlogic.standardutils.xsl;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;


public class TemplateCache {
	
	private static final ReferenceQueue<Templates> REFERENCE_QUEUE = new ReferenceQueue<Templates>();
	
	private static final ConcurrentHashMap<TemplateDescriptor, ValueReference> CACHE_MAP = new ConcurrentHashMap<TemplateDescriptor, ValueReference>();
	
	public static Templates getTemplates(TemplateDescriptor descriptor) throws TransformerConfigurationException{
		
		processReferenceQueue();
		
		ValueReference templateReference = CACHE_MAP.get(descriptor);
		
		Templates templates;
		
		if(templateReference != null){
		
			templates = templateReference.get();
			
			if(templates != null){
				
				return templates;
			}			
		}
		
		TransformerFactory transFact = TransformerFactory.newInstance();

		if(descriptor.getUriResolver() != null){
			transFact.setURIResolver(descriptor.getUriResolver());
		}
		
		templates = transFact.newTemplates(new StreamSource(descriptor.getUri().toString()));
		
		if(templates == null){
			
			throw new TransformerConfigurationException("Unable to cache template " + descriptor.getUri().toString());
		}
		
		CACHE_MAP.put(descriptor, new ValueReference(descriptor ,templates, REFERENCE_QUEUE));
		
		return templates;
	}

	private static void processReferenceQueue() {

		Reference<? extends Templates> reference;
		
		while((reference = REFERENCE_QUEUE.poll()) != null){
			
			TemplateDescriptor descriptor = ((ValueReference)reference).getDescriptor();
			
			CACHE_MAP.remove(descriptor);
		}
	}
	
	private static final class ValueReference extends WeakReference<Templates>{

		private final TemplateDescriptor descriptor;
		
		public ValueReference(TemplateDescriptor descriptor, Templates templates, ReferenceQueue<? super Templates> queue) {

			super(templates, queue);

			this.descriptor = descriptor;
		}
		
		public TemplateDescriptor getDescriptor() {
	
			return descriptor;
		}
	}
}
