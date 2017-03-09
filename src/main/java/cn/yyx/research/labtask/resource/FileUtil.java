package cn.yyx.research.labtask.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.yyx.research.labtask.system.EnvironmentUtil;

public class FileUtil {
	
	public static List<String> ReadFromFileToList(File f) {
		// System.err.println("unprinted file path:" + f.getAbsolutePath());
		List<String> contents = new LinkedList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String tmp = null;
			while ((tmp = reader.readLine()) != null) {
				contents.add(tmp);
			}
			reader.close();
			reader = null;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return contents;
	}
	
	public static String ReadFromFile(File f) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			StringBuilder content = new StringBuilder();
			String tmp = null;
			while ((tmp = reader.readLine()) != null) {
				content.append(tmp);
				content.append("\n");
			}
			reader.close();
			reader = null;
			String source = content.toString();
			return source;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public static void WriteToFile(String filename, String filecontent, String directory) {
		if (directory == null) {
			directory = "";
		}

		String filepath = directory + "/" + filename;
		if (directory.endsWith("/") || directory.endsWith("\\")) {
			filepath = directory + filename;
		}
		try {
			if (!directory.equals(""))
			{
				File diret = new File(directory);
				if (!diret.exists())
				{
					diret.mkdirs();
				}
			}
			File f = new File(filepath);
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(filecontent);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("There are errors in creating files or directories.");
			System.exit(1);
		}
	}

	public static void AppendToFile(String filepath, List<String> contents) {
		File f = new File(filepath);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			Iterator<String> itr = contents.iterator();
			while (itr.hasNext()) {
				String oneline = itr.next();
				bw.write(oneline);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void ReadFromStreamAndWriteToFile(InputStream is, String filename) {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			File dst = new File(filename);
			if (dst.exists())
			{
				dst.delete();
			}
			dst.createNewFile();
			if (EnvironmentUtil.IsWindows())
			{
				try {
					String scmd = "attrib +H \"" + dst.getAbsolutePath() + "\"";
					Runtime.getRuntime().exec(scmd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			in = new BufferedInputStream(is);
			out = new BufferedOutputStream(new FileOutputStream(dst, true));

			byte[] b = new byte[1024];
			int num_bytes = 0;
			while ((num_bytes = in.read(b)) >= 0) {
				out.write(b, 0, num_bytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
			{
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null)
			{
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (!file.exists()) {
			return flag;
		} else {
			if (file.isFile()) {
				return DeleteFile(sPath);
			} else {
				return DeleteDirectory(sPath);
			}
		}
	}

	public static boolean DeleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static boolean DeleteDirectory(String sPath) {
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = DeleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = DeleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

}
