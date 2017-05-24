package br.univali.bipost;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.univali.bipost.ui.EditorFrame;
import br.univali.bipost.ui.LinkDialog;
import br.univali.bipost.ui.SimulationViewer;

public class Program {
	private static final String CREATION_TOKEN = "task_creation";
	private static final String INCLUSION_TOKEN = "task_inclusion";
	public static final int MAX_TASKS = 16;
	private EditorFrame editorPanel;
	private LinkDialog linkPanel;
	private String osSource;
	
	public Program() {
		editorPanel = new EditorFrame();
		linkPanel = editorPanel.getLinkPanel();
		
		linkPanel.setOnStart(() -> {			
			List<Task> tasks = editorPanel.getTasks();
			
			if (tasks.size() == 0) {
				linkPanel.publishMessage("FAILED: task list is empty.");
				linkPanel.notifyResult(false);
				return;
			}
			
			linkPanel.publishMessage("Reading the source code of the Operating System...");
			
			try {
				osSource = readEntireFile(linkPanel.getPath(), "\n");				
			} catch (IOException e) {
				linkPanel.publishMessage("FAILED: could not read the source code.");
				return;
			}
			
			linkPanel.publishMessage("Source code successfully read. Verifying...");
			
			if (!checkToken(osSource, CREATION_TOKEN)) return;
			if (!checkToken(osSource, INCLUSION_TOKEN)) return;
			
			linkPanel.publishMessage("Source code verified; all tokens have been found.");
			
			String suffix = String.format(" task%s.", tasks.size() != 1 ? "s" : "");
			linkPanel.publishMessage("Starting the linking procedure with " + tasks.size() + suffix);
			
			for (Task task : tasks) {
				linkPanel.publishMessage("Verifying task " + task.getName() + "...");
				
				String source = task.getSourceCode();
				if (!checkToken(source, Task.BEGIN_TOKEN)) return;
				if (!checkToken(source, Task.END_TOKEN)) return;
			}
			
			linkPanel.publishMessage("Tasks successfully verified.");
			linkPanel.publishMessage("Beginning the replacing procedure...");
			
			List<ProcessedTask> processedTasks = preprocessTasks(tasks);
			
			linkPanel.publishMessage("Tasks successfully processed.");
			linkPanel.publishMessage("Inserting the source code of the tasks into the Operating System...");
			
			String inclusionSource = processedTasks.stream()
					.map(ProcessedTask::getSourceCode)
					.collect(Collectors.joining("\n\n"));
			
			osSource = replaceToken(osSource, INCLUSION_TOKEN, inclusionSource);
			
			linkPanel.publishMessage("Done.");
			linkPanel.publishMessage("Inserting the task creation code into the Operating System...");
			
			StringBuilder creationSource = new StringBuilder();
			for (ProcessedTask task : processedTasks) {
				String priority = String.format("0x%04X", task.getPriority() & 0xFFFF);
				
				creationSource.append("\tLDI " + task.getBeginLabel() + "\n");
				creationSource.append("\tSTO $arg1\n");
				creationSource.append("\tLDI " + task.getEndLabel() + "\n");
				creationSource.append("\tSTO $arg2\n");
				creationSource.append("\tLDI " + priority + "\n");
				creationSource.append("\tSTO $arg3\n");
				creationSource.append("\tCALL OS_TSK_CREATE\n");
			}
			
			osSource = replaceToken(osSource, CREATION_TOKEN, creationSource.toString());
			
			linkPanel.publishMessage("Done.");
			linkPanel.publishMessage("Successfully linked the tasks with the Operating System.");
			linkPanel.publishMessage("Result: " + osSource.length() + " characters in " + countLines(osSource) + " lines.");
			linkPanel.notifyResult(true);
		});
		
		linkPanel.setOnExport((file) -> {
			try (BufferedWriter w = Files.newBufferedWriter(file.toPath())) {
				w.write(osSource);
				linkPanel.publishMessage("Source code written successfully!");
			} catch (IOException e) {
				e.printStackTrace();
				linkPanel.publishMessage("ERROR: could not write the source code.");
			}
		});
		
		editorPanel.setVisible(true);
	}
	
	private int countLines(String s) {
		Matcher m = Pattern.compile("\r\n|\r|\n").matcher(s);
		int lines = 1;
		while (m.find()) {
		    lines++;
		}
		
		return lines;
	}

	private boolean checkToken(String source, String token) {
		token = "${" + token + "}";
		
		if (source.contains(token)) {
			linkPanel.publishMessage("Found \"" + token + "\" token.");
			return true;
		} else {
			linkPanel.publishMessage("FAILED: source code is missing the \"" + token + "\" token.");
			return false;
		}
	}
	
	private static String readEntireFile(String path, String delimiter) throws IOException {
		return Files.lines(Paths.get(path)).collect(Collectors.joining(delimiter));
	}

	private String replaceToken(String text, String token, String replacement) {
		return text.replace("${" + token + "}", replacement);
	}
	
	private List<ProcessedTask> preprocessTasks(List<Task> tasks) {
		Collections.sort(tasks);
		Collections.reverse(tasks);
		
		List<ProcessedTask> result = new ArrayList<>();
		
		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			
			String init = String.format("INIT_TSK_P%d_%d", task.getPriority(), i);
			String end = String.format("END_TSK_P%d_%d", task.getPriority(), i);
			
			String source = task.getSourceCode();
			source = replaceToken(source, Task.BEGIN_TOKEN, init + ":");
			source = replaceToken(source, Task.END_TOKEN, end + ":");
			
			result.add(new ProcessedTask(task.getName(), source, task.getPriority(), init, end));
			
			linkPanel.publishMessage("Task " + task.getName() + " processed. Details:");
			linkPanel.publishMessage("- Begin label: " + init);
			linkPanel.publishMessage("- End label: " + end);
			linkPanel.publishMessage("- Priority: " + task.getPriority());
		}
		
		return result;
	}
	
	public static void main(String[] args) {
//		System.setProperty("awt.useSystemAAFontSettings","on");
//		System.setProperty("swing.aatext", "true");
		
		SwingUtilities.invokeLater(() -> {
//			try {
//				Simulation s = new Simulation("C:/Users/Fernando/Dropbox/BIPOS/sim_result.txt");
//				SystemSource os = new SystemSource("C:/Users/Fernando/Dropbox/BIPOS/bipos.s");

				new SimulationViewer().setVisible(true);
				
//				for (int i : s.getIndices()) {
//					System.out.println(os.getLine(i).getText());
//				}
//			} catch (IOException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			new Program();
		});
	}
}
