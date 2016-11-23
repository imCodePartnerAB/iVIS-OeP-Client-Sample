package com.nordicpeak.flowengine.comparators;

import java.util.Comparator;

import com.nordicpeak.flowengine.beans.Flow;

public class FlowNameComparator implements Comparator<Flow> {

	@Override
	public int compare(Flow f1, Flow f2) {

		return f1.getName().compareTo(f2.getName());
	}

}
