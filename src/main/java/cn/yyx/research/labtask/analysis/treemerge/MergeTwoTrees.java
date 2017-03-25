package cn.yyx.research.labtask.analysis.treemerge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.IVariableBinding;

import cn.yyx.research.labtask.analysis.topology.TopologyNode;

public class MergeTwoTrees {
	
	private HashMap<TopologyNode, Integer> tree1_depth = new HashMap<TopologyNode, Integer>();
	private HashMap<TopologyNode, Integer> tree2_depth = new HashMap<TopologyNode, Integer>();
	
	private Map<IVariableBinding, String> rename_rule_tree1 = new TreeMap<IVariableBinding, String>();
	private Map<IVariableBinding, String> rename_rule_tree2 = new TreeMap<IVariableBinding, String>();
	
	private Set<TopologyNode> tree1_visited = new HashSet<TopologyNode>();
	private Set<TopologyNode> tree2_visited = new HashSet<TopologyNode>();
	
	private Set<TopologyNode> tree1_creation = new HashSet<TopologyNode>();
	private Set<TopologyNode> tree2_creation = new HashSet<TopologyNode>();
	
	private Map<MergeTypeMethod, MergeChoicePairs> mtms = new HashMap<MergeTypeMethod, MergeChoicePairs>();
	private Map<MergeTypeMethod, MergeChoiceOrderedPairs> mtms_ordered = new HashMap<MergeTypeMethod, MergeChoiceOrderedPairs>();
	
	private int count = 0;
	
	public MergeTwoTrees(int max_merge_times, Set<TopologyNode> tree1, Set<TopologyNode> tree2) {
		IntializeAnalysisEnvironment(tree1, tree2);
		
		Merge(max_merge_times);
		
		
	}
	
	private void Merge(int max_merge_times) {
		int pre_max_times = -1;
		int now_max_times = max_merge_times;
		while (now_max_times > 0 && now_max_times != pre_max_times)
		{
			pre_max_times = now_max_times;
			Set<MergeTypeMethod> mtms_keys = mtms_ordered.keySet();
			Iterator<MergeTypeMethod> mitr = mtms_keys.iterator();
			while (mitr.hasNext())
			{
				MergeTypeMethod mtm = mitr.next();
				MergeChoiceOrderedPairs mcop = mtms_ordered.get(mtm);
				MergeChoicePair mpair = mcop.IterateOnePair();
				if (mpair != null)
				{
					count++;
					String merge_name = "merge_"+count;
					rename_rule_tree1.put(mpair.getTn1().getInstance_type_binding(), merge_name);
					rename_rule_tree2.put(mpair.getTn2().getInstance_type_binding(), merge_name);
					
					now_max_times--;
				}
			}
		}
	}

	private void IntializeAnalysisEnvironment(Set<TopologyNode> tree1, Set<TopologyNode> tree2)
	{
		IterateEachPath(tree1.iterator(), 1, tree1_creation, tree1_depth, tree1_visited);
		IterateEachPath(tree2.iterator(), 1, tree2_creation, tree2_depth, tree2_visited);
		Iterator<TopologyNode> t1itr = tree1_creation.iterator();
		while (t1itr.hasNext())
		{
			TopologyNode tn1 = t1itr.next();
			if (tn1.isInstance_creation())
			{
				Iterator<TopologyNode> t2itr = tree2_creation.iterator();
				while (t2itr.hasNext())
				{
					TopologyNode tn2 = t2itr.next();
					if (tn2.isInstance_creation())
					{
						if (tn1.getInstance_type_binding().getType().equals(tn2.getInstance_type_binding().getType()))
						{
							if (tn1.getInstance_creation_binding().equals(tn2.getInstance_creation_binding()))
							{
								MergeTypeMethod mtm = new MergeTypeMethod(tn1.getInstance_type_binding().getType(), tn1.getInstance_creation_binding());
								MergeChoicePairs pairset = mtms.get(mtm);
								if (pairset == null)
								{
									pairset = new MergeChoicePairs();
									mtms.put(mtm, pairset);
								}
								pairset.AddToTree1(tn1);
								pairset.AddToTree1(tn2);
							}
						}
					}
				}
			}
		}
		
		// handle mtms to mtms_ordered.
		// mtms.
	}
	
	private void IterateEachPath(Iterator<TopologyNode> itr, int depth, Set<TopologyNode> tree_creation, HashMap<TopologyNode, Integer> tree_depth, Set<TopologyNode> tree_visited)
	{
		while (itr.hasNext())
		{
			TopologyNode tn = itr.next();
			tree_depth.put(tn, depth);
			if (tn.isInstance_creation())
			{
				tree_creation.add(tn);
			}
			if (!tree_visited.contains(tn))
			{
				tree_visited.add(tn);
				IterateEachPath(tn.IterateTopologyNode(), depth+1, tree_creation, tree_depth, tree_visited);
			}
		}
	}
	
}
