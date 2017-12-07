package com.piotrmajcher.jaxblab.invoiceutils.invoiceloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.piotrmajcher.jaxblab.invoiceutils.domain.Invoice;

class InvoiceLoaderImpl implements InvoiceLoader {
	
	private static final String XML_EXTENSION = ".xml";
	private Unmarshaller jaxbUnmarshaller;
	private Marshaller jaxbMarshaller;
	
	protected InvoiceLoaderImpl(Unmarshaller jaxbUnmarshaller, Marshaller jaxbMarshaller) {
		this.jaxbUnmarshaller = jaxbUnmarshaller;
		this.jaxbMarshaller = jaxbMarshaller;
	}
	
	public Invoice loadInvoiceFromXmlFile(File file) throws InvoiceLoaderException {
		Invoice invoice = null;
		
		try {
			invoice = (Invoice) jaxbUnmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			throw new InvoiceLoaderException(e.getMessage());
		}
		
		return invoice;
	}

	public void saveInvoiceToXml(Invoice invoice, String fileName) throws InvoiceLoaderException {

		String effectiveFileName = "./data/" + fileName;
		if (!effectiveFileName.contains(".xml")) {
			effectiveFileName += XML_EXTENSION;
		}
		File outputXmlFile = new File(effectiveFileName);
		try {
			FileOutputStream fos = new FileOutputStream(outputXmlFile, false); // "false" causes to override file
			jaxbMarshaller.marshal(invoice, fos);
			
		} catch (FileNotFoundException e) {
			throw new InvoiceLoaderException(e.getMessage());
		} catch (JAXBException e) {
			throw new InvoiceLoaderException(e.getMessage());
		}
	}
}
