package cn.yyx.research.labtask.consevodoop;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import cn.yyx.research.debug.util.DebugUtil;
import cn.yyx.research.labtask.analysis.jdtutil.JDTUtil;
import cn.yyx.research.labtask.analysis.topology.TopologyDotGraph;
import cn.yyx.research.labtask.analysis.topology.TopologyGenerator;
import cn.yyx.research.labtask.argparse.ConEvoDoopArg;
import cn.yyx.research.labtask.resource.ResourceUtil;
import cn.yyx.research.labtask.system.DisplayInfo;
import cn.yyx.research.labtask.system.EvoSuiteProgressTask;
import cn.yyx.research.labtask.system.RunProcess;
import cn.yyx.research.util.FileIterator;
import cn.yyx.research.util.FileUtil;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		ConEvoDoopArg ceda = new ConEvoDoopArg(args);
//		{
//			// --classlist=myclasses.txt --timelimit=60
//			ResourceUtil.InitialEnvironment();
//			String randoop_classpath = ceda.getClasspath() + File.pathSeparator + ResourceUtil.Randoop_All;
//			String randoop_cmd = ("java -classpath " + randoop_classpath
//					+ " randoop.main.Main gentests --junit-output-dir=randoop-tests --no-regression-assertions=true "
//					+ ceda.getArg()).trim();
//			System.err.println("Randoop Command:" + randoop_cmd);
//			RunProcess.RunOneProcess(randoop_cmd, new DisplayInfo(System.out), new DisplayInfo(System.err),
//					ceda.getTimelimit());
//
//			Iterator<String> itr = ceda.IterateClass();
//			String evosuite_classpath = ceda.getClasspath();
//
//			while (itr.hasNext()) {
//				String cls = itr.next();
//				String evosuite_cmd = "java -jar " + ResourceUtil.Evosuite_Master + " -Dassertions=false" + " -class "
//						+ cls + " -projectCP " + evosuite_classpath;
//				DisplayInfo dout = new DisplayInfo(System.out);
//				dout.RegisterOneTask(new EvoSuiteProgressTask());
//				DisplayInfo derr = new DisplayInfo(System.out);
//				derr.RegisterOneTask(new EvoSuiteProgressTask());
//				RunProcess.RunOneProcess(evosuite_cmd, dout, derr, -1);
//			}
//		}

		// TODO
		{
			TopologyDotGraph.InitialDotGraphDirectory();
			FileIterator fi = new FileIterator("randoop-tests", "RegressionTest[0-9]+\\.java$");
			Iterator<File> fitr = fi.EachFileIterator();
			while (fitr.hasNext())
			{
				File f = fitr.next();
				// System.err.println(f.getAbsolutePath());
				List<String> class_paths = Arrays.asList((ceda.getClasspath() + File.pathSeparator + ResourceUtil.Evosuite_Runtime).split(File.pathSeparator));
				// debugging.
				DebugUtil.PrintList(class_paths);
				IDocument pdocument = new Document(FileUtil.ReadFromFile(f));
				CompilationUnit cu = JDTUtil.parseSourceCode(f.getName(), pdocument, class_paths);
				@SuppressWarnings("unchecked")
				List<AbstractTypeDeclaration> types = cu.types();
				for (AbstractTypeDeclaration atd : types)
				{
					
					// testing.
					// System.err.println("TypeDeclarationType:" + atd);
					
					if (atd instanceof TypeDeclaration)
					{
						MethodDeclaration[] methods = ((TypeDeclaration) atd).getMethods();
						for (MethodDeclaration method : methods)
						{
							TopologyGenerator topo = new TopologyGenerator();
							method.accept(topo);
							String fname = f.getName();
							TopologyDotGraph tdg = new TopologyDotGraph(fname.substring(0, fname.lastIndexOf(".java")) + "_" + method.getName().toString(), topo.TopologyRoots());
							tdg.GenerateAllPath();
						}
					}
				}
				
			}
		}
		
		ResourceUtil.ClearEnvironment();
	}

}
