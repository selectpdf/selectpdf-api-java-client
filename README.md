# SelectPdf Online REST API - Java Client

SelectPdf Online REST API is a professional solution for managing PDF documents online. It now has a dedicated, easy to use, Java client library that can be setup in minutes.

## Installation

Download [selectpdf-api-java-client-1.4.0.zip](https://github.com/selectpdf/selectpdf-api-java-client/releases/download/1.4.0/selectpdf-api-java-client-1.4.0.zip), unzip it and copy selectpdf-api-java-client-1.4.0.jar to your CLASSPATH.

OR

Install SelectPdf Java Client for Online API via Maven: [SelectPdf on Maven](https://search.maven.org/artifact/com.selectpdf/selectpdf-api-client/1.4.0/jar).

```
<dependency>
  <groupId>com.selectpdf</groupId>
  <artifactId>selectpdf-api-client</artifactId>
  <version>1.4.0</version>
</dependency>
```

OR

Install the client library by Gradle (e.g. for Android Studio) from Maven by adding the following line to your Gradle dependencies:

```
implementation 'com.selectpdf:selectpdf-api-client:1.4.0'
```

OR

Clone [selectpdf-api-java-client](https://github.com/selectpdf/selectpdf-api-java-client) from Github and build the library.

```
git clone https://github.com/selectpdf/selectpdf-api-java-client
cd selectpdf-api-java-client
mvn install
```

## HTML To PDF API - Java Client

SelectPdf HTML To PDF Online REST API is a professional solution that lets you create PDF from web pages and raw HTML code in your applications. The API is easy to use and the integration takes only a few lines of code.

### Features

* Create PDF from any web page or html string.
* Full html5/css3/javascript support.
* Set PDF options such as page size and orientation, margins, security, web page settings.
* Set PDF viewer options and PDF document information.
* Create custom headers and footers for the pdf document.
* Hide web page elements during the conversion.
* Automatically generate bookmarks during the html to pdf conversion.
* Support for partial page conversion.
* Easy integration, no third party libraries needed.
* Works in all programming languages.
* No installation required.

Sign up for for free to get instant API access to SelectPdf [HTML to PDF API](https://selectpdf.com/html-to-pdf-api/).

### Sample Code

```java
package com.selectpdf;

public class HtmlToPdfMain {
    public static void main(String[] args) throws Exception {
        String url = "https://selectpdf.com";
        String localFile = "Test.pdf";
        String apiKey = "Your API key here";

        System.out.println(String.format("This is SelectPdf-%s.", ApiClient.CLIENT_VERSION));

        try {
            HtmlToPdfClient client = new HtmlToPdfClient(apiKey);

            // set parameters - see full list at https://selectpdf.com/html-to-pdf-api/
            client
                // main properties

                .setPageSize(ApiEnums.PageSize.A4) // PDF page size
                .setPageOrientation(ApiEnums.PageOrientation.Portrait) // PDF page orientation
                .setMargins(0) // PDF page margins
                .setRenderingEngine(ApiEnums.RenderingEngine.WebKit) // rendering engine
                .setConversionDelay(1) // conversion delay
                .setNavigationTimeout(30) // navigation timeout 
                .setShowPageNumbers(false) // page numbers
                .setPageBreaksEnhancedAlgorithm(true) // enhanced page break algorithm

                // additional properties

                // .setUseCssPrint(true) // enable CSS media print
                // .setDisableJavascript(true) // disable javascript
                // .setDisableInternalLinks(true) // disable internal links
                // .setDisableExternalLinks(true) // disable external links
                // .setKeepImagesTogether(true) // keep images together
                // .setScaleImages(true) // scale images to create smaller pdfs
                // .setSinglePagePdf(true) // generate a single page PDF
                // .setUserPassword("password") // secure the PDF with a password

                // generate automatic bookmarks

                // .setPdfBookmarksSelectors("H1, H2") // create outlines (bookmarks) for the specified elements
                // .setViewerPageMode(ApiEnums.PageMode.UseOutlines) // display outlines (bookmarks) in viewer
            ;

            System.out.println("Starting conversion...");

            // convert url to file
            client.convertUrlToFile(url, localFile);

            // convert url to memory
            // byte[] pdf = client.convertUrl(url);

            // convert html string to file
            // client.convertHtmlStringToFile("This is some <b>html</b>.", localFile);

            // convert html string to memory
            // byte[] pdf = client.convertHtmlString("This is some <b>html</b>.");

            System.out.println(String.format("Finished! Number of pages: %d.", client.getNumberOfPages()));

            // get API usage
            UsageClient usageClient = new UsageClient(apiKey);
            String usage = usageClient.getUsage(false);
            System.out.printf("Usage details: %s.\r\n", usage);

            // org.json.JSONObject usageObject = new org.json.JSONObject(usage);
            // int available = usageObject.getInt("available");
            // System.out.printf("Conversions remained this month: %d.\r\n", available);

        }
        catch (Exception ex) {
            System.out.println("An error occured: " + ex.getMessage());
        }
    }
        
}
```

## Pdf Merge API

SelectPdf Pdf Merge REST API is an online solution that lets you merge local or remote PDFs into a final PDF document.

### Features

* Merge local PDF document.
* Merge remote PDF from public url.
* Set PDF viewer options and PDF document information.
* Secure generated PDF with a password.
* Works in all programming languages.

See [PDF Merge API](https://selectpdf.com/pdf-merge-api/) page for full list of parameters.

### Sample Code

```java
package com.selectpdf;

public class PdfMerge {
    public static void main(String[] args) throws Exception {
        String testUrl = "https://selectpdf.com/demo/files/selectpdf.pdf";
        String testPdf = "Input.pdf";
        String localFile = "Result.pdf";
        String apiKey = "Your API key here";

        System.out.println(String.format("This is SelectPdf-%s.", ApiClient.CLIENT_VERSION));

        try {
            PdfMergeClient client = new PdfMergeClient(apiKey);

            // set parameters - see full list at https://selectpdf.com/pdf-merge-api/
            client
                // specify the pdf files that will be merged (order will be preserved in the final pdf)

                .addFile(testPdf) // add PDF from local file
                .addUrlFile(testUrl) // add PDF From public url
                // .addFile(testPdf, "pdf_password") // add PDF (that requires a password) from local file
                // .addUrlFile(testUrl, "pdf_password") // add PDF (that requires a password) from public url
            ;

            System.out.println("Starting pdf merge...");

            // merge pdfs to local file
            client.saveToFile(localFile);

            // merge pdfs to memory
            // byte[] pdf = client.save();

            System.out.println(String.format("Finished! Number of pages: %d.", client.getNumberOfPages()));

            // get API usage
            UsageClient usageClient = new UsageClient(apiKey);
            String usage = usageClient.getUsage(false);
            System.out.printf("Usage details: %s.\r\n", usage);

            // org.json.JSONObject usageObject = new org.json.JSONObject(usage);
            // int available = usageObject.getInt("available");
            // System.out.printf("Conversions remained this month: %d.\r\n", available);

        }
        catch (Exception ex) {
            System.out.println("An error occured: " + ex.getMessage());
        }
    }
        
}
```

## Pdf To Text API

SelectPdf Pdf To Text REST API is an online solution that lets you extract text from your PDF documents or search your PDF document for certain words.

### Features

* Extract text from PDF.
* Search PDF.
* Specify start and end page for partial file processing.
* Specify output format (plain text or html).
* Use a PDF from an online location (url) or upload a local PDF document.

See [Pdf To Text API](https://selectpdf.com/pdf-to-text-api/) page for full list of parameters.

### Sample Code - Pdf To Text

```java
package com.selectpdf;

public class PdfToText {
    public static void main(String[] args) throws Exception {
        String testUrl = "https://selectpdf.com/demo/files/selectpdf.pdf";
        String testPdf = "Input.pdf";
        String localFile = "Result.txt";
        String apiKey = "Your API key here";

        System.out.println(String.format("This is SelectPdf-%s.", ApiClient.CLIENT_VERSION));

        try {
            PdfToTextClient client = new PdfToTextClient(apiKey);

            // set parameters - see full list at https://selectpdf.com/pdf-to-text-api/
            client
                .setStartPage(1) // start page (processing starts from here)
                .setEndPage(0) // end page (set 0 to process file til the end)
                .setOutputFormat(ApiEnums.OutputFormat.Text) // set output format (0-Text or 1-HTML)
            ;

            System.out.println("Starting pdf to text...");

            // convert local pdf to local text file
            client.getTextFromFileToFile(testPdf, localFile);

            // extract text from local pdf to memory
            // String text = client.getTextFromFile(testPdf);
            // print text
            // System.out.println(text);

            // convert pdf from public url to local text file
            // client.getTextFromUrlToFile(testUrl, localFile);

            // extract text from pdf from public url to memory
            // String text = client.getTextFromUrl(testUrl);
            // print text
            // System.out.println(text);

            System.out.println(String.format("Finished! Number of pages: %d.", client.getNumberOfPages()));

            // get API usage
            UsageClient usageClient = new UsageClient(apiKey);
            String usage = usageClient.getUsage(false);
            System.out.printf("Usage details: %s.\r\n", usage);

            // org.json.JSONObject usageObject = new org.json.JSONObject(usage);
            // int available = usageObject.getInt("available");
            // System.out.printf("Conversions remained this month: %d.\r\n", available);

        }
        catch (Exception ex) {
            System.out.println("An error occured: " + ex.getMessage());
        }
    }
        
}
```

### Sample Code - Search Pdf

```java
package com.selectpdf;

public class SearchPdf {
    public static void main(String[] args) throws Exception {
        String testUrl = "https://selectpdf.com/demo/files/selectpdf.pdf";
        String testPdf = "Input.pdf";
        String apiKey = "Your API key here";

        System.out.println(String.format("This is SelectPdf-%s.", ApiClient.CLIENT_VERSION));

        try {
            PdfToTextClient client = new PdfToTextClient(apiKey);

            // set parameters - see full list at https://selectpdf.com/pdf-to-text-api/
            client
                .setStartPage(1) // start page (processing starts from here)
                .setEndPage(0) // end page (set 0 to process file til the end)
                .setOutputFormat(ApiEnums.OutputFormat.Text) // set output format (0-Text or 1-HTML)
            ;

            System.out.println("Starting search pdf...");

            // search local pdf
            String results = client.searchFile(testPdf, "pdf");

            // search pdf from public url
            // String results = client.searchUrl(testUrl, "pdf");

            System.out.printf("Search results:\r\n%s\r\n", results);

            // org.json.JSONArray textPositions = new org.json.JSONArray(results);
            // System.out.printf("Search results count: %d\r\n", textPositions.length());

            System.out.println(String.format("Finished! Number of pages processed: %d.", client.getNumberOfPages()));

            // get API usage
            UsageClient usageClient = new UsageClient(apiKey);
            String usage = usageClient.getUsage(false);
            System.out.printf("Usage details: %s.\r\n", usage);

            // org.json.JSONObject usageObject = new org.json.JSONObject(usage);
            // int available = usageObject.getInt("available");
            // System.out.printf("Conversions remained this month: %d.\r\n", available);

        }
        catch (Exception ex) {
            System.out.println("An error occured: " + ex.getMessage());
        }
    }
        
}
```