package com.selectpdf;

/**
 * Get the result of an asynchronous call.
 */
public class AsyncJobClient extends ApiClient {
    /**
     * Construct the async job client.
     * @param apiKey API Key.
     * @param jobId Job ID.
     */
    public AsyncJobClient(String apiKey, String jobId)
    {
        apiEndpoint = "https://selectpdf.com/api2/asyncjob/";
        parameters.put("key", apiKey);
        parameters.put("job_id", jobId);
    }
    
    /**
     * Get result of the asynchronous job.
     * @return Byte array containing the resulted file if the job is finished. Returns Null if the job is still running. Throws an exception if an error occurred.
     */
    public byte[] getResult()
    {
        byte[] result = performPost(null);

        if (jobId != null && !jobId.isBlank()) {
            return null;
        }
        else {
            return result;
        }
    }

    /**
     * Check if asynchronous job is finished.
     * @return True if job finished.
     */
    public Boolean finished()
    {
        // 200 OK - the job is finished (successfully). 
        // 202 Accepted - the job is still running. 
        // 499 (or some other error code) - error - job is finished (with error).
        return this.lastHTTPCode != 202;
    }

}
