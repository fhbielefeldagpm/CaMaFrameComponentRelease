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
package componentrelease.impl.views;
/*
 * 

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import model.Attachment;
import model.BeUser;
import model.Commission;
import model.CommissionStatus;
import model.Project;
import model.TaskType;
import service.BeUserService;
import service.ComponentFamilyService;

 */

import com.vaadin.ui.VerticalLayout;

public class RecordCommissionDataUI extends VerticalLayout {
/*
 *
	//header
	private Label lblId = new Label("Auftragsnummer:");
	private TextField id = new TextField();
	private Label lblCreator = new Label("Erstellt durch:");
	private TextField creator = new TextField();
	private Label lblDateOfCreation = new Label("am:");
	private DateField dateOfCreation = new DateField();
	private Label lblLastChangedBy = new Label("Zuletzt geändert durch:");
	private TextField lastChangedBy = new TextField();
	private Label lblDateOfChange = new Label("am:");
	private DateField dateOfChange = new DateField();
	private Label lblStatus = new Label("Status:");
	private NativeSelect<CommissionStatus> status = new NativeSelect<>();
	
	//body and details
	private Label lblCommissioner = new Label("Auftraggeber:");
	private NativeSelect<BeUser> commissioner = new NativeSelect<>();
	private Button btnSearchCommissioner = new Button(VaadinIcons.SEARCH);
	private Label lblTaskType = new Label("Aufgabe:");
	private NativeSelect<TaskType> taskType = new NativeSelect<>();
	
	private Label lblMaterialId = new Label("Materialnummer:");
	private TextField materialId = new TextField();
	private CheckBox chkNewBE = new CheckBox("Neues BE");
	private Label lblMaterialName = new Label("Mat-Bezeichnung:");
	private TextField materialName = new TextField();

	private Label lblComponentFamily = new Label("BE-Familie:");
	private NativeSelect<String> componentFamily = new NativeSelect<>();
	private Button btnSearchComponentFamily = new Button(VaadinIcons.SEARCH);
	
	private Label lblDesiredScheduleDate = new Label("Wunschtermin der Freigabe:");
	private DateField desiredScheduleDate = new DateField();
	private Label lblInternalOrderId = new Label("Innenauftrag:");
	private TextField internalOrderId = new TextField();
	
	private Label lblSupplierLiaison = new Label("Lieferantenbetreuer:");
	private NativeSelect<BeUser> supplierLiaison = new NativeSelect<>();
	private Button btnSearchSupplierLiaison = new Button(VaadinIcons.SEARCH);
	private Label lblDeveloper = new Label("Konstrukteur/Entwickler:");
	private NativeSelect<BeUser> developer = new NativeSelect<>();
	private Button btnSearchDeveloper = new Button(VaadinIcons.SEARCH);
	private Label lblTradeBuyer = new Label("Facheinkäufer:");
	private NativeSelect<BeUser> tradeBuyer = new NativeSelect<>();
	private Button btnSearchTradeBuyer = new Button(VaadinIcons.SEARCH);
	
	private Label lblProjects = new Label("Projekte");
	private NativeSelect<Project> projects = new NativeSelect<>();
	private Button btnAddProject = new Button("Projekte hinzufügen");
	
	private Label lblDescription = new Label("Beschreibung:");
	private TextArea description = new TextArea();
	
	private Label lblAttachments = new Label("Anhänge:");
	private NativeSelect<Attachment> attachments = new NativeSelect<>();
	private Button btnBrowseAttachments = new Button("Durchsuchen...");
	private Link folderLink = new Link("Öffne Dateiordner", new ExternalResource("http://www.google.com"));
	
	//control
	private Button btnSaveCommission = new Button("Speichern");
	
	//data items
//	private MaterialNumberRequest materialNumberRequest;
	private Commission commission = new Commission();
	private BeUser currentBeUser;
//	private TaskList taskList;
	
	//associations
	private MyUI myUI;
	
	//binders
	Binder<Commission> commissionBinder = new Binder<>(Commission.class);
	//services
	private BeUserService beUserService = BeUserService.getInstance();
	private ComponentFamilyService componentFamilyService = new ComponentFamilyService().getInstance();
	
	public RecordCommissionDataUI (MyUI myUI, BeUser currentBeUser) {
		this.myUI = myUI;
		this.currentBeUser = currentBeUser;
		//beUserService.save(currentBeUser);
		setSizeFull();
		HorizontalLayout headerLine = new HorizontalLayout(lblId, id,
				lblCreator, creator,
				lblDateOfCreation, dateOfCreation,
				lblLastChangedBy, lastChangedBy,
				lblDateOfChange, dateOfChange,
				lblStatus, status);

		HorizontalLayout clientLine = new HorizontalLayout(lblCommissioner, commissioner, btnSearchCommissioner);
		clientLine.setWidth("50%");
		HorizontalLayout taskTypeLine = new HorizontalLayout(lblTaskType, taskType);
		taskTypeLine.setWidth("50%");
		HorizontalLayout materialNumberLine = new HorizontalLayout(lblMaterialId, materialId, chkNewBE);
		materialNumberLine.setWidth("50%");
		HorizontalLayout materialDescriptionLine = new HorizontalLayout(lblMaterialName, materialName);
		materialDescriptionLine.setWidth("50%");
		HorizontalLayout componentFamilyLine = new HorizontalLayout(lblComponentFamily, componentFamily, btnSearchComponentFamily);
		componentFamilyLine.setWidth("50%");
		HorizontalLayout desiredDateLine = new HorizontalLayout(lblDesiredScheduleDate, desiredScheduleDate);
		desiredDateLine.setWidth("50%");
		HorizontalLayout internalOrderLine = new HorizontalLayout(lblInternalOrderId, internalOrderId);
		internalOrderLine.setWidth("50%");
		HorizontalLayout supplierLiaisonLine = new HorizontalLayout(lblSupplierLiaison, supplierLiaison, btnSearchSupplierLiaison);
		supplierLiaisonLine.setWidth("50%");
		HorizontalLayout developerLine = new HorizontalLayout(lblDeveloper, developer, btnSearchDeveloper);
		developerLine.setWidth("50%");
		HorizontalLayout tradeBuyerLine = new HorizontalLayout(lblTradeBuyer, tradeBuyer, btnSearchTradeBuyer);
		tradeBuyerLine.setWidth("50%");
		HorizontalLayout projectLine = new HorizontalLayout(lblProjects, projects, btnAddProject);
		projectLine.setWidth("50%");
		HorizontalLayout descriptionLine = new HorizontalLayout(lblDescription, description);
		descriptionLine.setWidth("50%");
		HorizontalLayout attachmentLine = new HorizontalLayout(lblAttachments, attachments, btnBrowseAttachments, folderLink);
		attachmentLine.setWidth("50%");
		
		addComponents(headerLine, clientLine, taskTypeLine, materialNumberLine, materialDescriptionLine, 
				componentFamilyLine, desiredDateLine, internalOrderLine, supplierLiaisonLine,
				developerLine, tradeBuyerLine, projectLine, descriptionLine, attachmentLine,
				btnSaveCommission);
		
		//disable header boxes and fill with BeUser reference and current date
		id.setEnabled(false);
		creator.setEnabled(false);
		creator.setValue(this.currentBeUser.getNameLine());
		dateOfCreation.setEnabled(false);
		dateOfCreation.setValue(LocalDate.now());
		lastChangedBy.setEnabled(false);
		lastChangedBy.setValue(this.currentBeUser.getNameLine());
		dateOfChange.setEnabled(false);
		dateOfChange.setValue(LocalDate.now());
		
		//setup and fill boxes
		creator.setValue(currentBeUser.getLastName() + ", " + currentBeUser.getFirstName());
		lastChangedBy.setValue(currentBeUser.getLastName() + ", " + currentBeUser.getFirstName());
		
		commissioner.setItemCaptionGenerator(
				beUser -> beUser.getUserId());
		commissioner.setItems(beUserService.findAll());
		
		supplierLiaison.setItemCaptionGenerator(
				beUser -> beUser.getUserId());
		supplierLiaison.setItems(beUserService.findAll());
		
		developer.setItemCaptionGenerator(
				beUser -> beUser.getUserId());
		developer.setItems(beUserService.findAll());
		
		
		//componentFamily.setItems(componentFamilyService.findAll());
		
		Collection<TaskType> taskTypes = new ArrayList<>();
		taskTypes.add(new TaskType(1, "Bauteilfreigabe"));
		taskTypes.add(new TaskType(2, "Applikationsfreigabe"));
		taskType.setItemCaptionGenerator(
				taskType -> taskType.getName());
		taskType.setItems(taskTypes );
		
		//configure bindings
		commissionBinder.forField(id)
			.withConverter(Long::valueOf, String::valueOf)
			.bind(Commission::getId, Commission::setId);
		
		commissionBinder.forField(creator)
			.bind("Commission.creator");
		
		commissionBinder.forField(lastChangedBy)
			.bind("Commission.lastChangedBy");
		
		commissionBinder.forField(commissioner)
			.bind("Commission.commissioner");
		
		//bind
		commissionBinder.bindInstanceFields(this);
		commissionBinder.setBean(this.commission);
	}
	 */
}
