package cn.yyx.research.labtask.analysis.topology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Statement;

public class TopologyNode {
	
	private boolean instance_creation = false;
	// only set when instance_creation is true.
	private IVariableBinding instance_binding = null;
	private IMethodBinding instance_creation_binding = null;
	
	private Statement kernel = null;
	private int line_number = -1;
	private boolean need_try_catch = false;
	private Set<TopologyNode> up_parents = new HashSet<TopologyNode>();
	private Map<IBinding, HashSet<IBinding>> data_dependency_current_copy = new HashMap<IBinding, HashSet<IBinding>>();
	
	public TopologyNode() {
	}
	
//	public void SetTopologyNode(Statement kernel, String represent, int line_number, boolean need_try_catch, Set<TopologyNode> up_parents)
//	{
//		this.setKernel(kernel);
//		this.setRepresent(represent);
//		this.setLine_number(line_number);
//		this.setNeed_try_catch(need_try_catch);
//		this.up_parents.addAll(up_parents);
//	}
	
	public void SnapShotDataDependency(Map<IBinding, HashSet<IBinding>> data_dependency)
	{
		data_dependency_current_copy.putAll(data_dependency);
	}
	
	public void AddOneParent(TopologyNode one_up_parent)
	{
		if (one_up_parent == null)
		{
			return;
		}
		this.up_parents.add(one_up_parent);
	}
	
	public Iterator<TopologyNode> IterateTopologyNode()
	{
		return up_parents.iterator();
	}
	
	public Statement getKernel() {
		return kernel;
	}

	public void setKernel(Statement kernel) {
		this.kernel = kernel;
	}

//	public String getRepresent() {
//		return represent;
//	}
//
//	public void setRepresent(String represent) {
//		this.represent = represent;
//	}

	public boolean isNeed_try_catch() {
		return need_try_catch;
	}

	public void setNeed_try_catch(boolean need_try_catch) {
		this.need_try_catch = need_try_catch;
	}

	public int getLine_number() {
		return line_number;
	}

	public void setLine_number(int line_number) {
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
		return line_number + ";" + kernel.toString();
	}

	public boolean isInstance_creation() {
		return instance_creation;
	}

	public void setInstance_creation(boolean instance_creation, IVariableBinding instance_binding, IMethodBinding instance_creation_binding) {
		this.instance_creation = instance_creation;
		this.instance_binding = instance_binding;
		this.instance_creation_binding = instance_creation_binding;
	}

	public IVariableBinding getInstance_type_binding() {
		return instance_binding;
	}

	public IMethodBinding getInstance_creation_binding() {
		return instance_creation_binding;
	}

}
