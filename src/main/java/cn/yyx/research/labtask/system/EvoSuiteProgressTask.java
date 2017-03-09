package cn.yyx.research.labtask.system;

public class EvoSuiteProgressTask implements DisplayTask {
	
	boolean firsted = false;
	
	public void run(String one_line) {
		if (one_line.startsWith("[Progress:>"))
		{
			if (firsted)
			{
//				for (int j = 0; j <= one_line.length(); j++) {
//					System.out.print('\b');
//				}
			}
			if (!firsted)
			{
				firsted = !firsted;
			}
		}
	}

}
