/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.html2pdf.resolver.form;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.html2pdf.css.util.CssUtils;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class FormFieldNameResolver {
    private static final String DEFAULT_NAME = "Field";
    private static final String NAME_COUNT_SEPARATOR = "_";
    private final Map<String, Integer> names = new HashMap<>();

    public FormFieldNameResolver() {
    }

//    public FormFieldNameResolver(Iterable<String> occupiedNames) {
//        reset(occupiedNames);
//    }
//
//    public FormFieldNameResolver(PdfDocument document) {
//        reset(document);
//    }

    public String resolveFormName(String name) {
        name = normalizeString(name);
        if (name.isEmpty()) {
            return resolveNormalisedFormName(DEFAULT_NAME);
        } else {
            return resolveNormalisedFormName(name);
        }

    }

    public void reset() {
        names.clear();
    }

//    public void reset(Iterable<String> occupiedNames) {
//        reset();
//        if (occupiedNames != null) {
//            String normalized;
//            for (String name : occupiedNames) {
//                normalized = normalizeString(name);
//                if (!normalized.isEmpty()) {
//                    resolveNormalisedFormName(normalized);
//                }
//            }
//        }
//    }
//
//    public void reset(PdfDocument document) {
//        if (document != null) {
//            PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, false);
//            if (acroForm != null) {
//                //We add all fields to acro form directly, so it is enough to check only direct kids names
//                PdfArray fieldsArray = acroForm.getPdfObject().getAsArray(PdfName.Fields);
//                if (fieldsArray != null) {
//                    ArrayList<String> fieldsNames = new ArrayList<>();
//                    for (PdfObject field : fieldsArray) {
//                        if (field.isDictionary()) {
//                            PdfString namePdfString = ((PdfDictionary) field).getAsString(PdfName.T);
//                            if (namePdfString != null) {
//                                fieldsNames.add(namePdfString.toUnicodeString());
//                            }
//                        }
//                    }
//                    reset(fieldsNames);
//                }
//            }
//        }
//    }

    private String normalizeString(String s) {
        return s != null ? s.trim().replace(".", "") : "";
    }

    private String resolveNormalisedFormName(String name) {
        int separatorIndex = name.lastIndexOf(NAME_COUNT_SEPARATOR);
        Integer nameIndex = null;
        if (separatorIndex != -1 && separatorIndex < name.length()) {
            String numberString = name.substring(separatorIndex + 1);
            nameIndex = CssUtils.parseInteger(numberString);
            //Treat number as index only in case it is positive
            if (nameIndex != null && nameIndex > 0) {
                name = name.substring(0, separatorIndex);
            }
        }
        Integer savedIndex = names.get(name);
        int indexToSave = savedIndex != null ? savedIndex.intValue() + 1 : 0;
        if (nameIndex != null && indexToSave < nameIndex.intValue()) {
            indexToSave = nameIndex.intValue();
        }
        names.put(name, indexToSave);
        return indexToSave == 0 ? name : name + NAME_COUNT_SEPARATOR + String.valueOf(indexToSave);
    }
}
