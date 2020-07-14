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
         * Parameters that will be sent to the API.
         */
        protected HashMap<String, String> parameters = new HashMap<String, String>();

        /**
         * HTTP Headers that will be sent to the API.
         */
        protected HashMap<String, String> headers = new HashMap<String, String>();

        /**
         * Set a custom SelectPdf API endpoint. Do not use this method unless advised by SelectPdf.
         * @param apiEndpoint API endpoint.
         */
        public void setApiEndpoint(String apiEndpoint)
        {
            this.apiEndpoint = apiEndpoint;
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
         * Create a POST request.
         * @param outStream Output response to this stream, if specified.
         * @return If output stream is not specified, return response as byte array.
         */
        protected byte[] performPost(OutputStream outStream)
        {
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
                if (urlConnection.getResponseCode() == 200) {
                    // All OK - Get the response stream with the content returned by the server

                    InputStream inStream = urlConnection.getInputStream();

                    if (outStream != null) {
                        copyStream(inStream, outStream);
                        inStream.close();
                        return null;
                    }
    
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    copyStream(inStream, output);
                    inStream.close();
                    return output.toByteArray();
    
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
                    throw new ApiException(String.format("(%d) %s", urlConnection.getResponseCode(), error));

                }
            }
            catch (IOException ex) {
                throw new ApiException(ex);
            }
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