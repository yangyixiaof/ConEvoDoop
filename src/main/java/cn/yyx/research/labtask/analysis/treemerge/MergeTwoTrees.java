package cn.yyx.research.labtask.analysis.treemerge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.IVariableBinding;

import cn.yyx.research.labtask.analysis.topology.TopologyNode;

public class MergeTwoTrees {
	
	private Set<TopologyNode> prim_tree1 = new HashSet<TopologyNode>();
	private Set<TopologyNode> prim_tree2 = new HashSet<TopologyNode>();
	
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
	
	private List<MergeChoicePair> merge_pairs = new LinkedList<MergeChoicePair>();
	
	private int count = 0;
	
	public MergeTwoTrees(int max_merge_times, Set<TopologyNode> tree1, Set<TopologyNode> tree2) {
		IntializeAnalysisEnvironment(tree1, tree2);
		
		PrepareMergeMaterial(max_merge_times);
		
		Merge();
		
		MergedTree mt = new MergedTree();
		GenerateTests(prim_tree1, mt, true);
		GenerateTests(prim_tree2, mt, false);
	}
	
	private void GenerateTests(Set<TopologyNode> prim_tree, MergedTree mt, boolean left_tree) {
		Queue<TopologyNode> tn_queue = new LinkedList<TopologyNode>();
		Iterator<TopologyNode> pitr = prim_tree.iterator();
		while (pitr.hasNext())
		{
			TopologyNode tn = pitr.next();
			tn_queue.add(tn);
		}
		while (!tn_queue.isEmpty())
		{
			TopologyNode tn = tn_queue.poll();
			if (deleted.contains(tn) || common_prefix.contains(tn)) {
				if (common_prefix.contains(tn)) {
					mt.getCommon_prefix().add(tn);
				}
			} else {
				if (left_tree) {
					mt.getLeft_thread().add(tn);
				} else {
					mt.getRight_thread().add(tn);
				}
			}
			
			Iterator<TopologyNode> ditr = tn.IterateDownChildren();
			while (ditr.hasNext())
			{
				TopologyNode dtn = ditr.next();
				tn_queue.add(dtn);
			}
		}
	}

	private Set<TopologyNode> common_prefix = new HashSet<TopologyNode>();
	private Set<TopologyNode> deleted = new HashSet<TopologyNode>();
	
	private void Merge() {
		// rename first.
		RenameOneTree(tree1_depth, rename_rule_tree1);
		RenameOneTree(tree2_depth, rename_rule_tree2);
		
		// merge
		Iterator<MergeChoicePair> mitr = merge_pairs.iterator();
		while (mitr.hasNext())
		{
			MergeChoicePair mcp = mitr.next();
			TopologyNode rep = mcp.ChooseReplaced();
			TopologyNode del = mcp.ChooseDeleted();
			DeleteSubTree(del);
			common_prefix.addAll(rep.getSub_tree());
		}
	}
	
	private void DeleteSubTree(TopologyNode del)
	{
		deleted.add(del);
		HashSet<TopologyNode> cmp_tree = del.getSub_tree();
		Iterator<TopologyNode> itr = cmp_tree.iterator();
		while (itr.hasNext())
		{
			TopologyNode tn = itr.next();
			if (tn == del)
			{
				continue;
			}
			boolean should_delete = true;
			Iterator<TopologyNode> ditr = tn.IterateDownChildren();
			while (ditr.hasNext())
			{
				TopologyNode dtn = ditr.next();
				if (!cmp_tree.contains(dtn))
				{
					should_delete = false;
					break;
				}
			}
			if (should_delete)
			{
				deleted.add(tn);
			}
		}
	}
	
	private void RenameOneTree(HashMap<TopologyNode, Integer> tree_depth, Map<IVariableBinding, String> rename_rule_tree)
	{
		Set<TopologyNode> tns = tree_depth.keySet();
		Iterator<TopologyNode> titr = tns.iterator();
		while (titr.hasNext())
		{
			TopologyNode tn = titr.next();
			tn.getKernel().accept(new MergeRename(rename_rule_tree));
		}
	}

	private void PrepareMergeMaterial(int max_merge_times) {
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
					merge_pairs.add(mpair);
					
					now_max_times--;
					if (now_max_times < 0)
					{
						break;
					}
				}
			}
		}
		
		// EliminateMergeSubTree(tree1_node_to_merge, tree1_root_to_merge);
		// EliminateMergeSubTree(tree2_node_to_merge, tree2_root_to_merge);
	}
	
//	private void EliminateMergeSubTree(Set<TopologyNode> tree_node_to_merge, Set<TopologyNode> tree_root_to_merge)
//	{
//		tree_root_to_merge.addAll(tree_node_to_merge);
//		Set<TopologyNode> to_be_deleted = new HashSet<TopologyNode>();
//		
//		Iterator<TopologyNode> titr1 = to_be_deleted.iterator();
//		while (titr1.hasNext())
//		{
//			TopologyNode tn1 = titr1.next();
//			Iterator<TopologyNode> titr2 = to_be_deleted.iterator();
//			while (titr2.hasNext())
//			{
//				TopologyNode tn2 = titr2.next();
//				if (tn1 != tn2)
//				{
//					if (tn2.getSub_tree().contains(tn1))
//					{
//						to_be_deleted.add(tn1);
//					}
//				}
//			}
//		}
//		
//		Iterator<TopologyNode> itr = to_be_deleted.iterator();
//		while (itr.hasNext())
//		{
//			TopologyNode tn = itr.next();
//			tree_root_to_merge.remove(tn);
//		}
//	}

	private void IntializeAnalysisEnvironment(Set<TopologyNode> tree1, Set<TopologyNode> tree2)
	{
		IterateEachPath(tree1.iterator(), 1, tree1_creation, tree1_depth, tree1_visited, prim_tree1);
		IterateEachPath(tree2.iterator(), 1, tree2_creation, tree2_depth, tree2_visited, prim_tree2);
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
		Set<MergeTypeMethod> mkeys = mtms.keySet();
		Iterator<MergeTypeMethod> mitr = mkeys.iterator();
		while (mitr.hasNext())
		{
			MergeTypeMethod mtm = mitr.next();
			MergeChoicePairs mcps = mtms.get(mtm);
			List<TopologyNode> order_tree1 = TranslateToOrdered(mcps.getTree1(), tree1_depth);
			List<TopologyNode> order_tree2 = TranslateToOrdered(mcps.getTree2(), tree2_depth);
			mtms_ordered.put(mtm, new MergeChoiceOrderedPairs(order_tree1, order_tree2));
		}
	}
	
	private List<TopologyNode> TranslateToOrdered(Set<TopologyNode> tns, HashMap<TopologyNode, Integer> tree_depth)
	{
		List<TopologyNode> tnlist = new LinkedList<TopologyNode>();
		Map<Integer, TopologyNode> order = new TreeMap<Integer, TopologyNode>();
		Iterator<TopologyNode> titr = tns.iterator();
		while (titr.hasNext())
		{
			TopologyNode tn = titr.next();
			order.put(-tree_depth.get(tn), tn);
		}
		Set<Integer> okeys = order.keySet();
		Iterator<Integer> oitr = okeys.iterator();
		while (oitr.hasNext())
		{
			Integer k = oitr.next();
			tnlist.add(order.get(k));
		}
		return tnlist;
	}
	
	private void IterateEachPath(Iterator<TopologyNode> itr, int depth, Set<TopologyNode> tree_creation, HashMap<TopologyNode, Integer> tree_depth, Set<TopologyNode> tree_visited, Set<TopologyNode> prim_tree)
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
				Iterator<TopologyNode> upitr = tn.IterateUpParents();
				if (!upitr.hasNext()) {
					prim_tree.add(tn);
				} else {
					IterateEachPath(upitr, depth+1, tree_creation, tree_depth, tree_visited, prim_tree);
				}
			}
		}
	}

}
