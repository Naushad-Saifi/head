/**

 * CollectionSheetHelper.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */
package org.mifos.framework.components.batchjobs.helpers;

import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.reports.business.service.CascadingReportParameterService;
import org.mifos.application.reports.business.service.ReportServiceFactory;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.NumberUtils;

public class CollectionSheetReportParameterCachingHelper extends TaskHelper {

	private static final Integer MIFOS_USER_ID = new Integer(1);

	public CollectionSheetReportParameterCachingHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		CascadingReportParameterService cascadingReportParameterService = ReportServiceFactory
				.getCascadingReportParameterService();
		cascadingReportParameterService.invalidate();
		try {
			List<OfficeBO> branchOffices = new OfficeBusinessService()
					.getBranchOffices();
			for (OfficeBO branchOffice : branchOffices) {

				Integer officeId = NumberUtils
						.convertShortToInteger(branchOffice.getOfficeId());
				cascadingReportParameterService.getActiveLoanOfficers(
						MIFOS_USER_ID, officeId);
				Integer allLoanOfficerId = SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM
						.getId();
				cascadingReportParameterService.getActiveCentersForLoanOfficer(
						allLoanOfficerId, officeId);
				cascadingReportParameterService
						.getMeetingDatesForCollectionSheet(
								officeId,
								allLoanOfficerId,
								SelectionItem.ALL_CENTER_SELECTION_ITEM
										.getId());
			}
		}
		catch (ServiceException e) {
			throw new BatchJobException(e);
		}
	}
}
