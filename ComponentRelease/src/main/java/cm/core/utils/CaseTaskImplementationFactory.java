/*
 * Copyright © 2018-2019 André Zensen, University of Applied Sciences Bielefeld
 * and various authors (see https://www.fh-bielefeld.de/wug/forschung/ag-pm)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cm.core.utils;

import javax.enterprise.inject.Produces;

import cm.core.tasks.CaseTask;
import cm.core.tasks.CaseTaskImplementation;
import componentrelease.impl.casetask.CreateSpecificationsCaseTaskImplementation;

/**
 * <p>
 * Central factory class with methods providing {@link CaseTaskImplementation}s
 * during runtime. A {@link CaseTask} is used as a parameter. Its cmId is then
 * used to get the correct implementation.
 * 
 * For example (ct being the given CaseTask parameter):
 * 		switch (ct.getCmId()) {
 * 			case "createSpecs":
 * 				return new CreateSpecificationsCaseTaskImplementation(ct);
 * 
 * </p>
 * 
 * @author André Zensen
 *
 */
public class CaseTaskImplementationFactory {

	@Produces
	public static CaseTaskImplementation getCaseTaskImplementation(CaseTask ct) {
		switch (ct.getCmId()) {
		case "createSpecs":
			return new CreateSpecificationsCaseTaskImplementation(ct);
		default:
			return null;
		}
	}

}
