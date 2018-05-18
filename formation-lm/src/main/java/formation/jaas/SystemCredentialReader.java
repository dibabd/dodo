package formation.jaas;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import formation.jaas.model.User;

public class SystemCredentialReader {
	public User readCredentials() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("userdata.xml").getFile());
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		String usr = null;
		String pwd = null;
		Document document;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(file);
			NodeList nodeList = document.getElementsByTagName("user");
			for(int i = 0; i < nodeList.getLength(); i++) {
				usr = document.getElementsByTagName("user").item(0).getTextContent();
				pwd = document.getElementsByTagName("password").item(0).getTextContent();
//				System.out.println("user:" + usr);
//				System.out.println("password:" + pwd);
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (usr == null) {
			usr = "";
			pwd = "";
		}
		if (pwd == null) {
			pwd = "";
		}
		User user = new User(usr, pwd);
		return user;
	}

	private static boolean isPwdMatch(char[] entryPwd, String sysPwd) {
		return new String(entryPwd).equals(sysPwd);
	}

	public boolean isPwdMatch(char[] entryPwd) {
		return isPwdMatch(entryPwd, readCredentials().getPassword());
	}

}
