package cn.yyx.research.labtask.system;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
// import java.util.Map;

public class RunProcess {
	
	public static void RunOneProcess(String cmd, DisplayInfo out, DisplayInfo err, int maxSeconds) {
		try {
			List<String> commands = new LinkedList<String>();
			if (EnvironmentUtil.IsWindows()) {
				commands.add("cmd");
				commands.add("/c");
				String[] cmds = cmd.split(" ");
				for (int i=0;i<cmds.length;i++) {
					commands.add(cmds[i]);
				}
			} else {
				commands.add("sh");
				commands.add("-c");
				commands.add(cmd);
			}
			ProcessBuilder pb = new ProcessBuilder(commands); // "java", "-jar",
															// "Test3.jar"
			// pb.directory(new File("F:\\dist"));
			// Map<String, String> map = pb.environment();
			
			Process process = pb.start();
			InputStream is = process.getInputStream();
			out.setIs(is);
			InputStream es = process.getErrorStream();
			err.setIs(es);
			Thread t1 = new Thread(out);
			t1.start();
			Thread t2 = new Thread(err);
			t2.start();
			if (maxSeconds <= 0) {
				process.waitFor();
				t1.join();
				t2.join();
			} else {
				int totalSeconds = 0;
				while (process.isAlive() && totalSeconds < maxSeconds)
				{
					totalSeconds += 5;
					try {
						Thread.sleep(totalSeconds * 1000);
					} catch (Exception e) {
					}
				}
				process.destroyForcibly();
				t1.join(1000);
				t2.join(1000);
			}
			Thread.sleep(1000);
			SystemStreamUtil.Flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
