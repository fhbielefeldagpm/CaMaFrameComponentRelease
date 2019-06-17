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
package cm.core.sentries;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import cm.core.CaseElement;
import cm.core.data.CaseFileItem;

/**
 * <p>
 * Base class for specializations {@link ElementOnPart} and
 * {@link CaseFileItemOnPart}. Observes state changes of {@link CaseElement}s or
 * {@link CaseFileItem}s and links them to {@link Sentry}s. Sentrys are notified
 * when the required transition has taken place.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.6 and 8.5 for more information.
 * </p>
 * 
 * @author André Zensen
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "onpart_type")
public abstract class OnPart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private boolean isSatisfied;

	public boolean isSatisfied() {
		return isSatisfied;
	}

	public void setSatisfied(boolean isSatisfied) {
		this.isSatisfied = isSatisfied;
	}

	@ManyToOne
	protected Sentry sentryRef;
}