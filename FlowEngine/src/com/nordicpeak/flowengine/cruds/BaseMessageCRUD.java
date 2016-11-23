package com.nordicpeak.flowengine.cruds;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.fileuploadutils.MultipartRequest;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.validationerrors.FileSizeLimitExceededValidationError;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.io.BinarySizes;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.mime.MimeUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.webutils.http.HTTPUtils;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.BaseFlowModule;
import com.nordicpeak.flowengine.beans.BaseAttachment;
import com.nordicpeak.flowengine.beans.BaseMessage;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowFamily;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.interfaces.FlowInstanceAccessController;

public abstract class BaseMessageCRUD<MessageType extends BaseMessage, AttachmentType extends BaseAttachment> {

	protected Logger log = Logger.getLogger(BaseMessageCRUD.class);

	protected AnnotatedDAO<MessageType> messageDAO;

	protected AnnotatedDAO<AttachmentType> attachmentDAO;

	protected BaseFlowModule baseFlowModule;

	protected Class<MessageType> messageClass;

	protected Class<AttachmentType> attachmentClass;

	protected QueryParameterFactory<AttachmentType, Integer> attachmentIDParamFactory;

	public BaseMessageCRUD(AnnotatedDAO<MessageType> messageDAO, AnnotatedDAO<AttachmentType> attachmentDAO, BaseFlowModule baseFlowModule, Class<MessageType> messageClass, Class<AttachmentType> attachmentClass) {

		this.messageDAO = messageDAO;
		this.attachmentDAO = attachmentDAO;
		this.baseFlowModule = baseFlowModule;
		this.messageClass = messageClass;
		this.attachmentClass = attachmentClass;
		this.attachmentIDParamFactory = attachmentDAO.getParamFactory("attachmentID", Integer.class);

	}

	protected List<AttachmentType> getAttachments(HttpServletRequest req, User user, List<ValidationError> errors) throws SerialException, SQLException {

		if (!(req instanceof MultipartRequest)) {

			return null;
		}

		MultipartRequest multipartRequest = (MultipartRequest) req;

		if (multipartRequest.getFileCount() > 0) {

			Collection<FileItem> files = multipartRequest.getFiles();

			Iterator<FileItem> fileIterator = files.iterator();

			while (fileIterator.hasNext()) {

				FileItem fileItem = fileIterator.next();

				if (StringUtils.isEmpty(fileItem.getName()) || fileItem.getSize() == 0) {

					fileIterator.remove();
					continue;
				}

			}

			List<AttachmentType> attachments = new ArrayList<AttachmentType>();

			for (FileItem fileItem : files) {

				AttachmentType attachment = this.getNewAttachmentInstance();

				String fileName = FilenameUtils.getName(fileItem.getName());

				attachment.setFilename(fileName);
				attachment.setSize(fileItem.getSize());
				attachment.setData(new SerialBlob(fileItem.get()));

				attachments.add(attachment);
			}

			return attachments;

		}

		return null;

	}

	protected HttpServletRequest parseRequest(HttpServletRequest req, List<ValidationError> errors) {

		if (MultipartRequest.isMultipartRequest(req)) {

			try {

				return new MultipartRequest(baseFlowModule.getRamThreshold() * BinarySizes.KiloByte, baseFlowModule.getMaxRequestSize() * BinarySizes.MegaByte, baseFlowModule.getTempDir(), req);

			} catch (SizeLimitExceededException e) {

				errors.add(new FileSizeLimitExceededValidationError(null, e.getActualSize(), e.getPermittedSize()));

			} catch (FileSizeLimitExceededException e) {

				errors.add(new FileSizeLimitExceededValidationError(e.getFileName(), e.getActualSize(), e.getPermittedSize()));

			} catch (FileUploadException e) {

				errors.add(new ValidationError("UnableToParseRequest"));

			}
		}

		return req;
	}

	protected AttachmentType getNewAttachmentInstance() {

		try {

			return attachmentClass.newInstance();

		} catch (InstantiationException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}

	}

	public ForegroundModuleResponse getRequestedMessageAttachment(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, FlowInstanceAccessController previewAccessController) throws SQLException, AccessDeniedException, URINotFoundException {

		MessageType message;

		HighLevelQuery<MessageType> query = new HighLevelQuery<MessageType>();

		query.addRelations(getFlowInstanceRelation(), FlowInstance.FLOW_STATE_RELATION, FlowInstance.FLOW_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION);
		
		if (uriParser.size() == 4 && NumberUtils.isInt(uriParser.get(2)) && NumberUtils.isInt(uriParser.get(3)) && (message = messageDAO.get(query)) != null) {

			previewAccessController.checkFlowInstanceAccess(message.getFlowInstance(), user);

			HighLevelQuery<AttachmentType> attachmentQuery = new HighLevelQuery<AttachmentType>();

			attachmentQuery.addParameter(attachmentIDParamFactory.getParameter(NumberUtils.toInt(uriParser.get(3))));

			AttachmentType attachment = attachmentDAO.get(attachmentQuery);

			if (attachment != null) {

				log.info("User " + user + " downloading " + getTypeLogName() + " message attachment " + attachment);
				
				InputStream in = null;
				OutputStream out = null;

				try {

					Blob data = attachment.getData();

					HTTPUtils.setContentLength(data.length(), res);

					res.setContentType(MimeUtils.getMimeType(attachment.getFilename()));
					res.setHeader("Content-Disposition", "inline; filename=\"" + FileUtils.toValidHttpFilename(attachment.getFilename()) + "\"");

					in = data.getBinaryStream();

					out = res.getOutputStream();

					StreamUtils.transfer(in, out);

				} catch (RuntimeException e) {

					log.debug("Caught exception " + e + " while sending message attachment " + attachment.getFilename() + " to " + user);

				} catch (IOException e) {

					log.debug("Caught exception " + e + " while sending message attachment " + attachment.getFilename() + " to " + user);

				} finally {

					StreamUtils.closeStream(in);
					StreamUtils.closeStream(out);
				}
				
				return null;

			}

		}

		throw new URINotFoundException(uriParser);
		
	}
	
	public abstract MessageType add(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, User user, Document doc, Element element, FlowInstance flowInstance) throws SQLException, IOException;

	protected abstract Field getFlowInstanceRelation();

	protected abstract String getTypeLogName();
	
}
