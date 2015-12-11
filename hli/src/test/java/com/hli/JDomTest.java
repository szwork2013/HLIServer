package com.hli;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

public class JDomTest {
	@Test
	public void JdomTest() {
		String xml =
		"<?xml version=\"1.0\" encoding=\"euc-kr\"?>" + System.lineSeparator() + 
		"<company>" + System.lineSeparator() + 
			"<staff>" + System.lineSeparator() + 
				"<firstname>영</firstname>" + System.lineSeparator() + 
				"<lastname><![CDATA[ 스무디킹 ]]></lastname>" + System.lineSeparator() + 
				"<nickname>mkyong</nickname>" + System.lineSeparator() + 
				"<salary>100000</salary>" + System.lineSeparator() + 
			"</staff>" + System.lineSeparator() + 
			"<staff>" + System.lineSeparator() + 
				"<firstname>low</firstname>" + System.lineSeparator() + 
				"<lastname>yin fong</lastname>" + System.lineSeparator() + 
				"<nickname>fong fong</nickname>" + System.lineSeparator() + 
				"<salary>200000</salary>" + System.lineSeparator() + 
			"</staff>" + System.lineSeparator() + 
		"</company>";
		
		SAXBuilder builder = new SAXBuilder();
		// File xmlFile = new File("c:\\file.xml");
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(xml.getBytes("euc-kr"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {

			Document document = (Document) builder.build(in);
			Element rootNode = document.getRootElement();
			List list = rootNode.getChildren("staff");
			System.out.println("list size:" + list.size());

			for (int i = 0; i < list.size(); i++) {

				Element node = (Element) list.get(i);

				System.out.println("First Name : "
						+ node.getChildText("firstname"));
				System.out.println("Last Name : "
						+ node.getChildText("lastname"));
				System.out.println("Nick Name : "
						+ node.getChildText("nickname"));
				System.out.println("Salary : " + node.getChildText("salary"));

			}

		} catch (IOException io) {
			System.out.println(io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		}

	}
}
