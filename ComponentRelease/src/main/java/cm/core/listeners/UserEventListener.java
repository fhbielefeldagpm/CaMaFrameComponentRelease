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
package cm.core.listeners;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * <p>
 * Class to represent specialization of EventListener elements in CMMN. Not yet
 * implemented. Use {@link EventListener} instead.
 * </p>
 * <p>
 * See CMMN 1.1 specification section 8.4.3 for more information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("user_event")
public class UserEventListener extends EventListener {

	public UserEventListener() {

	}
}