package componentrelease.impl.ifpart;

import cm.core.data.CaseFileItem;
import cm.core.data.SimpleProperty;
import cm.core.sentries.IfPart;
import cm.core.sentries.IfPartImplementation;

public class IfRevision extends IfPartImplementation {

	public IfRevision(IfPart ip) {
		super(ip);
	}

	@Override
	public boolean isSatisfied() {
		boolean response = false;
		CaseFileItem specifications = ip.getCaseFileItemRef();
		SimpleProperty dataApproved = specifications.getProperty("revisionNeeded");
		if (dataApproved != null) {
			if (dataApproved.getValue() != null) {
				if (Boolean.parseBoolean(dataApproved.getValue())) {
					response = true;
				} 
			}
		}
		return response;
	}

}
