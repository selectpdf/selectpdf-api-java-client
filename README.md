# HTML To PDF API

SelectPdf HTML To PDF Online REST API is a professional solution that lets you create PDF from web pages and raw HTML code in your applications. The API is easy to use and the integration takes only a few lines of code.

## Features

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

## Installation

Install SelectPdf Java Client for Online API via Maven: [SelectPdf on Maven](https://search.maven.org/artifact/com.selectpdf/selectpdf-api-client).

## Sample Code

```
package com.selectpdf;

/**
 * Test for SelectPdf API Client for Java.
 */
public class AppTest 
{
    public static void main(String[] args) throws Exception {
        String url = "https://selectpdf.com";
        String outFile = "test.pdf";

        try {
            HtmlToPdfClient api = new HtmlToPdfClient("Your key here");

            api
                .setPageSize(ApiEnums.PageSize.A4)
                .setPageOrientation(ApiEnums.PageOrientation.Portrait)
                .setMargins(0)
                .setNavigationTimeout(30)
                .setShowPageNumbers(false)
                .setPageBreaksEnhancedAlgorithm(true)
            ;

            System.out.println("Starting conversion ...");

            api.convertUrlToFile(url, outFile);

            System.out.println("Conversion finished successfully!");

            UsageClient usage = new UsageClient("Your key here");
            String info = usage.getUsage(false);
            System.out.println(info);
        }
        catch (Exception ex) {
            System.out.println("An error occured: " + ex.getMessage());
        }
        
    }
}
```
