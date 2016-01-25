package owlSpeak.util;import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Scanner;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;



public class OwliVeMain {

	public static void main (String[] args) {
		//File file = new File("C:\\owlspeak\\plugins\\owlive\\wohnzimmer.html");
		File file = new File("wohnzimmer.html");
		String ip = new String();
		int reload = 0;
		//String ip = "134.60.24.62";
		try {
			ip = args[0];
			System.out.println("IP = " + ip);
		} catch (Exception e) {
			ip = "134.60.24.160";
			System.out.println("Standard IP Frog= 134.60.24.160");
		}
		
		int sleeptimer = 2000;
		//SAXBuilder builder = new SAXBuilder();
		Document doc = new Document();
		String reloadTime = "2";
		String[] ontos = {"jalousie", "jalousie", "ventilator", "ventilator",  "tv", "tv", "music", "music", "heating", "heating", "light", "light"};
		String[] var = {"state", "dynamic", "state", "dynamic", "state", "dynamic", "state", "dynamic",	"state", "dynamic", "state", "dynamic"};
		String[][] namen = { 
			{"oben4-jaloauf.jpg","oben4-jalozu.jpg"},
			{"unten1-ventan.jpg","unten1-ventaus.jpg"},
			{"unten2-tvan.jpg","unten2-tvaus.jpg","unten2-tvaktiv.jpg"},
			{"unten3-audioan.jpg","unten3-audioaus.jpg","unten3-audioaktiv.jpg"},
			{"unten4-heizan.jpg","unten4-heizaus.jpg","unten4-heizaktiv.jpg"},
			{"unten5-lampan.jpg","unten5-lampaus.jpg","unten5-lampaktiv.jpg"},
			
		};
		String[] result = new String[ontos.length];
	
		while (true){
			// Abfragen Benutzername
			for (int i = 0; i < ontos.length; i++) {
				Scanner s = null, tmpScan = null;			
				try {
					InputStream is = null;
					URL get = new URL("http://" + ip + ":8080/owlSpeak?com=getVariable&onto="+ ontos[i] + "&variable=" + var[i]);
					is = get.openStream();
					String dan;
					
					//System.out.print(dan);
					tmpScan = new Scanner(is);
					s = tmpScan.useDelimiter("\\n" );
				    while (s.hasNext()){
				    	dan=s.next();
				    	//System.out.print(dan); 
				    	if (dan.contains("%%")) {
				    		result[i] = dan.substring(dan.indexOf("%%")+2, dan.lastIndexOf("%%"));
				    	}
				    }
				    
				} catch (MalformedURLException e) {	e.printStackTrace();
				} catch (SocketException e) {System.out.println("Host nicht gefunden.");
				} catch (IOException e) {e.printStackTrace();}
				finally {
					if (s != null)
						s.close();
					if (tmpScan != null)
						tmpScan.close();
				}
			}
		
		
	        Element html = new Element("html");
	        Element head = new Element("head");
	        Element meta = new Element("meta");
	        Element meta2 = new Element("meta");
	        Element body = new Element("body");
	        Element table = new Element("table");
	        Element tr1 = new Element("tr");
	        Element tr2 = new Element("tr");
	        Element td11 = new Element("td");
	        Element img11 = new Element("img");
	        Element td12 = new Element("td");
	        Element img12 = new Element("img");
	        Element td13 = new Element("td");
	        Element img13 = new Element("img");
	        Element td14 = new Element("td");
	        Element img14 = new Element("img");
	        Element td15 = new Element("td");
	        Element img15 = new Element("img");

	        doc.setRootElement(html);
	        html.addContent(head);
	        meta.setAttribute("http-equiv", "refresh");
	        meta.setAttribute("content", reloadTime + "; URL=wohnzimmer.html");
	        head.addContent(meta);
	        meta2.setAttribute("http-equiv", "cache-control");
	        meta2.setAttribute("content", "no-cache");
	        head.addContent(meta2);
	        html.addContent(body);
	        table.setAttribute("border", "0");
	        table.setAttribute("cellpadding", "0");
	        table.setAttribute("cellspacing", "0");
	     	body.addContent(table);
	        table.addContent(tr1);
			// *** 1-1
	        td11.setAttribute("style", "line-height:0px;");
	        img11.setAttribute("src", "oben1.jpg");
	        td11.addContent(img11);
	        tr1.addContent(td11);
	        // *** 1-2
	        td12.setAttribute("style", "line-height:0px;");
	        img12.setAttribute("src", "oben2.jpg");
	        td12.addContent(img12);
	        tr1.addContent(td12);
	        // *** 1-3
	        td13.setAttribute("style", "line-height:0px;");
	        img13.setAttribute("src", "oben3.jpg");
	        td13.addContent(img13);
	        tr1.addContent(td13);
	        // *** 1-4
	        td14.setAttribute("style", "line-height:0px;");
	        try{
	        if(result[0].equals("an") ) img14.setAttribute("src", "oben4-jaloauf.jpg");
	        else img14.setAttribute("src", "oben4-jalozu.jpg");
	        }catch (NullPointerException e){System.out.println("evil");};
	        td14.addContent(img14);
	        tr1.addContent(td14);
	        // *** 1-5
	        td15.setAttribute("style", "line-height:0px;");
	        img15.setAttribute("src", "oben5.jpg");
	        td15.addContent(img15);
	        tr1.addContent(td15);
	        
	        table.addContent(tr2);
	        
	        Element temp;
	        Element temp2;
	        for(int i=1; i<6; i++){
	           temp = new Element("td");
	           temp2 = new Element("img");
	           temp.setAttribute("style", "line-height:0px;");
	           if(result[i*2+1].equals("aktiv") ) {
	        	   temp2.setAttribute("src", namen[i][2]);
	        	   try{
	        	   URL get = new URL("http://" + ip + ":8080/owlSpeak?com=setVariable&onto="+ ontos[i*2+1] + "&variable=" + var[i*2+1]+"&value=inaktiv");
	        	   get.openStream();
	        	} catch (MalformedURLException e) {	e.printStackTrace();
	        	} catch (SocketException e) {System.out.println("Host nicht gefunden.");
				} catch (IOException e) {e.printStackTrace();}
	        	   
	        	   
	           }
	           
	           else if(result[i*2].equals("an") ) temp2.setAttribute("src", namen[i][0]);
		       else temp2.setAttribute("src", namen[i][1]);
	           tr2.addContent(temp);
	           temp.addContent(temp2);
	        }
        
	        XMLOutputter outputter = new XMLOutputter();
	        outputter.setFormat(Format.getPrettyFormat());
	        
			try {
				java.io.FileWriter writer = new java.io.FileWriter(file);
	            outputter.output(doc, writer);
	            writer.flush();
	            writer.close();
	            System.out.println("New file written: # " + reload);
	            reload++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		
		
			try {
				Thread.sleep(sleeptimer);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

