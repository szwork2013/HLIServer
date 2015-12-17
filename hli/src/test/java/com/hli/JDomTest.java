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
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

public class JDomTest {
	/*@Test
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

	}*/
	
	@Test
	public void JDomTest2() {
		String xml = 
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>" + System.lineSeparator() + 
		"<API_OUT_T11 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://gsapi.m2i.kr/\">" + System.lineSeparator() + 
		"<RESULTCODE>00</RESULTCODE>" + System.lineSeparator() + 
		"<RESULTMSG>완료</RESULTMSG>" + System.lineSeparator() + 
		"<LISTCOUNT>16</LISTCOUNT>" + System.lineSeparator() + 
		"<LIST>" + System.lineSeparator() + 
		"<T11_VIEW>" + System.lineSeparator() + 
		"<COUPONCODE>00A1310S00001</COUPONCODE>" + System.lineSeparator() + 
		"<UPDATE_DAY>2014-01-09 10:04</UPDATE_DAY>" + System.lineSeparator() + 
		"<USE_YN>Y</USE_YN>" + System.lineSeparator() + 
		"</T11_VIEW>" + System.lineSeparator() + 
		"<T11_VIEW>" + System.lineSeparator() + 
		"<COUPONCODE>00A1310Q00001</COUPONCODE>" + System.lineSeparator() + 
		"<UPDATE_DAY>2014-01-09 10:03</UPDATE_DAY>" + System.lineSeparator() + 
		"<USE_YN>Y</USE_YN>" + System.lineSeparator() + 
		"</T11_VIEW>" + System.lineSeparator() + 
		"</LIST>" + System.lineSeparator() + 
		"</API_OUT_T11>";
		
		System.out.println(xml);
		System.out.println("----------------------");
		
		SAXBuilder builder = new SAXBuilder();
		// File xmlFile = new File("c:\\file.xml");
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(xml.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			Document document = (Document) builder.build(in);
			Element rootNode = document.getRootElement();
			System.out.println("" + rootNode.getName() + "," + rootNode.getNamespace() + "," + rootNode.getNamespaceURI());
			
			//rootNode.setNamespace(Namespace.NO_NAMESPACE);
			//System.out.println("" + rootNode.getName() + "," + rootNode.getNamespace() + "," + rootNode.getNamespaceURI());
			
			//Test
/*			Element element = rootNode.getChild("LISTCOUNT", rootNode.getNamespace());
			System.out.println("name:" + element.getName());
			System.out.println("text:" + element.getText());*/
			
			Element listNode =rootNode.getChild("LIST", rootNode.getNamespace());
			
			if(listNode != null) {
				List<Element> t11List = listNode.getChildren("T11_VIEW", rootNode.getNamespace());
				for(int i=0; i<t11List.size(); ++i) {
					String couponCode = t11List.get(i).getChildText("COUPONCODE", rootNode.getNamespace());
					System.out.println("coupon code:" + couponCode);
				}
			} else {
				
			}


		} catch (IOException io) {
			System.out.println("io error:" + io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		} catch (Exception e) {
			System.out.println("exception:" + e.getMessage());
		} 
	}
}
