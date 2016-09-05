package analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JProgressBar;

public class SearchThreadManager {

	private Queue<String> filesToSearch;
	private Map<String, Set<String>> resultMap;

	private JProgressBar progressBar;

	public SearchThreadManager(String searchLocation) {
		filesToSearch = new ConcurrentLinkedQueue<>();
		resultMap = new ConcurrentHashMap<>();
		File file = new File(searchLocation);
		if (file.exists()) {
			if (file.isDirectory()) {
				browseFiles(file.listFiles());
			} else {
				filesToSearch.add(searchLocation);
			}
		}
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
		progressBar.setMinimum(0);
		progressBar.setMaximum(filesToSearch.size());
	}

	private void browseFiles(File[] files) {
		for (File file : files) {
			if (file.isDirectory()) {
				browseFiles(file.listFiles());
			} else {
				if (file.getPath().toUpperCase().endsWith(".SQL"))
					filesToSearch.add(file.getPath());
			}
		}
	}

	public void search(List<String> andTableConstraints, List<String> orTableConstraints,
			List<String> andColumnConstraints, List<String> orColumnConstraints, List<String> queryTypeConstraints) {
		long time = System.currentTimeMillis();
		int cores = Runtime.getRuntime().availableProcessors();

		List<Thread> searchThreads = new ArrayList<>();
		for (int i = 0; i < cores; ++i) {
			searchThreads.add(new Thread(new SearchThread(andTableConstraints, orTableConstraints, andColumnConstraints,
					orColumnConstraints, queryTypeConstraints, i)));
		}
		for (Thread thread : searchThreads) {
			thread.start();
		}

		for (Thread thread : searchThreads) {
			System.out.println("joining");
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("done in " + (System.currentTimeMillis() - time) + " ms.");

	}

	public Map<String, Set<String>> getSearchResults() {
		return resultMap;
	}

	private class SearchThread implements Runnable {
		List<String> andTableConstraints;
		List<String> orTableConstraints;
		List<String> andColumnConstraints;
		List<String> orColumnConstraints;
		List<String> queryTypeConstraints;
		int id;

		public SearchThread(List<String> andTableConstraints, List<String> orTableConstraints,
				List<String> andColumnConstraints, List<String> orColumnConstraints, List<String> queryTypeConstraints,
				int id) {
			this.andTableConstraints = andTableConstraints;
			this.orTableConstraints = orTableConstraints;
			this.andColumnConstraints = andColumnConstraints;
			this.orColumnConstraints = orColumnConstraints;
			this.queryTypeConstraints = queryTypeConstraints;
			this.id = id;
		}

		@Override
		public void run() {
			String currentFile = filesToSearch.poll();
			while (currentFile != null) {
				System.out.println(id + " - " + filesToSearch.size() + " - " + currentFile);
				try {
					Searcher manager = new Searcher(currentFile);
					manager.setTableAndConstraints(andTableConstraints);
					manager.setTableOrConstraints(orTableConstraints);
					manager.setColumnAndConstraints(andColumnConstraints);
					manager.setColumnOrConstraints(orColumnConstraints);
					manager.setQueryTypeConstraints(queryTypeConstraints);
					if (manager.analyze()) {
						for (String entry : manager.getQueryTypes()) {
							if (resultMap.get(entry) == null) {
								resultMap.put(entry, new LinkedHashSet<String>());
							}
							resultMap.get(entry).add(currentFile);
						}
					}
					if (progressBar != null){
						int newValue = progressBar.getMaximum() - filesToSearch.size();
						progressBar.setStringPainted(true);
						progressBar.setString(newValue + " / " + progressBar.getMaximum());
						progressBar.setValue(newValue);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				currentFile = filesToSearch.poll();
			}

	}
}

}
