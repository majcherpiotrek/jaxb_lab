package com.piotrmajcher.jaxblab.invoiceutils.invoiceloader;

import java.io.File;

import com.piotrmajcher.jaxblab.invoiceutils.domain.Invoice;

public interface InvoiceLoader {

	Invoice loadInvoiceFromXmlFile(File file) throws InvoiceLoaderException;
	
	void saveInvoiceToXml(Invoice invoice, String fileName) throws InvoiceLoaderException;
}
