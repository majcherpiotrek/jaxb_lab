package com.piotrmajcher.jaxblab.invoiceutils.htmltransformer;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.piotrmajcher.jaxblab.invoiceutils.domain.Invoice;

public class HtmlTransformerBuilder {
	
	private static final String DEFAULT_STYLESHEET_FILENAME = "invoice_transform.xsl";

	public HtmlTransformer newInstance(File selectedStylesheetFile) throws HtmlTransformerException {
		HtmlTransformer htmlTransformer = null;
		
		StreamSource stylesheetStreamSource = new StreamSource(selectedStylesheetFile);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		try {
			
			Transformer transformer = transformerFactory.newTransformer(stylesheetStreamSource);
			JAXBContext jaxbContext = JAXBContext.newInstance(Invoice.class);
			htmlTransformer = new HtmlTransformerImpl(transformer, jaxbContext);
			
		} catch (TransformerConfigurationException e) {
			throw new HtmlTransformerException(e.getMessage());
		} catch (JAXBException e) {
			throw new HtmlTransformerException(e.getMessage());
		}
		
		return htmlTransformer;
	}
	
	public HtmlTransformer defaultTransformer() throws HtmlTransformerException {
		ClassLoader classLoader = getClass().getClassLoader();
		File defaultStylesheetFile = new File(classLoader.getResource(DEFAULT_STYLESHEET_FILENAME).getFile());
		return newInstance(defaultStylesheetFile);
	}
}
