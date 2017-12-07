package com.piotrmajcher.jaxblab.invoiceutils.invoiceloader;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.piotrmajcher.jaxblab.invoiceutils.domain.Invoice;

public class InvoiceLoaderBuilder {
	
	private static final String XSD_SCHEMA_FILENAME = "invoice_schema.xsd";
	
	public InvoiceLoader newInstance() throws InvoiceLoaderException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(XSD_SCHEMA_FILENAME).getFile());
		InvoiceLoader invoiceLoader = null;
		
		try {
		
			JAXBContext jaxbContext = JAXBContext.newInstance(Invoice.class);
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema invoiceSchema = schemaFactory.newSchema(new StreamSource(file));
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(invoiceSchema);
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			invoiceLoader = new InvoiceLoaderImpl(jaxbUnmarshaller, jaxbMarshaller);
		
		} catch (JAXBException e) {
			throw new InvoiceLoaderException(e.getMessage());
		} catch (SAXException e) {
			throw new InvoiceLoaderException(e.getMessage());
		}
		
		return invoiceLoader;
	}
}
