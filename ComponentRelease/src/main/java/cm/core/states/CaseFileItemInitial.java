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
package cm.core.states;

import cm.core.data.CaseFileItem;
import cm.core.data.CaseFileItemStates;

public class CaseFileItemInitial extends CaseFileItemState implements ICaseFileItemState {
	/**
	 * <p>
	 * Represents newly introduced state INITIAL of {@link CaseFileItem}s. Used in
	 * the context of an adapted state pattern. Implements permissible transitions.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification sections 6.4 and 8.3 for more information.
	 * </p>
	 * 
	 * @author André Zensen
	 */
	public CaseFileItemInitial(CaseFileItem item) {
		super(item);
	}

	@Override
	public void create() {
		String transition = "create";
		getItem().setState(CaseFileItemStates.AVAILABLE.toString(), transition);

	}

	@Override
	public void update() {
		// nothing

	}

	@Override
	public void replace() {
		// nothing

	}

	@Override
	public void addChild() {
		// nothing

	}

	@Override
	public void removeChild() {
		// nothing

	}

	@Override
	public void addReference() {
		// nothing

	}

	@Override
	public void removeReference() {
		// nothing

	}

	@Override
	public void delete() {
		// nothing

	}

}
