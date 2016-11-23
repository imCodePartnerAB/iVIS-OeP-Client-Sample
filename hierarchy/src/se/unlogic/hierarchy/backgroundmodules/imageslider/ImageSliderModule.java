package se.unlogic.hierarchy.backgroundmodules.imageslider;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.backgroundmodules.AnnotatedBackgroundModule;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.DropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.HTMLEditorSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;

public class ImageSliderModule extends AnnotatedBackgroundModule {

	@ModuleSetting
	@HTMLEditorSettingDescriptor(name="Image slider HTML",description="The HTML of the image slider",required=true)
	@XSLVariable(name="imageSliderHTML")
	protected String imageSliderHTML = "This string should be set by your XSL stylesheet";

	@ModuleSetting
	@DropDownSettingDescriptor(name="Animation Effect",description="Specifies the animation effect",required=true,values={"fade", "fold", "sliceDown", "random"},valueDescriptions={"Fade", "Fold", "Slicedown", "Random"})
	protected String animationEffect = "fade";
	
	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Animation speed (ms)", description = "Specifies the slide transition speed in milliseconds", required = false, formatValidator = PositiveStringIntegerValidator.class)
	protected int animationSpeed = 500;
	
	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Pause time (ms)", description = "Specifies the pause time between slides in  milliseconds", required = false, formatValidator = PositiveStringIntegerValidator.class)
	protected int pauseTime = 3000;
	
	@ModuleSetting
	@CheckboxSettingDescriptor(name="Use direction navigation",description="Controls whether direction navigation should be used or not")	
	protected boolean directionNavigation = false;
	
	@Override
	public BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		Document doc = this.createDocument(req, uriParser, user);
		Element imageSliderElement = doc.createElement("ImageSlider");
		doc.getFirstChild().appendChild(imageSliderElement);

		XMLUtils.appendNewElement(doc, imageSliderElement, "animationEffect", animationEffect);
		XMLUtils.appendNewElement(doc, imageSliderElement, "animationSpeed", animationSpeed);
		XMLUtils.appendNewElement(doc, imageSliderElement, "pauseTime", pauseTime);
		XMLUtils.appendNewElement(doc, imageSliderElement, "directionNavigation", directionNavigation);
		XMLUtils.appendNewElement(doc, imageSliderElement, "imageSliderHTML", URLRewriter.setAbsoluteLinkUrls(imageSliderHTML, req));
					
		return new SimpleBackgroundModuleResponse(doc);
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}
	
}
