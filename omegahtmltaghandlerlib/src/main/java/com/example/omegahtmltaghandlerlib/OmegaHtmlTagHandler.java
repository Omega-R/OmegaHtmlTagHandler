package com.example.omegahtmltaghandlerlib;

import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.XMLReader;

/**
 * Created by Omega on 7/5/2017.
 */

public class OmegaHtmlTagHandler implements Html.TagHandler {

    private static final String HTML_TAG_TABLE = "table";
    private static final String HTML_TAG_TR = "tr";
    private static final String HTML_TAG_TH = "th";
    private static final String HTML_TAG_TD = "td";

    private static final String HTML_TAG_GREATER_THAN = ">";
    private static final String HTML_TAG_LESS_THAN = "<";
    private static final String HTML_TAG_FRACTION_LINE = "/";
    private static final String HTML_TAG_TABLE_PATTERN = "table > tbody > tr";


    private StringBuilder mTableHtmlBuilder = new StringBuilder();
    private int mTableTagLevel = 0;
    private boolean mIsDeleteSpace = true;
    private String mTypeSpace = "";
    private String mSymbolBetweenTableRows = "\n\n";

    private static class EmptyStyle {

    }

    //you can set span style
    private static class Table {

    }

    private static class Tr {

    }

    private static class Th {

    }

    private static class Td {

    }

    @Override
    public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {
        if (opening) {
            if (tag.equalsIgnoreCase(HTML_TAG_TABLE)) {
                start(output, new Table());
                if (mTableTagLevel == 0) {
                    mTableHtmlBuilder = new StringBuilder();
                }
                mTableTagLevel++;
            } else if (tag.equalsIgnoreCase(HTML_TAG_TR)) {
                start(output, new Tr());
            } else if (tag.equalsIgnoreCase(HTML_TAG_TH)) {
                start(output, new Th());
            } else if (tag.equalsIgnoreCase(HTML_TAG_TD)) {
                start(output, new Td());
            }
        } else {
            if (tag.equalsIgnoreCase(HTML_TAG_TABLE)) {
                mTableTagLevel--;
                if (mTableTagLevel == 0) {
                    Document document = Jsoup.parse(mTableHtmlBuilder.toString());
                    Elements rows = document.select(HTML_TAG_TABLE_PATTERN);
                    for (Element element : rows) {
                        String result = element.text();
                        if (mIsDeleteSpace) {
                            result = result != null ? result.replaceAll(mTypeSpace, "") : "";
                        }
                        output.append(result).append(mSymbolBetweenTableRows);
                    }
                } else {
                    end(output, Table.class);
                }
            } else if (tag.equalsIgnoreCase(HTML_TAG_TR)) {
                end(output, Tr.class);
            } else if (tag.equalsIgnoreCase(HTML_TAG_TH)) {
                end(output, Th.class);
            } else if (tag.equalsIgnoreCase(HTML_TAG_TD)) {
                end(output, Td.class);
            }
        }
        storeTableTags(opening, tag);
    }

    /**
     * Delete specifics space in table if needs, and can set type space
     */
    public void deleteSpecificSpaces(boolean isDeleteSpace, String typeSpace) {
        mIsDeleteSpace = isDeleteSpace;
        mTypeSpace = typeSpace;
    }

    /**
     * Specifies what character will be between the rows of the table
     */
    public void setSymbolsBetweenTableRows(String symbolBetweenTableRows) {
        mSymbolBetweenTableRows = symbolBetweenTableRows;
    }

    private void storeTableTags(boolean opening, String tag) {
        if (mTableTagLevel > 0 || tag.equalsIgnoreCase(HTML_TAG_TABLE)) {
            mTableHtmlBuilder.append(HTML_TAG_LESS_THAN);
            if (!opening) {
                mTableHtmlBuilder.append(HTML_TAG_FRACTION_LINE);
            }
            mTableHtmlBuilder
                    .append(tag.toLowerCase())
                    .append(HTML_TAG_GREATER_THAN);
        }
    }

    private void start(Editable output, Object mark) {
        int len = output.length();
        output.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);
    }

    private void end(Editable output, Class kind, Object... replaces) {
        Object obj = getLast(output, kind);
        int where = output.getSpanStart(obj);
        int len = output.length();

        // If we're in a table, then we need to store the raw HTML for later
        if (mTableTagLevel > 0) {
            final CharSequence extractedSpanText = extractSpanText(output, kind);
            mTableHtmlBuilder.append(extractedSpanText);
        }

        output.removeSpan(obj);
        if (where != len) {
            // paragraph styles like AlignmentSpan need to end with a new line!
            for (Object replace : replaces) {
                output.setSpan(replace, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * Returns the text contained within a span and deletes it from the output string
     */
    private CharSequence extractSpanText(Editable output, Class kind) {
        final Object obj = getLast(output, kind);
        // start of the tag
        final int where = output.getSpanStart(obj);
        // end of the tag
        final int len = output.length();
        CharSequence extractedSpanText;
        if (where >= 0) {
            extractedSpanText = output.subSequence(where, len);
            output.delete(where, len);
        } else {
            extractedSpanText = output.subSequence(0, len);
            output.delete(0, len);
        }
        return extractedSpanText;
    }

    /**
     * Get last marked position of a specific tag kind (private class)
     */
    private static Object getLast(Editable text, Class kind) {
        Object[] objects = text.getSpans(0, text.length(), kind);
        if (objects.length == 0) {
            return null;
        } else {
            for (int i = objects.length; i > 0; i--) {
                if (text.getSpanFlags(objects[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objects[i - 1];
                }
            }
            return null;
        }
    }
}
