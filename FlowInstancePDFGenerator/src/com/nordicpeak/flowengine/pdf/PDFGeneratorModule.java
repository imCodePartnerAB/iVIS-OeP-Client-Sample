package com.nordicpeak.flowengine.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.XMPSchemaBasic;
import org.apache.jempbox.xmp.XMPSchemaDublinCore;
import org.apache.jempbox.xmp.XMPSchemaPDF;
import org.apache.jempbox.xmp.pdfa.XMPSchemaPDFAId;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.pdf.ITextRenderer;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.interfaces.EventListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.settings.Setting;
import se.unlogic.hierarchy.core.settings.TextFieldSetting;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfile;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileHandler;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileSettingProvider;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.xml.ClassPathURIResolver;
import se.unlogic.standardutils.xml.XMLTransformer;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.xsl.URIXSLTransformer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.beans.PDFQueryResponse;
import com.nordicpeak.flowengine.dao.FlowEngineDAOFactory;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.events.SubmitEvent;
import com.nordicpeak.flowengine.interfaces.EvaluationHandler;
import com.nordicpeak.flowengine.interfaces.FlowEngineInterface;
import com.nordicpeak.flowengine.interfaces.PDFAttachment;
import com.nordicpeak.flowengine.interfaces.PDFProvider;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import com.nordicpeak.flowengine.managers.PDFManagerResponse;


public class PDFGeneratorModule extends AnnotatedForegroundModule implements FlowEngineInterface, PDFProvider, SiteProfileSettingProvider{

	private static final String LOGOTYPE_SETTING_ID = "pdf.flowinstance.logo";

	public static final RelationQuery EVENT_ATTRIBUTE_RELATION_QUERY = new RelationQuery(FlowInstanceEvent.ATTRIBUTES_RELATION);

	private final EventListener<SubmitEvent> submitEventListener = new SubmitEventListener();
	private final EventListener<CRUDEvent<FlowInstance>> crudEventListener = new FlowInstanceCRUDEventListener();

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "PDF XSL stylesheet", description = "The path in classpath relative from this class to the XSL stylesheet used to transform the XHTML for PDF output of queries", required = true)
	protected String pdfStyleSheet;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Default logotype", description = "The path to the default logotype. The path can be in both filesystem or classpath. Use classpath:// prefix resouces in classpath and file:/ prefix för files in filesystem.", required = true)
	protected String defaultLogotype = "classpath://com/nordicpeak/flowengine/pdf/staticcontent/pics/logo.png";

	@ModuleSetting
	@TextAreaSettingDescriptor(name="Supported actionID's", description="The action ID's which will trigger a PDF to be generated when a submit event is detected")
	private String supportedActionIDs;

	@ModuleSetting
	@TextAreaSettingDescriptor(name="Included fonts", description="Path to the fonts that should be included in the PDF (the paths can be either in filesystem or classpath)")
	private String includedFonts;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "PDF dir", description = "The directory where PDF files be stored ")
	protected String pdfDir;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Temp dir", description = "The directory where temporary files be stored ")
	protected String tempDir;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Enable XML debug", description="Enables writing of the generated XML to file if a file is set below.")
	private boolean xmlDebug;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="XML debug file", description="The file to write the generated XML to for debug purposes.")
	private String xmlDebugFile;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Enable XHTML debug", description="Enables writing of the generated XHTML to file if a file is set below.")
	private boolean xhtmlDebug;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="XHTML debug file", description="The file to write the generated XHTML to for debug purposes.")
	private String xhtmlDebugFile;

	@InstanceManagerDependency(required = true)
	private EvaluationHandler evaluationHandler;

	@InstanceManagerDependency(required = true)
	private QueryHandler queryHandler;

	protected SiteProfileHandler siteProfileHandler;

	private FlowEngineDAOFactory daoFactory;

	protected URIXSLTransformer pdfTransformer;

	private List<String> actionList;
	private List<String> fontList;

	@SuppressWarnings("unchecked")
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		systemInterface.getEventHandler().addEventListener(FlowInstanceManager.class, SubmitEvent.class, submitEventListener);
		systemInterface.getEventHandler().addEventListener(FlowInstance.class, CRUDEvent.class, crudEventListener);

		if(!systemInterface.getInstanceHandler().addInstance(PDFProvider.class, this)){

			throw new RuntimeException("Unable to register module " + this.moduleDescriptor + " in global instance handler using key " + PDFProvider.class.getSimpleName() + ", another instance is already registered using this key.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unload() throws Exception {

		if(this.equals(systemInterface.getInstanceHandler().getInstance(PDFProvider.class))){

			systemInterface.getInstanceHandler().removeInstance(PDFProvider.class);
		}

		systemInterface.getEventHandler().removeEventListener(FlowInstanceManager.class, SubmitEvent.class, submitEventListener);
		systemInterface.getEventHandler().removeEventListener(FlowInstance.class, CRUDEvent.class, crudEventListener);

		if(siteProfileHandler != null){

			siteProfileHandler.removeSettingProvider(this);
		}

		super.unload();
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		daoFactory = new FlowEngineDAOFactory(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());
	}

	@Override
	protected void moduleConfigured() throws Exception {

		super.moduleConfigured();

		if(supportedActionIDs == null){

			actionList = null;

		}else{

			actionList = Arrays.asList(supportedActionIDs.replace("\r", "").split("\\n"));
		}

		if(includedFonts == null){

			fontList = null;

		}else{

			fontList = Arrays.asList(includedFonts.replace("\r", "").split("\\n"));
		}

		if(pdfStyleSheet == null){

			pdfTransformer = null;

		}else{

			URL styleSheetURL = this.getClass().getResource(pdfStyleSheet);

			if(styleSheetURL != null){

				try {
					pdfTransformer = new URIXSLTransformer(styleSheetURL.toURI(),ClassPathURIResolver.getInstance(), true);

					log.info("Succesfully parsed PDF stylesheet " + pdfStyleSheet);

				} catch (Exception e){

					log.error("Unable to cache PDF style sheet " + pdfStyleSheet,e);

					pdfTransformer = null;
				}

			}else{
				log.error("Unable to cache PDF style sheet. Resource " + pdfStyleSheet + " not found");
			}
		}
	}

	public void onSubmitEvent(SubmitEvent event) {

		if(this.pdfStyleSheet == null || this.actionList == null){

			log.warn("Module " + this.moduleDescriptor + " not properly configured, refusing to create PDF for flow instance " + event.getFlowInstanceManager().getFlowInstance());
		}

		if(event.getEvent().getEventType() != EventType.SUBMITTED || event.getActionID() == null || !actionList.contains(event.getActionID()) || event.getFlowInstanceManager().getFlowInstance().getFlow().requiresSigning()){

			return;
		}

		log.info("Generating PDF for flow instance " + event.getFlowInstanceManager().getFlowInstance() + " triggered by flow instance event " + event.getEvent() + " by user " + event.getEvent().getPoster());

		try {
			createPDF(event.getFlowInstanceManager(), event.getSiteProfile(), event.getEvent().getPoster(), event.getEvent(), false);

		}catch(Throwable t){

			log.error("Error generating PDF for flow instance " + event.getFlowInstanceManager().getFlowInstance() + " triggered by flow instance event " + event + " by user " + event.getEvent().getPoster(),t);

		}
	}

	private File createPDF(FlowInstanceManager instanceManager, SiteProfile siteProfile, User user, FlowInstanceEvent event, boolean signed) throws Exception {

		if(dependencyReadLock != null){

			dependencyReadLock.lock();
		}

		File basePDF = null;
		File pdfWithAttachments = null;

		try{
			checkRequiredDependencies();

			List<PDFManagerResponse> managerResponses = instanceManager.getPDFContent(this);

			Document doc = XMLUtils.createDomDocument();
			Element documentElement = doc.createElement("Document");
			doc.appendChild(documentElement);

			documentElement.appendChild(instanceManager.getFlowInstance().toXML(doc));

			String logotype = null;

			if(siteProfile != null){

				logotype = siteProfile.getSettingHandler().getString(LOGOTYPE_SETTING_ID);

			}else if(this.siteProfileHandler != null){

				logotype = siteProfileHandler.getGlobalSettingHandler().getString(LOGOTYPE_SETTING_ID);
			}

			if(logotype == null){

				logotype = defaultLogotype;
			}

			XMLUtils.appendNewCDATAElement(doc, documentElement, "Logotype", logotype);

			XMLUtils.appendNewCDATAElement(doc, documentElement, "Signed", signed);

			Timestamp submitDate;

			if(event != null){

				submitDate = event.getAdded();

			}else{

				submitDate = TimeUtils.getCurrentTimestamp();
			}

			XMLUtils.appendNewCDATAElement(doc, documentElement, "SubmitDate", DateUtils.DATE_TIME_FORMATTER.format(submitDate));

			XMLUtils.append(doc, documentElement, "ManagerResponses", managerResponses);


			if(xmlDebug && xmlDebugFile != null){

				try{
					XMLUtils.writeXMLFile(doc, xmlDebugFile, true, systemInterface.getEncoding());

				}catch(Exception e){

					log.error("Error writing debug XML to file " + xmlDebugFile, e);
				}
			}

			StringWriter writer = new StringWriter();

			XMLTransformer.transformToWriter(pdfTransformer.getTransformer(), doc, writer, systemInterface.getEncoding());

			Document document = XMLUtils.parseXML(writer.toString(), false, false);

			if(xhtmlDebug && xhtmlDebugFile != null){

				try{
					XMLUtils.writeXMLFile(document, xhtmlDebugFile, true, systemInterface.getEncoding());

				}catch(Exception e){

					log.error("Error writing debug XHTML to file " + xhtmlDebugFile, e);
				}
			}

			basePDF = createBasePDF(document, managerResponses, instanceManager, event);

			pdfWithAttachments = addAttachments(basePDF, managerResponses, instanceManager, event);

			File outputFile = writePDFA(pdfWithAttachments, instanceManager, event);

			log.info("PDF for flow instance " + instanceManager.getFlowInstance() + ", event " + event + " written to " + outputFile.getAbsolutePath());

			if(event != null){

				setEventAttributes(event);
			}

			return outputFile;

		}finally{

			if(!FileUtils.deleteFile(basePDF)){

				log.warn("Unable to delete file: " + basePDF);
			}

			if(!FileUtils.deleteFile(pdfWithAttachments)){

				log.warn("Unable to delete file: " + pdfWithAttachments);
			}

			if(dependencyReadLock != null){

				dependencyReadLock.unlock();
			}
		}
	}

	private void setEventAttributes(FlowInstanceEvent event) throws SQLException {

		event.getAttributeHandler().setAttribute("pdf", "true");
		daoFactory.getFlowInstanceEventDAO().update(event, EVENT_ATTRIBUTE_RELATION_QUERY);
	}

	private File writePDFA(File pdfWithAttachments, FlowInstanceManager instanceManager, FlowInstanceEvent event) throws Exception {

		File outputFile = getFile(instanceManager.getFlowInstanceID(), event);

		outputFile.getParentFile().mkdirs();

		PDDocument document = PDDocument.loadNonSeq(pdfWithAttachments, null);

		try{
			document.getDocumentInformation().setProducer("Open ePlatform 1.x");

			PDDocumentCatalog cat = document.getDocumentCatalog();
			PDMetadata metadata = new PDMetadata(document);
			cat.setMetadata(metadata);

			XMPMetadata xmp = new XMPMetadata();
			XMPSchemaPDFAId pdfaid = new XMPSchemaPDFAId(xmp);
			xmp.addSchema(pdfaid);
			pdfaid.setConformance("A");
			pdfaid.setPart(3);
			pdfaid.setAbout("");

			XMPSchemaBasic schemaBasic = new XMPSchemaBasic(xmp);

			schemaBasic.setCreateDate(document.getDocumentInformation().getCreationDate());
			schemaBasic.setModifyDate(document.getDocumentInformation().getModificationDate());
			xmp.addSchema(schemaBasic);

			XMPSchemaPDF schemaPDF = new XMPSchemaPDF(xmp);
			schemaPDF.setProducer(document.getDocumentInformation().getProducer());
			xmp.addSchema(schemaPDF);

			XMPSchemaDublinCore schemaDublinCore = new XMPSchemaDublinCore(xmp);
			schemaDublinCore.setTitle(document.getDocumentInformation().getTitle());
			xmp.addSchema(schemaDublinCore);

			metadata.importXMPMetadata(xmp);

			InputStream colorProfile = this.getClass().getResourceAsStream("sRGB Color Space Profile.icm");

			PDOutputIntent oi = new PDOutputIntent(document, colorProfile);
			oi.setInfo("sRGB IEC61966-2.1");
			oi.setOutputCondition("sRGB IEC61966-2.1");
			oi.setOutputConditionIdentifier("sRGB IEC61966-2.1");
			oi.setRegistryName("http://www.color.org");
			cat.addOutputIntent(oi);

			document.save(outputFile);

		}finally{

			document.close();
		}

		return outputFile;
	}

	private File getFile(Integer flowInstanceID, FlowInstanceEvent event) {

		return new File(pdfDir + File.separator + flowInstanceID + File.separator + getFileSuffix(event) + ".pdf");
	}

	private File addAttachments(File basePDF, List<PDFManagerResponse> managerResponses, FlowInstanceManager instanceManager, FlowInstanceEvent event) throws IOException, DocumentException {

		File pdfWithAttachments = File.createTempFile("pdf-with-attachments", instanceManager.getFlowInstanceID() + "-" + getFileSuffix(event) + ".pdf", getTempDir());

		OutputStream outputStream = null;
		InputStream inputStream = null;

		try{
			outputStream = new FileOutputStream(pdfWithAttachments);
			inputStream = new FileInputStream(basePDF);

			PdfReader reader = new PdfReader(inputStream);
			PdfStamper stamper = new PdfStamper(reader, outputStream);
			PdfWriter writer = stamper.getWriter();

			for(PDFManagerResponse managerResponse : managerResponses){

				for(PDFQueryResponse queryResponse : managerResponse.getQueryResponses()){

					if(queryResponse.getAttachments() != null){

						for(PDFAttachment attachment : queryResponse.getAttachments()){

							try {
								PdfFileSpecification fs = StreamPdfFileSpecification.fileEmbedded(writer, attachment.getInputStream(), attachment.getName());
								writer.addFileAttachment(attachment.getDescription(), fs);
							} catch (Exception e) {

								log.error("Error appending attachment " + attachment.getName() + " from query " + queryResponse.getQueryDescriptor(), e);
							}
						}
					}
				}
			}

			stamper.close();

		}finally{

			StreamUtils.closeStream(inputStream);
			StreamUtils.closeStream(outputStream);
		}

		return pdfWithAttachments;
	}

	protected static void addAttachment(PdfWriter writer, File file, String description) throws IOException {

		PdfFileSpecification fs = StreamPdfFileSpecification.fileEmbedded(writer, new FileInputStream(file), file.getName());
		writer.addFileAttachment(description, fs);
	}

	private File createBasePDF(Node node, List<PDFManagerResponse> managerResponses, FlowInstanceManager instanceManager, FlowInstanceEvent event) throws DocumentException, IOException {

		File basePDF = File.createTempFile("basepdf", instanceManager.getFlowInstanceID() + "-" + getFileSuffix(event) + ".pdf", getTempDir());

		OutputStream basePDFOutputStream = null;

		try{
			basePDFOutputStream = new FileOutputStream(basePDF);

			ITextRenderer renderer = new ITextRenderer();
			ResourceLoaderAgent callback = new ResourceLoaderAgent(renderer.getOutputDevice(), managerResponses);
			callback.setSharedContext(renderer.getSharedContext());
			renderer.getSharedContext().setUserAgentCallback(callback);

			renderer.getFontResolver().addFont(includedFonts, "Identity-H", true);

			renderer.setDocument((Document)node, "c:\\users\\unlogic\foo.html");
			renderer.layout();

			renderer.createPDF(basePDFOutputStream);

		}finally{

			StreamUtils.closeStream(basePDFOutputStream);
		}

		return basePDF;
	}

	private String getFileSuffix(FlowInstanceEvent event) {

		if(event != null){

			return event.getEventID().toString();

		}else{

			return "temp";
		}
	}

	private File getTempDir() {

		if(tempDir != null){

			return new File(tempDir);
		}

		return null;
	}

	public void onCRUDEvent(CRUDEvent<FlowInstance> event) {

		if(event.getAction() == CRUDAction.DELETE){

			for(FlowInstance flowInstance : event.getBeans()){

				File instanceDir = new File(pdfDir + File.separator + flowInstance.getFlowInstanceID());

				if(!instanceDir.exists()){

					continue;
				}

				log.info("Deleting PDF files for flow instance " + flowInstance);

				FileUtils.deleteFiles(instanceDir, null, true);

				instanceDir.delete();
			}
		}

	}

	protected class SubmitEventListener implements EventListener<SubmitEvent> {

		@Override
		public void processEvent(SubmitEvent event, EventSource source) {

			if(source.isLocal()){

				PDFGeneratorModule.this.onSubmitEvent(event);
			}
		}

		@Override
		public int getPriority() {

			return 0;
		}
	}

	protected class FlowInstanceCRUDEventListener implements EventListener<CRUDEvent<FlowInstance>> {

		@Override
		public void processEvent(CRUDEvent<FlowInstance> event, EventSource source) {

			if(source.isLocal()){

				PDFGeneratorModule.this.onCRUDEvent(event);
			}
		}

		@Override
		public int getPriority() {

			return 0;
		}
	}

	@Override
	public EvaluationHandler getEvaluationHandler() {

		return evaluationHandler;
	}

	@Override
	public QueryHandler getQueryHandler() {

		return queryHandler;
	}

	@Override
	public SystemInterface getSystemInterface() {

		return systemInterface;
	}

	@Override
	public FlowEngineDAOFactory getDAOFactory() {

		return daoFactory;
	}

	@Override
	public File getPDF(Integer flowInstanceID, Integer eventID) {

		File pdfFile = new File(pdfDir + File.separator + flowInstanceID + File.separator + eventID + ".pdf");

		if(pdfFile.exists()){

			return pdfFile;
		}

		return null;
	}

	@InstanceManagerDependency(required=true)
	public void setSiteProfileHandler(SiteProfileHandler siteProfileHandler) {

		if(siteProfileHandler != null){

			siteProfileHandler.addSettingProvider(this);

		}else{

			this.siteProfileHandler.removeSettingProvider(this);
		}

		this.siteProfileHandler = siteProfileHandler;
	}

	@Override
	public List<Setting> getSiteProfileSettings() {

		return Collections.singletonList((Setting)new TextFieldSetting(LOGOTYPE_SETTING_ID, "PDF logotype", "The logotype used in generated PDF documents.", defaultLogotype, false));
	}

	@Override
	public File createTemporaryPDF(FlowInstanceManager instanceManager, boolean signed, SiteProfile siteProfile, User user) throws Exception{

		return createPDF(instanceManager, siteProfile, user, null, signed);
	}

	@Override
	public boolean saveTemporaryPDF(Integer flowInstanceID, FlowInstanceEvent event) throws Exception{

		File tempFile = getFile(flowInstanceID, null);

		if(tempFile == null){

			return false;
		}

		FileUtils.moveFile(tempFile, getFile(flowInstanceID, event));

		setEventAttributes(event);

		return true;
	}

	@Override
	public boolean deleteTemporaryPDF(Integer flowInstanceID) {

		File tempFile = getFile(flowInstanceID, null);

		return FileUtils.deleteFile(tempFile);
	}

	@Override
	public boolean hasTemporaryPDF(Integer flowInstanceID) {

		File tempFile = getFile(flowInstanceID, null);

		return tempFile.exists();
	}

	@Override
	public File getTemporaryPDF(Integer flowInstanceID) {

		return getFile(flowInstanceID, null);
	}
}
