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

import com.vaadin.ui.VerticalLayout;

public class RecordCommissionDataForm extends VerticalLayout {
//
//	// binder and data object to work on
//	private Binder<Commission> binder = new Binder<>(Commission.class);
//	private Commission commission = new Commission();
//	
//	public void setCreator(BeUser creator) {
//		this.commission.setCreator(creator);
//	}
//	
//	public void setLastChangedBy(BeUser changedByUser) {
//		this.commission.setLastChangedBy(changedByUser);
//	}
//	
//	/*
//	 * Data from POJO adapted to Vaadin elements/classes
//	 * Long/String 	-> TextField
//	 * Dates 		-> DateField
//	 * List/BeUser 	-> NativeSelect or TextField for simple display
//	 */
//	// header data
//	private TextField id = new TextField("Commission ID:");
//	private TextField creator = new TextField("Created by:");
//	private TextField dateOfCreation = new TextField("Created on:");
//	private TextField lastChangedBy = new TextField("Last changed by:");
//	private TextField dateOfChange = new TextField("Last change on:");
//	
//	private TextField status = new TextField("Status:");
//	
//	// body and details
//	private NativeSelect<BeUser> commissioner = new NativeSelect<>("Commissioner:");
//	private NativeSelect<TaskType> taskType = new NativeSelect<>("Task type:");
//	
//	// private Material material;
//	private TextField materialId = new TextField("Material-ID:");
//	private TextField materialName = new TextField("Material name:");
//
//	private NativeSelect<ComponentFamily> componentFamily = new NativeSelect<>("Component family:");
//	
//	private DateField desiredScheduleDate = new DateField("Desired schedule date:");
//	private TextField internalOrderId = new TextField("Internal order ID:");
//	
//	private NativeSelect<BeUser> supplierLiaison = new NativeSelect<>("Supplier liaison:");
//	private NativeSelect<BeUser> developer = new NativeSelect<>("Developer:");
//	private NativeSelect<BeUser> tradeBuyer = new NativeSelect<>("Trade buyer:");	
//	
//	// projects list
//	private Grid<AssociatedProject> projects = new Grid<>(AssociatedProject.class);
//	
//	private TextArea description = new TextArea("Description:");
//	//handled differently via grid and upload
//	//private NativeSelect<Attachment> attachments = new NativeSelect<>("Attachments:");
//	//not included, would require setup of local/network drive(s)
//	//private Link folderLink = new Link("Open directory", new ExternalResource("http://www.google.com"));
//	//not included for now
//	//private MaterialNumberRequest materialNumberRequest = new MaterialNumberRequest();
//	
//	/*
//	 * Additional elements for UI
//	 */
//	// search buttons could be grouped and refactored together with the native selects in a class extending csslayout to group them
//	// https://stackoverflow.com/questions/44007835/how-to-add-search-icon-in-vaadin-combobox
//	private Button btnSearchCommissioner = new Button(VaadinIcons.SEARCH);
//	private Button btnSearchComponentFamily = new Button(VaadinIcons.SEARCH);
//	private Button btnSearchSupplierLiaison = new Button(VaadinIcons.SEARCH);
//	private Button btnSearchDeveloper = new Button(VaadinIcons.SEARCH);
//	private Button btnSearchTradeBuyer = new Button(VaadinIcons.SEARCH);
//	
//	private CheckBox chkNewComponent = new CheckBox("New component");
//	// add projects
//	private Button btnAddProjects = new Button("Add projects");
//	// control
//	private Button btnSaveCommission = new Button("Save");
//	private Button btnCancelCommission = new Button("Cancel");
//	
//	//layouts horizontal or css
//	
//	HorizontalLayout lytHeaderInfo = new HorizontalLayout(id, creator, dateOfCreation, lastChangedBy, dateOfChange, status);
//	CssLayout lytCommissionerLine = new CssLayout(commissioner, btnSearchCommissioner);
//	CssLayout lytTaskLine = new CssLayout(taskType);
//	CssLayout lytMaterialLine = new CssLayout(materialId, chkNewComponent);
//	CssLayout lytMaterialLine2 = new CssLayout(materialName);
//	CssLayout lytComponentFamily = new CssLayout(componentFamily, btnSearchComponentFamily);
//	CssLayout lytDesiredDateLine = new CssLayout(desiredScheduleDate);
//	CssLayout lytInternalCommissionLine = new CssLayout(internalOrderId);
//	CssLayout lytSupplierLiaisonLine = new CssLayout(supplierLiaison, btnSearchSupplierLiaison);
//	CssLayout lytDeveloperLine = new CssLayout(developer, btnSearchDeveloper);
//	CssLayout lytTradeBuyerline = new CssLayout(tradeBuyer, btnSearchTradeBuyer);
//	CssLayout lytDescriptionLine = new CssLayout(description);
//	CssLayout lytProjectLine = new CssLayout(btnAddProjects);
//	CssLayout lytProjectGrid = new CssLayout(projects);
//	HorizontalLayout lytControls = new HorizontalLayout(btnSaveCommission, btnCancelCommission);
//	
////	HorizontalLayout lytHeaderInfo = new HorizontalLayout(id, creator, dateOfCreation, lastChangedBy, dateOfChange);
////	HorizontalLayout lytCommissionerLine = new HorizontalLayout(commissioner, btnSearchCommissioner);
////	HorizontalLayout lytTaskLine = new HorizontalLayout(taskType);
////	HorizontalLayout lytMaterialLine = new HorizontalLayout(materialId, chkNewComponent);
////	HorizontalLayout lytMaterialLine2 = new HorizontalLayout(materialName);
////	HorizontalLayout lytComponentFamily = new HorizontalLayout(componentFamily, btnSearchComponentFamily);
////	HorizontalLayout lytDesiredDateLine = new HorizontalLayout(desiredScheduleDate);
////	HorizontalLayout lytInternalCommissionLine = new HorizontalLayout(internalOrderId);
////	HorizontalLayout lytSupplierLiaisonLine = new HorizontalLayout(supplierLiaison, btnSearchSupplierLiaison);
////	HorizontalLayout lytDeveloperLine = new HorizontalLayout(developer, btnSearchDeveloper);
////	HorizontalLayout lytTradeBuyerline = new HorizontalLayout(tradeBuyer, btnSearchTradeBuyer);
////	HorizontalLayout lytDescriptionLine = new HorizontalLayout(description);
////	HorizontalLayout lytProjectLine = new HorizontalLayout(btnAddProjects);
////	HorizontalLayout lytProjectGrid = new HorizontalLayout(projects);
////	HorizontalLayout lytControls = new HorizontalLayout(btnSaveCommission, btnCancelCommission);
//	//HorizontalLayout lytPopup = new HorizontalLayout();
//	
//	//TODO move initialization to after check whether _commission is null
//	private AttachmentGrid attachmentGrid;
//	private AttachmentReceiver attachmentReceiverAndSucceedListener;
//	private Upload attachmentUpload;
//	
//	//associations
//	private StartForm startForm;
//	private MyUI myUI;
//	
//	//services
//	private BeUserService userService = BeUserService.getInstance();
//	private CommissionService commissionService = CommissionService.getInstance();
//	private ComponentFamilyService componentFamilyService = ComponentFamilyService.getInstance();
//	private TaskTypeService taskTypeService = TaskTypeService.getInstance();
//	
//	public RecordCommissionDataForm(MyUI myUI, StartForm startForm, Commission _commission) {
//		this.startForm = startForm;
//		this.myUI = myUI;
//		BeUser currentUser = startForm.getCurrentUser();
//		if(_commission != null) {
//			this.commission = _commission;
//		}
//		
//		//attachment grid setup
//		attachmentGrid = new AttachmentGrid(this.commission.getAttachments());
//		attachmentReceiverAndSucceedListener = new AttachmentReceiver(this.commission, this.attachmentGrid);
//		attachmentUpload = new Upload("Attachment", attachmentReceiverAndSucceedListener);
//		
//		//layout setup
//		addComponents(lytHeaderInfo,
//				lytCommissionerLine, lytTaskLine, lytMaterialLine, 
//				lytMaterialLine2, lytComponentFamily, lytDesiredDateLine, 
//				lytInternalCommissionLine, lytSupplierLiaisonLine, lytDeveloperLine,
//				lytTradeBuyerline, lytDescriptionLine, lytProjectLine, lytProjectGrid,
//				attachmentUpload, attachmentGrid, lytControls); //, lytPopup
//		
//		//display of commissions
////		CommissionSearchForm commissionSearchForm = new CommissionSearchForm(commissionService);
////		addComponent(commissionSearchForm);
//		
//		this.setWidth("100%");
//		setVisible(true);
//		String boxSize = "200";
//		
//		
//		//selects setup, caption generators, size and null selection disable
//		commissioner.setItemCaptionGenerator(BeUser::getNameLine);
//		commissioner.setWidth(boxSize);
//		commissioner.setEmptySelectionAllowed(false);
//		
//		taskType.setItemCaptionGenerator(TaskType::getName);
//		taskType.setWidth(boxSize);
//		if(_commission == null) {
//			taskType.setItems(taskTypeService.findAll());
//		} else {
//			TaskType setTaskType = _commission.getTaskType();
//			taskType.setItems(taskTypeService.findAll());
//			taskType.setSelectedItem(setTaskType);
//		}
//		taskType.setEmptySelectionAllowed(false);
//		
//		componentFamily.setItemCaptionGenerator(ComponentFamily::getName);
//		componentFamily.setWidth(boxSize);
//		componentFamily.setItems(componentFamilyService.findAll());
//		componentFamily.setEmptySelectionAllowed(false);
//		
//		supplierLiaison.setItemCaptionGenerator(BeUser::getNameLine);
//		supplierLiaison.setWidth(boxSize);
//		supplierLiaison.setEmptySelectionAllowed(false);
//		
//		developer.setItemCaptionGenerator(BeUser::getNameLine);
//		developer.setWidth(boxSize);
//		developer.setEmptySelectionAllowed(false);
//		
//		tradeBuyer.setItemCaptionGenerator(BeUser::getNameLine);
//		tradeBuyer.setWidth(boxSize);
//		tradeBuyer.setEmptySelectionAllowed(false);
//		
//		// embedded project grid setup
//		// TODO refactor as component, rename columns via manual add
//		projects.setColumns("project.name", "project.subProject.name", "project.projectLead.lastName", 
//				"project.prototypeSchedule", "project.b0SeriesSchedule", "project.sop", "project.yearlyAmount", "quantitativeFactor");
//		projects.setItems(commission.getAssociatedProjects());
//		projects.setWidth("100%");
//		lytProjectGrid.setSizeFull();
//		
//		// upload configuration
//		attachmentUpload.setButtonCaption("Upload");
//		attachmentUpload.addSucceededListener(this.attachmentReceiverAndSucceedListener);
//		
//		// disable header boxes and fill with BeUser reference, current date and status
//		id.setEnabled(false);
//		id.setWidth(boxSize);
//		creator.setEnabled(false);
//		creator.setWidth(boxSize);
//		// next line done via binder with ReadOnlyHasValue object
//		// creator.setValue(startForm.getCurrentUser().getNameLine());
//		if(_commission == null) {
//			this.setCreator(currentUser);
//		}
//		dateOfCreation.setEnabled(false);
//		dateOfCreation.setWidth(boxSize);
//		if(_commission == null) {
//			dateOfCreation.setValue(LocalDate.now().toString());
//			this.commission.setDateOfCreation(LocalDate.now());
//		} //binder should feed value		
//
//		lastChangedBy.setEnabled(false);
//		lastChangedBy.setWidth(boxSize);
//		// next line done via binder with ReadOnlyHasValue object
//		// lastChangedBy.setValue(startForm.getCurrentUser().getNameLine());
//		if(_commission == null) {
//			this.commission.setLastChangedBy(currentUser);
//		}
//		dateOfChange.setEnabled(false);
//		dateOfChange.setWidth(boxSize);
//		if(_commission == null) {
//			dateOfChange.setValue(LocalDate.now().toString());
//			this.commission.setDateOfChange(LocalDate.now());
//		}
//		
//		status.setEnabled(false);
//		
//		if(_commission == null) {
//			this.commission.setStatus("Open");
//		}		
//		
//		// event listeners
////			Popup window, disabled due to necessary fixed position to pop up from
////			btnSearchCommissioner.addClickListener(e -> {
////			PopupView searchPopup = new PopupView(null, new UserSearchForm(userService, commissioner, null));
////			lytPopup.addComponent(searchPopup);
////			searchPopup.setPopupVisible(true);
////			searchPopup.addPopupVisibilityListener(pe -> {
////				if(!pe.isPopupVisible()) {
////					lytPopup.removeAllComponents();
////				}
////			});
////		});
//			// buttons
//			btnSearchCommissioner.addClickListener(e -> {
//				Window searchWindow = new Window("Select a commissioner");
//				searchWindow.setContent(new UserSearchForm(userService, commissioner, searchWindow));
//				searchWindow.setModal(true);
//				searchWindow.center();
//				this.myUI.addWindow(searchWindow);
//			});
//	//		done by binder now
//	//		commissioner.addValueChangeListener(e -> {
//	//			if(e.getValue() != null) {
//	//				this.commission.setCommissioner(e.getValue());
//	//			}
//	//		});
//			
//			btnSearchSupplierLiaison.addClickListener(e -> {
//				Window searchWindow = new Window("Select a supplier liaison");
//				searchWindow.setContent(new UserSearchForm(userService, supplierLiaison, searchWindow));
//				searchWindow.setModal(true);
//				searchWindow.center();
//				this.myUI.addWindow(searchWindow);
//			});
//			
//			btnSearchDeveloper.addClickListener(e -> {
//				Window searchWindow = new Window("Select a developer");
//				searchWindow.setContent(new UserSearchForm(userService, developer, searchWindow));
//				searchWindow.setModal(true);
//				searchWindow.center();
//				this.myUI.addWindow(searchWindow);
//			});
//			
//			btnSearchTradeBuyer.addClickListener(e -> {
//				Window searchWindow = new Window("Select a trade buyer");
//				searchWindow.setContent(new UserSearchForm(userService, tradeBuyer, searchWindow));
//				searchWindow.setModal(true);
//				searchWindow.center();
//				this.myUI.addWindow(searchWindow);
//			});
//			
//
//		//TODO null handling for binder?
//		chkNewComponent.addValueChangeListener(e -> {
//			if(chkNewComponent.getValue()) {
//				materialId.clear();
//				materialId.setEnabled(false);
//			} else if(!chkNewComponent.getValue()) {
//				materialId.setEnabled(true);
//			}
//		});
//		
//		btnSaveCommission.addClickListener(e -> {
//			//TODO add last change by and last change date modification
//			
//			try {
//				binder.writeBean(commission);
//				commissionService.save(this.commission);
//			} catch (ValidationException e1) {
//				// TODO Error handling
//				e1.printStackTrace();
//			}
//		});
//		
//		btnCancelCommission.addClickListener(e ->{
//			// TODO display warning all entries will be lost
//			this.startForm.resetStartForm();
//		});
//		
//		//binder configuration
//			//header
//			ReadOnlyHasValue<Commission> creatorText = new ReadOnlyHasValue<>(
//			        commission -> creator.setValue(commission.getCreator().getNameLine()));
//			binder.forField(creatorText).bind(beUser -> beUser, null);
//			
//			ReadOnlyHasValue<Commission> dateOfCreationText = new ReadOnlyHasValue<>(
//			        commission -> dateOfCreation.setValue(commission.getDateOfCreation().toString()));
//			binder.forField(dateOfCreationText).bind(date -> date, null);			
//			
//			ReadOnlyHasValue<Commission> lastChangedByText = new ReadOnlyHasValue<>(
//			        commission -> lastChangedBy.setValue(commission.getLastChangedBy().getNameLine()));
//			binder.forField(lastChangedByText).bind(beUser -> beUser, null);
//			
//			ReadOnlyHasValue<Commission> dateOfChangeText = new ReadOnlyHasValue<>(
//			        commission -> dateOfChange.setValue(commission.getDateOfChange().toString()));
//			binder.forField(dateOfChangeText).bind(date -> date, null);	
//			
//			binder.forField(status).bind(Commission::getStatus, Commission::setStatus);
//		
//			//for changes from external search form
//			binder.forField(commissioner).bind(Commission::getCommissioner, Commission::setCommissioner);
//			binder.forField(supplierLiaison).bind(Commission::getSupplierLiaison, Commission::setSupplierLiaison);
//			binder.forField(developer).bind(Commission::getDeveloper, Commission::setDeveloper);
//			binder.forField(tradeBuyer).bind(Commission::getTradeBuyer, Commission::setTradeBuyer);
//			
//			//other
//			binder.forField(taskType).bind(Commission::getTaskType, Commission::setTaskType);
//			binder.forField(materialId).bind(Commission::getMaterialId, Commission::setMaterialId);
//			binder.forField(materialName).bind(Commission::getMaterialName, Commission::setMaterialName);
//			binder.forField(componentFamily).bind(Commission::getComponentFamily, Commission::setComponentFamily);
//			//desired schedule date should be handled by binder with bindInstanceFields
//			binder.forField(description).bind(Commission::getDescription, Commission::setDescription);
//			
//		
//		binder.setBean(this.commission);
//		
//	}
//	
//	public void setCommission(Commission commission) {
//		this.binder.setBean(commission);
//	}
//	
//	
//	
}
