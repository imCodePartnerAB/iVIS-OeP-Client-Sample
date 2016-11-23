package se.unlogic.hierarchy.basemodules;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import se.unlogic.hierarchy.core.annotations.EventListener;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.interfaces.ModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.VisibleModuleDescriptor;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xsl.XSLVariableReader;


public abstract class AnnotatedSectionModule<DescriptorType extends VisibleModuleDescriptor> extends BaseSectionModule<DescriptorType> {

	protected List<ScriptTag> scripts;
	protected List<LinkTag> links;

	protected List<ReflectionInstanceListener<?>> instanceListeners;
	protected boolean hasRequiredDependencies;

	protected List<ReflectionEventListener<?>> eventListeners;

	protected ReentrantReadWriteLock dependencyLock;
	protected Lock dependencyReadLock;

	@SuppressWarnings("unchecked")
	@Override
	public void init(DescriptorType descriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(descriptor, sectionInterface, dataSource);

		parseXSLStyleSheet();

		parseSettings(moduleDescriptor.getMutableSettingHandler());

		moduleConfigured();

		ReentrantReadWriteLock dependencyLock = new ReentrantReadWriteLock();

		instanceListeners = getInstanceListeners(dependencyLock.writeLock());

		if(instanceListeners != null){

			for(ReflectionInstanceListener<?> instanceListener : instanceListeners){

				log.debug("Adding instance listener for class " + instanceListener.getRawKey());
				systemInterface.getInstanceHandler().addInstanceListener(instanceListener.getRawKey(), instanceListener);

				if(!hasRequiredDependencies && instanceListener.isRequired()){

					hasRequiredDependencies = true;
				}
			}

			this.dependencyLock = dependencyLock;
			dependencyReadLock = dependencyLock.readLock();
		}

		eventListeners = getEventListeners();

		if(eventListeners != null){

			for(ReflectionEventListener<?> eventListener : eventListeners){

				log.debug("Adding event listener for channel " + eventListener.getChannel() + " and event type " + eventListener.getEventType());

				systemInterface.getEventHandler().addEventListener(eventListener.getChannel(), eventListener.getRawEventType(), eventListener);
			}
		}
	}

	@Override
	public void update(DescriptorType descriptor, DataSource dataSource) throws Exception {

		super.update(descriptor, dataSource);

		parseXSLStyleSheet();

		parseSettings(descriptor.getMutableSettingHandler());

		moduleConfigured();
	}

	protected void moduleConfigured() throws Exception{};

	@SuppressWarnings("unchecked")
	@Override
	public void unload() throws Exception {

		if(eventListeners != null){

			for(ReflectionEventListener<?> eventListener : eventListeners){

				log.debug("Removing event listener for channel " + eventListener.getChannel() + " and event type " + eventListener.getEventType());

				systemInterface.getEventHandler().removeEventListener(eventListener.getChannel(), eventListener.getRawEventType(), eventListener);
			}
		}

		if(instanceListeners != null){

			for(ReflectionInstanceListener<?> instanceListener : instanceListeners){

				log.debug("Removing instance listener for class " + instanceListener.getRawKey());
				systemInterface.getInstanceHandler().removeInstanceListener(instanceListener.getRawKey(), instanceListener);
			}
		}

		super.unload();
	}

	protected void parseSettings(MutableSettingHandler mutableSettingHandler) throws Exception {

		ModuleUtils.setModuleSettings(this,BaseSectionModule.class, mutableSettingHandler, sectionInterface.getSystemInterface());
	}

	@SuppressWarnings("unchecked")
	protected void parseXSLStyleSheet() throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, URISyntaxException{

		XSLVariableReader variableReader = ModuleUtils.getXSLVariableReader(moduleDescriptor, sectionInterface.getSystemInterface());

		if(variableReader != null){

			ModuleUtils.setXSLVariables(variableReader,this, BaseSectionModule.class,moduleDescriptor);
		}

		if(variableReader != null){

			List<ScriptTag> globalScripts = ModuleUtils.getGlobalScripts(variableReader);
			List<ScriptTag> localScripts = ModuleUtils.getScripts(variableReader, sectionInterface, getStaticContentPrefix(), moduleDescriptor);

			this.scripts = CollectionUtils.combine(globalScripts,localScripts);

			List<LinkTag> globalLinks =  ModuleUtils.getGlobalLinks(variableReader);
			List<LinkTag> localLinks =  ModuleUtils.getLinks(variableReader, sectionInterface, getStaticContentPrefix(), moduleDescriptor);
			
			this.links = CollectionUtils.combine(globalLinks,localLinks);
		}
	}

	@SuppressWarnings("rawtypes")
	private List<ReflectionInstanceListener<?>> getInstanceListeners(Lock writeLock) {

		List<ReflectionInstanceListener<?>> instanceListeners = new ArrayList<ReflectionInstanceListener<?>>();

		List<Field> fields = ReflectionUtils.getFields(this.getClass());

		for(Field field : fields){

			InstanceManagerDependency annotation = field.getAnnotation(InstanceManagerDependency.class);

			if(annotation == null){

				continue;
			}

			instanceListeners.add(new FieldInstanceListener(this, field, annotation.required(),writeLock));
		}

		List<Method> methods = ReflectionUtils.getMethods(this.getClass());

		for(Method method : methods){

			InstanceManagerDependency annotation = method.getAnnotation(InstanceManagerDependency.class);

			if(annotation == null){

				continue;
			}

			instanceListeners.add(new MethodInstanceListener(this, method, annotation.required(),writeLock));
		}

		if(instanceListeners.isEmpty()){

			return null;
		}

		return instanceListeners;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<ReflectionEventListener<?>> getEventListeners() {

		List<ReflectionEventListener<?>> eventListeners = new ArrayList<ReflectionEventListener<?>>();

		List<Method> methods = ReflectionUtils.getMethods(this.getClass());

		for(Method method : methods){

			EventListener annotation = method.getAnnotation(EventListener.class);

			if(annotation == null){

				continue;
			}

			eventListeners.add(new ReflectionEventListener(annotation.channel(), method.getParameterTypes()[0], this, method, annotation.priority()));
		}

		if(eventListeners.isEmpty()){

			return null;
		}

		return eventListeners;
	}

	protected abstract String getStaticContentPrefix();

	@Override
	public List<SettingDescriptor> getSettings() {

		ArrayList<SettingDescriptor> settingDescriptors = new ArrayList<SettingDescriptor>();

		ModuleUtils.addSettings(settingDescriptors, super.getSettings());

		try {
			ModuleUtils.addSettings(settingDescriptors, ModuleUtils.getAnnotatedSettingDescriptors(this,BaseSectionModule.class, systemInterface));

		} catch (RuntimeException e) {

			throw e;

		} catch (Exception e){

			throw new RuntimeException(e);
		}

		return settingDescriptors;
	}

	protected void setLinksAndScripts(ModuleResponse moduleResponse) {

		if(scripts != null){
			moduleResponse.addScripts(scripts);
		}

		if(links != null){
			moduleResponse.addLinks(links);
		}
	}
}
