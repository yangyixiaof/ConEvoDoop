package cn.yyx.research.labtask.consevodoop;

import java.io.File;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import cn.yyx.research.labtask.analysis.jdtutil.JDTUtil;
import cn.yyx.research.util.FileUtil;

public class TestJDT {

	public void common_testx1() {
		String[] ag1 = { "12", "34" };
		String[] ag2 = {};
		System.out.println(String.join(" ", ag1));
		System.out.println(String.join(" ", ag2));

		System.out.println(File.pathSeparator);

		File f = new File(
				"D:/eclipse-workspace-pool/eclipse-rcp-neon/rvpredict_benchmarks/target/benchmark-0.0.1-SNAPSHOT.jar");
		System.out.println(f.exists());

		System.out.println(System.getProperty("java.home"));
		long times = System.currentTimeMillis();
		System.out.println(times);
	}

	public void astvisitor_textx1() {
		IDocument pdocument = new Document(FileUtil.ReadFromFile(new File("test_examples/TestUtil.java")));
		CompilationUnit cu = JDTUtil.parseSourceCode("TestUtil.java", pdocument, null);
		cu.accept(new ASTVisitor() {

			@Override
			public void endVisit(MethodInvocation node) {
				Expression expr = node.getExpression();
				if (expr instanceof Name) {
					Name name = (Name) expr;
					IBinding ib = name.resolveBinding();
					if (ib instanceof ITypeBinding) {
						ITypeBinding tb = (ITypeBinding) ib;
						System.out
								.println("debugging:MethodInvocation:" + node + ";ITypeBinding:" + tb.getQualifiedName()
										+ ";IFSelfIsType:" + tb.isTypeVariable() + ";IsClass:" + tb.isClass());
					}
				}
				// debugging
				// System.out.println("debugging:MethodInvocation:" + node +
				// ";ITypeBinding:" + itb.getQualifiedName() + ";IFSelfIsType:"
				// + is_just_type + ";IsClass:" + is_class);
			}

		});
	}

	public void astvisitor_textx2() {
		IDocument pdocument = new Document(FileUtil.ReadFromFile(new File("test_examples/TestUtil.java")));
		CompilationUnit cu = JDTUtil.parseSourceCode("TestUtil.java", pdocument, null);
		cu.recordModifications();
		final ASTRewrite rewrite = ASTRewrite.create(cu.getAST());

		cu.accept(new ASTVisitor() {
			@Override
			public void endVisit(ParameterizedType node) {
				rewrite.replace(node, node.getType(), null);
				// return super.visit(node);
			}
		});

		TextEdit edits = rewrite.rewriteAST(pdocument, null);
		try {
			edits.apply(pdocument);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		CompilationUnit modified_cu = JDTUtil.parseSourceCode("TestUtil.java", pdocument, null);
		System.out.println("CompilationUnit:" + modified_cu);
	}

	public static void main(String[] args) {
		TestJDT tu = new TestJDT();
		tu.common_testx1();
		tu.astvisitor_textx1();
		tu.astvisitor_textx2();
	}

}
