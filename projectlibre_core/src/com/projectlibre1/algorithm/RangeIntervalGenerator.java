/*******************************************************************************
 * The contents of this file are subject to the Common Public Attribution License 
 * Version 1.0 (the "License"); you may not use this file except in compliance with 
 * the License. You may obtain a copy of the License at 
 * http://www.projectlibre.com/license . The License is based on the Mozilla Public 
 * License Version 1.1 but Sections 14 and 15 have been added to cover use of 
 * software over a computer network and provide for limited attribution for the 
 * Original Developer. In addition, Exhibit A has been modified to be consistent 
 * with Exhibit B. 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the 
 * specific language governing rights and limitations under the License. The 
 * Original Code is ProjectLibre. The Original Developer is the Initial Developer 
 * and is ProjectLibre Inc. All portions of the code written by ProjectLibre are 
 * Copyright (c) 2012-2019. All Rights Reserved. All portions of the code written by 
 * ProjectLibre are Copyright (c) 2012-2019. All Rights Reserved. Contributor 
 * ProjectLibre, Inc.
 *
 * Alternatively, the contents of this file may be used under the terms of the 
 * ProjectLibre End-User License Agreement (the ProjectLibre License) in which case 
 * the provisions of the ProjectLibre License are applicable instead of those above. 
 * If you wish to allow use of your version of this file only under the terms of the 
 * ProjectLibre License and not to allow others to use your version of this file 
 * under the CPAL, indicate your decision by deleting the provisions above and 
 * replace them with the notice and other provisions required by the ProjectLibre 
 * License. If you do not delete the provisions above, a recipient may use your 
 * version of this file under either the CPAL or the ProjectLibre Licenses. 
 *
 *
 * [NOTE: The text of this Exhibit A may differ slightly from the text of the notices 
 * in the Source Code files of the Original Code. You should use the text of this 
 * Exhibit A rather than the text found in the Original Code Source Code for Your 
 * Modifications.] 
 *
 * EXHIBIT B. Attribution Information for ProjectLibre required
 *
 * Attribution Copyright Notice: Copyright (c) 2012-2019, ProjectLibre, Inc.
 * Attribution Phrase (not exceeding 10 words): 
 * ProjectLibre, open source project management software.
 * Attribution URL: http://www.projectlibre.com
 * Graphic Image as provided in the Covered Code as file: projectlibre-logo.png with 
 * alternatives listed on http://www.projectlibre.com/logo 
 *
 * Display of Attribution Information is required in Larger Works which are defined 
 * in the CPAL as a work which combines Covered Code or portions thereof with code 
 * not governed by the terms of the CPAL. However, in addition to the other notice 
 * obligations, all copies of the Covered Code in Executable and Source Code form 
 * distributed must, as a form of attribution of the original author, include on 
 * each user interface screen the "ProjectLibre" logo visible to all users. 
 * The ProjectLibre logo should be located horizontally aligned with the menu bar 
 * and left justified on the top left of the screen adjacent to the File menu. The 
 * logo must be at least 144 x 31 pixels. When users click on the "ProjectLibre" 
 * logo it must direct them back to http://www.projectlibre.com. 
 *******************************************************************************/
package com.projectlibre1.algorithm;

import java.util.GregorianCalendar;

import com.projectlibre1.util.DateTime;

/**
 * A generator corresponding to a start/end with an optional stepping value.  The stepping value is specified as 
 * a unit type of the Calendar class (DAY_OF_YEAR for example) as a gregorian calendar is used.
 */
public class RangeIntervalGenerator implements IntervalGenerator {
	long start;
	long end;
	long step;
	int calendarStepUnit;
	int calendarStepAmount = 1;
	long currentEnd;
	long nextEnd;
	GregorianCalendar stepCal = null;

	private RangeIntervalGenerator() {
		this(0,Long.MAX_VALUE);
	}


	private RangeIntervalGenerator(long start, long end) {
		this.start = start;
		this.end = end;
		currentEnd = end;
		step = end; // make sure that will go past end
		nextEnd = end + step;
	}

	private RangeIntervalGenerator(long start, long end, int calendarStepUnit) {
		this.start = start;
		this.end = end;
		this.calendarStepUnit = calendarStepUnit;
		stepCal = new GregorianCalendar();
		stepCal.setTimeInMillis(start);
		stepCal.add(calendarStepUnit,calendarStepAmount);
		currentEnd = stepCal.getTimeInMillis();
		if (currentEnd > end) // in case just one time period
			currentEnd = end;
		stepCal.add(calendarStepUnit,calendarStepAmount);
		nextEnd = stepCal.getTimeInMillis();
	}
	


	/**
	 * Used if filtering values between dates, not in a groupBy
	 * @param start
	 * @param end
	 * @return
	 */
	public static RangeIntervalGenerator betweenInstance(long start, long end) {
		RangeIntervalGenerator result = getInstance(start, end);
		result.currentEnd = start;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.projectlibre1.algorithm.IntervalGenerator#current()
	 */
	public Object current() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.projectlibre1.algorithm.IntervalGenerator#currentEnd()
	 */
	public long currentEnd() {
		return currentEnd;
	}

	public long currentStart() {
		return start;
	}	
	/*  (non-Javadoc)
	 * @see com.projectlibre1.algorithm.IntervalGenerator#next()
	 */
	public boolean evaluate(Object obj) {
		start = currentEnd; // move on to next interval.  If only one, then will stop here
		currentEnd = nextEnd;
		if (currentEnd > end)
			currentEnd = end;
		
		if (stepCal != null) {
			stepCal.add(calendarStepUnit,calendarStepAmount);
			nextEnd = stepCal.getTimeInMillis();
		} else {
			nextEnd += step;
		}
		
		if (start >= end)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return Returns the end.
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * @return Returns the start.
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Continuous time is considered from 1/1/1970 to 1/1/3000.  Note I do not use Long.MAX_VALUE because adding to it would cause wrapping to negative values 
	 */
	public static RangeIntervalGenerator continuous() {
		return getInstance(0, DateTime.getMaxCalendar().getTimeInMillis());
	}
	public static RangeIntervalGenerator empty() {
		return getInstance(0, 0);	
	}

	public static RangeIntervalGenerator getInstance() {
		return new RangeIntervalGenerator();
	}

	public static RangeIntervalGenerator getInstance(long start, long end) {
		return new RangeIntervalGenerator(start, end);
	}

	public static RangeIntervalGenerator getInstance(long start, long end, int calendarStepUnit) {
		return new RangeIntervalGenerator(start, end, calendarStepUnit);
	}

	/* (non-Javadoc)
	 * @see com.projectlibre1.algorithm.IntervalGenerator#isActive()
	 */
	public boolean isCurrentActive() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.projectlibre1.algorithm.IntervalGenerator#hasNext()
	 */
	public boolean hasNext() {
		return (nextEnd < end);
	}

	/* (non-Javadoc)
	 * @see com.projectlibre1.algorithm.IntervalGenerator#canBeShared()
	 */
	public boolean canBeShared() {
		return true;
	}
}
