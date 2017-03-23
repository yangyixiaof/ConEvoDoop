package cn.yyx.research.labtask.analysis.ast;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

public class BindingManager {
	
	public static boolean QualifiedBinding(IBinding ib)
	{
		if (ib != null && (ib instanceof ITypeBinding || ib instanceof IVariableBinding))
		{
			return true;
		}
		return false;
	}
	
}
