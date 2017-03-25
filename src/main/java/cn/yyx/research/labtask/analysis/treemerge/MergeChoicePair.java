package cn.yyx.research.labtask.analysis.treemerge;

import cn.yyx.research.labtask.analysis.topology.TopologyNode;

public class MergeChoicePair {
	
	private TopologyNode tn1 = null;
	private TopologyNode tn2 = null;
	
	public MergeChoicePair(TopologyNode tn1, TopologyNode tn2) {
		this.setTn1(tn1);
		this.setTn2(tn2);
	}

	public TopologyNode getTn1() {
		return tn1;
	}

	public void setTn1(TopologyNode tn1) {
		this.tn1 = tn1;
	}

	public TopologyNode getTn2() {
		return tn2;
	}

	public void setTn2(TopologyNode tn2) {
		this.tn2 = tn2;
	}
	
}
