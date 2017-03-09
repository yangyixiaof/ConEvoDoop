package cn.yyx.research.labtask.dot;

import java.io.File;
import java.util.Iterator;

import cn.yyx.research.labtask.system.DisplayInfo;
import cn.yyx.research.labtask.system.RunProcess;
import cn.yyx.research.util.FileIterator;

public class DotView {
	
	public static final String dot_pics_directory = "dot_pics";
	public static final String dot_path = "D:/UnspaceInstallFile/graphviz-2.38/release/bin/dot.exe";
	public static final String dot_file_path = "randoop-tests-dot/RegressionTest8_test1.dot";
	public static final String dot_file_directory = "randoop-tests-dot";
	
	public void HandleSpecificDot()
	{
		String cmd = dot_path + " -Tjpg " + dot_file_path + " -o " + dot_pics_directory + "/test.jpg";
		System.out.println(new File("").getAbsolutePath());
		System.out.println(cmd);
		RunProcess.RunOneProcess(cmd, new DisplayInfo(System.out), new DisplayInfo(System.err), 60);
	}
	
	public void HandleAllDotsInDirectory()
	{
		FileIterator fi = new FileIterator(dot_file_directory, ".*\\.dot$");
		Iterator<File> fitr = fi.EachFileIterator();
		while (fitr.hasNext())
		{
			File f = fitr.next();
			System.out.print("Handling " + f.getName() + " ......");
			String fname = f.getName();
			String dotname = fname.substring(0, fname.lastIndexOf(".dot"));
			String cmd = dot_path + " -Tjpg " + f.getAbsolutePath() + " -o " + dot_pics_directory + "/" + dotname + ".jpg";
			RunProcess.RunOneProcess(cmd, new DisplayInfo(System.out), new DisplayInfo(System.err), 60);
			System.out.println(" successfully");
		}
	}
	
	public static void main(String[] args) {
		DotView dv = new DotView();
		dv.HandleAllDotsInDirectory();
	}
	
}
