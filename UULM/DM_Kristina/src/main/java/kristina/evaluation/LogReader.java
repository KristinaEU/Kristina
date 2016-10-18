package kristina.evaluation;

import java.io.File;



import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class LogReader {
	public static void main(String[] args){
		

		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setFeature("http://xml.org/sax/features/validation", false);
	    builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
	    builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		File[] folder = new File("./log").listFiles();
		
		long mean_runtime = 0;
		for (int i = 0; i < folder.length; i++) {
			File entry = folder[i];
			try {
				Document document = (Document) builder.build(entry);

				Element rootNode = document.getRootElement();
				List<Element> list = rootNode.getChildren("record");
				for(Element e: list){
					Element logger = e.getChild("logger");
					if(logger.getText().equals("runtime")){
						long runtime = Long.parseLong(e.getChild("message").getText());
						mean_runtime = (mean_runtime*i+runtime) / (i+1);
					}else if(logger.getText().equals("usrmv")||logger.getText().equals("sysmv")){
						System.out.println(e.getChild("message").getText());
					}
				}
				
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Average: "+ mean_runtime);
	}
}
