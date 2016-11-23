package com.imcode.oeplatform.flowengine.queries;

/**
 * Created by vitaly on 18.11.15.
 */
@Deprecated
public interface DependentField {
    public boolean isDependsOn();

    public void setDependsOn(boolean dependsOn);

    public String getDependencySourceName();

    public void setDependencySourceName(String dependencySourceName);

    public String getDependencyFieldName();

    public void setDependencyFieldName(String dependencyFieldName);
}
