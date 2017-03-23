package cn.yyx.research.labtask.analysis.topology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;

import cn.yyx.research.labtask.analysis.ast.BindingManager;

public class TopologyGenerator extends ASTVisitor {

	// private boolean start = false;
	// private Statement start_statement = null;
	// private Set<IBinding> start_bindings = new HashSet<IBinding>();
	// private IBinding correspond_binding = null;

	// private TopologyNode unknown_access = null;
	// private boolean first_simple_name = false;
	// private boolean is_static_access = false;

	private TopologyNode current_node = null;

	private boolean wrap_with_try_catch = false;

	// private StringBuilder represent = new StringBuilder();

	private Map<IBinding, TopologyNode> last_binding_node = new HashMap<IBinding, TopologyNode>();

	private Set<TopologyNode> roots = new HashSet<TopologyNode>();

	private Map<IBinding, HashSet<IBinding>> data_dependency = new HashMap<IBinding, HashSet<IBinding>>();

	private Set<IBinding> temp_binds = new HashSet<IBinding>();

	private CompilationUnit unit = null;
	
	private void StatementOverHandle(Statement stmt)
	{
		if (temp_binds.size() > 0)
		{
			Iterator<IBinding> itr = temp_binds.iterator();
			while (itr.hasNext())
			{
				IBinding ib = itr.next();
				TopologyNode tn = last_binding_node.get(ib);
				current_node.AddOneParent(tn);
			}
			itr = temp_binds.iterator();
			while (itr.hasNext())
			{
				IBinding ib = itr.next();
				last_binding_node.put(ib, current_node);
			}
			current_node.setKernel(stmt);
			int line_number = unit.getLineNumber(stmt.getStartPosition());
			current_node.setLine_number(line_number);
			current_node.setNeed_try_catch(wrap_with_try_catch);
		}
		temp_binds.clear();
	}

	public TopologyGenerator(CompilationUnit cu) {
		unit = cu;
	}
	
	private boolean QualifiedStatement(ASTNode node)
	{
		if ((node instanceof Statement) && !(node instanceof IfStatement) && !(node instanceof TryStatement)
				&& !(node instanceof Block)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean preVisit2(ASTNode node) {
		if (QualifiedStatement(node)) {
			if (current_node == null) {
				current_node = new TopologyNode();
			}
		}
		if (node instanceof TryStatement) {
			wrap_with_try_catch = true;
		}
		if (node instanceof CatchClause) {
			return false;
		}
		if (node instanceof Type) {
			return false;
		}
		if (node instanceof IfStatement) {
			return false;
		}
		return true;
	}

	@Override
	public void postVisit(ASTNode node) {
		if (QualifiedStatement(node)) {
			if (current_node != null)
			{
				StatementOverHandle((Statement)node);
				current_node = null;
			}
		}
		if (node instanceof TryStatement) {
			wrap_with_try_catch = false;
		}
		super.postVisit(node);
	}

	private void HandleDataDependency(IBinding ib) {
		if (BindingManager.QualifiedBinding(ib)) {
			HashSet<IBinding> set = data_dependency.get(ib);
			if (set == null) {
				set = new HashSet<IBinding>();
				data_dependency.put(ib, set);
			}
			set.addAll(temp_binds);
		}
	}

	@Override
	public void endVisit(MethodInvocation node) {
		Expression expr = node.getExpression();
		if (expr != null && expr instanceof Name) {
			Name name = (Name) expr;
			IBinding ib = name.resolveBinding();
			HandleDataDependency(ib);
		}
	}

	@Override
	public void endVisit(SingleVariableDeclaration node) {
		Name name = node.getName();
		IBinding ib = name.resolveBinding();
		HandleDataDependency(ib);
		current_node.setInstance_creation(true, node.resolveBinding());
	}

	@Override
	public void endVisit(Assignment node) {
		Expression expr = node.getLeftHandSide();
		if (expr != null && expr instanceof Name) {
			Name name = (Name) expr;
			IBinding ib = name.resolveBinding();
			HandleDataDependency(ib);
		}
	}

	@Override
	public void endVisit(SimpleName node) {
		// testing.
		// System.out.println("SimpleName:" + node);
		IBinding ib = node.resolveBinding();
		if (BindingManager.QualifiedBinding(ib)) {
			temp_binds.add(ib);
		}
	}

	public Set<TopologyNode> TopologyRoots() {
		// testing.
		// System.out.println(roots);
		return roots;
	}

}
