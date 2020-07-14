package com.selectpdf;

/**
 * Enumerations used by SelectPdf API Client.
 */
public class ApiEnums {
    
    /**
     * PDF page size.
     */
    public enum PageSize
    {
        /**
         * Custom page size.
         */
        Custom,
        /**
         * A1 page size.
         */
        A1,
        /**
         * A2 page size.
         */
        A2,
        /**
         * A3 page size.
         */
        A3,
        /**
         * A4 page size.
         */
        A4,
        /**
         * A5 page size.
         */
        A5,
        /**
         * Letter page size.
         */
        Letter,
        /**
         * Half Letter page size.
         */
        HalfLetter,
        /**
         * Ledger page size.
         */
        Ledger,
        /**
         * Legal page size.
         */
        Legal
    }

    /**
     * PDF page orientation.
     */
    public enum PageOrientation
    {
        /**
         * Portrait page orientation.
         */
        Portrait,
        /**
         * Landscape page orientation.
         */
        Landscape
    }

    /**
     * Rendering engine used for HTML to PDF conversion.
     */
    public enum RenderingEngine
    {
        /**
         * WebKit rendering engine.
         */
        WebKit,
        /**
         * WebKit Restricted rendering engine.
         */
        Restricted,
        /**
         * Blink rendering engine.
         */
        Blink
    }

    /**
     * Protocol used for secure (HTTPS) connections.
     */
    public enum SecureProtocol
    {
        /**
         * TLS 1.1 or newer. Recommended value.
         */
        Tls11OrNewer(0),
        /**
         * TLS 1.0 only.
         */
        Tls10(1),
        /**
         * SSL v3 only.
         */
        Ssl3(2);

        private int value;
        SecureProtocol(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }

        String getValueAsString() {
            return Integer.toString(value);
        }
    }

    /**
     * The page layout to be used when the pdf document is opened in a viewer.
     */
    public enum PageLayout
    {
        /**
         * Displays one page at a time.
         */
        SinglePage(0),
        /**
         * Displays the pages in one column.
         */
        OneColumn(1),
        /**
         * Displays the pages in two columns, with odd-numbered pages on the left.
         */
        TwoColumnLeft(2),
        /**
         * Displays the pages in two columns, with odd-numbered pages on the right.
         */
        TwoColumnRight(3);

        private int value;
        PageLayout(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }

        String getValueAsString() {
            return Integer.toString(value);
        }
    }

    /**
     * The PDF document's page mode.
     */
    public enum PageMode
    {
        /**
         * Neither document outline (bookmarks) nor thumbnail images are visible.
         */
        UseNone(0),
        /**
         * Document outline (bookmarks) are visible.
         */
        UseOutlines(1),
        /**
         * Thumbnail images are visible.
         */
        UseThumbs(2),
        /**
         * Full-screen mode, with no menu bar, window controls or any other window visible.
         */
        FullScreen(3),
        /**
         * Optional content group panel is visible.
         */
        UseOC(4),
        /**
         * Document attachments are visible.
         */
        UseAttachments(5);
        
        private int value;
        PageMode(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }

        String getValueAsString() {
            return Integer.toString(value);
        }
    }

    /**
     * Alignment for page numbers.
     */
    public enum PageNumbersAlignment
    {
        /**
         * Align left.
         */
        Left(1),
        /**
         * Align center.
         */
        Center(2),
        /**
         * Align right.
         */
        Right(3);

        private int value;
        PageNumbersAlignment(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }

        String getValueAsString() {
            return Integer.toString(value);
        }
    }

    /**
     * Specifies the converter startup mode.
     */
    public enum StartupMode
    {
        /**
         * The conversion starts right after the page loads.
         */
        Automatic,
        /**
         * The conversion starts only when called from JavaScript.
         */
        Manual
    }
    
}