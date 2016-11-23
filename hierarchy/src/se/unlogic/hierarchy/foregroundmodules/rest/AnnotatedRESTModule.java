package se.unlogic.hierarchy.foregroundmodules.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.BeanStringPopulatorRegistery;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;



public class AnnotatedRESTModule extends AnnotatedForegroundModule {

	private final List<ResponseHandler<?>> responseHandlers = new ArrayList<ResponseHandler<?>>();
	private final HashMap<String, List<RESTMapping>> restMethodMap = new HashMap<String, List<RESTMapping>>();
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {


		Method[] methods = this.getClass().getMethods();

		methodLoop: for (Method method : methods) {
			
			RESTMethod annotation = method.getAnnotation(RESTMethod.class);
			
			if(annotation == null){
				
				continue;
			}
			
			Class<?>[] parameters = method.getParameterTypes();
			
			if(parameters.length < 4 || !checkInitialParams(parameters)){
				
				log.error("Method " + method.getName() + " has invalid parametertypes, skipping method.");
				
				continue;
			}
			
			HashMap<String, ParamMapping> paramMap = null;
			
			//Parse parameters
			if(parameters.length > 4){
				
				paramMap = new HashMap<String, ParamMapping>();
				
				Annotation[][] paramAnnotations = method.getParameterAnnotations();
				
				int paramIndex = 4;
				
				while(paramIndex < parameters.length){
					
					URIParam uriParam = getURIParamAnnotation(paramIndex, paramAnnotations);
					
					if(uriParam == null){
						
						log.error("The " + (paramIndex + 1) + "th parameter of method " + method.getName() + " is missing the @" + URIParam.class.getSimpleName() + " annotation, skipping method.");
						
						continue methodLoop;
					
					}
					
					if(paramMap.containsKey(uriParam.name())){
						
						log.error("Method " + method.getName() + " has multiple @" + URIParam.class.getSimpleName() + " annotated parameters using the same name (" + uriParam.name() + "), skipping method.");
						
						continue methodLoop;
					}
					
					BeanStringPopulator<?> populator = BeanStringPopulatorRegistery.getBeanStringPopulator(parameters[paramIndex]);
					
					if(populator == null){
						
						log.error("The " + (paramIndex + 1) + "th parameter of method " + method.getName() + " is of the unsupported type " + parameters[paramIndex].getClass() + ", skipping method.");
						
						continue methodLoop;						
					}
					
					paramMap.put(uriParam.name(), new ParamMapping(uriParam, populator, paramIndex));
					
					paramIndex++;
				}
			}
			
			//Parse alias
			if (StringUtils.isEmpty(annotation.alias())) {
				
				if(paramMap != null){
					
					log.error("Method " + method.getName() + " has " + paramMap.size() + " @" + URIParam.class.getSimpleName() + " annotated parameter(s) which are not included in the alias, skipping method.");
					
					continue;
				}
				
				List<URIComponent> components = Collections.singletonList((URIComponent)new StaticURIComponent(method.getName()));
				
				URIComponentHandler uriComponentHandler = new URIComponentHandler(components);
				
				addMapping(new RESTMapping(method, annotation, uriComponentHandler));
				
			}else if(annotation.alias().startsWith("/")){
				
				log.error("Method " + method.getName() + " has an invalid alias starting with /, skipping method.");
				
				continue;
				
			}else if(annotation.alias().contains("//")){
				
				log.error("Method " + method.getName() + " has an invalid alias containing with //, skipping method.");
				
				continue;
				
			}else{
				
				String[] splitAlias = annotation.alias().split("/");
				
				List<URIComponent> uriComponents = new ArrayList<URIComponent>(splitAlias.length);
				
				for(String aliasPart : splitAlias){
					
					if(aliasPart.contains("{") || aliasPart.contains("}")){
						
						if(aliasPart.length() < 3 || !aliasPart.startsWith("{") || !aliasPart.endsWith("}")){
							
							log.error("Method " + method.getName() + " has an invalid alias syntax near " + aliasPart + ", skipping method.");
							
							continue methodLoop;
						}
						
						String paramName = aliasPart.substring(1, aliasPart.length() -1);
						
						if(paramMap == null){
							
							log.error("Alias of method " + method.getName() + " refers to unknown URI parameter " + paramName + ", skipping method");
							
							continue methodLoop;
						}
						
						ParamMapping paramMapping = paramMap.get(paramName);
						
						if(paramMapping == null){
							
							log.error("Alias of method " + method.getName() + " refers to unknown URI parameter " + paramName + ", skipping method");
							
							continue methodLoop;
						}
						
						paramMap.remove(paramName);
						
						uriComponents.add(new DynamicURIComponent(paramMapping));
					
					}else{
					
						uriComponents.add(new StaticURIComponent(aliasPart));
					}
				}
				
				if(paramMap != null && !paramMap.isEmpty()){
					
					log.error("Method " + method.getName() + " has " + paramMap.size() + " @" + URIParam.class.getSimpleName() + " annotated parameter(s) which are not included in the alias, skipping method.");
					
					continue;
				}
				
				addMapping(new RESTMapping(method, annotation, new URIComponentHandler(uriComponents)));
			}
		}
		
		super.init(moduleDescriptor, sectionInterface, dataSource);
	}
	
	private final void addMapping(RESTMapping restMapping) {

		String method = restMapping.getAnnotation().method().toUpperCase();
		
		List<RESTMapping> mappings = this.restMethodMap.get(method);
		
		if(mappings == null){
			
			mappings = new ArrayList<RESTMapping>(3);
			this.restMethodMap.put(method, mappings);
		}
		
		mappings.add(restMapping);
	}

	protected final void addResponseHandler(ResponseHandler<?> responseHandler){
		
		Class<?> type = responseHandler.getType();
		
		if(type == null){
			
			throw new NullPointerException("Response handler cannot return null as type");
		}
		
		responseHandlers.add(responseHandler);
	}	
	
	private final boolean checkInitialParams(Class<?>[] parameters) {

		int i = 0;
		
		while(i < 4){
			
			if(!parameters[i].equals(PARAMETER_TYPES[i])){
				
				return false;
			}
			
			i++;
		}
		
		return true;
	}

	private final URIParam getURIParamAnnotation(int paramIndex, Annotation[][] paramAnnotations) {

		Annotation[] annotations = paramAnnotations[paramIndex];
		
		for(Annotation annotation : annotations){
			
			if(annotation instanceof URIParam){
				
				return (URIParam) annotation;
			}
		}
		
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected ForegroundModuleResponse processForegroundRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		if(uriParser.size() > 1){
			
			List<RESTMapping> mappings = restMethodMap.get(req.getMethod());
			
			if(mappings != null){
				
				int requiredComponentCount = uriParser.size() - 1;
				
				URIParser nextLevelURIParser = uriParser.getNextLevel();
				
				for(RESTMapping mapping : mappings){
					
					if(mapping.getComponentHandler().getComponentCount() == requiredComponentCount){
						
						Object[] paramArray = new Object[mapping.getParamCount()];
						
						if(mapping.getComponentHandler().matches(nextLevelURIParser, paramArray)){
							
							paramArray[0] = req;
							paramArray[1] = res;
							paramArray[2] = user;
							paramArray[3] = uriParser;
							
							try {
								Object response = mapping.getMethod().invoke(this, paramArray);

								if(response == null){
									
									return null;
								}
								
								for(ResponseHandler responseHandler : responseHandlers){
									
									if(responseHandler.getType().isAssignableFrom(response.getClass())){
										
										responseHandler.handleResponse(response, res);
										
										return null;
									}
								}
								
								throw new RuntimeException("Unable to find response handler for response of type " + response.getClass());
								
							} catch (InvocationTargetException e) {

								if (e.getCause() != null && e.getCause() instanceof Exception) {
									throw (Exception) e.getCause();
								} else {
									throw e;
								}
							}							
						}
					}
				}
			}
		}
		
		return super.processForegroundRequest(req, res, user, uriParser);
	}
}
