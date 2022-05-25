package com.selectpdf;

/**
 * Get the locations of certain web elements. This is retrieved if pdf_web_elements_selectors parameter was set during the initial conversion call and elements were found to match the selectors.
 */
public class WebElementsClient extends ApiClient {
    /**
     * Construct the web elements client.
     * @param apiKey API Key.
     * @param jobId Job ID.
     */
    public WebElementsClient(String apiKey, String jobId)
    {
        apiEndpoint = "https://selectpdf.com/api2/webelements/";
        parameters.put("key", apiKey);
        parameters.put("job_id", jobId);
    }
    
    /**
     * Get the locations of certain web elements. This is retrieved if pdf_web_elements_selectors parameter is set and elements were found to match the selectors.
     * @return List of web elements locations.
     */
    public String getWebElements() {
        headers.put("Accept", "text/json");

        try
        {
            byte[] result = performPost(null);

            String elements = new String(result);
            return elements;
        }
        catch (Exception ex)
        {
            throw new ApiException("Could not get API web elements.");
        }

    }
}
