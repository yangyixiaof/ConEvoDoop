package cn.yyx.research.labtask.analysis.topology;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cn.yyx.research.util.FileIterator;
import cn.yyx.research.util.FileUtil;

public class TopologyDotGraph {
	
	private Set<TopologyNode> topology_root = null;
	private String name = null;
	private Set<TopologyNode> visited = new HashSet<TopologyNode>();
	public static String randoop_tests_dot_directory = "randoop-tests-dot";
	
	public TopologyDotGraph(String name, Set<TopologyNode> topology_root) {
		this.topology_root = topology_root;
		this.name = name;
	}
	
	public void GenerateAllPath()
	{
		visited.clear();
		
		StringBuilder sb = new StringBuilder("");
		sb.append("digraph {\n\nedge[fontname=\"SimSun\",fontcolor=red];\nnode[fontname=\"SimSun\",size=\"20,20\"];\n\n");
		
		IterateEachPath(topology_root.iterator(), sb, null);
		
		sb.append("\n}\n");
		FileUtil.WriteToFile(name + ".dot", sb.toString(), randoop_tests_dot_directory);
	}
	
	private void IterateEachPath(Iterator<TopologyNode> itr, StringBuilder sb, String nodename)
	{
		while (itr.hasNext())
		{
			TopologyNode tn = itr.next();
			if (!visited.contains(tn))
			{
				visited.add(tn);
				int line_number = tn.getLine_number();
				// String represent = tn.getRepresent().replace('.', '_').replace('<', '_').replace('>', '_').replace('#', '_');
				if (nodename != null) {
					sb.append(line_number + "->" + nodename + ";\n");// represent + "->" + nodename + ";\n"
				}
				IterateEachPath(tn.IterateTopologyNode(), sb, line_number + "");
			}
		}
	}
	
	public static void InitialDotGraphDirectory()
	{
		File f = new File(randoop_tests_dot_directory);
		if (f.exists())
		{
			FileIterator fi = new FileIterator(randoop_tests_dot_directory, ".*\\.dot$");
			Iterator<File> fitr = fi.EachFileIterator();
			while (fitr.hasNext())
			{
				File ff = fitr.next();
				ff.delete();
			}
		}
		if (!f.exists())
		{
			f.mkdir();
		}
	}
	
}
