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
package cm.core.data;
/**
 * <p>Types of {@link CaseFileItem}s as enumeration used to set and process types.</p>
 * <p>Does not include all suggested types. See CMMN 1.1 specification section 5.1.4.1 for more information.</p>
 * 
 * @author André Zensen
 *
 */
public enum CaseFileItemPropertyType {

	string,
	bool,
	integer,
	decimal,
	dateTime,
	time,
	date
	
}
