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

import cm.core.sentries.IfPart;
import cm.core.sentries.IfPartImplementation;
import componentrelease.impl.ifpart.IfApproved;
import componentrelease.impl.ifpart.IfRevision;
/**
 * <p>
 * Central factory class with methods providing {@link IfPartImplementation}s
 * during runtime. An {@link IfPart} is used as a parameter. Its cmId is then
 * used to get the correct implementation.
 * 
 * For example (ip being the given CaseTask parameter):
 * 		switch (ip.getCmId()) {
 * 			case "ifApproved":
 * 				return new IfApproved(ip);
 * 
 * </p>
 * 
 * @author André Zensen
 *
 */
public class IfPartImplementationFactory {

	public static IfPartImplementation getIfPartImplementation(IfPart ip) {
		switch (ip.getCmId()) {
		case "ifApproved":
			return new IfApproved(ip);
		case "ifRevision":
			return new IfRevision(ip);
		default:
			return null;
		}
	}
	
}
