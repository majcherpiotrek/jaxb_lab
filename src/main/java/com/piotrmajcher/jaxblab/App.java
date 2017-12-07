package com.piotrmajcher.jaxblab;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import com.piotrmajcher.jaxblab.invoiceutils.domain.Address;
import com.piotrmajcher.jaxblab.invoiceutils.domain.Buyer;
import com.piotrmajcher.jaxblab.invoiceutils.domain.Invoice;
import com.piotrmajcher.jaxblab.invoiceutils.domain.ObjectFactory;
import com.piotrmajcher.jaxblab.invoiceutils.domain.Products;
import com.piotrmajcher.jaxblab.invoiceutils.domain.Vendor;
import com.piotrmajcher.jaxblab.invoiceutils.htmltransformer.HtmlTransformer;
import com.piotrmajcher.jaxblab.invoiceutils.htmltransformer.HtmlTransformerBuilder;
import com.piotrmajcher.jaxblab.invoiceutils.htmltransformer.HtmlTransformerException;
import com.piotrmajcher.jaxblab.invoiceutils.invoiceloader.InvoiceLoader;
import com.piotrmajcher.jaxblab.invoiceutils.invoiceloader.InvoiceLoaderBuilder;
import com.piotrmajcher.jaxblab.invoiceutils.invoiceloader.InvoiceLoaderException;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String APP_NAME = "JAXB example";
    private static HtmlTransformer transformer;
    private static Invoice loadedInvoice;
    private static InvoiceLoader invoiceLoader;
    private static HtmlTransformerBuilder htmlTransformerBuilder;
    
	public static void main( String[] args )
    {    	
    	
    	try {

    		htmlTransformerBuilder = new HtmlTransformerBuilder();
    		transformer = htmlTransformerBuilder.defaultTransformer();
    		
    		final InvoiceLoaderBuilder invoiceLoaderBuilder = new InvoiceLoaderBuilder();
    		invoiceLoader = invoiceLoaderBuilder.newInstance();
    		
    		loadedInvoice = createSampleInvoice();
    		
            createAndShowGUI();

    	    } catch (InvoiceLoaderException e) {
				e.printStackTrace();
			} catch (HtmlTransformerException e) {
				e.printStackTrace();
			}
    }

	private static Invoice createSampleInvoice() {
		ObjectFactory objectFactory = new ObjectFactory();
    	Address buyerAddress = objectFactory.createAddress("Skłodowskiej-Curie", "79/1", "50-369", "Wrocław", "Polska");
    	Address vendorAddress = objectFactory.createAddress("Plac Grunwaldzki", "1", "50-369", "Wrocław", "Polska");
    	
    	Buyer buyer = objectFactory.createBuyer("Piotr", "Majcher", buyerAddress);
    	Vendor vendor = objectFactory.createVendor("Intersport", "", vendorAddress);
    	
    	List<Products> productsList = new LinkedList<Products>();
    	productsList.add(objectFactory.createProducts("Krążek hokejowy", 29.99, 10));
    	productsList.add(objectFactory.createProducts("Łyżwy nike bauer", 599.99, 1));
    	
    	Invoice invoice = objectFactory.createInvoice("Invoice nr 2017/12/7/1", buyer, vendor, productsList);
		return invoice;
	}

	private static void createAndShowGUI() {
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		final JFrame mainFrame = new JFrame(APP_NAME);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
	
		final JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setPreferredSize(new Dimension(600, 1000));
		try {
			editorPane.setText(transformer.transformInvoiceToHtml(loadedInvoice));
		} catch (HtmlTransformerException e2) {
			e2.printStackTrace();
		}
		
		/*
		 * Control panel setup
		 */
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		JPanel controlButtonsPanel = new JPanel();
		controlButtonsPanel.setLayout(new BoxLayout(controlButtonsPanel, BoxLayout.LINE_AXIS));
		
		JButton stylesheetChooserButton = new JButton("Choose invoice style");
		stylesheetChooserButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("./transformations"));
				if(fileChooser.showOpenDialog(mainFrame) == 0) {
					try {
						transformer = htmlTransformerBuilder.newInstance(fileChooser.getSelectedFile());
						editorPane.setText(transformer.transformInvoiceToHtml(loadedInvoice));
						JOptionPane.showMessageDialog(mainPanel, "Stylesheet loaded successfully");
					} catch (HtmlTransformerException e1) {
						JOptionPane.showMessageDialog(mainPanel, "Loading stylesheet from file failed", "Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}
		});
		
		JButton loadInvoiceButton = new JButton("Load invoice");
		loadInvoiceButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("./data"));
				if (fileChooser.showOpenDialog(mainFrame) == 0) {
					
					try {
						loadedInvoice = invoiceLoader.loadInvoiceFromXmlFile(fileChooser.getSelectedFile());
						editorPane.setText(transformer.transformInvoiceToHtml(loadedInvoice));
						JOptionPane.showMessageDialog(mainPanel, "Invoice loaded successfully");
					} catch (InvoiceLoaderException e) {
						JOptionPane.showMessageDialog(mainPanel, "Loading invoice from file failed", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					} catch (HtmlTransformerException e) {
						JOptionPane.showMessageDialog(mainPanel, "Loading invoice from file failed", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
					
				}
			}
		});
		
		JButton saveInvoiceButton = new JButton("Save invoice");
		saveInvoiceButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if (loadedInvoice != null) {
					String fileName = loadedInvoice.getInvoiceNumber().replaceAll(" ", "_").replaceAll("/", "_");
		    		fileName += ".xml";
					
		    		try {
						invoiceLoader.saveInvoiceToXml(loadedInvoice, fileName);
						JOptionPane.showMessageDialog(mainPanel, "Invoice saved: /data/" + fileName);
					} catch (InvoiceLoaderException e) {
						JOptionPane.showMessageDialog(mainPanel, "Saving invoice to file failed", "Error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
				
			}
		});
		
		/*
		 * Invoice edit panel
		 */
		
		JPanel invoiceEditPanel = new JPanel();
		invoiceEditPanel.setLayout(new BoxLayout(invoiceEditPanel, BoxLayout.PAGE_AXIS));
		
		JLabel invoiceTitleLabel = new JLabel("Invoice title");
		final JTextField invoiceTitleTextField = new JTextField();
		invoiceTitleTextField.setText(loadedInvoice.getInvoiceNumber());
		
		/*
		 * Vendor data
		 */
		JLabel vendorNameLabel = new JLabel("vendorName");
		final JTextField vendorNameTextField = new JTextField();
		vendorNameTextField.setText(loadedInvoice.getVendor().getName());
		
		JLabel vendorSurnameLabel = new JLabel("vendorSurname");
		final JTextField vendorSurnameTextField = new JTextField();
		vendorSurnameTextField.setText(loadedInvoice.getVendor().getSurname());
		
		JLabel vendorStreetLabel = new JLabel("vendorStreet");
		final JTextField vendorStreetTextField = new JTextField();
		vendorStreetTextField.setText(loadedInvoice.getVendor().getAddress().getStreet());
		
		JLabel vendorHouseNumberLabel = new JLabel("vendorHouseNumber");
		final JTextField vendorHouseNumberField = new JTextField();
		vendorHouseNumberField.setText(loadedInvoice.getVendor().getAddress().getHouseNumber());
		
		JLabel vendorPostCodeLabel = new JLabel("vendorPostCode");
		final JTextField vendorPostCodeTextField = new JTextField();
		vendorPostCodeTextField.setText(loadedInvoice.getVendor().getAddress().getPostCode());
		
		JLabel vendorCityLabel = new JLabel("vendorCity");
		final JTextField vendorCityTextField = new JTextField();
		vendorCityTextField.setText(loadedInvoice.getVendor().getAddress().getCity());
		
		JLabel vendorCountryLabel = new JLabel("vendorCountry");
		final JTextField vendorCountryTextField = new JTextField();
		vendorCountryTextField.setText(loadedInvoice.getVendor().getAddress().getCountry());
		////////////////////////////////////////////////////////
		
		/*
		 * Client data
		 */
		JLabel clientNameLabel = new JLabel("clientName");
		final JTextField clientNameTextField = new JTextField();
		clientNameTextField.setText(loadedInvoice.getBuyer().getName());
		
		JLabel clientSurnameLabel = new JLabel("clientSurname");
		final JTextField clientSurnameTextField = new JTextField();
		clientSurnameTextField.setText(loadedInvoice.getBuyer().getSurname());
		
		JLabel clientStreetLabel = new JLabel("clientStreet");
		final JTextField clientStreetTextField = new JTextField();
		clientStreetTextField.setText(loadedInvoice.getBuyer().getAddress().getStreet());
		
		JLabel clientHouseNumberLabel = new JLabel("clientHouseNumber");
		final JTextField clientHouseNumberField = new JTextField();
		clientHouseNumberField.setText(loadedInvoice.getBuyer().getAddress().getHouseNumber());
		
		JLabel clientPostCodeLabel = new JLabel("clientPostCode");
		final JTextField clientPostCodeTextField = new JTextField();
		clientPostCodeTextField.setText(loadedInvoice.getBuyer().getAddress().getPostCode());
		
		JLabel clientCityLabel = new JLabel("clientCity");
		final JTextField clientCityTextField = new JTextField();
		clientCityTextField.setText(loadedInvoice.getBuyer().getAddress().getCity());
		
		JLabel clientCountryLabel = new JLabel("clientCountry");
		final JTextField clientCountryTextField = new JTextField();
		clientCountryTextField.setText(loadedInvoice.getBuyer().getAddress().getCountry());
		
		////////////////////////////////////////////////////////
		invoiceEditPanel.add(invoiceTitleLabel);
		invoiceEditPanel.add(invoiceTitleTextField);
		
		invoiceEditPanel.add(vendorNameLabel);
		invoiceEditPanel.add(vendorNameTextField);
		invoiceEditPanel.add(vendorSurnameLabel);
		invoiceEditPanel.add(vendorSurnameTextField);
		invoiceEditPanel.add(vendorStreetLabel);
		invoiceEditPanel.add(vendorStreetTextField);
		invoiceEditPanel.add(vendorHouseNumberLabel);
		invoiceEditPanel.add(vendorHouseNumberField);
		invoiceEditPanel.add(vendorPostCodeLabel);
		invoiceEditPanel.add(vendorPostCodeTextField);
		invoiceEditPanel.add(vendorCityLabel);
		invoiceEditPanel.add(vendorCityTextField);
		invoiceEditPanel.add(vendorCountryLabel);
		invoiceEditPanel.add(vendorCountryTextField);
		
		invoiceEditPanel.add(clientNameLabel);
		invoiceEditPanel.add(clientNameTextField);
		invoiceEditPanel.add(clientSurnameLabel);
		invoiceEditPanel.add(clientSurnameTextField);
		invoiceEditPanel.add(clientStreetLabel);
		invoiceEditPanel.add(clientStreetTextField);
		invoiceEditPanel.add(clientHouseNumberLabel);
		invoiceEditPanel.add(clientHouseNumberField);
		invoiceEditPanel.add(clientPostCodeLabel);
		invoiceEditPanel.add(clientPostCodeTextField);
		invoiceEditPanel.add(clientCityLabel);
		invoiceEditPanel.add(clientCityTextField);
		invoiceEditPanel.add(clientCountryLabel);
		invoiceEditPanel.add(clientCountryTextField);
		
		
		JButton updatePreviewButon = new JButton("Update preview");
		updatePreviewButon.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				loadedInvoice.setInvoiceNumber(invoiceTitleTextField.getText());
				
				loadedInvoice.getVendor().setName(vendorNameTextField.getText());
				loadedInvoice.getVendor().setSurname(vendorSurnameTextField.getText());
				
				loadedInvoice.getVendor().getAddress().setStreet(vendorStreetTextField.getText());
				loadedInvoice.getVendor().getAddress().setHouseNumber(vendorHouseNumberField.getText());
				loadedInvoice.getVendor().getAddress().setPostCode(vendorPostCodeTextField.getText());
				loadedInvoice.getVendor().getAddress().setCity(vendorCityTextField.getText());
				loadedInvoice.getVendor().getAddress().setCountry(vendorCountryTextField.getText());
				
				loadedInvoice.getBuyer().setName(clientNameTextField.getText());
				loadedInvoice.getBuyer().setSurname(clientSurnameTextField.getText());
				
				loadedInvoice.getBuyer().getAddress().setStreet(clientStreetTextField.getText());
				loadedInvoice.getBuyer().getAddress().setHouseNumber(clientHouseNumberField.getText());
				loadedInvoice.getBuyer().getAddress().setPostCode(clientPostCodeTextField.getText());
				loadedInvoice.getBuyer().getAddress().setCity(clientCityTextField.getText());
				loadedInvoice.getBuyer().getAddress().setCountry(clientCountryTextField.getText());
				
				try {
					editorPane.setText(transformer.transformInvoiceToHtml(loadedInvoice));
				} catch (HtmlTransformerException e1) {
					JOptionPane.showMessageDialog(mainPanel, "Could not update preview", "Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
		
		invoiceEditPanel.add(updatePreviewButon);
		
		/*
		 * Editing order
		 */
		
		String[] comboBoxElements = new String[loadedInvoice.getProducts().size()];
		for (int i = 0; i < comboBoxElements.length; i++) {
			comboBoxElements[i] = loadedInvoice.getProducts().get(i).getProductName();
		}
		
		final JComboBox<String> comboProducts = new JComboBox<>(comboBoxElements);
		comboProducts.setModel(new DefaultComboBoxModel<>(comboBoxElements));
        JLabel chooseProductLabel = new JLabel("Choose product from the order");
        chooseProductLabel.setLabelFor(comboProducts);
        
        JButton deleteProductButton = new JButton("Delete product");
        deleteProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = comboProducts.getSelectedIndex();
				loadedInvoice.getProducts().remove(selectedIndex);
				comboProducts.removeItemAt(selectedIndex);
				
				try {
					editorPane.setText(transformer.transformInvoiceToHtml(loadedInvoice));
				} catch (HtmlTransformerException e1) {
					JOptionPane.showMessageDialog(mainPanel, "Could not update preview", "Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				
			}
		});
        
        JLabel productNameLabel = new JLabel("Product name");
        final JTextField productNameTextField = new JTextField();
        
        JLabel productPriceLabel = new JLabel("Product price");
        final JTextField productPriceTextField = new JTextField();
        
        JLabel productQuantityLabel = new JLabel("Product quantity");
        final JTextField productQuantityTextField = new JTextField();
        
        JButton addProductButton = new JButton("Add product to order");
        addProductButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ObjectFactory obf = new ObjectFactory();
				String productName = productNameTextField.getText();
				String productPrice = productPriceTextField.getText();
				String productQuantity = productQuantityTextField.getText();
				
				try {
					loadedInvoice.getProducts().add(obf.createProducts(productName, Double.valueOf(productPrice), Integer.valueOf(productQuantity)));
					comboProducts.addItem(productName);
					editorPane.setText(transformer.transformInvoiceToHtml(loadedInvoice));
				} catch(NumberFormatException ex) {
					JOptionPane.showMessageDialog(mainPanel, "Product price must be double and quantity integer!", "Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} catch (HtmlTransformerException e1) {
					JOptionPane.showMessageDialog(mainPanel, "Could not update preview", "Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
        
        
		invoiceEditPanel.add(chooseProductLabel);
		invoiceEditPanel.add(comboProducts);
		invoiceEditPanel.add(deleteProductButton);
		
		invoiceEditPanel.add(productNameLabel);
		invoiceEditPanel.add(productNameTextField);
		
		invoiceEditPanel.add(productPriceLabel);
		invoiceEditPanel.add(productPriceTextField);
		
		invoiceEditPanel.add(productQuantityLabel);
		invoiceEditPanel.add(productQuantityTextField);
		
		invoiceEditPanel.add(addProductButton);
	
		invoiceEditPanel.add(saveInvoiceButton);
		///////////////////////////////////////////////////////////////////////////////
		controlButtonsPanel.add(stylesheetChooserButton);
		controlButtonsPanel.add(loadInvoiceButton);
		controlPanel.add(controlButtonsPanel);
		controlPanel.add(invoiceEditPanel);
		///////////////////////////////////////////////////////////////////////////////
		
		
		
		
		mainPanel.add(editorPane);
		mainPanel.add(controlPanel);
		mainFrame.setContentPane(mainPanel);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private static void displayEditOrderDialog() {
		
		JPanel editOrderPanel = new JPanel();
		editOrderPanel.setLayout(new BoxLayout(editOrderPanel, BoxLayout.PAGE_AXIS));
		
		
		
		
	}
}
