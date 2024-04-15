package com.marinescu.fxmoney.Model;

import com.marinescu.fxmoney.Interfaces.LoanInterface;
import com.marinescu.fxmoney.Interfaces.ShowBalance;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a PDF with detailed information about a loan.
 */
public class PDFHolder implements ShowBalance, LoanInterface {
    /**
     * The loan amount.
     */
    private double amount;

    /**
     * The number of months for repayment.
     */
    private int months;

    /**
     * A list of file's data.
     */
    private final List<String[]> data = new ArrayList<>();

    /**
     * A list of file's table's columns.
     */
    private final String[] header = {"Month", "Opening ShowBalance", "EMI", "Interest Component", "Principal Component", "Closing ShowBalance"};

    /**
     * The class' instance.
     */
    private final static PDFHolder INSTANCE = new PDFHolder();

    /**
     * An empty class' controller.
     */
    private PDFHolder() {}

    /**
     * This method gets the instance of the class.
     * Through this method, the same object can be called in the application.
     * @return The class' instance.
     */
    public static synchronized PDFHolder getInstance() {
        return INSTANCE;
    }

    /**
     * This method creates a PDF document containing a table with loan analysis data.
     */
    public void createPDF(){
        setData();
        try {
            // Loads the existing PDF document from a file.

            String fileName = "LoanAnalysis.pdf";
            PDDocument document = PDDocument.load(new File(fileName));

            // Creates a new PDDocument to store the modified content.
            PDDocument newDocument = new PDDocument();

            // Imports the page from the existing document to the new document.
            PDPage newPage = new PDPage();
            newDocument.addPage(newPage);

            int pageNumber = 1;

            // Creates a new PDPageContentStream to modify the content.
            PDPageContentStream contentStream = new PDPageContentStream(newDocument, newPage);

            String title = "Loan Analysis";
            addTitle(contentStream, title);

            // Sets font and other properties for the table.
            float margin = 20; // Sets the margin for the table.
            float tableWidth = PDRectangle.A4.getWidth() - 2 * margin; // Calculates the table width based on the margin.
            float yStart = PDRectangle.A4.getHeight() - 110; // Sets the starting Y-coordinate for the table.

            // Sets row height and add table header.
            float rowHeight = 30;
            float columnWidth = tableWidth / header.length;

            addHeader(contentStream, header, yStart, rowHeight, margin, columnWidth);

            // Draws page number.
            contentStream.setFont(PDType1Font.TIMES_BOLD, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(PDRectangle.A4.getWidth() / 2, margin);
            contentStream.showText("Page " + pageNumber);
            contentStream.endText();
            pageNumber++;

            // Adds table rows with loan analysis data.
            yStart-=10;
            float currentY = yStart;
            rowHeight = 20;
            for (int i = 0; i < getData().size(); i++) {
                currentY -= rowHeight;

                if (currentY - 30 < rowHeight){
                    contentStream.close();
                    newPage = new PDPage();
                    newDocument.addPage(newPage);
                    contentStream = new PDPageContentStream(newDocument, newPage);

                    yStart = PDRectangle.A4.getHeight() - 110;

                    addHeader(contentStream, header, yStart, 30, margin, columnWidth);

                    yStart-=30;
                    currentY = yStart;
                    // Draws page number.
                    contentStream.setFont(PDType1Font.TIMES_BOLD, 10);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(PDRectangle.A4.getWidth() / 2, margin);
                    contentStream.showText("Page " + pageNumber);
                    contentStream.endText();
                    pageNumber++;
                }
                for (int j = 0; j < getData().get(i).length; j++) {
                    drawCell(contentStream, margin + j * columnWidth, currentY, columnWidth, rowHeight, getData().get(i)[j]);
                }

                //  Draws a line on the left side of the table.
                contentStream.moveTo(margin, currentY);
                contentStream.lineTo(margin, currentY + (-1 * rowHeight));
                contentStream.stroke();
            }

            // Draws lines between data rows.
            contentStream.moveTo(margin, currentY);
            contentStream.lineTo(margin + tableWidth, currentY);
            contentStream.stroke();

            contentStream.close();

            // Saves the modified document.
            String newFileName = "LoanAnalysis.pdf";
            newDocument.save(newFileName);

            // Closes the document.
            newDocument.close();
            document.close();

            File file = new File(fileName);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Desktop not supported or OPEN action not supported on this platform.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method adds a title to the file and underlines it.
     * @param contentStream The {@link PDPageContentStream} to which the title is added.
     * @param title The text of the title to add to the PDF page.
     * @throws IOException If an I/O error occurs while writing to the content stream. This might happen
     *                      if the stream is closed before this method is called, or if there is an error
     *                      writing to the underlying output.
     */
    private void addTitle(PDPageContentStream contentStream, String title) throws IOException {
        // Sets the font and position for the title.
        contentStream.setFont(PDType1Font.TIMES_BOLD, 16);
        float titleWidth = PDType1Font.TIMES_BOLD.getStringWidth(title) / 1000 * 16;
        float centerX = (PDRectangle.A4.getWidth() - titleWidth) / 2;
        float titleY = PDRectangle.A4.getHeight() - 80;
        contentStream.beginText();
        contentStream.newLineAtOffset(centerX, titleY);
        contentStream.showText(title);
        contentStream.endText();

        // Draws an underline for the title.
        contentStream.moveTo(centerX, titleY - 5);
        contentStream.lineTo(centerX + titleWidth, titleY - 5);
        contentStream.stroke();
    }

    /**
     *  This method draws a header cell for the file's table, including the cell border and centered text.
     *  It first draws the cell's borders, calculates the text wrapping and splitting
     *   based on the cell's width, and then draws the text centered vertically and horizontally
     *   within the cell. The text is split into lines if it exceeds the cell width.
     * @param contentStream The {@link PDPageContentStream} used to draw the cell and text.
     * @param x The x-coordinate of the top-left corner of the cell.
     * @param y The y-coordinate of the top-left corner of the cell.
     * @param width The width of the cell.
     * @param height The height of the cell.
     * @param text The text to be displayed inside the cell.
     * @throws IOException If an I/O error occurs while writing to the content stream. This can
     *                        happen for various reasons, such as the stream being closed prematurely
     *                        or issues with the underlying storage.
     */
    private void drawHeaderCell(PDPageContentStream contentStream, float x, float y, float width, float height, String text) throws IOException{
        contentStream.setLineWidth(1.0f);
        contentStream.setFont(PDType1Font.TIMES_BOLD, 12);

        // Adds lines between table cells.
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();
        contentStream.moveTo(x, y - height);
        contentStream.lineTo(x + width, y - height);
        contentStream.stroke();
        contentStream.moveTo(x + width, y);
        contentStream.lineTo(x + width, y - height);
        contentStream.stroke();

        // Calculates available space for the text in the cell.
        float availableWidth = width - 2 * 5;

        // Splits the text into words.
        String[] words = text.split("\\s+");
        StringBuilder lineBuilder = new StringBuilder();
        List<String> lines = new ArrayList<>();
        for (String word : words) {
            float wordWidth = PDType1Font.TIMES_BOLD.getStringWidth(word) / 1000 * 12;
            float lineTextWidth = PDType1Font.TIMES_BOLD.getStringWidth(lineBuilder.toString()) / 1000 * 12;

            if (lineTextWidth + wordWidth > availableWidth) {
                lines.add(lineBuilder.toString().trim());
                lineBuilder.setLength(0);
            }
            lineBuilder.append(word).append(" ");
        }
        // Adds the last line to the list.
        lines.add(lineBuilder.toString().trim());

        // Calculates total height required for the text with wrapping.
        float textHeight = PDType1Font.TIMES_BOLD.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 12;
        float totalTextHeight = lines.size() * textHeight;

        // Centers text vertically in the cell.
        float startY = y - height / 2 + totalTextHeight / 2 - 5;

        // Draws the lines and text for each line.
        for (String line : lines) {
            float lineTextWidth = PDType1Font.TIMES_BOLD.getStringWidth(line) / 1000 * 12;
            float startX = x + (availableWidth - lineTextWidth) / 2 + 5;

            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_BOLD, 12);
            contentStream.newLineAtOffset(startX, startY - 5);
            contentStream.showText(line);
            contentStream.endText();

            startY -= textHeight;
        }
    }

    /**
     * This method adds a header row to the file's table.
     * @param contentStream The {@link PDPageContentStream} used to draw the header row and lines.
     * @param header An array of Strings representing the content of each header cell.
     * @param yStart The y-coordinate of the top of the header row.
     * @param rowHeight The height of each row in the table, including the header row.
     * @param margin The margin between the edge of the page and the table.
     * @param columnWidth The width of each column in the table.
     * @throws IOException If an I/O error occurs while writing to the content stream. This can
     *                        occur if the stream is closed prematurely or if there are issues with
     *                        the underlying storage.
     */
    private void addHeader(PDPageContentStream contentStream, String[] header, float yStart, float rowHeight, float margin, float columnWidth) throws IOException {
        for (int i=0; i<header.length; i++){
            drawHeaderCell(contentStream, margin + i * columnWidth, yStart, columnWidth, rowHeight, header[i]);
        }

        float tableWidth = PDRectangle.A4.getWidth() - 2 * margin;

        //  Draws a line on the left side of the table.
        contentStream.moveTo(margin, yStart);
        contentStream.lineTo(margin, yStart + (-1 * rowHeight));
        contentStream.stroke();

        // Draws lines between the header and data rows.
        contentStream.moveTo(margin, yStart - rowHeight);
        contentStream.lineTo(margin + tableWidth, yStart - rowHeight);
        contentStream.stroke();
    }

    /**
     * It draws a single cell of the file's table, including its border and centered text.
     * @param contentStream The {@link PDPageContentStream} used for drawing operations.
     * @param x The x-coordinate of the upper-left corner of the cell.
     * @param y The y-coordinate of the upper-left corner of the cell.
     * @param width The width of the cell.
     * @param height The height of the cell.
     * @param text The text to be displayed within the cell.
     * @throws IOException If an I/O error occurs while writing to the content stream.
     */
    private void drawCell(PDPageContentStream contentStream, float x, float y, float width, float height, String text) throws IOException {
        contentStream.setLineWidth(1.0f);
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();
        contentStream.moveTo(x, y - height);
        contentStream.lineTo(x + width, y - height);
        contentStream.stroke();
        contentStream.moveTo(x + width, y);
        contentStream.lineTo(x + width, y - height);
        contentStream.stroke();

        // Calculates the text width.
        float textWidth = PDType1Font.HELVETICA.getStringWidth(text) / 1000 * 12;

        // Centers the text horizontally in the cell.
        float startX = x + (width - textWidth) / 2;
        float startY = y - height / 2 - 5; // Center text vertically in the cell

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(startX, startY);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * Gets the list of the data.
     * @return The list of the data.
     */
    public List<String[]> getData() {
        return data;
    }

    /**
     * This method populates the data list with the loan repayment schedule based on the current loan amount,
     *  interest rate, and loan term.
     */
    public void setData() {
        double emi = getEMI(getAmount(), getMonths());
        double openingBalance = getAmount();
        double closingBalance;
        this.data.clear();

        // Calculates and print the loan repayment schedule for each month.
        for (int month = 1; month <= getMonths(); month++) {

            // Calculates the interest component for the current month.

            double monthlyInterestRate = 0.03;
            double interestComponent = openingBalance * monthlyInterestRate;

            // Calculates the principal component for the current month.
            double principalComponent = emi - interestComponent;

            // Calculates the closing balance for the current month.

            closingBalance = openingBalance - principalComponent;
            data.add(new String[]{String.valueOf(month), "$"+ round(openingBalance, 2), "$"+ round(emi,2), "$"+ round(interestComponent, 2), "$"+ round(principalComponent, 2), "$"+ round(closingBalance, 2)});

            // Sets the opening balance for the next month to the current month's closing balance.
            openingBalance = closingBalance;
        }
    }

    /**
     * It gets the loan amount.
     * @return The loan amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * This method set's the loan amount with a new one.
     * @param amount The new amount.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * This method gets the number of months for repayment.
     * @return The number of months.
     */
    public int getMonths() {
        return months;
    }

    /**
     * This method sets the number of months with a new one.
     * @param months The new number of months.
     */
    public void setMonths(int months) {
        this.months = months;
    }

}
