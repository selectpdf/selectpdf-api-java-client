package com.selectpdf;

/**
 * Get usage details for SelectPdf Online API.
 */
public class UsageClient extends ApiClient {
    /**
     * Construct the Usage client.
     * @param apiKey API Key.
     */
    public UsageClient(String apiKey)
    {
        apiEndpoint = "https://selectpdf.com/api2/usage/";
        parameters.put("key", apiKey);
    }

    /**
     * Get API usage information.
     * @return Usage information as JSON string.
     */
    public String getUsage()
    {
        return getUsage(false);
    }

    /**
     * Get API usage information with history if specified.
     * @param getHistory Get history if set.
     * @return Usage information as JSON string.
     */
    public String getUsage(boolean getHistory)
    {
        headers.put("Accept", "text/json");

        if (getHistory)
        {
            parameters.put("get_history", "True");
        }

        try
        {
            byte[] result = performPost(null);

            String usage = new String(result);
            return usage;
        }
        catch (Exception ex)
        {
            throw new ApiException("Could not get API usage.");
        }
    }
}