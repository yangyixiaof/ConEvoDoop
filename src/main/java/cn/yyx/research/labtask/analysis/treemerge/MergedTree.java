package cn.yyx.research.labtask.analysis.treemerge;

import java.util.List;

import cn.yyx.research.labtask.analysis.topology.TopologyNode;

public class MergedTree {
	
	private List<TopologyNode> common_prefix = null;
	private List<TopologyNode> left_thread = null;
	private List<TopologyNode> right_thread = null;
	
	public MergedTree() {
		this.setCommon_prefix(common_prefix);
		this.setLeft_thread(left_thread);
		this.setRight_thread(right_thread);
	}

	public List<TopologyNode> getCommon_prefix() {
		return common_prefix;
	}

	public void setCommon_prefix(List<TopologyNode> common_prefix) {
		this.common_prefix = common_prefix;
	}

	public List<TopologyNode> getLeft_thread() {
		return left_thread;
	}

	public void setLeft_thread(List<TopologyNode> left_thread) {
		this.left_thread = left_thread;
	}

	public List<TopologyNode> getRight_thread() {
		return right_thread;
	}

	public void setRight_thread(List<TopologyNode> right_thread) {
		this.right_thread = right_thread;
	}
	
}
