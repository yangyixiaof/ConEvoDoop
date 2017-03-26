package cn.yyx.research.labtask.analysis.treemerge;

import cn.yyx.research.labtask.analysis.topology.TopologyNode;

public class MergeChoicePair {
	
	private double random_seed = -1;
	private TopologyNode tn1 = null;
	private TopologyNode tn2 = null;
	
	public MergeChoicePair(TopologyNode tn1, TopologyNode tn2) {
		this.setTn1(tn1);
		this.setTn2(tn2);
		this.random_seed = Math.random();
	}

	public TopologyNode getTn1() {
		return tn1;
	}

	private void setTn1(TopologyNode tn1) {
		this.tn1 = tn1;
	}

	public TopologyNode getTn2() {
		return tn2;
	}

	private void setTn2(TopologyNode tn2) {
		this.tn2 = tn2;
	}
	
	public TopologyNode ChooseReplaced()
	{
		if (random_seed > 0.5) {
			return tn2;
		} else {
			return tn1;
		}
	}
	
	public TopologyNode ChooseDeleted()
	{
		if (random_seed > 0.5) {
			return tn1;
		} else {
			return tn2;
		}
	}
	
	public TopologyNode ChooseInverseReplaced()
	{
		if (random_seed <= 0.5) {
			return tn2;
		} else {
			return tn1;
		}
	}
	
	public TopologyNode ChooseInverseDeleted()
	{
		if (random_seed <= 0.5) {
			return tn1;
		} else {
			return tn2;
		}
	}
	
}
