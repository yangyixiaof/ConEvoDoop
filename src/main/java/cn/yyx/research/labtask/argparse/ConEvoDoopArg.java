package cn.yyx.research.labtask.argparse;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.yyx.research.labtask.resource.FileUtil;

public class ConEvoDoopArg {
	
	private String arg = "";
	private String classpath = "";
	private Set<Integer> excluede_arg_index = new TreeSet<Integer>();
	private int timelimit = 60;
	private List<String> classes = new LinkedList<String>();
	
	public ConEvoDoopArg(String[] args) {
		for (int i=0;i<args.length;i++)
		{
			String onearg = args[i];
			if (onearg.equals("gentests"))
			{
				excluede_arg_index.add(i);
			}
			if (onearg.endsWith("-classpath"))
			{
				excluede_arg_index.add(i);
				excluede_arg_index.add(i+1);
				setClasspath(args[i+1]);
			}
			if (onearg.startsWith("--no-error-revealing-tests=") || onearg.startsWith("--no-regression-tests=") || onearg.startsWith("--no-regression-assertions="))
			{
				excluede_arg_index.add(i);
			}
			if (onearg.startsWith("--testclass="))
			{
				classes.add(onearg.substring("--testclass=".length()).trim());
			}
			if (onearg.startsWith("--classlist="))
			{
				String filepath = onearg.substring("--classlist=".length()).trim();
				classes.addAll(FileUtil.ReadFromFileToList(new File(filepath)));
			}
			if (onearg.startsWith("--timelimit="))
			{
				try {
					setTimelimit(Integer.parseInt(onearg.substring("--timelimit=".length()).trim()));
				} catch (Exception e) {
				}
			}
		}
		String[] targs = new String[args.length - excluede_arg_index.size()];
		int idx = 0;
		for (int i=0;i<args.length;i++)
		{
			if (!excluede_arg_index.contains(i))
			{
				targs[idx] = args[i];
				idx++;
			}
		}
		String temp_arg = String.join(" ", targs);
		setArg(temp_arg);
	}
	
	public String getArg() {
		return arg;
	}

	private void setArg(String arg) {
		this.arg = arg;
	}

	public Iterator<String> IterateClass() {
		return classes.iterator();
	}
	
	public int ClassSize()
	{
		return classes.size();
	}
	
	public int getTimelimit() {
		return timelimit;
	}

	private void setTimelimit(int timelimit) {
		this.timelimit = timelimit;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	
}
