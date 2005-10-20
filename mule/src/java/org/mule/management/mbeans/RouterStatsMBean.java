/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.management.mbeans;

import java.util.Map;

/**
 * <code>RouterStatsMBean</code> TODO
 * 
 * @author Guillaume Nodet
 * @version $Revision$
 */
public interface RouterStatsMBean
{

	long getCaughtMessages();

	long getNotRouted();

	long getTotalReceived();

	long getTotalRouted();

	Map getRouted();
}
