package se.unlogic.hierarchy.core.utils.crud;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;

import se.unlogic.fileuploadutils.MultipartRequest;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.io.BinarySizes;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;

public class MultipartRequestFilter implements RequestFilter {

	protected final Logger log = Logger.getLogger(this.getClass());
	
	protected final MultipartLimitProvider limitProvider;

	
	public MultipartRequestFilter(MultipartLimitProvider limitProvider) {

		super();
		this.limitProvider = limitProvider;
	}

	@Override
	public HttpServletRequest parseRequest(HttpServletRequest req, User user) throws ValidationException {


		if(MultipartRequest.isMultipartRequest(req)){

			try {
				log.info("Parsing multipart request from user " + user + "...");
				
				return new MultipartRequest(limitProvider.getRamThreshold() * BinarySizes.KiloByte, limitProvider.getMaxRequestSize() * BinarySizes.MegaByte, limitProvider.getTempDir(), req);

			} catch (SizeLimitExceededException e) {

				throw new ValidationException(new ValidationError("FileSizeLimitExceeded"));

			} catch (FileSizeLimitExceededException e) {

				throw new ValidationException(new ValidationError("FileSizeLimitExceeded"));

			} catch (FileUploadException e) {

				throw new ValidationException(new ValidationError("UnableToParseRequest"));
			}
		}

		return req;
	}

	@Override
	public void releaseRequest(HttpServletRequest req, User user) {

		if (req instanceof MultipartRequest) {

			((MultipartRequest)req).deleteFiles();
		}
	}
}
