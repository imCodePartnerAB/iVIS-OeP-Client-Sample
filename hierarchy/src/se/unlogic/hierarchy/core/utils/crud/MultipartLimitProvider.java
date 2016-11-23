package se.unlogic.hierarchy.core.utils.crud;


public interface MultipartLimitProvider {

	/**
	 * @return Maximum size of files in KB to be buffered in RAM during file uploads. Files exceeding the threshold are written to disk instead
	 */
	public int getRamThreshold();
	
	/**
	 * @return Maximum upload size in megabytes allowed in a single request
	 */
	public long getMaxRequestSize();
	
	/**
	 * @return Directory for temporary files
	 */
	public String getTempDir();
}
