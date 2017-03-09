package cn.yyx.research.labtask.analysis.jdtutil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.IDocument;

public class JDTUtil {

	public static CompilationUnit parseSourceCode(String identifier, IDocument pdocument, List<String> classpaths) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		String[] sources = { };
		String[] encodings = new String[] { }; // "UTF-8"
		String javahome = System.getProperty("java.home");
		List<String> fpaths = new LinkedList<String>();
		fpaths.add(javahome+"/lib/resources.jar");
		fpaths.add(javahome+"/lib/rt.jar");
		fpaths.add(javahome+"/lib/jsse.jar");
		fpaths.add(javahome+"/lib/jce.jar");
		fpaths.add(javahome+"/lib/charsets.jar");
		fpaths.add(javahome+"/lib/jfr.jar");
		fpaths.add(javahome+"/lib/access-bridge-64.jar");
		fpaths.add(javahome+"/lib/cldrdata.jar");
		fpaths.add(javahome+"/lib/dnsns.jar");
		fpaths.add(javahome+"/lib/jaccess.jar");
		fpaths.add(javahome+"/lib/jfxrt.jar");
		fpaths.add(javahome+"/lib/localedata.jar");
		fpaths.add(javahome+"/lib/nashorn.jar");
		fpaths.add(javahome+"/lib/sunec.jar");
		fpaths.add(javahome+"/lib/sunjce_provider.jar");
		fpaths.add(javahome+"/lib/sunmscapi.jar");
		fpaths.add(javahome+"/lib/sunpkcs11.jar");
		fpaths.add(javahome+"/lib/zipfs.jar");
		if (classpaths != null)
		{
			fpaths.addAll(classpaths);
		}
		String[] classpath = new String[fpaths.size()];
		fpaths.toArray(classpath);
		parser.setEnvironment(classpath, sources, encodings, true);
		
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setUnitName(identifier);
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);
		parser.setSource(pdocument.get().toCharArray());
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		return compilationUnit;
	}

}
