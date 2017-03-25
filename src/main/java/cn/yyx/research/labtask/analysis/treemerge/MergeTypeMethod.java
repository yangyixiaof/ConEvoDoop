package cn.yyx.research.labtask.analysis.treemerge;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class MergeTypeMethod {

	private ITypeBinding declare_type = null;
	private IMethodBinding creation_method = null;

	public MergeTypeMethod(ITypeBinding declare_type, IMethodBinding creation_method) {
		this.declare_type = declare_type;
		this.creation_method = creation_method;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((declare_type == null) ? 0 : declare_type.hashCode());
		result = prime * result + ((creation_method == null) ? 0 : creation_method.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MergeTypeMethod)
		{
			MergeTypeMethod mtm = (MergeTypeMethod)obj;
			if (declare_type.equals(mtm.declare_type) && creation_method.equals(mtm.creation_method))
			{
				return true;
			}
		}
		return super.equals(obj);
	}

}
