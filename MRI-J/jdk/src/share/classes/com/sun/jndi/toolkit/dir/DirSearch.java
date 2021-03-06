/*
 * Copyright 1999 Sun Microsystems, Inc.  All Rights Reserved.
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

package com.sun.jndi.toolkit.dir;

import javax.naming.*;
import javax.naming.directory.*;

/**
  * A class for searching DirContexts
  *
  * @author Jon Ruiz
  */
public class DirSearch {
   public static NamingEnumeration search(DirContext ctx,
       Attributes matchingAttributes,
       String[] attributesToReturn) throws NamingException {
        SearchControls cons = new SearchControls(
            SearchControls.ONELEVEL_SCOPE,
            0, 0, attributesToReturn,
            false, false);

        return new LazySearchEnumerationImpl(
            new ContextEnumerator(ctx, SearchControls.ONELEVEL_SCOPE),
            new ContainmentFilter(matchingAttributes),
            cons);
    }

    public static NamingEnumeration search(DirContext ctx,
        String filter, SearchControls cons) throws NamingException {

        if (cons == null)
            cons = new SearchControls();

        return new LazySearchEnumerationImpl(
            new ContextEnumerator(ctx, cons.getSearchScope()),
            new SearchFilter(filter),
            cons);
    }

    public static NamingEnumeration search(DirContext ctx,
        String filterExpr, Object[] filterArgs, SearchControls cons)
        throws NamingException {

        String strfilter = SearchFilter.format(filterExpr, filterArgs);
        return search(ctx, strfilter, cons);
    }
}
