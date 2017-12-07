package com.piotrmajcher.jaxblab.invoiceutils.htmltransformer;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import com.piotrmajcher.jaxblab.invoiceutils.domain.Invoice;

class HtmlTransformerImpl implements HtmlTransformer {

	private Transformer transformer;
	private JAXBContext jaxbContext;
	
	protected HtmlTransformerImpl(Transformer transformer, JAXBContext jaxbContext) {
		this.transformer = transformer;
		this.jaxbContext = jaxbContext;
	}
	public String transformInvoiceToHtml(Invoice invoice) throws HtmlTransformerException {
		String result = null;
		
        try {
        	
			JAXBSource jaxbSource = new JAXBSource(jaxbContext, invoice);
			
			StringWriter writer = new StringWriter();
	        StreamResult streamResult = new StreamResult(writer);
			transformer.transform(jaxbSource, streamResult);
			result = writer.toString();
		} catch (JAXBException e) {
			throw new HtmlTransformerException(e.getMessage());
		} catch (TransformerException e) {
			throw new HtmlTransformerException(e.getMessage());
		}
        
		return result;
	}

}
