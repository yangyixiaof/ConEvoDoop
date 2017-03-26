package cn.yyx.research.labtask.analysis.treemerge;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

public class MergeRename extends ASTVisitor {
	
	Map<IVariableBinding, String> rename_rules = null;
	
	public MergeRename(Map<IVariableBinding, String> rename_rules) {
		this.rename_rules = rename_rules;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		IBinding ib = node.resolveBinding();
		if (ib instanceof IVariableBinding)
		{
			IVariableBinding ivb = (IVariableBinding)ib;
			String rename = rename_rules.get(ivb);
			if (rename != null)
			{
				node.setIdentifier(rename);
			}
		}
		return super.visit(node);
	}
	
}
