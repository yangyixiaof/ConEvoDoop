package cn.yyx.research.util;

import java.io.File;

// import cn.yyx.research.labtask.analysis.jdtutil.JDTUtil;

public class TestUtil {
	
	public void common_testx1()
	{
		List<List<List<List<String>>>> list_n = new List<List<List<List<String>>>>();
		System.out.println(list_n);
		List<String> list = new LinkedList<String>();
		System.out.println(list);
		String[] ag1 = {"12", "34"};
		String[] ag2 = { };
		System.out.println(String.join(" ", ag1));
		System.out.println(String.join(" ", ag2));
		
		System.out.println(File.pathSeparator);
		
		File f = new File("D:/eclipse-workspace-pool/eclipse-rcp-neon/rvpredict_benchmarks/target/benchmark-0.0.1-SNAPSHOT.jar");
		System.out.println(f.exists());
		
		System.out.println(System.getProperty("java.home"));
		long times = System.currentTimeMillis();
		System.out.println(times);
	}
	
//	public void astvisitor_textx1()
//	{
//		JDTUtil.parseSourceCode("", pdocument, classpaths);
//	}
	
	public static void main(String[] args) {
		TestUtil tu = new TestUtil();
		tu.common_testx1();
//		tu.astvisitor_textx1();
	}
	
}
