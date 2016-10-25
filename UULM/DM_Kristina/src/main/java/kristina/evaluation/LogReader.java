package kristina.evaluation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
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
		turnList.setListData(new Turn[] {new Turn("","","","","","","", "", "","")});
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
		json.setEditable(false);
		workspace.setEditable(false);
		exception.setLineWrap(true);
		dmInput.setLineWrap(true);
		dmOutput.setLineWrap(true);
		kiInput.setLineWrap(true);
		kiOutput.setLineWrap(true);
		json.setLineWrap(true);
		workspace.setLineWrap(true);
		tab.add("Json Input", new JScrollPane(json));
		tab.add("LA Input", new JScrollPane(dmInput));
		tab.add("Send to KI", new JScrollPane(kiInput));
		tab.add("Received from KI", new JScrollPane(kiOutput));
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
							} else if (logger.getText().equals("sysmv")) {
								sysmv = e.getChild("message").getText();
							} else if (logger.getText().equals("DM2KI")) {
								kiIn = e.getChild("message").getText();
							} else if (logger.getText().equals("KI2DM")) {
								kiOut = e.getChild("message").getText();
							} else if (logger.getText().equals("DM2VSM")) {
								dmOut = e.getChild("message").getText();
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
								try{
								ar = "0";
								try{
									ar = StringEscapeUtils.unescapeEcmaScript(o.getJsonObject("data").getJsonObject("fusion").getString("arousal"));
								}catch(ClassCastException e1){
									ar = StringEscapeUtils.unescapeEcmaScript(o.getJsonObject("data").getJsonObject("fusion").getJsonNumber("arousal").toString());
								}
								}catch(Exception e2){
								}
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
						
						currentTurns.add(new Turn(usrmv+"\n"+sysmv+"\n",exc, dmIn, kiIn, kiOut, dmOut, j, val, ar, ws));
						
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				dialogues.add(new Dialogue("Dialogue "+nrDialogue++,currentUser, currentScenario, currentTurns));
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
