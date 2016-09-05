package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import analysis.Processor;
import constants.SQLQueryTypes;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8284558086230714735L;

	private static final String SEARCH_LOCATION = "SearchLocation";
	private static final String SAVE_LOCATION = "SaveLocation";
	private static final String AND_TABLES = "AndTables";
	private static final String AND_COLUMNS = "AndColumns";
	private static final String OR_TABLES = "OrTables";
	private static final String OR_COLUMNS = "OrColumns";
	private static final String EQUALS = "=";
	private static final String SAVED_CRITERIA = "Previous_Session.prop";

	private int WIDTH = 800;
	private int HEIGHT = 600;

	JTextArea searchFolder;
	JTextArea outputFolder;
	JTextArea requiredTables;
	JTextArea optionalTables;
	JTextArea requiredColumns;
	JTextArea optionalColumns;

	JCheckBox select;
	JCheckBox update;
	JCheckBox insert;
	JCheckBox create;
	JCheckBox alter;
	JCheckBox delete;
	JCheckBox drop;

	JProgressBar progressBar;

	public MainFrame() {
		this("");
	}

	public MainFrame(String title) {
		super(title);
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		GridLayout textLayout = new GridLayout(0, 2);

		JPanel text = new JPanel();
		text.setPreferredSize(new Dimension(WIDTH, 3 * HEIGHT / 4));
		text.setLayout(textLayout);
		UICriteria previousSession = null;
		try {
			previousSession = readPreviousCriteria();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		searchFolder = new JTextArea(previousSession.getSearchFolder());
		searchFolder.setLineWrap(true);
		outputFolder = new JTextArea(previousSession.getOutputFolder());
		outputFolder.setLineWrap(true);
		requiredTables = new JTextArea(previousSession.getAndTables());
		requiredTables.setLineWrap(true);
		optionalTables = new JTextArea(previousSession.getOrTables());
		optionalTables.setLineWrap(true);
		requiredColumns = new JTextArea(previousSession.getAndColumns());
		requiredColumns.setLineWrap(true);
		optionalColumns = new JTextArea(previousSession.getOrColumns());
		optionalColumns.setLineWrap(true);

		select = new JCheckBox(SQLQueryTypes.SELECT);
		select.setSelected(previousSession.isSelectQuery());
		update = new JCheckBox(SQLQueryTypes.UPDATE);
		update.setSelected(previousSession.isUpdateQuery());
		insert = new JCheckBox(SQLQueryTypes.INSERT);
		insert.setSelected(previousSession.isInsertQuery());
		create = new JCheckBox(SQLQueryTypes.CREATE);
		create.setSelected(previousSession.isCreateQuery());
		alter = new JCheckBox(SQLQueryTypes.ALTER);
		alter.setSelected(previousSession.isAlterQuery());
		delete = new JCheckBox(SQLQueryTypes.DELETE);
		delete.setSelected(previousSession.isDeleteQuery());
		drop = new JCheckBox(SQLQueryTypes.DROP);
		drop.setSelected(previousSession.isDropQuery());

		text.add(getJFileChooserButton("Search Folder", searchFolder));
		text.add(searchFolder);
		text.add(getJFileChooserButton("Search Output Folder", outputFolder));
		text.add(outputFolder);
		text.add(getDisabledButton("Tables required (all required):"));
		text.add(getDisabledButton("Columns required (all required):"));
		text.add(requiredTables);
		text.add(requiredColumns);
		text.add(getDisabledButton("Tables required (at least one required):"));
		text.add(getDisabledButton("Columns required (at least one required):"));
		text.add(optionalTables);
		text.add(optionalColumns);

		JPanel queryCheckBoxes = new JPanel();
		queryCheckBoxes.add(select);
		queryCheckBoxes.add(update);
		queryCheckBoxes.add(insert);
		queryCheckBoxes.add(create);
		queryCheckBoxes.add(alter);
		queryCheckBoxes.add(delete);
		queryCheckBoxes.add(drop);

		this.getContentPane().add(text, BorderLayout.NORTH);
		this.getContentPane().add(queryCheckBoxes, BorderLayout.CENTER);
		JButton searchButton = new JButton("SEARCH");
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				final UICriteria criteria = generateCriteria();
				StringBuilder sb = new StringBuilder();
				sb.append(SEARCH_LOCATION + EQUALS + criteria.getSearchFolder() + "\n");
				sb.append(SAVE_LOCATION + EQUALS + criteria.getOutputFolder() + "\n");
				sb.append(AND_TABLES + EQUALS + criteria.getAndTables() + "\n");
				sb.append(AND_COLUMNS + EQUALS + criteria.getAndColumns() + "\n");
				sb.append(OR_TABLES + EQUALS + criteria.getOrTables() + "\n");
				sb.append(OR_COLUMNS + EQUALS + criteria.getOrColumns() + "\n");

				sb.append(SQLQueryTypes.SELECT + EQUALS + criteria.isSelectQuery() + "\n");
				sb.append(SQLQueryTypes.UPDATE + EQUALS + criteria.isUpdateQuery() + "\n");
				sb.append(SQLQueryTypes.INSERT + EQUALS + criteria.isInsertQuery() + "\n");
				sb.append(SQLQueryTypes.CREATE + EQUALS + criteria.isCreateQuery() + "\n");
				sb.append(SQLQueryTypes.ALTER + EQUALS + criteria.isAlterQuery() + "\n");
				sb.append(SQLQueryTypes.DELETE + EQUALS + criteria.isDeleteQuery() + "\n");
				sb.append(SQLQueryTypes.DROP + EQUALS + criteria.isDropQuery() + "\n");

				PrintWriter writer = null;
				try {
					String fileName = System.getProperty("user.dir") + File.separator + SAVED_CRITERIA;
					File file = new File(fileName);
					if (!file.exists()) {
						file.getParentFile().mkdirs();
						file.createNewFile();
					}
					writer = new PrintWriter(fileName);
					writer.println(sb.toString());
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if (writer != null) {
						writer.close();
					}
				}

				SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {

					String outputFile;

					@Override
					protected Integer doInBackground() throws Exception {
						try {
							outputFile = new Processor().performSearch(criteria);
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						return null;
					}

					@Override
					protected void done() {
						try {
							Desktop.getDesktop().open(new File(outputFile));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				};
				worker.execute();
			}
		});
		progressBar = getProgressBar();

		JPanel footer = new JPanel(new GridLayout(0, 1));
		footer.add(searchButton, BorderLayout.CENTER);
		footer.add(progressBar, BorderLayout.SOUTH);
		this.getContentPane().add(footer, BorderLayout.SOUTH);

		this.setVisible(true);

	}

	private JProgressBar getProgressBar() {
		JProgressBar result = new JProgressBar();
		result.setStringPainted(true);
		result.setString("Progress");
		return result;
	}

	private JButton getDisabledButton(String str) {
		JButton button = new JButton(str);
		button.setEnabled(false);
		return button;
	}

	private UICriteria generateCriteria() {
		UICriteria result = new UICriteria();
		result.setSearchFolder(searchFolder.getText());
		result.setOutputFolder(outputFolder.getText());
		result.setAndTables(requiredTables.getText());
		result.setAndColumns(requiredColumns.getText());
		result.setOrTables(optionalTables.getText());
		result.setOrColumns(optionalColumns.getText());
		result.setSelectQuery(select.isSelected());
		result.setUpdateQuery(update.isSelected());
		result.setInsertQuery(insert.isSelected());
		result.setCreateQuery(create.isSelected());
		result.setAlterQuery(alter.isSelected());
		result.setDeleteQuery(delete.isSelected());
		result.setDropQuery(drop.isSelected());
		result.setProgressBar(progressBar);
		return result;
	}

	private UICriteria readPreviousCriteria() throws IOException {
		List<String> lines = Files
				.readAllLines(Paths.get(System.getProperty("user.dir") + File.separator + SAVED_CRITERIA));
		UICriteria result = new UICriteria();
		for (String line : lines) {
			String[] split = line.split("=", 2);
			if (split.length > 1) {
				switch (split[0]) {
				case SEARCH_LOCATION:
					result.setSearchFolder(split[1]);
					break;
				case SAVE_LOCATION:
					result.setOutputFolder(split[1]);
					break;
				case AND_TABLES:
					result.setAndTables(split[1]);
					break;
				case AND_COLUMNS:
					result.setAndColumns(split[1]);
					break;
				case OR_TABLES:
					result.setOrTables(split[1]);
					break;
				case OR_COLUMNS:
					result.setOrColumns(split[1]);
					break;
				case SQLQueryTypes.SELECT:
					result.setSelectQuery("true".equals(split[1]));
					break;
				case SQLQueryTypes.ALTER:
					result.setAlterQuery("true".equals(split[1]));
					break;
				case SQLQueryTypes.CREATE:
					result.setCreateQuery("true".equals(split[1]));
					break;
				case SQLQueryTypes.DELETE:
					result.setDeleteQuery("true".equals(split[1]));
					break;
				case SQLQueryTypes.DROP:
					result.setDropQuery("true".equals(split[1]));
					break;
				case SQLQueryTypes.INSERT:
					result.setInsertQuery("true".equals(split[1]));
					break;
				case SQLQueryTypes.UPDATE:
					result.setUpdateQuery("true".equals(split[1]));
					break;
				}
			}
		}
		return result;
	}

	private JButton getJFileChooserButton(String title, JTextArea target) {
		JButton button = new JButton(title);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				if (target.getText() != null && !"".equals(target.getText())) {
					chooser.setCurrentDirectory(new File(target.getText()));
				}
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					target.setText(chooser.getSelectedFile().getPath());
				}
			}

		});

		return button;
	}

}
