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

import cm.core.tasks.ProcessTask;
import cm.core.tasks.ProcessTaskImplementation;
import componentrelease.impl.processtask.ProvideDataProcessImplementation;

/**
 * <p>
 * Central factory class with methods providing
 * {@link ProcessTaskImplementation}s during runtime. A {@link ProcessTask} is
 * used as a parameter. Its cmId is then used to get the correct implementation.
 * 
 * For example (pt being the given ProcessTask parameter): switch (pt.getCmId())
 * { case "provideDataProcess": return new ProvideDataProcessImpl(pt);
 * 
 * </p>
 * 
 * @author André Zensen
 *
 */
public class ProcessTaskImplementationFactory {

	@Produces
	public static ProcessTaskImplementation getProcessImplementation(ProcessTask pt) {
		switch (pt.getCmId()) {
		case "provideData":
			return new ProvideDataProcessImplementation(pt);
		default:
			return null;
		}
	}

}
