package cn.yyx.research.labtask.analysis.treemerge;

import java.util.HashSet;
import java.util.Set;

import cn.yyx.research.labtask.analysis.topology.TopologyNode;

public class MergeChoicePairs {
	
	private Set<TopologyNode> tree1 = new HashSet<TopologyNode>();
	private Set<TopologyNode> tree2 = new HashSet<TopologyNode>();
	
	public MergeChoicePairs() {
	}

	public Set<TopologyNode> getTree1() {
		return tree1;
	}

	public void AddToTree1(Set<TopologyNode> tree1) {
		this.tree1.addAll(tree1);
	}
	
	public void AddToTree1(TopologyNode tn) {
		this.tree1.add(tn);
	}

	public Set<TopologyNode> getTree2() {
		return tree2;
	}

	public void AddToTree2(Set<TopologyNode> tree2) {
		this.tree2.addAll(tree2);
	}
	
	public void AddToTree2(TopologyNode tn) {
		this.tree2.add(tn);
	}
	
}
