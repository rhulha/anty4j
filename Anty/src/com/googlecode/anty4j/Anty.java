package com.googlecode.anty4j;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public class Anty
{

	private final String srcDirStr;
	private final String libDirStr[];
	private final String destDirStr;

	public Anty(String srcDir, String libDir[], String destDir)
	{
		this.srcDirStr = srcDir;
		this.libDirStr = libDir;
		this.destDirStr = destDir;
	}

	public void build()
	{
		Project project = getProject();
		Target target = getTarget(project, "default");

		Javac javacTask = new Javac();
		Path srcDir = new Path(project, srcDirStr);

		javacTask.setSrcdir(srcDir);
		javacTask.setSource("1.5");
		javacTask.setTarget("1.5");

		File destDir = new File(destDirStr);
		// mkdir destDir
		javacTask.setDestdir(destDir);

		Path libPath = new Path(project);
		for (int i = 0; i < libDirStr.length; i++)
		{
			String lds = libDirStr[i];
			FileSet fs = getIncludingFileSet(project, lds, "**/*.jar");
			libPath.addFileset(fs);
		}
		javacTask.setClasspath(libPath);

		linkTask(project, target, javacTask);

		Copy copyTask = new Copy();
		copyTask.setTodir(destDir);
		copyTask.addFileset(getExcludingFileSet(project, srcDirStr, "**/*.java"));

		linkTask(project, target, copyTask);

		project.executeTarget("default");

		System.out.println("Done.");
	}

	public static void main(String[] args)
	{
		if (args.length < 2)
		{
			System.out.println("Usage: java -jar Anty.jar <java src dir> <classes dest dir> [libDir1] [libDirN]");
			System.out.println("This programm uses http://www.apache.org/licenses/LICENSE-2.0.txt");
		} else
		{
			List<String> asList = Arrays.asList(args);
			String srcDirStr = asList.remove(0);
			;
			String destDirStr = asList.remove(0);
			;

			Anty a = new Anty(srcDirStr, asList.toArray(new String[0]), destDirStr);
			a.build();
		}
	}

	public static FileSet getIncludingFileSet(Project project, String dir, String include)
	{
		File libDir = new File(dir);

		FileSet fs = new FileSet();
		fs.setDir(libDir);
		fs.setIncludes(include);

		return fs;
	}

	public static FileSet getExcludingFileSet(Project project, String dir, String exclude)
	{
		File libDir = new File(dir);

		FileSet fs = new FileSet();
		fs.setDir(libDir);
		fs.setExcludes(exclude);

		return fs;
	}

	public static Project getProject()
	{
		Project project = new Project();
		project.init();
		return project;
	}

	public static Target getTarget(Project p, String name)
	{
		Target tar = new Target();
		tar.setName(name);
		p.addTarget(name, tar);
		tar.setProject(p);
		return tar;
	}

	public static void linkTask(Project p, Target tar, Task tas)
	{
		tas.init();
		tas.setOwningTarget(tar);
		tas.setProject(p);
		tar.addTask(tas);

	}

}
