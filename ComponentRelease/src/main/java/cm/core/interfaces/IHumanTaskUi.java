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
package cm.core.interfaces;

import cm.core.tasks.HumanTask;

/**
 * <p>
 * Provides interfaces to implement views for {@link HumanTask}s. Included are
 * common options for CaseWorkers. Can be used as a facade for
 * {@link IHumanTaskController} methods.
 * </p>
 * 
 * @author André Zensen
 *
 */
public interface IHumanTaskUi {

	public void cancel();

	public void complete();

	public Boolean isValid();

	public void save();

	public void terminate();

	public void returnToMenu();

}