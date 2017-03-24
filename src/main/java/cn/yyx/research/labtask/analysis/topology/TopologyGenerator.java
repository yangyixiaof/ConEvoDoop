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
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

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

	private IMethodBinding last_method_binding = null;
	private ITypeBinding last_type_binding = null;
	private Map<ITypeBinding, TopologyNode> last_type_binding_node = new HashMap<ITypeBinding, TopologyNode>();
	private Map<IVariableBinding, TopologyNode> last_variable_binding_node = new HashMap<IVariableBinding, TopologyNode>();

	private Set<IVariableBinding> creation_variable = new HashSet<IVariableBinding>();

	private Set<TopologyNode> roots = new HashSet<TopologyNode>();

	private Map<IBinding, HashSet<IBinding>> data_dependency = new HashMap<IBinding, HashSet<IBinding>>();

	private Set<IVariableBinding> temp_variable_binds = new HashSet<IVariableBinding>();

	private CompilationUnit unit = null;

	private void StatementOverHandle(Statement stmt) {
		if (temp_variable_binds.size() > 0 || last_type_binding_node.size() > 0) {
			// add parents.
			Iterator<IVariableBinding> itr = temp_variable_binds.iterator();
			while (itr.hasNext()) {
				IVariableBinding ib = itr.next();
				TopologyNode tn = last_variable_binding_node.get(ib);
				current_node.AddOneParent(tn);
				roots.remove(tn);
			}
			Set<ITypeBinding> type_binding_set = last_type_binding_node.keySet();
			Iterator<ITypeBinding> titr = type_binding_set.iterator();
			while (titr.hasNext()) {
				ITypeBinding itb = titr.next();
				TopologyNode tn = last_type_binding_node.get(itb);
				current_node.AddOneParent(tn);
				roots.remove(tn);
			}
		}
		
		// update last mapping.
		Iterator<IVariableBinding> itr = temp_variable_binds.iterator();
		while (itr.hasNext()) {
			IVariableBinding ib = itr.next();
			last_variable_binding_node.put(ib, current_node);
		}
		if (last_type_binding != null) {
			last_type_binding_node.put(last_type_binding, current_node);
			last_type_binding = null;
		}

		// set current node.
		current_node.setKernel(stmt);
		int line_number = unit.getLineNumber(stmt.getStartPosition());
		current_node.setLine_number(line_number);
		current_node.setNeed_try_catch(wrap_with_try_catch);
		current_node.SnapShotDataDependency(data_dependency);
		roots.add(current_node);
		temp_variable_binds.clear();
	}

	public TopologyGenerator(CompilationUnit cu) {
		unit = cu;
	}

	private boolean QualifiedStatement(ASTNode node) {
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
			if (current_node != null) {
				StatementOverHandle((Statement) node);
				current_node = null;
				last_method_binding = null;
			}
		}
		if (node instanceof TryStatement) {
			wrap_with_try_catch = false;
		}
		super.postVisit(node);
	}

	private IVariableBinding HandleDataDependency(IVariableBinding ivb) {
		if (ivb != null && creation_variable.contains(ivb)) {
			HashSet<IBinding> set = data_dependency.get(ivb);
			if (set == null) {
				set = new HashSet<IBinding>();
				data_dependency.put(ivb, set);
			}
			set.addAll(temp_variable_binds);
			return ivb;
		}
		return null;
	}

	@Override
	public void endVisit(MethodInvocation node) {
		Expression expr = node.getExpression();
		if (expr != null && expr instanceof Name) {
			Name name = (Name) expr;
			IBinding ib = name.resolveBinding();
			if (ib instanceof IVariableBinding) {
				HandleDataDependency((IVariableBinding) ib);
			}
		}
		IMethodBinding imb = node.resolveMethodBinding();
		if (imb != null) {
			last_method_binding = imb;
		}
	}

	@Override
	public void endVisit(ClassInstanceCreation node) {
		IMethodBinding imb = node.resolveConstructorBinding();
		if (imb != null) {
			last_method_binding = imb;
		}
		super.endVisit(node);
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		Name name = node.getName();
		IVariableBinding ivb = (IVariableBinding) name.resolveBinding();
		creation_variable.add(ivb);
		return super.visit(node);
	}

	@Override
	public void endVisit(VariableDeclarationFragment node) {
		Name name = node.getName();
		IVariableBinding ivb = (IVariableBinding) name.resolveBinding();
		HandleDataDependency(ivb);
		current_node.setInstance_creation(true, ivb, last_method_binding);
	}

	@Override
	public void endVisit(Assignment node) {
		Expression expr = node.getLeftHandSide();
		if (expr != null && expr instanceof Name) {
			Name name = (Name) expr;
			IVariableBinding ib = (IVariableBinding) name.resolveBinding();
			HandleDataDependency(ib);
		}
	}

	@Override
	public void endVisit(SimpleName node) {
		// testing.
		// System.out.println("SimpleName:" + node);
		if (current_node != null) {
			IBinding ib = node.resolveBinding();
			if (BindingManager.QualifiedBinding(ib)) {
				if (ib instanceof IVariableBinding) {
					IVariableBinding ivb = (IVariableBinding) ib;
					if (creation_variable.contains(ivb)) {
						temp_variable_binds.add(ivb);
					}
				}
				if (ib instanceof ITypeBinding) {
					ITypeBinding itb = (ITypeBinding) ib;
					if (last_type_binding == null) {
						last_type_binding = itb;
					}
				}
			}
		}
	}

	public Set<TopologyNode> TopologyRoots() {
		// testing.
		// System.out.println(roots);
		return roots;
	}

}
