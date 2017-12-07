package com.piotrmajcher.jaxblab.invoiceutils.htmltransformer;

import com.piotrmajcher.jaxblab.invoiceutils.domain.Invoice;

public interface HtmlTransformer {
	
	String transformInvoiceToHtml(Invoice invoice) throws HtmlTransformerException;
	
}
