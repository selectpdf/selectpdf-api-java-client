package com.selectpdf;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Base class for API clients. Do not use this directly.
 */
public class ApiClient {
    /**
     * API endpoint
     */
    protected String apiEndpoint = "https://selectpdf.com/api2/convert/";

    /**
     * API async jobs endpoint
     */
    protected String apiAsyncEndpoint = "https://selectpdf.com/api2/asyncjob/";

    /**
     * API web elements endpoint
     */
    protected String apiWebElementsEndpoint = "https://selectpdf.com/api2/webelements/";

    /**
     * Parameters that will be sent to the API.
     */
    protected HashMap<String, String> parameters = new HashMap<String, String>();

    /**
     * HTTP Headers that will be sent to the API.
     */
    protected HashMap<String, String> headers = new HashMap<String, String>();

    /**
     * Files that will be sent to the API.
     */
    protected HashMap<String, String> files = new HashMap<String, String>();

    /**
     * Binary data that will be sent to the API.
     */
    protected HashMap<String, byte[]> binaryData = new HashMap<String, byte[]>();

    /**
     * Number of pages of the pdf document resulted from the conversion.
     */
    protected int numberOfPages = 0;

    /**
     * Job ID for asynchronous calls or for calls that require a second request.
     */
    protected String jobId = "";

    /**
     * Last HTTP Code
     */
    protected int lastHTTPCode = 0;

    /**
     * Library version
     */
    public static final String CLIENT_VERSION = "1.4.0";
    
    protected static final String MULTIPART_FORM_DATA_BOUNDARY = "------------SelectPdf_Api_Boundry_$";
    protected static final String NEW_LINE = "\r\n";

    /**
     * Ping interval in seconds for asynchronous calls. Default value is 3 seconds.
     */
    public int AsyncCallsPingInterval = 3;

    /**
     * Maximum number of pings for asynchronous calls. Default value is 1,000 pings.
     */
    public int AsyncCallsMaxPings = 1000;

    /**
     * Set a custom SelectPdf API endpoint. Do not use this method unless advised by SelectPdf.
     * @param apiEndpoint API endpoint.
     */
    public void setApiEndpoint(String apiEndpoint)
    {
        this.apiEndpoint = apiEndpoint;
    }

    /**
     * Set a custom SelectPdf API endpoint for async jobs. Do not use this method unless advised by SelectPdf.
     * @param apiAsyncEndpoint API endpoint.
     */
    public void setApiAsyncEndpoint(String apiAsyncEndpoint)
    {
        this.apiAsyncEndpoint = apiAsyncEndpoint;
    }

    /**
     * Set a custom SelectPdf API endpoint for web elements. Do not use this method unless advised by SelectPdf.
     * @param apiWebElementsEndpoint API endpoint.
     */
    public void setApiWebElementsEndpoint(String apiWebElementsEndpoint)
    {
        this.apiWebElementsEndpoint = apiWebElementsEndpoint;
    }

    /**
     * Get the number of pages processed from the PDF document.
     * @return Number of pages processed from the PDF document.
     */
    public int getNumberOfPages() {
        return numberOfPages;
    }

    /**
     * Serialize parameters.
     * @return Serialized parameters.
     */
    protected String serializeParameters() {
        try {
            StringBuilder data = new StringBuilder();
            for (Map.Entry<String,String> param : parameters.entrySet()) {
                if (data.length() != 0) data.append('&');
                data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                data.append('=');
                data.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }

            return data.toString();
        }
        catch (UnsupportedEncodingException ex) {
            throw new ApiException("Error while serializing POST parameters.");
        }
    }

    /**
     * Serialize dictionary.
     * @param dictionaryToSerialize Dictionary to serialize.
     * @return Serialized dictionary.
     */
    protected String serializeDictionary(HashMap<String, String> dictionaryToSerialize) {
        try {
            StringBuilder data = new StringBuilder();
            for (Map.Entry<String,String> entry : dictionaryToSerialize.entrySet()) {
                if (data.length() != 0) data.append('&');
                data.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                data.append('=');
                data.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return data.toString();
        }
        catch (UnsupportedEncodingException ex) {
            throw new ApiException("Error while serializing dictionary.");
        }
    }

    /**
     * Create a POST request.
     * @param outStream Output response to this stream, if specified.
     * @return If output stream is not specified, return response as byte array.
     */
    protected byte[] performPost(OutputStream outStream)
    {
        headers.put("selectpdf-api-client", String.format("java-%s-%s", System.getProperty("java.version"), CLIENT_VERSION));

        // reset results
        numberOfPages = 0;
        jobId = "";
        lastHTTPCode = 0;

        HttpURLConnection urlConnection = null;

        try {
            // serialize parameters
            String serializedParameters = serializeParameters();
            byte[] byteData = serializedParameters.getBytes("UTF-8");
        
            URL apiUrl = new URL(apiEndpoint);
            urlConnection = (HttpURLConnection)apiUrl.openConnection();

            urlConnection.setRequestMethod("POST");
            for (Map.Entry<String,String> header : headers.entrySet()) { 
                urlConnection.setRequestProperty(header.getKey(), header.getValue()); // send headers
            }
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(byteData.length));
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(600000); //600,000ms=600s=10min
            
            // POST parameters
            OutputStream connectioOutputStream = urlConnection.getOutputStream();
            connectioOutputStream.write(byteData);
            connectioOutputStream.flush();
            connectioOutputStream.close();

            // GET response
            int statusCode = urlConnection.getResponseCode();
            lastHTTPCode = statusCode;

            if (statusCode == 200) {
                // All OK - Get the response stream with the content returned by the server

                String value = urlConnection.getHeaderField("selectpdf-api-pages");
                if (value != null && !value.isBlank())
                {
                    numberOfPages = Integer.parseInt(value);
                }
                value = urlConnection.getHeaderField("selectpdf-api-jobid");
                if (value != null && !value.isBlank())
                {
                    jobId = value;
                }

                InputStream inStream = urlConnection.getInputStream();

                if (outStream != null) {
                    copyStream(inStream, outStream);
                    inStream.close();
                    urlConnection.disconnect();
                    return null;
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                copyStream(inStream, output);
                inStream.close();
                urlConnection.disconnect();
                return output.toByteArray();

            }
            else if (statusCode == 202) {
                // request accepted (for asynchronous jobs)
                
                String value = urlConnection.getHeaderField("selectpdf-api-jobid");
                if (value != null && !value.isBlank())
                {
                    jobId = value;
                }

                urlConnection.disconnect();
                return null;
            }
            else {
                // error

                String error;
                if (urlConnection.getErrorStream() != null) {
                    ByteArrayOutputStream errOutput = new ByteArrayOutputStream();
                    copyStream(urlConnection.getErrorStream(), errOutput);
                    error = errOutput.toString();
                }
                else {
                    error = urlConnection.getResponseMessage();
                }
                urlConnection.disconnect();
                throw new ApiException(String.format("(%d) %s", statusCode, error));

            }
        }
        catch (IOException ex) {
            throw new ApiException(ex);
        }
    }

    /**
     * Create a multipart/form-data POST request (that can handle file uploads).
     * @param outStream Output response to this stream, if specified.
     * @return If output stream is not specified, return response as byte array.
     */
    protected byte[] performPostAsMultipartFormData(OutputStream outStream)
    {
        headers.put("selectpdf-api-client", String.format("java-%s-%s", System.getProperty("java.version"), CLIENT_VERSION));

        // reset results
        numberOfPages = 0;
        jobId = "";
        lastHTTPCode = 0;

        HttpURLConnection urlConnection = null;

        try {
            // serialize parameters
            byte[] byteData = encodeMultipartFormData();
        
            URL apiUrl = new URL(apiEndpoint);
            urlConnection = (HttpURLConnection)apiUrl.openConnection();

            urlConnection.setRequestMethod("POST");
            for (Map.Entry<String,String> header : headers.entrySet()) { 
                urlConnection.setRequestProperty(header.getKey(), header.getValue()); // send headers
            }
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + MULTIPART_FORM_DATA_BOUNDARY);
            urlConnection.setRequestProperty("Content-Length", String.valueOf(byteData.length));
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(600000); //600,000ms=600s=10min
            
            // POST parameters
            OutputStream connectioOutputStream = urlConnection.getOutputStream();
            connectioOutputStream.write(byteData);
            connectioOutputStream.flush();
            connectioOutputStream.close();

            // GET response
            int statusCode = urlConnection.getResponseCode();
            lastHTTPCode = statusCode;
            
            if (statusCode == 200) {
                // All OK - Get the response stream with the content returned by the server

                String value = urlConnection.getHeaderField("selectpdf-api-pages");
                if (value != null && !value.isBlank())
                {
                    numberOfPages = Integer.parseInt(value);
                }
                value = urlConnection.getHeaderField("selectpdf-api-jobid");
                if (value != null && !value.isBlank())
                {
                    jobId = value;
                }

                InputStream inStream = urlConnection.getInputStream();

                if (outStream != null) {
                    copyStream(inStream, outStream);
                    inStream.close();
                    urlConnection.disconnect();
                    return null;
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                copyStream(inStream, output);
                inStream.close();
                urlConnection.disconnect();
                return output.toByteArray();

            }
            else if (statusCode == 202) {
                // request accepted (for asynchronous jobs)
                
                String value = urlConnection.getHeaderField("selectpdf-api-jobid");
                if (value != null && !value.isBlank())
                {
                    jobId = value;
                }

                urlConnection.disconnect();
                return null;
            }
            else {
                // error

                String error;
                if (urlConnection.getErrorStream() != null) {
                    ByteArrayOutputStream errOutput = new ByteArrayOutputStream();
                    copyStream(urlConnection.getErrorStream(), errOutput);
                    error = errOutput.toString();
                }
                else {
                    error = urlConnection.getResponseMessage();
                }
                urlConnection.disconnect();
                throw new ApiException(String.format("(%d) %s", statusCode, error));

            }
        }
        catch (IOException ex) {
            throw new ApiException(ex);
        }
    }

    /**
     * Encode all parameters, files and raw data.
     * @return Encoded data.
     */
    private byte[] encodeMultipartFormData() {
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();

            // encode regular parameters
            String sParameters = "";

            for(Map.Entry<String, String> parameter: parameters.entrySet()) {
                sParameters += "--" + MULTIPART_FORM_DATA_BOUNDARY + NEW_LINE;
                sParameters += String.format("Content-Disposition: form-data; name=\"%s\"", parameter.getKey()) + NEW_LINE;
                sParameters += NEW_LINE;
                sParameters += parameter.getValue() + NEW_LINE;
            }

            data.write(sParameters.getBytes("UTF-8"));

            // encode files
            for(Map.Entry<String, String> fileDataEntry: files.entrySet()) {
                String sFileEncoding = "--" + MULTIPART_FORM_DATA_BOUNDARY + NEW_LINE;
                sFileEncoding += String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", fileDataEntry.getKey(), fileDataEntry.getValue()) + NEW_LINE;
                sFileEncoding += "Content-Type: application/octet-stream" + NEW_LINE;
                sFileEncoding += NEW_LINE;
                data.write(sFileEncoding.getBytes("UTF-8"));

                // file content
                copyStream(new FileInputStream(fileDataEntry.getValue()), data);

                data.write(NEW_LINE.getBytes("UTF-8"));
            }

            // encode additional binary data
            for(Map.Entry<String, byte[]> binaryDataEntry: binaryData.entrySet()) {
                String sFileEncoding = "--" + MULTIPART_FORM_DATA_BOUNDARY + NEW_LINE;
                sFileEncoding += String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", binaryDataEntry.getKey(), binaryDataEntry.getKey()) + NEW_LINE;
                sFileEncoding += "Content-Type: application/octet-stream" + NEW_LINE;
                sFileEncoding += NEW_LINE;
                data.write(sFileEncoding.getBytes("UTF-8"));

                // binary content
                data.write(binaryDataEntry.getValue());

                data.write(NEW_LINE.getBytes("UTF-8"));
            }

            // final boundary
            sParameters = "--" + MULTIPART_FORM_DATA_BOUNDARY + "--" + NEW_LINE;
            sParameters += NEW_LINE;

            data.write(sParameters.getBytes("UTF-8"));
            data.flush();

            return data.toByteArray();
        }
        catch(UnsupportedEncodingException ex) {
            throw new ApiException(ex);
        }
        catch(IOException ex) {
            throw new ApiException(ex);
        }
}

    /**
     * Start an asynchronous job.
     * @return Asynchronous job ID.
     */
    public String startAsyncJob() {
        parameters.put("async", "True");
        performPost(null);
        return jobId;
    }

    /**
     * Start an asynchronous job that requires multipart forma data.
     * @return Asynchronous job ID.
     */
    public String startAsyncJobMultipartFormData() {
        parameters.put("async", "True");
        performPostAsMultipartFormData(null);
        return jobId;
    }

    /**
     * Copy from one stream into another.
     * @param input Input stream.
     * @param output Output stream.
     * @throws IOException
     */
    private void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] bytes = new byte[8192];
        while (true) {
            int bytesNumber = input.read(bytes, 0, 8192);
            if (bytesNumber == -1) break;
            output.write(bytes, 0, bytesNumber);
        }
    }


}