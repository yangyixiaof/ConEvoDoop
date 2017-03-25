package cn.yyx.research.labtask.analysis.treemerge;

import java.util.LinkedList;
import java.util.List;

import cn.yyx.research.labtask.analysis.topology.TopologyNode;

public class MergeChoiceOrderedPairs {
	
	private int index = 0;
	private List<TopologyNode> depth_order1 = new LinkedList<TopologyNode>();
	private List<TopologyNode> depth_order2 = new LinkedList<TopologyNode>();
	
	public MergeChoiceOrderedPairs() {
	}
	
	public void AddToList1(TopologyNode tn)
	{
		depth_order1.add(tn);
	}
	
	public void AddToList2(TopologyNode tn)
	{
		depth_order2.add(tn);
	}
	
	public void Reset()
	{
		index = 0;
	}
	
	public MergeChoicePair IterateOnePair()
	{
		if (depth_order1.size() <= index || depth_order2.size() <= index)
		{
			return null;
		}
		MergeChoicePair mcp = new MergeChoicePair(depth_order1.get(index), depth_order2.get(index));
		index++;
		return mcp;
	}
	
}
