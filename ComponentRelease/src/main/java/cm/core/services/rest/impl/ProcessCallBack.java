package cm.core.services.rest.impl;

import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cm.core.CaseModel;
import cm.core.CaseElement;
import cm.core.services.CaseService;
import cm.core.services.TaskService;
import cm.core.tasks.ProcessTask;
import cm.core.tasks.Task;

@Path("/process")
public class ProcessCallBack {

	@Inject
	TaskService tService;
	
	@Inject
	CaseService cService;
	
    @Path("/callback/{id}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String callBack(@PathParam("id") long id) {
    	String response = "Callback executed on ProcessTask ";
    	ProcessTask pTask = tService.findProcessTaskById(id);
    	pTask.executeCallBack();
    	return response;
    }
    
    @GET
    @Path("/map")
    @Produces(MediaType.APPLICATION_JSON)
    public TreeMap<String, String> getMap() {
    	TreeMap<String, String> map = new TreeMap<>();
    	map.put("field1", "value1");
    	map.put("field2", "value2");
    	return map;
    }
    
    @GET
    @Path("/elements/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CaseElement> getElements(@PathParam("caseId") long id) {
    	CaseModel caseId = new CaseModel();
    	caseId.setId(id);
    	return cService.getElementsInCase(caseId);
    }
    
    @GET
    @Path("/tasks/{caseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getTasks(@PathParam("caseId") long id) {
    	CaseModel caseId = new CaseModel();
    	caseId.setId(id);
    	return cService.getTasksInCase(caseId);
    }
	
}
