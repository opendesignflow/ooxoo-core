/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.idyria.tools.xml.utils.XMLUtils;

/**
 * This class is top level for a document we have to dereference (for include and import)
 * Just give it a path (local file or http URL) and it will provide you with the DOMDocument
 * object
 * @author rtek
 * 
 */
public abstract class XSDResolvableDocument {

	// ! the base location for automatic local resolution
	protected String baseLocation = null;

	protected String schemaLocation = null;

	// ! the location used to retrieve document
	protected String realLocation = null;

	// ! this is the source package used to prefix imported types
	protected String sourcePackage = null;

	// ! indicated wether a resolution solution has been validated or not
	protected boolean resolved = false;

	// ! the resolved document
	protected Document doc = null;

	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

	public static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

	public XSDResolvableDocument() {
		
	}
	
	public XSDResolvableDocument(String path) {
		this.realLocation = this.schemaLocation = path;
	}

	/**
	 * This method tries to resolve the document
	 * 
	 * @throws Exception
	 * 
	 */
	public void resolve() throws Exception {

		// read schemaLocation

		// case its an absolute file
		if (realLocation.matches("^[/]{1}.*$")
				|| realLocation.matches("^[A-Za-z]{1}:.*$")) {

			System.out.println("Absolute path (" + realLocation + ")");
			this.resolveLocalFile(this.realLocation.replace('\\', '/'));

		} else if (realLocation.matches("^[\\w\\d-.]{1}[\\w\\d-./]*$")) {

			System.out
					.println("relative path : Base is : " + this.baseLocation);
			System.out.println("Solving : " + this.baseLocation
					+ System.getProperty("file.separator") + this.realLocation);

			// relative path only if baseLocation is null
			if (this.baseLocation != null) {
				this.realLocation = this.baseLocation + this.realLocation;
				this.resolve();
			} else
				this.resolveLocalFile(this.realLocation);

		} else if (realLocation.matches("^http(s)?://.*")) {

			System.out.println("Remote(Http) path : " + realLocation);
			resolveRemoteFile(realLocation);

		} else {
			throw new Exception("Protocol not supported");
		}

		// If resolved, at least check we have an XSD document
		if (this.isResolved()) {

			// Get document root
			Element root = (Element) this.doc.getDocumentElement();

			if (root == null
					|| !root.getLocalName().equals("schema")
					|| !root.getNamespaceURI().equals(
							XSDResolvableDocument.XSD_NS)) {
				throw new Exception("Document is not a valid XSDocument");
			}
		}

	}

	/**
	 * Reads the document from a local file
	 * 
	 * @throws Exception
	 * 
	 */
	private void resolveLocalFile(String path) throws Exception {

		File file = new File(path);

		// Read file
		if (file != null && file.isFile()) {

			try {
				System.out.println("-- resolving file at : " + path);
				// Read File
				FileInputStream reader = new FileInputStream(file);
				byte[] source = new byte[reader.available()];
				reader.read(source);
				Document rdoc = XMLUtils.buildDocument(new String(source));

				this.doc = rdoc;

				this.resolved = true;

				// adjust baseLocation
				this.baseLocation = file.getParentFile().getAbsolutePath()
						+ System.getProperty("file.separator");

			} catch (Exception ex) {
				ex.printStackTrace();
				Exception exe = new Exception(
						"Error while loading local schema: " + ex.toString());
				throw exe;
			}
		}

	}

	private void resolveRemoteFile(String urli) throws Exception {

		// resolve
		try {

			// create URL
			URL url = new URL(urli);

			// create parent folder
			String path = "";
			String urlpath = url.getPath();
			String[] split = urlpath.split("/");
			for (int i = 0; i < split.length - 1; i++)
				path += split[i] + "/";

			String parent = url.getProtocol() + "://" + url.getHost() + path;

			System.out.println("First Parent path: " + parent);

			// Add parent to object
			this.baseLocation = parent;

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			String contentype = conn.getContentType();

			// Get the answer input stream
			BufferedReader response = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			
//			InputStream inp = new InputStreamReader(
//					conn.getInputStream());
//			
//			
//			byte[] swallowed = TeaIOUtils.swallow(conn.getInputStream());
//			String ct = new String(swallowed);
			
			String inputline = null;
			String ct = new String();
			while ((inputline = response.readLine()) != null) {
				// System.out.println(inputline);
				ct = ct + inputline;
			}

			// Close the connection
			conn.disconnect();
			response.close();

			System.out.println("Content-type: " + contentype);
			if ((!contentype.contains("application/xml") && !contentype
					.contains("text/xml"))
					|| ct.length() == 0) {
				// Not XML -> error
				throw new Exception("This is no xml!");

			} else {

				// Build xml document
				this.doc = XMLUtils.buildDocument(ct);

				this.resolved = true;

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			Exception exe = new Exception("Error while loading remote schema: "
					+ ex.toString());
			throw exe;
		}

	}

	/**
	 * @return Returns the resolved.
	 */
	public boolean isResolved() {
		return resolved;
	}

	/**
	 * @param resolved
	 *            The resolved to set.
	 */
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	/**
	 * @return Returns the schemaLocation.
	 */
	public String getSchemaLocation() {
		return schemaLocation;
	}

	/**
	 * @param schemaLocation
	 *            The schemaLocation to set.
	 */
	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	/**
	 * @return Returns the sourcePackage.
	 */
	public String getSourcePackage() {
		return sourcePackage;
	}

	/**
	 * @param sourcePackage
	 *            The sourcePackage to set.
	 */
	public void setSourcePackage(String sourcePackage) {
		this.sourcePackage = sourcePackage;
	}

	/**
	 * @return Returns the baseLocation.
	 */
	public String getBaseLocation() {
		return baseLocation;
	}

	/**
	 * @param baseLocation
	 *            The baseLocation to set.
	 */
	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	/**
	 * @return Returns the document.
	 */
	public Document getDoc() {
		return doc;
	}

	/**
	 * @param document
	 *            The document to set.
	 */
	public void setDoc(Document document) {
		this.doc = document;
	}

	/**
	 * @return Returns the realLocation.
	 */
	public String getRealLocation() {
		return realLocation;
	}

	/**
	 * @param realLocation
	 *            The realLocation to set.
	 */
	public void setRealLocation(String realLocation) {
		this.realLocation = realLocation;
	}

}
