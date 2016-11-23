package se.unlogic.hierarchy.core.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import se.unlogic.hierarchy.core.beans.AliasMapping;
import se.unlogic.hierarchy.core.beans.ModuleMapping;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.AliasType;
import se.unlogic.hierarchy.core.exceptions.InvalidModuleNameException;
import se.unlogic.hierarchy.core.interfaces.Module;
import se.unlogic.hierarchy.core.interfaces.MultipleAliasModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.string.StringUtils;


public abstract class WildcardModuleCache<DescriptorType extends MultipleAliasModuleDescriptor, ModuleType extends Module<?>, ListenerType> extends BaseModuleCache<DescriptorType, ModuleType, ListenerType> {

	public static final ModulePriorityComparator PRIORITY_COMPARATOR = new ModulePriorityComparator();

	public static final String EXCLUDE_ALIAS_PREFIX = "exclude:";
	public static final String REGEXP_ALIAS_PREFIX = "regexp:";

	protected final ArrayList<ModuleMapping<DescriptorType>> mappingList = new ArrayList<ModuleMapping<DescriptorType>>();

	public WildcardModuleCache(SystemInterface systemInterface) {

		super(systemInterface);
	}

	protected void setAliasMappings(DescriptorType moduleDescriptor) {

		List<AliasMapping> aliasMappingList = new ArrayList<AliasMapping>();

		for (String alias : moduleDescriptor.getAliases()) {

			boolean exclude = false;

			if(alias.toLowerCase().startsWith(REGEXP_ALIAS_PREFIX)) {

				aliasMappingList.add(new AliasMapping(alias.replaceAll("/\\/", "\\").replace("regexp:", ""), AliasType.REGEXP));

			} else {

				if(alias.toLowerCase().startsWith(EXCLUDE_ALIAS_PREFIX) && alias.length() > EXCLUDE_ALIAS_PREFIX.length()){

					alias = alias.substring(EXCLUDE_ALIAS_PREFIX.length());
					exclude = true;
				}

				if (!alias.contains("*")) {

					aliasMappingList.add(new AliasMapping(alias, AliasType.WHEN_REQUEST_EQUALS,exclude));

				} else if (alias.equals("*") || alias.equals("**")) {

					aliasMappingList.add(new AliasMapping(null, AliasType.ALWAYS,exclude));

				} else if (alias.startsWith("*") && alias.endsWith("*") && alias.length() > 2) {

					aliasMappingList.add(new AliasMapping(alias.substring(1, alias.length() - 1), AliasType.WHEN_REQUEST_CONTAINS,exclude));

				} else if (alias.startsWith("*")) {

					aliasMappingList.add(new AliasMapping(alias.substring(1), AliasType.WHEN_REQUEST_ENDS_WITH,exclude));

				} else if (alias.endsWith("*")) {

					aliasMappingList.add(new AliasMapping(alias.substring(0, alias.length() - 1), AliasType.WHEN_REQUEST_STARTS_WITH,exclude));
					
				}else{
					
					log.warn("Unsupported alias detected for module " + moduleDescriptor);
				}
			}
		}

		this.mappingList.add(new ModuleMapping<DescriptorType>(moduleDescriptor, aliasMappingList));
		Collections.sort(this.mappingList, PRIORITY_COMPARATOR);
	}

	protected void removeAliasMappings(DescriptorType moduleDescriptor){

		for(ModuleMapping<DescriptorType> moduleMapping : mappingList){

			if(moduleMapping.getModuleDescriptor().equals(moduleDescriptor)){

				mappingList.remove(moduleMapping);
				return;
			}
		}
	}

	public List<Entry<DescriptorType, ModuleType>> getEntries(String currentURI, User user) {

		r.lock();

		List<Entry<DescriptorType, ModuleType>> matches = new ArrayList<Entry<DescriptorType, ModuleType>>();

		try {
			outer: for(ModuleMapping<DescriptorType> moduleMapping : mappingList){

				if(!AccessUtils.checkAccess(user, moduleMapping.getModuleDescriptor())){

					continue;
				}

				for (AliasMapping aliasMapping : moduleMapping.getMappings()) {

					switch (aliasMapping.getAliasType()) {

						case ALWAYS:

							if(!aliasMapping.isExclude()){
								matches.add(getEntry(moduleMapping));
							}

							continue outer;

						case WHEN_REQUEST_STARTS_WITH:

							if (currentURI.startsWith(aliasMapping.getAlias())) {

								if(!aliasMapping.isExclude()){
									matches.add(getEntry(moduleMapping));
								}

								continue outer;
							}

							break;

						case WHEN_REQUEST_ENDS_WITH:

							if (currentURI.endsWith(aliasMapping.getAlias())) {

								if(!aliasMapping.isExclude()){
									matches.add(getEntry(moduleMapping));
								}

								continue outer;
							}

							break;

						case WHEN_REQUEST_CONTAINS:

							if (currentURI.contains(aliasMapping.getAlias())) {

								if(!aliasMapping.isExclude()){
									matches.add(getEntry(moduleMapping));
								}

								continue outer;
							}

							break;

						case WHEN_REQUEST_EQUALS:

							if (currentURI.equals(aliasMapping.getAlias())) {

								if(!aliasMapping.isExclude()){
									matches.add(getEntry(moduleMapping));
								}

								continue outer;
							}

							break;

						case REGEXP:

							if (aliasMapping.getPattern().matcher(currentURI).matches()) {

								matches.add(getEntry(moduleMapping));

								continue outer;
							}

							break;
					}
				}
			}


		if (!matches.isEmpty()) {

			return matches;
		}

		} finally {
			r.unlock();
		}

		return null;
	}

	private Entry<DescriptorType, ModuleType> getEntry(ModuleMapping<DescriptorType> moduleMapping) {

		return new SimpleEntry<DescriptorType, ModuleType>(moduleMapping.getModuleDescriptor(), instanceMap.get(moduleMapping.getModuleDescriptor()));
	}

	protected void validateDescritor(DescriptorType descriptor) throws InvalidModuleNameException {

		if(StringUtils.isEmpty(descriptor.getName())){

			throw new InvalidModuleNameException(descriptor);
		}
	}
}
