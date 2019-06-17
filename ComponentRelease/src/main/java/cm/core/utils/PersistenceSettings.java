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

/**
 * Provides the name of the persistence context / local unit. Referenced in
 * cm.core.services.impl. You can change it here centrally. Local is used by
 * ResetDataBaseTest.java.
 * 
 * @author André Zensen
 *
 */
public class PersistenceSettings {

	public static final String persistenceContextUnitName = "componentRelease";
	public static final String persistenceContextUnitLocalName = "componentReleaseLocal";

}
