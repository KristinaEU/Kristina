package owlSpeak.kristina.evaluation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javafx.scene.control.ListView;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.ontology.OntClass;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class LogReader extends JFrame implements ListSelectionListener, ActionListener{

	long runtime;
	long runtimeWithKI;
	long runtimeWithoutKI;

	

	JLabel generalInfo = new JLabel();
	JLabel va = new JLabel();
	JList<Dialogue> dialogueList;
	JList<Turn> turnList;
	JTextArea exception = new JTextArea();
	JTextArea dmInput= new JTextArea();
	JTextArea kiInput= new JTextArea();
	JTextArea kiOutput= new JTextArea();
	JTextArea verbosity= new JTextArea();
	JTextArea dmOutput= new JTextArea();
	JTextArea json= new JTextArea();
	JTextArea workspace = new JTextArea();

	public LogReader() {

		
		
		JPanel dialogueInfo = new JPanel();
		dialogueInfo.setLayout(new BorderLayout());
		this.getContentPane().setLayout(new BorderLayout());
		dialogueList = new JList<Dialogue>(readFiles());
		dialogueList.setCellRenderer(new MyCellRenderer());
		dialogueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dialogueList.addListSelectionListener(this);
		dialogueInfo.add(new JScrollPane(dialogueList), BorderLayout.CENTER);
		
		JButton reload = new JButton("Refresh");
		reload.addActionListener(this);
		dialogueInfo.add(reload, BorderLayout.SOUTH);
		this.getContentPane().add(dialogueInfo, BorderLayout.WEST);
		
		JLabel times = new JLabel("Runtime: "+runtime+", with KI: \t"+runtimeWithKI+", without KI: \t"+runtimeWithoutKI);
		this.getContentPane().add(times, BorderLayout.SOUTH);
		
		JPanel center = new JPanel();
		JPanel main = new JPanel();
		center.setLayout(new BorderLayout());
		main.setLayout(new GridLayout(1,2));
		generalInfo = new JLabel("<html>&nbsp;<br>&nbsp;</html>");
		center.add(generalInfo, BorderLayout.NORTH);
		turnList = new JList<Turn>();
		turnList.setCellRenderer(new MyCellRenderer());
		turnList.setListData(new Turn[] {new Turn("","","","","","","", "", "","","")});
		turnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		turnList.addListSelectionListener(this);
		main.add(new JScrollPane(turnList));
		
		JPanel infoPane = new JPanel();
		infoPane.setLayout(new BorderLayout());
		va = new JLabel("<html>&nbsp;<br>&nbsp;</html>");
		infoPane.add(va, BorderLayout.NORTH);
		JTabbedPane tab = new JTabbedPane();
		exception.setEditable(false);
		dmInput.setEditable(false);
		dmOutput.setEditable(false);
		kiInput.setEditable(false);
		kiOutput.setEditable(false);
		verbosity.setEditable(false);
		json.setEditable(false);
		workspace.setEditable(false);
		exception.setLineWrap(true);
		dmInput.setLineWrap(true);
		dmOutput.setLineWrap(true);
		kiInput.setLineWrap(true);
		kiOutput.setLineWrap(true);
		verbosity.setLineWrap(true);
		json.setLineWrap(true);
		workspace.setLineWrap(true);
		tab.add("Json Input", new JScrollPane(json));
		tab.add("LA Input", new JScrollPane(dmInput));
		tab.add("Send to KI", new JScrollPane(kiInput));
		tab.add("Received from KI", new JScrollPane(kiOutput));
		tab.add("Relevance Rating", new JScrollPane(verbosity));
		tab.add("Workspace", new JScrollPane(workspace));
		tab.add("Output", new JScrollPane(dmOutput));
		tab.add("Exceptions", new JScrollPane(exception));
		infoPane.add(tab);
		main.add(infoPane);
		center.add(main, BorderLayout.CENTER);
		
		this.getContentPane().add(center, BorderLayout.CENTER);
		
		//dialogueList.setSelectedIndex(0);
		//updateDialogueView(dialogueList.getSelectedValue());

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
		setVisible(true);
		
		dialogueList.setSelectedIndex(0);
		turnList.setSelectedIndex(0);
	}

	public static void main(String[] args) {
		new LogReader();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource().equals(turnList)){
			Turn t = turnList.getSelectedValue();
			if(t != null){
			updateTurnView(t);
			}
		}else if(e.getSource().equals(dialogueList)){
			Dialogue d = dialogueList.getSelectedValue();
			if(d != null){
			updateDialogueView(d);
			}
		}
		
	}
	
	private void updateTurnView(Turn t){
		exception.setText(t.getException());
		json.setText(t.getJson());
		dmInput.setText(t.getDmInput());
		dmOutput.setText(t.getDmOutput());
		kiInput.setText(t.getKiInput());
		kiOutput.setText(t.getKiOutput());
		verbosity.setText(t.getVerbOutput());
		workspace.setText(t.getWorkspace());
		va.setText("<html>Valence: "+t.getValence()+"<br>Arousal: "+t.getArousal()+"</html>");
	}
	
	private void updateDialogueView(Dialogue d){
		generalInfo.setText("<html>User: "+d.getUser()+"<br>Scenario: "+d.getScenario()+"</html>");
		
		turnList.removeAll();
		turnList.setListData(d.getTurns().toArray(new Turn[0]));
	}
	
	private Dialogue[] readFiles(){
		LinkedList<Dialogue> dialogues = new LinkedList<Dialogue>();
		// read Logs
				SAXBuilder builder = new SAXBuilder();
				builder.setValidation(false);
				builder.setFeature("http://xml.org/sax/features/validation", false);
				builder.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
						false);
				builder.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-external-dtd",
						false);
				File[] folder = new File("./log").listFiles();
				runtime = 0;
				runtimeWithKI = 0;
				runtimeWithoutKI = 0;
				int nrWith = 0;
				int nrWithout = 0;

				int nrDialogue = 1;
				String currentUser = "";
				String currentScenario = "";
				LinkedList<Turn> currentTurns = new LinkedList<Turn>();
				
				LinkedList<Double> valList = new LinkedList<Double>();
				LinkedList<Double> arList = new LinkedList<Double>();
				LinkedList<Double> tresList = new LinkedList<Double>();
				int cnt = 0;
				
				for (int i = 0; i < folder.length; i++) {
					File entry = folder[i];
					try {
						Document document = (Document) builder.build(entry);

						Element rootNode = document.getRootElement();
						List<Element> list = rootNode.getChildren("record");
						List<String> loggerNames = new LinkedList<String>();
						for(Element e : list){
							loggerNames.add(e.getChildText("logger"));
						}
						String usrmv="";
						String sysmv="";
						String exc="";
						String dmIn="";
						String dmOut="";
						String kiIn="";
						String kiOut="";
						String verb="";
						String j = "";
						String val = "";
						String ar = "";
						String usr = "";
						String scen = "";
						String ws = "";
						for (Element e : list) {
							Element logger = e.getChild("logger");
							if (logger.getText().equals("runtime")) {
								long rt = Long.parseLong(e.getChild("message")
										.getText());
								runtime = (runtime * i + rt) / (i + 1);
								if(loggerNames.contains("DM2KI")){
									nrWith++;
									runtimeWithKI = (runtimeWithKI * (nrWith-1) + rt) / nrWith;
								}else{
									nrWithout++;
									runtimeWithoutKI = (runtimeWithoutKI * (nrWithout-1) + rt) / nrWithout;
								}
							} else if (logger.getText().equals("usrmv")) {
								usrmv = e.getChild("message").getText();
							} else if (logger.getText().equals("verbosity")) {
								verb = e.getChildText("message");
							} else if (logger.getText().equals("sysmv")) {
								sysmv = e.getChild("message").getText();
							} else if (logger.getText().equals("DM2KI")) {
								kiIn = e.getChild("message").getText();
							} else if (logger.getText().equals("KI2DM")) {
								kiOut = e.getChild("message").getText();
							} else if (logger.getText().equals("DM2VSM")) {
								dmOut = e.getChild("message").getText();
//								List<String> l = new LinkedList<String>();
//								l.add(e.getChild("message").getText());
//								Files.write(Paths.get("./src/main/resources/results/DM2LG/"+entry.getName()), l);
							} else if (logger.getText().equals("exception")) {
								exc = e.getChild("message").getText()+"\n"+e.getChild("exception").getChildText("message")+"\n";
								List<Element> stacktrace = e.getChild("exception").getChildren("frame");
								for(Element stackElement: stacktrace){
									exc = exc+"\t at "+stackElement.getChildText("class")+"."+stackElement.getChildText("method")+", line "+stackElement.getChildText("line")+"\n";
								}
							} else if(logger.getText().equals("workspace")){
								ws = e.getChildText("message");
							}else if (logger.getText().equals("VSM2DM")) {
								j = e.getChild("message").getText();
								JsonReader jsonReader = Json
										.createReader(new StringReader(j));
								JsonObject o = jsonReader.readObject();
								
								try{
								val = "0";
								try{
									val = StringEscapeUtils.unescapeEcmaScript(o.getJsonObject("data").getJsonObject("fusion").getString("valence"));
								}catch(ClassCastException e2){
									val = StringEscapeUtils.unescapeEcmaScript(o.getJsonObject("data").getJsonObject("fusion").getJsonNumber("valence").toString());
								}
								}catch(Exception e1){
									
								}
								valList.add(Double.valueOf(val));
								try{
								ar = "0";
								try{
									ar = StringEscapeUtils.unescapeEcmaScript(o.getJsonObject("data").getJsonObject("fusion").getString("arousal"));
								}catch(ClassCastException e1){
									ar = StringEscapeUtils.unescapeEcmaScript(o.getJsonObject("data").getJsonObject("fusion").getJsonNumber("arousal").toString());
								}
								}catch(Exception e2){
								}
								arList.add(Double.valueOf(ar));
								tresList.add(Math.sqrt(arList.get(cnt)*arList.get(cnt)+valList.get(cnt)*valList.get(cnt)));
								cnt++;
								try{					
								usr = StringEscapeUtils.unescapeEcmaScript(o.getJsonObject("meta").getString("user")).toLowerCase();
								}catch(Exception e3){
								}
								try{
								scen = StringEscapeUtils.unescapeEcmaScript(o.getJsonObject("meta").getString("scenario")).toLowerCase();
								}catch(Exception e4){
								}
								try{
								dmIn = StringEscapeUtils.unescapeEcmaScript(o
										.getJsonObject("data").getString(
												"language-analysis"));
								}catch(Exception e5){
									
								}
							}
						}
						
						if(!usr.equals(currentUser) || !scen.equals(currentScenario)){
							if(!currentTurns.isEmpty()){
							dialogues.add(new Dialogue("Dialogue "+nrDialogue++, currentUser, currentScenario, currentTurns));
							}
							
							currentUser = usr;
							currentScenario = scen;
							currentTurns = new LinkedList<Turn>();
						}
						
						currentTurns.add(new Turn(usrmv+"\n"+sysmv+"\n",exc, dmIn, kiIn, kiOut, dmOut, j, val, ar, ws,verb));
						
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						System.err.println(entry.getName());
					}
				}
				dialogues.add(new Dialogue("Dialogue "+nrDialogue++,currentUser, currentScenario, currentTurns));
				
				System.out.println(arList.stream().filter(d -> d > 0.55).count() +"/"+arList.size()+"="+new Double(arList.stream().filter(d -> d > 0.75).count())/arList.size());
				System.out.println(arList.stream().filter(d -> d > 0.6).count() +"/"+arList.size()+"="+new Double(arList.stream().filter(d -> d > 0.8).count())/arList.size());
				System.out.println(arList.stream().filter(d -> d > 0.65).count() +"/"+arList.size()+"="+new Double(arList.stream().filter(d -> d > 0.85).count())/arList.size());
				System.out.println(arList.stream().filter(d -> d > 0.7).count() +"/"+arList.size()+"="+new Double(arList.stream().filter(d -> d > 0.9).count())/arList.size());
				System.out.println(arList.stream().filter(d -> d > 0.75).count() +"/"+arList.size()+"="+new Double(arList.stream().filter(d -> d > 0.95).count())/arList.size());
				
				System.out.println(valList.stream().filter(d -> d > 0.55).count() +"/"+valList.size()+"="+new Double(valList.stream().filter(d -> d > 0.75).count())/valList.size());
				System.out.println(valList.stream().filter(d -> d > 0.6).count() +"/"+valList.size()+"="+new Double(valList.stream().filter(d -> d > 0.8).count())/valList.size());
				System.out.println(valList.stream().filter(d -> d > 0.65).count() +"/"+valList.size()+"="+new Double(valList.stream().filter(d -> d > 0.85).count())/valList.size());
				System.out.println(valList.stream().filter(d -> d > 0.7).count() +"/"+valList.size()+"="+new Double(valList.stream().filter(d -> d > 0.9).count())/valList.size());
				System.out.println(valList.stream().filter(d -> d > 0.75).count() +"/"+valList.size()+"="+new Double(valList.stream().filter(d -> d > 0.95).count())/valList.size());
				
				System.out.println(tresList.stream().filter(d -> d > 0.75).count() +"/"+tresList.size()+"="+new Double(tresList.stream().filter(d -> d > 0.75).count())/tresList.size());
				System.out.println(tresList.stream().filter(d -> d > 0.8).count() +"/"+tresList.size()+"="+new Double(tresList.stream().filter(d -> d > 0.8).count())/tresList.size());
				System.out.println(tresList.stream().filter(d -> d > 0.85).count() +"/"+tresList.size()+"="+new Double(tresList.stream().filter(d -> d > 0.85).count())/tresList.size());
				System.out.println(tresList.stream().filter(d -> d > 0.9).count() +"/"+tresList.size()+"="+new Double(tresList.stream().filter(d -> d > 0.9).count())/tresList.size());
				System.out.println(tresList.stream().filter(d -> d > 0.95).count() +"/"+tresList.size()+"="+new Double(tresList.stream().filter(d -> d > 0.95).count())/tresList.size());
				System.out.println(tresList.stream().filter(d -> d > 1).count() +"/"+tresList.size()+"="+new Double(tresList.stream().filter(d -> d > 1).count())/tresList.size());
				System.out.println(tresList.stream().filter(d -> d > 1.05).count() +"/"+tresList.size()+"="+new Double(tresList.stream().filter(d -> d > 1.05).count())/tresList.size());
				System.out.println(tresList.stream().filter(d -> d > 1.1).count() +"/"+tresList.size()+"="+new Double(tresList.stream().filter(d -> d > 1.1).count())/tresList.size());
				
				
				return dialogues.toArray(new Dialogue[0]);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialogueList.removeAll();
		dialogueList.setListData(readFiles());
		
		dialogueList.setSelectedIndex(0);
		turnList.setSelectedIndex(0);
	}
}

class MyCellRenderer extends JTextArea implements ListCellRenderer<Object> {

    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.

    public Component getListCellRendererComponent(
      JList<?> list,           // the list
      Object value,            // value to display
      int index,               // cell index
      boolean isSelected,      // is the cell selected
      boolean cellHasFocus)    // does the cell have focus
    {
        String s = value.toString();
        setText(s);
        setLineWrap(true);
        setEditable(false);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
