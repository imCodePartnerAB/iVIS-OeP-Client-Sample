package com.nordicpeak.flowengine.queries.basemapquery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.riges.lm.rmi.ValidateLM;
import se.riges.lm.rmi.exceptions.LMAccountException;
import se.riges.lm.rmi.exceptions.LMUnavailableException;
import se.riges.lm.rmi.interfaces.IEstate;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.DropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SettingHandler;
import se.unlogic.hierarchy.core.settings.Setting;
import se.unlogic.hierarchy.core.settings.TextFieldSetting;
import se.unlogic.hierarchy.core.utils.FCKUtils;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileHandler;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileSettingProvider;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.json.JsonArray;
import se.unlogic.standardutils.json.JsonObject;
import se.unlogic.standardutils.json.JsonUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.TCPPortStringFormatValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLGenerator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;

import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.InstanceMetadata;
import com.nordicpeak.flowengine.interfaces.MutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.MutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.PDFAttachment;
import com.nordicpeak.flowengine.interfaces.PDFResourceProvider;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.interfaces.QueryRequestProcessor;
import com.nordicpeak.flowengine.queries.basemapquery.utils.FeatureLabel;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryCRUDCallback;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryProviderModule;
import com.nordicpeak.flowengine.utils.BlobPDFAttachment;
import com.nordicpeak.flowengine.utils.BlobResourceProvider;
import com.nordicpeak.flowengine.utils.JTidyUtils;
import com.nordicpeak.flowengine.utils.TextTagReplacer;
import com.vividsolutions.jts.algorithm.CentroidArea;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public abstract class BaseMapQueryProviderModule<MapQueryType extends BaseMapQuery, MapQueryInstanceType extends BaseMapQueryInstance<MapQueryType>> extends BaseQueryProviderModule<MapQueryInstanceType> implements BaseQueryCRUDCallback, SiteProfileSettingProvider {

	@XSLVariable(prefix = "java.")
	protected String startExtentSettingName = "Start extent";

	@XSLVariable(prefix = "java.")
	protected String startExtentSettingDescription = "Comma separated coordinate list e.g 608114,6910996,641846,6932596";

	@XSLVariable(prefix = "java.")
	protected String lmUserSettingName = "LM user";

	@XSLVariable(prefix = "java.")
	protected String lmUserSettingDescription = "User to use for LM Search";

	@XSLVariable(prefix = "java.")
	protected String searchPrefixSettingName = "LM search prefix";

	@XSLVariable(prefix = "java.")
	protected String searchPrefixSettingDescription = "Search prefix used when searching for pud or address using LM Search";

	@XSLVariable(prefix = "java.")
	protected String pdfAttachmentDescriptionPrefix = "A file from query:";

	@XSLVariable(prefix = "java.")
	protected String pdfAttachmentFilename = "Map $scale.png";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Query type name", description = "The name of this query", required = true)
	protected String queryTypeDescription = "This should be configured in module settings";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Required query message", description = "The message shown for required validation error", required = true)
	protected String requiredQueryMessage;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Map client script URL", description = "The URL to the map client script", required = true)
	protected String mapScriptURL;

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "Map configuration path type", description = "The pathtype of the map configuration file", required = true)
	protected PathType mapConfigurationPathType = PathType.Classpath;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Map configuration path", description = "The path to the map configuration file", required = true)
	protected String mapConfigurationPath = "mapconfig.json";

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "Print configuration path type", description = "The pathtype of the print configuration file", required = true)
	protected PathType printConfigurationPathType = PathType.Classpath;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Print configuration path", description = "The path to the print configuration file", required = true)
	protected String printConfigurationPath = "printconfig.json";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Print service adress", description = "The address to the print service", required = true)
	protected String printServiceAddress = "http://not.set/print";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Small PNG layout", description = "The layout to use when generating small map image", required = true)
	protected String smallPNGLayout = "OH_PNG";

	@ModuleSetting
	@DropDownSettingDescriptor(name = "Small PNG scale", description = "The scale to use when generating small map image", required = true, valueDescriptions = { "1:400", "1:500", "1:1000", "1:2000", "1:5000", "1:10000" }, values = { "400", "500", "1000", "2000", "5000", "10000" })
	protected Integer smallPNGScale = 500;

	@ModuleSetting
	@DropDownSettingDescriptor(name = "Small PNG resolution", description = "The resolution to use when generating small map image", required = true, valueDescriptions = { "56", "127", "190", "254" }, values = { "56", "127", "190", "254" })
	protected Integer smallPNGResolution = 56;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Large PNG layout", description = "The layout to use when generating large map image", required = true)
	protected String largePNGLayout = "OH_PNG_LARGE";

	@ModuleSetting
	@DropDownSettingDescriptor(name = "Large PNG scale", description = "The scale to use when generating large map image", required = true, valueDescriptions = { "1:400", "1:500", "1:1000", "1:2000", "1:5000", "1:10000" }, values = { "400", "500", "1000", "2000", "5000", "10000" })
	protected Integer largePNGScale = 1000;

	@ModuleSetting
	@DropDownSettingDescriptor(name = "Large PNG resolution", description = "The resolution to use when generating large map image", required = true, valueDescriptions = { "56", "127", "190", "254" }, values = { "56", "127", "190", "254" })
	protected Integer largePNGResolution = 56;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Enable PUD validation", description = "Controls whether the submitted property unit designation should be validated using LM Search service RMI server or not")
	protected boolean enablePUDValidation = false;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "LM Search service RMI server address", description = "The address to the LM RMI server", required = true)
	protected String lmRMIServerAddress = "localhost";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "LM Search service RMI server port", description = "The port of the LM RMI server", required = true, formatValidator = TCPPortStringFormatValidator.class)
	protected int lmRMIServerPort = 1099;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "LM Search service RMI server name", description = "The name of the LM RMI server", required = true)
	protected String lmRMIServerName = "searchlm";

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Enable HTTP search service", description = "Controls whether the HTTP search service should be enabled or not")
	protected boolean enableHttpSearchService = true;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "LM Search service URL", description = "The URL to the LM search service", required = false)
	protected String httpSearchServiceURL = "http://not.set/search/lm";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Param for PUD search", description = "The parameter passed to service when searching for PUDs", required = false)
	protected String httpSearchPUDParam = "registerenheter";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Param for address search", description = "The parameter passed to service when searching for addresses", required = false)
	protected String httpSearchAddressParam = "addresses";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Param for place search", description = "The parameter passed to service when searching for places", required = false)
	protected String httpSearchPlaceParam = "placenames";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Param for coordinate search", description = "The parameter passed to service when searching for PUDs using coordinate ", required = false)
	protected String httpSearchCoordinateParam = "enhetsomraden";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Search result limit", description = "Specifies maximum number of hits returned", required = false, formatValidator = IntegerPopulator.class)
	protected Integer searchResultLimit = 25;

	@ModuleSetting
	@TextFieldSettingDescriptor(id = "BaseMapQuery-lmUser", name = "Default LM Search user", description = "Specifies default user to use for LM Search", required = true)
	protected String defaultLMUser;

	@ModuleSetting
	@TextFieldSettingDescriptor(id = "BaseMapQuery-startExtent", name = "Default start extent", description = "Specifies default start extent for this mapquery", required = true)
	protected String defaultStartExtent;

	@ModuleSetting
	@TextFieldSettingDescriptor(id = "BaseMapQuery-searchPrefix", name = "Default search prefix", description = "Specifies default search prefix when searching for pud or address using LM Search", required = false)
	protected String defaultSearchPrefix = "";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "MapFish connection timeout", description = "MapFish connection timeout")
	protected Integer mapFishConnectionTimeout = 5000;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "MapFish read timeout", description = "MapFish read timeout")
	protected Integer mapFishReadTimeout = 10000;

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "Query instance preview mode", description = "Specifies which preview mode sholud be used for this query", required = true)
	protected BaseMapQueryPreviewMode previewMode = BaseMapQueryPreviewMode.WEB_MAP;

	protected SiteProfileHandler siteProfileHandler;

	protected TextFieldSetting startExtentSetting;

	protected TextFieldSetting lmUserSetting;

	protected TextFieldSetting searchPrefixSetting;

	protected String mapConfiguration;

	protected String printConfiguration;

	protected AnnotatedDAO<MapQueryType> queryDAO;
	protected AnnotatedDAO<MapQueryInstanceType> queryInstanceDAO;

	protected QueryParameterFactory<MapQueryType, Integer> queryIDParamFactory;
	protected QueryParameterFactory<MapQueryInstanceType, Integer> queryInstanceIDParamFactory;

	protected Field QUERY_RELATION = ReflectionUtils.getField(getMapQueryInstanceClass(), "query");

	protected ValidateLM searchLM;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		this.queryTypeID = this.queryTypeID + "." + moduleDescriptor.getModuleID();
		
		super.init(moduleDescriptor, sectionInterface, dataSource);

		cacheConfigurations();

		if (enablePUDValidation) {

			searchLM = getSearchLMClient();

		} else {

			searchLM = null;
		}

	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		cacheConfigurations();

		searchLM = null;

		if (enablePUDValidation) {

			searchLM = getSearchLMClient();
		}

	}

	@Override
	protected void moduleConfigured() throws Exception {

		this.queryTypeName = queryTypeDescription;
		
		super.moduleConfigured();
		
		startExtentSetting = new TextFieldSetting("BaseMapQuery-startExtent", startExtentSettingName, startExtentSettingDescription, defaultStartExtent, true);
		lmUserSetting = new TextFieldSetting("BaseMapQuery-lmUser", lmUserSettingName, lmUserSettingDescription, defaultLMUser, true);
		searchPrefixSetting = new TextFieldSetting("BaseMapQuery-searchPrefix", searchPrefixSettingName, searchPrefixSettingDescription, defaultSearchPrefix, false);
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		List<QueryParameterPopulator<?>> queryParameterPopulators = new ArrayList<QueryParameterPopulator<?>>();

		List<BeanStringPopulator<?>> typePopulators = new ArrayList<BeanStringPopulator<?>>();

		queryDAO = daoFactory.getDAO(getMapQueryClass(), queryParameterPopulators, typePopulators);
		queryInstanceDAO = daoFactory.getDAO(getMapQueryInstanceClass());

		queryIDParamFactory = queryDAO.getParamFactory("queryID", Integer.class);
		queryInstanceIDParamFactory = queryInstanceDAO.getParamFactory("queryInstanceID", Integer.class);

	}

	protected void cacheConfigurations() {

		mapConfiguration = getConfiguration(mapConfigurationPath, mapConfigurationPathType, "config/", "map");

		printConfiguration = getConfiguration(printConfigurationPath, printConfigurationPathType, "printconfig/", "print");

	}

	private String getConfiguration(String path, PathType pathType, String pathPrefix, String configurationLogName) {

		if (pathType != null && path != null) {

			File file = null;

			try {

				if (pathType == PathType.Filesystem) {

					file = new File(path);

				} else if (pathType == PathType.RealtiveFilesystem) {

					file = new File(systemInterface.getApplicationFileSystemPath() + path);

				} else if (pathType == PathType.Classpath) {

					URL url = this.getClass().getResource(pathPrefix + path);

					if (url == null) {

						log.error(configurationLogName + " configuration " + path + " not found, please check modulesettings");

						return null;

					}

					file = new File(url.toURI());

				}

				if (file == null) {

					log.error("Unable to found " + configurationLogName + " configuration " + path);

					return null;

				}

				return StringUtils.readFileAsString(file);

			} catch (IOException e) {

				log.error("Unable to get " + configurationLogName + " configuration " + path);

			} catch (URISyntaxException e) {

				log.error("Unable to get " + configurationLogName + " configuration + " + path);

			}

		}

		log.error("No " + configurationLogName + " configuration set, please check modulesetting");

		return null;

	}

	@Override
	public void populate(MapQueryInstanceType queryInstance, HttpServletRequest req, User user, boolean allowPartialPopulation) throws ValidationException {

		Integer queryID = queryInstance.getQuery().getQueryID();

		String propertyUnitDesignation = req.getParameter("q" + queryID + "_propertyUnitDesignation");
		String extent = req.getParameter("q" + queryID + "_extent");
		String epsg = req.getParameter("q" + queryID + "_epsg");
		String baseLayer = req.getParameter("q" + queryID + "_baseLayer");

		if (StringUtils.isEmpty(propertyUnitDesignation)) {

			if (!allowPartialPopulation && queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED) {

				queryInstance.reset();

				queryInstance.setExtent(extent);
				queryInstance.setVisibleBaseLayer(baseLayer);

				throw new ValidationException(new ValidationError("RequiredQuery"));
			}

			queryInstance.reset();

			return;
		}

		if (StringUtils.isEmpty(extent) || StringUtils.isEmpty(epsg) || StringUtils.isEmpty(baseLayer)) {

			throw new ValidationException(new ValidationError("InCompleteMapQuerySubmit"));

		}

		if (enablePUDValidation) {

			IEstate estate = getPropertyUnitDesignation(propertyUnitDesignation, req, user);

			if (estate == null) {
				throw new ValidationException(new ValidationError("PUDNotValid"));
			}

			queryInstance.setPropertyUnitNumber(estate.getEstateID());

		}

		queryInstance.setVisibleBaseLayer(baseLayer);
		queryInstance.setPropertyUnitDesignation(propertyUnitDesignation);
		queryInstance.setExtent(extent);
		queryInstance.setEpsg(epsg);

	}

	public void generatePNG(MapQueryInstanceType queryInstance, User user) throws ValidationException {

		if (printConfiguration != null && printServiceAddress != null) {

			log.info("Generating map images for queryInstance " + queryInstance + " for user " + user + ", using server: " + printServiceAddress);

			String printConfig = printConfiguration;
			
			List<Geometry> geometries = queryInstance.getPrintableGeometries();

			JsonArray features = new JsonArray();

			List<Coordinate> allCoordinates = new ArrayList<Coordinate>();

			if (!CollectionUtils.isEmpty(geometries)) {

				List<FeatureLabel> labels = new ArrayList<FeatureLabel>();
				
				GeometryFactory geometryFactory = new GeometryFactory();
				
				for (Geometry geometry : geometries) {

					JsonObject properties = new JsonObject();
					properties.putField("_style", geometry.getGeometryType());

					JsonObject featureGeometry = new JsonObject();

					featureGeometry.putField("type", geometry.getGeometryType());

					Coordinate[] coordinates = geometry.getCoordinates();

					if (coordinates != null) {

						JsonArray coords = new JsonArray();

						if (coordinates.length > 1) {

							int count = 0;
							
							for (Coordinate coordinate : coordinates) {

								if(count < coordinates.length-1) {
								
									LineString lineString = geometryFactory.createLineString(new Coordinate[]{coordinates[count],coordinates[count+1]});
								
									Point centroid = lineString.getCentroid();
								
									labels.add(new FeatureLabel(UUID.randomUUID().toString(), centroid, NumberUtils.formatNumber(lineString.getLength(), 1, 1, false, true) + " m"));
								};
								
								JsonArray coord = new JsonArray();

								String x = NumberUtils.formatNumber(coordinate.x, 0, 1, false, true);
								String y = NumberUtils.formatNumber(coordinate.y, 0, 1, false, true);

								coord.addNode(x);
								coord.addNode(y);
								coords.addNode(coord);

								count++;
							}

							if (geometry.getGeometryType().equalsIgnoreCase("LineString")) {

								featureGeometry.putField("coordinates", coords);

							} else {

								JsonArray wrapper = new JsonArray();
								wrapper.addNode(coords);

								featureGeometry.putField("coordinates", wrapper);

							}

						} else if (coordinates.length == 1) {

							String x = NumberUtils.formatNumber(coordinates[0].x, 0, 1, false, true);
							String y = NumberUtils.formatNumber(coordinates[0].y, 0, 1, false, true);

							coords.addNode(x);
							coords.addNode(y);

							featureGeometry.putField("coordinates", coords);

						}

						allCoordinates.addAll(Arrays.asList(coordinates));

					}

					JsonObject feature = new JsonObject();
					feature.putField("type", "Feature");
					feature.putField("properties", properties);
					feature.putField("geometry", featureGeometry);

					features.addNode(feature);

				}

				if(!labels.isEmpty()) {
					
					StringBuilder labelStyles = new StringBuilder();
					
					for(FeatureLabel label : labels) {
						
						features.addNode(label.toJson());
						
						JsonObject labelStyle = new JsonObject();
						labelStyle.putField("label", label.getLabel());
						labelStyle.putField("strokeColor", label.getColor());
						labelStyle.putField("strokeWidth", label.getWidth() + "");
						labelStyle.putField("labelAlign", "cm");
						labelStyle.putField("fontSize", "9px");
						
						labelStyles.append(",\"" + label.getId() + "\":" + labelStyle.toJson());
						
						
					}
					
					printConfig = printConfig.replace("$labelStyles", labelStyles);
					
				} else {
					
					printConfig = printConfig.replace("$labelStyles", "");
					
				}
				
			}

			Coordinate centerCoordinate = null;

			if (!allCoordinates.isEmpty()) {

				centerCoordinate = calculateMapCentroid(allCoordinates);

			} else {

				List<Double> coordinates = NumberUtils.toDouble(Arrays.asList(queryInstance.getExtent().trim().split(",")));

				if (coordinates == null || coordinates.size() != 4) {

					throw new ValidationException(new ValidationError("UnableToGeneratePNG"));

				}

				Envelope extent = new Envelope(coordinates.get(0), coordinates.get(2), coordinates.get(3), coordinates.get(1));

				centerCoordinate = extent.centre();

			}

			JsonArray center = new JsonArray();

			center.addNode(centerCoordinate.x + "");
			center.addNode(centerCoordinate.y + "");

			printConfig = printConfig.replace("$center", center.toJson());
			printConfig = printConfig.replace("$features", features.toJson());
			printConfig = printConfig.replace("$srs", queryInstance.getEpsg());

			String[] baseLayer = queryInstance.getVisibleBaseLayer().trim().split("#");

			if (baseLayer.length > 1) {
				printConfig = printConfig.replace("$baseLayer", baseLayer[1]);
			}

			String smallPNGConfig = printConfig;

			smallPNGConfig = smallPNGConfig.replace("$dpi", smallPNGResolution.toString());
			smallPNGConfig = smallPNGConfig.replace("$scale", smallPNGScale.toString());
			smallPNGConfig = smallPNGConfig.replace("$layout", smallPNGLayout);

			queryInstance.setSmallPNG(getMapImageFromMapFish(queryInstance, user, smallPNGConfig));

			String largePNGConfig = printConfig;

			largePNGConfig = largePNGConfig.replace("$dpi", largePNGResolution.toString());
			largePNGConfig = largePNGConfig.replace("$scale", largePNGScale.toString());
			largePNGConfig = largePNGConfig.replace("$layout", largePNGLayout);

			queryInstance.setLargePNG(getMapImageFromMapFish(queryInstance, user, largePNGConfig));

		}

	}

	private Coordinate calculateMapCentroid(List<Coordinate> coordinates) {

		if (coordinates.size() == 1) {

			return coordinates.get(0);
		}

		CentroidArea area = new CentroidArea();

		area.add(coordinates.toArray(new Coordinate[coordinates.size()]));

		return area.getCentroid();

	}

	private SerialBlob getMapImageFromMapFish(MapQueryInstanceType queryInstance, User user, String config) throws ValidationException {

		ByteArrayOutputStream outputStream = null;

		try {

			StringReader reader = new StringReader(config);

			StringWriter writer = new StringWriter();

			HTTPUtils.sendHTTPPostRequest(reader, new URL(printServiceAddress + "/pdf/create.json"), writer, "UTF-8", mapFishConnectionTimeout, mapFishReadTimeout);

			String mapImageURL = writer.toString();

			if (mapImageURL != null) {

				mapImageURL = mapImageURL.substring(11, mapImageURL.length() - 2);

				if (HTTPUtils.isValidURL(mapImageURL)) {

					log.info("Generatated map image: " + mapImageURL + " for queryInstance " + queryInstance + " for user " + user);

					outputStream = new ByteArrayOutputStream();

					HTTPUtils.sendHTTPGetRequest(mapImageURL, null, outputStream);

					return new SerialBlob(outputStream.toByteArray());

				}

			}

			log.error("Invalid response from print service when generating png for queryInstance " + queryInstance + " for user " + user);

		} catch (Exception e) {

			log.error("Unable to generate png for queryInstance " + queryInstance + " for user " + user, e);

		} finally {

			StreamUtils.closeStream(outputStream);

		}

		throw new ValidationException(new ValidationError("UnableToGeneratePNG"));

	}

	@Override
	public Document createDocument(HttpServletRequest req, User user) {

		Document doc = super.createDocument(req, user);

		Element document = doc.getDocumentElement();

		XMLUtils.appendNewElement(doc, document, "previewMode", previewMode);
		XMLUtils.appendNewElement(doc, document, "mapScriptURL", mapScriptURL);
		XMLUtils.appendNewElement(doc, document, "requiredQueryMessage", requiredQueryMessage);

		SettingHandler settingHandler = siteProfileHandler != null ? siteProfileHandler.getCurrentSettingHandler(user, req, null) : this.moduleDescriptor.getMutableSettingHandler();

		XMLUtils.appendNewElement(doc, document, "startExtent", settingHandler.getString("BaseMapQuery-startExtent"));
		XMLUtils.appendNewElement(doc, document, "lmUser", settingHandler.getString("BaseMapQuery-lmUser"));
		
		return doc;
	}

	@Override
	public Query createQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		MapQueryType query = getMapQueryClass().newInstance();

		query.setQueryID(descriptor.getQueryID());

		this.queryDAO.add(query, transactionHandler, null);

		query.init(descriptor, getFullAlias() + "/" + configureQueryAlias() + "/" + descriptor.getQueryID());

		return query;

	}

	@Override
	public Query importQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		MapQueryType query = getMapQueryClass().newInstance();
		
		query.setQueryID(descriptor.getQueryID());
		
		query.populate(descriptor.getImportParser().getNode(XMLGenerator.getElementName(query.getClass())));
		
		this.queryDAO.add(query, transactionHandler, null);
		
		return query;
	}
	
	@Override
	public Query getQuery(MutableQueryDescriptor descriptor) throws Throwable {

		checkConfiguration();

		MapQueryType query = this.getQuery(descriptor.getQueryID());

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/" + configureQueryAlias() + "/" + descriptor.getQueryID());

		return query;

	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		checkConfiguration();

		MapQueryType query = this.getQuery(descriptor.getQueryID(), transactionHandler);

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/" + configureQueryAlias() + "/" + descriptor.getQueryID());

		return query;

	}

	@Override
	public QueryInstance getQueryInstance(MutableQueryInstanceDescriptor descriptor, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws Throwable {

		checkConfiguration();

		MapQueryInstanceType queryInstance = null;

		// Check if we should create a new instance or get an existing one
		if (descriptor.getQueryInstanceID() == null) {

			queryInstance = getMapQueryInstanceClass().newInstance();

			queryInstance.setQuery(getQuery(descriptor.getQueryDescriptor().getQueryID()));

			if (queryInstance.getQuery() == null) {

				return null;
			}

			queryInstance.set(descriptor);
			queryInstance.copyQueryValues();

		} else {

			queryInstance = getQueryInstance(descriptor.getQueryInstanceID());

			if (queryInstance == null) {

				return null;
			}

			queryInstance.set(descriptor);

		}

		FCKUtils.setAbsoluteFileUrls(queryInstance.getQuery(), RequestUtils.getFullContextPathURL(req) + ckConnectorModuleAlias);
		
		URLRewriter.setAbsoluteLinkUrls(queryInstance.getQuery(), req);

		TextTagReplacer.replaceTextTags(queryInstance.getQuery(), instanceMetadata.getSiteProfile());

		return queryInstance;

	}

	@Override
	public boolean deleteQuery(ImmutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		MapQueryType query = getQuery(descriptor.getQueryID());

		if (query == null) {

			return false;
		}

		this.queryDAO.delete(query, transactionHandler);

		return true;

	}

	@Override
	public boolean deleteQueryInstance(ImmutableQueryInstanceDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		MapQueryInstanceType queryInstance = getQueryInstance(descriptor.getQueryInstanceID());

		if (queryInstance == null) {

			return false;
		}

		this.queryInstanceDAO.delete(queryInstance, transactionHandler);

		return true;

	}

	@Override
	public void copyQuery(MutableQueryDescriptor sourceQueryDescriptor, MutableQueryDescriptor copyQueryDescriptor, TransactionHandler transactionHandler) throws SQLException {

		MapQueryType query = getQuery(sourceQueryDescriptor.getQueryID(), transactionHandler);

		query.setQueryID(copyQueryDescriptor.getQueryID());

		queryDAO.add(query, transactionHandler, null);

	}

	@Override
	public void save(MapQueryInstanceType queryInstance, TransactionHandler transactionHandler) throws Throwable {

		if (queryInstance.getQueryInstanceID() == null || !queryInstance.getQueryInstanceID().equals(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID())) {

			queryInstance.setQueryInstanceID(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID());

			this.queryInstanceDAO.add(queryInstance, transactionHandler, getSaveMapQueryInstanceRelationQuery());

		} else {

			this.queryInstanceDAO.update(queryInstance, transactionHandler, getSaveMapQueryInstanceRelationQuery());
		}

		removeUnnecessaryRelations(queryInstance);

	}

	@Override
	public String getTitlePrefix() {

		return moduleDescriptor.getName();
	}

	protected MapQueryInstanceType getQueryInstance(Integer queryInstanceID) throws SQLException {

		HighLevelQuery<MapQueryInstanceType> query = new HighLevelQuery<MapQueryInstanceType>(QUERY_RELATION);

		List<Field> getRelations = getMapQueryInstanceGetRelations();

		if (!CollectionUtils.isEmpty(getRelations)) {
			query.addRelations(getRelations);
		}

		List<Field> excludedGetRelations = getMapQueryInstanceExcludedGetRelations();

		if (!CollectionUtils.isEmpty(excludedGetRelations)) {
			query.addExcludedRelations(excludedGetRelations);
		}

		query.addParameter(queryInstanceIDParamFactory.getParameter(queryInstanceID));

		return queryInstanceDAO.get(query);

	}

	protected MapQueryType getQuery(Integer queryID) throws SQLException {

		HighLevelQuery<MapQueryType> query = new HighLevelQuery<MapQueryType>();

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query);
	}

	protected MapQueryType getQuery(Integer queryID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<MapQueryType> query = new HighLevelQuery<MapQueryType>();

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query, transactionHandler);
	}

	protected void checkConfiguration() {

	}

	@WebPublic(alias = "mapconfiguration")
	public ForegroundModuleResponse getMapConfiguration(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (!StringUtils.isEmpty(mapConfiguration)) {

			log.info("User " + user + " requesting map configuration");

			HTTPUtils.sendReponse(mapConfiguration, JsonUtils.getContentType(), res);

		}

		return null;

	}
	
	@WebPublic(alias = "clientprint")
	public ForegroundModuleResponse clientPrint(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if(printServiceAddress != null) {
			
			String param = null;
			
			if((param = uriParser.get(2)) != null && param.equalsIgnoreCase("info.json")) {
				
				String response = HTTPUtils.sendHTTPGetRequest(printServiceAddress + "/pdf/info.json", null, null, null);
				
				if(response != null) {
					
					HTTPUtils.sendReponse(response, JsonUtils.getContentType(), res);
					
				}
				
			}
		
		}
			
		return null;

	}

	@WebPublic(alias = "search")
	public ForegroundModuleResponse search(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (uriParser.size() >= 3) {

			String searchMethod = uriParser.get(2);

			if (httpSearchPUDParam != null && searchMethod.equalsIgnoreCase(httpSearchPUDParam)) {

				searchPUD(req, res, user, uriParser);

			} else if (httpSearchAddressParam != null && searchMethod.equalsIgnoreCase(httpSearchAddressParam)) {

				searchAddress(req, res, user, uriParser);

			} else if (httpSearchPlaceParam != null && searchMethod.equalsIgnoreCase(httpSearchPlaceParam)) {

				searchPlace(req, res, user, uriParser);

			} else if (httpSearchCoordinateParam != null && searchMethod.equalsIgnoreCase(httpSearchCoordinateParam)) {

				searchCoordinate(req, res, user, uriParser);

			} else {

				throw new URINotFoundException(uriParser);

			}

			return null;

		}

		throw new URINotFoundException(uriParser);
	}

	private void searchPUD(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (!enableHttpSearchService || StringUtils.isEmpty(httpSearchPUDParam)) {
			throw new URINotFoundException(uriParser);
		}

		String searchURL = httpSearchServiceURL + "/" + httpSearchPUDParam;

		if (uriParser.size() >= 5) {
			searchURL += "/" + uriParser.get(3) + "/" + uriParser.get(4);
		}

		sendSearchReqest(req, res, user, searchURL);

	}

	private void searchAddress(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (!enableHttpSearchService || StringUtils.isEmpty(httpSearchAddressParam)) {
			throw new URINotFoundException(uriParser);
		}

		sendSearchReqest(req, res, user, httpSearchServiceURL + "/" + httpSearchAddressParam);

	}

	private void searchPlace(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (!enableHttpSearchService || StringUtils.isEmpty(httpSearchPlaceParam)) {
			throw new URINotFoundException(uriParser);
		}

		sendSearchReqest(req, res, user, httpSearchServiceURL + "/" + httpSearchPlaceParam);

	}

	private void searchCoordinate(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (!enableHttpSearchService || StringUtils.isEmpty(httpSearchCoordinateParam)) {
			throw new URINotFoundException(uriParser);
		}

		sendSearchReqest(req, res, user, httpSearchServiceURL + "/" + httpSearchCoordinateParam);

	}

	@SuppressWarnings("unchecked")
	private void sendSearchReqest(HttpServletRequest req, HttpServletResponse res, User user, String search) throws IOException {

		SettingHandler profileSettingHandler = getCurrentSiteProfileSettingHandler(req, user);

		StringBuilder queryParameters = new StringBuilder();

		if (req.getParameterMap() != null) {

			HashMap<String, String[]> paramMap = new HashMap<String, String[]>(req.getParameterMap());

			for (String paramName : paramMap.keySet()) {

				String[] values = paramMap.get(paramName);

				if (values != null) {

					if (paramName.equalsIgnoreCase("lmuser")) {

						continue;

					} else if (paramName.equalsIgnoreCase("q")) {

						String prefix = profileSettingHandler.getString("BaseMapQuery-searchPrefix");

						if (!StringUtils.isEmpty(prefix)) {

							String q = values[0];

							if (!q.toLowerCase().startsWith(prefix.toLowerCase())) {

								queryParameters.append("q=" + URLEncoder.encode(prefix, "UTF-8") + URLEncoder.encode(" " + q, "ISO-8859-1") + "&");

								continue;

							}

						}

					}

					for (String value : values) {

						queryParameters.append(paramName + "=" + URLEncoder.encode(value, "ISO-8859-1") + "&");

					}

				}

			}

		}

		String searchQuery = search + "?" + queryParameters.toString() + "lmuser=" + profileSettingHandler.getString("BaseMapQuery-lmUser");

		try {

			log.info("User " + user + " searching using http search service with query " + searchQuery);

			String response = HTTPUtils.sendHTTPGetRequest(searchQuery, null, null, null);

			HTTPUtils.sendReponse(getUnescapedText(response), JsonUtils.getContentType(), res);

		} catch (IOException e) {

			log.warn("Unable to get any search result from lm search service using query " + searchQuery + ". Caused by: " + e.getMessage());

			JsonObject error = new JsonObject();
			error.putField("Error", "true");

			HTTPUtils.sendReponse(error.toJson(), JsonUtils.getContentType(), res);
		}

	}

	protected IEstate getPropertyUnitDesignation(String propertyUnitDesignation, HttpServletRequest req, User user) throws ValidationException {

		try {

			try {

				return getPUD(propertyUnitDesignation, req, user);

			} catch (RemoteException e) {

				try {

					return getPUD(propertyUnitDesignation, req, user);

				} catch (RemoteException e1) {

					log.error("Unable to connect to LM RMI server " + lmRMIServerAddress + ":" + lmRMIServerPort + " when trying to get pud requested by user " + user);

				}

			}

		} catch (LMAccountException e) {

			log.error("The account for the given municipality is not valid when trying to get pud requested by user " + user);

		} catch (LMUnavailableException e) {

			log.error("Server was unable to contact LM web service API when trying to get pud requested by user " + user);

		}

		throw new ValidationException(new ValidationError("UnableToValidatePUD"));
	}

	private IEstate getPUD(String propertyUnitDesignation, HttpServletRequest req, User user) throws RemoteException, LMAccountException, LMUnavailableException {

		ValidateLM searchLM = getSearchLMClient();

		if (searchLM != null) {

			return searchLM.validateEstate(propertyUnitDesignation, getCurrentSiteProfileSettingHandler(req, user).getString("BaseMapQuery-lmUser"));

		}

		return null;

	}

	protected IEstate getPropertyUnitDesignation(Double x, Double y, HttpServletRequest req, User user) throws ValidationException {

		try {

			try {

				return getPUD(x, y, req, user);

			} catch (RemoteException e) {

				try {

					return getPUD(x, y, req, user);

				} catch (RemoteException e1) {

					log.error("Unable to connect to LM RMI server " + lmRMIServerAddress + ":" + lmRMIServerPort + " when trying to get pud by coordinate requested by user " + user);

				}

			}

		} catch (LMAccountException e) {

			log.error("The account for the given municipality is not valid when trying to get pud by coordinate requested by user " + user);

		} catch (LMUnavailableException e) {

			log.error("Server was unable to contact LM web service API when trying to get pud by coordinate requested by user " + user);

		}

		throw new ValidationException(new ValidationError("UnableToValidatePUD"));
	}

	private IEstate getPUD(Double x, Double y, HttpServletRequest req, User user) throws ValidationException, RemoteException, LMAccountException, LMUnavailableException {

		ValidateLM searchLM = getSearchLMClient();

		if (searchLM != null) {

			return searchLM.getEstateByCoordinate(x, y, this.getCurrentSiteProfileSettingHandler(req, user).getString("BaseMapQuery-lmUser"));

		}

		return null;

	}

	public abstract ForegroundModuleResponse configureQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception;

	protected abstract Class<MapQueryType> getMapQueryClass();

	protected abstract Class<MapQueryInstanceType> getMapQueryInstanceClass();

	protected abstract String configureQueryAlias();

	protected void removeUnnecessaryRelations(MapQueryInstanceType queryInstance) {

	}

	protected List<Field> getMapQueryInstanceGetRelations() {

		return null;
	}

	protected List<Field> getMapQueryInstanceExcludedGetRelations() {

		return null;
	}

	protected RelationQuery getSaveMapQueryInstanceRelationQuery() {

		return null;
	}

	private ValidateLM getSearchLMClient() {

		try {

			if (searchLM != null) {

				return searchLM;
			}

			Registry registry = LocateRegistry.getRegistry(lmRMIServerAddress, lmRMIServerPort);

			return (ValidateLM) registry.lookup(lmRMIServerName);

		} catch (RemoteException e) {

			log.error("Unable to connect to LM RMI server " + lmRMIServerAddress + ":" + lmRMIServerPort + " (" + lmRMIServerName + ")");

		} catch (NotBoundException e) {

			log.error("LM RMI server " + lmRMIServerAddress + ":" + lmRMIServerPort + " (" + lmRMIServerName + ") not bounded");

		}

		return null;
	}

	private String getUnescapedText(String text) {

		if (text != null) {

			Charset utf8charset = Charset.forName("UTF-8");

			Charset iso88591charset = Charset.forName("ISO-8859-1");

			ByteBuffer inputBuffer = ByteBuffer.wrap(text.getBytes());

			CharBuffer data = utf8charset.decode(inputBuffer);

			ByteBuffer outputBuffer = iso88591charset.encode(data);

			text = new String(outputBuffer.array());

		}

		return text;
	}

	@InstanceManagerDependency(required = true)
	public void setSiteProfileHandler(SiteProfileHandler siteProfileHandler) {

		if (siteProfileHandler != null) {

			siteProfileHandler.addSettingProvider(this);

		} else {

			this.siteProfileHandler.removeSettingProvider(this);
		}

		this.siteProfileHandler = siteProfileHandler;
	}

	@Override
	public List<Setting> getSiteProfileSettings() {

		return Arrays.asList((Setting) startExtentSetting, (Setting) lmUserSetting, (Setting) searchPrefixSetting);
	}

	protected SettingHandler getCurrentSiteProfileSettingHandler(HttpServletRequest req, User user) {

		return siteProfileHandler != null ? siteProfileHandler.getCurrentSettingHandler(user, req, null) : this.moduleDescriptor.getMutableSettingHandler();
	}

	@Override
	public void unload() throws Exception {

		if (siteProfileHandler != null) {

			siteProfileHandler.removeSettingProvider(this);
		}

		super.unload();

	}

	@Override
	protected void appendPDFData(Document doc, Element showQueryValuesElement, MapQueryInstanceType queryInstance) {

		super.appendPDFData(doc, showQueryValuesElement, queryInstance);

		if (queryInstance.getQuery().getDescription() != null) {

			XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "Description", JTidyUtils.getXHTML(queryInstance.getQuery().getDescription()));
			XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "isHTMLDescription", queryInstance.getQuery().getDescription().contains("<") && queryInstance.getQuery().getDescription().contains(">"));
		}
	}

	@Override
	protected List<PDFAttachment> getPDFAttachments(MapQueryInstanceType queryInstance) {

		List<PDFAttachment> attachments = new ArrayList<PDFAttachment>(2);

		if (queryInstance.getSmallPNG() != null) {

			attachments.add(new BlobPDFAttachment(queryInstance.getSmallPNG(), this.pdfAttachmentFilename.replace("$scale", this.smallPNGScale + ""), this.pdfAttachmentDescriptionPrefix + " " + queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getName()));

		}

		if (queryInstance.getLargePNG() != null) {

			attachments.add(new BlobPDFAttachment(queryInstance.getLargePNG(), this.pdfAttachmentFilename.replace("$scale", this.largePNGScale + ""), this.pdfAttachmentDescriptionPrefix + " " + queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getName()));

		}

		if (!attachments.isEmpty()) {

			return attachments;
		}

		return null;
	}

	@Override
	protected PDFResourceProvider getPDFResourceProvider(MapQueryInstanceType queryInstance) {

		if (queryInstance.getSmallPNG() == null) {

			return null;
		}

		return new BlobResourceProvider(queryInstance.getSmallPNG());

	}

	@Override
	public QueryRequestProcessor getQueryRequestProcessor(MapQueryInstanceType queryInstance, HttpServletRequest req, User user) throws IOException {

		if (previewMode.equals(BaseMapQueryPreviewMode.WEB_MAP) || req.getParameter("mapimage") == null) {

			return null;
		}

		if (previewMode.equals(BaseMapQueryPreviewMode.SMALL_PNG) && queryInstance.getSmallPNG() != null) {

			return new BaseMapImageRequestProcessor(previewMode.toString(), queryInstance.getSmallPNG());

		} else if (previewMode.equals(BaseMapQueryPreviewMode.LARGE_PNG) && queryInstance.getLargePNG() != null) {

			return new BaseMapImageRequestProcessor(previewMode.toString(), queryInstance.getLargePNG());

		}

		return null;

	}

}
