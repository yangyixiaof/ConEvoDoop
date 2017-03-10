package cn.yyx.research.labtask.analysis.topology;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.Statement;

public class TopologyNode {
	
	private Statement kernel = null;
	private String represent = null;
	private int line_number = -1;
	private boolean need_try_catch = false;
	
	private List<TopologyNode> up_parents = new LinkedList<TopologyNode>();
	
	public TopologyNode(Statement kernel, String represent, int line_number, boolean need_try_catch, List<TopologyNode> up_parents)
	{
		this.setKernel(kernel);
		this.setRepresent(represent);
		this.setLine_number(line_number);
		this.setNeed_try_catch(need_try_catch);
		this.up_parents.addAll(up_parents);
	}
	
	public Iterator<TopologyNode> IterateTopologyNode()
	{
		return up_parents.iterator();
	}
	
	public Statement getKernel() {
		return kernel;
	}

	private void setKernel(Statement kernel) {
		this.kernel = kernel;
	}

	public String getRepresent() {
		return represent;
	}

	private void setRepresent(String represent) {
		this.represent = represent;
	}

	public boolean isNeed_try_catch() {
		return need_try_catch;
	}

	private void setNeed_try_catch(boolean need_try_catch) {
		this.need_try_catch = need_try_catch;
	}

	public int getLine_number() {
		return line_number;
	}

	private void setLine_number(int line_number) {
		this.line_number = line_number;
	}
	
	@Override
	public int hashCode() {
		return kernel.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TopologyNode)
		{
			return kernel.equals(((TopologyNode)obj).kernel);
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return kernel.toString();
	}
	
}
