package cn.yyx.research.labtask.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class DisplayInfo implements Runnable {
	
	public static final int MaxCount = 25;
	
	protected InputStream is = null;
	protected PrintStream ps = null;
	
	protected List<DisplayTask> handles = new LinkedList<DisplayTask>();
	
	public DisplayInfo(PrintStream out) {
		this.ps = out;
	}
	
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String oneline = null;
		try {
			while ((oneline = br.readLine()) != null)
			{
				oneline = oneline.trim();
				HandleInformation(oneline);
				ps.println(oneline);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("Thread " + ps.getClass() + " Over!");
		SystemStreamUtil.Flush();
	}
	
	public void setIs(InputStream is) {
		this.is = is;
	}
	
	private void HandleInformation(String oneline)
	{
		// do nothing.
		for (DisplayTask dt : handles)
		{
			dt.run(oneline);
		}
	}
	
	public void RegisterOneTask(DisplayTask one_task)
	{
		handles.add(one_task);
	}
	
}
