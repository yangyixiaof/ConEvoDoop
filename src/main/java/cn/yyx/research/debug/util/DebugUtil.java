package cn.yyx.research.debug.util;

import java.util.Iterator;
import java.util.List;

public class DebugUtil {
	
	public static void PrintList(List<String> class_paths)
	{
		System.out.println("=============== Start printing class paths ===============");
		Iterator<String> citr = class_paths.iterator();
		while (citr.hasNext())
		{
			String class_path = citr.next();
			System.out.println(class_path);
		}
		System.out.println("=============== End printing class paths ===============");
	}
	
}
