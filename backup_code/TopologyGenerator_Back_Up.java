package cn.yyx.research.labtask.analysis.topology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;

public class TopologyGenerator_Back_Up extends ASTVisitor {
	
	private boolean start = false;
	private Statement start_statement = null;
	private Set<IBinding> start_bindings = new HashSet<IBinding>();
	private IBinding correspond_binding = null;
	
	private TopologyNode unknown_access = null;
	private boolean first_simple_name = false;
	
	private boolean is_static_access = false;
	
	private boolean wrap_with_try_catch = false;
	
	private StringBuilder represent = new StringBuilder();
	private Map<IBinding, TopologyNode> last_binding_node = new HashMap<IBinding, TopologyNode>();
	private Set<TopologyNode> roots = new HashSet<TopologyNode>();
	
	private CompilationUnit unit = null;
	
	public TopologyGenerator_Back_Up(CompilationUnit cu) {
		unit = cu;
	}
	
	@Override
	public boolean preVisit2(ASTNode node) {
		if (!start && (node instanceof Statement) && !(node instanceof IfStatement) && !(node instanceof TryStatement) && !(node instanceof Block))
		{
			start = true;
			start_statement = (Statement) node;
			represent.delete(0, represent.length());
			correspond_binding = null;
			start_bindings.clear();
			first_simple_name = false;
			is_static_access = false;
		}
		if (node instanceof TryStatement)
		{
			wrap_with_try_catch = true;
		}
		if (node instanceof CatchClause)
		{
			return false;
		}
		if (node instanceof Type)
		{
			return false;
		}
		if (node instanceof IfStatement)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public void postVisit(ASTNode node) {
		if (node == start_statement)
		{
			List<TopologyNode> topos = new LinkedList<TopologyNode>();
			if (unknown_access != null)
			{
				topos.add(unknown_access);
			}
			Iterator<IBinding> sitr = start_bindings.iterator();
			while (sitr.hasNext())
			{
				IBinding ib = sitr.next();
				TopologyNode tn = last_binding_node.get(ib);
				if (tn == null) {
					if (ib != correspond_binding)
					{
						System.err.println("Last binding topology node is null? This should not happen.");
						System.err.println(last_binding_node);
						System.exit(1);
					}
				} else {
					topos.add(tn);
					roots.remove(tn);
				}
			}
			int line_number = unit.getLineNumber(node.getStartPosition());// - 1
			TopologyNode ntn = new TopologyNode(start_statement, represent.toString(), line_number, wrap_with_try_catch, topos);
			roots.add(ntn);
			if (correspond_binding == null) {
				if (!is_static_access)
				{
					System.err.println("Warning:no variable declaration or method invocation? The strange form is:" + node + ";This statement will be ignored.");
				}
				unknown_access = ntn;
			} else {
				last_binding_node.put(correspond_binding, ntn);
			}
			
			start_statement = null;
			start = false;
			represent.delete(0, represent.length());
			correspond_binding = null;
			start_bindings.clear();
			first_simple_name = false;
			is_static_access = false;
		}
		if (node instanceof TryStatement)
		{
			wrap_with_try_catch = false;
		}
		super.postVisit(node);
	}
	
	@Override
	public void endVisit(SimpleName node) {
		if (!start)
		{
			return;
		}
		
		// testing.
		// System.out.println("SimpleName:" + node);
		
		IBinding binding = node.resolveBinding();
		if (binding != null)
		{
			if (binding instanceof IVariableBinding) {
				IVariableBinding varbinding = (IVariableBinding)binding;
				if (!varbinding.isField())
				{
					ITypeBinding typebinding = varbinding.getType();
					represent.append("#" + typebinding.getQualifiedName());
					start_bindings.add(binding);
				}
				
				// testing.
				// System.out.println("node:"+node+";binding:"+binding+";is_field:"+varbinding.isField());
			}
			if (!first_simple_name)
			{
				if (binding instanceof IVariableBinding) {
					IVariableBinding varbinding = (IVariableBinding)binding;
					if (!varbinding.isField())
					{
						if (correspond_binding == null)
						{
							correspond_binding = varbinding;
						}
					}
				} else if (binding instanceof ITypeBinding) {
					// is at least partly static access.
					// System.out.println("debugging:MethodInvocation:" + node + ";ITypeBinding:" + tb.getQualifiedName() + ";IFSelfIsType:" + tb.isTypeVariable() + ";IsClass:" + tb.isClass());
					ITypeBinding tb = (ITypeBinding)binding;
					represent.append(tb.getQualifiedName());
					is_static_access = true;
				}
				first_simple_name = true;
			}
		}
	}
	
	public Set<TopologyNode> TopologyRoots()
	{
		// testing.
		// System.out.println(roots);
		
		return roots;
	}
	
}