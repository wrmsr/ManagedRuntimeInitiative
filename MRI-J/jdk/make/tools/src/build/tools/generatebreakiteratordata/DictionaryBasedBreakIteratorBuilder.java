/*
 * Copyright 2003 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package build.tools.generatebreakiteratordata;

import java.util.Hashtable;
import java.util.Vector;

/**
 * The Builder class for DictionaryBasedBreakIterator inherits almost all of
 * its functionality from RuleBasedBreakIteratorBuilder, but extends it with
 * extra logic to handle the "<dictionary>" token.
 */
class DictionaryBasedBreakIteratorBuilder extends RuleBasedBreakIteratorBuilder {

    /**
     * A list of flags indicating which character categories are contained in
     * the dictionary file (this is used to determine which ranges of characters
     * to apply the dictionary to)
     */
    private boolean[] categoryFlags;

    /**
     * A CharSet that contains all the characters represented in the dictionary
     */
    private CharSet dictionaryChars = new CharSet();
    private String dictionaryExpression = "";

    public DictionaryBasedBreakIteratorBuilder(String description) {
        super(description);
    }

    /**
     * We override handleSpecialSubstitution() to add logic to handle
     * the <dictionary> tag.  If we see a substitution named "<dictionary>",
     * parse the substitution expression and store the result in
     * dictionaryChars.
     */
    protected void handleSpecialSubstitution(String replace, String replaceWith,
                                             int startPos, String description) {
        super.handleSpecialSubstitution(replace, replaceWith, startPos, description);

        if (replace.equals("<dictionary>")) {
            if (replaceWith.charAt(0) == '(') {
                error("Dictionary group can't be enclosed in (", startPos, description);
            }
            dictionaryExpression = replaceWith;
            dictionaryChars = CharSet.parseString(replaceWith);
        }
    }

    /**
     * The other half of the logic to handle the dictionary characters happens
     * here. After the inherited builder has derived the real character
     * categories, we set up the categoryFlags array in the iterator. This array
     * contains "true" for every character category that includes a dictionary
     * character.
     */
    protected void buildCharCategories(Vector tempRuleList) {
        super.buildCharCategories(tempRuleList);

        categoryFlags = new boolean[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            CharSet cs = (CharSet)categories.elementAt(i);
            if (!(cs.intersection(dictionaryChars).empty())) {
                categoryFlags[i] = true;
            }
        }
    }

    // This function is actually called by
    // RuleBasedBreakIteratorBuilder.buildCharCategories(), which is called by
    // the function above. This gives us a way to create a separate character
    // category for the dictionary characters even when
    // RuleBasedBreakIteratorBuilder isn't making a distinction.
    protected void mungeExpressionList(Hashtable expressions) {
        expressions.put(dictionaryExpression, dictionaryChars);
    }

    void makeFile(String filename) {
        super.setAdditionalData(super.toByteArray(categoryFlags));
        super.makeFile(filename);
    }
}
